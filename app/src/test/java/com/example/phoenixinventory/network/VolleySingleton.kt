package com.example.myapp.network

import android.content.Context
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley

object VolleySingleton {
    @Volatile private var instance: RequestQueue? = null

    fun getQueue(context: Context): RequestQueue = instance ?: synchronized(this) {
            instance ?: Volley.newRequestQueue(context.applicationContext).also { instance = it }
        }

    fun <T> addToQueue(context: Context, req: Request<T>) {
        getQueue(context).add(req)
    }
}
