package com.kkostrubiec.premiumestate.data.repository

import com.kkostrubiec.premiumestate.data.api.PropertyApiService
import com.kkostrubiec.premiumestate.data.mapper.PropertyMapper.toDomain
import com.kkostrubiec.premiumestate.domain.model.Property
import com.kkostrubiec.premiumestate.domain.repository.PropertyRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PropertyRepositoryImpl @Inject constructor(
    private val apiService: PropertyApiService
) : PropertyRepository {

    override suspend fun getProperties(): Result<List<Property>> {
        return try {
            val response = apiService.getProperties()
            if (response.isSuccessful) {
                response.body()?.let { propertyListResponse ->
                    Result.success(propertyListResponse.toDomain())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPropertyDetails(listingId: Int): Result<Property> {
        return try {
            val response = apiService.getPropertyDetails(listingId)
            if (response.isSuccessful) {
                response.body()?.let { dataProperty ->
                    Result.success(dataProperty.toDomain())
                } ?: Result.failure(Exception("Empty response body"))
            } else {
                Result.failure(Exception("HTTP ${response.code()}: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
