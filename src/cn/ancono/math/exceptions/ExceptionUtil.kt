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
    fun negativeSquare(): Nothing {
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
}
