package cn.ancono.math.geometry.analytic.planeAG.curve;
/*
 * An equation of a curve in a plane.
 * @author lyc
 *
 */

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.geometry.analytic.planeAG.PAffineTrans;
import cn.ancono.math.geometry.analytic.planeAG.PlanePointSet;
import cn.ancono.math.geometry.analytic.planeAG.Point;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * An abstract class for plane curves.
 *
 * @param <T>
 * @author liyicheng
 */
public abstract class AbstractPlaneCurve<T> extends MathObject<T> implements PlanePointSet<T> {

    protected AbstractPlaneCurve(MathCalculator<T> mc) {
        super(mc);
    }

    /**
     * Determines whether the given point is on this curve.
     *
     * @param p a point
     * @return {@code true} if {@code p} is on this curve.
     */
    @Override
    public abstract boolean contains(Point<T> p);

    /**
     * Performs a transformation to this curve and returns a transformed curve. Assume {@code p} is a Point and {@code trans} is a PAffineTrans,
     * if {@code this.contains(p)} then it is assured that {@code this.transform(trans).contains(trans.apply(p))}. This method simply wraps this
     * curve with a TransformedCurve, but subclasses can override this method. Notice that the given PAffineTrans must be inversable, or an ArithematicExpection
     * will be thrown.
     *
     * @param trans a PAffineTrans
     * @return a transformed PlaneCurve
     * @throws ArithmeticException if the affine translation cannot be inversed.
     */
    public AbstractPlaneCurve<T> transform(PAffineTrans<T> trans) {
        return new TransformedCurve<>(getMc(), this, trans.inverse());
    }

    /**
     * Determines whether the given point is an intercept point of the two curves.
     *
     * @param p   a point
     * @param pc1 a curve
     * @param pc2 a curve
     * @return {@code true} if the given point is an intercept point of the two curves.
     */
    public static <T> boolean isInterceptPoint(Point<T> p, AbstractPlaneCurve<T> pc1, AbstractPlaneCurve<T> pc2) {
        return pc1.contains(p) && pc2.contains(p);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> AbstractPlaneCurve<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);


}
