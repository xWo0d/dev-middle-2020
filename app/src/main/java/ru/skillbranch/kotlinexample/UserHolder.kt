package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException

object UserHolder {
    private val map = mutableMapOf<String, User>()

    fun registerUser(
        fullName: String,
        email: String,
        password: String
    ): User {
        return User.makeUser(
            fullName,
            email = email,
            password = password
        )
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
        return User.makeUser(
            fullName,
            phone = rawPhone
        )
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

    //Реализуй метод importUsers(list: List): List, в качестве аргумента принимает список строк где
    // разделителем полей является ";" данные перечислены в следующем порядке - Полное имя
    // пользователя; email; соль:хеш пароля; телефон (
    // Пример:
    // " John Doe ;JohnDoe@unknow.com;[B@7591083d:c6adb4becdc64e92857e1e2a0fd6af84;;"
    // ) метод должен вернуть коллекцию список User (Пример возвращаемого userInfo:
    //firstName: John
    //lastName: Doe
    //login: johndoe@unknow.com
    //fullName: John Doe
    //initials: J D
    //email: JohnDoe@unknow.com
    //phone: null
    //meta: {src=csv}
    //), при этом meta должно содержать "src" : "csv", если сзначение в csv строке пустое то
    // соответствующее свойство в объекте User должно быть null, обратите внимание что salt и hash
    // пароля в csv разделены ":" , после импорта пользователей вызов метода loginUser должен
    // отрабатывать корректно (достаточно по логину паролю)
    fun importUsers(list: List<String>): List<User> = list
        .map { it.split(";") }
        .map { it.map(String::trim) }
        .map { User.makeUser(it) }
        .map { user ->
            map[user.login]
                ?.let { throw IllegalArgumentException("A user with this login already exists") }
                ?: let { map[user.login] = user }
            user
        }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun clearHolder() = map.clear()

}