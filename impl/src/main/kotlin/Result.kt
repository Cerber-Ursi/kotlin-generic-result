package ru.cerbe.result

sealed class Result<Value, Error : Throwable> {
    abstract fun unwrap(): Value

    data class Ok<Value, Error : Throwable> internal constructor(private val inner: Value) : Result<Value, Error>() {
        override fun unwrap(): Value = inner
    }

    data class Err<Value, Error : Throwable> internal constructor(private val inner: Error) : Result<Value, Error>() {
        override fun unwrap(): Value = throw inner
    }
}

fun <Value> ofNullable(inner: Value?): Result<Value, NullPointerException> =
    if (inner != null) {
        Result.Ok(inner)
    } else {
        Result.Err(NullPointerException())
    }
