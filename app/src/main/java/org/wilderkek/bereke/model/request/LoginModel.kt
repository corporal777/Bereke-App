package org.wilderkek.bereke.model.request


data class LoginRequestBody(
    val login: String,
    val password: String
)

data class LoginResponse(
    val id: String,
    val token: String
)

data class TokenModel(
    val deviceId: String,
    val token: String
)

data class RegisterLoginModel(
    val login: String,
    val deviceId: String
)

data class VerifyLoginModel(
    val login: String,
    val code: String
)