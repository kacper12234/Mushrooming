package com.kacper.mushrooming.utils

import com.kacper.mushrooming.api.ApiClient
import com.kacper.mushrooming.api.dto.ApiError
import retrofit2.Response
import java.io.IOException

object ErrorUtils {
    fun parseError(response: Response<*>): ApiError {
        val converter = ApiClient.retrofit()
                .responseBodyConverter<ApiError>(ApiError::class.java, arrayOfNulls(0))
        return try {
            converter.convert(response.errorBody()!!)
        } catch (e: IOException) {
            return ApiError()
        }
    }
}