package com.kkostrubiec.premiumestate.data.repository

import com.kkostrubiec.premiumestate.data.api.PropertyApiService
import com.kkostrubiec.premiumestate.data.model.Property
import com.kkostrubiec.premiumestate.data.model.PropertyListResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepository @Inject constructor(
    private val apiService: PropertyApiService
) {

    suspend fun getProperties(): Result<PropertyListResponse> {
        return try {
            val response = apiService.getProperties()
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPropertyDetails(listingId: Int): Result<Property> {
        return try {
            val response = apiService.getPropertyDetails(listingId)
            if (response.isSuccessful) {
                response.body()?.let {
                    Result.success(it)
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
