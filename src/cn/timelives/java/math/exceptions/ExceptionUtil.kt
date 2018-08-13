package cn.timelives.java.math.exceptions

object ExceptionUtil {

    /**
     * Throws an ArithmeticException indicating dividing by zero.
     * <pre>x / 0</pre>
     */
    fun divideByZero(): Nothing {
        throw ArithmeticException("Divide by zero: /0")
    }

    /**
     * Throws an ArithmeticException of 0^0, an undefined exponent operation
     */
    fun zeroExponent(): Nothing {
        throw ArithmeticException("Zero exponent: 0^0")
    }

    /**
     * Throws an ArithmeticException of calculating the square
     * root of a negative number.
     */
    fun negativeSquare(): Nothing {
        throw ArithmeticException("Negative square: Sqr(-x)")
    }

    /**
     * Throws an ArithmeticException of log(-x)
     */
    fun negativeLog(): Nothing {
        throw ArithmeticException("Negative log: log(-x)")
    }

    fun valueTooBig(): Nothing {
        throw NumberValueException("Too big.")
    }

    fun valueTooBig(expr: String): Nothing {
        throw NumberValueException("Too big.", expr)
    }
}
