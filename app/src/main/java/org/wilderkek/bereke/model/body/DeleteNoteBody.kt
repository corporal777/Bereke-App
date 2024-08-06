package org.wilderkek.bereke.model.body

data class DeleteNoteBody(
    val deleteBy: String,
    val noteId: String
)