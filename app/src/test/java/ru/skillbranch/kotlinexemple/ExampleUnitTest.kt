package ru.skillbranch.kotlinexemple

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun register_user() {
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
}
