/**
 *
 */
package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.geometry.analytic.plane.curve.AbstractPlaneCurve;
import cn.ancono.math.geometry.analytic.space.Plane.PlaneCoordinateConverter;
import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * This class bridges the spaceAG and planeAG.
 * @author liyicheng
 *
 */
public class SPlaneCurve<T> extends SpacePlaneObject<T> {
    final AbstractPlaneCurve<T> pc;
    final PlaneCoordinateConverter<T> pcc;

    protected SPlaneCurve(RealCalculator<T> mc, PlaneCoordinateConverter<T> pcc, AbstractPlaneCurve<T> pc) {
        super(mc, pcc.getPlane());
        this.pc = pc;
        this.pcc = pcc;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.spaceAG.SpacePointSet#contains(cn.ancono.math.spaceAG.SPoint)
     */
    @Override
    public boolean contains(SPoint<T> p) {
        return pl.contains(p) && pc.contains(pcc.toPlanePoint0(p));
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    @NotNull
    @Override
    public <N> SPlaneCurve<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new SPlaneCurve<N>((RealCalculator<N>) newCalculator, pcc.mapTo(newCalculator, mapper),
                pc.mapTo(newCalculator, mapper));
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SPlaneCurve) {
            SPlaneCurve<?> spc = (SPlaneCurve<?>) obj;
            return pc.equals(spc.pc) && pcc.equals(spc.pcc);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#hashCode()
     */
    @Override
    public int hashCode() {
        return pc.hashCode() * 37 + pcc.hashCode();
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    @Override
    public boolean valueEquals(@NotNull MathObject<T, EqualPredicate<T>> obj) {
        if (obj instanceof SPlaneCurve) {
            SPlaneCurve<T> spc = (SPlaneCurve<T>) obj;
            return pc.valueEquals(spc.pc) && pcc.valueEquals(spc.pcc);
        }
        return false;
    }


    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SPlaneCurve:p={").append(pl).append("} curve=").append(pc.toString());
        return sb.toString();
    }

}
