/**
 *
 */
package cn.ancono.math.function;


import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.equation.SVPEquation;
import cn.ancono.math.equation.SVPEquation.QEquation;
import cn.ancono.math.geometry.analytic.plane.Line;
import cn.ancono.math.geometry.analytic.plane.Point;
import cn.ancono.math.geometry.analytic.plane.curve.AbstractPlaneFunction;
import cn.ancono.math.geometry.analytic.plane.curve.ConicSection;
import cn.ancono.math.geometry.analytic.plane.curve.GeneralConicSection;
import cn.ancono.math.numberModels.ComputeExpression;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Describes function {@code f(x) = ax^2+bx+c (a!=0)}.
 * @author liyicheng
 *
 */
public final class QuadraticFunction<T> extends AbstractPlaneFunction<T> implements SVPFunction<T> {
    private final T a, b, c;

    /**
     * @param mc
     */
    QuadraticFunction(MathCalculator<T> mc, T a, T b, T c) {
        super(mc);
        if (mc.isZero(a)) {
            throw new IllegalArgumentException("a==0");
        }
        this.a = a;
        this.b = b;
        this.c = c;
    }

    private static final ComputeExpression EXPR_APPLY = ComputeExpression.compile("($1$0+$2)$0+$3");

    /* (non-Javadoc)
     * @see cn.ancono.math.geometry.analytic.planeAG.curve.PlaneFunction#apply(java.lang.Object)
     */
    @NotNull
    @Override
    public T apply(@NotNull T x) {
        //(ax+b)x+c
        return EXPR_APPLY.compute(getMc(), x, a, b, c);
    }

    private Point<T> climax;
    private static final ComputeExpression EXPR_CLIMAXY = ComputeExpression.compile("(4$0$2-$1*$1)/$0/4");

    public Point<T> climax() {
        if (climax == null) {
            //-b/2a , (4ac-b^2)/4a
            climax = Point.valueOf(getMc().divideLong(getMc().divide(b, a), -2l), EXPR_CLIMAXY.compute(getMc(), a, b, c), getMc());
        }
        return climax;
    }

    /**
     * Determines whether the quadratic function has a minimum value or a maximum value.
     * @return
     */
    public boolean hasMinValue() {
        return getMc().compare(a, getMc().getZero()) > 0;
    }

    /**
     * Gets the coefficient a.
     * @return
     */
    public T getA() {
        return a;
    }

    /**
     * Gets the coefficient a.
     * @return
     */
    public T getB() {
        return b;
    }

    /**
     * Gets the coefficient a.
     * @return
     */
    public T getC() {
        return c;
    }

    /**
     * Returns the tangent line of passes the point (x,f(x))
     * @param x x coordinate of the point
     * @return
     */
    public Line<T> tangentLine(T x) {
        //k = 2ax0+b
        T k = getMc().add(getMc().multiplyLong(getMc().multiply(a, x), 2l), b);
        return Line.pointSlope(getPoint(x), k, getMc());
    }


    /**
     * Converts the quadratic function to an equation.
     * @return
     */
    public QEquation<T> toEquation() {
        return SVPEquation.quadratic(a, b, c, getMc());
    }

    /**
     * Returns a ConicSection representing this function.
     * @return
     */
    public ConicSection<T> toConicSection() {
        return GeneralConicSection.generalFormula(getMc(), a, null, null, b, getMc().negate(getMc().getOne()), c);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.number_models.MathCalculator)
     */
    @NotNull
    @Override
    public <N> QuadraticFunction<N> mapTo(@NotNull MathCalculator<N> newCalculator, @NotNull Function<T, N> mapper) {
        return new QuadraticFunction<N>(newCalculator, mapper.apply(a), mapper.apply(b), mapper.apply(c));
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj instanceof QuadraticFunction) {
            QuadraticFunction<?> q = (QuadraticFunction<?>) obj;
            return getMc().equals(q.getMc()) && a.equals(q.a) && b.equals(q.b) && c.equals(q.c);
        }
        return false;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#hashCode()
     */
    @Override
    public int hashCode() {
        int hash = getMc().hashCode();
        hash = hash * 31 + a.hashCode();
        hash = hash * 31 + b.hashCode();
        hash = hash * 31 + c.hashCode();
        return hash;
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */

    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SVPFunction)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        SVPFunction<T> f = (SVPFunction<T>) obj;
        return SVPFunction.isEqual(this, f, getMc()::isEqual);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject, java.util.function.Function)
     */
    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SVPFunction)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        SVPFunction<N> f = (SVPFunction<N>) obj;
        return SVPFunction.isEqual(this, f, (x, y) -> getMc().isEqual(x, mapper.apply(y)));
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.function.SVPFunction#getCoefficient(int)
     */
    @Override
    public T get(int n) {
        switch (n) {
            case 0:
                return c;
            case 1:
                return b;
            case 2:
                return a;
        }
        throw new IndexOutOfBoundsException("For n=" + n);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.function.SVPFunction#getMaxPower()
     */
    @Override
    public int getDegree() {
        return 2;
    }

    /**
     * Returns a new quadratic function.
     * @param a
     * @param b
     * @param c
     * @param mc
     * @return
     */
    public static <T> QuadraticFunction<T> generalFormula(T a, T b, T c, MathCalculator<T> mc) {
        return new QuadraticFunction<T>(mc, a, b, c);
    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
     */
    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T> nf) {
        StringBuilder sb = new StringBuilder();
        for (int i = 2; i > 0; i--) {
            if (getMc().isZero(get(i)))
                continue;
            sb.append(nf.format(get(i))).append("*x^").append(i).append(" + ");
        }
        if (getMc().isZero(get(0)) == false) {
            sb.append(nf.format(get(0)));
        } else {
            sb.delete(sb.length() - 3, sb.length());
        }
        return sb.toString();
    }


}
