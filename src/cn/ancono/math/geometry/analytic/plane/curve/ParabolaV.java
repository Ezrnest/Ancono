/**
 *
 */
package cn.ancono.math.geometry.analytic.plane.curve;

import cn.ancono.math.geometry.analytic.plane.Line;
import cn.ancono.math.geometry.analytic.plane.PAffineTrans;
import cn.ancono.math.geometry.analytic.plane.Point;
import cn.ancono.math.geometry.analytic.plane.TransMatrix;
import cn.ancono.math.numberModels.api.RealCalculator;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * ParabolaV is a class for the simplest form of parabola: the vertex is (0,0) and 
 * the symmetry axis is either x axis or y axis. The general formula for this type of 
 * parabola can be written as 
 * {@code y^2 = 2px} or {@code x^2 = 2py}.
 *
 * @author liyicheng
 *
 */
public final class ParabolaV<T> extends ConicSection<T> {
    final T p;
    final boolean onX;

    /**
     * @param mc
     * @param A
     * @param B
     * @param C
     * @param D
     * @param E
     * @param F
     */
    protected ParabolaV(RealCalculator<T> mc, T A, T B, T C, T D, T E, T F, T p, boolean onX) {
        super(mc, A, B, C, D, E, F);
        this.p = p;
        this.onX = onX;
    }

    private Line<T> directrix;

    /**
     * Returns the directrix of this parabola, x = -p/2 or y = -p/2
     * @return
     */
    public Line<T> directrix() {
        if (directrix == null) {
            T p_2 = getMc().divideLong(p, -2l);
            directrix = onX ? Line.parallelY(p_2, getMc()) : Line.parallelX(p_2, getMc());
        }
        return directrix;
    }

    private Point<T> focus;

    /**
     * Returns the focus of this parabola (p/2,0) or (0,p/2).
     * @return
     */
    public Point<T> focus() {
        if (focus == null) {
            T p_2 = getMc().divideLong(p, 2l), z = getMc().getZero();
            focus = Point.valueOf(onX ? p_2 : z, onX ? z : p_2, getMc());
        }
        return focus;
    }

    /**
     * Returns {@code this} because it is already the normalized form.
     */
    @Override
    public Pair<TransMatrix<T>, ConicSection<T>> normalizeAndTrans() {
        return new Pair<>(TransMatrix.identityTrans(getMc()), this);
    }

    /**
     * Returns {@code this} because it is already the normalized form.
     */
    @Override
    public ConicSection<T> normalize() {
        return this;
    }

    /**
     * Returns {@code this} because it is already the normalized form.
     */
    @Override
    public Pair<PAffineTrans<T>, ConicSection<T>> toStandardFormAndTrans() {
        return new Pair<>(PAffineTrans.identity(getMc()), this);
    }

    /**
     * Returns {@code this} because it is already the normalized form.
     */
    @Override
    public ConicSection<T> toStandardForm() {
        return this;
    }


    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    @NotNull
    @Override
    public <N> ParabolaV<N> mapTo(@NotNull RealCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new ParabolaV<>(newCalculator, mapper.apply(A), mapper.apply(B),
                mapper.apply(C), mapper.apply(D), mapper.apply(E), mapper.apply(F), mapper.apply(p), onX);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.geometry.analytic.planeAG.ConicSection#getType()
     */
    @Override
    public cn.ancono.math.geometry.analytic.plane.curve.ConicSection.Type determineType() {
        return ConicSection.Type.PARABOLA;
    }

    /**
     * Returns y^2 - 2px = 0 if {@code onX}
     * or x^2 - 2py = 0.
     *
     * @param p
     * @param onX
     * @param mc
     * @return
     */
    static <T> ParabolaV<T> generalFormla0(T p, boolean onX, RealCalculator<T> mc) {
        T zero = mc.getZero(), one = mc.getOne();
        T A = onX ? zero : one;
        T C = onX ? one : zero;
        T D = onX ? mc.multiplyLong(p, -2l) : zero;
        T E = onX ? zero : mc.multiplyLong(p, -2l);
        return new ParabolaV<>(mc, A, zero, C, D, E, zero, p, onX);
    }

    /**
     * Returns a parabola: {@code y^2 - 2px = 0} if {@code onX}
     * or otherwise {@code x^2 - 2py = 0}
     *
     * @param p
     * @param onX
     * @param mc
     * @return
     */
    public static <T> ParabolaV<T> generalFormula(T p, boolean onX, RealCalculator<T> mc) {
        if (mc.isZero(p)) {
            throw new IllegalArgumentException("p==0");
        }
        return generalFormla0(p, onX, mc);
    }

}
