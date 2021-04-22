package cn.ancono.math;

import cn.ancono.math.exceptions.ExceptionUtil;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberTheory.Primes;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import static cn.ancono.utilities.ArraySup.ensureCapacityAndAdd;


/**
 * Provides some useful math functions which are not included in JDK.
 *
 * <p>
 * For functions related to combination (factorial, combination,...),
 * please refer to
 * {@linkplain cn.ancono.math.discrete.combination.CombUtils}
 *
 * @author lyc
 */
@SuppressWarnings("Duplicates")
public class MathUtils {

    /**
     * Returns the great common divisor (GCD) of two numbers, the result will always be non-negative.
     * <p></p>
     * Note: It follows from common conventions that <code>gcd(a, 0) = gcd(0, a) = a</code>
     *
     * @param n1 a number
     * @param n2 another number
     * @return <code>gcd(n1, n2)</code>
     */
    public static long gcd(long n1, long n2) {
        //use Euclidean gcd algorithm
        while (n2 != 0) {
            long t = n2;
            n2 = n1 % n2;
            n1 = t;
        }
        return Math.abs(n1);
    }

    /**
     * Returns the great common divisor (GCD) of two numbers, the result will always be non-negative.
     * <p></p>
     * Note: It follows from common conventions that <code>gcd(a, 0) = gcd(0, a) = a</code>
     *
     * @param n1 a number
     * @param n2 another number
     * @return <code>gcd(n1, n2)</code>
     */
    public static int gcd(int n1, int n2) {
        //use Euclid's gcd algorithm
        while (n2 != 0) {
            int t = n2;
            n2 = n1 % n2;
            n1 = t;
        }
        return Math.abs(n1);
    }

    static int[] gcdUV0(int a, int b) {
//        int[] quotients = new int[4];
//        int n = 0;
//        while (true) {
//            int q = a / b;
//            int r = a % b;
//            if (r == 0) {
//                break;
//            }
//            quotients = ArraySup.ensureCapacityAndAdd(quotients, q, n++);
//            a = b;
//            b = r;
//        }
//        // computes u and v
//        int u0 = 1, u1 = 0,
//                v0 = 0, v1 = 1;
//        // u[s] = u[s-2]-q[s-2]*u[s-1]
//        for (int i = 0; i < n; i++) {
//            int nextU = u0 - quotients[i] * u1;
//            int nextV = v0 - quotients[i] * v1;
//            u0 = u1;
//            u1 = nextU;
//            v0 = v1;
//            v1 = nextV;
//        }
//        return new int[]{b, u1, v1};
        //Re-implemented by lyc at 2020-03-03 15:57
        /*
        Euclid's Extended Algorithms:
        Refer to Henri Cohen 'A course in computational algebraic number theory' Algorithm 1.3.6
         */
        if (b == 0) {
            return new int[]{a, 1, 0};
        }
        /*
        Explanation of the algorithm:
        we want to maintain the following equation while computing the gcd using the Euclid's algorithm
        let d0=a, d1=b, d2, d3 ... be the sequence of remainders in Euclid's algorithm,
        then we have
            a*1 + b*0 = d0
            a*0 + b*1 = d1
        let
            u0 = 1, v0 = 0
            u1 = 0, v1 = 1
        then we want to build a sequence of u_i, v_i such that
            a*u_i + b*v_i = d_i,
        when we find the d_n = gcd(a,b), the corresponding u_n and v_n is what we want.
        We have:
            d_i = q_i * d_{i+1} + d_{i+2}        (by Euclid's algorithm
        so
            a*u_i + b*v_i = q_i * (a*u_{i+1} + b*v_{i+1}) + (a*u_{i+2} + b*v_{i+2})
            u_i - q_i * u_{i+1} = u_{i+2}
            v_i - q_i * v_{i+1} = v_{i+2}
        but it is only necessary for us to record u_i, since v_i can be calculated from the equation
            a*u_i + b*v_i = d_i
         */
        int d0 = a;
        int d1 = b;
        int u0 = 1;
        int u1 = 0;
        while (d1 > 0) {
            int q = d0 / d1;
            int d2 = d0 % d1;
            d0 = d1;
            d1 = d2;
            int u2 = u0 - q * u1;
            u0 = u1;
            u1 = u2;
        }
        int v = (d0 - a * u0) / b;
        return new int[]{d0, u0, v};
    }

    /**
     * Computes the greatest common divisor of two numbers and a pair of number <code>(u,v)</code> such that
     * <pre>ua+vb=gcd(a,b)</pre>
     * <p>
     * The result <code>gcd(a,b)</code> will always be positive.
     * It follows from common conventions that <code>gcd(a, 0) = gcd(0, a) = a</code>
     *
     * @return an int array of <code>[gcd(a,b), u, v]</code>.
     */
    public static int[] gcdUV(int a, int b) {
        int[] result = gcdUV0(Math.abs(a), Math.abs(b));
        //deal with negative values
        if (a < 0) {
            result[1] = -result[1];
        }
        if (b < 0) {
            result[2] = -result[2];
        }
        return result;
    }


    static long[] gcdUV0(long a, long b) {
//        long[] quotients = new long[4];
//        int n = 0;
//        while (true) {
//            long q = a / b;
//            long r = a % b;
//            if (r == 0) {
//                break;
//            }
//            quotients = ArraySup.ensureCapacityAndAdd(quotients, q, n++);
//            a = b;
//            b = r;
//        }
//        // computes u and v
//        long u0 = 1, u1 = 0,
//                v0 = 0, v1 = 1;
//        // u[s] = u[s-2]-q[s-2]*u[s-1]
//        for (int i = 0; i < n; i++) {
//            long nextU = u0 - quotients[i] * u1;
//            long nextV = v0 - quotients[i] * v1;
//            u0 = u1;
//            u1 = nextU;
//            v0 = v1;
//            v1 = nextV;
//        }
//        return new long[]{b, u1, v1};
        //Re-implemented by lyc at 2020-03-03 16:42
        /*
        Euclid's Extended Algorithms:
        Refer to Henri Cohen 'A course in computational algebraic number theory' Algorithm 1.3.6
         */
        if (b == 0) {
            return new long[]{a, 1, 0};
        }
        /*
        See the explanation above in the int version of the same algorithm
         */
        long d0 = a;
        long d1 = b;
        long u0 = 1;
        long u1 = 0;
        while (d1 > 0) {
            long q = d0 / d1;
            long d2 = d0 % d1;
            d0 = d1;
            d1 = d2;
            long u2 = u0 - q * u1;
            u0 = u1;
            u1 = u2;
        }
        long v = (d0 - a * u0) / b;
        return new long[]{d0, u0, v};
    }

    /**
     * Computes the greatest common divisor of two numbers and a pair of number (u,v) such that
     * <pre>ua+vb=gcd(a,b)</pre>
     * <p>
     * The result <code>gcd(a,b)</code> will always be positive.
     * It follows from common conventions that <code>gcd(a, 0) = gcd(0, a) = a</code>
     *
     * @return an array of <code>[gcd(a,b), u, v]</code>.
     */
    public static long[] gcdUV(long a, long b) {
        long[] result = gcdUV0(Math.abs(a), Math.abs(b));
        //deal with negative values
        if (a < 0) {
            result[1] = -result[1];
        }
        if (b < 0) {
            result[2] = -result[2];
        }
        return result;
    }


    /**
     * Calculate the two numbers' least common multiple(LCM). The result will always be positive.
     * <p></p>
     * Note: It follows from common conventions that <code>lcm(a, 0) = lcm(0, a) = 0</code>
     *
     * @param n1 a number
     * @param n2 another number
     * @return <code>lcm(n1, n2)</code>
     */
    public static long lcm(long n1, long n2) {
        long gcd = gcd(n1, n2);
        return Math.abs(n1 / gcd * n2);
    }

    /**
     * Calculate the two numbers' least common multiple(LCM). The result will always be positive.
     * <p></p>
     * Note: It follows from common conventions that <code>lcm(a, 0) = lcm(0, a) = 0</code>
     *
     * @param n1 a number
     * @param n2 another number
     * @return <code>lcm(n1, n2)</code>
     */
    public static int lcm(int n1, int n2) {
        int gcd = gcd(n1, n2);
        return Math.abs(n1 / gcd * n2);
    }

    /**
     * Returns the greatest common divisor of all the given numbers, the result will always be positive.
     *
     * @param ls an array of numbers with at least one element.
     * @return the GCD of {@code ls}
     * @see #gcd(long, long)
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
     * Calculate the least common multiplier (LCM) of the given numbers' LCM, the result will always be positive.
     *
     * @param ls an array of numbers with at least one element.
     * @return the LCM of <code>ls</code>
     * @see #lcm(long, long)
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

    /**
     * Calculate the two numbers' least common multiple(LCM). The result will always be positive.
     * <p></p>
     * Note: It follows from common conventions that <code>lcm(a, 0) = lcm(0, a) = 0</code>
     *
     * @param n1 a number
     * @param n2 another number
     * @return <code>lcm(n1, n2)</code>
     */
    public static BigInteger lcm(BigInteger n1, BigInteger n2) {
        BigInteger gcd = n1.gcd(n2);
        return n1.divide(gcd).multiply(n2);
    }

    /**
     * Returns the max number <code>k</code> that {@code |b|%|a|^k==0} while {@code |b|%|a|^(k+1)!=0}, that
     * is, the degree of of <code>a</code> in <code>b</code>.
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
     * Returns the max number <code>k</code> that {@code |b|%|a|^k==0} while {@code |b|%|a|^(k+1)!=0}, that
     * is, the degree of of <code>a</code> in <code>b</code>.
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
     * Returns the quotient of <code>a/b</code> if it is divisible, throws an Exception otherwise.
     */
    public static long divideExact(long a, long b) {
        if (a % b != 0) {
            ExceptionUtil.notExactDivision(a, b);
        }
        return a / b;
    }

    /**
     * Returns the quotient of <code>a/b</code> if it is divisible, throws an Exception otherwise.
     */
    public static int divideExact(int a, int b) {
        if (a % b != 0) {
            ExceptionUtil.notExactDivision(a, b);
        }
        return a / b;
    }

    /**
     * Calculate the square root of {@code n}. If n cannot be expressed as
     * a square of an integer,then {@code -1} will be returned. Throws an
     * exception if {@code n<0}
     *
     * @param n a number, positive or 0.
     * @return the positive exact square root of n,or {@code -1}
     */
    public static long squareRootExact(long n) {
        if (n < 0) {
            ExceptionUtil.sqrtForNegative();
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

    /**
     * Determines whether <code>n</code> is perfect square, that is, there exists <code>m</code>
     * such that <code>n = m^2</code>.
     */
    public static boolean isPerfectSquare(long n) {
        if (n < 0) {
            return false;
        }
        var sqrt = sqrtInt(n);
        return sqrt * sqrt == n;
    }

    /**
     * Return the value of n^p.
     *
     * @param n a number
     * @param p > -1
     * @return n^p
     * @throws ArithmeticException if p < 0 or p==0&&n==0
     * @see MathUtils#pow(long, int)
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
    public static long pow(long n, int p) {
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
     * find the number <code>n</code> such that
     * {@code n=2^k, 2^(k-1) < num < n}
     *
     * @param num num>0
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
        //TODO better implementation
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
     * Power using binary reduce.
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
     * Returns a non-negative integer of <code>a mod m</code>
     */
    public static int mod(int a, int m) {
        var re = a % m;
        if (re < 0) {
            re += m;
        }
        return re;
    }

    /**
     * Returns a non-negative integer of <code>a mod m</code>
     */
    public static long mod(long a, long m) {
        var re = a % m;
        if (re < 0) {
            re += m;
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
     * @return {@code (a^n) % mod}
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
     * Returns the mod inverse of <code>a</code> mod <code>p</code>. That is, a number <code>u</code> such that
     * <code>a * u = 1 (mod p)</code>. It is required that <code>a</code> and <code>p</code> are co-prime.
     */
    public static int modInverse(int a, int p) {
        //Created by lyc at 2020-03-03 16:50
        var arr = gcdUV(a, p);
        // au + pv = 1
        if (arr[0] != 1) {
            throw new ArithmeticException("a and p is not coprime: a=" + a + ", p=" + p);
        }
        return arr[1];
    }

    /**
     * Returns the mod inverse of <code>a</code> mod <code>p</code>. That is, a number <code>u</code> such that
     * <code>a * u = 1 (mod p)</code>. It is required that <code>a</code> and <code>p</code> are co-prime.
     */
    public static long modInverse(long a, long p) {
        //Created by lyc at 2020-03-03 16:50
        var arr = gcdUV(a, p);
        // au + pv = 1
        if (arr[0] != 1) {
            throw new ArithmeticException("a and p is not coprime: a=" + a + ", p=" + p);
        }
        return arr[1];
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
        return (int) powerAndMod(a, n, (long) mod);
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
     * Produces Miller-Rabin prime number test algorithm for the number x. If this
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
     * Produces a random long number x (x>=0 && x < bound) according to the random.
     *
     * @param rd    a random
     * @param bound exclusive
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
     * Returns the integer value of the square root of {@code n}, that is, an integer <code>m</code>
     * such that <code>m<sup>2</sup> <= n < <code>(m+1)<sup>2</sup></code></code>.
     *
     * @param n a positive number.
     * @return {@code [sqrt(n)]}
     */
    public static long sqrtInt(long n) {
        if (n < 0) {
            throw new ArithmeticException("n<0");
        }
        if (n >= 9223372030926249001L) {
            //to prevent overflow
            return 3037000499L;
        }
        return (long) Math.sqrt(n);

//        int p = 4;
//        //find the lower bound and the upper bound.
//        long high = 64L;
//        while (high < n) {
//            p += 2;
//            high *= 4;
//        }
//        p >>>= 1;
//        long low = 1L << p;
//        high = low << 1;
//        return ModelPatterns.binarySearchL(low, high, (long x) -> {
//            long sqr = x * x;
////			print(x);
//            if (sqr == n) {
//                return 0;
//            }
//            if (sqr > n) {
//                return 1;
//            }
//            if (sqr + 2 * x + 1 > n) {
//                return 0;
//            }
//            return -1;
//        });
    }


    /**
     * Returns {@code x*y<=0}
     */
    public static boolean isOppositeSign(double x, double y) {
        return (x >= 0d && y <= 0d) || (x <= 0d && y >= 0d);
    }

    /**
     * Returns {@code (x-a)(y-a)<=0}
     */
    public static boolean isOppositeSide(double x, double y, double a) {
        return (x >= a && y <= a) || (x <= a && y >= a);
    }


    /**
     * Determines whether x and y have the same sign.
     */
    public static boolean isSameSign(int x, int y) {
        if (x > 0) {
            return y > 0;
        } else if (x == 0) {
            return y == 0;
        } else {//x<0
            return y < 0;
        }
    }


    /**
     * Returns the sign number of <code>x</code> as an int.
     */
    public static int signum(double x) {
        return Double.compare(x, 0);
    }


    /**
     * Returns the so-called 'Tschebyscheff distance':
     * {@code max(abs(x1-x2), abs(y1-y2))}
     */
    public static double tschebyscheffDistance(double x1, double y1, double x2, double y2) {
        return Math.max(Math.abs(x1 - x2), Math.abs(y1 - y2));
    }

    /**
     * Returns the distance of (x1,y2) and (x2,y2) defined in space Lp, which is equal to
     * <pre>(abs(x1-x2)^p+abs(y1-y2)^p)^(1/p)</pre>
     * If {@code p==Double.POSITIVE_INFINITY}, then returns the tschebyscheff distance.
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
        if (n <= FACTOR_ENUMERATE_THRESHOLD && !pr.isPrimesAvailable(n / 2 + 1)) {
            return factorsEnumerate(n);
        } else {
            return factorsUsingPrimes(n);
        }
    }

    private static final long FACTOR_ENUMERATE_THRESHOLD = 10000;

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
     * @param order order in fr
     * @param base  multiplied previously
     * @param index index in factors
     */
    private static int addFactor(long[][] fr, long[] factors, int order, long base, int index) {
        if (order == fr.length) {
            factors[index] = base;
            return index + 1;
        }
        long[] pFactor = fr[order];
        int maxPower = Math.toIntExact(pFactor[1]);
        for (int i = 0; i <= maxPower; i++) {
            index = addFactor(fr, factors, order + 1, base * pow(pFactor[0], i), index);
        }
        return index;
    }


    /**
     * Returns a two-dimension array representing the
     * number's prime factors and the corresponding powers.
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
            re *= pow(f[0], Math.toIntExact(f[1]));
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
            re *= pow(pr.getPrime(i), factorPower[i]);
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
            long t = pow(factor[0], Math.toIntExact(factor[1] + 1)) - 1;
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
            base *= pow(factor[0], basePow);
            int rePow = Fraction.of(basePow).divide(exp).toInt();
            result *= pow(factor[0], rePow);
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
                ExceptionUtil.zeroExponent();
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
            base *= pow(factor[0], basePow);
            int rePow = Fraction.of(basePow).divide(exp).toInt();
            result *= pow(factor[0], rePow);
        }
        return new long[]{result, base};
    }

//    public static String simplifyExpression(String expr) {
//        var ex = Expression.valueOf(expr);
//        var mc = ExprCalculator.Companion.getNewInstance();
//        return mc.simplify(ex).toString();
//    }

    /**
     * Returns <code>(-1)<sup>pow</sup></code>.
     */
    public static int powOfMinusOne(int pow) {
        if (pow % 2 == 0) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Returns <code>x + (y - x) * k</code>.
     *
     * @param k the interpolate factor.
     */
    public static double interpolate(double x, double y, double k) {
        return x + (y - x) * k;
    }

    public static double product(double[] array) {
        double r = 1;
        for (double l : array) {
            r *= l;
        }
        return r;
    }

    public static long product(long[] array) {
        long r = 1;
        for (long l : array) {
            r *= l;
        }
        return r;
    }

    public static int product(int[] array) {
        int r = 1;
        for (int l : array) {
            r *= l;
        }
        return r;
    }

    public static double sum(double[] array) {
        double r = 0;
        for (double l : array) {
            r += l;
        }
        return r;
    }

    public static long sum(long[] array) {
        long r = 0;
        for (long l : array) {
            r += l;
        }
        return r;
    }

    public static int sum(int[] array) {
        int r = 0;
        for (int l : array) {
            r += l;
        }
        return r;
    }

    public static double sum(double[] array, int start, int end) {
        double r = 0;
        for (int i = start; i < end; i++) {
            r += array[i];
        }
        return r;
    }

    public static long sum(long[] array, int start, int end) {
        long r = 0;
        for (int i = start; i < end; i++) {
            r += array[i];
        }
        return r;
    }

    public static int sum(int[] array, int start, int end) {
        int r = 0;
        for (int i = start; i < end; i++) {
            r += array[i];
        }
        return r;
    }


    public static double inner(double[] x, double[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("The length must be the same! Given: " + x.length + ", " + y.length);
        }
        double result = 0;
        for (int i = 0; i < x.length; i++) {
            result += x[i] * y[i];
        }
        return result;
    }

    public static long inner(long[] x, long[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("The length must be the same! Given: " + x.length + ", " + y.length);
        }
        long result = 0;
        for (int i = 0; i < x.length; i++) {
            result += x[i] * y[i];
        }
        return result;
    }

    public static int inner(int[] x, int[] y) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("The length must be the same! Given: " + x.length + ", " + y.length);
        }
        int result = 0;
        for (int i = 0; i < x.length; i++) {
            result += x[i] * y[i];
        }
        return result;
    }


    /**
     * Returns a solution for the modular equations: <pre>x mod m<sub>i</sub> = r<sub>i</sub>,</pre> where
     * <code>m<sub>i</sub></code> are co-prime integers.
     * The result is guaranteed to be minimal non-negative solution.
     *
     * @param mods       an array of modular, <code>m<sub>i</sub></code>
     * @param remainders an array of remainders,
     * @return the solution of the modular equation
     */
    public static long chineseRemainder(long[] mods, long[] remainders) {
//        long M = product(mods);
//        long x = 0;
//        for (int i = 0; i < mods.length; i++) {
//            var m = mods[i];
//            var r = remainders[i];
//            var t = M / m;
//            var inv = modInverse(t, m);
//            x += r * t * inv;
//            x %= M;
//        }
//        return x;
        //Created by lyc at 2021-04-20 20:31
        /*
        Proof of this algorithm:
        Invariant: x satisfies: x = rem[j] mod m[j] for j < i
         */
        long m = mods[0];
        long x = remainders[0];
        for (int i = 1; i < mods.length; i++) {
            var t = gcdUV(m, mods[i]);
            var u = t[1];
            var v = t[2];
            // um + v m[i] = 1
            x = u * m * remainders[i] + v * mods[i] * x;
            // x mod m[i] = um*rem[i] mod m[i] =(1-v m[i])rem[i] mod m[i] = rem[i]
            // for j < i, x mod m[j] = v * m[i] * x mod m[j] = (1-um)x mod m[j] = x
            m = m * mods[i];
            x %= m;
            // x in (-m,m)
        }
        if (x < 0) {
            x += m; //make it non-negative
        }
        return x;
    }


    public static long primitiveRoot(long p) {
        if (p <= 2) {
            throw new IllegalArgumentException("p must be an odd prime!");
        }
        //TODO
        return 0;
    }

//    public static void main(String[] args) {
////        System.out.println(chineseRemainder(new long[]{3L,5L},new long[]{1,2}));
//        for (long i = 0; i < 20; i++) {
//            System.out.println(i+": "+sqrtInt(i));
//        }
//    }
}
