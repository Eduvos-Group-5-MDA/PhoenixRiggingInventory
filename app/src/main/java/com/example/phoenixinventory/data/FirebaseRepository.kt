package com.example.phoenixinventory.data

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import java.util.Date
import java.util.concurrent.TimeUnit

/**
 * Repository class that handles all Firebase Firestore operations.
 * All methods are suspend functions for proper coroutine support.
 */
class FirebaseRepository {

    private val db = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "FirebaseRepository"
        private const val COLLECTION_ITEMS = "inventory_items"
        private const val COLLECTION_USERS = "users"
        private const val COLLECTION_CHECKOUTS = "checkout_records"
        private const val COLLECTION_REPORTS = "reports"
    }

    // ==================== Item Operations ====================

    /**
     * Get all inventory items from Firestore (excluding deleted items)
     */
    suspend fun getAllItems(): Result<List<InventoryItem>> = try {
        val snapshot = db.collection(COLLECTION_ITEMS)
            .get()
            .await()

        val items = snapshot.documents.mapNotNull { doc ->
            doc.toObject(InventoryItem::class.java)
        }.filter { item ->
            // Filter out items where deleted is explicitly true
            // Items without the deleted field are treated as not deleted
            !item.deleted
        }
        Log.d(TAG, "getAllItems: Retrieved ${items.size} items")
        Result.success(items)
    } catch (e: Exception) {
        Log.e(TAG, "getAllItems: Error", e)
        Result.failure(e)
    }

    /**
     * Get a specific item by ID
     */
    suspend fun getItemById(id: String): Result<InventoryItem?> = try {
        val document = db.collection(COLLECTION_ITEMS)
            .document(id)
            .get()
            .await()

        val item = document.toObject(InventoryItem::class.java)
        Log.d(TAG, "getItemById: Retrieved item $id")
        Result.success(item)
    } catch (e: Exception) {
        Log.e(TAG, "getItemById: Error for id $id", e)
        Result.failure(e)
    }

    /**
     * Add a new item to Firestore
     */
    suspend fun addItem(item: InventoryItem): Result<Unit> = try {
        val itemWithTimestamp = item.copy(
            createdAt = Date(),
            updatedAt = Date()
        )

        db.collection(COLLECTION_ITEMS)
            .document(item.id)
            .set(itemWithTimestamp)
            .await()

        Log.d(TAG, "addItem: Added item ${item.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "addItem: Error adding item ${item.id}", e)
        Result.failure(e)
    }

    /**
     * Update an existing item
     */
    suspend fun updateItem(item: InventoryItem): Result<Unit> = try {
        val itemWithTimestamp = item.copy(updatedAt = Date())

        db.collection(COLLECTION_ITEMS)
            .document(item.id)
            .set(itemWithTimestamp)
            .await()

        Log.d(TAG, "updateItem: Updated item ${item.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "updateItem: Error updating item ${item.id}", e)
        Result.failure(e)
    }

    /**
     * Soft delete an item (marks as deleted, doesn't actually delete)
     */
    suspend fun deleteItem(id: String): Result<Unit> = try {
        db.collection(COLLECTION_ITEMS)
            .document(id)
            .update(
                mapOf(
                    "deleted" to true,
                    "updatedAt" to Date()
                )
            )
            .await()

        Log.d(TAG, "deleteItem: Soft deleted item $id")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "deleteItem: Error soft deleting item $id", e)
        Result.failure(e)
    }

    /**
     * Get all deleted items
     */
    suspend fun getDeletedItems(): Result<List<InventoryItem>> = try {
        val snapshot = db.collection(COLLECTION_ITEMS)
            .whereEqualTo("deleted", true)
            .get()
            .await()

        val items = snapshot.documents.mapNotNull { doc ->
            doc.toObject(InventoryItem::class.java)
        }
        Log.d(TAG, "getDeletedItems: Retrieved ${items.size} deleted items")
        Result.success(items)
    } catch (e: Exception) {
        Log.e(TAG, "getDeletedItems: Error", e)
        Result.failure(e)
    }

    /**
     * Restore a deleted item (marks as available)
     */
    suspend fun restoreItem(id: String): Result<Unit> = try {
        db.collection(COLLECTION_ITEMS)
            .document(id)
            .update(
                mapOf(
                    "deleted" to false,
                    "status" to "Available",
                    "updatedAt" to Date()
                )
            )
            .await()

        Log.d(TAG, "restoreItem: Restored item $id")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "restoreItem: Error restoring item $id", e)
        Result.failure(e)
    }

    // ==================== User Operations ====================

    /**
     * Get all users from Firestore
     */
    suspend fun getAllUsers(): Result<List<User>> = try {
        val snapshot = db.collection(COLLECTION_USERS)
            .get()
            .await()

        val users = snapshot.documents.mapNotNull { doc ->
            doc.toObject(User::class.java)
        }
        Log.d(TAG, "getAllUsers: Retrieved ${users.size} users")
        Result.success(users)
    } catch (e: Exception) {
        Log.e(TAG, "getAllUsers: Error", e)
        Result.failure(e)
    }

    /**
     * Get a specific user by ID
     */
    suspend fun getUserById(id: String): Result<User?> = try {
        val document = db.collection(COLLECTION_USERS)
            .document(id)
            .get()
            .await()

        val user = document.toObject(User::class.java)
        Log.d(TAG, "getUserById: Retrieved user $id")
        Result.success(user)
    } catch (e: Exception) {
        Log.e(TAG, "getUserById: Error for id $id", e)
        Result.failure(e)
    }

    /**
     * Add a new user to Firestore
     */
    suspend fun addUser(user: User): Result<Unit> = try {
        val userWithTimestamp = user.copy(createdAt = Date())

        db.collection(COLLECTION_USERS)
            .document(user.id)
            .set(userWithTimestamp)
            .await()

        Log.d(TAG, "addUser: Added user ${user.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "addUser: Error adding user ${user.id}", e)
        Result.failure(e)
    }

    /**
     * Update an existing user
     */
    suspend fun updateUser(user: User): Result<Unit> = try {
        db.collection(COLLECTION_USERS)
            .document(user.id)
            .set(user)
            .await()

        Log.d(TAG, "updateUser: Updated user ${user.id}")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "updateUser: Error updating user ${user.id}", e)
        Result.failure(e)
    }

    /**
     * Get the current user (for demo purposes, returns first user)
     */
    suspend fun getCurrentUser(): Result<User?> = try {
        val snapshot = db.collection(COLLECTION_USERS)
            .limit(1)
            .get()
            .await()

        val user = snapshot.documents.firstOrNull()?.toObject(User::class.java)
        Log.d(TAG, "getCurrentUser: Retrieved user ${user?.id}")
        Result.success(user)
    } catch (e: Exception) {
        Log.e(TAG, "getCurrentUser: Error", e)
        Result.failure(e)
    }

    /**
     * Hard delete a user (permanently removes from database)
     * Should only be called by admins or managers
     */
    suspend fun hardDeleteUser(userId: String): Result<Unit> {
        return try {
            // Check if user has any active checkouts
            val checkoutsSnapshot = db.collection(COLLECTION_CHECKOUTS)
                .whereEqualTo("userId", userId)
                .whereEqualTo("checkedInAt", null)
                .get()
                .await()

            if (!checkoutsSnapshot.isEmpty) {
                return Result.failure(Exception("Cannot delete user with active checkouts. Please check in all items first."))
            }

            // Permanently delete the user
            db.collection(COLLECTION_USERS)
                .document(userId)
                .delete()
                .await()

            Log.d(TAG, "hardDeleteUser: Permanently deleted user $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "hardDeleteUser: Error deleting user $userId", e)
            Result.failure(e)
        }
    }

    // ==================== Checkout Operations ====================

    /**
     * Check out an item to a user
     */
    suspend fun checkOutItem(itemId: String, userId: String, notes: String = ""): Result<Unit> {
        return try {
            // Get the item
            val itemDoc = db.collection(COLLECTION_ITEMS).document(itemId).get().await()
            val item = itemDoc.toObject(InventoryItem::class.java)
                ?: return Result.failure(Exception("Item not found"))

            // Check if item is available
            if (item.status != "Available") {
                return Result.failure(Exception("Item is not available for checkout"))
            }

            // Create checkout record
            val checkoutRecord = CheckoutRecord(
                itemId = itemId,
                userId = userId,
                checkedOutAt = Date(),
                notes = notes
            )

            db.collection(COLLECTION_CHECKOUTS)
                .document(checkoutRecord.id)
                .set(checkoutRecord)
                .await()

            // Update item status
            db.collection(COLLECTION_ITEMS)
                .document(itemId)
                .update("status", "Checked Out", "updatedAt", Date())
                .await()

            Log.d(TAG, "checkOutItem: Checked out item $itemId to user $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "checkOutItem: Error checking out item $itemId", e)
            Result.failure(e)
        }
    }

    /**
     * Check in an item
     */
    suspend fun checkInItem(itemId: String): Result<Unit> {
        return try {
            // Find active checkout record
            val checkoutSnapshot = db.collection(COLLECTION_CHECKOUTS)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("checkedInAt", null)
                .get()
                .await()

            if (checkoutSnapshot.isEmpty) {
                return Result.failure(Exception("No active checkout found for item"))
            }

            val checkoutDoc = checkoutSnapshot.documents.first()

            // Update checkout record with check-in time
            checkoutDoc.reference
                .update("checkedInAt", Date())
                .await()

            // Update item status
            db.collection(COLLECTION_ITEMS)
                .document(itemId)
                .update("status", "Available", "updatedAt", Date())
                .await()

            Log.d(TAG, "checkInItem: Checked in item $itemId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "checkInItem: Error checking in item $itemId", e)
            Result.failure(e)
        }
    }

    /**
     * Get current checkout info for a specific item (if checked out)
     */
    suspend fun getCurrentCheckout(itemId: String): Result<CheckedOutItemDetail?> {
        return try {
            Log.d(TAG, "getCurrentCheckout: Looking for checkout for itemId: $itemId")

            val checkoutSnapshot = db.collection(COLLECTION_CHECKOUTS)
                .whereEqualTo("itemId", itemId)
                .whereEqualTo("checkedInAt", null)
                .get()
                .await()

            Log.d(TAG, "getCurrentCheckout: Found ${checkoutSnapshot.size()} checkout records")

            if (checkoutSnapshot.isEmpty) {
                Log.d(TAG, "getCurrentCheckout: No active checkout for item $itemId")
                return Result.success(null)
            }

            val checkoutDoc = checkoutSnapshot.documents.first()
            val checkoutRecord = checkoutDoc.toObject(CheckoutRecord::class.java)
                ?: return Result.success(null)

            Log.d(TAG, "getCurrentCheckout: Checkout record - itemId: ${checkoutRecord.itemId}, userId: ${checkoutRecord.userId}")

            // Get user details
            Log.d(TAG, "getCurrentCheckout: Fetching user with ID: ${checkoutRecord.userId}")
            val userDoc = db.collection(COLLECTION_USERS).document(checkoutRecord.userId).get().await()
            Log.d(TAG, "getCurrentCheckout: User document exists: ${userDoc.exists()}")
            val user = userDoc.toObject(User::class.java)
            Log.d(TAG, "getCurrentCheckout: User found: ${user?.name}")
            if (user == null) {
                Log.e(TAG, "getCurrentCheckout: User is null for userId: ${checkoutRecord.userId}")
                return Result.success(null)
            }

            // Get item details
            val itemDoc = db.collection(COLLECTION_ITEMS).document(itemId).get().await()
            val item = itemDoc.toObject(InventoryItem::class.java) ?: return Result.success(null)

            val daysOut = if (checkoutRecord.checkedOutAt != null) {
                TimeUnit.MILLISECONDS.toDays(
                    Date().time - checkoutRecord.checkedOutAt!!.time
                ).toInt()
            } else 0

            val detail = CheckedOutItemDetail(item, user, checkoutRecord, daysOut)
            Log.d(TAG, "getCurrentCheckout: Retrieved checkout info for item $itemId")
            Result.success(detail)
        } catch (e: Exception) {
            Log.e(TAG, "getCurrentCheckout: Error", e)
            Result.failure(e)
        }
    }

    /**
     * Get all currently checked out items with full details
     */
    suspend fun getCheckedOutItems(): Result<List<CheckedOutItemDetail>> = try {
        val checkoutSnapshot = db.collection(COLLECTION_CHECKOUTS)
            .whereEqualTo("checkedInAt", null)
            .get()
            .await()

        val checkedOutDetails = checkoutSnapshot.documents.mapNotNull { doc ->
            val checkoutRecord = doc.toObject(CheckoutRecord::class.java) ?: return@mapNotNull null

            // Get item and user details
            val itemDoc =
                db.collection(COLLECTION_ITEMS).document(checkoutRecord.itemId).get().await()
            val item = itemDoc.toObject(InventoryItem::class.java) ?: return@mapNotNull null

            // Filter out deleted items
            if (item.deleted) return@mapNotNull null

            val userDoc =
                db.collection(COLLECTION_USERS).document(checkoutRecord.userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: return@mapNotNull null

            // Calculate days out
            val daysOut = if (checkoutRecord.checkedOutAt != null) {
                TimeUnit.MILLISECONDS.toDays(
                    Date().time - checkoutRecord.checkedOutAt!!.time
                ).toInt()
            } else 0

            CheckedOutItemDetail(item, user, checkoutRecord, daysOut)
        }

        Log.d(TAG, "getCheckedOutItems: Retrieved ${checkedOutDetails.size} checked out items")
        Result.success(checkedOutDetails)
    } catch (e: Exception) {
        Log.e(TAG, "getCheckedOutItems: Error", e)
        Result.failure(e)
    }

    /**
     * Get items checked out longer than specified days
     */
    suspend fun getItemsOutLongerThan(days: Int): Result<List<CheckedOutItemDetail>> = try {
        val cutoffDate = Date(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(days.toLong()))

        val checkoutSnapshot = db.collection(COLLECTION_CHECKOUTS)
            .whereEqualTo("checkedInAt", null)
            .whereLessThan("checkedOutAt", cutoffDate)
            .get()
            .await()

        val overdueItems = checkoutSnapshot.documents.mapNotNull { doc ->
            val checkoutRecord = doc.toObject(CheckoutRecord::class.java) ?: return@mapNotNull null

            val itemDoc =
                db.collection(COLLECTION_ITEMS).document(checkoutRecord.itemId).get().await()
            val item = itemDoc.toObject(InventoryItem::class.java) ?: return@mapNotNull null

            // Filter out deleted items
            if (item.deleted) return@mapNotNull null

            val userDoc =
                db.collection(COLLECTION_USERS).document(checkoutRecord.userId).get().await()
            val user = userDoc.toObject(User::class.java) ?: return@mapNotNull null

            val daysOut = if (checkoutRecord.checkedOutAt != null) {
                TimeUnit.MILLISECONDS.toDays(
                    Date().time - checkoutRecord.checkedOutAt!!.time
                ).toInt()
            } else 0

            CheckedOutItemDetail(item, user, checkoutRecord, daysOut)
        }

        Log.d(
            TAG,
            "getItemsOutLongerThan: Retrieved ${overdueItems.size} items out longer than $days days"
        )
        Result.success(overdueItems)
    } catch (e: Exception) {
        Log.e(TAG, "getItemsOutLongerThan: Error", e)
        Result.failure(e)
    }

    // ==================== Statistics Operations ====================

    /**
     * Get total value of all inventory items
     */
    suspend fun getTotalValue(): Result<Double> = try {
        val items = getAllItems().getOrThrow()
        val total = items.sumOf { it.value }
        Log.d(TAG, "getTotalValue: $total")
        Result.success(total)
    } catch (e: Exception) {
        Log.e(TAG, "getTotalValue: Error", e)
        Result.failure(e)
    }

    /**
     * Get total value of stolen, lost, or damaged items
     */
    suspend fun getStolenLostDamagedValue(): Result<Double> = try {
        val snapshot = db.collection(COLLECTION_ITEMS)
            .whereIn("status", listOf("Stolen", "Lost", "Damaged"))
            .get()
            .await()

        val total = snapshot.documents.mapNotNull { doc ->
            doc.toObject(InventoryItem::class.java)?.value
        }.sum()

        Log.d(TAG, "getStolenLostDamagedValue: $total")
        Result.success(total)
    } catch (e: Exception) {
        Log.e(TAG, "getStolenLostDamagedValue: Error", e)
        Result.failure(e)
    }

    /**
     * Get count of stolen, lost, or damaged items
     */
    suspend fun getStolenLostDamagedCount(): Result<Int> = try {
        val snapshot = db.collection(COLLECTION_ITEMS)
            .whereIn("status", listOf("Stolen", "Lost", "Damaged"))
            .get()
            .await()

        val count = snapshot.size()
        Log.d(TAG, "getStolenLostDamagedCount: $count")
        Result.success(count)
    } catch (e: Exception) {
        Log.e(TAG, "getStolenLostDamagedCount: Error", e)
        Result.failure(e)
    }

    /**
     * Get count of checked out items
     */
    suspend fun getCheckedOutCount(): Result<Int> = try {
        val snapshot = db.collection(COLLECTION_ITEMS)
            .whereEqualTo("status", "Checked Out")
            .get()
            .await()

        val count = snapshot.size()
        Log.d(TAG, "getCheckedOutCount: $count")
        Result.success(count)
    } catch (e: Exception) {
        Log.e(TAG, "getCheckedOutCount: Error", e)
        Result.failure(e)
    }

    // ==================== Data Initialization ====================

    /**
     * Initialize Firestore with sample data
     * This should only be called once when setting up the app for the first time
     */
    suspend fun initializeSampleData(): Result<Unit> {
        return try {
            // Check if data already exists
            val existingItems = db.collection(COLLECTION_ITEMS).limit(1).get().await()
            if (!existingItems.isEmpty) {
                Log.d(TAG, "initializeSampleData: Data already exists, skipping initialization")
                return Result.success(Unit)
            }

            // Sample users
            val users = listOf(
                User(
                    id = "user1",
                    name = "John Doe",
                    email = "stadlerkieran@gmail.com",
                    role = "Employee",
                    phone = "071 234 5678",
                    createdAt = Date()
                ),
                User(
                    id = "user2",
                    name = "Jane Smith",
                    email = "jane.smith@example.com",
                    role = "Manager",
                    phone = "082 345 6789",
                    createdAt = Date()
                ),
                User(
                    id = "user3",
                    name = "Mike Johnson",
                    email = "mike.j@example.com",
                    role = "Employee",
                    phone = "083 456 7890",
                    createdAt = Date()
                )
            )

            // Sample items
            val items = listOf(
                InventoryItem(
                    id = "item1",
                    name = "Safety Harness Type A",
                    serialId = "SH-001",
                    description = "Professional safety harness for rigging operations",
                    condition = "Excellent",
                    status = "Available",
                    value = 450.0,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                InventoryItem(
                    id = "item2",
                    name = "Carabiner Set (10pc)",
                    serialId = "CAR-102",
                    description = "Heavy-duty aluminum carabiners",
                    condition = "Good",
                    status = "Checked Out",
                    value = 350.0,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                InventoryItem(
                    id = "item3",
                    name = "Rigging Rope 50m",
                    serialId = "RR-203",
                    description = "Industrial grade rigging rope",
                    condition = "Fair",
                    status = "Under Maintenance",
                    value = 280.0,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                InventoryItem(
                    id = "item4",
                    name = "Pulley System",
                    serialId = "PS-304",
                    description = "Multi-point pulley rigging system",
                    condition = "Excellent",
                    status = "Checked Out",
                    value = 890.0,
                    permissionNeeded = true,
                    createdAt = Date(),
                    updatedAt = Date()
                ),
                InventoryItem(
                    id = "item5",
                    name = "Damaged Cable",
                    serialId = "DC-405",
                    description = "Steel cable - damaged during operation",
                    condition = "Poor",
                    status = "Damaged",
                    value = 520.0,
                    createdAt = Date(),
                    updatedAt = Date()
                )
            )

            // Sample checkout records
            val now = Date()
            val thirtyFiveDaysAgo = Date(now.time - TimeUnit.DAYS.toMillis(35))
            val fiveDaysAgo = Date(now.time - TimeUnit.DAYS.toMillis(5))

            val checkoutRecords = listOf(
                CheckoutRecord(
                    id = "checkout1",
                    itemId = "item2",
                    userId = "user1",
                    checkedOutAt = fiveDaysAgo,
                    notes = "Standard checkout"
                ),
                CheckoutRecord(
                    id = "checkout2",
                    itemId = "item4",
                    userId = "user2",
                    checkedOutAt = thirtyFiveDaysAgo,
                    notes = "Long-term project"
                )
            )

            // Add all data to Firestore
            users.forEach { user ->
                db.collection(COLLECTION_USERS).document(user.id).set(user).await()
            }

            items.forEach { item ->
                db.collection(COLLECTION_ITEMS).document(item.id).set(item).await()
            }

            checkoutRecords.forEach { record ->
                db.collection(COLLECTION_CHECKOUTS).document(record.id).set(record).await()
            }

            Log.d(TAG, "initializeSampleData: Successfully initialized sample data")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "initializeSampleData: Error", e)
            Result.failure(e)
        }
    }

    // ==================== Report Operations ====================

    /**
     * Create a new report
     */
    suspend fun createReport(report: Report): Result<Unit> = try {
        db.collection(COLLECTION_REPORTS)
            .document(report.id)
            .set(report)
            .await()
        Log.d(TAG, "createReport: Report ${report.id} created successfully")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "createReport: Error", e)
        Result.failure(e)
    }

    /**
     * Get all reports (for admins/managers)
     */
    suspend fun getAllReports(): Result<List<Report>> = try {
        val snapshot = db.collection(COLLECTION_REPORTS)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val reports = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Report::class.java)
        }
        Log.d(TAG, "getAllReports: Retrieved ${reports.size} reports")
        Result.success(reports)
    } catch (e: Exception) {
        Log.e(TAG, "getAllReports: Error", e)
        Result.failure(e)
    }

    /**
     * Get reports by status
     */
    suspend fun getReportsByStatus(status: String): Result<List<Report>> = try {
        val snapshot = db.collection(COLLECTION_REPORTS)
            .whereEqualTo("status", status)
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get()
            .await()

        val reports = snapshot.documents.mapNotNull { doc ->
            doc.toObject(Report::class.java)
        }
        Log.d(TAG, "getReportsByStatus: Retrieved ${reports.size} $status reports")
        Result.success(reports)
    } catch (e: Exception) {
        Log.e(TAG, "getReportsByStatus: Error", e)
        Result.failure(e)
    }

    /**
     * Get a specific report by ID
     */
    suspend fun getReportById(id: String): Result<Report?> = try {
        val document = db.collection(COLLECTION_REPORTS)
            .document(id)
            .get()
            .await()

        val report = document.toObject(Report::class.java)
        Log.d(TAG, "getReportById: Retrieved report $id")
        Result.success(report)
    } catch (e: Exception) {
        Log.e(TAG, "getReportById: Error", e)
        Result.failure(e)
    }

    /**
     * Update report status (resolve/unresolve)
     */
    suspend fun updateReportStatus(
        reportId: String,
        status: String,
        resolvedBy: String?,
        resolvedByName: String?
    ): Result<Unit> = try {
        val updates = mutableMapOf<String, Any?>(
            "status" to status
        )

        if (status == "Resolved") {
            updates["resolvedBy"] = resolvedBy
            updates["resolvedByName"] = resolvedByName
            updates["resolvedAt"] = com.google.firebase.firestore.FieldValue.serverTimestamp()
        } else {
            updates["resolvedBy"] = null
            updates["resolvedByName"] = null
            updates["resolvedAt"] = null
        }

        db.collection(COLLECTION_REPORTS)
            .document(reportId)
            .update(updates)
            .await()

        Log.d(TAG, "updateReportStatus: Report $reportId status updated to $status")
        Result.success(Unit)
    } catch (e: Exception) {
        Log.e(TAG, "updateReportStatus: Error", e)
        Result.failure(e)
    }

    /**
     * Get unresolved reports count
     */
    suspend fun getUnresolvedReportsCount(): Result<Int> = try {
        val snapshot = db.collection(COLLECTION_REPORTS)
            .whereEqualTo("status", "Unresolved")
            .get()
            .await()

        val count = snapshot.size()
        Log.d(TAG, "getUnresolvedReportsCount: $count unresolved reports")
        Result.success(count)
    } catch (e: Exception) {
        Log.e(TAG, "getUnresolvedReportsCount: Error", e)
        Result.failure(e)
    }
}
