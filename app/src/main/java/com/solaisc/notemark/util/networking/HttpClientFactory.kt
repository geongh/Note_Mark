package com.solaisc.notemark.util.networking

import com.solaisc.notemark.BuildConfig
import com.solaisc.notemark.util.authentication.AuthInfo
import com.solaisc.notemark.util.authentication.SessionStorage
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(
    private val sessionStorage: SessionStorage
) {
    fun build(): HttpClient {
        return HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    json = Json {
                        ignoreUnknownKeys = true
                    }
                )
            }
            defaultRequest {
                contentType(ContentType.Application.Json)
                header("x-user-email", BuildConfig.EMAIL)
            }
            install(Auth) {
                bearer {
                    /*sendWithoutRequest {
                        it.url.encodedPath.contains("auth/login") || it.url.encodedPath.contains("auth/register")
                    }*/

                    loadTokens {
                        val info = sessionStorage.get()
                        BearerTokens(
                            accessToken = info?.accessToken ?: "",
                            refreshToken = info?.refreshToken ?: ""
                        )
                    }
                    refreshTokens {
                        val info = sessionStorage.get()

                        val response = client.post(
                            "https://notemark.pl-coding.com/api/auth/refresh"
                        ) {
                            contentType(ContentType.Application.Json)
                            setBody(
                                AccessTokenRequest(
                                    refreshToken = info?.refreshToken ?: ""
                                )
                            )
                            markAsRefreshTokenRequest()
                        }

                        if (response.status == HttpStatusCode.OK) {
                            val newTokens = response.body<AccessTokenResponse>()

                            val newAuthInfo = AuthInfo(
                                accessToken = newTokens.accessToken,
                                refreshToken = newTokens.refreshToken,
                                username = info?.username ?: ""
                            )

                            sessionStorage.set(newAuthInfo)

                            BearerTokens(
                                accessToken = newAuthInfo.accessToken,
                                refreshToken = newAuthInfo.refreshToken
                            )
                        } else {
                            BearerTokens(
                                accessToken = "",
                                refreshToken = ""
                            )
                        }
                    }
                }
            }
        }
    }
}