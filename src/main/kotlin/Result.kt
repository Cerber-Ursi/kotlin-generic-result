package ru.cerbe.result

sealed class Result<Value, Error: Throwable> {
    data class Ok<Value>(val inner: Value): Result<Value, Throwable>()
    data class Err<Error: Throwable>(val inner: Error): Result<Any, Error>()
}