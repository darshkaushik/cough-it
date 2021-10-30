package com.ibmhack2021.coughit.api

import retrofit2.http.Body
import retrofit2.http.POST

interface RestApi {

    // dummy api
    // todo : its models and responses are still need to be defined
    @POST("prediction")
    suspend fun getPrediction()
}