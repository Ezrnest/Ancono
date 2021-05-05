package cn.ancono.math.geometry.analytic.space.shape;

import cn.ancono.math.MathObject;
import cn.ancono.math.geometry.analytic.space.SPoint;
import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * TODO :unfinished
 *
 * @param <T>
 * @author liyicheng
 */
public final class Cylinder<T> extends SpaceObject<T> {

    //a cylinder has it

    protected Cylinder(RealCalculator<T> mc) {
        super(mc);
    }

    @Override
    public boolean isInside(SPoint<T> p) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isOnSurface(SPoint<T> p) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean contains(SPoint<T> p) {
        // TODO Auto-generated method stub
        return false;
    }

    @NotNull
    @Override
    public <N> Cylinder<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int hashCode() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        // TODO Auto-generated method stub
        return false;
    }

    /* (non-Javadoc)
     * @see cn.ancono.cn.ancono.utilities.math.spaceAG.shape.SpaceObject#volume()
     */
    @Override
    public T volume() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see cn.ancono.cn.ancono.utilities.math.spaceAG.shape.SpaceObject#surfaceArea()
     */
    @Override
    public T surfaceArea() {
        // TODO Auto-generated method stub
        return null;
    }

}
