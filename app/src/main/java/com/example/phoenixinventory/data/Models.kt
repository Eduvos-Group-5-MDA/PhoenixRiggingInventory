package com.example.phoenixinventory.data

import java.util.Date
import java.util.UUID

data class InventoryItem(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val serialId: String,
    val description: String,
    val condition: String, // Excellent, Good, Fair, Poor
    val status: String, // Available, Checked Out, Under Maintenance, Retired, Stolen, Lost, Damaged
    val value: Double = 0.0,
    val permanentCheckout: Boolean = false,
    val permissionNeeded: Boolean = false,
    val driversLicenseNeeded: Boolean = false,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date()
)

data class User(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val email: String,
    val role: String, // Admin, Manager, Employee
    val createdAt: Date = Date()
)

data class CheckoutRecord(
    val id: String = UUID.randomUUID().toString(),
    val itemId: String,
    val userId: String,
    val checkedOutAt: Date,
    val checkedInAt: Date? = null,
    val notes: String = ""
)

// Helper for checked out items with full details
data class CheckedOutItemDetail(
    val item: InventoryItem,
    val user: User,
    val checkoutRecord: CheckoutRecord,
    val daysOut: Int
)
