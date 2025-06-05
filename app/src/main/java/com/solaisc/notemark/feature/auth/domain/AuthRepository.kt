package com.solaisc.notemark.feature.auth.domain

import com.solaisc.notemark.util.result.DataError
import com.solaisc.notemark.util.result.EmptyResult

interface AuthRepository {

    suspend fun login(email: String, password: String): EmptyResult<DataError.Network>

    suspend fun register(username: String, email: String, password: String): EmptyResult<DataError.Network>
}