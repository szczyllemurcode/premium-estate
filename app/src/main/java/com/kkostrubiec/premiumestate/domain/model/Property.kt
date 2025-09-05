package com.kkostrubiec.premiumestate.domain.model

/**
 * Domain model for Property - represents property data in the business logic layer.
 * This is separate from the API response model to maintain clean architecture boundaries.
 */
data class Property(
    val id: Int,
    val bedrooms: Int?,
    val city: String,
    val area: Double,
    val imageUrl: String?,
    val price: Double,
    val professional: String,
    val propertyType: String,
    val offerType: OfferType,
    val rooms: Int?
)

/**
 * Enum representing the offer type instead of using raw integers
 */
enum class OfferType(val value: Int) {
    SALE(1),
    RENT(2),
    SOLD(3),
    RENTED(4),
    UNKNOWN(0);

    companion object {
        fun fromValue(value: Int): OfferType {
            return entries.find { it.value == value } ?: UNKNOWN
        }
    }
}
