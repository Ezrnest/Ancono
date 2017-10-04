/**
 * 
 */
package cn.timelives.java.math.planeAG.curve;

import java.util.function.BiFunction;

import cn.timelives.java.math.function.BiMathFunction;
import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.planeAG.PlanePointSet;
import cn.timelives.java.math.planeAG.Point;

/**
 * Substituable curve is described with a equation 
 * <pre>f(x,y) = 0
 * </pre>
 * When substituting, the function <i>f</i> is applied.
 * @author liyicheng
 *
 */
public interface SubstituableCurve<T> extends PlanePointSet<T>{
	/**
	 * Do substitution with the given point p.
	 * @param p a point 
	 * @return the result of substitution.
	 */
	default T substitute(Point<T> p){
		return substitute(p.getX(),p.getY());
	}
	/**
	 * Do substitution with the given arguments x and y.
	 * @param x 
	 * @param y
	 * @return the result of substitution.
	 */
	T substitute(T x,T y);
	/**
	 * Returns a SubstituableCurve.
	 * @param f a double-parameter math function 
	 * @param isZero a math function to determines whether the parameter is zero.
	 * @return
	 */
	public static <T> SubstituableCurve<T> fromMathFunction(BiMathFunction<T,T,T> f,MathFunction<T,Boolean> isZero){
		return new SubstituableCurve<T>() {
			private final BiFunction<T,T,Boolean> contains = f.andThen(isZero);
			/* (non-Javadoc)
			 * @see cn.timelives.java.math.planeAG.PlanePointSet#contains(cn.timelives.java.math.planeAG.Point)
			 */
			@Override
			public boolean contains(Point<T> p) {
				return contains.apply(p.getX(),p.getY());
			}
			
			/* (non-Javadoc)
			 * @see cn.timelives.java.math.planeAG.SubstituableCurve#substitute(java.lang.Object, java.lang.Object)
			 */
			@Override
			public T substitute(T x, T y) {
				return f.apply(x,y);
			}
		};
	}
	
}
