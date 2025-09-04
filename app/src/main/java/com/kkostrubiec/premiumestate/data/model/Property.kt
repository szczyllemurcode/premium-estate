package com.kkostrubiec.premiumestate.data.model

import com.google.gson.annotations.SerializedName

data class Property(
    @SerializedName("id")
    val id: Int,
    @SerializedName("bedrooms")
    val bedrooms: Int? = null,
    @SerializedName("city")
    val city: String,
    @SerializedName("area")
    val area: Double,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("price")
    val price: Double,
    @SerializedName("professional")
    val professional: String,
    @SerializedName("propertyType")
    val propertyType: String,
    @SerializedName("offerType")
    val offerType: Int,
    @SerializedName("rooms")
    val rooms: Int? = null
)
