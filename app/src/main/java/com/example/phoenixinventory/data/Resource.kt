package com.example.phoenixinventory.data

/**
 * A generic wrapper class to handle loading states, success, and error states
 * for asynchronous operations.
 */
sealed class Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>()
    data class Error(val message: String, val exception: Throwable? = null) : Resource<Nothing>()
    object Loading : Resource<Nothing>()

    val isLoading: Boolean
        get() = this is Loading

    val isSuccess: Boolean
        get() = this is Success

    val isError: Boolean
        get() = this is Error

    fun getOrNull(): T? = when (this) {
        is Success -> data
        else -> null
    }

    // Apply the fix here
    fun getOrDefault(default: @UnsafeVariance T): T = when (this) {
        is Success -> data
        else -> default
    }

    fun getOrElse(block: (Error) -> @UnsafeVariance T): T = when (this) {
        is Success -> data
        is Error -> block(this)
        is Loading -> throw IllegalStateException("Cannot get value while loading")
    }
}

/**
 * Extension function to convert a Result to a Resource
 */
fun <T> Result<T>.toResource(): Resource<T> = when {
    isSuccess -> Resource.Success(getOrThrow())
    else -> Resource.Error(
        message = exceptionOrNull()?.message ?: "Unknown error",
        exception = exceptionOrNull()
    )
}
