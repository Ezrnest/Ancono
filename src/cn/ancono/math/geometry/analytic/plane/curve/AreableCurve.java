/**
 *
 */
package cn.ancono.math.geometry.analytic.plane.curve;

import cn.ancono.math.geometry.analytic.plane.PlanePointSet;

/**
 * Areable curve is a curve with an area which is inside this curve 
 * that can be computed. This kind of curve can be either closed or "nearly" closed. 
 * @author liyicheng
 *
 */
public interface AreableCurve<T> extends PlanePointSet<T> {
    /**
     * Computes the area
     * @return the area in this curve.
     */
    T computeArea();
}
