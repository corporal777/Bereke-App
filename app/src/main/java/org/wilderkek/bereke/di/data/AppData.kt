package org.wilderkek.bereke.di.data

import android.content.Context
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import org.wilderkek.bereke.model.response.UserModel
import java.util.*

class AppData(private val appPrefs: AppPrefs, private val context: Context) {

    var token: String? = appPrefs.userToken
        set(value) {
            val changed = field != value
            if (changed) {
                if (value.isNullOrEmpty()) {
                    field = null
                    appPrefs.userToken = null
                } else {
                    field = value
                    appPrefs.userToken = value
                }
            }
        }

    var userId: Int? = appPrefs.userId
        set(value) {
            val changed = field != value
            if (changed) {
                if (value == null || value == -1) {
                    field = -1
                    appPrefs.userId = -1
                } else {
                    field = value
                    appPrefs.userId = value
                }
            }
        }

    private var userTown: String = appPrefs.userTown
        set(value) {
            val changed = field != value
            if (changed) {
                if (value.isNullOrBlank()) {
                    field = ""
                    appPrefs.userTown = ""
                } else {
                    field = value
                    appPrefs.userTown = value
                }
            }
        }

    private var deviceId: String? = appPrefs.uniqueDeviceId
        set(value) {
            if (field.isNullOrEmpty()) {
                field = value
                appPrefs.uniqueDeviceId = value
            }
        }


    private var user: UserModel? = null
    val userChangeSubject = BehaviorSubject.createDefault(user.asOptional())
    val userIdChangeSubject = BehaviorSubject.createDefault(userId.asOptional())
    val messageSubject = PublishSubject.create<String>()

    fun sendMessageSubject(message: Any) {
        if (message is String) messageSubject.onNext(message)
        else if (message is Int) messageSubject.onNext(context.getString(message))
    }

    fun saveToken(token: String) {
        val changed = this.token != token
        if (changed) this.token = token
    }

    fun getDeviceUID(): String {
        if (deviceId.isNullOrEmpty()) {
            deviceId = UUID.randomUUID().toString().replace("-", "")
        }
        return deviceId ?: ""
    }

    fun login(user: UserModel) {
        this.userId = user.id.toInt()
        this.user = user
        userIdChangeSubject.onNext(userId.asOptional())
        userChangeSubject.onNext(user.asOptional())
    }

    fun logOut() {
        this.userId = -1
        this.user = null
        userIdChangeSubject.onNext(userId.asOptional())
        userChangeSubject.onNext(user.asOptional())
    }


    fun getUser(): UserModel? = this.user
//        this.user ?: UserNew(
//            "76002",
//            "Mads",
//            "Mikkelsen",
//            "",
//            null,
//            ContactInformation("+79267806176", "podxvat@gmail.com"),
//            UserNotesModel(null, null)
//        )


    fun updateUser(newUser: UserModel) {
        val changed = this.user != newUser
        if (changed) {
            this.user = newUser
            userChangeSubject.onNext(user.asOptional())
        }
    }

    fun updateUserField(newUser: UserModel.() -> Unit) {
        userChangeSubject.onNext(this.user?.apply(newUser).asOptional())
    }

    fun initUserTown(town: String) {
        this.userTown = town
    }

    fun getUserId(): String = this.userId.toString()
    fun isLoggedOut() = !isUserAuthorized() && user == null

    fun isUserAuthorized() = (userId != null && userId != -1)


    fun getUserTown() = this.userTown

}