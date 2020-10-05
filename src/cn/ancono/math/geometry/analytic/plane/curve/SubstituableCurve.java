/**
 *
 */
package cn.ancono.math.geometry.analytic.plane.curve;

import cn.ancono.math.function.BiMathFunction;
import cn.ancono.math.function.MathFunction;
import cn.ancono.math.geometry.analytic.plane.PlanePointSet;
import cn.ancono.math.geometry.analytic.plane.Point;

import java.util.function.BiFunction;

/**
 * Substituable curve is described with a equation 
 * <pre>f(x,y) = 0
 * </pre>
 * When substituting, the function <i>f</i> is applied.
 * @author liyicheng
 *
 */
public interface SubstituableCurve<T> extends PlanePointSet<T> {
    /**
     * Do substitution with the given point p.
     * @param p a point
     * @return the result of substitution.
     */
    default T substitute(Point<T> p) {
        return substitute(p.getX(), p.getY());
    }

    /**
     * Do substitution with the given arguments x and y.
     * @param x
     * @param y
     * @return the result of substitution.
     */
    T substitute(T x, T y);

    /**
     * Returns a SubstituableCurve.
     * @param f a double-parameter math function
     * @param isZero a math function to determines whether the parameter is zero.
     * @return
     */
    public static <T> SubstituableCurve<T> fromMathFunction(BiMathFunction<T, T, T> f, MathFunction<T, Boolean> isZero) {
        return new SubstituableCurve<T>() {
            private final BiFunction<T, T, Boolean> contains = f.andThen(isZero);

            /* (non-Javadoc)
             * @see cn.ancono.math.geometry.analytic.planeAG.PlanePointSet#contains(cn.ancono.math.geometry.analytic.planeAG.Point)
             */
            @Override
            public boolean contains(Point<T> p) {
                return contains.apply(p.getX(), p.getY());
            }

            /* (non-Javadoc)
             * @see cn.ancono.math.geometry.analytic.planeAG.SubstituableCurve#substitute(java.lang.Object, java.lang.Object)
             */
            @Override
            public T substitute(T x, T y) {
                return f.apply(x, y);
            }
        };
    }

}
