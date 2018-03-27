/**
 * 
 */
package cn.timelives.java.math.planeAG.curve;

import cn.timelives.java.math.function.AbstractSVFunction;
import cn.timelives.java.math.function.SVFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.planeAG.PlanePointSet;
import cn.timelives.java.math.planeAG.Point;

import java.util.function.Function;

/**
 * @author liyicheng
 *
 */
public abstract class AbstractPlaneFunction<T> extends AbstractSVFunction<T> implements SVFunction<T>,PlanePointSet<T>{

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
	
	/*
	 * @see cn.timelives.java.math.planeAG.curve.AbstractPlaneCurve#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public abstract <N> AbstractPlaneFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
