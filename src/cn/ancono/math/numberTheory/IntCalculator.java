/*
 * 2017-09-09
 */
package cn.ancono.math.numberTheory;


import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.abs.calculator.EUDCalculator;
import cn.ancono.math.exceptions.ExceptionUtil;
import cn.ancono.utilities.structure.Pair;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;

/**
 * IntCalculator represents a common supertype for all calculator that deal with integers(int, long, BigInteger...),
 * which provides some necessary methods that is required but is difficult to implement using the
 * normal MathCalculator, such as <code>mod</code> operation and so on.
 * <p>
 * <p>
 * This calculator do not necessarily support all the calculations in
 * MathCalculator, but it is recommended that all the added methods related to
 * number theory should be fully implemented.
 *
 * @author liyicheng 2017-09-09 20:33
 */
public interface IntCalculator<T> extends MathCalculator<T>, EUDCalculator<T> {

    /**
     * Returns the integer <code>1</code> of type T.
     */
    @NotNull
    @Override
    T getOne();

    /**
     * Integers are comparable.
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

//    /**
//     * Determines whether the number is an integer. An integer must always be a
//     * quotient. The constant values {@link #getOne()} and {@link #getZero()}
//     * must be an integer.
//     * <p>
//     * For example, {@code 1} is an integer while {@code 1.1} is not.
//     *
//     * @param x a number
//     * @return {@code true} if the number is an integer, otherwise
//     * {@code false}.
//     */
//    boolean isInteger(T x);

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

//    /**
//     * Determines whether the number is a quotient, which can be represented by
//     * a quotient {@code p/q} where {@code p} and {@code q} has no common
//     * factor.
//     * <p>
//     * For example, {@code 1}, {@code 2/3} are quotients, while {@code sqr(2)}
//     * is not.
//     *
//     * @param x a number
//     * @return {@code true} if the number is a quotient, otherwise
//     * {@code false}.
//     */
//    boolean isQuotient(T x);

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
    @Override
    @NotNull
    T mod(@NotNull T a, @NotNull T b);

    /**
     * Returns the remainder:{@code a % b}. If {@code a>0}, then the result will
     * be positive, and if {@code a<0}, then the result should be negative. It
     * is required that {@code b} is positive, otherwise an ArithmeticException
     * will be thrown.
     * <p>
     * For example, {@code remainder(1,2)=0}, {@code remainder(7,3)=1} and
     * {@code remainder(-7,3) = -1}.
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a % b}
     */
    @NotNull
    default T remainder(@NotNull T a, @NotNull T b) {
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
     * Returns the result of {@code a \ b}. Returns the biggest integer {@code n}
     * in absolute value that {@code |nb|<=|a|}, whether the result is positive
     * is determined by {@code a}.
     * <p>
     * For example, {@code divideToInteger(3,2) == 1} and
     * {@code divideToInteger(-5,2) == -2}
     *
     * @param a the dividend
     * @param b the divisor
     * @return {@code a \ b}
     */
    @NotNull T divideToInteger(@NotNull T a, @NotNull T b);

    /**
     * Returns a pair of two numbers containing {@code (this / val)} followed by
     * {@code (this % val)}.
     *
     * @param a the dividend
     * @param b the divisor
     * @return a pair of two numbers: the quotient {@code (a / b)} is the first
     * element, and the remainder {@code (a % b)} is the second element.
     */
    @NotNull
    default Pair<T, T> divideAndRemainder(@NotNull T a, @NotNull T b) {
        T quotient = divideToInteger(a, b);
        T reminder = remainder(a, b);
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
    default boolean isExactDivide(@NotNull T a, @NotNull T b) {
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
//        if (!isInteger(x)) {
//            return false;
//        }
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
//        if (!isInteger(x)) {
//            return false;
//        }
        T two = increase(getOne());
        return isZero(mod(x, two));
    }

    /**
     * Returns {@literal gcd(|a|,|b|)}, the maximal positive common factor of the two
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
    default T gcd(@NotNull T a, @NotNull T b) {
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

//    /**
//     * Returns the greatest common divisor of two numbers and a pair of number (u,v) such that
//     * <pre>ua+vb=gcd(a,b)</pre>
//     * The returned greatest common divisor is the same as {@link NTCalculator#gcd(Object, Object)}.
//     * Note that the pair of <code>u</code> and <code>v</code> returned is not unique and different implementation
//     * may return differently when a,b is the same.<P></P>
//     * The default implementation is based on the Euclid's algorithm.
//     *
//     * @return a tuple of <code>{gcd(a,b), u, v}</code>.
//     */
//    @NotNull
//    default Triple<T, T, T> gcdUV(@NotNull T a, T b) {
//        if (isZero(a)) {
//            return new Triple<>(b, getZero(), getOne());
//        }
//        if (isZero(b)) {
//            return new Triple<>(a, getOne(), getZero());
//        }
//        return gcdUV0(a, b);
//    }


//    private Triple<T, T, T> gcdUV0(T a, T b) {
////        T[] quotients = (T[]) new Object[4];
////        int n = 0;
////        while (true) {
////            var t = divideAndReminder(a, b);
////            T q = t.getFirst();
////            T r = t.getSecond();
////            if (isZero(r)) {
////                break;
////            }
////            quotients = ArraySup.ensureCapacityAndAdd(quotients, q, n++);
////            a = b;
////            b = r;
////        }
////        // computes u and v
////        T one = getOne();
////        T zero = getZero();
////        T u0 = one, u1 = zero,
////                v0 = zero, v1 = one;
////        // u[s] = u[s-2]-q[s-2]*u[s-1]
////        for (int i = 0; i < n; i++) {
////            T nextU = subtract(u0, multiply(quotients[i], u1));
////            T nextV = subtract(v0, multiply(quotients[i], v1));
////            u0 = u1;
////            u1 = nextU;
////            v0 = v1;
////            v1 = nextV;
////        }
////        return new Triple<>(b, u1, v1);
//        if (isZero(b)) {
//            return new Triple<>(a, getOne(), getZero());
//        }
//        /*
//        Explanation of the algorithm:
//        we want to maintain the following equation while computing the gcd using the Euclid's algorithm
//        let d0=a, d1=b, d2, d3 ... be the sequence of remainders in Euclid's algorithm,
//        then we have
//            a*1 + b*0 = d0
//            a*0 + b*1 = d1
//        let
//            u0 = 1, v0 = 0
//            u1 = 0, v1 = 1
//        then we want to build a sequence of u_i, v_i such that
//            a*u_i + b*v_i = d_i,
//        when we find the d_n = gcd(a,b), the corresponding u_n and v_n is what we want.
//        We have:
//            d_i = q_i * d_{i+1} + d_{i+2}        (by Euclid's algorithm
//        so
//            a*u_i + b*v_i = q_i * (a*u_{i+1} + b*v_{i+1}) + (a*u_{i+2} + b*v_{i+2})
//            u_i - q_i * u_{i+1} = u_{i+2}
//            v_i - q_i * v_{i+1} = v_{i+2}
//        but it is only necessary for us to record u_i, since v_i can be calculated from the equation
//            a*u_i + b*v_i = d_i
//         */
//        var d0 = a;
//        var d1 = b;
//        var u0 = getOne();
//        var u1 = getZero();
//        while (!isZero(d1)) {
//            var pair = divideAndRemainder(d0, d1);
//            var q = pair.getFirst();
//            var d2 = pair.getSecond();
//            d0 = d1;
//            d1 = d2;
//            var u2 = subtract(u0, multiply(q, u1));
//            u0 = u1;
//            u1 = u2;
//        }
//        var v = divide(subtract(d0, multiply(a, u0)), b);
//        return new Triple<>(d0, u0, v);
//    }

    /**
     * Returns {@literal lcm(|a|,|b|)}, the two numbers' positive least common multiple.
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
        Pair<T, T> dar = divideAndRemainder(b, a);
        while (isZero(dar.getSecond())) {
            // b%a==0
            k = increase(k);
            b = dar.getFirst();
            // b = b/a;
            dar = divideAndRemainder(b, a);
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
     * @param a a number.
     * @param n a non-negative number.
     * @param m the modular.
     */
    default T powerAndMod(T a, T n, T m) {
        if (isNegative(n)) {
            throw new IllegalArgumentException("n<0");
        }
//        if (!isPositive(a)) {
//            throw new IllegalArgumentException("a<=0");
//        }
//        if (!isPositive(m)) {
//            throw new IllegalArgumentException("mod<=0");
//        }

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
    }

    /**
     * Returns {@code (a^n) mod m}, where <code>n</code> is a long.
     * <p>
     * For example, {@code powerAndMod(2,2,3) = 1}, and
     * {@code powerAndMod(3,9,7) = 6}.
     *
     * @param x a number, positive.
     * @param n a non-negative number.
     * @param m the modular.
     */
    @NotNull
    @Override
    default T powerAndMod(@NotNull T x, long n, @NotNull T m) {
        return EUDCalculator.super.powerAndMod(x, n, m);
    }

}


