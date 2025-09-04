package com.kkostrubiec.premiumestate.data.model

import com.google.gson.annotations.SerializedName

data class PropertyListResponse(
    @SerializedName("items")
    val items: List<Property>,
    @SerializedName("totalCount")
    val totalCount: Int
)
