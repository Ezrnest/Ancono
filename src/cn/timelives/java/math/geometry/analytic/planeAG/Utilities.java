/**
 * 
 */
package cn.timelives.java.math.geometry.analytic.planeAG;

import cn.timelives.java.math.geometry.analytic.planeAG.curve.SubstituableCurve;
import cn.timelives.java.math.geometry.visual.visual2D.SubstitutableCurve;

import java.awt.geom.Point2D;
import java.util.function.DoubleFunction;
import java.util.function.ToDoubleFunction;

/**
 * The utility class for planeAG.
 * @author liyicheng
 *
 */
public final class Utilities {

	/**
	 * 
	 */
	private Utilities() {
	}
	/**
	 * Returns the area of triangle <i>ABC</i>, the 
	 * area may be negate.
	 * @param A
	 * @param B
	 * @param C
	 * @return
	 */
	public static <T> T area(Point<T> A,Point<T> B,Point<T> C){
		Triangle<T> tri = new Triangle<>(A.getMathCalculator(), A, B, C);
		return tri.areaPN();
	}
	
	public static <T> T angleCos(Point<T> A,Point<T> O,Point<T> B){
		return PVector.vector(O, A).angleCos(PVector.vector(O, B));
	}
	
	/**
	 * Returns a double-typed function which is adapted from a SubstituableCurve.
	 * @param fromDouble the function to convert double to T
	 * @param toDouble  the function to convert T to double
	 * @param curve a curve
	 * @return a function that accept {@link Point2D.Double} and returns double.
	 */
	public static <T> SubstitutableCurve mapToDouble(
			DoubleFunction<T> fromDouble,ToDoubleFunction<T> toDouble,
			SubstituableCurve<T> curve){
		return (x,y) -> toDouble.applyAsDouble(curve.substitute(fromDouble.apply(x), fromDouble.apply(y)));
		
	}
	
	/**
	 * Returns a double-typed function which is adapted from a SubstituableCurve.
	 * @param curve a curve
	 * @return a function that accept {@link Point2D.Double} and returns double.
	 */
	public static SubstitutableCurve mapToDouble(
			SubstituableCurve<Double> curve){
		return (x,y) -> curve.substitute(x, y);
		
	}
	
}
