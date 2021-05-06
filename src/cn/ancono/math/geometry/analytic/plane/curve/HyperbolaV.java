package cn.ancono.math.geometry.analytic.plane.curve;

import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.equation.SVPEquation.QEquation;
import cn.ancono.math.exceptions.UnsupportedCalculationException;
import cn.ancono.math.geometry.analytic.plane.Line;
import cn.ancono.math.geometry.analytic.plane.Point;
import cn.ancono.math.numberModels.api.RealCalculator;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * A hyperbola is a special kind of conic section,it is a set of point that
 * <pre>{p|d(p,F1)-d(p,F2)=2a}</pre>,where {@code a} is a constant and is equal to a half of the length of major axis.
 * <p>
 * In this class of hyperbola,the hyperbola's center is always at the point O(0,0),and its foci are
 * on either coordinate axis X or Y. The reason why the hyperbola is set like this is to reduce
 * the calculation.
 * <p>
 * In an hyperbola,there are two foci,we usually call them {@code F1,F2},
 * and the distance between the two foci is equal to {@code 2c}.
 * The value {@code 2a} is equal to the length of transverse axis,
 * value {@code 2b} that {@code a^2+b^2=c^2} is the length of the conjugate axis.
 * There is also a boolean value to indicate whether the hyperbola's foci are on X axis or Y axis.
 * <p>
 * The standard equation of this ellipse is
 * <pre>x^2/a^2 - y^2/b^2 = 1</pre>
 * or
 * <pre>x^2/b^2 - y^2/a^2 = -1</pre>
 * determined by whether foci are on X axis.
 *
 * @author lyc
 */
public final class HyperbolaV<T> extends EHSection<T> {

    private List<Line<T>> asys;

    //onX only indicates the hyperbola's foci,a for x and b for y does not change.

    protected HyperbolaV(RealCalculator<T> mc, T A, T C, T a, T b, T c, T a2, T b2, T c2, boolean onX) {
        super(mc, A, C, a, b, c, a2, b2, c2, onX);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.geometry.analytic.planeAG.ConicSection#getType()
     */
    @Override
    public cn.ancono.math.geometry.analytic.plane.curve.ConicSection.Type determineType() {
        return ConicSection.Type.HYPERBOLA;
    }

    @Override
    public List<Point<T>> vertices() {
        //points :
        List<Point<T>> list = new ArrayList<>();
        if (onX) {
            list.add(Point.valueOf(getMc().negate(a), getMc().getZero(), getMc()));
            list.add(Point.valueOf(a, getMc().getZero(), getMc()));
        } else {
            list.add(Point.valueOf(getMc().getZero(), getMc().negate(b), getMc()));
            list.add(Point.valueOf(getMc().getZero(), b, getMc()));
        }
        return list;
    }

    @Override
    public T substitute(T x, T y) {
        //Ax^2 / a^2 + By^2 + F
        T re = getMc().multiply(A, square(x));
        re = getMc().add(re, getMc().multiply(C, square(y)));
        return getMc().add(re, F);
    }

    /**
     * Returns the conjugate hyperbola of this.
     *
     * @return a hyperbola
     */
    public HyperbolaV<T> conjugateHyperbola() {
        return new HyperbolaV<>(getMc(), getMc().negate(A), getMc().negate(C), a, b, c, a2, b2, c2, !onX);
    }

    /**
     * Returns the two asymptotes of this hyperbola.
     *
     * @return a list of lines.
     */
    public List<Line<T>> asymptote() {
        if (asys == null) {
            T zero = getMc().getZero();
            asys = new ArrayList<>(2);
            asys.add(Line.pointDirection(zero, zero, a, b, getMc()));
            asys.add(Line.pointDirection(zero, zero, a, getMc().negate(b), getMc()));
        }
        return asys;
    }

    /**
     * Determines whether the given line is parallel to one of the asymptotes,which means
     * this line has only one intersect point with {@code this}.
     *
     * @return {@code true} if the given line is parallel to one of the asymptotes.
     */
    public boolean isParallelAsymptote(Line<T> line) {
        for (Line<T> l : asymptote()) {
            if (l.isParallel(line)) {
                return true;
            }
        }
        return false;
    }

    private void checkOn(Point<T> p) {
        if (!contains(p)) {
            throw new IllegalArgumentException("point not on hyperbola");
        }
    }

    @Override
    public T focusDL(Point<T> p) {
        checkOn(p);
        if (onX) {
            return getMc().abs(getMc().add(a, getMc().multiply(getEccentricity(), p.x)));
        } else {
            return getMc().abs(getMc().add(b, getMc().multiply(getEccentricity(), p.y)));
        }
    }

    @Override
    public T focuseDR(Point<T> p) {
        checkOn(p);
        if (onX) {
            return getMc().abs(getMc().subtract(a, getMc().multiply(getEccentricity(), p.x)));
        } else {
            return getMc().abs(getMc().subtract(b, getMc().multiply(getEccentricity(), p.y)));
        }
    }

    @Override
    public T computeX(T y) {
        T t = onX ? b2 : getMc().negate(b2);
        T re = getMc().divide(a, b);
        return getMc().multiply(re, getMc().squareRoot(getMc().add(t, square(y))));
    }

    @Override
    public T computeY(T x) {
        T t = onX ? getMc().negate(b2) : b2;
        T re = getMc().divide(b, a);
        return getMc().multiply(re, getMc().squareRoot(getMc().add(t, square(x))));
    }

    /**
     * Returns the relation of the line to this hyperbola.
     * <li>Returns -1 if  {@code line} doesn't intersect with this hyperbola.
     * <li>Returns 0 if {@code line} is parallel to one of the asymptotes(which means
     * it only intersect with {@code this} at one point.
     * <li>Returns 1 if {@code line} is a tangent line.
     * <li>Returns 2 if {@code line} intersects with this hyperbola at two points.
     *
     * @param line a line
     * @return an integer representing the relation.
     */
    public int relation(Line<T> line) {
        if (isParallelAsymptote(line)) {
            return 0;
        }
        if (line.slope() == null) {
            QEquation<T> equ = (QEquation<T>) createEquationY(line);
            int solv = equ.getNumberOfRoots();
            if (solv == 0) {
                return -1;
            }
            return solv;
        }

        QEquation<T> equa = (QEquation<T>) createEquationX(line);
        int ren = equa.getNumberOfRoots();
        if (ren == 0) {
            return -1;
        }
        return ren;
    }

    @Override
    public T chordLength(Line<T> line) {
        if (isParallelAsymptote(line)) {
            return null;
        }
        T k = line.slope();
        if (k == null) {
            QEquation<T> equ = (QEquation<T>) createEquationY(line);
            int re = 2;
            try {
                re = equ.getNumberOfRoots();
            } catch (UnsupportedCalculationException ex) {
                //ignore
            }
            if (re == 0) {
                return null;
            } else if (re == 1) {
                return getMc().getZero();
            }
            return equ.rootsSubtract();
        }
        QEquation<T> equ = (QEquation<T>) createEquationX(line);
        int re = 2;
        try {
            re = equ.getNumberOfRoots();
        } catch (UnsupportedCalculationException ex) {
            //ignore
        }
        if (re == 0) {
            return null;
        } else if (re == 1) {
            return getMc().getZero();
        }
        T len = getMc().squareRoot(getMc().add(getMc().getOne(), square(k)));
        return getMc().multiply(len, equ.rootsSubtract());
    }

    @Override
    public T chordLengthSq(Line<T> line) {
        if (isParallelAsymptote(line)) {
            return null;
        }
        T k = line.slope();
        if (k == null) {
            QEquation<T> equ = (QEquation<T>) createEquationY(line);
            int re = 2;
            try {
                re = equ.getNumberOfRoots();
            } catch (UnsupportedCalculationException ex) {
                //ignore
            }
            if (re == 0) {
                return null;
            } else if (re == 1) {
                return getMc().getZero();
            }
            return equ.rootsSubtractSq();
        }
        QEquation<T> equ = (QEquation<T>) createEquationX(line);
        int re = 2;
        try {
            re = equ.getNumberOfRoots();
        } catch (UnsupportedCalculationException ex) {
            //ignore
        }
        if (re == 0) {
            return null;
        } else if (re == 1) {
            return getMc().getZero();
        }
        T len = getMc().add(getMc().getOne(), square(k));
        return getMc().multiply(len, equ.rootsSubtractSq());
    }


    @Override
    public boolean valueEquals(@NotNull MathObject<T, RealCalculator<T>> obj) {
        if (obj instanceof HyperbolaV) {
            HyperbolaV<T> ev = (HyperbolaV<T>) obj;
            if (ev.onX == onX) {
                return getMc().isEqual(ev.a, a) && getMc().isEqual(ev.b, b);
            }
            return false;
        }
        return super.valueEquals(obj);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof HyperbolaV) {
            HyperbolaV<?> ev = (HyperbolaV<?>) obj;
            return ev.onX == onX && a.equals(ev.a) && b.equals(ev.b);
        }
        return false;
    }

    @NotNull
    @Override
    public <N> HyperbolaV<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
        HyperbolaV<N> nell = new HyperbolaV<N>((RealCalculator<N>) newCalculator, mapper.apply(A), mapper.apply(C)
                , mapper.apply(a), mapper.apply(b), mapper.apply(c)
                , mapper.apply(a2), mapper.apply(b2), mapper.apply(c2)
                , onX);


        nell.e = e == null ? null : mapper.apply(e);
        nell.f1 = f1 == null ? null : f1.mapTo(newCalculator, mapper);
        nell.f2 = f2 == null ? null : f2.mapTo(newCalculator, mapper);
        if (asys != null) {
            ArrayList<Line<N>> newList = new ArrayList<>(2);
            asys.forEach(l -> newList.add(l.mapTo(newCalculator, mapper)));
            nell.asys = newList;
        }
        return nell;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Hyperbola: x^2/(");
        sb.append(a2);
        sb.append(')').append(" - y^2/(");
        sb.append(b2);
        sb.append(") = ");
        if (!onX) {
            sb.append('-');
        }
        sb.append("1");
        return sb.toString();
    }

    private static <T> HyperbolaV<T> create0(T a, T b, T c, T a2, T b2, T c2, boolean onX, RealCalculator<T> mc) {
        T A, C;
        A = mc.reciprocal(a2);
        C = mc.reciprocal(b2);
        if (onX) {
            C = mc.negate(C);
        } else {
            A = mc.negate(A);
        }
        return new HyperbolaV<>(mc, A, C,
                a, b, c,
                a2, b2, c2,
                onX);
    }

    /**
     * Creates a hyperbola of
     * <pre>x^2/a^2 - y^2/b^2 = +-1</pre>,
     *
     * @param a   coefficient a
     * @param b   coefficient b
     * @param onX decides whether this Hyperbola should be on X axis.
     * @param mc  a {@link RealCalculator}
     * @return new HyperbolaV
     * @throws IllegalArgumentException if {@code a==b} or {@code a <= 0 || b <= 0}
     */
    public static <T> HyperbolaV<T> standardEquation(T a, T b, boolean onX, RealCalculator<T> mc) {
        T a2 = mc.multiply(a, a);
        T b2 = mc.multiply(b, b);
        T c2 = mc.add(a2, b2);
        T c = mc.squareRoot(c2);
        return create0(a, b, c, a2, b2, c2, onX, mc);
    }

    /**
     * Creates an hyperbolaV of
     * <pre>x^2/a2 - y^2/b2 = 1</pre> if {@code onX==true}
     * or <pre>y^2/a2 - x^2/b2 = 1</pre>
     *
     * @param a2  coefficient a2
     * @param b2  coefficient b2
     * @param onX decides whether this Hyperbola should be on X axis.
     * @param mc  a {@link RealCalculator}
     * @return new hyperbola
     */
    public static <T> HyperbolaV<T> standardEquationSqrt(T a2, T b2, boolean onX, RealCalculator<T> mc) {
        T c2 = mc.add(a2, b2);
        T a = mc.squareRoot(a2);
        T b = mc.squareRoot(b2);
        T c = mc.squareRoot(c2);
        if (onX) {
            return create0(a, b, c, a2, b2, c2, onX, mc);
        } else {
            return create0(b, a, c, b2, a2, c2, onX, mc);
        }
    }

}
