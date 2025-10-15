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


/* package com.example.myapp.controller

import android.content.Context
import com.example.myapp.model.Asset
import com.example.myapp.model.AssetModel
import com.example.myapp.model.ResultCallback
import com.example.myapp.model.SimpleCallback

class AssetController(context: Context) {

    private val model = AssetModel(context)

    fun fetchAllAssets(callback: ResultCallback<List<Asset>>) {
        model.getAllAssets(callback)
    }

    fun fetchAssetById(assetId: Int, callback: ResultCallback<Asset>) {
        if (assetId <= 0) {
            callback.onError("Invalid asset ID")
            return
        }
        model.getAssetById(assetId, callback)
    }

    fun createAsset(
        assetCode: String,
        name: String,
        categoryId: Int?,
        locationId: Int?,
        condition: String,
        status: String,
        requiresApproval: Boolean,
        isPermanent: Boolean,
        unitValue: Double?,
        callback: ResultCallback<Asset>
    ) {
        if (assetCode.isBlank() || name.isBlank()) {
            callback.onError("Asset code and name are required")
            return
        }
        val asset = Asset(
            assetId = 0,
            assetCode = assetCode,
            name = name,
            categoryId = categoryId,
            locationId = locationId,
            condition = condition,
            status = status,
            requiresApproval = requiresApproval,
            isPermanent = isPermanent,
            unitValue = unitValue ?: 0.0
        )
        model.createAsset(asset, callback)
    }

    fun updateAsset(asset: Asset, callback: SimpleCallback) {
        if (asset.assetId <= 0) {
            callback.onError("Invalid asset ID")
            return
        }
        if (asset.name.isBlank()) {
            callback.onError("Asset name cannot be empty")
            return
        }
        model.updateAsset(asset, callback)
    }

    fun deleteAsset(assetId: Int, callback: SimpleCallback) {
        if (assetId <= 0) {
            callback.onError("Invalid asset ID")
            return
        }
        model.deleteAsset(assetId, callback)
    }
} */

