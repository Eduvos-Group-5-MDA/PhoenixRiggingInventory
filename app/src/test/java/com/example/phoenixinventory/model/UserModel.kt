package com.example.myapp.model

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapp.network.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject

class UserModel(private val context: Context) {

//UserModel.kt
    companion object {
        private const val BASE_URL = "http://pheonixrigging.infinityfree.me/general.php?TABLE=Users"
    }

    // READ all users (GET) √
    fun getAllUsers(callback: ResultCallback<List<User>>) {
        val url = "$BASE_URL&CRUD=READ"
        val req = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
                val users = mutableListOf<User>()	//Creates list of User instances
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val user = User(		//Builds User instances with received DB data.

                        userId = obj.optInt("User_ID"),
			name = obj.optString("Name"),
			surname = obj.optString("Surname"),
                        email = obj.optString("Email"),
			phoneNum = obj.optString("Phone_number"),
			ZAID= obj.optString("ID_number"),
                        role = obj.optString("Role")
			driversLicense = obj.optBoolean("Drivers_license"), //obj.optBoolean
			password = obj.optString("Password")

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

    // READ single user by id (GET) √
    fun getUserById(userId: Int, callback: ResultCallback<User>) {
        val url = "$BASE_URL&CRUD=READ"
	val payload = JSONObject().apply {
            put("User_ID", userId)
        }
        val req = JsonObjectRequest(Request.Method.GET, url, payload,
            { obj: JSONObject ->
                val user = User(		//Builds the User instance with received DB data
                        userId = obj.optInt("User_ID"),
			name = obj.optString("Name"),
			surname = obj.optString("Surname"),
                        email = obj.optString("Email"),
			phoneNum = obj.optString("Phone_number"),
			ZAID = obj.optString("ID_number"),
                        role = obj.optString("Role")
			driversLicense = obj.optBoolean("Drivers_license"),
			password = obj.optString("Password")
                )
                callback.onSuccess(user) //successfully receives something from the website; whether the data is correct or not
            },
            { err ->
                callback.onError(err.message ?: "Network error")
            }
        )
        VolleySingleton.addToQueue(context, req)
    }

    // CREATE user (POST) √
    fun createUser(user: User, callback: ResultCallback<User>) {
        val url = "$BASE_URL&CRUD=CREATE"
        val payload = JSONObject().apply { //Creating JSON object to send to PHP

		put("Name", user.name)
		put("Surname", user.surname)
		put("Email", user.email)
		put("Phone_number",user.phoneNum)
		put("ID_number", user.ZAID)
		put("Role", user.role)
		//put("Company", user.company)
		put("Drivers_license", user.driversLicense)
		put("Password", user.password) //no comma trailing between statements in kotlin

        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,
            { obj: JSONObject ->
                val created = User(

                        userId = obj.optInt("User_ID"),
			name = obj.optString("Name"),
			surname = obj.optString("Surname"),
                        email = obj.optString("Email"),
			phoneNum = obj.optString("Phone_number"),
			ZAID = obj.optString("ID_number"),
                        role = obj.optString("Role")
			//company = obj.optString("Company")
			driversLicense = obj.optBoolean("Drivers_license"),
			password = obj.optString("Password")

			//Acknowledge. If not received; error

                )
                callback.onSuccess(created)
            },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }

    // UPDATE single user by id (POST) √
    fun updateUser(user: User, callback: SimpleCallback) {
        val url = "$BASE_URL&CRUD=UPDATE"
        val payload = JSONObject().apply {//Creating JSON object to send to PHP

		put("Name", user.name)
		put("Surname", user.surname)
		put("Email", user.email)
		put("Phone_number",user.phoneNum)
		put("ID_number", user.ZAID)
		put("Role", user.role)
		//put("Company", user.company)
		put("Drivers_license", user.driversLicense)
		put("Password", user.password)

        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,	//Acknowledge. If not received; error
            { _ -> callback.onSuccess() },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }

    // DELETE single user by id (POST) √
    fun deleteUser(userId: Int, callback: SimpleCallback) {
        val url = "$BASE_URL&CRUD=DELETE"
        val payload = JSONObject().apply {
            put("User_ID", userId)
        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,
            { _ -> callback.onSuccess() },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }
}
