package cn.timelives.java.math.spaceAG.shape;

import java.util.Set;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.spaceAG.Line;
import cn.timelives.java.math.spaceAG.Plane;
import cn.timelives.java.math.spaceAG.Segment;

public abstract class Prism<T> extends Polyhedron<T> {
	
	private final long p;
	
	/**
	 * Create a prism with the number of its Parallelograms.
	 * @param mc
	 * @param p the number of its Parallelogram.
	 */
	protected Prism(MathCalculator<T> mc,long p) {
		super(mc);
		this.p = p;
	}
	
	@Override
	public long numEdge() {
		return 3*p;
	}
	
	@Override
	public long numSurface() {
		return p+2;
	}
	
	@Override
	public long numVertex() {
		return 2*p;
	}
	@Override
	public boolean isConvex() {
		return true;
	}
	
	
	
	/**
	 * Gets the bottom base surface of this prism.
	 * @return
	 */
	public abstract Plane<T> getBottomSurface();
	
	/**
	 * Gets the top base surface of this prism.
	 * @return
	 */
	public abstract Plane<T> getTopSurface();
	
	/**
	 * Gets the side surfaces of this prism.
	 * @return
	 */
	public abstract Set<Plane<T>> getSides();
	/**
	 * Determines whether the plane is a side surface.
	 * @param p
	 * @return
	 */
	public abstract boolean isSide(Plane<T> p);
	/**
	 * Gets the slantedges of this prism.
	 * @return
	 */
	public abstract Set<Segment<T>> getSlantedge();
	/**
	 * Returns the height.
	 * @return
	 */
	public abstract T getHeight();
	
	@Override
	public abstract <N> Prism<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
