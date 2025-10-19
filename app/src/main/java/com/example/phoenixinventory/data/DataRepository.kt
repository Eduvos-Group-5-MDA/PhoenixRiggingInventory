package com.example.phoenixinventory.data

import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * DataRepository now uses Firebase as the backend.
 * All operations are synchronous wrappers around Firebase calls.
 * For better performance, consider using suspend functions directly from FirebaseRepository.
 */
object DataRepository {

    private val firebaseRepo = FirebaseRepository()

    // Fallback in-memory storage (used only if Firebase is unavailable)
    private val items = mutableListOf<InventoryItem>()
    private val users = mutableListOf<User>()
    private val checkoutRecords = mutableListOf<CheckoutRecord>()

    init {
        // Initialize with sample data
        users.addAll(
            listOf(
                User(
                    id = "user1",
                    name = "John Doe",
                    email = "stadlerkieran@gmail.com",
                    role = "Employee"
                ),
                User(
                    id = "user2",
                    name = "Jane Smith",
                    email = "jane.smith@example.com",
                    role = "Manager"
                ),
                User(
                    id = "user3",
                    name = "Mike Johnson",
                    email = "mike.j@example.com",
                    role = "Employee"
                )
            )
        )

        items.addAll(
            listOf(
                InventoryItem(
                    id = "item1",
                    name = "Safety Harness Type A",
                    serialId = "SH-001",
                    description = "Professional safety harness for rigging operations",
                    condition = "Excellent",
                    status = "Available",
                    value = 450.0
                ),
                InventoryItem(
                    id = "item2",
                    name = "Carabiner Set (10pc)",
                    serialId = "CAR-102",
                    description = "Heavy-duty aluminum carabiners",
                    condition = "Good",
                    status = "Checked Out",
                    value = 350.0
                ),
                InventoryItem(
                    id = "item3",
                    name = "Rigging Rope 50m",
                    serialId = "RR-203",
                    description = "Industrial grade rigging rope",
                    condition = "Fair",
                    status = "Under Maintenance",
                    value = 280.0
                ),
                InventoryItem(
                    id = "item4",
                    name = "Pulley System",
                    serialId = "PS-304",
                    description = "Multi-point pulley rigging system",
                    condition = "Excellent",
                    status = "Checked Out",
                    value = 890.0,
                    permissionNeeded = true
                ),
                InventoryItem(
                    id = "item5",
                    name = "Damaged Cable",
                    serialId = "DC-405",
                    description = "Steel cable - damaged during operation",
                    condition = "Poor",
                    status = "Damaged",
                    value = 520.0
                )
            )
        )

        // Create some checkout records
        val now = Date()
        val thirtyFiveDaysAgo = Date(now.time - TimeUnit.DAYS.toMillis(35))
        val fiveDaysAgo = Date(now.time - TimeUnit.DAYS.toMillis(5))

        checkoutRecords.addAll(
            listOf(
                CheckoutRecord(
                    id = "checkout1",
                    itemId = "item2",
                    userId = "user1",
                    checkedOutAt = fiveDaysAgo
                ),
                CheckoutRecord(
                    id = "checkout2",
                    itemId = "item4",
                    userId = "user2",
                    checkedOutAt = thirtyFiveDaysAgo
                )
            )
        )
    }

    // Firebase Repository Access
    fun getFirebaseRepository(): FirebaseRepository = firebaseRepo

    // Item operations (Firebase-backed)
    fun getAllItems(): List<InventoryItem> = runBlocking {
        firebaseRepo.getAllItems().getOrElse {
            items.toList() // Fallback to in-memory
        }
    }

    fun getItemById(id: String): InventoryItem? = runBlocking {
        firebaseRepo.getItemById(id).getOrElse {
            items.find { it.id == id } // Fallback to in-memory
        }
    }

    fun addItem(item: InventoryItem) = runBlocking {
        val result = firebaseRepo.addItem(item)
        if (result.isFailure) {
            items.add(item) // Fallback to in-memory
        }
    }

    fun updateItem(item: InventoryItem) = runBlocking {
        val result = firebaseRepo.updateItem(item)
        if (result.isFailure) {
            val index = items.indexOfFirst { it.id == item.id }
            if (index != -1) {
                items[index] = item.copy(updatedAt = Date())
            }
        }
    }

    fun removeItem(id: String) = runBlocking {
        val result = firebaseRepo.deleteItem(id)
        if (result.isFailure) {
            items.removeIf { it.id == id }
            checkoutRecords.removeIf { it.itemId == id }
        }
    }

    // User operations (Firebase-backed)
    fun getAllUsers(): List<User> = runBlocking {
        firebaseRepo.getAllUsers().getOrElse {
            users.toList() // Fallback to in-memory
        }
    }

    fun getUserById(id: String): User? = runBlocking {
        firebaseRepo.getUserById(id).getOrElse {
            users.find { it.id == id } // Fallback to in-memory
        }
    }

    fun getCurrentUser(): User = runBlocking {
        firebaseRepo.getCurrentUser().getOrElse {
            null // Return null on error, fallback to in-memory below
        } ?: users.firstOrNull() ?: User(name = "Guest", email = "guest@example.com", role = "Employee")
    }

    fun updateUser(user: User) = runBlocking {
        val result = firebaseRepo.updateUser(user)
        if (result.isFailure) {
            val index = users.indexOfFirst { it.id == user.id }
            if (index != -1) {
                users[index] = user
            }
        }
    }

    // Checkout operations (Firebase-backed)
    fun checkOutItem(itemId: String, userId: String, notes: String = "") = runBlocking {
        val result = firebaseRepo.checkOutItem(itemId, userId, notes)
        if (result.isFailure) {
            val item = getItemById(itemId)
            if (item != null && item.status == "Available") {
                updateItem(item.copy(status = "Checked Out"))
                checkoutRecords.add(
                    CheckoutRecord(
                        itemId = itemId,
                        userId = userId,
                        checkedOutAt = Date(),
                        notes = notes
                    )
                )
            }
        }
    }

    fun checkInItem(itemId: String) = runBlocking {
        val result = firebaseRepo.checkInItem(itemId)
        if (result.isFailure) {
            val item = getItemById(itemId)
            val activeCheckout = checkoutRecords.find { it.itemId == itemId && it.checkedInAt == null }

            if (item != null && activeCheckout != null) {
                updateItem(item.copy(status = "Available"))
                val index = checkoutRecords.indexOf(activeCheckout)
                if (index != -1) {
                    checkoutRecords[index] = activeCheckout.copy(checkedInAt = Date())
                }
            }
        }
    }

    fun getCheckedOutItems(): List<CheckedOutItemDetail> = runBlocking {
        firebaseRepo.getCheckedOutItems().getOrElse {
            checkoutRecords
                .filter { it.checkedInAt == null }
                .mapNotNull { record ->
                    val item = getItemById(record.itemId)
                    val user = getUserById(record.userId)
                    if (item != null && user != null && record.checkedOutAt != null) {
                        val daysOut = TimeUnit.MILLISECONDS.toDays(
                            Date().time - record.checkedOutAt!!.time
                        ).toInt()
                        CheckedOutItemDetail(item, user, record, daysOut)
                    } else null
                }
        }
    }

    fun getItemsOutLongerThan(days: Int): List<CheckedOutItemDetail> = runBlocking {
        firebaseRepo.getItemsOutLongerThan(days).getOrElse {
            getCheckedOutItems().filter { it.daysOut >= days }
        }
    }

    // Stats operations (Firebase-backed)
    fun getTotalValue(): Double = runBlocking {
        firebaseRepo.getTotalValue().getOrElse {
            items.sumOf { it.value }
        }
    }

    fun getStolenLostDamagedValue(): Double = runBlocking {
        firebaseRepo.getStolenLostDamagedValue().getOrElse {
            items
                .filter { it.status in listOf("Stolen", "Lost", "Damaged") }
                .sumOf { it.value }
        }
    }

    fun getStolenLostDamagedCount(): Int = runBlocking {
        firebaseRepo.getStolenLostDamagedCount().getOrElse {
            items.count { it.status in listOf("Stolen", "Lost", "Damaged") }
        }
    }

    fun getCheckedOutCount(): Int = runBlocking {
        firebaseRepo.getCheckedOutCount().getOrElse {
            items.count { it.status == "Checked Out" }
        }
    }

    // Initialize sample data in Firebase
    fun initializeSampleData() = runBlocking {
        firebaseRepo.initializeSampleData()
    }
}
