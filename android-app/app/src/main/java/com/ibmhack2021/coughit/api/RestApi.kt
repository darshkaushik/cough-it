package com.ibmhack2021.coughit.api

import com.ibmhack2021.coughit.model.login.request.LoginRequest
import com.ibmhack2021.coughit.model.login.response.LoginResponse
import com.ibmhack2021.coughit.model.prediction.request.PredictionRequest
import com.ibmhack2021.coughit.model.prediction.response.Prediction
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface RestApi {


    @POST("prediction")
    suspend fun getPrediction(@Body predictionRequest: PredictionRequest) : Response<Prediction>

    @POST("auth/login")
    suspend fun loginToServer(@Body loginRequest: LoginRequest) : Response<LoginResponse>
}