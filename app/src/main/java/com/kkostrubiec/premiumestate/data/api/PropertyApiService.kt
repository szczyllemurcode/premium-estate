package com.kkostrubiec.premiumestate.data.api

import com.kkostrubiec.premiumestate.data.model.Property
import com.kkostrubiec.premiumestate.data.model.PropertyListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface PropertyApiService {

    @GET("listings.json")
    suspend fun getProperties(): Response<PropertyListResponse>

    @GET("listings/{listingId}.json")
    suspend fun getPropertyDetails(@Path("listingId") listingId: Int): Response<Property>
}
