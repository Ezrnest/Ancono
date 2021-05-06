/**
 *
 */
package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

/**
 * Describes an object that is on a plane in the space.
 * @author liyicheng
 *
 */
public abstract class SpacePlaneObject<T> extends SpacePointSet<T> {
    protected final Plane<T> pl;

    /**
     * @param mc
     */
    protected SpacePlaneObject(RealCalculator<T> mc, Plane<T> pl) {
        super(mc);
        this.pl = pl;
    }

    /**
     * Returns the plane this object is on
     * @return
     */
    public Plane<T> getPlane() {
        return pl;
    }

    /**
     * Determines whether this plane object is on the plane.
     *
     * @param pl
     * @return
     */
    public boolean isOnPlane(Plane<T> pl) {
        return this.pl.valueEquals(pl);
    }

    @NotNull
    @Override
    public RealCalculator<T> getCalculator() {
        return (RealCalculator<T>) super.getCalculator();
    }
}
