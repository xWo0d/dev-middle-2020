package ru.skillbranch.kotlinexemple

import org.junit.Test

import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.rules.ExpectedException
import java.lang.IllegalArgumentException

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    val expectedException = ExpectedException.none()

    @Rule fun expectedRule() = expectedException

    @Before
    fun resetMap() = UserHolder.clearMap()

    @Test
    fun `register user with mail and password`() {
        val holder = UserHolder
        holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")
        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: john_doe@unknown.com
            fullName: John Doe
            initials: J D
            email: John_Doe@unknown.com
            phone: null
            meta: {auth=password}
        """.trimIndent()

        val failResult = holder.loginUser("John_Doe@unknown.com", "testPass")
        val succesResult = holder.loginUser("john_doe@unknown.com", "testPass")

        assertEquals(null, failResult)
        assertEquals(expectedInfo, succesResult)

    }

    @Test
    fun `throw exception when try to register by email and password user with existed login`() {
        val holder = UserHolder

        expectedRule().expect(IllegalArgumentException::class.java)
        expectedRule().expectMessage("A user with this email already exists")

        holder.registerUser("John Doe", "John_Doe@unknown.com", "testPass")
        holder.registerUser("Abraam Lincoln", "John_Doe@unknown.com", "testPass")
    }

    @Test
    fun `throw exception when try to register by phone user with existed login`() {
        val holder = UserHolder

        expectedRule().expect(IllegalArgumentException::class.java)
        expectedRule().expectMessage("A user with this phone already exists")

        holder.registerUserByPhone("John Doe", "+7 123 456 7890")
        holder.registerUserByPhone("Abraam Lincoln", "+7 123 456 7890")
    }

    @Test
    fun `register user with phone`() {
        val holder = UserHolder

        val phone = "+7 123 456 7891"
        val normalizedPhone = "+71234567891"

        val user = holder.registerUserByPhone("John Doe", phone)
        val pass = user.accessCode ?: ""

        val expectedInfo = """
            firstName: John
            lastName: Doe
            login: $normalizedPhone
            fullName: John Doe
            initials: J D
            email: null
            phone: $normalizedPhone
            meta: {auth=sms}
        """.trimIndent()

        val failResult = holder.loginUser(normalizedPhone, "wrongPass")
        val succesResult = holder.loginUser(normalizedPhone, pass)

        assertTrue(pass.length == 6)
        assertEquals(null, failResult)
        assertEquals(expectedInfo, succesResult)
    }

    @Test
    fun `throw IllegalArgumentException when register user with invalid number`() {
        val holder = UserHolder

        expectedRule().expect(IllegalArgumentException::class.java)
        expectedRule().expectMessage("Enter a valid phone number starting with a + and containing 11 digits")

        holder.registerUserByPhone("John Doe", "71234567890") // not start with "+"
        holder.registerUserByPhone("John Doe", "+7123456789") // 10 digits
        holder.registerUserByPhone("John Doe", "+7123456789a") // contains letters
    }

    @Test
    fun `passwordHash and accessCode should be changed on request access code`() {
        val holder = UserHolder
        val phone = "+71234567890"

        val user = holder.registerUserByPhone("John Doe", "+71234567890") // 1contains letters

        val oldAccessCode = user.accessCode
        val oldPasswordHash = user.passwordHash

        holder.requestAccessCode(phone)

        assertNotEquals(user.accessCode, oldAccessCode)
        assertNotEquals(user.passwordHash, oldPasswordHash)
        assertNotNull(holder.loginUser(phone, user.accessCode ?: ""))

    }
}
