/**
 *
 */
package cn.ancono.math.geometry.analytic.plane.curve;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.function.AbstractSVFunction;
import cn.ancono.math.function.SVFunction;
import cn.ancono.math.geometry.analytic.plane.PlanePointSet;
import cn.ancono.math.geometry.analytic.plane.Point;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * @author liyicheng
 *
 */
public abstract class AbstractPlaneFunction<T> extends AbstractSVFunction<T> implements SVFunction<T>, PlanePointSet<T> {

    /**
     * @param mc
     */
    public AbstractPlaneFunction(MathCalculator<T> mc) {
        super(mc);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.geometry.analytic.planeAG.PlanePointSet#contains(cn.ancono.math.geometry.analytic.planeAG.Point)
     */
    @Override
    public boolean contains(Point<T> p) {
        return getMc().isEqual(p.y, apply(p.x));
    }

    /**
     * Returns a point.
     * @param x the x coordinate.
     * @return a new point.
     */
    public Point<T> getPoint(T x) {
        return Point.valueOf(x, apply(x), getMc());
    }

    /*
     * @see cn.ancono.math.geometry.analytic.planeAG.curve.AbstractPlaneCurve#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> AbstractPlaneFunction<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);
}
