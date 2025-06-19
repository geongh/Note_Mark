package com.solaisc.notemark.feature.auth.data

import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.EmptyResult
import com.solaisc.notemark.util.result.Result
import com.solaisc.notemark.util.result.asEmptyDataResult
import com.solaisc.notemark.feature.auth.domain.AuthRepository
import com.solaisc.notemark.util.authentication.AuthInfo
import com.solaisc.notemark.util.authentication.SessionStorage
import com.solaisc.notemark.util.networking.post
import io.ktor.client.HttpClient

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    private val sessionStorage: SessionStorage
): AuthRepository {
    override suspend fun login(email: String, password: String): EmptyResult<DataError.Network> {
        val result = httpClient.post<LoginRequest, LoginResponse>(
            route = "/api/auth/login",
            body = LoginRequest(
                email = email,
                password = password
            )
        )
        if(result is Result.Success) {
            sessionStorage.set(
                AuthInfo(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                    username = result.data.username
                )
            )
        }
        return result.asEmptyDataResult()
    }

    override suspend fun register(username: String, email: String, password: String): EmptyResult<DataError.Network> {
        return httpClient.post<RegisterRequest, Unit>(
            route = "/api/auth/register",
            body = RegisterRequest(
                username = username,
                email = email,
                password = password
            )
        )
    }
}