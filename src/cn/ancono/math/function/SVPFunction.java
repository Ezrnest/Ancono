/**
 *
 */
package cn.ancono.math.function;


import cn.ancono.math.MathCalculatorHolder;
import cn.ancono.math.algebra.IPolynomial;
import cn.ancono.math.algebra.linear.Vector;

import java.util.function.BiPredicate;


/**
 * Single value polynomial function. Generally, the equation can be shown as 
 * <pre>an*x^n + ... + a1*x + a0 , (an!=0,n>0)</pre>
 * @author liyicheng
 *
 */
public interface SVPFunction<T> extends SVFunction<T>, MathCalculatorHolder<T>, IPolynomial<T> {

    /**
     * Returns the coefficient {@code x^n},if {@code n==0} then the
     * coefficient {@code a0} will be returned.
     *
     * @param n the power of {@code x}
     * @return the coefficient
     * @throws IndexOutOfBoundsException if {@code n} is bigger than {@code getMaxPower()} or
     *                                   it is smaller than 0.
     */
    public T get(int n);

    /**
     * Returns the max power of x in this equation.
     * @return an integer number indicates the max power.
     *
     */
    int getDegree();

    public default Vector<T> coefficientVector() {
        return IPolynomial.coefficientVector(this, getMathCalculator());
    }

    /**
     * Determines whether the two SVPFunctions are equal. The subclasses may
     * use this method to test whether the two objects are equal.
     * @param f1 a function
     * @param f2 another function
     * @param equal
     * @return
     */
    public static <T, S> boolean isEqual(SVPFunction<T> f1, SVPFunction<S> f2, BiPredicate<T, S> equal) {
        return IPolynomial.isEqual(f1, f2, equal);
    }
}
