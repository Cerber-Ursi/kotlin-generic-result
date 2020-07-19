package ru.cerbe.result

class ResultError(message: String, cause: Throwable?) : RuntimeException(message, cause)

class UnexpectedValueError(value: Any) : RuntimeException("Expected Result.Error, got: $value")

class MaybeNullable(cause: Throwable?) : RuntimeException(cause ?: NullPointerException())