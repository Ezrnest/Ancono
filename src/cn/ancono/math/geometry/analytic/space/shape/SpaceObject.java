package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.geometry.analytic.space.SPoint;
import cn.ancono.math.geometry.analytic.space.SpacePointSet;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.math.set.InfiniteSet;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Space object is a class that has a shape in space.
 *
 * @author liyicheng
 */
public abstract class SpaceObject<T> extends SpacePointSet<T> implements InfiniteSet<SPoint<T>> {

    protected SpaceObject(RealCalculator<T> mc) {
        super(mc);
    }

    /**
     * Determines whether the point is inside this space object.
     *
     * @param p
     * @return
     */
    public abstract boolean isInside(SPoint<T> p);

    /**
     * Determines whether the point is on the surface of this space object.
     *
     * @param p
     * @return
     */
    public abstract boolean isOnSurface(SPoint<T> p);

    /**
     * Returns the volume of this space object,
     * throws {@link UnsupportedOperationException} if needed.
     *
     * @return the volume of this space object.
     */
    public abstract T volume();

    /**
     * Returns the area of surface of this space object,
     * throws {@link UnsupportedOperationException} if needed.
     *
     * @return the area of surface of this space object.
     */
    public abstract T surfaceArea();

    /**
     * @see cn.ancono.math.set.InfiniteSet#cardinality()
     */
    @Override
    public int cardinality() {
        return CARDINAL_REAL;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.spaceAG.SpacePointSet#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> SpaceObject<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper);

    @NotNull
    @Override
    public RealCalculator<T> getCalculator() {
        return (RealCalculator<T>) super.getCalculator();
    }
}
