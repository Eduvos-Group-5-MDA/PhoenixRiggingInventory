package com.example.phoenixinventory.data

import android.util.Log
import com.example.phoenixinventory.data.network.ApiClient
import com.example.phoenixinventory.data.network.AuthResponseDto
import com.example.phoenixinventory.data.network.CheckedOutItemDetailDto
import com.example.phoenixinventory.data.network.CheckoutRequest
import com.example.phoenixinventory.data.network.InventoryItemDto
import com.example.phoenixinventory.data.network.ItemRequest
import com.example.phoenixinventory.data.network.LoginRequest
import com.example.phoenixinventory.data.network.PhoenixApiService
import com.example.phoenixinventory.data.network.RegisterRequest
import com.example.phoenixinventory.data.network.UserDto
import com.example.phoenixinventory.data.network.CheckoutRecordDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.format.DateTimeParseException
import java.util.Date

object DataRepository {
    private const val TAG = "DataRepository"

    private val api: PhoenixApiService = ApiClient.service
    private val initMutex = Mutex()

    private val itemsState = MutableStateFlow<List<InventoryItem>>(emptyList())
    private val usersState = MutableStateFlow<List<User>>(emptyList())
    private val checkedOutItemsState = MutableStateFlow<List<CheckedOutItemDetail>>(emptyList())
    private val statsState = MutableStateFlow(InventoryStats())
    private val currentUserState = MutableStateFlow(
        User(name = "Guest", email = "guest@example.com", role = "Employee")
    )

    private var authToken: String? = null
    private var initialized = false

    fun itemsFlow(): StateFlow<List<InventoryItem>> = itemsState.asStateFlow()
    fun usersFlow(): StateFlow<List<User>> = usersState.asStateFlow()
    fun checkedOutItemsFlow(): StateFlow<List<CheckedOutItemDetail>> = checkedOutItemsState.asStateFlow()
    fun statsFlow(): StateFlow<InventoryStats> = statsState.asStateFlow()
    fun currentUserFlow(): StateFlow<User> = currentUserState.asStateFlow()

    fun getCurrentUser(): User = currentUserState.value

    fun isLoggedIn(): Boolean = authToken != null

    fun logout() {
        authToken = null
        ApiClient.updateAuthToken(null)
        initialized = false
        itemsState.value = emptyList()
        usersState.value = emptyList()
        checkedOutItemsState.value = emptyList()
        statsState.value = InventoryStats()
        currentUserState.value = User(name = "Guest", email = "guest@example.com", role = "Employee")
    }

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        val response = withContext(Dispatchers.IO) {
            api.login(LoginRequest(email.trim(), password))
        }
        handleAuthResponse(response)
    }

    suspend fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        role: String,
        phone: String?,
        company: String?,
        hasDriverLicense: Boolean,
        employeeId: String?
    ): Result<User> = runCatching {
        val response = withContext(Dispatchers.IO) {
            api.register(
                RegisterRequest(
                    firstName = firstName.trim(),
                    lastName = lastName.trim(),
                    email = email.trim(),
                    password = password,
                    role = role,
                    phone = phone?.trim().takeUnless { it.isNullOrBlank() },
                    company = company?.trim().takeUnless { it.isNullOrBlank() },
                    hasDriverLicense = hasDriverLicense,
                    employeeId = employeeId?.trim().takeUnless { it.isNullOrBlank() }
                )
            )
        }
        handleAuthResponse(response)
    }

    private suspend fun handleAuthResponse(response: AuthResponseDto): User {
        authToken = response.token
        ApiClient.updateAuthToken(authToken)
        val user = response.user.toDomainUser()
        currentUserState.value = user
        refreshAllData()
        return user
    }

    suspend fun refreshAllData() {
        try {
            initMutex.withLock {
                withContext(Dispatchers.IO) {
                    val items = api.getItems().map { it.toDomainItem() }
                    val users = api.getUsers().map { it.toDomainUser() }
                    val stats = api.getItemStats()
                    val checkedOut = api.getCheckedOutItems().map { it.toDomainDetail() }

                    itemsState.value = items
                    usersState.value = users
                    checkedOutItemsState.value = checkedOut
                    statsState.value = InventoryStats(
                        totalValue = stats.totalValue,
                        stolenLostDamagedValue = stats.stolenLostDamagedValue,
                        stolenLostDamagedCount = stats.stolenLostDamagedCount,
                        checkedOutCount = stats.checkedOutCount
                    )
                    initialized = true
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "refreshAllData: failed", ex)
            throw ex
        }
    }

    private suspend fun ensureInitialized() {
        if (!initialized) {
            refreshAllData()
        }
    }

    suspend fun getAllItems(): List<InventoryItem> {
        ensureInitialized()
        return itemsState.value
    }

    suspend fun getItemById(id: String): InventoryItem? {
        ensureInitialized()
        return itemsState.value.find { it.id == id } ?: runCatching {
            withContext(Dispatchers.IO) { api.getItem(id).toDomainItem() }
        }.getOrNull()?.also { item ->
            replaceItem(item)
        }
    }

    suspend fun getAllUsers(): List<User> {
        ensureInitialized()
        return usersState.value
    }

    suspend fun getUserById(id: String): User? {
        ensureInitialized()
        return usersState.value.find { it.id == id }
    }

    suspend fun getCheckedOutItems(): List<CheckedOutItemDetail> {
        ensureInitialized()
        return checkedOutItemsState.value
    }

    suspend fun getItemsOutLongerThan(days: Int): List<CheckedOutItemDetail> {
        ensureInitialized()
        return runCatching {
            withContext(Dispatchers.IO) { api.getItemsOutLongerThan(days).map { it.toDomainDetail() } }
        }.onSuccess { overdue ->
            // Merge into cache for consistency
            val updated = checkedOutItemsState.value.toMutableList()
            overdue.forEach { detail ->
                val index = updated.indexOfFirst { it.checkoutRecord.id == detail.checkoutRecord.id }
                if (index >= 0) {
                    updated[index] = detail
                }
            }
            checkedOutItemsState.value = updated
        }.getOrElse {
            checkedOutItemsState.value.filter { it.daysOut >= days }
        }
    }

    suspend fun addItem(item: InventoryItem): Result<InventoryItem> = runCatching {
        ensureInitialized()
        val created = withContext(Dispatchers.IO) {
            api.createItem(item.toRequest()).toDomainItem()
        }
        itemsState.value = (itemsState.value + created).sortedBy { it.name.lowercase() }
        refreshStats()
        created
    }

    suspend fun updateItem(item: InventoryItem): Result<InventoryItem> = runCatching {
        ensureInitialized()
        val updated = withContext(Dispatchers.IO) {
            api.updateItem(item.id, item.toRequest()).toDomainItem()
        }
        replaceItem(updated)
        refreshStats()
        updated
    }

    suspend fun removeItem(id: String): Result<Unit> = runCatching {
        ensureInitialized()
        withContext(Dispatchers.IO) { api.deleteItem(id) }
        itemsState.value = itemsState.value.filterNot { it.id == id }
        checkedOutItemsState.value = checkedOutItemsState.value.filterNot { it.item.id == id }
        refreshStats()
    }

    suspend fun updateUser(user: User, newPassword: String? = null): Result<User> = runCatching {
        ensureInitialized()
        val body = mutableMapOf<String, Any?>(
            "name" to user.name,
            "email" to user.email,
            "role" to user.role,
            "phone" to user.phone,
            "company" to user.company,
            "driverLicense" to user.driverLicense,
            "employeeId" to user.employeeId
        )
        if (!newPassword.isNullOrBlank()) {
            body["password"] = newPassword
        }

        val updated = withContext(Dispatchers.IO) {
            api.updateUser(user.id, body).toDomainUser()
        }
        usersState.value = usersState.value.map { if (it.id == updated.id) updated else it }
        if (currentUserState.value.id == updated.id) {
            currentUserState.value = updated
        }
        updated
    }

    suspend fun checkOutItem(itemId: String, userId: String, notes: String): Result<Unit> = runCatching {
        ensureInitialized()
        val record = withContext(Dispatchers.IO) {
            api.checkoutItem(CheckoutRequest(itemId, userId, notes)).toDomainCheckout()
        }
        val updatedItem = itemsState.value.find { it.id == itemId }?.copy(status = "Checked Out")
        if (updatedItem != null) {
            replaceItem(updatedItem)
        }
        val user = usersState.value.find { it.id == userId }
        if (user != null && updatedItem != null) {
            val detail = CheckedOutItemDetail(
                item = updatedItem,
                user = user,
                checkoutRecord = record,
                daysOut = 0
            )
            checkedOutItemsState.value = checkedOutItemsState.value + detail
        }
        refreshStats()
    }

    suspend fun checkInItem(itemId: String): Result<Unit> = runCatching {
        ensureInitialized()
        withContext(Dispatchers.IO) { api.checkinItem(itemId) }
        val updatedItem = itemsState.value.find { it.id == itemId }?.copy(status = "Available")
        if (updatedItem != null) {
            replaceItem(updatedItem)
        }
        checkedOutItemsState.value = checkedOutItemsState.value.filterNot { it.item.id == itemId }
        refreshStats()
    }

    suspend fun refreshStats() {
        runCatching {
            val stats = withContext(Dispatchers.IO) { api.getItemStats() }
            statsState.value = InventoryStats(
                totalValue = stats.totalValue,
                stolenLostDamagedValue = stats.stolenLostDamagedValue,
                stolenLostDamagedCount = stats.stolenLostDamagedCount,
                checkedOutCount = stats.checkedOutCount
            )
        }.onFailure { ex ->
            Log.w(TAG, "refreshStats: failed", ex)
        }
    }

    fun getTotalValue(): Double = statsState.value.totalValue

    fun getStolenLostDamagedValue(): Double = statsState.value.stolenLostDamagedValue

    fun getStolenLostDamagedCount(): Int = statsState.value.stolenLostDamagedCount

    fun getCheckedOutCount(): Int = statsState.value.checkedOutCount

    private fun replaceItem(item: InventoryItem) {
        itemsState.value = itemsState.value.map { if (it.id == item.id) item else it }
        checkedOutItemsState.value = checkedOutItemsState.value.map {
            if (it.item.id == item.id) it.copy(item = item) else it
        }
    }

    private fun InventoryItem.toRequest(): ItemRequest = ItemRequest(
        name = name,
        serialId = serialId,
        description = description,
        condition = condition,
        status = status,
        value = value,
        permanentCheckout = permanentCheckout,
        permissionNeeded = permissionNeeded,
        driversLicenseNeeded = driversLicenseNeeded
    )

    private fun CheckedOutItemDetailDto.toDomainDetail(): CheckedOutItemDetail {
        val item = item.toDomainItem()
        val user = user.toDomainUser()
        return CheckedOutItemDetail(
            item = item,
            user = user,
            checkoutRecord = checkoutRecord.toDomainCheckout(),
            daysOut = daysOut
        )
    }

    private fun InventoryItemDto.toDomainItem(): InventoryItem = InventoryItem(
        id = id,
        name = name,
        serialId = serialId,
        description = description.orEmpty(),
        condition = condition,
        status = status,
        value = value,
        permanentCheckout = permanentCheckout,
        permissionNeeded = permissionNeeded,
        driversLicenseNeeded = driversLicenseNeeded,
        createdAt = createdAt.toDate(),
        updatedAt = updatedAt.toDate()
    )

    private fun UserDto.toDomainUser(): User = User(
        id = id,
        name = name,
        email = email,
        role = role,
        phone = phone,
        company = company,
        driverLicense = driverLicense,
        employeeId = employeeId,
        createdAt = createdAt.toDate()
    )

    private fun com.example.phoenixinventory.data.network.CheckoutRecordDto.toDomainCheckout(): CheckoutRecord = CheckoutRecord(
        id = id,
        itemId = itemId,
        userId = userId,
        checkedOutAt = checkedOutAt.toDate(),
        checkedInAt = checkedInAt.toDate(),
        notes = notes.orEmpty()
    )

    private fun String?.toDate(): Date? = try {
        this?.let { Date.from(Instant.parse(it)) }
    } catch (ex: DateTimeParseException) {
        Log.w(TAG, "Failed to parse date: $this", ex)
        null
    }
}
