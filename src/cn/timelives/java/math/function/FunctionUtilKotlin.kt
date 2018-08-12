package cn.timelives.java.math.function

/**
 * Converts function's apply method to invoke.
 */
operator fun <T> SVFunction<T>.invoke(x: T): T = apply(x)