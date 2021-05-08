package cn.ancono.math.algebra.abs.calculator


/*
 * Created by liyicheng at 2021-05-06 19:32
 */



/**
 * Describes an abelian group with a order relation denoted by `<, <=, >, >=`.
 *
 * The order must be consistent with addition, that is:
 *
 *     x < y    implies    x + a < y + a, for any a
 *
 *
 */
interface OrderedAbelGroupCal<T> : AbelGroupCal<T>, OrderPredicate<T> {

    /**
     * Compares two elements.
     */
    override fun compare(o1: T, o2: T): Int

    /**
     * Returns the absolute value `|x|` of [x]`.
     * If `x >= 0` then `x` is returned, otherwise `-x` is returned.
     *
     * The triangle inequality is satisfied:
     *
     *     |a + b| <= |a| + |b|
     *
     */
    fun abs(x: T): T {
        if (compare(x, zero) < 0) {
            return -x
        }
        return x
    }

    /**
     * Determines whether the number is positive. This method is equivalent to `compare(x, zero) > 0`.
     *
     * @param x a number
     * @return `x > 0`
     */
    fun isPositive(x: T): Boolean {
        return compare(x, zero) > 0
    }

    /**
     * Determines whether the number is negative. This method is equivalent to `compare(x, zero) < 0`.
     *
     * @param x a number
     * @return `x < 0`
     */
    fun isNegative(x: T): Boolean {
        return compare(x, zero) < 0
    }
}

/**
 * Describes a ring with a order relation denoted by `<, <=, >, >=`, which satisfies:
 *
 *     x < y    implies    x + a < y + a, for any a
 *     x > 0, y > 0    implies    x * y > 0
 *
 * The following properties hold:
 *
 *     x > y  and  c > 0    implies    c*x > c*y
 *     |a*b| = |a|*|b|
 *
 *
 *
 */
interface OrderedRingCal<T> : RingCalculator<T>, OrderedAbelGroupCal<T>

/**
 * Describes a field with a order relation denoted by `<`, which satisfies:
 *
 *     x > 0, y > 0   implies    x + y > 0,   x * y > 0
 *     x > 0    implies    -x < 0
 *
 */
interface OrderedFieldCal<T> : FieldCalculator<T>, OrderedRingCal<T>

