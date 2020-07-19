package ru.cerbe.result

class ResultError(override val cause: Throwable?, private val suppliedMessage: String): RuntimeException() {
    override fun toString(): String {
        return suppliedMessage + "\nOriginal exception: " + super.toString()
    }
}

class MaybeNullable(passedCause: Throwable?): RuntimeException() {
    override val cause: Throwable = passedCause ?: NullPointerException()
}