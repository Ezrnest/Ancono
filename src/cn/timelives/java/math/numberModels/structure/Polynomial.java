/**
 * 2017-11-21
 */
package cn.timelives.java.math.numberModels.structure;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.MathCalculatorHolder;
import cn.timelives.java.math.algebra.IPolynomial;
import cn.timelives.java.math.algebra.linearAlgebra.Matrix;
import cn.timelives.java.math.algebra.linearAlgebra.MatrixSup;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.algebra.linearAlgebra.Vector;
import cn.timelives.java.math.geometry.analytic.planeAG.Point;
import cn.timelives.java.math.numberModels.*;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberTheory.EuclidRingNumberModel;
import cn.timelives.java.math.numberTheory.NTCalculator;
import cn.timelives.java.math.numberTheory.combination.CombUtils;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.ModelPatterns;
import cn.timelives.java.utilities.structure.Pair;
import cn.timelives.java.utilities.structure.WithInt;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.IntFunction;

import static cn.timelives.java.utilities.Printer.print;


/**
 * An implement for interface IPolynomial.
 *
 * @author liyicheng
 * @see IPolynomial
 * 2017-11-21 17:10
 */
@SuppressWarnings("Duplicates")
public final class Polynomial<T> extends MathObject<T> implements IPolynomial<T>, Comparable<Polynomial<T>>,
        EuclidRingNumberModel<Polynomial<T>> {
    /**
     * A map.
     */
    private final NavigableMap<Integer, T> map;
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
//        if (n < 0 || n > degree) {
//            throw new IndexOutOfBoundsException("For n=" + n);
//        }
        return getCoefficient0(n);
    }

    public Vector<T> coefficientVector() {
        return IPolynomial.coefficientVector(this, getMathCalculator());
    }

    public boolean isZero(){
        return isConstant() && getMc().isZero(getCoefficient(0));
    }

    public boolean isOne(){
        var mc = getMc();
        return isConstant() && mc.isEqual(mc.getOne(),getCoefficient(0));
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

    private void addToMap(Polynomial<T> para1, TreeMap<Integer, T> map, int mp1, int mp2, MathCalculator<T> mc) {
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
        return new Polynomial<>(mc, map, para1.degree + y.degree);
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

    /**
     * Returns a polynomial of substituting the variable with the given polynomial.
     * @param sub a polynomial
     */
    public Polynomial<T> substitute(Polynomial<T> sub){
        var mc = getMc();
        var re = constant(mc,getCoefficient(degree));
        for (int i = degree - 1; i > -1; i--) {
            re = sub.multiply(re);
            re = constant(mc,getCoefficient(i)).add(re);
        }
        return re;
    }



    /**
     * Divides this polynomial by a number to get a new polynomial whose leading coefficient is one.
     */
    public Polynomial<T> unit() {
        if (getMc().isEqual(getMc().getOne(), getCoefficient(degree))) {
            return this;
        }
        T k = getCoefficient(degree);
        return divide(k);
    }

    /**
     * Returns <code>k*this</code>
     */
    public Polynomial<T> multiply(T k){
        var mc = getMc();
        if(mc.isZero(k)){
            return zero(mc);
        }
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            var t = mc.multiply(en.getValue(), k);
            en.setValue(t);
        }
        return new Polynomial<>(mc, nmap, degree);
    }

    /**
     * Returns <code>1/k*this</code>
     */
    public Polynomial<T> divide(T k){
        var mc = getMc();
        NavigableMap<Integer, T> nmap = getCoefficientMap();
        for (Entry<Integer, T> en : nmap.entrySet()) {
            en.setValue(mc.divide(en.getValue(), k));
        }
        return new Polynomial<>(mc, nmap, degree);
    }

    /**
     * Returns the quotient <code>q</code> and remainder <code>r</code> such that
     * <pre> this = q * g + r, where deg(r) < deg(g)</pre>
     * @param g a non-zero polynomial
     */
    public kotlin.Pair<Polynomial<T>, Polynomial<T>> divideAndRemainder(Polynomial<T> g){
        var p1 = this;
        var mc = getMc();
        if (g.isZero()) {
            throw new ArithmeticException("divide by zero!");
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
        List<WithInt<T>> toSubtract = new ArrayList<>(g.map.size() - 1);
        for (Entry<Integer, T> en : g.map.entrySet()) {
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
        Polynomial<T> qm = new Polynomial<>(mc, quotient, mp1 - mp2);
        Polynomial<T> rm = remains.isEmpty() ? zero(mc) : new Polynomial<>(mc, remains, remains.lastKey());
        return new kotlin.Pair<>(qm, rm);
    }

    /**
     * Returns the 'integer' quotient part of the division of <code>this</code> and <code>g</code>.
     * Assuming
     * <pre> this = q * g + r, where deg(r) < deg(g)</pre>
     * then this method returns <code>q</code>.
     * @param g a non-zero polynomial
     */
    @Override
    @NotNull
    public Polynomial<T> divideToInteger(@NotNull Polynomial<T> g){
        return divideAndRemainder(g).getFirst();
    }
    /**
     * Returns the remainder of the division of <code>this</code> and <code>g</code>.
     * Assuming
     * <pre> this = q * g + r, where deg(r) < deg(g)</pre>
     * then this method returns <code>r</code>.
     * @param g a non-zero polynomial
     */
    @Override
    @NotNull
    public Polynomial<T> remainder(@NotNull Polynomial<T> g){
        return divideAndRemainder(g).getSecond();
    }

    /*
     * @see cn.timelives.java.math.MathCalculator#pow(java.lang.Object, long)
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
        long mp = exp * this.degree;
        if (mp > Integer.MAX_VALUE || mp < 0) {
            throw new ArithmeticException("Too big for exp=" + exp);
        }

        NavigableMap<Integer, T> map = ModelPatterns.binaryProduce(exp, one(mc).map, this.map, (x, y) -> multiplyToMap(x, y, mc));
        return new Polynomial<>(mc, map, (int) mp);
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
        return new Polynomial<>(mc, nmap, degree - 1);
    }

    /**
     * Returns the derivative of this polynomial(formally), that is
     * <text>sigma(n*a<sub>n</sub>x<sup>n-1</sup>, n from 1 to (<i>degree of this</i>-1))</text>
     * @return the derivative of this polynomial(formally)
     */
    public Polynomial<T> derivative(){
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
        return new Polynomial<>(mc, nmap, degree - 1);
    }

    /**
     * Returns one of the integration of this polynomial whose constant part is zero.
     */
    public Polynomial<T> integration(){
        if(isZero()){
            return this;
        }
        var mc = getMc();
        NavigableMap<Integer,T> nmap = new TreeMap<>();
        for(var en : map.entrySet()){
            int n = en.getKey();
            T coe = en.getValue();
            nmap.put(n+1,mc.divideLong(coe,n+1));
        }
        return new Polynomial<>(mc, nmap, degree + 1);
    }


    /**
     * Assuming this polynomial is <code>f(x)</code>, this method returns the
     * result of <code>sigma(f(i),i from 1 to n)</code> as a polynomial.
     */
    public Polynomial<T> sumOfN(){
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
     * Returns the greatest common divisor of this and <code>g</code>. The coefficient of the top term in the
     * returned polynomial is one.
     */
    @NotNull
    @Override
    public Polynomial<T> gcd(@NotNull Polynomial<T> g){
        Polynomial<T> a = this,b = g;
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
        return a.unit();
    }

    @NotNull
    @Override
    public Triple<Polynomial<T>, Polynomial<T>, Polynomial<T>> gcdUV(@NotNull Polynomial<T> y) {
        return CalculatorUtils.gcdUV(this,y, zero(getMc()),one(getMc()));
    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public Polynomial<T> lcm(@NotNull Polynomial<T> y) {
        return (Polynomial<T>) DefaultImpls.lcm(this,y);
    }

    @Override
    public boolean isCoprime(@NotNull Polynomial<T> y) {
        return this.gcd(y).isOne();
    }

    @Override
    public int deg(@NotNull Polynomial<T> y) {
        return DefaultImpls.deg(this,y);
    }

    /**
     * Determines whether this polynomial can have duplicated roots.
     */
    public boolean hasDuplicatedRoot(){
        return !isCoprime(derivative());
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
        return powerX(p,mc).sumOfN();
    }

    private static <T> Polynomial<T> sumOfX1(MathCalculator<T> mc) {
        //1+2+...n = (n+1)n / 2 = 1/2 * n^2 + 1/2 * n
        var half = mc.divideLong(mc.getOne(), 2L);
        return Polynomial.valueOf(mc, mc.getZero(), half, half);
    }

    private static <T> Polynomial<T> sumOfX2(MathCalculator<T> mc) {
        //1^2+2^2+...n^2 = n(n+1)(2n+1)/6 = 1/3*n^3+1/2*n^2+1/6*n
        var c1 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 3), mc);
        var c2 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 2), mc);
        var c3 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 6), mc);
        return Polynomial.valueOf(mc, mc.getZero(), c3, c2, c1);
    }

    private static <T> Polynomial<T> sumOfX3(MathCalculator<T> mc) {
        //1^2+2^2+...n^2 = (n(n+1)/2)^2 = 1/4*n^4+1/2*n^3+1/4*n^2
        var c1 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 4), mc);
        var c2 = CalculatorUtils.valueOfFraction(Fraction.valueOf(1, 2), mc);
        var o = mc.getZero();
        return Polynomial.valueOf(mc, o, o, c1, c2, c1);
    }

    /**
     * Returns the sylvester matrix of this and g. It is required that <code>this</code> and
     * <code>g</code> must not be zero at the same time.
     * <pre>R(this,g)</pre>
     * @param g another polynomial
     * @return a square matrix whose size is <code>this.degree + g.degree</code>.
     */
    public Matrix<T> sylvesterMatrix(Polynomial<T> g){
        return MatrixSup.sylvesterDet(this,g);
    }

    /**
     * Returns the determinant of the sylvester matrix of this and g. It is required that <code>this</code> and
     * <code>g</code> must not be zero at the same time.
     * <pre>|R(this,g)|</pre>
     * @param g another polynomial
     * @return the determinant of the sylvester matrix
     */
    public T sylvesterDet(Polynomial<T> g){
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
     * @return the determinant of this.
     */
    public T determinant(){
        var det = sylvesterMatrix(derivative());
        var re = det.calDet();
        re = getMc().divide(re,first());
        int n = getDegree();
        if(n % 4 == 2 || n % 4 == 3){
            return getMc().negate(re);
        }else{
            return re;
        }
    }


    /*
     * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
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
     * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
     */
    @Override
    public boolean valueEquals(@NotNull MathObject<T> obj) {
        if (!(obj instanceof Polynomial)) {
            return false;
        }
        return IPolynomial.isEqual(this, (Polynomial<T>) obj, getMc()::isEqual);
    }


    /*
     * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
     */
    @Override
    public <N> boolean valueEquals(@NotNull MathObject<N> obj, @NotNull Function<N, T> mapper) {
        if (!(obj instanceof Polynomial)) {
            return false;
        }
        return IPolynomial.isEqual(this, (Polynomial<N>) obj, (x, y) -> getMc().isEqual(x, mapper.apply(y)));
    }

    /*
     * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
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

    private static final Map<MathCalculator<?>, Polynomial<?>> zeros = new HashMap<>();

    /**
     * Adds all the polynomials.
     * @param ps a non-empty array
     */
    @SafeVarargs
    public static <T> Polynomial<T> addAll(Polynomial<T>...ps){
        return NumberModelUtils.sigma(ps,0,ps.length);
    }

    /**
     * Multiplies all the polynomials.
     * @param ps a non-empty array
     */
    @SafeVarargs
    public static <T> Polynomial<T> multiplyAll(Polynomial<T>...ps){
        return NumberModelUtils.multiplyAll(ps,0,ps.length);
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

    public static <T> Polynomial<T> valueOf(MathCalculator<T> mc, List<T> coes){
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
     * Converts the given polynomial to a polynomial of the given character {@code ch}.
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
    public static <T> Polynomial<T> fromPolynomial(IPolynomial<T> p, MathCalculator<T> mc){
        if(p instanceof Polynomial){
            return (Polynomial<T>) p;
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
        return new Polynomial<>(mc,map,degree);
    }

    /**
     * Returns a polynomial that is equal to <code>multiplyAll(x-roots[i])</code>.
     * If <code>roots.length==0</code>, then the result is <code>1</code>
     * @param mc a MathCalculator
     * @param roots the roots
     */
    @SafeVarargs
    public static <T> Polynomial<T> ofRoots(MathCalculator<T> mc, T...roots){
        return ofRoots(mc,roots,0,roots.length);
    }

    /**
     * Returns a polynomial of <code>x - root</code>
     */
    public static <T> Polynomial<T> ofRoot(MathCalculator<T> mc, T root){
        return valueOf(mc,mc.negate(root),mc.getOne());
    }

    /**
     * Returns a polynomial of <code>multiplyAll(x-roots[i], i from startInclusive to endExclusive)</code>.
     * @param mc a MathCalculator
     * @param rts the roots
     * @param startInclusive index of i to start with(inclusive)
     * @param endExclusive index of i to end with(exclusive)
     */
    public static <T> Polynomial<T> ofRoots(MathCalculator<T> mc, T[] rts, int startInclusive, int endExclusive){
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
    public static <T> Polynomial<T> lagrangeInterpolation(Point<T>...points){
        //sigma(multiplyAll((x-x_j)/(x_i-x_j),j!=i),i from 0 to points.length-1)
        if(points.length==0){
            throw new IllegalArgumentException("points.length==0");
        }
        var mc = points[0].getMathCalculator();
        Polynomial<T> re = zero(mc);
        @SuppressWarnings("unchecked") T[] roots = (T[]) new Object[points.length];
        @SuppressWarnings("unchecked") Polynomial<T>[] rootPoly = (Polynomial<T>[]) new Polynomial[points.length];
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
    public static <T> Polynomial<T> binomialPower(T x0, int n, MathCalculator<T> mc){
        if(mc.isZero(x0)){
            return powerX(n,mc);
        }
        var map = new TreeMap<Integer,T>();
        var x0Power = mc.getOne();
        var binomialCoes = CombUtils.binomialsOf(n);
        for(int i=n;i>=0;i--){
            T coe = mc.multiplyLong(x0Power,binomialCoes.get(n));
            if(mc.isZero(coe)){
                continue;
            }
            map.put(n,coe);
            //noinspection SuspiciousNameCombination
            x0Power = mc.multiply(x0Power,x0);
        }
        return new Polynomial<>(mc,map,n);
    }

    /**
     * Gets a calculator of the specific type of PolynomialX
     *
     */
    public static <T> PolynomialCalculator<T> getCalculator(MathCalculator<T> mc) {
        return new PolynomialCalculator<>(mc);
    }



    public static class PolynomialCalculator<T> extends MathCalculatorAdapter<Polynomial<T>>
            implements MathCalculatorHolder<T>, NTCalculator<Polynomial<T>> {
        private final MathCalculator<T> mc;
        private final Polynomial<T> zero, one;

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
        public boolean isEqual(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return IPolynomial.isEqual(para1, para2, mc::isEqual);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#compare(java.lang.Object, java.lang.Object)
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
         * @see cn.timelives.java.math.MathCalculator#add(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> add(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return para1.add(para2);
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#negate(java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> negate(@NotNull Polynomial<T> para) {
            return para.negate();
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#subtract(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> subtract(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return para1.subtract(para2);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#getZero()
         */
        @NotNull
        @Override
        public Polynomial<T> getZero() {
            return zero;
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#isZero(java.lang.Object)
         */
        @Override
        public boolean isZero(@NotNull Polynomial<T> para) {
            return isEqual(zero, para);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#multiply(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> multiply(@NotNull Polynomial<T> para1, @NotNull Polynomial<T> para2) {
            return para1.multiply(para2);
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#divide(java.lang.Object, java.lang.Object)
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
         * @return a pair of the quotient and the reminder.
         */
        public Pair<Polynomial<T>, Polynomial<T>> divideAndReminder(Polynomial<T> p1, Polynomial<T> p2) {
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
            Polynomial<T> qm = new Polynomial<>(mc, quotient, mp1 - mp2);
            Polynomial<T> rm = remains.isEmpty() ? zero : new Polynomial<>(mc, remains, remains.lastKey());
            return new Pair<>(qm, rm);
        }

        /**
         * Returns the reminder of the two polynomials.
         */
        @Override
        public Polynomial<T> reminder(Polynomial<T> a, Polynomial<T> b) {
            return divideAndReminder(a, b).getSecond();
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#getOne()
         */
        @NotNull
        @Override
        public Polynomial<T> getOne() {
            return one;
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#multiplyLong(java.lang.Object, long)
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
         * @see cn.timelives.java.math.MathCalculator#divideLong(java.lang.Object, long)
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
         * @see cn.timelives.java.math.MathCalculator#squareRoot(java.lang.Object)
         */
        @NotNull
        @Override
        public Polynomial<T> squareRoot(@NotNull Polynomial<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.timelives.java.math.MathCalculator#nroot(java.lang.Object, long)
         */
        @NotNull
        @Override
        public Polynomial<T> nroot(@NotNull Polynomial<T> x, long n) {
            throw new UnsupportedCalculationException();
        }


        /*
         * @see cn.timelives.java.math.MathCalculator#pow(java.lang.Object, long)
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
         * @see cn.timelives.java.math.MathCalculator#constantValue(java.lang.String)
         */
        @Override
        public Polynomial<T> constantValue(@NotNull String name) {
            return constant(mc, mc.constantValue(name));
        }

        /*
         * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#abs(java.lang.Object)
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
         * Returns a the greatest common divisor of {@code a} and {@code b}. A greatest common divisor of polynomial {@code p} and {@code q}
         * is a polynomial {@code d} that divides {@code p} and {@code q} such that every common divisor of {@code p} and {@code q} also divides {@code d}.
         *
         * @return the  greatest common divisor of {@code a} and {@code b}, whose leading coefficient is one.
         */
        @Override
        public Polynomial<T> gcd(Polynomial<T> a, Polynomial<T> b) {
            if (a.degree < b.degree) {
                Polynomial<T> t = a;
                a = b;
                b = t;
            }
            while (b.degree > 0) {
                Polynomial<T> t = reminder(a, b);
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
            return Polynomial.class;
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#isInteger(java.lang.Object)
         */
        @Override
        public boolean isInteger(Polynomial<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#isQuotient(java.lang.Object)
         */
        @Override
        public boolean isQuotient(Polynomial<T> x) {
            throw new UnsupportedCalculationException();
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#mod(java.lang.Object, java.lang.Object)
         */
        @Override
        public Polynomial<T> mod(Polynomial<T> a, Polynomial<T> b) {
            return divideAndReminder(a, b).getSecond();
        }

        /*
         * @see cn.timelives.java.math.numberTheory.NTCalculator#divideToInteger(java.lang.Object, java.lang.Object)
         */
        @Override
        public Polynomial<T> divideToInteger(Polynomial<T> a, Polynomial<T> b) {
            return divideAndReminder(a, b).getFirst();
        }
    }

    public static void main(String[] args) {
//        var mc = Fraction.getCalculator();
//        var p1 = Point.valueOf(Fraction.valueOf("-1"),Fraction.valueOf("1"),mc);
//        var p2 = Point.valueOf(Fraction.valueOf("0"),Fraction.valueOf("0"),mc);
//        var p3 = Point.valueOf(Fraction.valueOf("1"),Fraction.valueOf("1"),mc);
//        var p = Polynomial.valueOf(Calculators.getCalculatorLongExact(), 1L, 1L).mapTo(Fraction::valueOf,mc);
////        print(lagrangeInterpolation(p1,p2,p3));
//        print(p);
//        print(p.difference().sumOfN());
//    }
//		MathCalculator<Integer> mc = Calculators.getCalculatorInteger();
//		Polynomial<Integer> p1 = Polynomial.valueOf(mc, 6,7,1);
//		var pf = p1.mapTo(Fraction::valueOf,Fraction.getCalculator());
//		print(pf);
//		print(pf.integration());
        var mc = Multinomial.getCalculator();
        var f = valueOf(mc,Multinomial.valueOf("-x-3"),Multinomial.valueOf("2"),Multinomial.ZERO,Multinomial.ONE);
        var g = valueOf(mc,Multinomial.valueOf("1-y"),Multinomial.valueOf("-1"),Multinomial.ONE);
        print(f.sylvesterMatrix(g).calDet());
//        print(f.determinant());
//        var f2 = f.substitute(Polynomial.valueOf(Multinomial.getCalculator(),Multinomial.ZERO,Multinomial.ZERO,Multinomial.ONE));
//        print(f2.determinant());
//        print(f.determinant().pow(2).multiply(Multinomial.valueOf("16a^2*c")));
//		Polynomial<Integer> p2 = Polynomial.valueOf(mc, -6,-5,1);
//		PolynomialCalculator<Integer> mmc = getCalculator(mc);
//		Polynomial<Integer> re = mmc.add(p1, p2);
//		print(p1);
//		print(p2);
////		print(re);
////		print(mmc.multiply(p1, p2));//
//		print(mmc.divideAndReminder(p1, p2));
////		print(mmc.pow(p2, 5));
////		print(mmc.gcd(mmc.pow(p2, 5), mmc.pow(p2, 3)));
//		print(mmc.gcd(p1, p2));
	}


}
