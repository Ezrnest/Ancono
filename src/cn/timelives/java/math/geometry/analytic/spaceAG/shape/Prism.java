package cn.timelives.java.math.geometry.analytic.spaceAG.shape;

import cn.timelives.java.math.geometry.analytic.spaceAG.Plane;
import cn.timelives.java.math.geometry.analytic.spaceAG.Segment;
import cn.timelives.java.math.MathCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Function;

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

    @NotNull
    @Override
    public abstract <N> Prism<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
}
