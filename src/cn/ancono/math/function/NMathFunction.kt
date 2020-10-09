package cn.ancono.math.function


/**
 * Describes the math function of `T^n -> R`.
 */
interface NMathFunction<T, R> : MathFunction<List<T>, R> {

    /**
     * The length of input argument of this function.
     */
    val paramLength: Int

    /**
     * Applies the function to the given argument in variant argument form.
     */
    fun apply(vararg ts: T): R = apply(listOf(*ts))

}

operator fun <T, R> NMathFunction<T, R>.invoke(vararg ts: T): R = this.apply(*ts)