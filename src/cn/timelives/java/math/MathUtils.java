package cn.timelives.java.math;

import cn.timelives.java.math.equation.Type;
import cn.timelives.java.math.equation.inequation.SVPInequation;
import cn.timelives.java.math.exceptions.ExceptionUtil;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Fraction;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Expression;
import cn.timelives.java.math.numberTheory.Primes;
import cn.timelives.java.math.set.IntervalUnion;
import cn.timelives.java.utilities.ArraySup;
import cn.timelives.java.utilities.ModelPatterns;

import java.math.BigInteger;
import java.util.*;

import static cn.timelives.java.utilities.ArraySup.ensureCapacityAndAdd;
import static cn.timelives.java.utilities.Printer.print;


/**
 * Provides some useful math functions which are not included in JDK.
 *
 * @author lyc
 */
@SuppressWarnings("Duplicates")
public class MathUtils {

//    public static void main(String[] args) {
//        print(gcdUV(5,13));
//    }

    /**
     * Calculate two numbers' GCD.Make sure that these two numbers are both bigger than zero.
     *
     * @param n1 a number
     * @param n2 another number
     * @return GCD of n1 and n2
     */
    public static long gcd(long n1, long n2) {
        //use Euclid's gcd algorithm
        long t;
        while (n2 != 0) {
            t = n2;
            n2 = n1 % n2;
            n1 = t;
        }
        return n1;
    }

    /**
     * Calculate two numbers' GCD of int. Make sure that these two numbers are both bigger than zero.
     *
     * @param n1 a number
     * @param n2 another number
     * @return GCD of n1 and n2
     */
    public static int gcd(int n1, int n2) {
        //use Euclid's gcd algorithm
        int t;
        while (n2 != 0) {
            t = n2;
            n2 = n1 % n2;
            n1 = t;
        }
        return n1;
    }

    /**
     * Computes the greatest common divisor of two numbers and a pair of number (u,v) such that
     * <pre>ua+vb=gcd(a,b)</pre>
     * If one of a or b is zero, then gcd(a,b) is another one.
     *
     * @return an int array of <code>{gcd(a,b), u, v}</code>.
     */
    public static int[] gcdUV(int a, int b) {
        if (a == 0) {
            return new int[]{b, 0, 1};
        }
        if (b == 0) {
            return new int[]{a, 1, 0};
        }
        return gcdUV0(a,b);
    }

    static int[] gcdUV0(int a,int b){
        int[] quotients = new int[4];
        int n = 0;
        while (true) {
            int q = a / b;
            int r = a % b;
            if(r == 0){
                break;
            }
            quotients = ArraySup.ensureCapacityAndAdd(quotients, q, n++);
            a = b;
            b = r;
        }
        // computes u and v
        int u0 = 1, u1 = 0,
                v0 = 0, v1 = 1;
        // u[s] = u[s-2]-q[s-2]*u[s-1]
        for(int i=0;i<n;i++){
            int nextU = u0 - quotients[i] * u1;
            int nextV = v0 - quotients[i] * v1;
            u0 = u1;
            u1 = nextU;
            v0 = v1;
            v1 = nextV;
        }
        return new int[]{b, u1, v1};
    }


    /**
     * Computes the greatest common divisor of two numbers and a pair of number (u,v) such that
     * <pre>ua+vb=gcd(a,b)</pre>
     * If one of a or b is zero, then gcd(a,b) is another one.
     *
     * @return an long array of <code>{gcd(a,b), u, v}</code>.
     */
    public static long[] gcdUV(long a, long b) {
        if (a == 0) {
            return new long[]{b, 0, 1};
        }
        if (b == 0) {
            return new long[]{a, 1, 0};
        }
        return gcdUV0(a,b);
    }

    static long[] gcdUV0(long a,long b){
        long[] quotients = new long[4];
        int n = 0;
        while (true) {
            long q = a / b;
            long r = a % b;
            if(r == 0){
                break;
            }
            quotients = ArraySup.ensureCapacityAndAdd(quotients, q, n++);
            a = b;
            b = r;
        }
        // computes u and v
        long u0 = 1, u1 = 0,
                v0 = 0, v1 = 1;
        // u[s] = u[s-2]-q[s-2]*u[s-1]
        for(int i=0;i<n;i++){
            long nextU = u0 - quotients[i] * u1;
            long nextV = v0 - quotients[i] * v1;
            u0 = u1;
            u1 = nextU;
            v0 = v1;
            v1 = nextV;
        }
        return new long[]{b, u1, v1};
    }

    /**
     * Calculate the two numbers' least common multiple.The parameters are required to be positive,
     * if either of them is negative,the result is unspecified.
     *
     * @param n1 a number
     * @param n2 another number
     * @return LCM of n1 and n2.
     */
    public static long lcm(long n1, long n2) {
        long gcd = gcd(n1, n2);
        return n1 / gcd * n2;
    }

    /**
     * Calculate the two int numbers' least common multiple.The parameters are required to be positive,
     * if either of them is negative,the result is unspecified.
     *
     * @param n1 a number
     * @param n2 another number
     * @return LCM of n1 and n2.
     */
    public static int lcm(int n1, int n2) {
        int gcd = gcd(n1, n2);
        return n1 / gcd * n2;
    }

    /**
     * Calculate the numbers' GCD.The numbers are required to be positive.
     *
     * @param ls an array of numbers,at least one element.
     * @return the GCD of {@code ls}
     */
    public static long gcd(long... ls) {
        if (ls.length < 2) {
            return ls[0];
        }
        long gcd = gcd(ls[0], ls[1]);
        for (int i = 2; i < ls.length; i++) {
            gcd = gcd(gcd, ls[i]);
        }
        return gcd;
    }


    /**
     * Calculate the numbers' GCD and LCM.The result will be stored in an array with two
     * elements.The numbers are required to be positive.
     *
     * @param ls an array of numbers,at least one element.
     * @return an array,first element of which is the GCD of {@code ls},and the
     * second element is the LCM of {@code ls}.
     */
    public static long lcm(long... ls) {
        if (ls.length < 2) {
            return ls[0];
        }
        long lcm = ls[0];
        for (int i = 1; i < ls.length; i++) {
            lcm = lcm(lcm, ls[i]);
        }
        return lcm;
    }

    public static BigInteger lcm(BigInteger n1, BigInteger n2) {
        BigInteger gcd = n1.gcd(n2);
        return n1.divide(gcd).multiply(n2);
    }

    /**
     * Returns the max number k that {@code |b|%|a|^k==0} while {@code |b|%|a|^(k+1)!=0}.
     *
     * @param a a number except {@code 0,1,-1}.
     * @param b another number
     * @return deg(a, b)
     */
    @SuppressWarnings("Duplicates")
    public static int deg(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);

        if (a == 0 || a == 1) {
            throw new IllegalArgumentException();
        }
        int k = 0;
        while (b % a == 0) {
            k++;
            b = b / a;
        }
        return k;
    }

    /**
     * Returns the max number k that {@code |b|%|a|^k==0} while {@code |b|%|a|^(k+1)!=0}, with
     * its parameters are ints.
     *
     * @param a a number except {@code 0,1,-1}.
     * @param b another number
     * @return deg(a, b)
     */
    @SuppressWarnings("Duplicates")
    public static int deg(int a, int b) {
        a = Math.abs(a);
        b = Math.abs(b);
        if (a == 0 || a == 1) {
            throw new IllegalArgumentException();
        }
        int k = 0;
        while (b % a == 0) {
            k++;
            b = b / a;
        }
        return k;
    }

    /**
     * Returns the result of {@code deg(|p|,|n|!)},
     *
     * @param p a number except {@code 0,1,-1}.
     * @param n another number
     * @return the result
     */
    public static long degFactorial(long p, long n) {
        p = Math.abs(p);
        n = Math.abs(n);
        if (p == 0 || p == 1) {
            throw new IllegalArgumentException();
        }
        long re = 0;
        while ((n = n / p) != 0) {
            re += n;
        }
        return re;
    }

    /**
     * Returns the max number of k that {@code a^k <= b && a^(k+1) > b},this
     * method requires that {@code |a| > 1 && b != 0}.
     * <p>This method is equal to {@literal [log(a,b)]} in math.
     *
     * @param a a number , {@code |a| > 1}
     * @param b a number , {@code b != 0}
     * @return the result , non-negative
     */
    public static int maxPower(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        if (a == 0 || a == 1 || b == 0) {
            throw new IllegalArgumentException();
        }
        int re = 0;
        long p = 1;
        while (p <= b) {
            p *= a;
            re++;
//            print(p + ",re=" + re);
        }
        return --re;
    }

    /**
     * Calculate the square root of {@code n}.If n cannot be expressed as
     * a square of an integer,then {@code -1} will be returned. Throws an
     * exception if {@code n<0}
     *
     * @param n a number, positive or 0.
     * @return the positive exact square root of n,or {@code -1}
     */
    public static long squareRootExact(long n) {
        if (n < 0) {
            throw new ArithmeticException();
        }
        if (n == 0) {
            return 0;
        }
        if (n == 1) {
            return 1;
        }
        long re = 1;
        //fast even number test
        while ((n & 1) == 0) {
            n >>= 1;
            if ((n & 1) != 0) {
                return -1;
            }

            n >>= 1;
            re <<= 1;
        }
        long t = 3;
        long t2 = 9;
        while (t2 <= n) {
            if (n % t == 0) {
                if (n % t2 != 0) {
                    return -1;
                }
                re *= t;
                n /= t2;
                continue;
            }
            t = t + 2;
            t2 = t * t;
        }
        if (n != 1) {
            return -1;
        }
        return re;
    }

    public static boolean isPerfectSquare(long n) {
        return squareRootExact(n) >= 0;
    }

    /**
     * Return the value of n^p.
     *
     * @param n a number
     * @param p > -1
     * @return n^p
     * @throws ArithmeticException if p < 0 or p==0&&n==0
     * @see MathUtils#power(long, int)
     * @deprecated bad time performance
     */
    @Deprecated
    public static long power0(long n, int p) {
        if (p < 0) {
            throw new ArithmeticException("Cannot calculate as integer");
        } else if (p == 0) {
            if (n == 0) {
                throw new ArithmeticException("0^0");
            } else {
                return 1;
            }
        }
        // calculate two power:
        int p2 = 1;
        int n2 = 1;
        while (n2 <= p) {
            n2 *= 2;
            p2++;
        }
        long[] powers = new long[p2];
        powers[0] = n;
        for (int i = 1; i < p2; i++) {
            powers[i] = powers[i - 1] * powers[i - 1];
        }
        long re = 1;
        for (int i = 0; i < p2; i++) {
            if ((p & 1) == 1) {
                re *= powers[i];
            }
            p >>>= 1;
        }
        return re;
    }

    /**
     * Return the value of n^p.
     *
     * @param n a number
     * @param p > -1
     * @return n ^ p
     * @throws ArithmeticException if p < 0 or p==0&&n==0
     */
    public static long power(long n, int p) {
        if (p < 0) {
            throw new ArithmeticException("Cannot calculate as integer");
        } else if (p == 0) {
            if (n == 0L) {
                throw new ArithmeticException("0^0");
            } else {
                return 1L;
            }
        }
        long re = 1L;
        while (p > 0) {
            if ((p & 1) != 0) {
                re *= n;
            }
            n *= n;
            p >>= 1;
        }
        return re;
    }

    /**
     * Return the value of n^p.
     *
     * @param n a number
     * @param p > -1
     * @return n ^ p
     * @throws ArithmeticException if {@code p < 0} or {@code p==0&&n==0} or the result overflows a long
     */
    public static long powerExact(long n, int p) {
        if (p < 0) {
            throw new ArithmeticException("Cannot calculate as integer");
        } else if (p == 0) {
            if (n == 0) {
                throw new ArithmeticException("0^0");
            } else {
                return 1;
            }
        }
        long re = 1L;
        while (p > 0) {
            if ((p & 1) != 0) {
                re = Math.multiplyExact(re, n);
            }
            n = Math.multiplyExact(n, n);
            p >>= 1;
        }
        return re;
    }


    /**
     * Turn the vector = (x,y) anticlockwise for {@code rad}.
     */
    public static double[] turnRad(double x, double y, double rad) {
        // x' = x cos - y sin
        // y' = x sin + y cos

        double sin = Math.sin(rad);
        double cos = Math.cos(rad);
        double[] xy = new double[2];
        xy[0] = x * cos - y * sin;
        xy[1] = x * sin + y * cos;
        return xy;
    }

    /**
     * find the number n :
     * {@code n=2^k, 2^(k-1)<num<n}
     *
     * @param num num>0
     * @return
     */
    public static int findMin2T(int num) {
        int n = 1;
        while (n < num) {
            n <<= 1;
        }
        return n;
    }


    /**
     * Return the average of a1 and a2 exactly as an integer,
     * this method is equal to (a1+a2)/2 without overflow and
     * underflow.
     *
     * @return (a1 + a2)/2
     */
    public static int averageExactly(int a1, int a2) {
        return (int) (((long) a1 + (long) a2) / 2);
    }

    /**
     * Returns the positive n-th root of {@code x},or {@code -1} if it cannot be represent as
     * long.For example {@code rootN(1024,5) = 4}
     *
     * @param x a number
     * @param n indicate the times of root
     * @return n-th root of {@code x}
     * @throws IllegalArgumentException if {@code n<=0} or {@code x<0}
     */
    public static long rootN(long x, int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("n<=0");
        }
        if (n == 1) {
            return x;
        }
        if (x == 1L || x == 0L) {
            return x;
        }
        long root = 2L;
        //try from 2.
        long t;
        while (true) {
            t = powerF(root, n);
            if (t == x) {
                break;
            }
            if (t <= 0 || t > x) {
                root = -1;
                break;
            }
            root++;
        }
        return root;
    }

    /**
     * A fast version of power.
     *
     * @param p
     * @param n
     * @return
     */
    private static long powerF(long n, int p) {
        long re = 1L;
        while (p > 0) {
            if ((p & 1) != 0) {
                re *= n;
            }
            n *= n;
            p >>= 1;
        }
        return re;
    }

    /**
     * Returns {@code (a^n) % mod}, for example, {@code powerAndMod(2,2,3) = 1}.This
     * method will not check overflow.
     *
     * @param a   a number, positive
     * @param n   must be >=0, if n < 0, than it is taken as 0 and 1 will be returned.
     * @param mod the modular, must be less than 2<<63, or overflow may happen.
     * @return
     */
    public static long powerAndMod(long a, long n, long mod) {
        if (a < 0) {
            throw new IllegalArgumentException("a<0");
        }
        if (mod == 1) {
            return 0;
        }
        if (a == 0 || a == 1) {
            return a;
        }
        long ans = 1;
        a = a % mod;
        //noinspection Duplicates
        while (n > 0) {
            if ((n & 1) == 1) {
                ans = (a * ans) % mod;

            }
            a = (a * a) % mod;
            n >>= 1;
        }
        return ans;
    }

    /**
     * Returns {@code (a^n) % mod}, with number as int. For example, {@code powerAndMod(2,2,3) = 1}.This
     * method will not check overflow.
     *
     * @param a   a number, positive
     * @param n   must be >=0, if n < 0, than it is taken as 0 and 1 will be returned.
     * @param mod the modular, must be less than 2<<31, or overflow may happen.
     */
    public static int powerAndMod(int a, int n, int mod) {
        if (a < 0) {
            throw new IllegalArgumentException("a<0");
        }
        if (mod == 1) {
            return 0;
        }
        if (a == 0 || a == 1) {
            return a;
        }
        int ans = 1;
        a = a % mod;
        //noinspection Duplicates
        while (n > 0) {
            if ((n & 1) == 1) {
                ans = (a * ans) % mod;

            }
            a = (a * a) % mod;
            n >>= 1;
        }
        return ans;
    }

    /**
     * {@code (a^n) % mod}, with number as int. For example, {@code powerAndMod(2,2,3) = 1}.
     * This method will not check overflow.
     *
     * @param a   a number, positive
     * @param n   must be >=0, if n < 0, than it is taken as 0 and 1 will be returned.
     * @param mod the modular, must be less than 2<<31, or overflow may happen.
     */
    public static int powerAndMod(int a, long n, int mod) {
        return (int) powerAndMod((long) a, n, (long) mod);
    }

    /**
     * Produces Miller-Rabin prime number test algorithm for the number x.If this
     * method returns true, then the number has the possibility of (1/4)^round of not
     * being a prime.
     *
     * @param x     a number,positive
     * @param round round number for test, positive
     * @return true if the number passes the test.
     */
    public static boolean doMillerRabin(long x, long round) {
        //basic check
        if (x == 2 || x == 3 || x == 5 || x == 7 || x == 11 || x == 13 ||
                x == 17 || x == 19 || x == 23 || x == 29 || x == 31) {
            return true;
        }
        if (x < 2) {
            throw new IllegalArgumentException("x<=1");
        }
        if (round < 1) {
            throw new IllegalArgumentException("round < 1");
        }
        long x_1 = x - 1;
        long d = x_1;
        int s = 0;
        while ((d & 1) == 0) {
            d >>= 1;
            s++;
        }
        Random rd = new Random();
        for (int i = 0; i < round; i++) {
            long a = randomLong(rd, x);
            long t = powerAndMod(a, d, x);
            if (t == 1) {
                return false;

            }
            for (int r = 0; r < s; r++, t = (t * t) % x) {
                if (t == x_1) {
                    return false;
                }
            }

        }
        return true;
    }

    /**
     * Produces a random long number x (x>=0 && x < bound) according to the random.
     *
     * @param rd    a random
     * @param bound exclusive
     * @return
     */
    public static long randomLong(Random rd, long bound) {
        if (bound <= Integer.MAX_VALUE) {
            return rd.nextInt((int) bound);
        }
        long mask = Long.MAX_VALUE;
        while (mask >= bound) {
            mask >>= 1;
        }
        mask <<= 1;
        mask--;
        while (true) {
            long r = rd.nextLong() & mask;
            if (r < bound) {
                return r;
            }
        }
    }

    /**
     * Produces Miller-Rabin prime number test algorithm for the number x.If this
     * method returns true, then the number is almost a prime number( 1/(2<<50) chance of not
     * being one).
     *
     * @param x a number,positive
     * @return true if the number passes the test.
     * @see MathUtils#doMillerRabin(long, long)
     */
    public static boolean doMillerRabin(long x) {
        return doMillerRabin(x, 25);
    }

    /**
     * Computes the n-th katalan number. This method do not consider the overflow.
     *
     * @param n
     * @return
     */
    public static long calculateKatalan(int n) {
        long[] katList = new long[n];
        katList[0] = 1;
        return ck0(n - 1, katList);
    }

    private static long ck0(int n, long[] katList) {
        if (katList[n] != 0) {
            return katList[n];
        }
        long sum = 0;
        for (int i = 0; i < n; i++) {
            sum += ck0(i, katList) * ck0(n - i - 1, katList);
        }
        katList[n] = sum;
        return sum;
    }

    /**
     * Solve an equation that
     * <pre>ax^2 + bx + c = 0</pre>
     * This method will ignore imaginary solutions.
     * <p>This method will return a list of solutions,which will contain
     * no element if there is no real solution({@code delta<0}),
     * one if there is only one solution(or two solutions of the identity value)({@code delta==0})
     * or two elements if there are two solutions(({@code delta>0}).
     * <p>This method normally requires {@code squareRoot()} method of the {@link MathCalculator}.
     *
     * @param a  the coefficient of x^2.
     * @param b  the coefficient of x.
     * @param c  the constant coefficient
     * @param mc a MathCalculator
     * @return the list of solution,regardless of order.
     */
    public static <T> List<T> solveEquation(T a, T b, T c, MathCalculator<T> mc) {
        //Calculate the delta
        T delta;
        {//=mc.subtract(mc.multiply(b, b), mc.multiplyLong(mc.multiply(a, c), 4l));;
            T t1 = mc.multiply(b, b);
            T t2 = mc.multiply(a, c);
            T t3 = mc.multiplyLong(t2, 4L);
            delta = mc.subtract(t1, t3);
        }
        int compare = 1;
        try {
            compare = mc.compare(delta, mc.getZero());
        } catch (UnsupportedCalculationException ex) {
            try {
                if (mc.isZero(delta))
                    compare = 0;
            } catch (UnsupportedCalculationException ex2) {
            }
        }
//		Printer.print(delta);
        if (compare < 0) {
            //no solution
            return Collections.emptyList();
        } else if (compare == 0) {
            List<T> so = new ArrayList<>(1);
            // -b/2a
            T re = mc.divide(mc.divideLong(b, -2L), a);
            so.add(re);
            return so;
        } else {
            // x1 = (-b + sqr(delta)) / 2a
            // x2 = (-b - sqr(delta)) / 2a
            List<T> so = new ArrayList<>(2);
            delta = mc.squareRoot(delta);
            T a2 = mc.multiplyLong(a, 2);
            T re = mc.divide(mc.subtract(delta, b), a2);
            so.add(re);
            re = mc.negate(mc.divide(mc.add(b, delta), a2));
            so.add(re);
            return so;
        }
    }

    /**
     * Solve an equation of
     * <pre>ax^2 + bx + c = 0</pre>
     * This method will use the root-formula and will compute all of the solutions(include imaginary
     * solutions),and always returns two solutions even if the two solutions are the identity.
     *
     * @param a  the coefficient of x^2.
     * @param b  the coefficient of x.
     * @param c  the constant coefficient
     * @param mc a MathCalculator
     * @return a list of the solutions
     */
    public static <T> List<T> solveEquationIma(T a, T b, T c, MathCalculator<T> mc) {
        T delta = mc.subtract(mc.multiply(b, b), mc.multiplyLong(mc.multiply(a, c), 4L));
        // x1 = (-b + sqr(delta)) / 2a
        // x2 = (-b - sqr(delta)) / 2a
        List<T> so = new ArrayList<>(2);
        delta = mc.squareRoot(delta);
        T a2 = mc.multiplyLong(a, 2);
        T re = mc.divide(mc.subtract(delta, b), a2);
        so.add(re);
        re = mc.negate(mc.divide(mc.add(b, delta), a2));
        so.add(re);
        return so;
    }

    /**
     * Solves an inequation of
     * <pre>ax^2 + bx + c = 0</pre>
     *
     * @param a   the coefficient of x^2.
     * @param b   the coefficient of x.
     * @param c   the constant coefficient
     * @param mc  a MathCalculator
     * @param <T>
     * @return
     */
    public static <T> IntervalUnion<T> solveInequation(T a, T b, T c, Type op, MathCalculator<T> mc) {
        return SVPInequation.quadratic(a, b, c, op, mc).getSolution();
    }

    /**
     * Reduce the number to an array representing each digit of the radix. The
     * {@code number} should be the sum of
     * <pre>result[i] * radix^i</pre>
     *
     * @param number a positive integer
     * @param radix  an integer bigger than one
     * @return an array containing corresponding digits.
     */
    public static int[] radix(int number, int radix) {
        if (number < 0) {
            throw new IllegalArgumentException("number < 0");
        }
        checkValidRadix(radix);
        if (number < radix) {
            return new int[]{number};
        }
        int maxPow = maxPower(radix, number);
//        print(maxPow);
        int[] res = new int[maxPow + 1];
        int i = 0;
        while (number > 0) {
            int t = number / radix;
            res[i++] = number - radix * t;
            number = t;
        }
        return res;
    }

    private static void checkValidRadix(int radix) {
        if (radix <= 1) {
            throw new IllegalArgumentException("radix <= 1");
        }
    }

    /**
     * Returns a number that is equal to the sum of
     * <pre>digits[i] * radix^i</pre>
     *
     * @param digits an array of digits.
     * @param radix  an integer bigger than one
     */
    public static int fromRadix(int[] digits, int radix) {
        checkValidRadix(radix);
        if (digits.length == 0) {
            return 0;
        }
        int result = digits[digits.length - 1];
        for (int i = digits.length - 2; i > -1; i--) {
            result *= radix;
            result += digits[i];
        }
        return result;
    }

    private static void checkValidRadix(long radix) {
        if (radix <= 1) {
            throw new IllegalArgumentException("radix <= 1");
        }
    }

    /**
     * Reduce the number to an array representing each digit of the radix. The
     * {@code number} should be the sum of
     * <pre>result[i] * radix^i</pre>
     *
     * @param number a positive integer
     * @param radix  an integer bigger than one
     * @return an array containing corresponding digits.
     */
    public static long[] radix(long number, long radix) {
        if (number < 0) {
            throw new IllegalArgumentException("number < 0");
        }
        checkValidRadix(radix);
        if (number < radix) {
            return new long[]{number};
        }
        int maxPow = maxPower(radix, number);
//        print(maxPow);
        long[] res = new long[maxPow + 1];
        int i = 0;
        while (number > 0) {
            long t = number / radix;
            res[i++] = number - radix * t;
            number = t;
        }
        return res;
    }

    /**
     * Returns a number that is equal to the sum of
     * <pre>digits[i] * radix^i</pre>
     *
     * @param digits an array of digits.
     * @param radix  an integer bigger than one
     */
    public static long fromRadix(long[] digits, long radix) {
        checkValidRadix(radix);
        if (digits.length == 0) {
            return 0;
        }
        long result = digits[digits.length - 1];
        for (int i = digits.length - 2; i > -1; i--) {
            result *= radix;
            result += digits[i];
        }
        return result;
    }

    /**
     * Returns the integer value of the square root of @{@code n}.
     * <pre>[sqrt(n)]</pre>
     *
     * @param n a positive number.
     * @return {@code [sqrt(n)]}
     */
    public static long sqrtIntL(long n) {
        if (n < 0) {
            throw new ArithmeticException("n<0");
        }
        if (n < 16) {
            return sqrtInt0(n, 0);
        }
        if (n >= 9223372030926249001L) {
            //to prevent the overflow
            return 3037000499L;
        }
        int p = 4;
        //find the lower bound and the upper bound.
        long high = 64L;
        while (high < n) {
            p += 2;
            high *= 4;
        }
        p >>>= 1;
        long low = 1L << p;
        high = low << 1;
        return ModelPatterns.binarySearchL(low, high, (long x) -> {
            long sqr = x * x;
//			print(x);
            if (sqr == n) {
                return 0;
            }
            if (sqr > n) {
                return 1;
            }
            if (sqr + 2 * x + 1 > n) {
                return 0;
            }
            return -1;
        });
    }


    private static long sqrtInt0(long n, long lb) {
        while (lb * lb <= n) {
            lb++;
        }
        return lb - 1;
    }

    /**
     * Returns the arctan value of y/x.
     *
     * @param mc
     * @param x
     * @param y
     * @return
     */
    public static <T> T atan(MathCalculator<T> mc, T x, T y) {
        if (mc.isZero(x)) {
            int comp = mc.compare(y, mc.getZero());
            if (comp == 0) {
                throw new ArithmeticException("x=y=0!");
            }
            T pi_2 = mc.divideLong(Objects.requireNonNull(mc.constantValue(MathCalculator.STR_PI)), 2L);
            return comp > 0 ? pi_2 : mc.negate(pi_2);
        }
        return mc.arctan(mc.divide(y, x));
    }

    /**
     * Returns {@code x*y<=0}
     */
    public static boolean oppositeSignum(double x, double y) {
        return (x >= 0d && y <= 0d) || (x <= 0d && y >= 0d);
    }

    /**
     * Returns {@code (x-a)(y-a)<=0}
     */
    public static boolean oppositeSide(double x, double y, double a) {
        return (x >= a && y <= a) || (x <= a && y >= a);
    }

    /**
     * Returns {@code (x-a)(y-a)<=0}
     */
    public static <T> boolean oppositeSide(T x, T y, T a, MathCalculator<T> mc) {
        return mc.compare(x, a) * mc.compare(y, a) <= 0;
    }

    /**
     * Determines whether x and y have the same sign.
     */
    public static boolean sameSignum(int x, int y) {
        if (x > 0) {
            return y > 0;
        } else if (x == 0) {
            return y == 0;
        } else {//x<0
            return y < 0;
        }
    }

    public static int signum(int x) {
        return Integer.signum(x);
    }

    public static int signum(long x) {
        return Long.signum(x);
    }

    public static int signum(double x) {
        return Double.compare(x, 0);
    }

    /**
     * Determines whether {@code x} is closer to zero than {@code y}(or equal),
     * in other words returns {@code Math.abs(x) <= Math.abs(y)}
     *
     * @param x
     * @param y
     * @return
     */
    public static boolean closerToZero(double x, double y) {
        return Math.abs(x) <= Math.abs(y);
    }

    /**
     * Returns the so-called 'Tschebyscheff distance':
     * {@code max(abs(x1-x2), abs(y1-y2))}
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static double tschebyscheffDistance(double x1, double y1, double x2, double y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    /**
     * Returns the distance of (x1,y2) and (x2,y2) defined in space Lp, which is equal to
     * <pre>(abs(x1-x2)^p+abs(y1-y2)^p)^(1/p)</pre>
     * If {@code p==Double.POSITIVE_INFINITY}, then returns the tschebyscheff distance.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param p
     * @return
     */
    public static double distanceP(double x1, double y1, double x2, double y2, double p) {
        if (p == Double.POSITIVE_INFINITY) {
            return tschebyscheffDistance(x1, y1, x2, y2);
        }
        if (p <= 0) {
            throw new IllegalArgumentException("p<=0");
        }
        double dx = Math.abs(x1 - x2),
                dy = Math.abs(y1 - y2);
        return Math.pow(Math.pow(dx, p) + Math.pow(dy, p), 1 / p);

    }

    /**
     * Returns the biggest number n that meets the requirements that:
     * {@code n = k*p} where {@code k} is an integer,
     * {@code n <= x}.
     *
     * @param x a number
     * @param p a positive number
     * @return
     */
    public static double maxBelow(double x, double p) {
        p = Math.abs(p);
        if (x < 0) {
            double t = x % p;
            if (t == 0) {
                return x;
            }
            return x - t - p;
        } else {
            return x - x % p;
        }
    }

    /**
     * Returns the biggest integer k that meets the requirements that:
     * {@code n = k*p} and
     * {@code n <= x}.
     *
     * @param x a positive number
     * @param p a positive number
     * @return
     */
    public static long maxBelowK(double x, double p) {
        if (p > x) {
            return 0;
        }
        long n = 1;
        double t = p;
        while (t < x) {
            t *= 2;
            n = n * 2;
        }
        long d = n / 2;
        for (; n > d; n--) {
            if (t <= x) {
                return n;
            }
            t -= p;
        }
        return d;
    }

    /**
     * Returns the number of factors of the integer.
     *
     * @return a positive integer
     */
    public static long factorCount(long n) {
        long[][] factors = factorReduce(n);
        return factorCount0(factors);
    }

    private static long factorCount0(long[][] factors) {
        long num = 1;
        for (long[] factor : factors) {
            num *= factor[1] + 1;
        }
        return num;
    }

    /**
     * Returns an array containing all the factors of <code>n</code> in order.
     *
     * @param n a positive integer
     */
    public static long[] factors(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("n<1");
        }
        //two ways
        Primes pr = Primes.getInstance();
        if (n <= FACTOR_ENUMERATE_THREHOLD && !pr.isPrimesAvailable(n / 2 + 1)) {
            return factorsEnumerate(n);
        } else {
            return factorsUsingPrimes(n);
        }
    }

    private static final long FACTOR_ENUMERATE_THREHOLD = 10000;

    private static long[] factorsEnumerate(long n) {
        long[] factors = new long[16];
        factors[0] = 1;
        int idx = 1;
        for (long t = 2; t <= n; t++) {
            if (n % t == 0) {
                factors = ensureCapacityAndAdd(factors, t, idx);
                idx++;
            }
        }
        return Arrays.copyOf(factors, idx);
    }

    private static long[] factorsUsingPrimes(long n) {
        long[][] fr = factorReduce(n);
        long[] factors = new long[Math.toIntExact(factorCount0(fr))];
        addFactor(fr, factors, 0, 1, 0);
        Arrays.sort(factors);
        return factors;
    }

    /**
     * @param fr
     * @param factors
     * @param order   order in fr
     * @param base    multiplied previously
     * @param index   index in factors
     * @return
     */
    private static int addFactor(long[][] fr, long[] factors, int order, long base, int index) {
        if (order == fr.length) {
            factors[index] = base;
            return index + 1;
        }
        long[] pFactor = fr[order];
        int maxPower = Math.toIntExact(pFactor[1]);
        for (int i = 0; i <= maxPower; i++) {
            index = addFactor(fr, factors, order + 1, base * power(pFactor[0], i), index);
        }
        return index;
    }


    /**
     * Returns a two-dimension array representing the
     * number's prime factors and the corresponding times.
     * <P>For example, <text> factorReduce(6)={{2,1},{3,1}} </text>
     */
    public static long[][] factorReduce(long n) {
        if (n < 1) {
            throw new IllegalArgumentException("n<1");
        }
        if (n == 1) {
            return new long[][]{{1, 1}};
        }

        Primes pr = Primes.getInstance();
        long[][] factors = new long[16][];
        int count = 0;
        int index = 0;

        while (true) {
            long p = pr.getPrime(index++);
            if (n < p) {
                break;
            }
            if (n % p == 0) {
                long[] pair = new long[]{p, 0};
                factors = ensureCapacityAndAdd(factors, pair, count);
                do {
                    pair[1]++;
                    n = n / p;
                } while (n % p == 0);
                count++;
            }
        }


        if (n != 1) {
            factors = ensureCapacityAndAdd(factors, new long[]{n, 1}, count);
            count++;
        }
        if (factors.length > count) {
            factors = Arrays.copyOf(factors, count);
        }
        return factors;
    }

    /**
     * Returns the sum of
     * <code>factorsAndPower[i][0] ^ factorsAndPower[i][1]</code>
     *
     * @param factorsAndPower an two-dimension array containing the factors and its corresponding power.
     */
    public static long fromFactors(long[][] factorsAndPower) {
        long re = 1;
        for (long[] f : factorsAndPower) {
            re *= power(f[0], Math.toIntExact(f[1]));
        }
        return re;
    }

    /**
     * Returns the sum of
     * <code>p[i] ^ factorPower[i]</code>, where p[i] is the
     * i-th prime number starting from p[0] = 2.
     */
    public static long fromFactorPowers(int[] factorPower) {
        if (factorPower.length == 0) {
            return 1;
        }
        long re = 1;
        Primes pr = Primes.getInstance();
        pr.ensurePrimesNumber(factorPower.length - 1);
        for (int i = 0; i < factorPower.length; i++) {
            re *= power(pr.getPrime(i), factorPower[i]);
        }
        return re;
    }

    /**
     * The radical of n, rad(n),
     * is the product of distinct prime factors of n.
     * For example, 504 = 23 × 32 × 7, so rad(504) = 2 × 3 × 7 = 42.
     *
     * @return rad(n)
     */
    public static long rad(long n) {
        long[][] pfactors = factorReduce(n);
        long re = 1;
        for (long[] f : pfactors) {
            re *= f[0];
        }
        return re;
    }

    /**
     * Returns the sum of factors of <code>n</code>.
     *
     * @param n a positive integer
     */
    public static long factorSum(long n) {
        long[][] factors = factorReduce(n);
        long sum = 1;
        for (long[] factor : factors) {
            if (factor[0] == 1) {
                sum += 1;
                continue;
            }
            //1+a+a^2+...+a^p = (a^(p+1)-1)/(a-1)
            long t = power(factor[0], Math.toIntExact(factor[1] + 1)) - 1;
            t /= (factor[0] - 1);
            sum *= t;
        }
        return sum;
    }

    /**
     * Computes the biggest factor {@code result} of {@code n} that satisfies {@code p^exp = result}, where
     * {@code p} is an integer. This method will return an array composed of the biggest factor described ahead,
     * the integer {@code p}.
     * <p></p>For example, {@code integerExp(81,1/3) = (27,3)}, because {@code 3^3 = 27}.
     *
     * @param n   a non-negative number
     * @param exp a positive fraction
     * @return an array of the biggest factor {@code result} and the base {@code p}
     */
    public static long[] integerExpFloor(long n, Fraction exp) {
        var t = integerExpCheck(n, exp);
        if (t != null) {
            return t;
        }
        long[][] factors = factorReduce(n);
        long base = 1;
        long result = 1;
        for (long[] factor : factors) {
            long pow = factor[1];
            int basePow = Math.toIntExact(exp.multiply(pow).floor());
            base *= power(factor[0], basePow);
            int rePow = Fraction.valueOf(basePow).divide(exp).toInt();
            result *= power(factor[0], rePow);
        }
        return new long[]{result, base};
    }

    private static long[] integerExpCheck(long n, Fraction exp) {
        if (n < 0) {
            throw new IllegalArgumentException("n<0");
        }
        if (!exp.isPositive()) {
            throw new IllegalArgumentException("exp < 0");
        }
        if (n == 0) {
            if (exp.isZero()) {
                ExceptionUtil.INSTANCE.zeroExponent();
                return null;
            }
            return new long[]{0, 0};
        }
        if (n == 1) {
            return new long[]{1, 1};
        }
        return null;
    }

    /**
     * Computes the smallest multiple {@code result} of {@code n} that satisfies {@code p^exp = result}, where
     * {@code p} is an integer. This method will return an array composed of the smallest multiple described ahead,
     * the integer {@code p}.
     * <p></p>For example, {@code integerExp(81,1/3) = (3^6,3)}.
     *
     * @param n   a non-negative number
     * @param exp a positive fraction
     * @return an array of the biggest factor {@code result} and the base {@code p}
     */
    public static long[] integerExpCeil(long n, Fraction exp) {
        var t = integerExpCheck(n, exp);
        if (t != null) {
            return t;
        }
        long[][] factors = factorReduce(n);
        long base = 1;
        long result = 1;
        for (long[] factor : factors) {
            long pow = factor[1];
            int basePow = Math.toIntExact(exp.multiply(pow).ceil());
            base *= power(factor[0], basePow);
            int rePow = Fraction.valueOf(basePow).divide(exp).toInt();
            result *= power(factor[0], rePow);
        }
        return new long[]{result, base};
    }

    public static String simplifyExpression(String expr) {
        var ex = Expression.valueOf(expr);
        var mc = ExprCalculator.Companion.getNewInstance();
        return mc.simplify(ex).toString();
    }

}
