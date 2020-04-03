package ru.skillbranch.kotlinexample

import androidx.annotation.VisibleForTesting
import java.lang.IllegalArgumentException
import java.lang.StringBuilder
import java.math.BigInteger
import java.security.MessageDigest
import java.security.SecureRandom

class User private constructor(
    private var firstName: String,
    private var lastName: String?,
    email: String? = null,
    rawPhone: String? = null,
    meta: Map<String, Any>? = null,
    salt: String? = null
) {
    val userInfo: String

    private val fullName: String
        get() = listOfNotNull(firstName, lastName)
            .joinToString(" ")
            .capitalize()

    private val initials: String
        get() = listOfNotNull(firstName, lastName)
            .map { it.first().toUpperCase() }
            .joinToString(" ")

    private var phone: String? = null
        set(value) {
            field = value?.normalizePhone()
        }

    private var _login: String? = null

    var login: String
        set(value) {
            _login = value.toLowerCase()
        }
        get() = _login!!

    private var _salt: String? = null

    @VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
    lateinit var passwordHash: String

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    var accessCode: String? = null

    fun generateAndSendAccessCode(phone: String) {
        accessCode = generateAccessCode().also {
            passwordHash = encrypt(it)
            sendAccessCodeToUser(phone, it)
        }
    }

    // for email
    constructor(
        firstName: String,
        lastName: String?,
        email: String,
        password: String
    ) : this(firstName, lastName, email = email, meta = mapOf("auth" to "password")) {
        println("Secondary mail constructor")
        passwordHash = encrypt(password)
    }

    // for phone
    constructor(
        firstName: String,
        lastName: String?,
        rawPhone: String
    ) : this(firstName, lastName, rawPhone = rawPhone, meta = mapOf("auth" to "sms")) {
        println("Secondary phone constructor")
        generateAndSendAccessCode(rawPhone)
    }

    // for csv
    constructor(
        firstName: String,
        lastName: String?,
        email: String?,
        rawPhone: String?,
        salt: String,
        hash: String
    ) : this(firstName, lastName, email, rawPhone = rawPhone, meta = mapOf("src" to "csv"), salt = salt) {
        passwordHash = hash
    }

    private fun generateAccessCode(): String {
        val possible = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"

        return StringBuilder().apply {
            repeat(6) {
                (possible.indices).random().also { index ->
                    append(possible[index])
                }
            }
        }.toString()
    }

    init {
        println("First init block, primary constructor was called")

        check(firstName.isNotBlank()) { "First name must not be blank" }
        check(!email.isNullOrBlank() || !rawPhone.isNullOrBlank()) { "Email or phone must not be blank" }

        phone = rawPhone
        login = email ?: phone!!
        _salt = salt ?: ByteArray(16).also { SecureRandom().nextBytes(it) }.toString()

        userInfo = """
            firstName: $firstName
            lastName: $lastName
            login: $login
            fullName: $fullName
            initials: $initials
            email: $email
            phone: $phone
            meta: $meta
        """.trimIndent()
    }

    fun checkPassword(pass: String) = encrypt(pass) == passwordHash

    fun changePassword(oldPass: String, newPass: String) {
        if (checkPassword(oldPass)) passwordHash = encrypt(newPass)
        else throw IllegalAccessException("The entered password does not match the current password")
    }

    private fun encrypt(password: String) = _salt.plus(password).md5() // good

    private fun String.md5(): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(toByteArray()) // 16 bytes
        val hexString = BigInteger(1, digest).toString(16)
        return hexString.padStart(length = 32, padChar = '0')
    }

    private fun sendAccessCodeToUser(phone: String, code: String) {
        println(".... sending access code: $code on $phone")
    }

    companion object Factory {
        fun makeUser(
            fullName: String,
            email: String? = null,
            password: String? = null,
            phone: String? = null
        ): User {
            val (firstName, lastName) = fullName.fullNameToPair()

            return when {
                !phone.isNullOrBlank() -> {
                    if (!isValidPhone(
                            phone
                        )
                    ) throw IllegalArgumentException("Enter a valid phone number starting with a + and containing 11 digits")
                    else User(
                        firstName,
                        lastName,
                        phone
                    )
                }
                !email.isNullOrBlank() && !password.isNullOrBlank() -> User(
                    firstName,
                    lastName,
                    email,
                    password
                )
                else -> throw IllegalArgumentException("Email or phone must not be null or blank")
            }
        }

        fun makeUser(csv: List<String>): User {
            check(csv.size == 5) { "Invalid csv record. Must be 5 fields or missing semicolon " +
                    "at the end: $csv" }
            val (firstName, lastName) = csv[0].fullNameToPair()
            val email = csv[1].takeIf(String::isNotEmpty)
            val (salt, hash) = csv[2].toSaltHashPair()
            val rawPhone = csv[3].takeIf(String::isNotEmpty)
            return User(firstName, lastName, email, rawPhone, salt, hash)
        }

        private fun String.fullNameToPair(): Pair<String, String?> {
            return split(" ")
                .filter { it.isNotBlank() }
                .run {
                    when (size) {
                        1 -> first() to null
                        2 -> first() to last()
                        else -> throw IllegalArgumentException(
                            "Fullname must contain only first name " +
                                    "and last name , current split result $this"
                        )
                    }
                }
        }

        private fun String.toSaltHashPair(): Pair<String, String> {
            split(":")
                .filter { it.isNotBlank() }
                .run {
                    if (size == 2) return first() to last()
                    else throw IllegalArgumentException("Invalid salt:hash string: $this")
                }
        }

        fun isValidPhone(phone: String): Boolean {
            val normalizedPhone = phone.replace(" ", "")
            val validRegex = """^\+\d((\d{3})|(\(\d{3}\)))\d{3}[-]?\d{2}[-]?\d{2}$""".toRegex()
            return validRegex.containsMatchIn(normalizedPhone.trim())
        }

    }

}

fun String.normalizePhone() = replace("""[^+\d]""".toRegex(), "")