//Error handling

package com.example.myapp.model

interface ResultCallback<T> {
    fun onSuccess(result: T)
    fun onError(error: String)
}

interface SimpleCallback {
    fun onSuccess()
    fun onError(error: String)
}
