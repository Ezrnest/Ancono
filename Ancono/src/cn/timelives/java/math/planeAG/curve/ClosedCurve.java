/**
 * 
 */
package cn.timelives.java.math.planeAG.curve;

import cn.timelives.java.math.planeAG.Point;

/**
 * Closed curve is a curve that keeps a relation of inside and outside 
 * determining whether a point in plane is inside this curve, on this curve
 * or outside this curve. A curve should always returns the same result
 * for the methods as long as the given points are the same or they are 
 * {@link Point#valueEquals(cn.timelives.java.math.FlexibleMathObject)}.
 * <p>
 * The closed curve may also provides the  
 * @author liyicheng
 * @param T the type of the number model
 */
public interface ClosedCurve<T> extends AreableCurve<T>{
	/**
	 * Determines whether the point is inside of this curve.
	 * @param p
	 * @return
	 */
	default boolean isInside(Point<T> p){
		return relation(p) < 0;
	}
	/**
	 * Determines whether the point is outside of this 
	 * @param p
	 * @return
	 */
	default boolean isOutside(Point<T> p){
		return relation(p) > 0;
	}
	/**
	 * Determines the point's relation with the curve, returns {@code -1} if {@link #isInside(Point)}, 
	 * {@code 1} if {@link #isOutside(Point)}, or {@code 0} otherwise.
	 * @return {@code 1,0} or {@code -1}
	 */
	int relation(Point<T> p);
		
	/**
	 * Computes the area inside this curve, throws an exception if necessary.
	 * @return the area inside this curve.
	 * @throws UnableToCalculateException if the area cannot be calculated. (optional)
	 */
	@Override
	T computeArea();
	
}
