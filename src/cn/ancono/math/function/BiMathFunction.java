/**
 *
 */
package cn.ancono.math.function;

import java.util.function.BiFunction;

/**
 * BiMathFunction is an interface indicating math functions.Math function is a kind of special function.
 * This function must perform like a real math function:
 * <ul>
 * <li>It does NOT make change to parameter:Any parameter should not be changed in this function. 
 * <li>It is <tt>consistent</tt>:If this function is applied with identity parameters for multiple times,the
 * result should be the identity.
 * </ul>
 *
 * @author lyc
 *
 * @param <P1> parameter type 1
 * @param <P2> parameter type 2
 * @param <R> result type
 */
public interface BiMathFunction<P1, P2, R> extends BiFunction<P1, P2, R> {
    /* (non-Javadoc)
     * @see java.util.function.BiFunction#apply(java.lang.Object, java.lang.Object)
     */
    @Override
    R apply(P1 x, P2 y);
}
