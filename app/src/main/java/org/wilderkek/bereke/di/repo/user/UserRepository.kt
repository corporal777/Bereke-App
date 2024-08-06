package org.wilderkek.bereke.di.repo.user

import android.graphics.Bitmap
import io.reactivex.Maybe
import io.reactivex.Single
import org.wilderkek.bereke.model.response.ImageModel
import org.wilderkek.bereke.model.response.UserModel

interface UserRepository {
    fun getCurrentUser() : Maybe<UserModel>
    fun getUserById(id : String) : Maybe<UserModel>
    fun getUsersList() : Maybe<List<UserModel>>
    fun changeUserImage(photo: Bitmap?): Single<ImageModel>
    fun updateUser(data: Map<String, String?>): Maybe<UserModel>
}