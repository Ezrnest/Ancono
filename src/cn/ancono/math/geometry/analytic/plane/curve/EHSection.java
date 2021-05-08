package cn.ancono.math.geometry.analytic.plane.curve;

import cn.ancono.math.geometry.analytic.plane.Line;
import cn.ancono.math.geometry.analytic.plane.PAffineTrans;
import cn.ancono.math.geometry.analytic.plane.Point;
import cn.ancono.math.geometry.analytic.plane.TransMatrix;
import cn.ancono.math.numberModels.api.RealCalculator;
import kotlin.Pair;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

;

/**
 * An abstract super class for Ellipse and Hyperbola
 *
 * @param <T>
 * @author lyc
 */
public abstract class EHSection<T> extends ConicSection<T> {
    protected final T a, b, c;
    protected final T a2, b2, c2;
    protected Point<T> f1, f2;
    protected final boolean onX;
    protected T e;

    protected EHSection(RealCalculator<T> mc, T A, T C
            , T a, T b, T c, T a2, T b2, T c2
            , boolean onX) {
        super(mc, A, mc.getZero(), C, mc.getZero(), mc.getZero(), mc.negate(mc.getOne()));
        this.a = requireNonNull(a);
        this.b = requireNonNull(b);
        this.c = requireNonNull(c);
        this.a2 = requireNonNull(a2);
        this.b2 = requireNonNull(b2);
        this.c2 = requireNonNull(c2);

        this.onX = onX;

    }

    /**
     * Gets the eccentricity.(c/a)
     *
     * @return eccentricity
     */
    public T getEccentricity() {
        if (e == null) {
            e = getMc().divide(c, a);
        }
        return e;
    }

    /**
     * Returns whether this ellipse/hyperbola has foci on X axis.
     *
     * @return {@code true} if this ellipse/hyperbola has foci on X axis.
     */
    public boolean isOnX() {
        return onX;
    }

    /**
     * Gets the coefficient {@code a}.
     *
     * @return the coefficient {@code a}
     */
    public T coeA() {
        return a;
    }

    /**
     * Gets the coefficient {@code b} of this ellipse/hyperbola.
     *
     * @return the coefficient {@code b}
     */
    public T coeB() {
        return b;
    }

    /**
     * Gets the coefficient {@code c} of this ellipse/hyperbola.
     *
     * @return the coefficient {@code c}
     */
    public T coeC() {
        return c;
    }

    /**
     * Gets the square of the coefficient {@code a} of this ellipse/hyperbola.
     *
     * @return the coefficient {@code a}
     */
    public T coeA2() {
        return a2;
    }

    /**
     * Gets the square of the coefficient {@code b} of this ellipse/hyperbola.
     *
     * @return the coefficient {@code b}
     */
    public T coeB2() {
        return b2;
    }

    /**
     * Gets the square of the coefficient {@code c} of this ellipse/hyperbola.
     *
     * @return the coefficient {@code c}
     */
    public T coeC2() {
        return c2;
    }

    /**
     * A so usually called square calculation.<p>
     * <b>Make sure the calculator has been initialized.</b>
     *
     * @param x
     * @return square of x
     */
    protected T square(T x) {
        return getMc().multiply(x, x);
    }

    /**
     * Gets the list of foci of this ellipse/hyperbola,
     *
     * @return a list of points.
     */
    public List<Point<T>> foci() {
        if (f1 == null) {
            T zero = getMc().getZero();
            if (onX) {
                f1 = new Point<>(getMc(), getMc().negate(c), zero);
                f2 = new Point<>(getMc(), c, zero);
            } else {
                f1 = new Point<>(getMc(), zero, getMc().negate(c));
                f2 = new Point<>(getMc(), zero, c);
            }
        }
        List<Point<T>> list = new ArrayList<>(2);
        list.add(f1);
        list.add(f2);
        return list;
    }

    /**
     * Return a list of vertices of this ellipse/hyperbola.
     *
     * @return a list of vertices
     */
    public abstract List<Point<T>> vertices();


    /**
     * Returns the two directrix of this ellipse.
     *
     * @return a list of lines.
     */
    public List<Line<T>> directrix() {
        T t = getMc().divide(a2, c);
        T tn = getMc().negate(t);
        List<Line<T>> list = new ArrayList<>(2);
        if (onX) {
            list.add(Line.parallelY(t, getMc()));
            list.add(Line.parallelY(tn, getMc()));
        } else {
            list.add(Line.parallelX(t, getMc()));
            list.add(Line.parallelX(tn, getMc()));
        }
        return list;
    }

    /**
     * Returns the point's distance to the left focus. This point must be on the curve.
     *
     * @param p a point
     * @return the distance
     */
    public abstract T focusDL(Point<T> p);

    /**
     * Returns the point's distance to the right focus. This point must be on the curve.
     *
     * @param p a point
     * @return the distance
     */
    public abstract T focuseDR(Point<T> p);

    /**
     * Computes the corresponding positive value x of the given value {@code y},
     * regardless of the range of y.
     * <p>This method may throw a {@link UnsupportedOperationException}.
     *
     * @param y y coordinate of a point.
     * @return the x coordinate.
     */
    public abstract T computeX(T y);

    /**
     * Computes the corresponding positive value y of the given value {@code x },
     * regardless of the range of x.
     * <p>This method may throw a {@link UnsupportedOperationException}.
     *
     * @param x x coordinate of a point.
     * @return the y coordinate.
     */
    public abstract T computeY(T x);

//	public abstract List<Point<T>> intersectPoints(Line<T> line);

    /**
     * Computes the length of the chord of the {@code line} and this ellipse/hyperbola.
     * If the line doesn't intersect with {@code this},{@code null} will be returned,and
     * if the line is a tangent line, 0 will be returned.For hyperbola,if the line only
     * intersect with {@code this} at one point,{@code null} will be returned too.
     *
     * @param line
     * @return the chord length.
     */
    public abstract T chordLength(Line<T> line);

    public abstract T chordLengthSq(Line<T> line);

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


    @Override
    public int hashCode() {
        int hash = onX ? 1 : -1;
        hash = hash * 37 + a.hashCode();
        hash = hash * 37 + b.hashCode();
        return hash;
    }
}
