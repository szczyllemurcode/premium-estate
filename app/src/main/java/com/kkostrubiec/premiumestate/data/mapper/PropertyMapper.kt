package com.kkostrubiec.premiumestate.data.mapper

import com.kkostrubiec.premiumestate.data.model.Property as DataProperty
import com.kkostrubiec.premiumestate.data.model.PropertyListResponse as DataPropertyListResponse
import com.kkostrubiec.premiumestate.domain.model.Property as DomainProperty
import com.kkostrubiec.premiumestate.domain.model.OfferType

/**
 * Mapper to convert between data layer and domain layer models.
 * This maintains clean architecture boundaries by separating API models from business logic models.
 */
object PropertyMapper {

    /**
     * Converts data layer Property to domain layer Property
     */
    fun DataProperty.toDomain(): DomainProperty {
        return DomainProperty(
            id = this.id,
            bedrooms = this.bedrooms,
            city = this.city,
            area = this.area,
            imageUrl = this.url,
            price = this.price,
            professional = this.professional,
            propertyType = this.propertyType,
            offerType = OfferType.fromValue(this.offerType),
            rooms = this.rooms
        )
    }

    /**
     * Converts a list of data layer Properties to domain layer Properties
     */
    fun List<DataProperty>.toDomain(): List<DomainProperty> {
        return this.map { it.toDomain() }
    }

    /**
     * Converts PropertyListResponse to a list of domain Properties
     */
    fun DataPropertyListResponse.toDomain(): List<DomainProperty> {
        return this.items.toDomain()
    }
}
