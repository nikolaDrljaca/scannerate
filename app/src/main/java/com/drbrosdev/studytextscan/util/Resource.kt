package com.drbrosdev.studytextscan.util

sealed class Resource<T>(
    private val data: T? = null,
    val error: Throwable? = null
) {
    open operator fun invoke(): T? = data

    class Success<T>(data: T): Resource<T>(data)
    class Loading<T>(data: T? = null): Resource<T>(data)
    class Error<T>(throwable: Throwable, data: T? = null): Resource<T>(data, throwable)
}