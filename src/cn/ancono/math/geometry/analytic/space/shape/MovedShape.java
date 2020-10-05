/**
 *
 */
package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.geometry.analytic.space.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Moved shape is a shape that is produced by a {@link SPlaneCurve} moving by a vector, which
 * shapes a plane object. The 
 * @author liyicheng
 *
 */
public class MovedShape<T> extends SpaceObject<T> {
    final SpacePlaneObject<T> spo;
    final SVector<T> vec;
    final SPointTrans<T> spt;

    /**
     * @param mc
     * @param pl
     */
    MovedShape(MathCalculator<T> mc, SpacePlaneObject<T> spo, SVector<T> vec) {
        super(mc);
        this.spo = spo;
        this.vec = vec;
        spt = spo.getPlane().projectionAsFunction(vec);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.spaceAG.SpacePointSet#contains(cn.ancono.math.spaceAG.SPoint)
     */
    @Override
    public boolean contains(SPoint<T> p) {
        SPoint<T> projection = spo.getPlane().projection(p, vec);
        if (!spo.contains(projection)) {
            //the project must be in the shape
            return false;
        }
        SVector<T> sv = SVector.vector(p, projection);
        return SpaceAgUtils.compareVector(vec, sv) >= 0;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
     */
    @NotNull
    @Override
    public <N> MovedShape<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#hashCode()
     */
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject, java.util.function.Function)
     */
    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see SpaceObject#isInside(SPoint)
     */
    @Override
    public boolean isInside(SPoint<T> p) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see SpaceObject#isOnSurface(SPoint)
     */
    @Override
    public boolean isOnSurface(SPoint<T> p) {
        // TODO Auto-generated method stub
        return false;
    }

    /**
     * @see SpaceObject#volume()
     */
    @Override
    public T volume() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * @see SpaceObject#surfaceArea()
     */
    @Override
    public T surfaceArea() {
        // TODO Auto-generated method stub
        return null;
    }

}
