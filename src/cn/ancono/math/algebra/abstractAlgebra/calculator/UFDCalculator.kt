package cn.ancono.math.algebra.abstractAlgebra.calculator

/**
 * A calculator for unique factorization domain(UFD).
 */
interface UFDCalculator<T : Any> : UnitRingCalculator<T> {

    /**
     * Returns the greatest common divisor of [a] and [b].
     */
    fun gcd(a: T, b: T): T

    /**
     * Returns the result of exact division `x/y`, throws an `UnsupportedCalculationException` if it is not exact division.
     */
    fun exactDivide(x: T, y: T): T

    /**
     * Determines whether `x` exactly divides `y`.
     */
    fun isExactDivide(a: T, b: T): Boolean
}

