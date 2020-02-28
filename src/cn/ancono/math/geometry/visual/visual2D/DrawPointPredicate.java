/**
 * 2017-11-16
 */
package cn.ancono.math.geometry.visual.visual2D;

/**
 * @author liyicheng
 * 2017-11-16 20:46
 */
public interface DrawPointPredicate {

    /**
     * Determines whether to draw the specific point. The data must not be changed.
     *
     * @param data the array storing the data of f(x,y).
     * @param x    x coordinate in the data, excluding {@code 0} and {@code length-1}
     * @param y    y coordinate in the data, excluding {@code 0} and {@code length-1}
     * @return
     */
    public boolean shouldDraw(double[][] data, int x, int y);
}
