package org.wilderkek.bereke.di.repo.auth

import io.reactivex.Completable
import io.reactivex.Maybe
import org.wilderkek.bereke.api.ApiService
import org.wilderkek.bereke.api.FireBaseHelper
import org.wilderkek.bereke.di.data.AppData
import org.wilderkek.bereke.model.request.LoginRequestBody
import org.wilderkek.bereke.model.request.RegisterLoginModel
import org.wilderkek.bereke.model.request.TokenModel
import org.wilderkek.bereke.model.request.VerifyLoginModel
import org.wilderkek.bereke.model.response.UserModel

class AuthRepositoryImpl(val appData: AppData, val apiService: ApiService) : AuthRepository {

    override fun getToken(deviceId: String): Maybe<TokenModel> {
        return apiService.getToken(deviceId).doOnSuccess { appData.saveToken(it.token) }
    }

    override fun login(body: LoginRequestBody): Maybe<UserModel> {
        return apiService.login(body).doOnSuccess { appData.login(it) }
    }

    override fun logout(id: String): Completable {
        return apiService.logout(id)
    }

    override fun register(body: Map<String, String>): Maybe<UserModel> {
        return apiService.register(body).doOnSuccess { appData.login(it) }
    }

    override fun checkUserPassword(password: String): Completable {
        return apiService.checkUserPassword(appData.getUserId(), password)
    }

    override fun updateUserPassword(password: Map<String, String>): Completable {
        return apiService.updateUserPassword(appData.getUserId(), password)
    }

    override fun updateUserLogin(login: Map<String, String>): Completable {
        return apiService.updateUserLogin(appData.getUserId(), login)
    }

    override fun checkIfLoginExists(login: String): Completable {
        return apiService.checkLoginExists(login)
    }

    override fun sendFcmToken(): Completable {
        return FireBaseHelper.getFcmToken()
            .flatMapCompletable { token ->
                apiService.sendFcmToken(
                    mapOf(
                        "deviceId" to appData.getDeviceUID(),
                        "userId" to appData.getUserId(),
                        "token" to token
                    )
                )
            }
    }

    override fun sendVerifyCode(login: String): Completable {
        return apiService.sendVerifyCode(RegisterLoginModel(login, appData.getDeviceUID()))
    }

    override fun checkVerifyCode(body: VerifyLoginModel): Completable {
        return apiService.checkVerifyCode(body)
    }
}



