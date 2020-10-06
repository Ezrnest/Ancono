package cn.ancono.math.function


/**
 * Describes the math function of `T^n -> R`.
 */
interface NMathFunction<T, R> : MathFunction<List<T>, R> {

    /**
     * The length of input argument of this function.
     */
    val n: Int

    /**
     * Applies the function to the given argument in variant argument form.
     */
    fun apply(vararg ts: T): R = apply(listOf(*ts))

}