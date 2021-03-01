package cn.ancono.math.exceptions

object ExceptionUtil {

    /**
     * Throws an ArithmeticException indicating dividing by zero.
     * <pre>x / 0</pre>
     */
    @JvmStatic
    fun dividedByZero(): Nothing {
        throw ArithmeticException("Divide by zero: /0")
    }

    /**
     * Throws an ArithmeticException indicating the element is not invertible while trying to calculate its inverse.
     * <pre>x / 0</pre>
     */
    @JvmStatic
    fun notInvertible(): Nothing {
        throw ArithmeticException("Not invertible.")
    }

    /**
     * Throws an ArithmeticException of 0^0, an undefined exponent operation
     */
    @JvmStatic
    fun zeroExponent(): Nothing {
        throw ArithmeticException("Zero exponent: 0^0")
    }

    /**
     * Throws an ArithmeticException of calculating the square
     * root of a negative number.
     */
    @JvmStatic
    fun sqrtForNegative(): Nothing {
        throw ArithmeticException("Negative square: Sqr(-x)")
    }

    /**
     * Throws an ArithmeticException of log(-x)
     */
    @JvmStatic
    fun negativeLog(): Nothing {
        throw ArithmeticException("Negative log: log(-x)")
    }

    @JvmStatic
    fun valueTooBig(): Nothing {
        throw NumberValueException("Too big.")
    }

    @JvmStatic
    fun valueTooBig(expr: String): Nothing {
        throw NumberValueException("Too big.", expr)
    }

    @JvmStatic
    fun notImplemented(reason: String): Nothing {
        throw NotImplementedError(reason)
    }

    /**
     * Throws an exception indicating the division can not be done because [x] is not exact divisible by [y].
     *
     * For example, `2` is exact not divisible by `3`.
     */
    @JvmStatic
    fun notExactDivision(x: Any, y: Any): Nothing {
        throw ArithmeticException("Not exact division: $x / $y")
    }
}
