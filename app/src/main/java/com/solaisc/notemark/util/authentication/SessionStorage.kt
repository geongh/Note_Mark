package com.solaisc.notemark.util.authentication

interface SessionStorage {

    suspend fun get(): AuthInfo?

    suspend fun set(info: AuthInfo?)
}