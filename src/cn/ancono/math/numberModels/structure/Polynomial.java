/**
 * 2017-11-21
 */
package cn.ancono.math.numberModels.structure;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathCalculatorHolder;
import cn.ancono.math.MathObject;
import cn.ancono.math.algebra.IPolynomial;
import cn.ancono.math.algebra.abstractAlgebra.calculator.UFDCalculator;
import cn.ancono.math.algebra.linearAlgebra.Matrix;
import cn.ancono.math.algebra.linearAlgebra.MatrixSup;
import cn.ancono.math.algebra.linearAlgebra.Vector;
import cn.ancono.math.exceptions.ExceptionUtil;
import cn.ancono.math.exceptions.UnsupportedCalculationException;
import cn.ancono.math.geometry.analytic.planeAG.Point;
import cn.ancono.math.numberModels.*;
import cn.ancono.math.numberModels.api.AlgebraModel;
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter;
import cn.ancono.math.numberModels.api.NumberFormatter;
import cn.ancono.math.numberModels.api.Simplifier;
import cn.ancono.math.numberTheory.EuclidRingNumberModel;
import cn.ancono.math.numberTheory.NTCalculator;
import cn.ancono.math.numberTheory.combination.CombUtils;
import cn.ancono.utilities.CollectionSup;
import cn.ancono.utilities.ModelPatterns;
import cn.ancono.utilities.structure.Pair;
import cn.ancono.utilities.structure.WithInt;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;


/**
 * An implement for interface IPolynomial.
 *
 * @author liyicheng
 * @see IPolynomial
 * 2017-11-21 17:10
 */
public final class Polynomial<T> extends MathObject<T> implements
        IPolynomial<T>,
        Comparable<Polynomial<T>>,
        AlgebraModel<T, Polynomial<T>>,
        EuclidRingNumberModel<Polynomial<T>> {
    /**
     * A map.
     */
    private final NavigableMap<Integer, T> map;
    /**
     * The max power of the variable.
     */
    private final int degree;


    Polynomial(MathCalculator<T> calculator, NavigableMap<Integer, T> map, int degree) {
        super(calculator);
        this.map = Objects.requireNonNull(map);
        this.degree = degree;
//        if(map.isEmpty()){
//            if(degree!=0){
//                throw new ArithmeticException();
//            }
//        }
    }


    /*
     * @see cn.ancono.math.algebra.Polynomial#getMaxPower()
     */
    @Override
    public int getDegree() {
        return degree;
    }

    private T getCoefficient0(Integer n) {
        T a = map.get(n);
        if (a == null) {
            return getMc().getZero();
        }
        return a;
    }

    /*
     * @see cn.ancono.math.algebra.Polynomial#getCoefficient(int)
     */
    @Override
    public T getCoefficient(int n) {
//        if (n < 0 || n > degree) {
//            throw new IndexOutOfBoundsException("For n=" + n);
//        }
        return getCoefficient0(n);
    }

    public Vector<T> coefficientVector() {
        return IPolynomial.coefficientVector(this, getMathCalculator());
    }

    public boolean isZero() {
        return isConstant() && getMc().isZero(getCoefficient(0));
    }

    public boolean isOne() {
        var mc = getMc();
        return isConstant() && mc.isEqual(mc.getOne(), getCoefficient(0));
    }

    @Override
    @NotNull
    public Polynomial<T> add(Polynomial<T> y) {
        var para1 = this;
        var mc = getMc();
        TreeMap<Integer, T> map = new TreeMap<>();
        int mp1 = para1.getDegree();
        int mp2 = y.getDegree();
        int mp0 = mp1;
        int mp = -1;
        if (mp1 > mp2) {
            addToMap(para1, map, mp1, mp2, mc);
            mp0 = mp2;
            mp = mp1;
        } else if (mp2 > mp1) {
            addToMap(y, map, mp2, mp1, mc);
            mp = mp2;
        }
        for (int i = mp0; i > -1; i--) {
            Integer n = i;
            T a = para1.getCoefficient0(n),
                    b = y.getCoefficient0(n);
            T sum = mc.add(a, b);
            if (!mc.isZero(sum)) {
                if (mp == -1) {
                    mp = i;
                }
                map.put(n, sum);
            }
        }
        if (mp == -1) {
            return zero(mc);
        }
        return new Polynomial<>(mc, map, mp);
    }

    private static <T> void addToMap(Polynomial<T> para1, TreeMap<Integer, T> map, int mp1, int mp2, MathCalculator<T> mc) {
        for (int i = mp1; i > mp2; i--) {
            Integer n = i;
            T a = para1.getCoefficient0(n);
            if (!mc.isZero(a))
                map.put(n, a);
        }
    }

    @Override
    public Polynomial<T> negate() {
        var para = this;
        var mc = getMc();
        NavigableMap<Integer, T> nmap = para.getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            en.setValue(mc.negate(en.getValue()));
        }
        return new Polynomial<>(mc, nmap, para.degree);
    }

    @Override
    @NotNull
    public Polynomial<T> subtract(Polynomial<T> y) {
        var para1 = this;
        var mc = getMc();
        TreeMap<Integer, T> map = new TreeMap<>();
        int mp1 = para1.getDegree();
        int mp2 = y.getDegree();
        int mp0 = mp1;
        int mp = -1;
        if (mp1 > mp2) {
            for (int i = mp1; i > mp2; i--) {
                Integer n = i;
                T a = para1.getCoefficient0(n);
                if (!mc.isZero(a)) {
                    map.put(n, a);
                }
            }
            mp0 = mp2;
            mp = mp1;
        } else if (mp2 > mp1) {
            for (int i = mp2; i > mp1; i--) {
                Integer n = i;
                T a = y.getCoefficient0(n);
                if (!mc.isZero(a)) {
                    a = mc.negate(a);
                    map.put(n, a);
                }
            }
            mp = mp2;
        }
        for (int i = mp0; i > -1; i--) {
            Integer n = i;
            T a = para1.getCoefficient0(n),
                    b = y.getCoefficient0(n);
            T sum = mc.subtract(a, b);
            if (!mc.isZero(sum)) {
                if (mp == -1) {
                    mp = i;
                }
                map.put(n, sum);
            }
        }
        if (mp == -1) {
            return zero(mc);
        }
        return new Polynomial<>(mc, map, mp);
    }

    @Override
    @NotNull
    public Polynomial<T> multiply(Polynomial<T> y) {
        var para1 = this;
        var mc = getMc();
        NavigableMap<Integer, T> map = multiplyToMap(para1.map, y.map, mc);
        if (map.isEmpty()) {
            return zero(mc);
        }
        return fromMap(map, mc);
    }

    private static <T> NavigableMap<Integer, T> multiplyToMap(NavigableMap<Integer, T> p1, NavigableMap<Integer, T> p2, MathCalculator<T> mc) {
        TreeMap<Integer, T> map = new TreeMap<>();
        for (Entry<Integer, T> en1 : p1.entrySet()) {
            int n1 = en1.getKey();
            for (Entry<Integer, T> en2 : p2.entrySet()) {
                int t = n1 + en2.getKey();
                T coe = mc.multiply(en1.getValue(), en2.getValue());
                map.compute(t, (p, c) -> c == null ? coe : mc.add(c, coe));
            }
        }
        return map;
    }

    /**
     * Returns the value of this polynomial
     *
     * @param x value of x to substitute
     */
    public T compute(T x) {
        T re = getCoefficient(degree);
        var mc = getMc();
        for (int i = degree - 1; i > -1; i--) {
            //noinspection SuspiciousNameCombination
            re = mc.multiply(re, x); // left-multiply x.
            re = mc.add(getCoefficient(i), re);
        }
        return re;
    }

    /**
     * Returns a polynomial of substituting the variable with the given value.
     */
    public Polynomial<T> substitute(Polynomial<T> sub) {
        var mc = getMc();
        var re = constant(mc, getCoefficient(degree));
        for (int i = degree - 1; i > -1; i--) {
            re = sub.multiply(re);
            re = constant(mc, getCoefficient(i)).add(re);
        }
        return re;
    }


    /**
     * Performs a homomorphism map that maps the character of the polynomial to <code>x</code> and
     * maps the coefficient using the <code>injection</code>.
     */
    public <V extends AlgebraModel<T, V>> V homoMap(V x, Function<T, V> injection) {
        var re = injection.apply(getCoefficient(degree));
        for (int i = degree - 1; i > -1; i--) {
            re = x.multiply(re);
            re = injection.apply(getCoefficient(i)).add(re);
        }
        return re;
    }


    @Override
    public boolean isLinearRelevant(@NotNull Polynomial<T> t) {
        return monic().valueEquals(t.monic());
    }

    /**
     * Divides this polynomial by a number to get a new polynomial whose leading coefficient is one.
     */
    public Polynomial<T> monic() {
        if (isZero() || getMc().isEqual(getMc().getOne(), getCoefficient(degree))) {
            return this;
        }
        T k = getCoefficient(degree);
        return divide(k);
    }

    /**
     * Returns <code>k*this</code>
     */
    @NotNull
    public Polynomial<T> multiply(T k) {
        var mc = getMc();
        if (mc.isZero(k)) {
            return zero(mc);
        }
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            var t = mc.multiply(en.getValue(), k);
            en.setValue(t);
        }
        return fromMap(nmap, mc);
    }

    @NotNull
    public Polynomial<T> multiplyLong(long k) {
        var mc = getMc();
        if (k == 0) {
            return zero(mc);
        }
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            var t = mc.multiplyLong(en.getValue(), k);
            en.setValue(t);
        }
        return fromMap(nmap, mc);
    }


    /**
     * Returns <code>1/k*this</code>
     */
    public Polynomial<T> divide(T k) {
        var mc = getMc();
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            en.setValue(mc.divide(en.getValue(), k));
        }
        return new Polynomial<>(mc, nmap, degree);
    }

    @NotNull
    public Polynomial<T> divideLong(long k) {
        var mc = getMc();
        if (k == 0) {
            ExceptionUtil.dividedByZero();
        }
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            var t = mc.divideLong(en.getValue(), k);
            en.setValue(t);
        }
        return fromMap(nmap, mc);
    }

    private List<WithInt<T>> followingTermsToListWithInt(Polynomial<T> g) {
        List<WithInt<T>> toSubtract = new ArrayList<>(g.map.size() - 1);
        for (Entry<Integer, T> en : g.map.entrySet()) {
            int n = en.getKey();
            if (n != g.degree) {
                toSubtract.add(new WithInt<>(n, en.getValue()));
            }
        }
        return toSubtract;
    }

    /**
     * Returns the quotient <code>q</code> and remainder <code>r</code> such that
     * <pre> this = q * g + r, where deg(r) < deg(g)</pre>
     *
     * @param g a non-zero polynomial
     */
    public kotlin.Pair<Polynomial<T>, Polynomial<T>> divideAndRemainder(Polynomial<T> g) {
        var p1 = this;
        var mc = getMc();
        if (g.isZero()) {
            ExceptionUtil.dividedByZero();
        }
        int mp1 = p1.degree, mp2 = g.degree;
        if (mp2 > mp1) {
            return new kotlin.Pair<>(zero(mc), p1);
        }
        if (isZero()) {
            var zero = zero(mc);
            return new kotlin.Pair<>(zero, zero);
        }
        NavigableMap<Integer, T> remains = p1.getCoefficientMap();
        TreeMap<Integer, T> quotient = new TreeMap<>();
        T first = g.getCoefficient(mp2);
        var toSubtract = followingTermsToListWithInt(g);

        while (!remains.isEmpty()) {
            Entry<Integer, T> en = remains.pollLastEntry();
            int p = en.getKey() - mp2;
            if (p < 0) {
                remains.put(en.getKey(), en.getValue());
                break;
            }
            T k = mc.divide(en.getValue(), first);
            quotient.put(p, k);
            for (WithInt<T> w : toSubtract) {
                int n = p + w.getInt();
                T a = mc.multiply(k, w.getObj());
                remains.compute(n, (x, t) -> {
                    if (t == null) {
                        return mc.negate(a);
                    }
                    t = mc.subtract(t, a);
                    if (mc.isZero(t)) {
                        return null;
                    }
                    return t;
                });
            }
        }
        Polynomial<T> qm = new Polynomial<>(mc, quotient, mp1 - mp2);
        Polynomial<T> rm = remains.isEmpty() ? zero(mc) : new Polynomial<>(mc, remains, remains.lastKey());
        return new kotlin.Pair<>(qm, rm);
    }

    /**
     * Returns the 'integer' quotient part of the division of <code>this</code> and <code>g</code>.
     * Assuming
     * <pre> this = q * g + r, where deg(r) < deg(g)</pre>
     * then this method returns <code>q</code>.
     *
     * @param g a non-zero polynomial
     */
    @Override
    @NotNull
    public Polynomial<T> divideToInteger(@NotNull Polynomial<T> g) {
        return divideAndRemainder(g).getFirst();
    }

    /**
     * Returns the remainder of the division of <code>this</code> and <code>g</code>.
     * Assuming
     * <pre> this = q * g + r, where deg(r) < deg(g)</pre>
     * then this method returns <code>r</code>.
     *
     * @param g a non-zero polynomial
     */
    @Override
    @NotNull
    public Polynomial<T> remainder(@NotNull Polynomial<T> g) {
        //special implementation for better performance
        var f = this;
        var mc = getMc();
        if (g.isZero()) {
            ExceptionUtil.dividedByZero();
        }
        int mp2 = g.degree;
        if (mp2 > f.degree || f.isZero()) {
            return f;
        }
        if (g.isConstant()) {
            return zero(mc);
        }
        NavigableMap<Integer, T> remains = f.getCoefficientMap();
        T first = g.getCoefficient(mp2);
        var toSubtract = followingTermsToListWithInt(g);

        while (!remains.isEmpty()) {
            Entry<Integer, T> en = remains.pollLastEntry();
            int p = en.getKey() - mp2;
            if (p < 0) {
                remains.put(en.getKey(), en.getValue());
                break;
            }
            T k = mc.divide(en.getValue(), first);
            for (WithInt<T> w : toSubtract) {
                int n = p + w.getInt();
                T a = mc.multiply(k, w.getObj());
                remains.compute(n, (x, t) -> {
                    if (t == null) {
                        return mc.negate(a);
                    }
                    t = mc.subtract(t, a);
                    if (mc.isZero(t)) {
                        return null;
                    }
                    return t;
                });
            }
        }
        return remains.isEmpty() ? zero(mc) : new Polynomial<>(mc, remains, remains.lastKey());
//        return divideAndRemainder(g).getSecond();
    }

    /*
     * @see cn.ancono.math.MathCalculator#pow(java.lang.Object, long)
     */
    @NotNull
    public Polynomial<T> pow(int exp) {
        if (exp == 1) {
            return this;
        }
        var mc = getMc();
        if (this.degree == 0) {
            //single
            return constant(mc, mc.pow(getCoefficient(0), exp));
        }
        long mp = (long) exp * this.degree;
        if (mp > Integer.MAX_VALUE || mp < 0) {
            throw new ArithmeticException("Too big for exp=" + exp);
        }

        NavigableMap<Integer, T> map = ModelPatterns.binaryProduce(exp, one(mc).map, this.map, (x, y) -> multiplyToMap(x, y, mc));
        return fromMap(map, mc);
    }

    /**
     * 'Shifts' this polynomial by multiplying <code>x<sup>d</sup></code>. If {@code d} is negative, the terms of
     * negative power will be dropped.
     * <br>
     * For example, <code>(x<sup>2</sup> + x + 1).shift(1) = x<sup>3</sup> + x<sup>2</sup> + x</code> and
     * <code>(x<sup>2</sup> + x + 1).shift(-1) = x + 1</code>
     *
     * @param d the degree of shifting
     */
    public Polynomial<T> shift(int d) {
        if (d + degree <= 0) {
            return Polynomial.zero(getMc());
        }
        NavigableMap<Integer, T> nMap = new TreeMap<>();
        for (var en : map.tailMap(-d, true).entrySet()) {
            var pow = en.getKey() + d;
            var coe = en.getValue();
            nMap.put(pow, coe);
        }
        return new Polynomial<>(getMc(), nMap, degree + d);
    }

    /**
     * Returns the difference of this polynomial. Assuming this is <code>f(x)</code>, then
     * the result is <code>f(x)-f(x-1)</code>.
     *
     * @return <code>Δf(x)</code>
     */
    public Polynomial<T> difference() {
        var mc = getMc();
        if (degree == 0) {
            return zero(mc);
        }
        //x^n - (x-1)^n = sigma((-1)^(n-i+1)*C(n,i)*x^i,i from 0 to n-1)
        NavigableMap<Integer, T> nmap = new TreeMap<>();
        for (var en : map.entrySet()) {
            int n = en.getKey();
            T coe = en.getValue();
            var binomials = CombUtils.binomialsOf(n).iterator();
            for (int i = 0; i < n; i++) {
                var t1 = mc.multiplyLong(coe, binomials.next());
                T t2;
                if ((n - i) % 2 == 0) {
                    t2 = mc.negate(t1);
                } else {
                    t2 = t1;
                }
                nmap.compute(i, (k, v) -> {
                    if (v == null) {
                        return t2;
                    } else {
                        return mc.add(t2, v);
                    }
                });
            }
        }
        return fromMap(nmap, mc);
    }

    /**
     * Returns the derivative of this polynomial(formally), that is
     * <text>sigma(n*a<sub>n</sub>x<sup>n-1</sup>, n from 1 to (<i>degree of this</i>-1))</text>
     *
     * @return the derivative of this polynomial(formally)
     */
    public Polynomial<T> derivative() {
        var mc = getMc();
        if (degree == 0) {
            return zero(mc);
        }
        NavigableMap<Integer, T> nmap = new TreeMap<>();
        for (var en : map.entrySet()) {
            int n = en.getKey();
            if (n == 0) {
                continue;
            }
            T coe = en.getValue();
            nmap.put(n - 1, mc.multiplyLong(coe, n));
        }
        return fromMap(nmap, mc);
    }

    /**
     * Returns one of the integration of this polynomial whose constant part is zero.
     */
    public Polynomial<T> integration() {
        if (isZero()) {
            return this;
        }
        var mc = getMc();
        NavigableMap<Integer, T> nmap = new TreeMap<>();
        for (var en : map.entrySet()) {
            int n = en.getKey();
            T coe = en.getValue();
            nmap.put(n + 1, mc.divideLong(coe, n + 1));
        }
        return new Polynomial<>(mc, nmap, degree + 1);
    }


    /**
     * Assuming this polynomial is <code>f(x)</code>, this method returns the
     * result of <code>sigma(f(i),i from 1 to n)</code> as a polynomial.
     */
    @SuppressWarnings("unchecked")
    public Polynomial<T> sumOfN() {
        //use lagrange
        var points = new Point[degree + 2];
        var mc = getMc();
        points[0] = Point.pointO(mc);
        for (int i = 1; i < points.length; i++) {
            points[i] = Point.valueOf(CalculatorUtils.valueOfLong(i, mc), CalculatorUtils.sigma(1, i + 1, mc, j ->
                    compute(CalculatorUtils.valueOfLong(j, mc))), mc);
        }
//        print(points);
        return lagrangeInterpolation(points);
    }


    /**
     * Returns the gcd of all coefficients.
     * <br>
     * It is required that the MathCalculator is an instance of UFDCalculator.
     */
    public T cont() {
        var mc = getMc();
        @SuppressWarnings("unchecked")
        var gc = (UFDCalculator<T>) mc;
        T re = gc.getZero();
        for (var coe : this.map.values()) {
            if (mc.isZero(coe)) {
                continue;
            }
            re = gc.gcd(re, coe);
        }
        return re;
    }

    /**
     * Returns the primitive polynomial corresponding to this polynomial.
     * <br>
     * It is required that the <code>MathCalculator</code> is an instance of <code>UFDCalculator</code>.
     */
    public Polynomial<T> toPrimitive() {
        return divide(cont());
    }

    /**
     * Returns the greatest common divisor of this and <code>g</code>. The coefficient of the top term in the
     * returned polynomial is one.
     */
    @NotNull
    @Override
    public Polynomial<T> gcd(@NotNull Polynomial<T> g) {
        Polynomial<T> a = this, b = g;
        if (a.degree < b.degree) {
            Polynomial<T> t = a;
            a = b;
            b = t;
        }
        while (b.degree > 0) {
            Polynomial<T> t = a.remainder(b);
            a = b;
            b = t;
        }
        if (b.isZero()) {
            return a.monic();
        } else {
            return one(getMathCalculator());
        }
    }

    @NotNull
    @Override
    public Triple<Polynomial<T>, Polynomial<T>, Polynomial<T>> gcdUV(@NotNull Polynomial<T> y) {
        return CalculatorUtils.gcdUV(this, y, zero(getMc()), one(getMc()));
    }

    @NotNull
    @Override
    public Polynomial<T> lcm(@NotNull Polynomial<T> y) {
        var x = this;
        if (x.isZero()) {
            return x;
        }
        if (y.isZero()) {
            return y;
        }
        var gcd = x.gcd(y);
        return (x.multiply(y)).divideToInteger(gcd);
//        return (Polynomial<T>) EuclidRingNumberModel.DefaultImpls.lcm(this,y);
    }

    @Override
    public boolean isCoprime(@NotNull Polynomial<T> y) {
        return this.gcd(y).isOne();
    }

    @Override
    public int deg(@NotNull Polynomial<T> y) {
        var b = this;
        if (y.isZero()) {
            throw new ArithmeticException("a==0");
        }
        var k = 0;
        var dar = b.divideAndRemainder(y);
        while (dar.getFirst().isZero()) {
            // b%a==0
            k++;
            if (b == dar.getFirst()) {
                throw new ArithmeticException("a==1");
            }
            b = dar.getFirst();
            // b = b/a;
            dar = b.divideAndRemainder(y);

        }
        return k;
//        return EuclidRingNumberModel.DefaultImpls.deg(this , y);
    }

    /**
     * Determines whether this polynomial can have duplicated roots.
     */
    public boolean hasDuplicatedRoot() {
        return !isCoprime(derivative());
    }

    /**
     * Returns a polynomial containing only terms of degree <= n in this polynomial.
     *
     * @param n a non-negative integer
     * @return a polynomial whose degree is at most {@code n}
     */
    public Polynomial<T> dropHigherTerms(int n) {
        if (degree <= n) {
            return this;
        }
        var nMap = getCoefficientMap().headMap(n, true);
        return Polynomial.fromMap(nMap, getMc());
    }

    /**
     * Returns the polynomial representing the result of <code>sigma(x^p,1,n) = 1^p+2^p+3^p+...+n^p</code> where
     * <code>p</code> is a non-negative integer.
     *
     * @param p  a non-negative integer
     * @param mc a MathCalculator
     */
    public static <T> Polynomial<T> sumOfXP(int p, MathCalculator<T> mc) {
        if (p < 0) {
            throw new IllegalArgumentException("p<0");
        }
        switch (p) {
            case 0:
                return oneX(mc);
            case 1:
                return sumOfX1(mc);
            case 2:
                return sumOfX2(mc);
            case 3:
                return sumOfX3(mc);
        }
        return powerX(p, mc).sumOfN();
    }

    private static <T> Polynomial<T> sumOfX1(MathCalculator<T> mc) {
        //1+2+...n = (n+1)n / 2 = 1/2 * n^2 + 1/2 * n
        var half = mc.divideLong(mc.getOne(), 2L);
        return Polynomial.valueOf(mc, mc.getZero(), half, half);
    }

    private static <T> Polynomial<T> sumOfX2(MathCalculator<T> mc) {
        //1^2+2^2+...n^2 = n(n+1)(2n+1)/6 = 1/3*n^3+1/2*n^2+1/6*n
        var c1 = CalculatorUtils.valueOfFraction(Fraction.of(1, 3), mc);
        var c2 = CalculatorUtils.valueOfFraction(Fraction.of(1, 2), mc);
        var c3 = CalculatorUtils.valueOfFraction(Fraction.of(1, 6), mc);
        return Polynomial.valueOf(mc, mc.getZero(), c3, c2, c1);
    }

    private static <T> Polynomial<T> sumOfX3(MathCalculator<T> mc) {
        //1^2+2^2+...n^2 = (n(n+1)/2)^2 = 1/4*n^4+1/2*n^3+1/4*n^2
        var c1 = CalculatorUtils.valueOfFraction(Fraction.of(1, 4), mc);
        var c2 = CalculatorUtils.valueOfFraction(Fraction.of(1, 2), mc);
        var o = mc.getZero();
        return Polynomial.valueOf(mc, o, o, c1, c2, c1);
    }

    /**
     * Returns the sylvester matrix of this and g. It is required that <code>this</code> and
     * <code>g</code> must not be zero at the same time.
     * <pre>R(this,g)</pre>
     *
     * @param g another polynomial
     * @return a square matrix whose size is <code>this.degree + g.degree</code>.
     */
    public Matrix<T> sylvesterMatrix(Polynomial<T> g) {
        return MatrixSup.sylvesterDet(this, g);
    }

    /**
     * Returns the determinant of the sylvester matrix of this and g. It is required that <code>this</code> and
     * <code>g</code> must not be zero at the same time.
     * <pre>|R(this,g)|</pre>
     *
     * @param g another polynomial
     * @return the determinant of the sylvester matrix
     */
    public T sylvesterDet(Polynomial<T> g) {
        return sylvesterMatrix(g).calDet();
    }

    /**
     * Returns the determinant of this polynomial, which is defined as
     * <pre>(-1)<sup>n(n-1)/2</sup>a<sub>n</sub><sup>-1</sup>R(this,this')</pre>
     * where <code>a<sub>n</sub></code> is the coefficient of the top term, and
     * <code>this'</code> is the derivative of this polynomial.
     * <p>
     * For example, assuming this is <code>ax^2+bx+c</code>, the determinant will
     * be <code>b^2-4ac</code>
     *
     * @return the determinant of this.
     */
    public T determinant() {
        var det = sylvesterMatrix(derivative());
        var re = det.calDet();
        re = getMc().divide(re, first());
        int n = getDegree();
        if (n % 4 == 2 || n % 4 == 3) {
            return getMc().negate(re);
        } else {
            return re;
        }
    }


    /*
     * @see cn.ancono.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.ancono.math.MathCalculator)
     */
    @Override
    public <N> Polynomial<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
        TreeMap<Integer, N> nmap = new TreeMap<>();
        for (Entry<Integer, T> en : map.entrySet()) {
            nmap.put(en.getKey(), mapper.apply(en.getValue()));
        }
        return new Polynomial<>(newCalculator, nmap, degree);
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject)
     */
    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (!(obj instanceof Polynomial)) {
            return false;
        }
        return IPolynomial.isEqual(this, (Polynomial<T>) obj, getMc()::isEqual);
    }


    /*
     * @see cn.ancono.math.FlexibleMathObject#valueEquals(cn.ancono.math.FlexibleMathObject, java.util.function.Function)
     */
    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (!(obj instanceof Polynomial)) {
            return false;
        }
        return IPolynomial.isEqual(this, (Polynomial<N>) obj, (x, y) -> getMc().isEqual(x, mapper.apply(y)));
    }

    /*
     * @see cn.ancono.math.FlexibleMathObject#toString(cn.ancono.math.numberModels.api.NumberFormatter)
     */
    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        return IPolynomial.stringOf(this, getMc(), nf);
    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Polynomial<T> o) {
        int mp = o.degree;
        if (mp > degree) {
            return -1;
        } else if (mp < degree) {
            return 1;
        }
        for (int i = mp; i > -1; i--) {
            Integer n = i;
            T a = getCoefficient0(n);
            T b = o.getCoefficient0(n);
            int t = getMc().compare(a, b);
            if (t != 0) {
                return t;
            }
        }
        return 0;
    }

    private int hashCode;

    /*
     * @see cn.ancono.math.FlexibleMathObject#hashCode()
     */
    @Override
    public int hashCode() {
        if (hashCode == 0) {
            hashCode = map.hashCode() + 31 * degree;
        }
        return hashCode;
    }

    public NavigableMap<Integer, T> getCoefficientMap() {
        if (map instanceof TreeMap) {
            @SuppressWarnings("unchecked")
            NavigableMap<Integer, T> nmap = (NavigableMap<Integer, T>) ((TreeMap<Integer, T>) map).clone();
            return nmap;
        } else {
            return new TreeMap<>(map);
        }
    }

    public List<T> getNonZeroCoefficients() {
        var result = new ArrayList<T>(map.size());
        var mc = getMc();
        for (var en : map.entrySet()) {
            var coe = en.getValue();
            if (!mc.isZero(coe)) {
                result.add(coe);
            }
        }
        return result;
    }

    private static final Map<MathCalculator<?>, Polynomial<?>> zeros = new HashMap<>();

    /**
     * Drops zero terms in the map.
     */
    private static <T> Polynomial<T> fromMap(NavigableMap<Integer, T> map, MathCalculator<T> mc) {
        map.entrySet().removeIf(en -> mc.isZero(en.getValue()));
        var entry = map.lastEntry();
        if (entry == null) {
            return zero(mc);
        }

        return new Polynomial<>(mc, map.headMap(entry.getKey(), true), entry.getKey());
    }

    /**
     * Adds all the polynomials.
     *
     * @param ps a non-empty array
     */
    @SafeVarargs
    public static <T> Polynomial<T> addAll(Polynomial<T>... ps) {
        return NumberModelUtils.sigma(ps, 0, ps.length);
    }

    /**
     * Multiplies all the polynomials.
     *
     * @param ps a non-empty array
     */
    @SafeVarargs
    public static <T> Polynomial<T> multiplyAll(Polynomial<T>... ps) {
        return NumberModelUtils.multiplyAll(ps, 0, ps.length);
    }

    /**
     * Returns zero.
     */
    public static <T> Polynomial<T> zero(MathCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        Polynomial<T> zero = (Polynomial<T>) zeros.get(mc);
        if (zero == null) {
            zero = new Polynomial<>(mc, Collections.emptyNavigableMap(), 0);
            synchronized (zeros) {
                zeros.put(mc, zero);
            }
        }
        return zero;
    }

    /**
     * Returns the polynomial {@literal 1}。
     */
    public static <T> Polynomial<T> one(MathCalculator<T> mc) {
        return constant(mc, mc.getOne());
    }

    /**
     * Returns the polynomial {@literal x}.
     */
    public static <T> Polynomial<T> oneX(MathCalculator<T> mc) {
        TreeMap<Integer, T> map = new TreeMap<>();
        map.put(1, mc.getOne());
        return new Polynomial<>(mc, map, 1);
    }

    /**
     * Returns the polynomial <code>ax<sup>n</sup>+b</code>.
     *
     * @param a the coefficient of x
     * @param n the power of x, it is required to be bigger than 1.
     * @param b the constance term
     */
    public static <T> Polynomial<T> twoTerms(MathCalculator<T> mc, T a, int n, T b) {
        TreeMap<Integer, T> map = new TreeMap<>();
        if (n < 1) {
            throw new IllegalArgumentException("n < 1");
        }
        map.put(n, a);
        map.put(0, b);
        return new Polynomial<>(mc, map, n);
    }

    public static <T> Polynomial<T> constant(MathCalculator<T> mc, T c) {
        NavigableMap<Integer, T> map = new TreeMap<>();
        if (mc.isZero(c)) {
            return zero(mc);
        }
        map.put(0, c);
        return new Polynomial<>(mc, map, 0);
    }

    /**
     * Returns a polynomial of <code>sigma(coes[i]*x^i)</code>
     *
     * @param coes coefficients of the polynomial, <code>null</code> will be treated as zero.
     */
    @SafeVarargs
    public static <T> Polynomial<T> valueOf(MathCalculator<T> mc, T... coes) {
        if (coes.length == 0) {
            return zero(mc);
        }
        int max = coes.length - 1;
        while (coes[max] == null || mc.isZero(coes[max])) {
            max--;
        }
        if (max <= 0) {
            return zero(mc);
        }
        TreeMap<Integer, T> map = new TreeMap<>();
        for (int i = max; i > -1; i--) {
            if (coes[i] != null && !mc.isZero(coes[i])) {
                map.put(i, coes[i]);
            }
        }
        if (map.isEmpty()) {
            return zero(mc);
        }
        return new Polynomial<>(mc, map, max);
    }

    public static <T> Polynomial<T> valueOf(MathCalculator<T> mc, List<T> coes) {
        if (coes.isEmpty()) {
            return zero(mc);
        }
        int max = coes.size() - 1;
        while (coes.get(max) == null || mc.isZero(coes.get(max))) {
            max--;
        }
        if (max <= 0) {
            return zero(mc);
        }
        TreeMap<Integer, T> map = new TreeMap<>();
        for (int i = max; i > -1; i--) {
            T t = coes.get(i);
            if (t != null && !mc.isZero(t)) {
                map.put(i, t);
            }
        }
        if (map.isEmpty()) {
            return zero(mc);
        }
        return new Polynomial<>(mc, map, max);
    }

    /**
     * Builds a polynomial from a list of coefficients and another list containing the corresponding powers.
     */
    private static <T> Polynomial<T> buildFromList(MathCalculator<T> mc, List<T> coes, List<Integer> pow) {
        var map = new TreeMap<Integer, T>();
        int maxPow = 0;
        for (int i = 0; i < coes.size(); i++) {
            if (mc.isZero(coes.get(i))) {
                continue;
            }
            map.put(pow.get(i), coes.get(i));
            int p = pow.get(i);
            if (p > maxPow) {
                maxPow = p;
            }
        }
        if (map.isEmpty()) {
            return zero(mc);
        }
        return new Polynomial<>(mc, map, maxPow);
    }


    /**
     * Returns <code>x^p</code>, where p is a non-negative integer.
     */
    public static <T> Polynomial<T> powerX(int p, MathCalculator<T> mc) {
        if (p < 0) {
            throw new IllegalArgumentException("p<0");
        }
        var map = new TreeMap<Integer, T>();
        map.put(p, mc.getOne());
        return new Polynomial<>(mc, map, p);
    }

    /**
     * Returns <code>k*x^p</code>, where p is a non-negative integer.
     */
    public static <T> Polynomial<T> powerX(int p, T k, MathCalculator<T> mc) {
        if (p < 0) {
            throw new IllegalArgumentException("p<0");
        }
        if (mc.isZero(k)) {
            return zero(mc);
        }
        var map = new TreeMap<Integer, T>();
        map.put(p, k);
        return new Polynomial<>(mc, map, p);
    }

    /**
     * Converts the given polynomial to a polynomial of the given character {@code ch}.
     *
     * @throws ArithmeticException if the polynomial has a fraction power or a negative power for the character
     *                             (such as x^0.5 or x^(-2)).
     */
    public static Polynomial<Multinomial> fromMultinomial(Multinomial p, String ch) {
        TreeMap<Integer, Multinomial> map = new TreeMap<>();
        int max = 0;
        for (Term f : p.getTerms()) {
            Fraction powf = f.getCharacterPower(ch);
            if (powf.isNegative() || !powf.isInteger()) {
                throw new ArithmeticException("Unsupported exponent for:[" + ch + "] in " + f.toString());
            }
            int pow = powf.intValue();
            if (pow > max) {
                max = pow;
            }
            Multinomial coe = Multinomial.monomial(f.removeChar(ch));
            map.compute(pow, (x, y) -> {
                if (y == null) {
                    return coe;
                } else {
                    return y.add(coe);
                }
            });
        }
        return new Polynomial<>(Multinomial.getCalculator(), map, max);
    }

    /**
     * Converts a polynomial to a PolynomialX.
     */
    public static <T> Polynomial<T> fromPolynomial(IPolynomial<T> p, MathCalculator<T> mc) {
        if (p instanceof Polynomial) {
            return (Polynomial<T>) p;
        }
        int degree = p.getDegree();
        if (degree == 0) {
            return zero(mc);
        }
        NavigableMap<Integer, T> map = new TreeMap<>();
        for (int i = 0; i < degree; i++) {
            T coe = p.getCoefficient(i);
            if (!mc.isZero(coe)) {
                map.put(i, coe);
            }
        }
        T lead = p.getCoefficient(degree);
        if (mc.isZero(lead)) {
            throw new ArithmeticException("The top term of a polynomial is zero!");
        }
        map.put(degree, lead);
        if (map.isEmpty()) {
            throw new ArithmeticException("All terms of a polynomial of non-zero degree are zero!");
        }
        return new Polynomial<>(mc, map, degree);
    }

    /**
     * Returns a polynomial that is equal to <code>multiplyAll(x-roots[i])</code>.
     * If <code>roots.length==0</code>, then the result is <code>1</code>
     *
     * @param mc    a MathCalculator
     * @param roots the roots
     */
    @SafeVarargs
    public static <T> Polynomial<T> ofRoots(MathCalculator<T> mc, T... roots) {
        return ofRoots(mc, roots, 0, roots.length);
    }

    /**
     * Returns a polynomial of <code>x - root</code>
     */
    public static <T> Polynomial<T> ofRoot(MathCalculator<T> mc, T root) {
        return valueOf(mc, mc.negate(root), mc.getOne());
    }

    /**
     * Returns a polynomial of <code>multiplyAll(x-roots[i], i from startInclusive to endExclusive)</code>.
     *
     * @param mc             a MathCalculator
     * @param rts            the roots
     * @param startInclusive index of i to start with(inclusive)
     * @param endExclusive   index of i to end with(exclusive)
     */
    public static <T> Polynomial<T> ofRoots(MathCalculator<T> mc, T[] rts, int startInclusive, int endExclusive) {
        if (startInclusive == endExclusive) {
            return one(mc);
        }
        if (startInclusive == endExclusive - 1) {
            return ofRoot(mc, rts[startInclusive]);
        }
        int mid = (startInclusive + endExclusive) / 2;
        return ofRoots(mc, rts, startInclusive, mid).multiply(ofRoots(mc, rts, mid, endExclusive));
    }

    /**
     * Returns a polynomial that satisfies the given points, and its degree is
     * <code>points.length-1</code>.
     * If <code>points.length==0</code>, then an exception will be thrown.
     *
     * @param points a list of points, not empty
     */
    @SafeVarargs
    public static <T> Polynomial<T> lagrangeInterpolation(Point<T>... points) {
        //sigma(multiplyAll((x-x_j)/(x_i-x_j),j!=i),i from 0 to points.length-1)
        if (points.length == 0) {
            throw new IllegalArgumentException("points.length==0");
        }
        var mc = points[0].getMathCalculator();
        Polynomial<T> re = zero(mc);
        @SuppressWarnings("unchecked") T[] roots = (T[]) new Object[points.length];
        @SuppressWarnings("unchecked") Polynomial<T>[] rootPoly = (Polynomial<T>[]) new Polynomial[points.length];
        for (int i = 0; i < points.length; i++) {
            var xi = points[i].x;
            roots[i] = xi;
            rootPoly[i] = ofRoot(mc, xi);
        }
        for (int i = 0; i < points.length; i++) {
            var t = points[i].y;
            if (mc.isZero(t)) {
                continue;
            }
            var m1 = i == 0 ? one(mc) : NumberModelUtils.multiplyAll(rootPoly, 0, i);
            var m2 = i == points.length - 1 ? one(mc) : NumberModelUtils.multiplyAll(rootPoly, i + 1, points.length);
            var single = m1.multiply(m2);
            var curRoot = roots[i];
            IntFunction<T> f = (int j) -> mc.subtract(curRoot, roots[j]);
            var deno = mc.multiply(CalculatorUtils.multiplyAll(0, i, mc, f),
                    CalculatorUtils.multiplyAll(i + 1, points.length, mc, f));
            t = mc.divide(t, deno);
            single = single.multiply(t);
            re = re.add(single);
        }
        return re;
    }

    /**
     * Returns the polynomial of <code>(x-x0)<sup>n</sup></code>
     */
    public static <T> Polynomial<T> binomialPower(T x0, int n, MathCalculator<T> mc) {
        if (mc.isZero(x0)) {
            return powerX(n, mc);
        }
        var map = new TreeMap<Integer, T>();
        var x0Power = mc.getOne();
        var binomialCoes = CombUtils.binomialsOf(n);
        for (int i = n; i >= 0; i--) {
            T coe = mc.multiplyLong(x0Power, binomialCoes.get(i));
            if (mc.isZero(coe)) {
                continue;
            }
            map.put(i, coe);
            //noinspection SuspiciousNameCombination
            x0Power = mc.multiply(x0Power, x0);
        }
        return new Polynomial<>(mc, map, n);
    }

    private static <T> T[] fillPowArr(T x, int n, MathCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        T[] re = (T[]) new Object[n + 1];
        T t = x;
        for (int i = 0; i <= n; i++) {
            re[i] = t;
            t = mc.multiply(x, t);
        }
        return re;
    }

    /**
     * Returns the polynomial of <code>(ax-b)<sup>n</sup></code>
     */
    public static <T> Polynomial<T> binomialPower(T a, T b, int n, MathCalculator<T> mc) {
        var map = new TreeMap<Integer, T>();

        T[] aPow = fillPowArr(a, n, mc);
        T[] bPow = fillPowArr(b, n, mc);
        var binomialCoes = CombUtils.binomialsOf(n);
        for (int i = n; i >= 0; i--) {
            T coe = mc.multiplyLong(mc.multiply(aPow[i], bPow[n - i]), binomialCoes.get(n));
            if (mc.isZero(coe)) {
                continue;
            }
            map.put(n, coe);
        }
        return fromMap(map, mc);
    }

    /**
     * Gets a calculator of the specific type of PolynomialX
     */
    public static <T> PolynomialCalculator<T> getCalculator(MathCalculator<T> mc) {
        return new PolynomialCalculator<>(mc);
    }

    /**
     * Returns a
     *
     * @param p an irreducible polynomial.
     * @return a
     */
    public static <T> ModPolyCalculator<T> getModCalculator(Polynomial<T> p) {
        return new ModPolyCalculator<>(p);
    }


    public static class PolynomialCalculator<T> extends MathCalculatorAdapter<Polynomial<T>>
            implements MathCalculatorHolder<T>, NTCalculator<Polynomial<T>> {
        protected final MathCalculator<T> mc;
        protected final Polynomial<T> zero, one;

        /**
         *
         */
        PolynomialCalculator(MathCalculator<T> mc) {
            this.mc = mc;
            zero = zero(mc);
            one = one(mc);
        }

        /*
         * @see cn.ancono.math.MathCalculatorHolder#getMathCalculator()
         */
        @NotNull
        @Override
        public MathCalculator<T> getMathCalculator() {
            return mc;
        }


        /*
         * @see cn.ancono.math.MathCalculator#isEqual(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isEqual(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return IPolynomial.isEqual(para1, para2, mc::isEqual);
        }

        /*
         * @see cn.ancono.math.MathCalculator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return para1.compareTo(para2);
        }


        @Override
        public BigInteger asBigInteger(Polynomial<T> x) {
            throw new UnsupportedOperationException();
        }

        /*
         * @see cn.ancono.math.MathCalculator#add(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> add(@NotNull Polynomial<T> x, @NotNull Polynomial<T> y) {
            return x.add(y);
        }


        /*
         * @see cn.ancono.math.MathCalculator#negate(java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> negate(@NotNull Polynomial<T> para) {
            return para.negate();
        }


        /*
         * @see cn.ancono.math.MathCalculator#subtract(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> subtract(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return para1.subtract(para2);
        }

        /*
         * @see cn.ancono.math.MathCalculator#getZero()
         */
        @NotNull
        @Override
        public Polynomial<T> getZero() {
            return zero;
        }

        /*
         * @see cn.ancono.math.MathCalculator#isZero(java.lang.Object)
         */
        @Override
        public boolean isZero(@NotNull Polynomial<T> para) {
            return isEqual(zero, para);
        }

        /*
         * @see cn.ancono.math.MathCalculator#multiply(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> multiply(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return para1.multiply(para2);
        }

        /*
         * @see cn.ancono.math.MathCalculator#divide(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> divide(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            Pair<Polynomial<T>, Polynomial<T>> p = divideAndReminder(para1, para2);
            if (!isZero(p.getSecond())) {
                throw new UnsupportedCalculationException("Reminder!= 0, see divideAndReminder");
            }
            return p.getFirst();
        }

        /**
         * Returns a pair of quotient and the reminder of the division of two PolynomialX.
         * <pre>p1 = k*p2 + r</pre> The degree of {@code r} is smaller than {@code p2}.
         *
         * @return a pair of the quotient and the reminder.
         */
        public Pair<Polynomial<T>, Polynomial<T>> divideAndReminder(Polynomial<T> p1, Polynomial<T> p2) {
            var pair = p1.divideAndRemainder(p2);
            return new Pair<>(pair.getFirst(), pair.getSecond());
        }

        /**
         * Returns the reminder of the two polynomials.
         */
        @Override
        public Polynomial<T> reminder(Polynomial<T> a, Polynomial<T> b) {
            return a.remainder(b);
        }

        /*
         * @see cn.ancono.math.MathCalculator#getOne()
         */
        @NotNull
        @Override
        public Polynomial<T> getOne() {
            return one;
        }


        /*
         * @see cn.ancono.math.MathCalculator#multiplyLong(java.lang.Object, long)
         */
        @NotNull
        @Override
        public Polynomial<T> multiplyLong(@NotNull Polynomial<T> p, long l) {
            if (l == 1) {
                return p;
            }
            if (l == 0) {
                return zero;
            }
            if (l == -1) {
                return negate(p);
            }
            NavigableMap<Integer, T> nmap = p.getCoefficientMap();
            //noinspection SuspiciousNameCombination
            CollectionSup.modifyMap(nmap, (x, y) -> mc.multiplyLong(y, l));
            return new Polynomial<>(mc, nmap, p.degree);
        }

        /*
         * @see cn.ancono.math.MathCalculator#divideLong(java.lang.Object, long)
         */
        @NotNull
        @Override
        public Polynomial<T> divideLong(@NotNull Polynomial<T> p, long n) {
            if (n == 0) {
                throw new ArithmeticException("Divide by zero");
            }
            if (n == 1) {
                return p;
            }
            if (n == -1) {
                return negate(p);
            }
            NavigableMap<Integer, T> nmap = p.getCoefficientMap();
            //noinspection SuspiciousNameCombination
            CollectionSup.modifyMap(nmap, (x, y) -> mc.divideLong(y, n));
            return new Polynomial<>(mc, nmap, p.degree);
        }

        /*
         * @see cn.ancono.math.MathCalculator#squareRoot(java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> squareRoot(@NotNull Polynomial<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.ancono.math.MathCalculator#nroot(java.lang.Object, long)
         */
        @NotNull
        @Override
        public Polynomial<T> nroot(@NotNull Polynomial<T> x, long n) {
            throw new UnsupportedCalculationException();
        }


        /*
         * @see cn.ancono.math.MathCalculator#pow(java.lang.Object, long)
         */
        @NotNull
        @Override
        public Polynomial<T> pow(@NotNull Polynomial<T> p, long exp) {
            if (exp == 1) {
                return p;
            }
            if (p.degree == 0) {
                //single
                return constant(mc, mc.pow(p.getCoefficient(0), exp));
            }
            long mp = exp * p.degree;
            if (mp > Integer.MAX_VALUE || mp < 0) {
                throw new ArithmeticException("Too big for exp=" + exp);
            }
            NavigableMap<Integer, T> map = ModelPatterns.binaryProduce(exp, one.map, p.map, (x, y) -> multiplyToMap(x, y, mc));
            return new Polynomial<>(mc, map, (int) mp);
        }

        /*
         * @see cn.ancono.math.MathCalculator#constantValue(java.lang.String)
         */
        @Override
        public Polynomial<T> constantValue(@NotNull String name) {
            return constant(mc, mc.constantValue(name));
        }

        /*
         * @see cn.ancono.math.numberModels.MathCalculatorAdapter#abs(java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> abs(@NotNull Polynomial<T> para) {
            if (mc.compare(para.getCoefficient(para.degree), mc.getZero()) < 0) {
                return negate(para);
            }
            return para;
        }

        /**
         * Returns a the greatest common divisor of {@code a} and {@code b}. A greatest common divisor of polynomial
         * {@code p} and {@code q}
         * is a polynomial {@code d} that divides {@code p} and {@code q} such that every common divisor of {@code p}
         * and {@code q} also divides {@code d}.
         *
         * @return the  greatest common divisor of {@code a} and {@code b}, whose leading coefficient is one.
         */
        @Override
        public Polynomial<T> gcd(Polynomial<T> a, Polynomial<T> b) {
            return a.gcd(b);
        }


        /*
         * @see cn.ancono.math.MathCalculator#getNumberClass()
         */
        @NotNull
        @Override
        public Class<?> getNumberClass() {
            return Polynomial.class;
        }

        /*
         * @see cn.ancono.math.numberTheory.NTCalculator#isInteger(java.lang.Object)
         */
        @Override
        public boolean isInteger(Polynomial<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.ancono.math.numberTheory.NTCalculator#isQuotient(java.lang.Object)
         */
        @Override
        public boolean isQuotient(Polynomial<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.ancono.math.numberTheory.NTCalculator#mod(java.lang.Object, java.lang.Object)
         */
        @Override
        public Polynomial<T> mod(Polynomial<T> a, Polynomial<T> b) {
            return reminder(a, b);
        }

        /*
         * @see cn.ancono.math.numberTheory.NTCalculator#divideToInteger(java.lang.Object, java.lang.Object)
         */
        @Override
        public Polynomial<T> divideToInteger(Polynomial<T> a, Polynomial<T> b) {
            return divideAndReminder(a, b).getFirst();
        }
    }

    public static class ModPolyCalculator<T> extends PolynomialCalculator<T> {
        private final Polynomial<T> p;

        public ModPolyCalculator(Polynomial<T> p) {
            super(p.getMathCalculator());
            this.p = p;
        }

        @Override
        public boolean isEqual(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return super.isEqual(mod(para1), mod(para2));
        }

        @Override
        public @NotNull Polynomial<T> add(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return mod(super.add(mod(para1), mod(para2)));
        }

        @Override
        public @NotNull Polynomial<T> subtract(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return mod(super.subtract(mod(para1), mod(para2)));
        }

        @Override
        public boolean isZero(@NotNull Polynomial<T> para) {
            return super.isZero(mod(para));
        }

        @Override
        public @NotNull Polynomial<T> multiply(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return mod(super.multiply(mod(para1), mod(para2)));
        }

        @Override
        public @NotNull Polynomial<T> divide(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return super.divide(para1, para2);
        }

        @Override
        public Pair<Polynomial<T>, Polynomial<T>> divideAndReminder(Polynomial<T> p1, Polynomial<T> p2) {
            return super.divideAndReminder(mod(p1), mod(p2));
        }

        @Override
        public Polynomial<T> reminder(Polynomial<T> a, Polynomial<T> b) {
            return super.reminder(a, b);
        }

        @Override
        public @NotNull Polynomial<T> pow(@NotNull Polynomial<T> p, long exp) {
            if (exp == 1) {
                return p;
            }
            if (p.degree == 0) {
                //single
                return constant(mc, mc.pow(p.getCoefficient(0), exp));
            }
            long mp = exp * p.degree;
            if (mp > Integer.MAX_VALUE || mp < 0) {
                throw new ArithmeticException("Too big for exp=" + exp);
            }
            return ModelPatterns.binaryProduce(exp, one, p, this::multiply);
        }

        public Polynomial<T> mod(Polynomial<T> a) {
            if (a.degree < p.degree) {
                return a;
            }
            return mod(a, p);
        }

        @Override
        public Polynomial<T> mod(Polynomial<T> a, Polynomial<T> b) {
            return super.mod(a, b);
        }

        @Override
        public Polynomial<T> divideToInteger(Polynomial<T> a, Polynomial<T> b) {
            return super.divideToInteger(a, b);
        }

        @NotNull
        @Override
        public Polynomial<T> reciprocal(@NotNull Polynomial<T> x) {
            var t = x.gcdUV(p); //ut + vp = 1
            var gcd = t.getFirst();
            if (!gcd.isOne()) {
                throw new UnsupportedCalculationException("Polynomial (" + x + ") is not invertible!");
            }
            var u = t.getSecond();
            return mod(u);
        }
    }


    public static <T> NumberFormatter<Polynomial<T>> composedFormatter(NumberFormatter<T> formatter) {
        return (p, mc) -> p.toString(formatter);
    }

    /**
     * Simplify `f` and `g` as if they are numerator and denominator.
     */
    public static <T> Pair<Polynomial<T>, Polynomial<T>> simplifyFraction(Polynomial<T> f, Polynomial<T> g) {
        var gcd = f.gcd(g);
        return new Pair<>(f.divideToInteger(gcd), g.divideToInteger(gcd));
    }

    private static <T> Pair<Polynomial<T>, Polynomial<T>> adjustSign(Polynomial<T> f, Polynomial<T> g,
                                                                     MathCalculator<T> mc,
                                                                     Simplifier<T> sim) {
        if (mc.isComparable()) {
            if (mc.compare(g.first(), mc.getZero()) < 0) {
                return new Pair<>(f.negate(), g.negate());
            }
        }
        return new Pair<>(f, g);
    }

    /**
     * Simplifies the coefficient of two polynomials.
     */
    public static <T> Pair<Polynomial<T>, Polynomial<T>> simplifyCoefficient(Polynomial<T> f, Polynomial<T> g,
                                                                             MathCalculator<T> mc,
                                                                             Simplifier<T> sim) {
        List<T> list = new ArrayList<>();
        var pow = new ArrayList<Integer>();
        for (var en : f.map.entrySet()) {
            if (!mc.isZero(en.getValue())) {
                list.add(en.getValue());
                pow.add(en.getKey());
            }
        }
        var pos = list.size();
        for (var en : g.map.entrySet()) {
            if (!mc.isZero(en.getValue())) {
                list.add(en.getValue());
                pow.add(en.getKey());
            }
        }
        list = sim.simplify(list);
        f = buildFromList(mc, list.subList(0, pos), pow.subList(0, pos));
        g = buildFromList(mc, list.subList(pos, list.size()), pow.subList(pos, pow.size()));
        return adjustSign(f, g, mc, sim);
    }

    /**
     * Simplify `f` and `g` as if they are numerator and denominator.
     */
    public static <T> Pair<Polynomial<T>, Polynomial<T>> simplifyFraction(Polynomial<T> f, Polynomial<T> g,
                                                                          Simplifier<T> sim) {
        var mc = f.getMathCalculator();
        var gcd = f.gcd(g);
        f = f.divideToInteger(gcd);
        g = g.divideToInteger(gcd);
        if (g.isConstant()) {
            if (g.isZero()) {
                return new Pair<>(one(mc), g);
            }
            return new Pair<>(f.divide(g.constant()), one(mc));
        }
        return simplifyCoefficient(f, g, mc, sim);
    }


    private static class PolySimplifier<T> implements Simplifier<Polynomial<T>> {
        @Override
        public List<Polynomial<T>> simplify(List<Polynomial<T>> numbers) {
            //TODO
            return null;
        }

        @Override
        public Polynomial<T> simplify(Polynomial<T> x) {
            //TODO
            return null;
        }

        @Override
        public Pair<Polynomial<T>, Polynomial<T>> simplify(Polynomial<T> a, Polynomial<T> b) {
            //TODO
            return null;
        }
    }

//    public static void main(String[] args) {
//		MathCalculator<Integer> mc = Calculators.getCalculatorInteger();
//        var p = Polynomial.valueOf(mc, 1, 2, 3);
//        print(p);
//        print(p.multiply(p));
//    }
//        var mc = Fraction.getCalculator();
//        var p1 = Point.valueOf(Fraction.valueOf("-1"),Fraction.valueOf("1"),mc);
////        var p2 = Point.valueOf(Fraction.valueOf("0"),Fraction.valueOf("0"),mc);
////        var p3 = Point.valueOf(Fraction.valueOf("1"),Fraction.valueOf("1"),mc);
////        var p = Polynomial.valueOf(Calculators.getCalculatorLongExact(), 1L, 1L).mapTo(Fraction::valueOf,mc);
//////        print(lagrangeInterpolation(p1,p2,p3));
////        print(p);
////        print(p.difference().sumOfN());
////    }
//        print(p.dropHigherTerms(10));
//        print(p.shift(4));
//        print(p.shift(-4));
//        p = Polynomial.valueOf(mc, 1, 0, 0, 1, 3, 4);
//        print(p);
//        print(p.dropHigherTerms(3));
////		Polynomial<Integer> p1 = Polynomial.valueOf(mc, 6,7,1);
////		var pf = p1.mapTo(Fraction::valueOf,Fraction.getCalculator());
////		print(pf);
////		print(pf.integration());
//        var mc = Multinomial.getCalculator();
//        var f = valueOf(mc,Multinomial.valueOf("-x-3"),Multinomial.valueOf("2"),Multinomial.ZERO,Multinomial.ONE);
//        var g = valueOf(mc,Multinomial.valueOf("1-y"),Multinomial.valueOf("-1"),Multinomial.ONE);
//        print(f.sylvesterMatrix(g).calDet());
////        print(f.determinant());
////        var f2 = f.substitute(Polynomial.valueOf(Multinomial.getCalculator(),Multinomial.ZERO,Multinomial.ZERO,Multinomial.ONE));
////        print(f2.determinant());
////        print(f.determinant().pow(2).multiply(Multinomial.valueOf("16a^2*c")));
////		Polynomial<Integer> p2 = Polynomial.valueOf(mc, -6,-5,1);
////		PolynomialCalculator<Integer> mmc = getCalculator(mc);
////		Polynomial<Integer> re = mmc.add(p1, p2);
////		print(p1);
////		print(p2);
//////		print(re);
//////		print(mmc.multiply(p1, p2));//
////		print(mmc.divideAndReminder(p1, p2));
//////		print(mmc.pow(p2, 5));
//////		print(mmc.gcd(mmc.pow(p2, 5), mmc.pow(p2, 3)));
////		print(mmc.gcd(p1, p2));


}
