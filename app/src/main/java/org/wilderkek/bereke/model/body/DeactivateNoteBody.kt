package org.wilderkek.bereke.model.body

data class DeactivateNoteBody(
    val deactivateBy: String,
    val noteId: String
)