package com.example.phoenixinventory.data

import java.util.Date
import java.util.UUID

data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val serialId: String = "",
    val description: String = "",
    val condition: String = "",
    val status: String = "",
    val value: Double = 0.0,
    val permanentCheckout: Boolean = false,
    val permissionNeeded: Boolean = false,
    val driversLicenseNeeded: Boolean = false,
    val createdAt: Date? = null,
    val updatedAt: Date? = null
)

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val role: String = "",
    val phone: String? = null,
    val company: String? = null,
    val driverLicense: Boolean = false,
    val employeeId: String? = null,
    val createdAt: Date? = null
)

data class CheckoutRecord(
    val id: String = UUID.randomUUID().toString(),
    val itemId: String = "",
    val userId: String = "",
    val checkedOutAt: Date? = null,
    val checkedInAt: Date? = null,
    val notes: String = ""
)

data class CheckedOutItemDetail(
    val item: InventoryItem,
    val user: User,
    val checkoutRecord: CheckoutRecord,
    val daysOut: Int
)

data class InventoryStats(
    val totalValue: Double = 0.0,
    val stolenLostDamagedValue: Double = 0.0,
    val stolenLostDamagedCount: Int = 0,
    val checkedOutCount: Int = 0
)