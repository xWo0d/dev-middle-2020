package ru.skillbranch.kotlinexemple

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(fullName, email = email, password = password)
            .also { user ->
                map[user.login]
                    ?.let { throw IllegalArgumentException("A user with this email already exists") }
                    ?: let { map[user.login] = user }
            }
    }

    fun registerUserByPhone(
        fullName: String,
        rawPhone: String
    ): User {
        return User.makeUser(fullName, phone = rawPhone)
            .also { user ->
                map[user.login]
                    ?.let { throw IllegalArgumentException("A user with this phone already exists") }
                    ?: let { map[user.login] = user }
            }
    }

    fun loginUser(login: String, password: String): String? {
        return map[login.trim()]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    // Реализуй метод requestAccessCode(login: String) : Unit, после выполнения данного метода у
    // пользователя с соответствующим логином должен быть сгенерирован новый код авторизации и
    // помещен в свойство accessCode, соответственно должен измениться и хеш пароля пользователя
    // (вызов метода loginUser должен отрабатывать корректно)
    fun requestAccessCode(login: String) {
        map[login.trim()]?.let { it.generateAndSendAccessCode(login) }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearMap() = map.clear()

}