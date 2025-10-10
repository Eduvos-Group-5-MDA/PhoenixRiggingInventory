package com.example.myapp.model

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapp.network.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject

class UserModel(private val context: Context) {

    companion object {
        private const val BASE_URL = "" //** URL NEEDED
    }

    // READ all users (GET)
    fun getAllUsers(callback: ResultCallback<List<User>>) {
        val url = "$BASE_URL?CRUD='READ'"
        val req = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
                val users = mutableListOf<User>()	//Creates list of User instances
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val user = User(		//Builds User instances with received DB data.
                        userId = obj.optInt("id", 0),
                        name = obj.optString("name"),
                        email = obj.optString("email"),
                        role = obj.optString("role", null)
                    )
                    users.add(user)
                }
                callback.onSuccess(users)
            },
            { err ->
                callback.onError(err.message ?: "Network error")
            }
        )
        VolleySingleton.addToQueue(context, req)
    }

    // READ single user by id (GET)
    fun getUserById(userId: Int, callback: ResultCallback<User>) {
        val url = "$BASE_URL?CRUD='READ'"
	val payload = JSONObject().apply {
            put("user_id", userId)
        }
        val req = JsonObjectRequest(Request.Method.GET, url, payload,
            { obj: JSONObject ->
                val user = User(		//Builds the User instance with received DB data
                    userId = obj.optInt("id", 0),
                    name = obj.optString("name"),
                    email = obj.optString("email"),
                    role = obj.optString("role", null)
                )
                callback.onSuccess(user)
            },
            { err ->
                callback.onError(err.message ?: "Network error")
            }
        )
        VolleySingleton.addToQueue(context, req)
    }

    // CREATE user (POST)
    fun createUser(user: User, callback: ResultCallback<User>) {
        val url = "$BASE_URL?CRUD='CREATE'"
        val payload = JSONObject().apply { //Creating JSON object to send to PHP
            put("user_name", user.name) //note formatting
            put("email", user.email)
            if (user.role != null) put("role", user.role)
        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,
            { resp: JSONObject ->
                val created = User(
                    userId = resp.optInt("id", 0),
                    name = resp.optString("name"),
                    email = resp.optString("email"),
                    role = resp.optString("role", null)		//Acknowledge. If not received; error
                )
                callback.onSuccess(created)
            },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }

    // UPDATE single user by id (POST)
    fun updateUser(user: User, callback: SimpleCallback) {
        val url = "$BASE_URL?CRUD='UPDATE'"
        val payload = JSONObject().apply {	//Creating JSON object to send to PHP
            put("user_id", user.userId) //note formatting
            put("name", user.name)
            put("email", user.email)
            if (user.role != null) put("role", user.role)
        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,	//Acknowledge. If not received; error
            { _ -> callback.onSuccess() },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }

    // DELETE single user by id (POST)
    fun deleteUser(userId: Int, callback: SimpleCallback) {
        val url = "$BASE_URL?CRUD='DELETE'"
        val payload = JSONObject().apply {
            put("user_id", userId)
        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,
            { _ -> callback.onSuccess() },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }
}
