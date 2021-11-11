package com.ibmhack2021.coughit.model.login.request

data class LoginRequest(
    val email: String,
    val profile_url: String,
    val uid: String
)