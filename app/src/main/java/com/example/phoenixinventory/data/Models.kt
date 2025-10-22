package com.example.phoenixinventory.data

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date
import java.util.UUID

/**
 * Represents an inventory item in the system.
 * Tracks all details about rigging equipment including status, condition, and value.
 */
data class InventoryItem(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val serialId: String = "",
    val description: String = "",
    val category: String = "", // Power Tools, Hand Tools, Rigging Equipment, Vehicle, Miscellaneous
    val condition: String = "", // Excellent, Good, Fair, Poor
    val status: String = "", // Available, Checked Out, Under Maintenance, Retired, Stolen, Lost, Damaged
    val value: Double = 0.0,
    val permanentCheckout: Boolean = false,
    val permissionNeeded: Boolean = false,
    val driversLicenseNeeded: Boolean = false,
    val deleted: Boolean = false,
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
        category = "",
        condition = "",
        status = "",
        value = 0.0,
        permanentCheckout = false,
        permissionNeeded = false,
        driversLicenseNeeded = false,
        deleted = false,
        createdAt = null,
        updatedAt = null
    )
}

/**
 * Represents a user in the system with role-based permissions.
 * Roles: Admin, Manager, Employee, Guest
 */
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

/**
 * Tracks item checkout history.
 * Links items to users and records checkout/check-in timestamps for audit trail.
 */
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

/**
 * Combined view model for displaying checked out items.
 * Joins item, user, and checkout record data with calculated days out.
 */
data class CheckedOutItemDetail(
    val item: InventoryItem,
    val user: User,
    val checkoutRecord: CheckoutRecord,
    val daysOut: Int
)

/**
 * User-submitted reports for issues, suggestions, bugs, or other concerns.
 * Tracks report status and resolution by admins/managers.
 */
data class Report(
    @DocumentId
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val description: String = "",
    val category: String = "", // Issue, Suggestion, Bug, Other
    val priority: String = "", // Low, Medium, High, Critical
    val userId: String = "", // ID of user who submitted the report
    val userName: String = "", // Name of user who submitted (for display)
    val userEmail: String = "", // Email of user who submitted
    val status: String = "Unresolved", // Unresolved, Resolved
    val resolvedBy: String? = null, // ID of admin/manager who resolved it
    val resolvedByName: String? = null, // Name of admin/manager who resolved it
    @ServerTimestamp
    val createdAt: Date? = null,
    @ServerTimestamp
    val resolvedAt: Date? = null
) {
    // No-arg constructor for Firestore
    constructor() : this(
        id = UUID.randomUUID().toString(),
        title = "",
        description = "",
        category = "",
        priority = "",
        userId = "",
        userName = "",
        userEmail = "",
        status = "Unresolved",
        resolvedBy = null,
        resolvedByName = null,
        createdAt = null,
        resolvedAt = null
    )
}