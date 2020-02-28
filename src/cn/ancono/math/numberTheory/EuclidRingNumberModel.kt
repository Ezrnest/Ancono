package cn.ancono.math.numberTheory

import cn.ancono.math.numberModels.api.RingNumberModel
import cn.ancono.math.numberModels.api.times

/**
 * Describes the number model that can be elements of a Euclid ring.
 * Created at 2018/12/8 17:15
 * @author  liyicheng
 */
interface EuclidRingNumberModel<T : EuclidRingNumberModel<T>> : RingNumberModel<T> {


    /**
     * Returns the greatest common divisor of `this` and `y`.
     */
    fun gcd(y: T): T {
        @Suppress("UNCHECKED_CAST")
        var a: T = this as T
        var b = y
        var t: T
        while (!b.isZero()) {
            t = b
            b = a.remainder(b)
            a = t
        }
        return a
    }

    /**
     * Returns a triple containing `gcd(this,y)` and `u,v` that
     * `u*this + v*y = gcd(this,y)`
     */
    fun gcdUV(y: T): Triple<T, T, T>

    /**
     * Returns the least common multiplier of `this` and `y`.
     */
    fun lcm(y: T): T {
        @Suppress("UNCHECKED_CAST") val x = this as T
        if (x.isZero()) {
            return x
        }
        if (y.isZero()) {
            return y
        }
        val gcd = x.gcd(y)
        return (x * y).divideToInteger(gcd)
    }

    fun divideAndRemainder(y: T): Pair<T, T> {
        val q = divideToInteger(y)
        val r = remainder(y)
        return q to r
    }

    fun divideToInteger(y: T): T = divideAndRemainder(y).first

    fun remainder(y: T): T = divideAndRemainder(y).second

    fun isCoprime(y: T): Boolean

    fun deg(y: T): Int {
        @Suppress("UNCHECKED_CAST") var b = this as T
        if (y.isZero()) {
            throw ArithmeticException("a==0")
        }
        var k = 0
        var dar = b.divideAndRemainder(y)
        while (dar.second.isZero()) {
            // b%a==0
            k++
            if (b == dar.first) {
                throw ArithmeticException("a==1")
            }
            b = dar.first
            // b = b/a;
            dar = b.divideAndRemainder(y)

        }
        return k
    }

}

@Suppress("NOTHING_TO_INLINE")
inline operator fun <T : EuclidRingNumberModel<T>> EuclidRingNumberModel<T>.rem(y: T) = this.remainder(y)
