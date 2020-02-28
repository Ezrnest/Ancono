/**
 * 2017-10-06
 */
package cn.ancono.math.function;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.math.calculus.SDerivable;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.math.set.Interval;
import cn.ancono.math.set.IntervalUnion;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This class provides some basic useful functions such as
 *
 * @author liyicheng
 * 2017-10-06 10:02
 */
public abstract class AbstractSVFunction<T> extends MathObject<T> implements SVFunction<T> {

    /**
     * @param mc
     */
    protected AbstractSVFunction(MathCalculator<T> mc) {
        super(mc);
    }

    /**
     * Returns the String representation of this function, the prefix 'f(x)='
     * should not be included.
     */
    @NotNull
    @Override
    public abstract String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf);


    /*
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
     */
    @NotNull
    @Override
    public abstract <N> AbstractSVFunction<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);

    /**
     * Describe the function:
     * <pre>ln(x)</pre>
     *
     * @param <T>
     * @author liyicheng
     * 2017-10-10 18:37
     */
    public static final class Ln<T> extends AbstractSVFunction<T> implements SDerivable<T, Power<T>> {
        /**
         * @param mc
         */
        Ln(MathCalculator<T> mc) {
            super(mc);
        }

        /*
         * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
         */
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().ln(x);
        }

        /*
         * @see cn.ancono.math.calculus.Derivable#derive()
         */
        @NotNull
        @Override
        public Power<T> derive() {
            return new Power<>(getMc(), getMc().getOne(), Fraction.NEGATIVE_ONE);
        }

        /*
         * @see cn.ancono.math.function.MathFunction#domain()
         */
        @NotNull
        @Override
        public Interval<T> domain() {
            return Interval.positive(getMc());
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> Ln<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
            return new Ln<>(newCalculator);
        }

        /*
         * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
         */
        @Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
            if (obj instanceof Log) {
                return getMc().isEqual(((Log<T>) obj).a, getMc().constantValue(MathCalculator.Companion.STR_E));
            }
            if (!(obj instanceof Ln)) {
                return false;
            }
            return true;
        }

        /*
         * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.numberModels.api.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
            return "ln(x)";
        }
    }

    /**
     * Describe the function:
     * <pre>log<sup>a</sup>x</pre>
     * where {@literal a>0 && a!=1}
     *
     * @param <T>
     * @author liyicheng
     * 2017-10-10 18:37
     */
    public static final class Log<T> extends AbstractSVFunction<T> implements SDerivable<T, Power<T>> {
        private final T a;

        /**
         * @param mc
         */
        protected Log(MathCalculator<T> mc, T a) {
            super(mc);
            this.a = a;
        }

        /*
         * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
         */
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().log(a, x);
        }

        /*
         * @see cn.ancono.math.calculus.Derivable#derive()
         */
        @NotNull
        @Override
        public Power<T> derive() {
            return new Power<>(getMc(), getMc().reciprocal(getMc().ln(a)), Fraction.NEGATIVE_ONE);
        }

        /*
         * @see cn.ancono.math.function.MathFunction#domain()
         */
        @NotNull
        @Override
        public Interval<T> domain() {
            return Interval.positive(getMc());
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
            return "log(" + nf.format(a, getMc()) + ",x)";
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> Log<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
            return new Log<>(newCalculator, mapper.apply(a));
        }

        /*
         * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
         */
        @Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
            if (obj instanceof Ln) {
                return ((Ln<T>) obj).valueEquals(this);
            }
            if (!(obj instanceof Log)) {
                return false;
            }
            Log<T> log = (Log<T>) obj;
            return getMc().isEqual(a, log.a);
        }


    }

    /**
     * Describes the power function:
     * <pre>a*x^n</pre>
     * where n is a rational number.
     *
     * @param <T>
     * @author liyicheng
     * 2017-10-10 19:04
     */
    public static final class Power<T> extends AbstractSVFunction<T> implements SDerivable<T, Power<T>> {
        /**
         * @param mc
         */
        Power(MathCalculator<T> mc, T a, Fraction n) {
            super(mc);
            this.a = a;
            this.n = n;
        }

        /**
         * @param mc
         */
        Power(MathCalculator<T> mc, Fraction n) {
            this(mc, mc.getOne(), n);
        }

        /**
         *
         */
        Power(MathCalculator<T> mc) {
            this(mc, mc.getZero(), Fraction.ONE);
        }

        private final T a;
        private final Fraction n;

        /*
         * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
         */
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            if (n.getSignum() == 0) {
                return a;
            }
            if (getMc().isZero(a)) {
                return getMc().getZero();
            }
            T t;
            if (n.getDenominator() == 1) {
                t = getMc().pow(x, n.getNumerator());
            } else if (n.getNumerator() == 1) {
                t = getMc().nroot(x, n.getDenominator());
            } else {
                t = getMc().nroot(getMc().pow(x, n.getNumerator()), n.getDenominator());
            }
            if (n.getSignum() < 0) {
                t = getMc().reciprocal(t);
            }
            return getMc().multiply(a, t);
        }

        /*
         * @see cn.ancono.math.calculus.Derivable#derive()
         */
        @NotNull
        @Override
        public Power<T> derive() {
            if (getMc().isZero(a)) {
                return this;
            }
            if (n.getSignum() == 0) {
                return new Power<>(getMc(), getMc().getZero(), Fraction.ONE);
            }
            Fraction _n = n.minus(Fraction.ONE);
            T _a = getMc().divideLong(getMc().multiplyLong(a, n.getNumerator()), n.getDenominator());
            return new Power<T>(getMc(), _a, _n);
        }

        private IntervalUnion<T> domain;

        /**
         * Gets the a:<pre>a*x^n</pre>
         *
         * @return the a
         */
        public T getA() {
            return a;
        }

        /**
         * Gets the n:<pre>a*x^n</pre>
         *
         * @return the n as a fraction
         */
        public Fraction getN() {
            return n;
        }

        /*
         * @see cn.ancono.math.function.MathFunction#domain()
         */
        @NotNull
        @Override
        public IntervalUnion<T> domain() {
            if (domain == null) {
                IntervalUnion<T> dom;
                if (n.getSignum() >= 0) {
                    if (n.getDenominator() % 2 == 1) {
                        dom = IntervalUnion.universe(getMc());
                    } else {
                        dom = IntervalUnion.valueOf(Interval.toPositiveInf(getMc().getZero(), true, getMc()));
                    }
                } else {
                    if (n.getDenominator() % 2 == 1) {
                        dom = IntervalUnion.except(getMc().getZero(), getMc());
                    } else {
                        dom = IntervalUnion.valueOf(Interval.positive(getMc()));
                    }
                }
                domain = dom;
            }
            return domain;

        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
            if (getMc().isZero(a)) {
                return "0";
            } else if (n.getSignum() == 0) {
                return nf.format(a, getMc());
            }
            StringBuilder sb = new StringBuilder();
            if (!getMc().isEqual(a, getMc().getOne())) {
                sb.append(nf.format(a, getMc()));
            }
            sb.append("x");
            if (n.getDenominator() == 1 && n.getSignum() > 0) {
                if (n.getNumerator() != 1) {
                    sb.append("^").append(n.getNumerator());
                }
            } else {
                sb.append("^(").append(n.toString()).append(")");
            }
            return sb.toString();
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> Power<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
            return new Power<N>(newCalculator, mapper.apply(a), n);
        }

        /*
         * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
         */
        @Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
            if (!(obj instanceof Power)) {
                return false;
            }
            Power<T> p = (Power<T>) obj;
            if (getMc().isZero(a)) {
                return getMc().isZero(p.a);
            }
            return getMc().isEqual(a, p.a) && n.equals(p.n);
        }

    }

    /**
     * Describes the exponential function:
     * <pre>c*a^x</pre>
     * where {@code c!=0 && a > 0 && a!=1}
     *
     * @param <T>
     * @author liyicheng
     * 2017-10-10 19:04
     */
    public static final class Exp<T> extends AbstractSVFunction<T> implements SDerivable<T, Exp<T>> {
        private final T a, c;

        /**
         * @param mc
         */
        Exp(MathCalculator<T> mc, T c, T a) {
            super(mc);
            this.a = a;
            this.c = c;
        }

        /*
         * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
         */
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().multiply(c, getMc().exp(a, x));
        }

        /*
         * @see cn.ancono.math.calculus.Derivable#derive()
         */
        @NotNull
        @Override
        public Exp<T> derive() {
            return new Exp<>(getMc(), getMc().multiply(c, getMc().ln(a)), a);
        }

        /**
         * Gets the a:<pre>c*a^x</pre>
         *
         * @return the a
         */
        public T getA() {
            return a;
        }

        /**
         * Gets the c:<pre>c*a^x</pre>
         *
         * @return the c
         */
        public T getC() {
            return c;
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
            StringBuilder sb = new StringBuilder();
            if (!getMc().isEqual(getMc().getOne(), a)) {
                sb.append(nf.format(a, getMc()));
            }
            String as = nf.format(a, getMc());
            if (as.length() == 1) {
                sb.append(as);
            } else {
                sb.append('(').append(as).append(')');
            }
            sb.append("^x");

            return sb.toString();
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> Exp<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
            return new Exp<N>(newCalculator, mapper.apply(c), mapper.apply(a));
        }

        /*
         * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
         */
        @Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
            if (obj instanceof Ex) {
                return obj.valueEquals(this);
            }
            if (!(obj instanceof Exp)) {
                return false;
            }
            Exp<T> exp = (Exp<T>) obj;
            return getMc().isEqual(a, exp.a) && getMc().isEqual(c, exp.c);
        }
    }

    /**
     * Returns the power function:
     * <pre>e^x</pre>
     * where {@code e} is the natural base of logarithm.
     *
     * @param <T>
     * @author liyicheng
     * 2017-10-10 19:04
     */
    public static final class Ex<T> extends AbstractSVFunction<T> implements SDerivable<T, Ex<T>> {
        /**
         * @param mc
         */
        Ex(MathCalculator<T> mc) {
            super(mc);
        }

        /*
         * @see cn.ancono.math.function.SVFunction#apply(java.lang.Object)
         */
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().exp(x);
        }

        /*
         * @see cn.ancono.math.calculus.Derivable#derive()
         */
        @NotNull
        @Override
        public Ex<T> derive() {
            return this;
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#toString(cn.ancono.math.numberModels.api.NumberFormatter)
         */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
            return "e^x";
        }

        /*
         * @see cn.ancono.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
         */
        @NotNull
        @Override
        public <N> Ex<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
            return new Ex<N>(newCalculator);
        }

        /*
         * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
         */
        @Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
            if (obj instanceof Exp) {
                Exp<T> exp = (Exp<T>) obj;
                return getMc().isEqual(getMc().getOne(), exp.c) && getMc().isEqual(Objects.requireNonNull(getMc().constantValue(MathCalculator.STR_E)), exp.a);
            }
            return obj instanceof Ex;
        }
    }

    private static final Map<MathCalculator<?>, Ex<?>> expmap = new ConcurrentHashMap<>();
    private static final Map<MathCalculator<?>, Ln<?>> lnmap = new ConcurrentHashMap<>();

    /**
     * Returns the function : {@literal e^x}
     *
     * @param mc a {@link MathCalculator}
     * @return {@literal e^x}
     */
    public static <T> Ex<T> naturalExp(MathCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        Ex<T> ex = (Ex<T>) expmap.get(mc);
        if (ex == null) {
            ex = new Ex<>(mc);
            expmap.put(mc, ex);
        }
        return ex;
    }

    /**
     * Returns the function : {@literal ln(x)}
     *
     * @param mc a {@link MathCalculator}
     * @return {@literal ln(x)}
     */
    public static <T> Ln<T> naturalLog(MathCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        Ln<T> ex = (Ln<T>) lnmap.get(mc);
        if (ex == null) {
            ex = new Ln<>(mc);
            lnmap.put(mc, ex);
        }
        return ex;
    }

    /**
     * Returns the exponential function:
     * <pre>c*a^x</pre>
     * where {@code c!=0 && a > 0 && a!=1}
     *
     * @param mc a {@link MathCalculator}
     * @return <pre>c*a^x</pre>
     */
    public static <T> Exp<T> exp(T a, T c, MathCalculator<T> mc) {
        if (mc.isZero(c)) {
            throw new IllegalArgumentException("c == 0");
        }
        if (mc.compare(a, mc.getZero()) <= 0 || mc.isEqual(mc.getOne(), a)) {
            throw new IllegalArgumentException("a <= 0 || a==1");
        }
        return new Exp<T>(mc, c, a);
    }

    /**
     * Returns the exponential function:
     * <pre>a^x</pre>
     * where {@code a > 0 && a!=1}
     *
     * @param mc a {@link MathCalculator}
     * @return <pre>a^x</pre>
     */
    public static <T> Exp<T> exp(T a, MathCalculator<T> mc) {
        if (mc.compare(a, mc.getZero()) <= 0 || mc.isEqual(mc.getOne(), a)) {
            throw new IllegalArgumentException("a <= 0 || a==1");
        }
        return new Exp<T>(mc, mc.getOne(), a);
    }

    /**
     * Returns the function:
     * <pre>log<sup>a</sup>x</pre>
     * where {@literal a>0 && a!=1}
     *
     * @param a  the base
     * @param mc a {@link MathCalculator}
     * @return <pre>log<sup>a</sup>x</pre>
     */
    public static <T> Log<T> log(T a, MathCalculator<T> mc) {
        if (mc.compare(a, mc.getZero()) <= 0 || mc.isEqual(mc.getOne(), a)) {
            throw new IllegalArgumentException("a <= 0 || a==1");
        }
        return new Log<T>(mc, a);
    }

    /**
     * Returns the power function:
     * <pre>a*x^n</pre>
     * where n is a rational number.
     *
     * @param a
     * @param n
     * @param mc a {@link MathCalculator}
     * @return <pre>a*x^n</pre>
     */
    public static <T> Power<T> pow(T a, Fraction n, MathCalculator<T> mc) {
        if (mc.isZero(a)) {
            return new Power<>(mc);
        }
        return new Power<>(mc, a, n);
    }

    /**
     * Returns the power function:
     * <pre>x^n</pre>
     * where n is a rational number.
     *
     * @param n
     * @param mc a {@link MathCalculator}
     * @return <pre>x^n</pre>
     */
    public static <T> Power<T> pow(Fraction n, MathCalculator<T> mc) {
        return new Power<>(mc, n);
    }

}
