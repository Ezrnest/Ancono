package cn.ancono.math.algebra.abs.calculator

/**
 * A calculator for unique factorization domain(UFD). It supports computing the greatest common divisor of
 * two elements.
 *
 * **Implementation Notes:** Subclasses should always implements the method [UnitRingCalculator.isUnit].
 *
 *
 */
interface UFDCalculator<T> : UnitRingCalculator<T> {


    /**
     * Returns the greatest common divisor of [a] and [b].
     */
    fun gcd(a: T, b: T): T

    //    /**
    //     * Returns the greatest common divisor of two numbers and a pair of number (u,v) such that
    //     * <pre>ua+vb=gcd(a,b)</pre>
    //     * The returned greatest common divisor is the same as {@link NTCalculator#gcd(Object, Object)}.
    //     * Note that the pair of <code>u</code> and <code>v</code> returned is not unique and different implementation
    //     * may return differently when a,b is the same.<P></P>
    //     * The default implementation is based on the Euclid's algorithm.
    //     *
    //     * @return a tuple of <code>{gcd(a,b), u, v}</code>.
    //     */
    //    @NotNull
    //    default Triple<T, T, T> gcdUV(@NotNull T a, T b) {
    //        if (isZero(a)) {
    //            return new Triple<>(b, getZero(), getOne());
    //        }
    //        if (isZero(b)) {
    //            return new Triple<>(a, getOne(), getZero());
    //        }
    //        return gcdUV0(a, b);
    //    }

    /**
     * Determines whether the two numbers `a` and `b`
     * are co-prime.
     */
    fun isCoprime(a: T, b: T): Boolean {
        return isUnit(gcd(a, b))
    }

    /**
     * Returns the result of exact division `x/y`, throws an `UnsupportedCalculationException` if it is not exact division.
     */
    override fun exactDivide(x: T, y: T): T

    /**
     * Determines whether `x` exactly divides `y`.
     */
    fun isExactDivide(a: T, b: T): Boolean
}

