/**
 * 2017-09-09
 */
package cn.ancono.math.numberTheory;


import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.abstractAlgebra.calculator.UFDCalculator;
import cn.ancono.math.exceptions.ExceptionUtil;
import cn.ancono.utilities.ArraySup;
import cn.ancono.utilities.structure.Pair;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * NTCalculator represents number theory calculator, which provides some
 * necessary methods that is required but is difficult to implement using the
 * normal MathCalculator, such as <i>mod</i> operation and so on.
 * <p>
 * This calculator do not necessarily support all the calculations in
 * MathCalculator, but it is recommended that all the added methods related to
 * number theory should be fully implemented.
 *
 * @author liyicheng 2017-09-09 20:33
 */
public interface NTCalculator<T> extends MathCalculator<T>, UFDCalculator<T> {

    /**
     * Returns the "minimal" element in the type T.
     *
     * @return
     */
    @NotNull
    @Override
    T getOne();

    /**
     * The number model used for number theory should be comparable.
     *
     * @return
     */
    @Override
    default boolean isComparable() {
        return true;
    }
    // methods that is often used as a number theory calculator.

    /**
     * Determines whether the number is positive.
     * <p>
     * This method is added for convenience. The default implement is
     *
     * <pre>
     * return compare(x, getZero()) > 0;
     * </pre>
     *
     * @param x a number
     * @return {@code x>0}
     */
    default boolean isPositive(T x) {
        return compare(x, getZero()) > 0;
    }

    /**
     * Determines whether the number is negative.
     * <p>
     * This method is added for convenience. The default implement is
     *
     * <pre>
     * return compare(x, getZero()) < 0;
     * </pre>
     *
     * @param x a number
     * @return {@code x<0}
     */
    default boolean isNegative(T x) {
        return compare(x, getZero()) < 0;
    }

    /**
     * Returns {@code x+1}.
     * <p>
     * This method is added for convenience. The default implement is
     *
     * <pre>
     * return add(x, getOne());
     * </pre>
     *
     * @param x a number
     * @return {@code x+1}
     */
    default T increase(T x) {
        return add(x, getOne());
    }

    /**
     * Returns {@code x-1}.
     * <p>
     * This method is added for convenience. The default implement is
     *
     * <pre>
     * return subtract(x, getOne());
     * </pre>
     *
     * @param x a number
     * @return {@code x-1}
     */
    default T decrease(T x) {
        return subtract(x, getOne());
    }

    //////////////////////////////////////////////////////////////////
    // Separate line for methods
    //////////////////////////////////////////////////////////////////

    /**
     * Determines whether the number is an integer. An integer must always be a
     * quotient. The constant values {@link #getOne()} and {@link #getZero()}
     * must be an integer.
     * <p>
     * For example, {@code 1} is an integer while {@code 1.1} is not.
     *
     * @param x a number
     * @return {@code true} if the number is an integer, otherwise
     * {@code false}.
     */
    boolean isInteger(T x);

    /**
     * Converts a value of type T to long, throws {@link UnsupportedOperationException} if
     * this cannot be done.
     */
    default long asLong(T x) {
        return asBigInteger(x).longValueExact();
    }

    /**
     * Converts a value of type T to BigInteger, throws {@link UnsupportedOperationException} if
     * this cannot be done.
     */
    BigInteger asBigInteger(T x);

    /**
     * Determines whether the number is a quotient, which can be represented by
     * a quotient {@code p/q} where {@code p} and {@code q} has no common
     * factor.
     * <p>
     * For example, {@code 1}, {@code 2/3} are quotients, while {@code sqr(2)}
     * is not.
     *
     * @param x a number
     * @return {@code true} if the number is a quotient, otherwise
     * {@code false}.
     */
    boolean isQuotient(T x);

    /**
     * Returns {@code a mod b}, a <i>non-negative</i> number as the result. It
     * is required that {@code b} is positive, otherwise an ArithmeticException
     * will be thrown.
     * <p>
     * For example, {@code mod(2,1)=0}, {@code mod(7,3)=1} and
     * {@code mod(-7,3) = 2}.
     *
     * @param a a number
     * @param b the modulus
     * @return {@code a mod b}
     */
    T mod(T a, T b);

    /**
     * Returns the reminder:{@code a % b}. If {@code a>0}, then the result will
     * be positive, and if {@code a<0}, then the result should be negative. It
     * is required that {@code b} is positive, otherwise an ArithmeticException
     * will be thrown.
     * <p>
     * For example, {@code reminder(1,2)=0}, {@code reminder(7,3)=1} and
     * {@code reminder(-7,3) = -1}.
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a % b}
     */
    default T reminder(T a, T b) {
        if (isZero(b)) {
            throw new ArithmeticException();
        }
        int signum = compare(a, getZero());
        if (signum == 0) {
            return getZero();
        }
        if (signum < 0) {
            // return a negative number
            return subtract(mod(negate(a), abs(b)), b);
        } else {
            return mod(a, abs(b));
        }
    }

    /**
     * Returns the result of {@code a \ b}. Returns the biggest number {@code n}
     * in absolute value that {@code |nb|<=|a|}, whether the result is positive
     * is determined by {@code a}. The result of this method should always
     * passes {@link #isInteger(Object)}.
     * <p>
     * For example, {@code divideToInteger(3,2) == 1} and
     * {@code divideToInteger(-5,2) == -2}
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a \ b}
     */
    T divideToInteger(T a, T b);

    /**
     * Returns a pair of two numbers containing {@code (this / val)} followed by
     * {@code (this % val)}.
     *
     * @param a the dividend
     * @param b the divisor
     * @return a pair of two numbers: the quotient {@code (a / b)} is the first
     * element, and the remainder {@code (a % b)} is the second element.
     */
    default Pair<T, T> divideAndReminder(T a, T b) {
        T quotient = divideToInteger(a, b);
        T reminder = reminder(a, b);
        return new Pair<>(quotient, reminder);
    }

    /**
     * Determines whether {@code mod(a,b)==0}.
     * <p>
     * This method is added for convenience. The default implement is
     *
     * <pre>
     * return isEqual(mod(a, b), getZero());
     * </pre>
     *
     * @param a a number
     * @param b another number
     * @return {@code mod(a,b)==0}
     */
    default boolean isExactDivide(T a, T b) {
        return isEqual(mod(a, b), getZero());
    }

    @NotNull
    @Override
    default T exactDivide(@NotNull T x, @NotNull T y) {
        if (!isExactDivide(x, y)) {
            ExceptionUtil.notExactDivision(x, y);
        }
        return divide(x, y);
    }

    /**
     * Determines whether the number is an odd number. A number is an odd number
     * if it is an integer and {@code mod(x,2)==1}.
     * <p>
     * This method is added for convenience. The default implement is
     *
     * <pre>
     * if (!isInteger(x)) {
     * 	return false;
     * }
     * T two = increase(getOne());
     * return isEqual(getOne(), mod(x, two));
     * </pre>
     *
     * @param x a number
     * @return {@code true} if it is an odd number, otherwise {@code false}.
     */
    default boolean isOdd(T x) {
        if (!isInteger(x)) {
            return false;
        }
        return !isEven(x);
    }

    /**
     * Determines whether the number is an even number. A number is an even
     * number if it is an integer and {@code mod(x,2)==0}.
     * <p>
     * This method is added for convenience. The default implement is
     *
     * <pre>
     * if (!isInteger(x)) {
     * 	return false;
     * }
     * T two = increase(getOne());
     * return isEqual(getZero(), mod(x, two));
     * </pre>
     *
     * @param x a number
     * @return {@code true} if it is an even number, otherwise {@code false}.
     */
    default boolean isEven(T x) {
        if (!isInteger(x)) {
            return false;
        }
        T two = increase(getOne());
        return isEqual(getZero(), mod(x, two));
    }

    /**
     * Returns {@literal gcd(|a|,|b|)}, the maximum common factor of the two
     * numbers. Returns {@code 0} if {@code a==0&&b==0}, and returns another
     * non-zero number if either of them is {@code 0}. Whether the two number is
     * negative is ignored. This method is implemented with Euclid's algorithm by default.
     * <p>
     * For example, {@code gcd(3,5)=1}, {@code gcd(12,30)=6}.
     *
     * @param a a number
     * @param b another number
     * @return {@code gcd(|a|,|b|)}
     */
    @NotNull
    @Override
    default T gcd(T a, T b) {
        a = abs(a);
        b = abs(b);
        T t;
        while (!isZero(b)) {
            t = b;
            b = mod(a, b);
            a = t;
        }
        return a;
    }

    /**
     * Returns the greatest common divisor of two numbers and a pair of number (u,v) such that
     * <pre>ua+vb=gcd(a,b)</pre>
     * The returned greatest common divisor is the same as {@link NTCalculator#gcd(Object, Object)}.
     * Note that the pair of <code>u</code> and <code>v</code> returned is not unique and different implementation
     * may return differently when a,b is the same.<P></P>
     * The default implementation is based on the Euclid's algorithm.
     *
     * @return a tuple of <code>{gcd(a,b), u, v}</code>.
     */
    default Triple<T, T, T> gcdUV(T a, T b) {
        if (isZero(a)) {
            return new Triple<>(b, getZero(), getOne());
        }
        if (isZero(b)) {
            return new Triple<>(a, getOne(), getZero());
        }
        return gcdUV0(a, b);
    }

    /**
     * Determines whether the two numbers <code>a</code> and <code>b</code>
     * are co-prime.
     */
    default boolean isCoprime(T a, T b) {
        return isEqual(gcd(a, b), getOne());
    }

    @SuppressWarnings("unchecked")
    private Triple<T, T, T> gcdUV0(T a, T b) {
        T[] quotients = (T[]) new Object[4];
        int n = 0;
        while (true) {
            var t = divideAndReminder(a, b);
            T q = t.getFirst();
            T r = t.getSecond();
            if (isZero(r)) {
                break;
            }
            quotients = ArraySup.ensureCapacityAndAdd(quotients, q, n++);
            a = b;
            b = r;
        }
        // computes u and v
        T one = getOne();
        T zero = getZero();
        T u0 = one, u1 = zero,
                v0 = zero, v1 = one;
        // u[s] = u[s-2]-q[s-2]*u[s-1]
        for (int i = 0; i < n; i++) {
            T nextU = subtract(u0, multiply(quotients[i], u1));
            T nextV = subtract(v0, multiply(quotients[i], v1));
            u0 = u1;
            u1 = nextU;
            v0 = v1;
            v1 = nextV;
        }
        return new Triple<>(b, u1, v1);
    }

    /**
     * Returns {@literal lcm(|a|,|b|)}, the two numbers' least common multiple.
     * If either of the two numbers is 0, then 0 will be return.
     * <p>
     * For example, {@code lcm(3,5)=15}, {@code lcm(12,30)=60}.
     *
     * @param a a number
     * @param b another number
     * @return {@literal lcm(|a|,|b|)}.
     */
    default T lcm(T a, T b) {
        if (isZero(a) || isZero(b)) {
            return getZero();
        }
        a = abs(a);
        b = abs(b);
        T gcd = gcd(a, b);
        return multiply(divide(a, gcd), b);
    }

    /**
     * Returns the max number k that {@code |b|%|a|^k==0} while
     * {@code |b|%|a|^(k+1)!=0}.
     *
     * @param a a number except {@code 0,1,-1}.
     * @param b another number
     * @return deg(a, b)
     */
    default T deg(T a, T b) {
        a = abs(a);
        b = abs(b);
        if (isZero(a) || isEqual(a, getOne())) {
            throw new IllegalArgumentException("a==0 or |a|==1");
        }
        T k = getZero();
        Pair<T, T> dar = divideAndReminder(b, a);
        while (isZero(dar.getSecond())) {
            // b%a==0
            k = increase(k);
            b = dar.getFirst();
            // b = b/a;
            dar = divideAndReminder(b, a);
        }
        // while(b % a ==0){
        // k++;
        // b = b/a;
        // }
        return k;
    }

    /**
     * Returns {@code (a^n) mod m}.
     * <p>
     * For example, {@code powerAndMod(2,2,3) = 1}, and
     * {@code powerAndMod(3,9,7) = 6}.
     *
     * @param a a number, positive.
     * @param n a non-negative number.
     * @param m the modular.
     */
    default T powerAndMod(T a, T n, T m) {
        if (isNegative(n)) {
            throw new IllegalArgumentException("n<0");
        }
        if (!isPositive(a)) {
            throw new IllegalArgumentException("a<=0");
        }
        if (!isPositive(m)) {
            throw new IllegalArgumentException("mod<=0");
        }

        T one = getOne();
        if (isEqual(m, one)) {
            return getZero();
        }
        if (isEqual(a, one)) {
            return one;
        }
        T ans = one;
        T two = add(one, one);
        a = mod(a, m);
        while (!isZero(n)) {
            if (isOdd(n)) {
                ans = mod(multiply(a, ans), m);
            }
            a = mod(multiply(a, a), m);
            n = divideToInteger(n, two);
        }
        return ans;
        // long ans = 1;
        // a = a % mod;
        // while(n>0){
        // if((n&1)==1){
        // ans = (a*ans)%mod;
        //
        // }
        // a = (a*a) % mod;
        // n>>=1;
        // }
        // return ans;
    }

    /**
     * Returns {@code (a^n) mod m}, where <code>n</code> is a long.
     * <p>
     * For example, {@code powerAndMod(2,2,3) = 1}, and
     * {@code powerAndMod(3,9,7) = 6}.
     *
     * @param a a number, positive.
     * @param n a non-negative number.
     * @param m the modular.
     */
    default T powerAndMod(T a, long n, T m) {
        if (n < 0) {
            throw new IllegalArgumentException("n<0");
        }
        if (!isPositive(a)) {
            throw new IllegalArgumentException("a<=0");
        }
        if (!isPositive(m)) {
            throw new IllegalArgumentException("mod<=0");
        }

        T one = getOne();
        if (isEqual(m, one)) {
            return getZero();
        }
        if (isEqual(a, one)) {
            return one;
        }
        T ans = one;
        a = mod(a, m);
        while (n > 0) {
            if ((n & 1) == 1) {
                ans = mod(multiply(a, ans), m);
            }
            a = mod(multiply(a, a), m);
            n >>= 1;
        }
        return ans;
    }

}
