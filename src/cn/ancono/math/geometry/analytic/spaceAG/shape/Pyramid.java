/**
 *
 */
package cn.ancono.math.geometry.analytic.spaceAG.shape;

import cn.ancono.math.MathCalculator;

/**
 * @author liyicheng
 *
 */
public abstract class Pyramid<T> extends Polyhedron<T> {
    protected final int num;

    /**
     * @param mc
     * @param num the number of lean edge
     */
    protected Pyramid(MathCalculator<T> mc, int num) {
        super(mc);
        this.num = num;
    }

    /* (non-Javadoc)
     * @see cn.ancono.utilities.math.spaceAG.shape.Polyhedron#numEdge()
     */
    @Override
    public long numEdge() {
        return num * 2;
    }

    /* (non-Javadoc)
     * @see cn.ancono.utilities.math.spaceAG.shape.Polyhedron#numSurface()
     */
    @Override
    public long numSurface() {
        return num + 1;
    }

    /* (non-Javadoc)
     * @see cn.ancono.utilities.math.spaceAG.shape.Polyhedron#numVertex()
     */
    @Override
    public long numVertex() {
        return num + 1;
    }

    /* (non-Javadoc)
     * @see cn.ancono.utilities.math.spaceAG.shape.Polyhedron#isConvex()
     */
    @Override
    public boolean isConvex() {
        return true;
    }

}
