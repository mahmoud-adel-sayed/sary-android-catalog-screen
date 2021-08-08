package com.sary.task.util

import android.app.Application
import com.sary.task.R
import java.io.IOException
import retrofit2.Response as RetrofitResponse

sealed class Response<out D> {
    data class Success<out T>(val data: T) : Response<T>()
    data class Error(val message: String?) : Response<Nothing>()
    class Empty<T> : Response<T>()

    companion object {
        @JvmStatic
        fun <T> create(response: RetrofitResponse<T>): Response<T> {
            return if (response.isSuccessful) {
                val data = response.body()
                if (data == null || response.code() == 204) {
                    Empty()
                } else {
                    Success(data = data)
                }
            } else {
                val message = response.errorBody()?.string()
                Error(message = if (message.isNullOrEmpty()) response.message() else message)
            }
        }

        @JvmStatic
        fun <T> create(app: Application, throwable: Throwable): Response<T> =
                Error(message = ErrorHandler.getMessage(app, throwable))
    }
}

object ErrorHandler {
    @JvmStatic
    fun getMessage(app: Application, throwable: Throwable): String {
        return if (throwable is IOException) {
            app.getString(R.string.err_no_internet_connection)
        } else {
            throwable.message ?: app.getString(R.string.error_unknown)
        }
    }
}