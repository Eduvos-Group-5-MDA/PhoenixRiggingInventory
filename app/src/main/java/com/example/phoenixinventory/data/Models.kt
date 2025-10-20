package com.example.phoenixinventory.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import java.util.UUID

data class InventoryItem(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val serialId: String = "",
    val description: String = "",
    val condition: String = "", // Excellent, Good, Fair, Poor
    val status: String = "", // Available, Checked Out, Under Maintenance, Retired, Stolen, Lost, Damaged
    val value: Double = 0.0,
    val permanentCheckout: Boolean = false,
    val permissionNeeded: Boolean = false,
    val driversLicenseNeeded: Boolean = false,
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val updatedAt: Date? = null
) {
    // No-arg constructor for Firestore
    constructor() : this(
        id = UUID.randomUUID().toString(),
        name = "",
        serialId = "",
        description = "",
        condition = "",
        status = "",
        value = 0.0,
        permanentCheckout = false,
        permissionNeeded = false,
        driversLicenseNeeded = false,
        createdAt = null,
        updatedAt = null
    )
}

data class User(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val email: String = "",
    val role: String = "", // Admin, Manager, Employee, Guest
    val phone: String = "",
    val idNumber: String? = null,
    val company: String? = null,
    val hasDriverLicense: Boolean? = null,
    @ServerTimestamp
    val createdAt: Date? = null
) {
    // No-arg constructor for Firestore
    constructor() : this(
        id = UUID.randomUUID().toString(),
        name = "",
        email = "",
        role = "",
        phone = "",
        idNumber = null,
        company = null,
        hasDriverLicense = null,
        createdAt = null
    )
}

data class CheckoutRecord(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val itemId: String = "",
    val userId: String = "",
    @ServerTimestamp
    val checkedOutAt: Date? = null,
    val checkedInAt: Date? = null,
    val notes: String = ""
) {
    // No-arg constructor for Firestore
    constructor() : this(
        id = UUID.randomUUID().toString(),
        itemId = "",
        userId = "",
        checkedOutAt = null,
        checkedInAt = null,
        notes = ""
    )
}

// Helper for checked out items with full details
data class CheckedOutItemDetail(
    val item: InventoryItem,
    val user: User,
    val checkoutRecord: CheckoutRecord,
    val daysOut: Int
)