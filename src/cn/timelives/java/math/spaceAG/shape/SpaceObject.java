package cn.timelives.java.math.spaceAG.shape;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.set.InfiniteSet;
import cn.timelives.java.math.spaceAG.SPoint;
import cn.timelives.java.math.spaceAG.SpacePointSet;

import java.util.function.Function;
/**
 * Space object is a class that has a shape in space.
 * @author liyicheng
 *
 */
public abstract class SpaceObject<T> extends SpacePointSet<T> implements InfiniteSet<SPoint<T>>{

	protected SpaceObject(MathCalculator<T> mc) {
		super(mc);
	}
	/**
	 * Determines whether the point is inside this space object.
	 * @param p
	 * @return
	 */
	public abstract boolean isInside(SPoint<T> p);
	/**
	 * Determines whether the point is on the surface of this space object.
	 * @param p
	 * @return
	 */
	public abstract boolean isOnSurface(SPoint<T> p);
	/**
	 * Returns the volume of this space object,  
	 * throws {@link UnsupportedOperationException} if needed.
	 * @return the volume of this space object.
	 */
	public abstract T volume();
	/**
	 * Returns the area of surface of this space object,  
	 * throws {@link UnsupportedOperationException} if needed.
	 * @return the area of surface of this space object.
	 */
	public abstract T surfaceArea();
	
	/**
	 * @see cn.timelives.java.math.set.InfiniteSet#cardinalNumber()
	 */
	@Override
	public int cardinalNumber() {
		return CARDINAL_REAL;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.spaceAG.SpacePointSet#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract <N> SpaceObject<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
