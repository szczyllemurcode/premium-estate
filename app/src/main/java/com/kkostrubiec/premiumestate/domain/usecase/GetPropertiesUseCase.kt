package com.kkostrubiec.premiumestate.domain.usecase

import com.kkostrubiec.premiumestate.data.repository.PropertyRepository
import com.kkostrubiec.premiumestate.domain.model.Property
import javax.inject.Inject

/**
 * Use case for retrieving a list of properties.
 * Encapsulates the business logic for fetching properties from the repository.
 */
class GetPropertiesUseCase @Inject constructor(
    private val repository: PropertyRepository
) {
    suspend operator fun invoke(): Result<List<Property>> {
        return repository.getProperties()
    }
}
