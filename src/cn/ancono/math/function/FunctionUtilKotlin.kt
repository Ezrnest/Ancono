package cn.ancono.math.function

/**
 * Converts function's apply method to invoke.
 */
operator fun <T, R> MathFunction<T, R>.invoke(x: T): R = apply(x)

fun <T> MathFunction<T, T>.asSVFunction(): SVFunction<T> = SVFunction.fromFunction(this.domain(), this)


