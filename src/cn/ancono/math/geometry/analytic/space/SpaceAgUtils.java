/**
 *
 */
package cn.ancono.math.geometry.analytic.space;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.numberModels.Calculators;

/**
 * A tool for spaceAG.
 * @author liyicheng
 *
 */
public final class SpaceAgUtils {
    /**
     * Returns the angle of {@code ��AOB}
     * @param A
     * @param O
     * @param B
     * @return
     */
    public static <T> T angleCos(SPoint<T> A, SPoint<T> O, SPoint<T> B) {
        return SVector.vector(O, A).angleCos(SVector.vector(O, B));
    }

    /**
     * Returns the angle between two planes: <i>ABC</i> and <i>BCD</i>, or
     * say {@code ��A-BC-D}.
     * @param A a point
     * @param B a point
     * @param C a point
     * @param D a point
     * @return
     */
    public static <T> T angleCos(SPoint<T> A, SPoint<T> B, SPoint<T> C, SPoint<T> D) {
        return null;
    }

    /**
     * Compares the two parallel vector(if they are not parallel, then the result is {@code -1}). Considering {@code v1} is
     * the direct toward which is positive, compares the two vector of their length. Returns {@code -1} if {@code v1<v2},
     * {@code 0} if {@code v1==v2}, and {@code 1} if {@code v1>v2}. If the two vectors are of different direction, then
     *  {@code -1} will always be returned.
     * @param v1
     * @param v2
     * @return
     */
    public static <T> int compareVector(SVector<T> v1, SVector<T> v2) {
        MathCalculator<T> mc = (MathCalculator<T>) v1.getCalculator();
        T t1 = v1.x, t2 = v2.x;
        if (mc.isZero(t1)) {
            if (mc.isZero(v2.y)) {
                t1 = v1.z;
                t2 = v2.z;
            } else {
                t1 = v1.y;
                t2 = v2.y;
            }
        }
        if (!Calculators.isSameSign(t1, t2, mc)) {
            return -1;
        }
        int comp = mc.compare(t1, t2);
        int signum = Calculators.signum(t1, mc);
        return signum > 0 ? comp : -comp;
    }
}
