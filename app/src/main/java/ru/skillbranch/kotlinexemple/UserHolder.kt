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
        val loginKey = if (User.isValidPhone(login)) {
            login.normalizePhone()
        } else {
            login.trim()
        }
        return map[loginKey]?.run {
            if (checkPassword(password)) this.userInfo
            else null
        }
    }

    fun requestAccessCode(login: String) {
        val loginKey = if (User.isValidPhone(login)) {
            login.normalizePhone()
        } else {
            login.trim()
        }

        map[loginKey]?.let { it.generateAndSendAccessCode(login) }
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() = map.clear()

}