package org.wilderkek.bereke.di.repo.auth

import io.reactivex.Completable
import io.reactivex.Maybe
import org.wilderkek.bereke.model.request.LoginRequestBody
import org.wilderkek.bereke.model.request.RegisterLoginModel
import org.wilderkek.bereke.model.request.TokenModel
import org.wilderkek.bereke.model.request.VerifyLoginModel
import org.wilderkek.bereke.model.response.UserModel

interface AuthRepository {
    fun getToken(deviceId : String) : Maybe<TokenModel>
    fun login(body: LoginRequestBody): Maybe<UserModel>
    fun logout(id: String): Completable
    fun register(body: Map<String, String>): Maybe<UserModel>
    fun checkUserPassword(password: String): Completable
    fun updateUserPassword(password: Map<String, String>): Completable
    fun updateUserLogin(login: Map<String, String>): Completable
    fun checkIfLoginExists(login: String): Completable
    fun sendFcmToken(): Completable
    fun sendVerifyCode(login : String): Completable
    fun checkVerifyCode(body : VerifyLoginModel): Completable
}