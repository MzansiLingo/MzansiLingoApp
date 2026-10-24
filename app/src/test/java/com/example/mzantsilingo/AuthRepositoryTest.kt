package com.example.mzantsilingo

import com.example.mzantsilingo.data.model.api.ApiService
import com.example.mzantsilingo.data.model.repository.AuthRepository
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AuthRepositoryTest {

    private lateinit var server: MockWebServer
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val api = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
        repository = AuthRepository(api)
    }

    @After
    fun tearDown() = server.shutdown()

    @Test
    fun login_success_returnsUser() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"message":"Login successful.","user":{"id":7,"fullName":"Thandi","email":"t@t.com","username":"thandi"}}"""
            )
        )
        val result = repository.login("t@t.com", "Password123")
        assertTrue(result.isSuccess)
        assertEquals(7, result.getOrNull()?.id)
    }

    @Test
    fun login_wrongPassword_returnsServerMessage() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(401).setBody("""{"message":"Invalid email or password."}""")
        )
        val result = repository.login("t@t.com", "bad")
        assertTrue(result.isFailure)
        assertEquals("Invalid email or password.", result.exceptionOrNull()?.message)
    }

    @Test
    fun register_success_returnsUserWithId() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"message":"Account created successfully.","user":{"id":3,"fullName":"A","email":"a@a.com","username":"a"}}"""
            )
        )
        val result = repository.register("A", "a@a.com", "a", "Password123")
        assertEquals(3, result.getOrNull()?.id)
    }

    @Test
    fun register_duplicateEmail_returnsConflictMessage() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(409).setBody("""{"message":"An account with this email already exists."}""")
        )
        val result = repository.register("A", "a@a.com", "a", "Password123")
        assertEquals("An account with this email already exists.", result.exceptionOrNull()?.message)
    }

    @Test
    fun googleLogin_success_returnsUser() = runBlocking {
        server.enqueue(
            MockResponse().setResponseCode(200).setBody(
                """{"message":"Login successful.","user":{"id":9,"fullName":"G","email":"g@g.com","username":"g"}}"""
            )
        )
        val result = repository.googleLogin("fake-id-token")
        assertEquals(9, result.getOrNull()?.id)
    }
}