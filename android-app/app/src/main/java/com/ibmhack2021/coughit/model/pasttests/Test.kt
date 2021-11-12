package com.ibmhack2021.coughit.model.pasttests

data class Test(
    val _id: String,
    val _rev: String,
    val date: String,
    val documentType: String,
    val email: String,
    val prediction: String
)