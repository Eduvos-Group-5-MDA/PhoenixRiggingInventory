package com.example.phoenixinventory.data

import java.util.Date
import java.util.concurrent.TimeUnit

object DataRepository {

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

    // Item operations
    fun getAllItems(): List<InventoryItem> = items.toList()

    fun getItemById(id: String): InventoryItem? = items.find { it.id == id }

    fun addItem(item: InventoryItem) {
        items.add(item)
    }

    fun updateItem(item: InventoryItem) {
        val index = items.indexOfFirst { it.id == item.id }
        if (index != -1) {
            items[index] = item.copy(updatedAt = Date())
        }
    }

    fun removeItem(id: String) {
        items.removeIf { it.id == id }
        // Also remove related checkout records
        checkoutRecords.removeIf { it.itemId == id }
    }

    // User operations
    fun getAllUsers(): List<User> = users.toList()

    fun getUserById(id: String): User? = users.find { it.id == id }

    fun getCurrentUser(): User = users.first() // For demo, returns first user

    // Checkout operations
    fun checkOutItem(itemId: String, userId: String, notes: String = "") {
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

    fun checkInItem(itemId: String) {
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

    fun getCheckedOutItems(): List<CheckedOutItemDetail> {
        return checkoutRecords
            .filter { it.checkedInAt == null }
            .mapNotNull { record ->
                val item = getItemById(record.itemId)
                val user = getUserById(record.userId)
                if (item != null && user != null) {
                    val daysOut = TimeUnit.MILLISECONDS.toDays(
                        Date().time - record.checkedOutAt.time
                    ).toInt()
                    CheckedOutItemDetail(item, user, record, daysOut)
                } else null
            }
    }

    fun getItemsOutLongerThan(days: Int): List<CheckedOutItemDetail> {
        return getCheckedOutItems().filter { it.daysOut >= days }
    }

    // Stats operations
    fun getTotalValue(): Double = items.sumOf { it.value }

    fun getStolenLostDamagedValue(): Double {
        return items
            .filter { it.status in listOf("Stolen", "Lost", "Damaged") }
            .sumOf { it.value }
    }

    fun getStolenLostDamagedCount(): Int {
        return items.count { it.status in listOf("Stolen", "Lost", "Damaged") }
    }

    fun getCheckedOutCount(): Int {
        return items.count { it.status == "Checked Out" }
    }
}
