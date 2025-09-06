package com.kkostrubiec.premiumestate.domain.usecase

import com.kkostrubiec.premiumestate.domain.repository.PropertyRepository
import com.kkostrubiec.premiumestate.domain.model.Property
import javax.inject.Inject

/**
 * Use case for retrieving property details by ID.
 * Encapsulates the business logic for fetching a specific property from the repository.
 */
class GetPropertyDetailsUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(propertyId: Int): Result<Property> {
        return repository.getPropertyDetails(propertyId)
    }
}
