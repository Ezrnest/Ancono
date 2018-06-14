package cn.timelives.java.math.exceptions;

public final class ExceptionUtil {
    private ExceptionUtil(){}

    /**
     * Throws an ArithmeticException indicating dividing by zero.
     * <pre>x / 0</pre>
     */
    public static void divideByZero(){
        throw new ArithmeticException("Divide by zero: /0");
    }

    /**
     * Throws an ArithmeticException of 0^0, an undefined exponent operation
     */
    public static void zeroExponent(){
        throw new ArithmeticException("Zero exponent: 0^0");
    }

    /**
     * Throws an ArithmeticException of calculating the square
     * root of a negative number.
     */
    public static void negativeSquare(){
        throw new ArithmeticException("Negative square: Sqr(-x)");
    }

    /**
     * Throws an ArithmeticException of log(-x)
     */
    public static void negativeLog(){
        throw new ArithmeticException("Negative log: log(-x)");
    }



}
