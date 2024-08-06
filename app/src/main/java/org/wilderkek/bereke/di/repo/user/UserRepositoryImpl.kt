package org.wilderkek.bereke.di.repo.user

import android.graphics.Bitmap
import io.reactivex.Maybe
import io.reactivex.Single
import org.wilderkek.bereke.util.toBodyPart
import org.wilderkek.bereke.api.ApiService
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.model.response.ImageModel
import org.wilderkek.bereke.model.response.UserModel

class UserRepositoryImpl(val appData: AppData, val apiService: ApiService) : UserRepository {

    override fun getCurrentUser(): Maybe<UserModel> {
        return if (appData.userId == null || appData.userId == -1) Maybe.empty()
        else apiService.getUserById(appData.getUserId())
            .doOnSuccess { appData.updateUser(it) }
    }

    override fun getUserById(id: String): Maybe<UserModel> = apiService.getUserById(id)

    override fun getUsersList(): Maybe<List<UserModel>> {
        return apiService.getUsersList()
    }

    override fun changeUserImage(photo: Bitmap?): Single<ImageModel> {
        return apiService.changeUserImage(
            appData.getUserId(),
            photo?.toBodyPart("file", "image.png")
        )
    }

    override fun updateUser(data: Map<String, String?>): Maybe<UserModel> {
        return apiService.updateUser(appData.getUserId(), data)
    }
}