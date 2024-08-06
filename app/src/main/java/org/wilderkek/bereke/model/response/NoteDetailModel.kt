package org.wilderkek.bereke.model.response

import com.google.gson.JsonElement

data class NoteDetailModel(
    val note: NoteModel,
    val creator : UserModel?,
    val additionalData: JsonElement?
)