package cn.ancono.math.equation;

import cn.ancono.math.IMathObject;
import cn.ancono.math.MathCalculatorHolder;
import cn.ancono.math.algebra.DecomposedPoly;
import cn.ancono.math.algebra.IPolynomial;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.function.AbstractSVPFunction;
import cn.ancono.math.numberModels.api.NumberFormatter;
import cn.ancono.math.numberModels.api.RealCalculator;
import cn.ancono.math.numberModels.api.Simplifiable;
import cn.ancono.math.numberModels.api.Simplifier;
import cn.ancono.math.numberModels.structure.Polynomial;
import cn.ancono.math.property.Solveable;
import cn.ancono.math.set.MathSets;
import cn.ancono.math.set.SingletonSet;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.function.Function;

;


/**
 * SVPEquation stands for <i>single variable polynomial equation</i>,which means that this
 * equation can be transformed to a polynomial function {@code f} that {@code f(x) = 0}.
 * Generally,the equation can be shown as
 * <pre>an*x^n + ... + a1*x + a0 = 0 , (an!=0,n>=0)</pre>
 *
 * @param <T>
 * @author lyc
 */
public abstract class SVPEquation<T> extends SVEquation<T>
        implements IPolynomial<T>, Simplifiable<T, SVPEquation<T>> {
    protected final int mp;

    protected SVPEquation(RealCalculator<T> mc, int mp) {
        super(mc);
        this.mp = mp;
    }

    /**
     * Returns the max power of x in this equation.
     *
     * @return an integer number indicates the max power.
     */
    public int getDegree() {
        return mp;
    }

    /*
     * @see cn.ancono.math.numberModels.api.Simplifiable#simplify()
     */
    @Override
    public SVPEquation<T> simplify() {
        return this;
    }

    /*
     * @see cn.ancono.math.numberModels.api.Simplifiable#simplify(cn.ancono.math.numberModels.api.Simplifier)
     */
    @Override
    public SVPEquation<T> simplify(Simplifier<T> sim) {
        List<T> list = new ArrayList<>(mp + 1);
        for (int i = 0; i <= mp; i++) {
            list.add(get(i));
        }
        list = sim.simplify(list);
        return valueOf(list, getMc());
    }

    /**
     * Determine whether the two equations are equal, this method only
     * compare the corresponding coefficient.
     * <p>Therefore, for example,
     * {@literal 2x=0} and {@literal x=0} are considered to be not the identity.
     * This assures that if two equations are equal, then the functions returned
     * <p>To compare the another
     * by {@link #asFunction()} are equal.
     *
     * @param obj
     */
    @Override
    public boolean valueEquals(@NotNull IMathObject<T> obj) {
        if (!(obj instanceof SVPEquation)) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        SVPEquation<T> sv = (SVPEquation<T>) obj;
        return IPolynomial.isEqual(this, sv, getMc()::isEqual);
    }

//    @Override
//    public <N> boolean valueEquals(@NotNull MathObjectReal<N> obj, @NotNull Function<N, T> mapper) {
//        if (!(obj instanceof SVPEquation)) {
//            return false;
//        }
//        if (obj == this) {
//            return true;
//        }
//        SVPEquation<N> sv = (SVPEquation<N>) obj;
//        return IPolynomial.isEqual(this, sv, CalculatorUtils.mappedIsEqual(getMc(), mapper));
//    }

    /* (non-Javadoc)
     * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.number_models.NumberFormatter)
     */
    @NotNull
    @Override
    public String toString(@NotNull NumberFormatter<T> nf) {
        return IPolynomial.stringOf(this, getMc(), nf, "x") + " = 0";

    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof SVPEquation)) {
            return false;
        }
        SVPEquation<?> sv = (SVPEquation<?>) obj;
        return IPolynomial.isEqual(this, sv);
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#hashCode()
     */
    @Override
    public int hashCode() {
        return op.hashCode() * 31 + IPolynomial.hashCodeOf(this);
    }

    /*
     * @see cn.ancono.math.SingleVEquation#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> SVPEquation<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper);

    /**
     * A default implements for the equation.
     *
     * @param <T>
     * @author lyc
     */
    static class DSVPEquation<T> extends SVPEquation<T> {
        private final T[] coes;


        private transient int hash = 0;

        DSVPEquation(RealCalculator<T> mc, T[] coes) {
            super(mc, coes.length - 1);
            this.coes = coes;
        }

        @Override
        public T compute(T x) {
            T re = coes[mp];
            for (int i = mp - 1; i > -1; i--) {
                re = getMc().multiply(x, re);
                re = getMc().add(coes[i], re);
            }
            return re;
        }

        @Override
        public boolean isSolution(T x) {
            return getMc().isZero(compute(x));
        }

        @Override
        public int getDegree() {
            return mp;
        }


        @NotNull
        @Override
        public <N> DSVPEquation<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            @SuppressWarnings("unchecked")
            N[] newCoes = (N[]) new Object[coes.length];
            for (int i = 0; i < newCoes.length; i++) {
                newCoes[i] = mapper.apply(coes[i]);
            }
            return new DSVPEquation<>((RealCalculator<N>) newCalculator, newCoes);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof SVPEquation) {
                return Arrays.equals(coes, ((DSVPEquation<?>) obj).coes);
            }
            return false;
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                int h = mp;
                h = h * 31 + Arrays.hashCode(coes);
                hash = h;
            }
            return hash;
        }


        @Override
        public boolean valueEquals(@NotNull IMathObject<T> obj) {
            if (obj instanceof SVPEquation) {
                var sv = (DSVPEquation<T>) obj;
                if (sv.mp == this.mp) {
                    for (int i = 0; i < coes.length; i++) {
                        if (!getMc().isEqual(coes[i], sv.coes[i])) {
                            return false;
                        }
                    }
                    return true;
                }
                return false;
            }
            return false;
        }

        @Override
        public T get(int n) {
            return coes[n];
        }


    }

    static class SVPFEquation<T> extends SVPEquation<T> {
        private final AbstractSVPFunction<T> f;

        /**
         * @param mc
         */
        protected SVPFEquation(RealCalculator<T> mc, AbstractSVPFunction<T> f) {
            super(mc, f.getDegree());
            this.f = f;
        }

        /*
         * @see cn.ancono.math.SVPEquation#compute(java.lang.Object)
         */
        @Override
        public T compute(T x) {
            return f.apply(x);
        }

        /*
         * @see cn.ancono.math.SVPEquation#getCoefficient(int)
         */
        @Override
        public T get(int n) {
            return f.get(n);
        }

        /*
         * @see cn.ancono.math.SVPEquation#getMaxPower()
         */
        @Override
        public int getDegree() {
            return f.getDegree();
        }

        /*
         * @see cn.ancono.math.SingleVEquation#mapTo(java.util.function.Function, cn.ancono.math.numberModels.api.MathCalculator)
         */
        @NotNull
        @Override
        public <N> SVPEquation<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new SVPFEquation<>((RealCalculator<N>) newCalculator, f.mapTo(newCalculator, mapper));
        }


    }

    /**
     * Creates an SVPEquation from a list of coefficients. The index of the
     * coefficient is considered as the corresponding power of {@code x}.
     * For example, a list [1,2,3] represents for {@literal 3x^2+2x+1}.
     *
     * @param coes a list of coefficient
     * @param mc   a {@link RealCalculator}
     * @return an equation
     */
    public static <T> SVPEquation<T> valueOf(List<T> coes, RealCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        T[] arr = (T[]) coes.toArray();
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == null) {
                throw new NullPointerException("null in list: index = " + i);
            }
        }
        return new DSVPEquation<>(mc, arr);
    }

    /**
     * Creates an equation with it coefficients. The coefficient of x^n is {@code coes[n]}.
     *
     * @param mc   a {@link RealCalculator}
     * @param coes an array of coefficients, if an element is {@code null},
     *             then it will be considered as zero.
     * @return a SVPEquation
     */
    @SafeVarargs
    public static <T> SVPEquation<T> valueOf(RealCalculator<T> mc, T... coes) {
        for (int i = 0; i < coes.length; i++) {
            if (coes[i] == null) {
                coes[i] = mc.getZero();
            }
        }
        int max = coes.length - 1;
        while (mc.isZero(coes[max])) {
            max--;
        }
        coes = Arrays.copyOf(coes, max + 1);
        return new DSVPEquation<>(mc, coes);
    }

    /**
     * Returns an equation from a multinomial.
     *
     * @param m  a {@link IPolynomial}
     * @param mc a {@link RealCalculator}
     * @return a {@link SVPEquation}
     */
    public static <T> SVPEquation<T> fromPolynomial(IPolynomial<T> m, RealCalculator<T> mc) {
        if (m instanceof SVPEquation) {
            return (SVPEquation<T>) m;
        }
        final int size = m.getDegree() + 1;
        @SuppressWarnings("unchecked")
        T[] list = (T[]) new Object[size];
        for (int i = 0; i < size; i++) {
            list[i] = m.get(i);
        }
        return new DSVPEquation<>(mc, list);
    }

    /**
     * Returns an equation from a multinomial which is also a {@link MathCalculatorHolder}.
     *
     * @param m a {@link IPolynomial}
     * @return a {@link SVPEquation}
     * @throws ClassCastException if {@code !(m instanceof MathCalculatorHolder)};
     */
    public static <T> SVPEquation<T> fromPolynomial(IPolynomial<T> m) {
        @SuppressWarnings("unchecked")
        MathCalculatorHolder<T> holder = (MathCalculatorHolder<T>) m;
        return fromPolynomial(m, holder.getCalculator());
    }

    /**
     * Returns an equation that is equal to {@code (x-a)^p = 0},the roots are
     * already available({@code x=a}).
     *
     * @param a  coefficient
     * @param p  power of the expression,should be larger than 0.
     * @param mc a {@link RealCalculator}
     * @return an equation
     */
    public static <T> SVPEquation<T> binomialPower(T a, int p, RealCalculator<T> mc) {
        if (p <= 0) {
            throw new IllegalArgumentException("p <= 0 ");
        }

        var list = Collections.singletonList(new Pair<>(Polynomial.ofRoot(mc, a), p));
        return new RootEquation<>(mc, new DecomposedPoly<>(list));
    }

    /**
     * A root equation is a equation with its roots given.
     *
     * @param <T>
     * @author liyicheng
     * 2017-10-06 15:59
     */
    public static final class RootEquation<T> extends SVPEquation<T> {
        private final DecomposedPoly<T> p;


        RootEquation(RealCalculator<T> mc, DecomposedPoly<T> p) {
            super(mc, p.getDegree());
            this.p = p;
        }


        @Override
        public <N> RootEquation<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new RootEquation<>((RealCalculator<N>) newCalculator, p.map((RealCalculator<N>) newCalculator, mapper::apply));
        }

        @Override
        public T get(int n) {
            return p.getExpanded().get(n);
        }


        @Override
        public T compute(T x) {
            return p.compute(x);
        }
    }


    /**
     * Creates an equation of
     * <pre>ax + b = 0</pre>
     *
     * @param a  coefficient of x.
     * @param b  coefficient.
     * @param mc a {@link RealCalculator}
     * @return a new LEquation
     */
    public static <T> LEquation<T> linear(T a, T b, RealCalculator<T> mc) {
        return new LEquation<T>(mc, a, b);
    }

    /**
     * Create an equation that
     * <pre>ax^2 + bx + c = 0</pre>
     *
     * @param a  the coefficient of x^2.
     * @param b  the coefficient of x.
     * @param c  the constant coefficient
     * @param mc a {@link RealCalculator}
     * @return an equation
     */
    public static <T> QEquation<T> quadratic(T a, T b, T c, RealCalculator<T> mc) {
        if (mc.isZero(a)) {
            throw new IllegalArgumentException("a == 0");
        }
        return new QEquation<T>(mc, a, b, c);
    }

    /**
     * Returns an equation that
     * <pre>(x-d)^2 = 0</pre>
     *
     * @param d  an coefficient
     * @param mc a {@link RealCalculator}
     * @return an equation
     */
    public static <T> QEquation<T> perfectSquare(T d, RealCalculator<T> mc) {
        T a = mc.getOne();
        T b = mc.multiplyLong(d, -2l);
        T c = mc.multiply(d, d);
        T delta = mc.getZero();
        int t = QEquation.ONE_IN_R;
        return new QEquation<>(mc, a, b, c, d, d, delta, t);
    }

    /**
     * QEquation is quadratic equation with only one unknown.This class provides
     * simple method for solving the equation,calculate x1+x2,x1x2 and so on.
     *
     * @param <T>
     * @author lyc
     */
    public static final class QEquation<T> extends SVPEquation<T> {

        private final T a, b, c;

        protected QEquation(RealCalculator<T> mc, T a, T b, T c) {
            super(mc, 2);
            this.a = Objects.requireNonNull(a);
            this.b = Objects.requireNonNull(b);
            this.c = Objects.requireNonNull(c);
        }

        private QEquation(RealCalculator<T> mc, T a, T b, T c, T x1, T x2, T delta, int d) {
            super(mc, 2);
            this.a = a;
            this.b = b;
            this.c = c;
            this.x1 = x1;
            this.x2 = x2;
            this.delta = delta;
            this.d = d;
        }

        /**
         * Temporary storage for x1 and x2.
         */
        private transient T x1, x2;

        private transient T delta;

        private transient int d = UNKNOWN;

        private static final int TWO_IN_R = 2;
        private static final int ONE_IN_R = 1;
        private static final int NONE_IN_R = 0;
        private static final int UNKNOWN = -1;

        @Override
        public T compute(T x) {
            T re = getMc().multiply(a, x);
            re = getMc().add(re, b);
            re = getMc().multiply(x, re);
            return getMc().add(re, c);
        }

        public T coeA() {
            return a;
        }

        public T coeB() {
            return a;
        }

        public T coeC() {
            return a;
        }

        @Override
        public int getDegree() {
            return 2;
        }

        /**
         * Solve this equation,all solutions will be considered as well as duplicated root,so the
         * returning list will always contain two elements.
         * <p>This method may return imaginary roots.
         *
         * @return a list of solutions.
         */
        public List<T> solve() {
            if (x1 == null) {
                T delta = getMc().squareRoot(delta());
                // x1 = (-b + sqr(delta)) / 2a
                // x2 = (-b - sqr(delta)) / 2a
                T a2 = getMc().multiplyLong(a, 2);
                x1 = getMc().divide(getMc().subtract(delta, b), a2);
                x2 = getMc().negate(getMc().divide(getMc().add(b, delta), a2));
            }
            List<T> so = new ArrayList<>(2);
            so.add(x1);
            so.add(x2);
            return so;
        }


        /**
         * Computes the delta value of this equation,which is calculated by
         * <pre> b^2 - 4ac </pre>
         *
         * @return delta
         * @see #solveR()
         */
        public T delta() {
            if (delta == null)
                delta = getMc().subtract(getMc().multiply(b, b), getMc().multiplyLong(getMc().multiply(a, c), 4l));
            return delta;
        }

        /**
         * Returns the number of roots in the real number field according to
         * the delta value.
         *
         * @return the number or roots.
         */
        public int getNumberOfRoots() {
            delta();
            int comp = getMc().compare(delta, getMc().getZero());
            if (comp < 0) {
                d = NONE_IN_R;
                return 0;
            } else if (comp == 0) {
                d = ONE_IN_R;
                return 1;
            } else {
                d = TWO_IN_R;
                return 2;
            }
        }

        /**
         * Solve this equation in real number field,and take the duplicated root as one root,
         * <p>This method will return a list of solutions,which will contain
         * no element if there is no real solution({@code ��<0}),
         * one if there is only one solution(or two solutions of the identity value)({@code ��=0})
         * or two elements if there are two solutions(({@code ��>0}).
         *
         * @return a list of solution,regardless of order.
         */
        public List<T> solveR() {
            if (d == UNKNOWN) {
                try {
                    getNumberOfRoots();
                } catch (UnsupportedOperationException uoe) {
                    return solve();
                }
            }
            if (d == NONE_IN_R) {
                return Collections.emptyList();
            } else if (d == ONE_IN_R) {
                if (x1 == null) {
                    T t = getMc().divide(b, getMc().multiplyLong(a, -2l));
                    x1 = t;
                    x2 = t;
                }
                List<T> list = new ArrayList<>(1);
                list.add(x1);
                return list;
            } else {
                return solve();
            }
        }

        /**
         * Returns the sum of roots in this equation,which is calculated by
         * <pre>-b/a</pre>
         *
         * @return x1+x2
         */
        public T rootsSum() {
            return getMc().negate(getMc().divide(b, a));
        }

        /**
         * Returns the multiply of roots in this equation,which is calculated by
         * <pre>c/a</pre>
         *
         * @return x1*x2
         */
        public T rootsMul() {
            return getMc().divide(c, a);
        }

        /**
         * Returns (x1-x2)^2,which is equal to delta/a^2.
         *
         * @return (x1 - x2)^2
         */
        public T rootsSubtractSq() {
            T d = delta();
            d = getMc().divide(delta, getMc().multiply(a, a));
            return d;
        }

        /**
         * Returns |x1-x2|,which is equal to sqrt(delta)/|a|.
         *
         * @return |x1-x2|
         */
        public T rootsSubtract() {
            if (x1 != null) {
                return getMc().abs(getMc().subtract(x1, x2));
            }
            T d = delta();
            return getMc().divide(getMc().squareRoot(d), getMc().abs(a));
        }


        @Override
        public <N> QEquation<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new QEquation<>((RealCalculator<N>) newCalculator, mapper.apply(a), mapper.apply(b), mapper.apply(c)
                    , x1 == null ? null : mapper.apply(x1), x2 == null ? null : mapper.apply(x2), delta == null ? null : mapper.apply(delta), d);
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof QEquation) {
                QEquation<?> qe = (QEquation<?>) obj;
                return a.equals(qe.a) && b.equals(qe.b) && c.equals(qe.c);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int hash = a.hashCode();
            hash = 31 * hash + b.hashCode();
            hash = 31 * hash + c.hashCode();
            return hash;
        }

        @Override
        public boolean valueEquals(@NotNull IMathObject<T> obj) {
            if (obj instanceof QEquation) {
                QEquation<T> eq = (QEquation<T>) obj;
                return getMc().isEqual(a, eq.a) && getMc().isEqual(b, eq.b) && getMc().isEqual(c, eq.c);
            }
            return super.valueEquals(obj);

        }


        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append('(').append(a).append(")x^2 + (").append(b).append(")x + (").append(c).append(") = 0");
            return sb.toString();
        }


        @Override
        public T get(int n) {
            switch (n) {
                case 0:
                    return c;
                case 1:
                    return b;
                case 2:
                    return a;
                default:
                    throw new IllegalArgumentException();
            }
        }


    }

    /**
     * a*x +b = 0
     *
     * @param <T>
     * @author lyc
     */
    public static final class LEquation<T> extends SVPEquation<T> implements Solveable<T> {

        private final T a, b;
        private final T sol;

        protected LEquation(RealCalculator<T> mc, T a, T b) {
            super(mc, 1);
            if (mc.isZero(a)) {
                throw new IllegalArgumentException("a=0");
            }
            this.a = a;
            this.b = Objects.requireNonNull(b);
            sol = mc.negate(mc.divide(b, a));
        }

        private LEquation(RealCalculator<T> mc, T a, T b, T sol) {
            super(mc, 1);
            this.a = Objects.requireNonNull(a);
            this.b = Objects.requireNonNull(b);
            this.sol = Objects.requireNonNull(sol);
        }

        @Override
        public T compute(T x) {
            return getMc().add(getMc().multiply(a, x), b);
        }

        @Override
        public int getDegree() {
            return 1;
        }

        @Override
        public T get(int n) {
            switch (n) {
                case 0:
                    return b;
                case 1:
                    return a;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public T solution() {
            return sol;
        }

        /*
         * @see cn.ancono.math.property.Solveable#getBaseSolutions()
         */
        @Override
        public SingletonSet<T> getSolution() {
            return MathSets.singleton(sol, getMc());
        }

        @Override
        public <N> LEquation<N> mapTo(@NotNull EqualPredicate<N> newCalculator, @NotNull Function<T, N> mapper) {
            return new LEquation<N>((RealCalculator<N>) newCalculator, mapper.apply(a), mapper.apply(b), mapper.apply(sol));
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LEquation) {
                LEquation<?> leq = (LEquation<?>) obj;
                return a.equals(leq.a) && b.equals(leq.b);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return a.hashCode() * 31 + b.hashCode();
        }

        @Override
        public boolean valueEquals(@NotNull IMathObject<T> obj) {
            if (obj instanceof LEquation) {
                LEquation<T> leq = (LEquation<T>) obj;
                return getMc().isEqual(a, leq.a) && getMc().isEqual(b, leq.b);
            }
            return super.valueEquals(obj);
        }

    }
}
