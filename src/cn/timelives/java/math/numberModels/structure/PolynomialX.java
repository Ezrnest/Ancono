/**
 * 2017-11-21
 */
package cn.timelives.java.math.numberModels.structure;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.algebra.Polynomial;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.algebra.linearAlgebra.Vector;
import cn.timelives.java.math.geometry.analytic.planeAG.Point;
import cn.timelives.java.math.numberModels.*;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.api.RingNumberModel;
import cn.timelives.java.math.numberTheory.NTCalculator;
import cn.timelives.java.math.numberTheory.combination.CFunctions;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.ModelPatterns;
import cn.timelives.java.utilities.structure.Pair;
import cn.timelives.java.utilities.structure.WithInt;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;

import static cn.timelives.java.utilities.Printer.print;


/**
 * An implement for polynomial.
 *
 * @author liyicheng
 * @see Polynomial
 * 2017-11-21 17:10
 */
public final class PolynomialX<T> extends MathObject<T> implements Polynomial<T>, Comparable<PolynomialX<T>>,
        RingNumberModel<PolynomialX<T>> {
    /**
     * A map.
     */
    private final NavigableMap<Integer, T> map;
    private final int degree;


    PolynomialX(MathCalculator<T> calculator, NavigableMap<Integer, T> map, int degree) {
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
     * @see cn.timelives.java.math.algebra.Polynomial#getMaxPower()
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
     * @see cn.timelives.java.math.algebra.Polynomial#getCoefficient(int)
     */
    @Override
    public T getCoefficient(int n) {
        if (n < 0 || n > degree) {
            throw new IndexOutOfBoundsException("For n=" + n);
        }
        return getCoefficient0(n);
    }

    public Vector<T> coefficientVector() {
        return Polynomial.coefficientVector(this, getMathCalculator());
    }


    @Override
    @NotNull
    public PolynomialX<T> add(PolynomialX<T> y) {
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
        return new PolynomialX<>(mc, map, mp);
    }

    private void addToMap(PolynomialX<T> para1, TreeMap<Integer, T> map, int mp1, int mp2, MathCalculator<T> mc) {
        for (int i = mp1; i > mp2; i--) {
            Integer n = i;
            T a = para1.getCoefficient0(n);
            if (!mc.isZero(a))
                map.put(n, a);
        }
    }

    @Override
    public PolynomialX<T> negate() {
        var para = this;
        var mc = getMc();
        NavigableMap<Integer, T> nmap = para.getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            en.setValue(mc.negate(en.getValue()));
        }
        return new PolynomialX<>(mc, nmap, para.degree);
    }

    @Override
    @NotNull
    public PolynomialX<T> subtract(PolynomialX<T> y) {
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
        return new PolynomialX<>(mc, map, mp);
    }

    @Override
    @NotNull
    public PolynomialX<T> multiply(PolynomialX<T> y) {
        var para1 = this;
        var mc = getMc();
        NavigableMap<Integer, T> map = multiplyToMap(para1.map, y.map, mc);
        if (map.isEmpty()) {
            return zero(mc);
        }
        return new PolynomialX<>(mc, map, para1.degree + y.degree);
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
     * @param x value of x to substitute
     */
    public T compute(T x) {
        T re = getCoefficient(degree);
        for (int i = degree - 1; i > -1; i--) {
            re = getMc().multiply(x, re);
            re = getMc().add(getCoefficient(i), re);
        }
        return re;
    }

    public boolean isZero(){
        return isConstant() && getMc().isZero(getCoefficient(0));
    }

    /**
     * Divides this polynomial by a number to get a new polynomial whose leading coefficient is one.
     */
    public PolynomialX<T> unit() {
        if (getMc().isEqual(getMc().getOne(), getCoefficient(degree))) {
            return this;
        }
        T k = getCoefficient(degree);
        return divide(k);
    }

    /**
     * Returns <code>k*this</code>
     */
    public PolynomialX<T> multiply(T k){
        var mc = getMc();
        if(mc.isZero(k)){
            return zero(mc);
        }
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            var t = mc.multiply(en.getValue(), k);
            en.setValue(t);
        }
        return new PolynomialX<>(mc, nmap, degree);
    }

    /**
     * Returns <code>1/k*this</code>
     */
    public PolynomialX<T> divide(T k){
        var mc = getMc();
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            en.setValue(mc.divide(en.getValue(), k));
        }
        return new PolynomialX<>(mc, nmap, degree);
    }

    public Pair<PolynomialX<T>,PolynomialX<T>> divideAndRemainder(PolynomialX<T> p2){
        var p1 = this;
        var mc = getMc();
        if (p2.isZero()) {
            throw new ArithmeticException("divide by zero!");
        }
        int mp1 = p1.degree, mp2 = p2.degree;
        if (mp2 > mp1) {
            return new Pair<>(zero(mc), p1);
        }
        if (isZero()) {
            var zero = zero(mc);
            return new Pair<>(zero, zero);
        }
        NavigableMap<Integer, T> remains = p1.getCoefficientMap();
        TreeMap<Integer, T> quotient = new TreeMap<>();
        T first = p2.getCoefficient(mp2);
        List<WithInt<T>> toSubtract = new ArrayList<>(p2.map.size() - 1);
        for (Entry<Integer, T> en : p2.map.entrySet()) {
            int n = en.getKey();
            if (n != mp2) {
                toSubtract.add(new WithInt<>(n, en.getValue()));
            }
        }
        //noinspection Duplicates
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
        PolynomialX<T> qm = new PolynomialX<>(mc, quotient, mp1 - mp2);
        PolynomialX<T> rm = remains.isEmpty() ? zero(mc) : new PolynomialX<>(mc, remains, remains.lastKey());
        return new Pair<>(qm, rm);
    }

    /**
     * Returns the difference of this polynomial. Assuming this is <code>f(x)</code>, then
     * the result is <code>f(x)-f(x-1)</code>.
     *
     * @return <code>Δf(x)</code>
     */
    public PolynomialX<T> difference() {
        var mc = getMc();
        if (degree == 0) {
            return zero(mc);
        }
        //x^n - (x-1)^n = sigma((-1)^(n-i+1)*C(n,i)*x^i,i from 0 to n-1)
        NavigableMap<Integer, T> nmap = new TreeMap<>();
        for (var en : map.entrySet()) {
            int n = en.getKey();
            T coe = en.getValue();
            var binomials = CFunctions.binomialsOf(n).iterator();
            for (int i = 0; i < n; i++) {
                var t1 = mc.multiplyLong(coe, binomials.next());
                T t2;
                if ((n-i) % 2 == 0) {
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
        return new PolynomialX<>(mc, nmap, degree - 1);
    }

    /**
     * Returns the derivative of this polynomial(formally), that is
     * <text>sigma(n*a<sub>n</sub>x<sup>n-1</sup>, n from 1 to (<i>degree of this</i>-1))</text>
     * @return the derivative of this polynomial(formally)
     */
    public PolynomialX<T> derivative(){
        var mc = getMc();
        if(degree == 0){
            return zero(mc);
        }
        NavigableMap<Integer,T> nmap = new TreeMap<>();
        for(var en : map.entrySet()){
            int n = en.getKey();
            if(n == 0){
                continue;
            }
            T coe = en.getValue();
            nmap.put(n-1,mc.multiplyLong(coe,n));
        }
        return new PolynomialX<>(mc, nmap, degree - 1);
    }


    /**
     * Assuming this polynomial is <code>f(x)</code>, this method returns the
     * result of <code>sigma(f(i),i from 1 to n)</code> as a polynomial.
     */
    public PolynomialX<T> sumOfN(){
        //use lagrange
        var points = new Point[degree+2];
        var mc = getMc();
        points[0] = Point.pointO(mc);
        for(int i=1;i<points.length;i++){
            points[i] = Point.valueOf(CalculatorUtils.valueOfLong(i,mc),CalculatorUtils.sigma(1,i+1,mc,j ->
                    compute(CalculatorUtils.valueOfLong(j,mc))),mc);
        }
        print(points);
        //noinspection unchecked
        return lagrangeInterpolation(points);
    }


    /**
     * Returns the polynomial representing the result of <code>sigma(x^p,1,n) = 1^p+2^p+3^p+...+n^p</code> where
     * <code>p</code> is a non-negative integer.
     *
     * @param p  a non-negative integer
     * @param mc a MathCalculator
     */
    public static <T> PolynomialX<T> sumOfXP(int p, MathCalculator<T> mc) {
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
        return powerX(p,mc).sumOfN();
    }

    private static <T> PolynomialX<T> sumOfX1(MathCalculator<T> mc) {
        //1+2+...n = (n+1)n / 2 = 1/2 * n^2 + 1/2 * n
        var half = mc.divideLong(mc.getOne(), 2L);
        return PolynomialX.valueOf(mc, mc.getZero(), half, half);
    }

    private static <T> PolynomialX<T> sumOfX2(MathCalculator<T> mc) {
        //1^2+2^2+...n^2 = n(n+1)(2n+1)/6 = 1/3*n^3+1/2*n^2+1/6*n
        var c1 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 3), mc);
        var c2 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 2), mc);
        var c3 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 6), mc);
        return PolynomialX.valueOf(mc, mc.getZero(), c3, c2, c1);
    }

    private static <T> PolynomialX<T> sumOfX3(MathCalculator<T> mc) {
        //1^2+2^2+...n^2 = (n(n+1)/2)^2 = 1/4*n^4+1/2*n^3+1/4*n^2
        var c1 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 4), mc);
        var c2 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 2), mc);
        var o = mc.getZero();
        return PolynomialX.valueOf(mc, o, o, c1, c2, c1);
    }


    /*
     * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
     */
    @Override
    public <N> PolynomialX<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
        TreeMap<Integer, N> nmap = new TreeMap<>();
        for (Entry<Integer, T> en : map.entrySet()) {
            nmap.put(en.getKey(), mapper.apply(en.getValue()));
        }
        return new PolynomialX<>(newCalculator, nmap, degree);
    }

    /*
     * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
     */
    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (!(obj instanceof PolynomialX)) {
            return false;
        }
        return Polynomial.isEqual(this, (PolynomialX<T>) obj, getMc()::isEqual);
    }


    /*
     * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
     */
    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (!(obj instanceof PolynomialX)) {
            return false;
        }
        return Polynomial.isEqual(this, (PolynomialX<N>) obj, (x, y) -> getMc().isEqual(x, mapper.apply(y)));
    }

    /*
     * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
     */
    @NotNull
    @Override
    public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
        return Polynomial.stringOf(this, getMc(), nf);
    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(PolynomialX<T> o) {
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
     * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
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

    private static final Map<MathCalculator<?>, PolynomialX<?>> zeros = new HashMap<>();

    /**
     * Adds all the polynomials.
     * @param ps a non-empty array
     */
    @SafeVarargs
    public static <T> PolynomialX<T> addAll(PolynomialX<T>...ps){
        return NumberModelUtils.sigma(ps,0,ps.length);
    }

    /**
     * Multiplies all the polynomials.
     * @param ps a non-empty array
     */
    @SafeVarargs
    public static <T> PolynomialX<T> multiplyAll(PolynomialX<T>...ps){
        return NumberModelUtils.multiplyAll(ps,0,ps.length);
    }

    /**
     * Returns zero.
     */
    public static <T> PolynomialX<T> zero(MathCalculator<T> mc) {
        @SuppressWarnings("unchecked")
        PolynomialX<T> zero = (PolynomialX<T>) zeros.get(mc);
        if (zero == null) {
            zero = new PolynomialX<>(mc, Collections.emptyNavigableMap(), 0);
            synchronized (zeros) {
                zeros.put(mc, zero);
            }
        }
        return zero;
    }

    /**
     * Returns the polynomial {@literal 1}。
     */
    public static <T> PolynomialX<T> one(MathCalculator<T> mc) {
        return constant(mc, mc.getOne());
    }

    /**
     * Returns the polynomial {@literal x}.
     */
    public static <T> PolynomialX<T> oneX(MathCalculator<T> mc) {
        TreeMap<Integer, T> map = new TreeMap<>();
        map.put(1, mc.getOne());
        return new PolynomialX<>(mc, map, 1);
    }

    public static <T> PolynomialX<T> constant(MathCalculator<T> mc, T c) {
        NavigableMap<Integer, T> map = new TreeMap<>();
        if (mc.isZero(c)) {
            return zero(mc);
        }
        map.put(0, c);
        return new PolynomialX<>(mc, map, 0);
    }

    /**
     * Returns a polynomial of <code>sigma(coes[i]*x^i)</code>
     * @param coes coefficients of the polynomial, <code>null</code> will be treated as zero.
     */
    @SafeVarargs
    public static <T> PolynomialX<T> valueOf(MathCalculator<T> mc, T... coes) {
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
        return new PolynomialX<>(mc, map, max);
    }


    /**
     * Returns <code>x^p</code>, where p is a non-negative integer.
     */
    public static <T> PolynomialX<T> powerX(int p, MathCalculator<T> mc) {
        if (p < 0) {
            throw new IllegalArgumentException("p<0");
        }
        var map = new TreeMap<Integer, T>();
        map.put(p, mc.getOne());
        return new PolynomialX<>(mc, map, p);
    }

    /**
     * Converts the given polynomial to a polynomial of the given character {@code ch}.
     * @throws ArithmeticException if the polynomial has a fraction power or a negative power for the character
     *                             (such as x^0.5 or x^(-2)).
     */
    public static PolynomialX<Multinomial> fromMultinomial(Multinomial p, String ch) {
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
        return new PolynomialX<>(Multinomial.getCalculator(), map, max);
    }

    /**
     * Converts a polynomial to a PolynomialX.
     */
    public static <T> PolynomialX<T> fromPolynomial(Polynomial<T> p,MathCalculator<T> mc){
        if(p instanceof PolynomialX){
            return (PolynomialX<T>) p;
        }
        int degree = p.getDegree();
        if(degree == 0){
            return zero(mc);
        }
        NavigableMap<Integer,T> map = new TreeMap<>();
        for(int i=0;i<degree;i++){
            T coe = p.getCoefficient(i);
            if(!mc.isZero(coe)){
                map.put(i,coe);
            }
        }
        T lead = p.getCoefficient(degree);
        if(mc.isZero(lead)){
            throw new ArithmeticException("The top term of a polynomial is zero!");
        }
        map.put(degree,lead);
        if(map.isEmpty()){
            throw new ArithmeticException("All terms of a polynomial of non-zero degree are zero!");
        }
        return new PolynomialX<>(mc,map,degree);
    }

    /**
     * Returns a polynomial that is equal to <code>multiplyAll(x-roots[i])</code>.
     * If <code>roots.length==0</code>, then the result is <code>1</code>
     * @param mc a MathCalculator
     * @param roots the roots
     */
    @SafeVarargs
    public static <T> PolynomialX<T> ofRoots(MathCalculator<T> mc, T...roots){
        return ofRoots(mc,roots,0,roots.length);
    }

    /**
     * Returns a polynomial of <code>x - root</code>
     */
    public static <T> PolynomialX<T> ofRoot(MathCalculator<T> mc, T root){
        return valueOf(mc,mc.negate(root),mc.getOne());
    }

    /**
     * Returns a polynomial of <code>multiplyAll(x-roots[i], i from startInclusive to endExclusive)</code>.
     * @param mc a MathCalculator
     * @param rts the roots
     * @param startInclusive index of i to start with(inclusive)
     * @param endExclusive index of i to end with(exclusive)
     */
    public static <T> PolynomialX<T> ofRoots(MathCalculator<T> mc, T[] rts, int startInclusive, int endExclusive){
        if(startInclusive == endExclusive){
            return one(mc);
        }
        if(startInclusive == endExclusive - 1){
            return ofRoot(mc,rts[startInclusive]);
        }
        int mid = (startInclusive+endExclusive)/2;
        return ofRoots(mc,rts,startInclusive,mid).multiply(ofRoots(mc,rts,mid,endExclusive));
    }

    /**
     * Returns a polynomial that satisfies the given points, and its degree is
     * <code>points.length-1</code>.
     * If <code>points.length==0</code>, then an exception will be thrown.
     * @param points a list of points, not empty
     */
    @SafeVarargs
    public static <T> PolynomialX<T> lagrangeInterpolation(Point<T>...points){
        //sigma(multiplyAll((x-x_j)/(x_i-x_j),j!=i),i from 0 to points.length-1)
        if(points.length==0){
            throw new IllegalArgumentException("points.length==0");
        }
        var mc = points[0].getMathCalculator();
        PolynomialX<T> re = zero(mc);
        @SuppressWarnings("unchecked") T[] roots = (T[]) new Object[points.length];
        @SuppressWarnings("unchecked") PolynomialX<T>[] rootPoly = (PolynomialX<T>[]) new PolynomialX[points.length];
        for(int i = 0;i<points.length;i++){
            var xi = points[i].x;
            roots[i] = xi;
            rootPoly[i] = ofRoot(mc,xi);
        }
        for(int i=0;i<points.length;i++){
            var t = points[i].y;
            if(mc.isZero(t)){
                continue;
            }
            var m1 = i == 0 ? one(mc) : NumberModelUtils.multiplyAll(rootPoly,0,i);
            var m2 = i == points.length-1 ? one(mc) : NumberModelUtils.multiplyAll(rootPoly,i+1,points.length);
            var single = m1.multiply(m2);
            var curRoot = roots[i];
            IntFunction<T> f = (int j) -> mc.subtract(curRoot,roots[j]);
            var deno = mc.multiply(CalculatorUtils.multiplyAll(0,i,mc, f),
                    CalculatorUtils.multiplyAll(i+1,points.length,mc, f));
            t = mc.divide(t,deno);
            single = single.multiply(t);
            re = re.add(single);
        }
        return re;
    }

    /**
     * Returns the polynomial of <code>(x-x0)<sup>n</sup></code>
     */
    public static <T> PolynomialX<T> binomialPower(T x0, int n, MathCalculator<T> mc){
        if(mc.isZero(x0)){
            return powerX(n,mc);
        }
        var map = new TreeMap<Integer,T>();
        var x0Power = mc.getOne();
        var binomialCoes = CFunctions.binomialsOf(n);
        for(int i=n;i>=0;i--){
            T coe = mc.multiplyLong(x0Power,binomialCoes.get(n));
            if(mc.isZero(coe)){
                continue;
            }
            map.put(n,coe);
            //noinspection SuspiciousNameCombination
            x0Power = mc.multiply(x0Power,x0);
        }
        return new PolynomialX<>(mc,map,n);
    }

    /**
     * Gets a calculator of the specific type of PolynomialX
     *
     */
    public static <T> PolynomialCalculator<T> getCalculator(MathCalculator<T> mc) {
        return new PolynomialCalculator<>(mc);
    }

    public static class PolynomialCalculator<T> extends MathCalculatorAdapter<PolynomialX<T>>
            implements MathCalculatorHolder<T>, NTCalculator<PolynomialX<T>> {
        private final MathCalculator<T> mc;
        private final PolynomialX<T> zero, one;

        /**
         *
         */
        PolynomialCalculator(MathCalculator<T> mc) {
            this.mc = mc;
            zero = zero(mc);
            one = one(mc);
        }

        /*
         * @see cn.timelives.java.math.MathCalculatorHolder#getMathCalculator()
         */
        @NotNull
        @Override
        public MathCalculator<T> getMathCalculator() {
            return mc;
        }



        /*
         * @see cn.timelives.java.math.MathCalculator#isEqual(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isEqual(@NotNull PolynomialX<T> para1, @NotNull PolynomialX<T> para2) {
            return Polynomial.isEqual(para1, para2, mc::isEqual);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(@NotNull PolynomialX<T> para1, @NotNull PolynomialX<T> para2) {
            return para1.compareTo(para2);
        }


        @Override
        public BigInteger asBigInteger(PolynomialX<T> x) {
            throw new UnsupportedOperationException();
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#add(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public PolynomialX<T> add(@NotNull PolynomialX<T> para1, @NotNull PolynomialX<T> para2) {
            return para1.add(para2);
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#negate(java.lang.Object)
         */
        @NotNull
        @Override
        public PolynomialX<T> negate(@NotNull PolynomialX<T> para) {
            return para.negate();
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#subtract(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public PolynomialX<T> subtract(@NotNull PolynomialX<T> para1, @NotNull PolynomialX<T> para2) {
            return para1.subtract(para2);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#getZero()
         */
        @NotNull
        @Override
        public PolynomialX<T> getZero() {
            return zero;
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#isZero(java.lang.Object)
         */
        @Override
        public boolean isZero(@NotNull PolynomialX<T> para) {
            return isEqual(zero, para);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#multiply(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public PolynomialX<T> multiply(@NotNull PolynomialX<T> para1, @NotNull PolynomialX<T> para2) {
            return para1.multiply(para2);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#divide(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public PolynomialX<T> divide(@NotNull PolynomialX<T> para1, @NotNull PolynomialX<T> para2) {
            Pair<PolynomialX<T>, PolynomialX<T>> p = divideAndReminder(para1, para2);
            if (!isZero(p.getSecond())) {
                throw new UnsupportedCalculationException("Reminder!= 0, see divideAndReminder");
            }
            return p.getFirst();
        }

        /**
         * Returns a pair of quotient and the reminder of the division of two PolynomialX.
         * <pre>p1 = k*p2 + r</pre> The degree of {@code r} is smaller than {@code p2}.
         * @return a pair of the quotient and the reminder.
         */
        public Pair<PolynomialX<T>, PolynomialX<T>> divideAndReminder(PolynomialX<T> p1, PolynomialX<T> p2) {
            if (p2.isZero()) {
                throw new ArithmeticException("divide by zero!");
            }
            int mp1 = p1.degree, mp2 = p2.degree;
            if (mp2 > mp1) {
                return new Pair<>(zero(mc), p1);
            }
            if (p1.isZero()) {
                return new Pair<>(zero, zero);
            }
            NavigableMap<Integer, T> remains = p1.getCoefficientMap();
            TreeMap<Integer, T> quotient = new TreeMap<>();
            T first = p2.getCoefficient(mp2);
            List<WithInt<T>> toSubtract = new ArrayList<>(p2.map.size() - 1);
            for (Entry<Integer, T> en : p2.map.entrySet()) {
                int n = en.getKey();
                if (n != mp2) {
                    toSubtract.add(new WithInt<>(n, en.getValue()));
                }
            }
            //noinspection Duplicates
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
            PolynomialX<T> qm = new PolynomialX<>(mc, quotient, mp1 - mp2);
            PolynomialX<T> rm = remains.isEmpty() ? zero : new PolynomialX<>(mc, remains, remains.lastKey());
            return new Pair<>(qm, rm);
        }

        /**
         * Returns the reminder of the two polynomials.
         */
        @Override
        public PolynomialX<T> reminder(PolynomialX<T> a, PolynomialX<T> b) {
            return divideAndReminder(a, b).getSecond();
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#getOne()
         */
        @NotNull
        @Override
        public PolynomialX<T> getOne() {
            return one;
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#multiplyLong(java.lang.Object, long)
         */
        @NotNull
        @Override
        public PolynomialX<T> multiplyLong(@NotNull PolynomialX<T> p, long l) {
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
            return new PolynomialX<>(mc, nmap, p.degree);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#divideLong(java.lang.Object, long)
         */
        @NotNull
        @Override
        public PolynomialX<T> divideLong(@NotNull PolynomialX<T> p, long n) {
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
            return new PolynomialX<>(mc, nmap, p.degree);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#squareRoot(java.lang.Object)
         */
        @NotNull
        @Override
        public PolynomialX<T> squareRoot(@NotNull PolynomialX<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#nroot(java.lang.Object, long)
         */
        @NotNull
        @Override
        public PolynomialX<T> nroot(@NotNull PolynomialX<T> x, long n) {
            throw new UnsupportedCalculationException();
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#pow(java.lang.Object, long)
         */
        @NotNull
        @Override
        public PolynomialX<T> pow(@NotNull PolynomialX<T> p, long exp) {
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
            return new PolynomialX<>(mc, map, (int) mp);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#constantValue(java.lang.String)
         */
        @Override
        public PolynomialX<T> constantValue(@NotNull String name) {
            return constant(mc, mc.constantValue(name));
        }

        /*
         * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#abs(java.lang.Object)
         */
        @NotNull
        @Override
        public PolynomialX<T> abs(@NotNull PolynomialX<T> para) {
            if (mc.compare(para.getCoefficient(para.degree), mc.getZero()) < 0) {
                return negate(para);
            }
            return para;
        }

        /**
         * Returns a the greatest common divisor of {@code a} and {@code b}. A greatest common divisor of polynomial {@code p} and {@code q}
         * is a polynomial {@code d} that divides {@code p} and {@code q} such that every common divisor of {@code p} and {@code q} also divides {@code d}.
         *
         * @return the  greatest common divisor of {@code a} and {@code b}, whose leading coefficient is one.
         */
        @Override
        public PolynomialX<T> gcd(PolynomialX<T> a, PolynomialX<T> b) {
            if (a.degree < b.degree) {
                PolynomialX<T> t = a;
                a = b;
                b = t;
            }
            while (b.degree > 0) {
                PolynomialX<T> t = reminder(a, b);
                a = b;
                b = t;
            }
            return a.unit();
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#getNumberClass()
         */
        @NotNull
        @Override
        public Class<?> getNumberClass() {
            return PolynomialX.class;
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#isInteger(java.lang.Object)
         */
        @Override
        public boolean isInteger(PolynomialX<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#isQuotient(java.lang.Object)
         */
        @Override
        public boolean isQuotient(PolynomialX<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#mod(java.lang.Object, java.lang.Object)
         */
        @Override
        public PolynomialX<T> mod(PolynomialX<T> a, PolynomialX<T> b) {
            return divideAndReminder(a, b).getSecond();
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#divideToInteger(java.lang.Object, java.lang.Object)
         */
        @Override
        public PolynomialX<T> divideToInteger(PolynomialX<T> a, PolynomialX<T> b) {
            return divideAndReminder(a, b).getFirst();
        }
    }

//    public static void main(String[] args) {
//        var mc = Fraction.getCalculator();
//        var p1 = Point.valueOf(Fraction.valueOf("-1"),Fraction.valueOf("1"),mc);
//        var p2 = Point.valueOf(Fraction.valueOf("0"),Fraction.valueOf("0"),mc);
//        var p3 = Point.valueOf(Fraction.valueOf("1"),Fraction.valueOf("1"),mc);
//        var p = PolynomialX.valueOf(Calculators.getCalculatorLongExact(), 1L, 1L).mapTo(Fraction::valueOf,mc);
////        print(lagrangeInterpolation(p1,p2,p3));
//        print(p);
//        print(p.difference().sumOfN());
//    }
//		MathCalculator<Integer> mc = Calculators.getCalculatorInteger();
//		PolynomialX<Integer> p1 = PolynomialX.valueOf(mc, 6,7,1);
//		PolynomialX<Integer> p2 = PolynomialX.valueOf(mc, -6,-5,1);
//		PolynomialCalculator<Integer> mmc = getCalculator(mc);
//		PolynomialX<Integer> re = mmc.add(p1, p2);
//		print(p1);
//		print(p2);
////		print(re);
////		print(mmc.multiply(p1, p2));//
//		print(mmc.divideAndReminder(p1, p2));
////		print(mmc.pow(p2, 5));
////		print(mmc.gcd(mmc.pow(p2, 5), mmc.pow(p2, 3)));
//		print(mmc.gcd(p1, p2));
//	}


}
