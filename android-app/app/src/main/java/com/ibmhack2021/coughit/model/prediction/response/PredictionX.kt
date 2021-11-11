package com.ibmhack2021.coughit.model.prediction.response

data class PredictionX(
    val fields: List<String>,
    val id: String,
    val values: List<List<List<Double>>>
)