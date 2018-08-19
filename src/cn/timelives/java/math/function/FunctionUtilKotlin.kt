package cn.timelives.java.math.function

/**
 * Converts function's apply method to invoke.
 */
operator fun <T : Any, R : Any> MathFunction<T, R>.invoke(x: T): R = apply(x)

fun <T : Any> MathFunction<T, T>.asSVFunction(): SVFunction<T> = SVFunction.fromFunction(this.domain(), this)


