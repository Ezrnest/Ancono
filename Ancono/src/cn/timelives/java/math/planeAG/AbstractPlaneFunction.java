/**
 * 
 */
package cn.timelives.java.math.planeAG;

import cn.timelives.java.math.function.SVFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.planeAG.curve.AbstractPlaneCurve;

/**
 * @author liyicheng
 *
 */
public abstract class AbstractPlaneFunction<T> extends AbstractPlaneCurve<T> implements SVFunction<T> {

	/**
	 * @param mc
	 */
	public AbstractPlaneFunction(MathCalculator<T> mc) {
		super(mc);
	}

	/* (non-Javadoc)
	 * @see cn.timelives.java.math.planeAG.PlanePointSet#contains(cn.timelives.java.math.planeAG.Point)
	 */
	@Override
	public boolean contains(Point<T> p) {
		return mc.isEqual(p.y, apply(p.x));
	}
	/**
	 * Returns a point.
	 * @param x the x coordinate.
	 * @return a new point.
	 */
	public Point<T> getPoint(T x){
		return Point.valueOf(x, apply(x), mc);
	}
	
}
