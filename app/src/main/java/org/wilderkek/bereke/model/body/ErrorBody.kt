package org.otunjargych.tamtam.model.request

data class ErrorResponse(
    val message: String,
    val code: Int
){

    companion object{
        const val USER_ALREADY_EXISTS_ERROR = "User with this email already exists!"
        const val USER_ALREADY_CREATED_ERROR = "User already created!"
    }
}