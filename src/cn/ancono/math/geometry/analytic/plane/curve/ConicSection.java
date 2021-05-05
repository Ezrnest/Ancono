package cn.ancono.math.geometry.analytic.plane.curve;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.equation.SVPEquation;
import cn.ancono.math.equation.SVPEquation.LEquation;
import cn.ancono.math.equation.SVPEquation.QEquation;
import cn.ancono.math.exceptions.UnsupportedCalculationException;
import cn.ancono.math.geometry.analytic.plane.*;
import cn.ancono.math.numberModels.ComputeExpression;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.math.numberModels.api.Simplifiable;
import cn.ancono.math.numberModels.api.Simplifier;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Conic section is a set of curves that can be described with the equation
 * <pre>
 * Ax^2 + Bxy + Cy^2 + Dx + Ey + F = 0
 * </pre>
 * For instance, {@link Circle} is a kind of conic section.
 *
 * @author lyc
 * @see GeneralConicSection
 * @see Circle
 * @see EllipseV
 * @see HyperbolaV
 */
public abstract class
ConicSection<T>
        extends AbstractPlaneCurve<T>
        implements Simplifiable<T, ConicSection<T>>, SubstituableCurve<T> {
    /**
     * The coefficients
     */
    protected final T A, B, C, D, E, F;

    /**
     * Describes the basic types of the Conic Section. All the
     * conic can be classified into one of this types, and can
     * be transformed to the standard formula of a specific type
     * through rotating and translating.
     *
     * @author liyicheng
     */
    public enum Type {
        /**
         * An ellipse, whose standard formula is {@literal x^2/a^2 + y^2/b^2 - 1 = 0}.
         */
        ELLIPSE,
        /**
         * An imaginary ellipse, whose standard formula is {@literal x^2/a^2 + y^2/b^2 + 1 = 0}.
         */
        IMAGINARY_ELLIPSE,
        /**
         * A point, whose standard formula is {@literal x^2/a^2 + y^2/b^2 = 0}.
         */
        POINT,
        /**
         * A hyperbola, whose standard formula is {@literal x^2/a^2 - y^2/b^2 - 1 = 0}, or {@literal -x^2/a^2 + y^2/b^2 - 1 = 0}.
         */
        HYPERBOLA,
        /**
         * A pair of intersect lines, whose standard formula is {@literal x^2/a^2 - y^2/b^2 = 0}
         */
        INTERSECT_LINE,
        /**
         * A parabola, whose standard formula is {@literal y^2 - 2px = 0} or {@literal x^2 - 2py = 0}.
         */
        PARABOLA,
        /**
         * A pair of parallel lines, whose standard formula is {@literal x^2 - a^2 = 0}, or {@literal x^2 + a^2 = 0}.
         */
        PARALLEL_LINE,
        /**
         * A pair of imaginary parallel lines, whose standard formula is {@literal x^2 + a^2 = 0}, or {@literal y^2 + a^2 = 0}.
         */
        IMAGINARY_PARALLEL_LINE,
        /**
         * A pair of coincide lines, whose standard formula is {@literal x^2 = 0}, or  {@literal y^2 = 0}.
         */
        CONCIDE_LINE
    }

    /**
     * To create a conic section,the following coefficient must be given.
     *
     * @param mc
     * @param A
     * @param B
     * @param C
     * @param D
     * @param E
     * @param F
     */
    protected ConicSection(MathCalculator<T> mc, T A, T B, T C, T D, T E, T F) {
        super(mc);
        if (mc.isZero(A) && mc.isZero(C) && mc.isZero(B)) {
            throw new IllegalArgumentException("A=B=C=0 for conic section");
        }
        this.A = requireNonNull(A);
        this.B = requireNonNull(B);
        this.C = requireNonNull(C);
        this.D = requireNonNull(D);
        this.E = requireNonNull(E);
        this.F = requireNonNull(F);
    }

    /**
     * Gets the coefficient A.
     *
     * @return the a
     */
    public T getA() {
        return A;
    }

    /**
     * Gets the coefficient B.
     *
     * @return the b
     */
    public T getB() {
        return B;
    }

    /**
     * Gets the coefficient C.
     *
     * @return the c
     */
    public T getC() {
        return C;
    }

    /**
     * Gets the coefficient D.
     *
     * @return the d
     */
    public T getD() {
        return D;
    }

    /**
     * Gets the coefficient E.
     *
     * @return the e
     */
    public T getE() {
        return E;
    }

    /**
     * Gets the coefficient F.
     *
     * @return the f
     */
    public T getF() {
        return F;
    }

    /**
     * Returns a list that contains all the coefficients.The order of coefficient is A,B,C,D,E,F.
     * The list is modifiable.
     *
     * @return a coefficient list.
     */
    public List<T> getCoefficients() {
        List<T> list = new ArrayList<T>(6);
        list.add(A);
        list.add(B);
        list.add(C);
        list.add(D);
        list.add(E);
        list.add(F);
        return list;
    }

    @Override
    public T substitute(T x, T y) {
        T re = getMc().multiply(A, getMc().multiply(x, x));
        re = getMc().add(re, getMc().multiply(B, getMc().multiply(x, y)));
        re = getMc().add(re, getMc().multiply(C, getMc().multiply(y, y)));
        re = getMc().add(re, getMc().multiply(D, x));
        re = getMc().add(re, getMc().multiply(E, y));
        re = getMc().add(re, F);
        return re;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.geometry.analytic.planeAG.PlaneCurve#contains(cn.ancono.math.geometry.analytic.planeAG.Point)
     */
    @Override
    public boolean contains(Point<T> p) {
        return getMc().isZero(substitute(p.x, p.y));
    }

    /**
     * Returns the polar line corresponding to the given point. The point must not be the
     * <i>center</i> of this conic section, and if so {@code null} will be returned.
     * <br>
     * The polar can be described as :
     * <ul>
     * <li>If the point is on this conic section, then it will be the tangent line.
     * <li>If two tangent lines that pass the point {@code p} exist, the the result will
     * be the line connecting the two tangency points.
     * <li>Otherwise, for each secant line that passes {@code p},
     * there are two intersect points with this conic section,
     * and the all intersect point of the two tangent lines from the two
     * intersect points will be on <b>the identity line</b>, which is the result.
     * </ul>
     *
     * @param p a point.
     * @return the polar line, or {@code null}
     */
    public Line<T> polarLine(Point<T> p) {
        T a = getMc().add(getMc().multiply(getMc().add(A, getMc().divideLong(B, 2)), p.x), getMc().divideLong(D, 2));
        T b = getMc().add(getMc().multiply(getMc().add(C, getMc().divideLong(B, 2)), p.y), getMc().divideLong(E, 2));
        T c = getMc().add(getMc().divideLong(getMc().multiply(D, p.x), 2),
                getMc().add(getMc().divideLong(getMc().multiply(E, p.y), 2), F));
        if (getMc().isZero(a) && getMc().isZero(b)) {
            return null;
        }
        return Line.generalFormula(a, b, c, getMc());
    }


    /**
     * Returns the tangent line of this conic section from point {@code p}.The point must be on this
     * conic section.
     *
     * @param p
     * @return the tangent line.
     * @throws IllegalArgumentException if {@code p} is not on this conic section.
     */
    public Line<T> tangentLine(Point<T> p) {
        if (!contains(p)) {
            throw new IllegalArgumentException("Not contain point:" + p);
        }
        return polarLine(p);

    }

    /**
     * Returns the corresponding polar point. This method will check whether the line can actually
     * be a polar line, throw {@link IllegalArgumentException} if it is invalid.
     *
     * @param l a line
     * @return the corresponding polar line.
     * @see #polarLine(Point)
     */
    public Point<T> polarPoint(Line<T> l) {
        T x = getMc().divide(getMc().subtract(getMc().multiplyLong(l.getA(), 2l), D), getMc().subtract(getMc().multiplyLong(A, 2l), B));
        T y = getMc().divide(getMc().subtract(getMc().multiplyLong(l.getB(), 2l), E), getMc().subtract(getMc().multiplyLong(C, 2l), B));
        //check valid
        Point<T> p = Point.valueOf(x, y, getMc());
        Line<T> l2 = polarLine(p);
        if (l2 != null && l2.valueEquals(l)) {
            return p;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Create the equation of {@code x} from the line and this conic section.this method
     * requires that {@code line} is not parallel to Y axis.<p>
     * This method will return either a {@link QEquation} or {@link LEquation},decided by
     * whether the coefficient of x^2 is equal to zero,use {@link SVPEquation#getDegree()} to
     * identify it.
     *
     * @param line a line.
     * @return an equation of {@code x} indicating the two intersect points of the line
     * and this ellipse,or {@code null}.
     * @throws IllegalArgumentException if k == null
     */
    public SVPEquation<T> createEquationX(Line<T> line) {
        T k = line.slope();
        if (k == null) {
            throw new IllegalArgumentException("k==null");
        }
        T b = line.getInterceptY();
        //(A +Bk+Ck^2)
        T ca = getMc().add(A, getMc().multiply(k, getMc().add(B, getMc().multiply(C, k))));
        // Bb + 2kbc + Ek + D
        T cb = getMc().add(D, getMc().add(getMc().multiply(b, getMc().add(B, getMc().multiply(k, C))), getMc().multiply(k, getMc().add(getMc().multiply(b, C), E))));
        //cb^2 + Eb + F
        T cc = getMc().add(F, getMc().multiply(b, getMc().add(getMc().multiply(C, b), E)));
        if (getMc().isZero(ca)) {
//			throw new ArithmeticException("a = 0");
            return SVPEquation.linear(cb, cc, getMc());
        }
        return SVPEquation.quadratic(ca, cb, cc, getMc());
    }

    /**
     * Create the equation of {@code y} from the line and this conic section.this method
     * requires that {@code line} is not parallel to X axis.<p>
     * This method will return either a {@link QEquation} or {@link LEquation},decided by
     * whether the coefficient of y^2 is equal to zero,use {@link SVPEquation#getDegree()} to
     * identify it.
     *
     * @param line a line.
     * @return an equation of {@code y} indicating the two intersect points of the line
     * and this ellipse,or {@code null}.
     * @throws IllegalArgumentException if k == 0
     */
    public SVPEquation<T> createEquationY(Line<T> line) {
        T at = line.getA();
        if (getMc().isZero(at)) {
            throw new IllegalArgumentException("k==0");
        }
        T bt = line.getB();
        T k = getMc().negate(getMc().divide(bt, at));
        // solve an equation:
        T b = line.getInterceptX();
        //(C +Bk+Ak^2)
        T ca = getMc().add(C, getMc().multiply(k, getMc().add(B, getMc().multiply(A, k))));
        // Bb + 2kbc + Ek + D
        T cb = getMc().add(E, getMc().add(getMc().multiply(b, getMc().add(B, getMc().multiply(k, A))), getMc().multiply(k, getMc().add(getMc().multiply(b, A), D))));
        //cb^2 + Eb + F
        T cc = getMc().add(F, getMc().multiply(b, getMc().add(getMc().multiply(A, b), D)));
        if (getMc().isZero(ca)) {
//			throw new ArithmeticException("a = 0");
            return SVPEquation.linear(cb, cc, getMc());
        }
        return SVPEquation.quadratic(ca, cb, cc, getMc());
    }

    /**
     * Computes the intersect points with the given line.
     *
     * @param line
     * @return
     */
    public List<Point<T>> intersectPoints(Line<T> line) {
        SVPEquation<T> equa = null;
        try {
            equa = createEquationX(line);
        } catch (IllegalArgumentException ex) {
        }
        if (equa != null) {
            if (equa.getDegree() == 1) {
                List<Point<T>> ps = new ArrayList<>(1);
                T x = ((LEquation<T>) equa).solution();
                T y = line.computeY(x);
                ps.add(new Point<>(getMc(), x, y));
                return ps;
            } else {
                QEquation<T> eq = (QEquation<T>) equa;
                int ren = 2;
                try {
                    ren = eq.getNumberOfRoots();
                } catch (UnsupportedCalculationException ece) {

                }
                switch (ren) {
                    case 0:
                        return Collections.emptyList();
                    case 1: {
                        List<Point<T>> ps = new ArrayList<>(1);
                        T x = eq.solve().get(0);
                        ps.add(new Point<>(getMc(), x, line.computeY(x)));
                        return ps;
                    }
                    default: {
                        List<Point<T>> ps = new ArrayList<>(2);
                        for (T x : eq.solve()) {
                            ps.add(new Point<>(getMc(), x, line.computeY(x)));
                        }
                        return ps;
                    }
                }
            }
        } else {
            equa = createEquationY(line);
            if (equa.getDegree() == 1) {
                List<Point<T>> ps = new ArrayList<>(1);
                T y = ((LEquation<T>) equa).solution();
                T x = line.computeX(y);
                ps.add(new Point<>(getMc(), x, y));
                return ps;
            } else {
                QEquation<T> eq = (QEquation<T>) equa;
                int ren = eq.getNumberOfRoots();
                switch (ren) {
                    case 0:
                        return Collections.emptyList();
                    case 1: {
                        List<Point<T>> ps = new ArrayList<>(1);
                        T y = eq.solve().get(0);
                        ps.add(new Point<>(getMc(), line.computeX(y), y));
                        return ps;
                    }
                    default: {
                        List<Point<T>> ps = new ArrayList<>(2);
                        for (T y : eq.solve()) {
                            ps.add(new Point<>(getMc(), line.computeX(y), y));
                        }
                        return ps;
                    }
                }
            }
        }


    }

    /**
     * Returns the another intersect point of the line and this conic section. Returns null if
     * there the line only intersect with this conic section at one point.
     *
     * @param p
     * @param line
     * @return
     */
    public Point<T> intersectPointAnother(Point<T> p, Line<T> line) {
        if ((!line.contains(p)) || (!contains(p))) {
            throw new IllegalArgumentException();
        }
        SVPEquation<T> equa = null;
        boolean equationOfX;
        //if line is Ax - C = 0, then the slope doesn't exist,
        //so create the equation with y.
        if (getMc().isZero(line.getB())) {
            equationOfX = false;
            equa = createEquationY(line);
        } else {
            equationOfX = true;
            equa = createEquationX(line);
        }
        if (equa.getDegree() == 1) {
            return null;
        }
        QEquation<T> eq = (QEquation<T>) equa;
        int degree = 2;
        try {
            degree = eq.getNumberOfRoots();
        } catch (UnsupportedCalculationException ece) {
        }
        if (degree < 2) {
            return null;
        }
        T x, y;
        if (equationOfX) {
            x = getMc().subtract(eq.rootsSum(), p.x);
            y = line.computeY(x);
        } else {
            y = getMc().subtract(eq.rootsSum(), p.y);
            x = line.computeX(y);
        }
        return Point.valueOf(x, y, getMc());

    }

    /**
     * Performs transform:
     * <pre>
     * x = vx.x*x' + vx.y * y'
     * y = vy.x*x' + vy.y * y'
     * </pre>
     *
     * @param vx
     * @param vy
     * @return
     */
    public ConicSection<T> transform(PVector<T> vx, PVector<T> vy) {
        T a = vx.getX(), b = vx.getY(),
                c = vy.getX(), d = vy.getY();
        return transform0(a, b, c, d);
    }

    GeneralConicSection<T> transform0(T a, T b, T c, T d) {
        T _A = getMc().add(getMc().add(getMc().multiply(A, getMc().multiply(a, a)), getMc().multiply(B, getMc().multiply(a, c))), getMc().multiply(C, getMc().multiply(c, c)));
        T _C = getMc().add(getMc().add(getMc().multiply(A, getMc().multiply(b, b)), getMc().multiply(B, getMc().multiply(b, d))), getMc().multiply(C, getMc().multiply(d, d)));
        T _B = getMc().add(getMc().add(getMc().multiply(getMc().multiply(B, a), d), getMc().multiply(B, getMc().multiply(b, c))),
                getMc().add(getMc().multiplyLong(getMc().multiply(A, getMc().multiply(a, b)), 2l),
                        getMc().multiplyLong(getMc().multiply(C, getMc().multiply(c, d)), 2l)));
        if (getMc().isZero(_A) && getMc().isZero(_C) && getMc().isZero(_B)) {
            throw new IllegalArgumentException("A=B=C=0");
        }
        T _D = getMc().add(getMc().multiply(E, c), getMc().multiply(D, a));
        T _E = getMc().add(getMc().multiply(E, d), getMc().multiply(D, b));
        return new GeneralConicSection<T>(getMc(), _A, _B, _C, _D, _E, F);
    }

    /**
     * Returns the transformed formula of the conic section. This operation
     * is a transformation of coordinate:
     * <pre>tmat * (x,y)<sup>T</sup> = (x',y')<sup>T</sup></pre>
     * And therefore:
     * <pre>tmat<sup>-1</sup>*(x',y')<sup>T</sup> = (x,y)<sup>T</sup>
     * </pre>
     *
     * @param tmat
     * @return
     */
    public ConicSection<T> transform(TransMatrix<T> tmat) {
        if (tmat.getRow() != 2 || tmat.getColumn() != 2) {
            throw new IllegalArgumentException("Invalid matrix size!");
        }
        //compute inverse
        tmat = tmat.inverse();
        return transform0(tmat.get(0, 0), tmat.get(0, 1), tmat.get(1, 0), tmat.get(1, 1));
    }

    /**
     * Performs a translation operation, (moves the conic section toward).
     *
     * @param d
     * @param doX determines whether to move along x axis.
     * @return
     */
    public ConicSection<T> translate(T d, boolean doX) {
        return translate(PVector.valueOf(doX ? d : getMc().getZero(), doX ? getMc().getZero() : d, getMc()));
    }

    /**
     * Performs a translation operation,(moves the conic section toward).
     * If point A(x,y) is on this conic section, after this operation,
     * point A'(x+v.x,y+v.y) will be on this conic section.
     *
     * @param v
     * @return
     */
    public ConicSection<T> translate(PVector<T> v) {
        //x: D-2*Aa-Bb
        //y: E-2*Cb-Ba
        //F: Aa^2+Bab+Cb^2-Da-Eb+F
        T x = v.getX(), y = v.getY();
        T _D = getMc().subtract(D, getMc().add(getMc().multiplyLong(getMc().multiply(A, x), 2l), getMc().multiply(B, y)));
        T _E = getMc().subtract(D, getMc().add(getMc().multiplyLong(getMc().multiply(C, y), 2l), getMc().multiply(B, x)));
        T _F = expr1.compute(getMc(), A, B, C, D, E, F, x, y);
        return GeneralConicSection.generalFormula(A, B, C, _D, _E, _F, getMc());
    }

    private static final ComputeExpression expr1 = ComputeExpression.compile("$0$6^2+$1$6$7+$2$7^2-$3$6-$4$7+$5");

    /* (non-Javadoc)
     * @see cn.ancono.math.geometry.analytic.planeAG.curve.AbstractPlaneCurve#transform(cn.ancono.math.geometry.analytic.planeAG.PAffineTrans)
     */
    @Override
    public ConicSection<T> transform(PAffineTrans<T> trans) {
        return transform(trans.getMatrix()).translate(trans.getVector());
    }

    /**
     * Determines the type of this conic section. Returns {@code null} if the type cannot be determined.
     *
     * @return
     * @see Type
     */
    public abstract Type determineType();


    /**
     * Performs a rotation to make the coefficient of {@literal xy} to zero,
     * returns the result conic section.
     *
     * @return
     */
    public ConicSection<T> normalize() {
        return normalizeAndTrans().getSecond();
    }

    /**
     * Performs a rotation to make the coefficient of {@literal xy} to zero,
     * returns the transformation matrix and the result conic section.
     *
     * @return
     */
    public abstract Pair<TransMatrix<T>, ConicSection<T>> normalizeAndTrans();

    /**
     * Transform this conic section to standard form:
     * <pre>Ax^2+Cy^2+F=0</pre>
     *
     * @return
     */
    public ConicSection<T> toStandardForm() {
        return toStandardFormAndTrans().getSecond();
    }

    /**
     * Transform this conic section to standard form, and returns the transformation performed and the standard form.
     * <pre>Ax^2+Cy^2+F=0</pre>
     *
     * @return
     */
    public abstract Pair<PAffineTrans<T>, ConicSection<T>> toStandardFormAndTrans();


    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (obj instanceof ConicSection) {
            ConicSection<T> cs = (ConicSection<T>) obj;
            T p;
            if (getMc().isZero(A)) {
                //use the
                p = getMc().divide(cs.C, C);
            } else {
                p = getMc().divide(cs.A, A);
            }
            if (getMc().isZero(p)) {
                return false;
            }

            return getMc().isEqual(getMc().multiply(A, p), cs.A) &&
                    getMc().isEqual(getMc().multiply(B, p), cs.B) &&
                    getMc().isEqual(getMc().multiply(C, p), cs.C) &&
                    getMc().isEqual(getMc().multiply(D, p), cs.D) &&
                    getMc().isEqual(getMc().multiply(E, p), cs.E) &&
                    getMc().isEqual(getMc().multiply(F, p), cs.F);
        }
        return false;
    }

    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (obj instanceof ConicSection) {
            ConicSection<N> cs = (ConicSection<N>) obj;
            T p;
            if (getMc().isZero(A)) {
                //use the
                p = getMc().divide(mapper.apply(cs.C), C);
            } else {
                p = getMc().divide(mapper.apply(cs.A), A);
            }
            if (getMc().isZero(p)) {
                return false;
            }

            return getMc().isEqual(getMc().multiply(A, p), mapper.apply(cs.A)) &&
                    getMc().isEqual(getMc().multiply(B, p), mapper.apply(cs.B)) &&
                    getMc().isEqual(getMc().multiply(C, p), mapper.apply(cs.C)) &&
                    getMc().isEqual(getMc().multiply(D, p), mapper.apply(cs.D)) &&
                    getMc().isEqual(getMc().multiply(E, p), mapper.apply(cs.E)) &&
                    getMc().isEqual(getMc().multiply(F, p), mapper.apply(cs.F));
        }
        return false;
    }

    /*
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + A.hashCode();
        result = prime * result + B.hashCode();
        result = prime * result + C.hashCode();
        result = prime * result + D.hashCode();
        result = prime * result + E.hashCode();
        result = prime * result + F.hashCode();
        return result;
    }

    /*
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ConicSection) {
            ConicSection<?> cs = (ConicSection<?>) obj;
            return A.equals(cs.A) &&
                    B.equals(cs.B) &&
                    C.equals(cs.C) &&
                    D.equals(cs.D) &&
                    E.equals(cs.E) &&
                    F.equals(cs.F);
        }
        return false;
    }

    @Override
    public ConicSection<T> simplify() {
        return this;

    }

    @Override
    public ConicSection<T> simplify(Simplifier<T> sim) {
        List<T> list = sim.simplify(getCoefficients());
        return GeneralConicSection.generalFormula(list, getMc());
    }

    /**
     * Returns the general formula.
     *
     * @param nf
     */
    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T> nf) {
        StringBuilder sb = new StringBuilder();
        final String str = " + ";
        sb.append("ConicSection : ");
        if (!getMc().isZero(A)) {
            sb.append('(').append(nf.format(A)).append(")x^2").append(str);
        }
        if (!getMc().isZero(B)) {
            sb.append('(').append(nf.format(B)).append(")xy").append(str);
        }
        if (!getMc().isZero(C)) {
            sb.append('(').append(nf.format(C)).append(")y^2").append(str);
        }
        if (!getMc().isZero(D)) {
            sb.append('(').append(nf.format(D)).append(")x").append(str);
        }
        if (!getMc().isZero(E)) {
            sb.append('(').append(nf.format(E)).append(")y").append(str);
        }
        if (!getMc().isZero(F)) {
            sb.append('(').append(nf.format(F)).append(")").append(str);
        }
        sb.delete(sb.length() - str.length() + 1, sb.length());
        sb.append("= 0");
        return sb.toString();
    }
//	public static void main(String[] args) {
//		PolynomialOld p = new PolynomialOld(FormulaCalculator.getCalculator(),
//				"Ax^2+Bxy+Cy^2+Dx+Ey+F");
//		PolyCalculator pc = PolyCalculator.DEFALUT_CALCULATOR;
//		p = pc.replace("x", p, PolynomialOld.valueOf("x-a"));
//		p = pc.replace("y", p, PolynomialOld.valueOf("y-b"));
//		print(p);
//		//x^2
//		BigDecimal TWO = BigDecimal.valueOf(2l),
//				ONE = BigDecimal.ONE,ZERO = BigDecimal.ZERO;
//		printnb("x^2: ");
//		p.getFormulaList().stream().filter(f -> TWO.equals(f.getCharacterPower("x"))).forEach(x -> printnb(x.removeChar("x")));
//		print();
//		printnb("y^2: ");
//		p.getFormulaList().stream().filter(f -> TWO.equals(f.getCharacterPower("y"))).forEach(x -> printnb(x.removeChar("y")));
//		print();
//		printnb("xy: ");
//		p.getFormulaList().stream().filter(f -> ONE.equals(f.getCharacterPower("x")) && ONE.equals(f.getCharacterPower("y")))
//		.forEach(x -> printnb(x.removeChar("y").removeChar("x")));
//		print();
//		printnb("x: ");
//		p.getFormulaList().stream().filter(f -> ONE.equals(f.getCharacterPower("x"))&& f.getCharacterPower("y")==null).forEach(x -> printnb(x.removeChar("x")));
//		print();
//		printnb("y: ");
//		p.getFormulaList().stream().filter(f -> ONE.equals(f.getCharacterPower("y"))&& f.getCharacterPower("x")==null).forEach(x -> printnb(x.removeChar("y")));
//		print();
//		printnb("constant: ");
//		p.getFormulaList().stream().filter(f -> f.getCharacterPower("y") == null && f.getCharacterPower("x")==null).forEach(x -> printnb(x));
//		print();
//		print(p);
//	}

}
