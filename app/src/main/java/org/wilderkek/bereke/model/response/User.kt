package org.wilderkek.bereke.model.response

import com.google.gson.annotations.SerializedName


data class UserModel(
    val id: String,
    val firstName: String,
    val lastName: String,
    var login: String,
    var image: String?,
    @SerializedName("contactsInformation")
    val contacts: ContactInformation,
    val userNotes: UserNotesModel?
) {
    val nameLastName: String
        get() = "$firstName $lastName"
    val publicId: String
        get() = "id$id"
    val contactEmail: String
        get() = contacts.email ?: ""
    val contactPhone: String
        get() = contacts.phone ?: ""
}

data class ContactInformation(
    val phone: String?,
    val email: String?
)

data class UserNotesModel(
    val activeNotes: String,
    val pendingNotes: String
)