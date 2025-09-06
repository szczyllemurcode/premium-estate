package com.kkostrubiec.premiumestate.domain.repository

import com.kkostrubiec.premiumestate.domain.model.Property

/**
 * Repository interface for property data operations.
 * Defines the contract for accessing property data from various sources.
 */
interface PropertyRepository {

    /**
     * Retrieves a list of all available properties.
     * @return Result containing a list of properties or an error
     */
    suspend fun getProperties(): Result<List<Property>>

    /**
     * Retrieves detailed information for a specific property.
     * @param listingId The unique identifier of the property
     * @return Result containing the property details or an error
     */
    suspend fun getPropertyDetails(listingId: Int): Result<Property>
}
