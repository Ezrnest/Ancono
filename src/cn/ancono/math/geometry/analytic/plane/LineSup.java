package cn.ancono.math.geometry.analytic.plane;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.numberModels.api.Simplifier;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for supplementary line calculations.
 *
 * @author lyc
 */
public class LineSup {
    private LineSup() {
    }

    /**
     * Calculate whether the three lines intersect at the identity point.
     *
     * @param l1 a line
     * @param l2 a line
     * @param l3 a line
     * @return {@code true} if they intersect at the identity point.
     */
    public static <T> boolean sameIntersectPoint(Line<T> l1, Line<T> l2, Line<T> l3) {
        //we use a matrix to calculate this stuff
        // |a1 b1 c1|
        // |a2 b2 c2| = 0 for true
        // |a3 b3 c3|
        MathCalculator<T> mc = l1.getMathCalculator();
        T result = mc.multiply(l1.a, mc.multiply(l2.b, l3.c));
        result = mc.add(result, mc.multiply(l1.b, mc.multiply(l2.c, l3.a)));
        result = mc.add(result, mc.multiply(l1.c, mc.multiply(l2.a, l3.b)));
        result = mc.subtract(result, mc.multiply(l1.c, mc.multiply(l2.b, l3.a)));
        result = mc.subtract(result, mc.multiply(l1.a, mc.multiply(l2.c, l3.b)));
        result = mc.subtract(result, mc.multiply(l1.b, mc.multiply(l2.a, l3.c)));

        return mc.isZero(result);
    }

    /**
     * Simplify the line using the given simplifier.
     *
     * @param l   a line
     * @param sim a simplifier
     * @return a new line.
     */
    public static <T> Line<T> simplify(Line<T> l, Simplifier<T> sim) {
        List<T> list = new ArrayList<>(3);
        list.add(l.a);
        list.add(l.b);
        list.add(l.c);
        list = sim.simplify(list);
        return Line.generalFormula(list.get(0), list.get(1), list.get(2), l.getMathCalculator());
    }

    /**
     * Return a line that is perpendicular to <i>AB</i> and passes through the middle
     * point of {@code AB}.
     *
     * @param A
     * @param B
     * @return a line
     * @throws IllegalArgumentException if A and B are the identity point
     */
    public static <T> Line<T> perpendicularMiddleLine(Point<T> A, Point<T> B) {
        if (A.valueEquals(B)) {
            throw new IllegalArgumentException();
        }
        Point<T> M = A.middle(B);
        PVector<T> nv = A.directVector(B);
        return Line.pointNormal(M, nv, A.getMathCalculator());
    }

    /**
     * Gets the coordinate axis X.
     *
     * @param mc
     * @return
     */
    public static <T> Line<T> xAxis(MathCalculator<T> mc) {
        T zero = mc.getZero();
        return Line.parallelX(zero, mc);
    }

    /**
     * Gets the coordinate axis Y.
     *
     * @param mc
     * @return
     */
    public static <T> Line<T> yAxis(MathCalculator<T> mc) {
        T zero = mc.getZero();
        return Line.parallelY(zero, mc);
    }

}
