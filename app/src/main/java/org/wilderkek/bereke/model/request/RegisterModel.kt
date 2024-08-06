package org.wilderkek.bereke.model.request

import com.google.gson.annotations.SerializedName
import org.wilderkek.bereke.model.response.ContactInformation

data class RegisterRequestBody(
    @SerializedName("firstName")
    val firstName : String,
    @SerializedName("lastName")
    val lastName : String,
    val login : String,
    val password : String,
    val image : String = "",
    val contactInformation: ContactInformation
)
