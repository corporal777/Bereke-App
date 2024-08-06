package org.wilderkek.bereke.api

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError

sealed class EventResponse {
    data class Changed(val snapshot: DataSnapshot) : EventResponse()
    data class Cancelled(val error: DatabaseError) : EventResponse()
}