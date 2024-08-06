package org.wilderkek.bereke.model.body

data class NoteComplaintBody(
    val complaintId : String,
    val noteId : String,
    val message: String
)