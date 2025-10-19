package com.example.phoenixinventory.data.network

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

data class InventoryItemDto(
    val id: String,
    val name: String,
    val serialId: String,
    val description: String? = null,
    val condition: String,
    val status: String,
    val value: Double = 0.0,
    val permanentCheckout: Boolean = false,
    val permissionNeeded: Boolean = false,
    val driversLicenseNeeded: Boolean = false,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

data class UserDto(
    val id: String,
    val name: String,
    val email: String,
    val role: String,
    val phone: String? = null,
    val company: String? = null,
    @SerializedName("driverLicense") val driverLicense: Boolean = false,
    val employeeId: String? = null,
    val createdAt: String? = null
)

data class CheckoutRecordDto(
    val id: String,
    val itemId: String,
    val userId: String,
    val checkedOutAt: String? = null,
    val checkedInAt: String? = null,
    val notes: String? = null
)

data class CheckedOutItemDetailDto(
    val item: InventoryItemDto,
    val user: UserDto,
    val checkoutRecord: CheckoutRecordDto,
    val daysOut: Int
)

data class AuthResponseDto(
    val token: String,
    val user: UserDto
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: String,
    val phone: String? = null,
    val company: String? = null,
    val hasDriverLicense: Boolean = false,
    val employeeId: String? = null
)

data class ItemRequest(
    val name: String,
    val serialId: String,
    val description: String? = null,
    val condition: String,
    val status: String,
    val value: Double = 0.0,
    val permanentCheckout: Boolean = false,
    val permissionNeeded: Boolean = false,
    val driversLicenseNeeded: Boolean = false
)

data class CheckoutRequest(
    val itemId: String,
    val userId: String,
    val notes: String? = null
)

data class StatsResponse(
    val totalValue: Double = 0.0,
    val stolenLostDamagedValue: Double = 0.0,
    val stolenLostDamagedCount: Int = 0,
    val checkedOutCount: Int = 0
)

interface PhoenixApiService {
    // Authentication
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponseDto

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponseDto

    // Items
    @GET("items")
    suspend fun getItems(): List<InventoryItemDto>

    @GET("items/{id}")
    suspend fun getItem(@Path("id") id: String): InventoryItemDto

    @POST("items")
    suspend fun createItem(@Body request: ItemRequest): InventoryItemDto

    @PUT("items/{id}")
    suspend fun updateItem(@Path("id") id: String, @Body request: ItemRequest): InventoryItemDto

    @DELETE("items/{id}")
    suspend fun deleteItem(@Path("id") id: String)

    @GET("items/stats/summary")
    suspend fun getItemStats(): StatsResponse

    // Users
    @GET("users")
    suspend fun getUsers(): List<UserDto>

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): UserDto

    @PUT("users/{id}")
    suspend fun updateUser(@Path("id") id: String, @Body body: Map<String, @JvmSuppressWildcards Any?>): UserDto

    // Checkouts
    @GET("checkouts/checked-out-items")
    suspend fun getCheckedOutItems(): List<CheckedOutItemDetailDto>

    @GET("checkouts/overdue/{days}")
    suspend fun getItemsOutLongerThan(@Path("days") days: Int): List<CheckedOutItemDetailDto>

    @POST("checkouts/checkout")
    suspend fun checkoutItem(@Body request: CheckoutRequest): CheckoutRecordDto

    @POST("checkouts/checkin/{itemId}")
    suspend fun checkinItem(@Path("itemId") itemId: String)
}
