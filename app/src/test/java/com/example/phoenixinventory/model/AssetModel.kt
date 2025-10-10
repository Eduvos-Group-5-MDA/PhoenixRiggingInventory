package com.example.myapp.model

import android.content.Context
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.example.myapp.network.VolleySingleton
import org.json.JSONArray
import org.json.JSONObject

class AssetModel(private val context: Context) {

    companion object {
        private const val BASE_URL = "" //** [www.infinityfree.com/assets.php] + ?CRUD=READ
    }

    fun getAllAssets(callback: ResultCallback<List<Asset>>) {
        val url = "$BASE_URL?CRUD=READ" //TABLE=Assets
        val req = JsonArrayRequest(Request.Method.GET, url, null,
            { response: JSONArray ->
                val assets = mutableListOf<Asset>()
                for (i in 0 until response.length()) {
                    val obj = response.getJSONObject(i)
                    val asset = Asset(
                        assetId = obj.optInt("id", 0),
                        name = obj.optString("name"),
                        status = obj.optString("status"),
                        location = obj.optString("location", null),
                        category = obj.optString("category", null)
                    )
                    assets.add(asset)
                }
                callback.onSuccess(assets)
            },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }

    fun createAsset(asset: Asset, callback: ResultCallback<Asset>) {
        val url = "$BASE_URL?CRUD=CREATE"
        val payload = JSONObject().apply {
            put("name", asset.name)
            put("status", asset.status)
            if (asset.location != null) put("location", asset.location)
            if (asset.category != null) put("category", asset.category)
        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,
            { resp ->
                val created = Asset(
                    assetId = resp.optInt("id", 0),
                    name = resp.optString("name"),
                    status = resp.optString("status"),
                    location = resp.optString("location", null),
                    category = resp.optString("category", null)
                )
                callback.onSuccess(created)
            },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }

    fun updateAsset(asset: Asset, callback: SimpleCallback) {
        val url = "$BASE_URL?CRUD=UPDATE"
        val payload = JSONObject().apply {
            put("id", asset.assetId)
            put("name", asset.name)
            put("status", asset.status)
            if (asset.location != null) put("location", asset.location)
            if (asset.category != null) put("category", asset.category)
        }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,
            { _ -> callback.onSuccess() },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }

    fun deleteAsset(assetId: Int, callback: SimpleCallback) {
        val url = "$BASE_URL?CRUD=DELETE"
        val payload = JSONObject().apply { put("id", assetId) }
        val req = JsonObjectRequest(Request.Method.POST, url, payload,
            { _ -> callback.onSuccess() },
            { err -> callback.onError(err.message ?: "Network error") }
        )
        VolleySingleton.addToQueue(context, req)
    }
}
