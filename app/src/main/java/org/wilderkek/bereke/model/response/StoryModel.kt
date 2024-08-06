package org.wilderkek.bereke.model.response

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class StoriesModel(
    val title: String,
    val logo: String,
    val stories: List<StoryModel>
) : Parcelable


@Parcelize
data class StoryModel(
    val title: String?,
    val message: String,
    val image: String,
    val phone: StoryPhoneModel,
    val date: String
) : Parcelable

@Parcelize
data class StoryPhoneModel(
    val value: String,
    val type: String
): Parcelable