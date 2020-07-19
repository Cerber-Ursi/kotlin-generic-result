package ru.cerbe.result

sealed class Result<Value, Error : Throwable> {
    fun unwrap(): Value = expect("Tried to unwrap an Error value")
    abstract fun expect(message: String): Value
    abstract fun unwrapOr(value: () -> Value): Value
    abstract fun unwrapOrNull(): Value?

    fun unwrapErr(): Error = expectErr("Tried to unwrap an error from Ok value")
    abstract fun expectErr(message: String): Error

    abstract fun <NewValue> map(mapper: (Value) -> NewValue): Result<NewValue, Error>
    abstract fun <NewValue> maybeMap(mapper: (Value) -> NewValue?): Result<NewValue, MaybeNullable>
    abstract fun <NewError : Throwable> mapError(mapper: (Error) -> NewError): Result<Value, NewError>
    abstract fun <NewValue> andThen(mapper: (Value) -> Result<NewValue, Error>): Result<NewValue, Error>
    abstract fun <NewError : Throwable> orElse(value: (Error) -> Result<Value, NewError>): Result<Value, NewError>

    internal data class Ok<Value, Error : Throwable>(private val inner: Value) : Result<Value, Error>() {
        override fun expect(message: String): Value = inner
        override fun unwrapOr(value: () -> Value): Value = inner
        override fun unwrapOrNull(): Value? = inner
        override fun expectErr(message: String): Error = throw ResultError(message, UnexpectedValueError(inner as Any))
        override fun <NewValue> map(mapper: (Value) -> NewValue): Result<NewValue, Error> = Ok(mapper(inner))
        override fun <NewValue> maybeMap(mapper: (Value) -> NewValue?): Result<NewValue, MaybeNullable> {
            val mapped = mapper(inner)
            return if (mapped == null) {
                Err(MaybeNullable(null))
            } else {
                Ok(mapped)
            }
        }

        override fun <NewError : Throwable> mapError(mapper: (Error) -> NewError): Result<Value, NewError> = Ok(inner)

        override fun <NewValue> andThen(mapper: (Value) -> Result<NewValue, Error>): Result<NewValue, Error> =
            mapper(inner)

        override fun <NewError : Throwable> orElse(value: (Error) -> Result<Value, NewError>): Result<Value, NewError> =
            Ok(inner)
    }

    internal data class Err<Value, Error : Throwable>(private val inner: Error) : Result<Value, Error>() {
        override fun expect(message: String): Value = throw ResultError(message, inner)
        override fun unwrapOr(value: () -> Value): Value = value()
        override fun unwrapOrNull(): Value? = null
        override fun expectErr(message: String): Error = inner
        override fun <NewValue> map(mapper: (Value) -> NewValue): Result<NewValue, Error> = Err(inner)
        override fun <NewValue> maybeMap(mapper: (Value) -> NewValue?): Result<NewValue, MaybeNullable> =
            Err(MaybeNullable(inner))

        override fun <NewError : Throwable> mapError(mapper: (Error) -> NewError): Result<Value, NewError> =
            Err(mapper(inner))

        override fun <NewValue> andThen(mapper: (Value) -> Result<NewValue, Error>): Result<NewValue, Error> =
            Err(inner)

        override fun <NewError : Throwable> orElse(value: (Error) -> Result<Value, NewError>): Result<Value, NewError> =
            value(inner)
    }

    companion object ResultBuilder {
        @JvmStatic
        fun <Value> ofNullable(inner: Value?): Result<Value, NullPointerException> =
            if (inner != null) {
                Ok(inner)
            } else {
                Err(NullPointerException())
            }

        @JvmStatic
        fun <Value> ofFallible(creator: () -> Value): Result<Value, Throwable> =
            ofFallible(Throwable::class.java, creator)

        @JvmStatic
        fun <Value, Error : Throwable> ofFallible(expected: Class<Error>, creator: () -> Value): Result<Value, Error> {
            return try {
                Ok(creator())
            } catch (e: Throwable) {
                if (expected.isInstance(e)) {
                    @Suppress("UNCHECKED_CAST")
                    // Это безопасно, т.к. мы только что проверили корректность каста через isInstance.
                    Err(e as Error)
                } else {
                    throw e
                }
            }
        }
    }
}
