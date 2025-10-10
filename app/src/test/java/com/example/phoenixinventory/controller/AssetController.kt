//VALIDATION MISSING
//UNFINISHED

package com.example.myapp.controller

import android.content.Context
import com.example.myapp.model.ResultCallback
import com.example.myapp.model.SimpleCallback
import com.example.myapp.model.Asset
import com.example.myapp.model.AssetModel

class AssetController(context: Context) {
    private val model = AssetModel(context)

    suspend fun fetchAllAssets(callback: ResultCallback<List<Asset>>) {
        // No coroutine usage inside model (Volley), so call directly.
        model._(callback)
    }

    //*
    fun createAsset(name: String, x:y, x:y?, callback: ResultCallback<Asset>) {
        // Example front-end validation
        if (name.isBlank() || x.isBlank()) {
            callback.onError("Name and email required")
            return
        }
        val asset = Asset(name = name, email = email, role = role)
        model._(asset, callback)
    }

    fun updateAsset(asset: Asset, callback: SimpleCallback) {
        if (asset.assetId <= 0) {
            callback.onError("Invalid user id")
            return
        }
        model.updateAsset(asset, callback)
    }

    fun deleteAsset(assetId: Int, callback: SimpleCallback) {
        if (assetId <= 0) {
            callback.onError("Invalid user id")
            return
        }
        model.deleteAsset(assetId, callback)
    }
}
