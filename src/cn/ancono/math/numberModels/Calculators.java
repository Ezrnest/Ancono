/**
 * 2017-09-22
 */
package cn.ancono.math.numberModels;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathUtils;
import cn.ancono.math.exceptions.ExceptionUtil;
import cn.ancono.math.exceptions.UnsupportedCalculationException;
import cn.ancono.math.numberTheory.NTCalculator;
import cn.ancono.math.numberTheory.Primes;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

/**
 * Provides some utility methods for {@link MathCalculator}
 *
 * @author liyicheng
 * 2017-09-22 20:35
 */
public final class Calculators {
//    private static final Logger log = Logger.getLogger(Calculators.class.getSimpleName());

    /**
     *
     */
    private Calculators() {
    }

    /**
     * Determines whether the two numbers are the identity in sign, which means they are both positive, negative or
     * zero.
     *
     * @param x
     * @param y
     * @param mc
     * @return
     */
    public static <T> boolean isSameSign(@NotNull T x, @NotNull T y, MathCalculator<T> mc) {
        T z = mc.getZero();
        //noinspection SuspiciousNameCombination
        return mc.compare(x, z) == mc.compare(y, z);
    }

    /**
     * Returns the sign number of {@code x}.
     *
     * @param x
     * @param mc
     * @return
     */
    public static <T> int signum(@NotNull T x, MathCalculator<T> mc) {
        return mc.compare(x, mc.getZero());
    }

    public static <T> boolean isPositive(@NotNull T x, @NotNull MathCalculator<T> mc) {
        return signum(x, mc) > 0;
    }

    public static <T> boolean isNegative(@NotNull T x, @NotNull MathCalculator<T> mc) {
        return signum(x, mc) < 0;
    }

    /**
     * Determines whether a<x<b or b<x<a.
     *
     * @param x
     * @param a
     * @param b
     * @param mc
     * @param <T>
     * @return
     */
    public static <T> boolean between(@NotNull T x, @NotNull T a, @NotNull T b, MathCalculator<T> mc) {
        return mc.compare(a, x) * mc.compare(x, b) > 0;
    }

    public static <T> T square(@NotNull T x, MathCalculator<T> mc) {
        return mc.multiply(x, x);
    }

    public static <T> T cube(@NotNull T x, MathCalculator<T> mc) {
        return mc.multiply(x, mc.multiply(x, x));
    }

    public static <T> T doubleOf(@NotNull T x, MathCalculator<T> mc) {
        return mc.multiplyLong(x, 2L);
    }

    public static <T> T half(@NotNull T x, MathCalculator<T> mc) {
        return mc.divideLong(x, 2L);
    }

    public static <T> T plus1(@NotNull T x, MathCalculator<T> mc) {
        return mc.add(x, mc.getOne());
    }

    public static <T> T minus1(@NotNull T x, MathCalculator<T> mc) {
        return mc.add(x, mc.getOne());
    }

    public static <T> T pi(MathCalculator<T> mc) {
        return mc.constantValue(MathCalculator.STR_PI);
    }

    public static <T> T hypot(@NotNull T a, @NotNull T b, MathCalculator<T> mc) {
        return mc.squareRoot(mc.add(square(a, mc), square(b, mc)));
    }


    private static void throwFor() throws UnsupportedCalculationException {
        throw new UnsupportedCalculationException("Adapter");
    }

    private static void throwFor(String s) throws UnsupportedCalculationException {
        throw new UnsupportedCalculationException(s);
    }

    static class IntegerCalculatorExact extends IntegerCalculator {
        private static final IntegerCalculatorExact cal = new IntegerCalculatorExact();

        IntegerCalculatorExact() {
        }


        @NotNull
        @Override
        public Integer add(@NotNull Integer x, @NotNull Integer y) {
            return Math.addExact(x, y);
        }

        @NotNull
        @Override
        public Integer subtract(@NotNull Integer x, @NotNull Integer y) {
            return Math.subtractExact(x, y);
        }

        @NotNull
        @Override
        public Integer multiply(@NotNull Integer x, @NotNull Integer y) {
            return Math.multiplyExact(x, y);
        }

        @NotNull
        @Override
        public Integer divide(@NotNull Integer x, @NotNull Integer y) {
            return x / y;
        }

        @NotNull
        @Override
        public Integer multiplyLong(@NotNull Integer p, long l) {
            return Math.toIntExact(p * l);
        }

        @NotNull
        @Override
        public Integer divideLong(@NotNull Integer p, long n) {
            return (int) (p / n);
        }


        @NotNull
        @Override
        public Integer reciprocal(@NotNull Integer p) {
            throwFor("Integer value");
            return null;
        }

        @NotNull
        @Override
        public Integer squareRoot(@NotNull Integer x) {
            return (int) Math.sqrt(x);
        }

        @NotNull
        @Override
        public Integer pow(@NotNull Integer p, long exp) {
            int n = p;
            //range check
            if (n == 0 || n == 1) {
                return p;
            }
            if (n == -1) {
                return exp % 2 == 0 ? Integer.valueOf(1) : Integer.valueOf(-1);
            }
            if (exp == 0) {
                return Integer.valueOf(1);
            }
            if (exp >= Integer.SIZE || exp < 0) {
                //impossible exponent
                throw new ArithmeticException("For exp:" + exp);
            }
            int ex = (int) exp;
            int re = n;
            for (int i = 1; i < ex; i++) {
                re = Math.multiplyExact(re, n);
            }
            return re;
        }

        @NotNull
        @Override
        public Integer constantValue(@NotNull String name) {
            throwFor("No constant value avaliable");
            return null;
        }

        @NotNull
        @Override
        public Integer exp(@NotNull Integer a, @NotNull Integer b) {
            int d = a, z = b;

            if (z < 0) {
                if (d == 1) {
                    return 1;
                } else if (d == -1) {
                    return (z & 1) == 0 ? 1 : -1;
                }
                throwFor("Negative Exp");
            } else if (z == 0) {
                if (d == 0) {
                    throw new ArithmeticException("0^0");
                }
                return 1;
            }
            // log(z)
            int re = 1;
            while (z != 0) {
                if ((z & 1) != 0) {
                    re = Math.multiplyExact(re, d);
                }
                d = Math.multiplyExact(d, d);
                z >>= 1;
            }
            return Integer.valueOf(re);
        }

        /**
         *
         */
        @Override
        public Integer decrease(Integer x) {
            return Math.decrementExact(x);
        }

        /**
         *
         */
        @Override
        public Integer increase(Integer x) {
            return Math.incrementExact(x);
        }

        /**
         * @see NTCalculator#powerAndMod(java.lang.Object, java.lang.Object, java.lang.Object)
         */
        @Override
        public Integer powerAndMod(Integer at, Integer nt, Integer mt) {
            int a = at, n = nt, mod = mt;
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
            ans = Math.toIntExact(LongCalculatorExact.getAns(n, mod, ans, Math.multiplyExact(a, ans), Math.multiplyExact(a, a), a));
            return ans;
        }


        /* (non-Javadoc)
         * @see cn.ancono.utilities.math.MathCalculator#getNumberClass()
         */
        @NotNull
        @Override
        public Class<Integer> getNumberClass() {
            return Integer.class;
        }
    }

    /**
     * An implements for integer, which also implements {@link NTCalculator}.
     *
     * @author liyicheng
     * 2017-09-10 12:10
     */
    public static class IntegerCalculator extends MathCalculatorAdapter<Integer> implements NTCalculator<Integer> {
        private static final IntegerCalculator cal = new IntegerCalculator();

        IntegerCalculator() {
        }

        ;

        @Override
        public boolean isEqual(@NotNull Integer x, @NotNull Integer y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull Integer x, @NotNull Integer y) {
            return x.compareTo(y);
        }

        @Override
        public boolean isComparable() {
            return true;
        }

        @NotNull
        @Override
        public Integer add(@NotNull Integer x, @NotNull Integer y) {
            return x + y;
        }

        @NotNull
        @Override
        public Integer negate(@NotNull Integer para) {
            return -para;
        }

        @NotNull
        @Override
        public Integer abs(@NotNull Integer para) {
            return Math.abs(para);
        }

        @NotNull
        @Override
        public Integer subtract(@NotNull Integer x, @NotNull Integer y) {
            return x - y;
        }

        @NotNull
        @Override
        public Integer multiply(@NotNull Integer x, @NotNull Integer y) {
            return x * y;
        }

        @NotNull
        @Override
        public Integer divide(@NotNull Integer x, @NotNull Integer y) {
            return x / y;
        }

        @NotNull
        @Override
        public Integer multiplyLong(@NotNull Integer p, long l) {
            return (int) (p * l);
        }

        @NotNull
        @Override
        public Integer divideLong(@NotNull Integer p, long n) {
            return (int) (p / n);
        }

        @NotNull
        @Override
        public Integer getZero() {
            return 0;
        }

        @Override
        public boolean isZero(@NotNull Integer para) {
            return para.intValue() == 0;
        }

        @NotNull
        @Override
        public Integer getOne() {
            return 1;
        }

        @NotNull
        @Override
        public Integer reciprocal(@NotNull Integer p) {
            throwFor("Integer value");
            return null;
        }

        @NotNull
        @Override
        public Integer squareRoot(@NotNull Integer x) {
            return (int) Math.sqrt(x);
        }

        @NotNull
        @Override
        public Integer pow(@NotNull Integer p, long exp) {
            int n = p;
            if (exp == 0) {
                return 1;
            }
            //range check
            if (n == 0 || n == 1) {
                return p;
            }
            if (n == -1) {
                return exp % 2 == 0 ? Integer.valueOf(1) : Integer.valueOf(-1);
            }
            if (exp < 0) {
                //impossible exponent
                throwFor("exp = " + exp + " < 0");
            }
            int re = n;
            for (long i = 1; i < exp; i++) {
                re = re * n;
            }
            return re;
        }

        @NotNull
        @Override
        public Integer constantValue(@NotNull String name) {
            throwFor("No constant value avaliable");
            return null;
        }

        @NotNull
        @Override
        public Integer exp(@NotNull Integer a, @NotNull Integer b) {
            int d = a, z = b;

            if (z < 0) {
                if (d == 1) {
                    return 1;
                } else if (d == -1) {
                    return (z & 1) == 0 ? 1 : -1;
                }
                throwFor("Negative Exp");
            } else if (z == 0) {
                if (d == 0) {
                    throw new ArithmeticException("0^0");
                }
                return 1;
            }
            // log(z)
            int re = 1;
            while (z != 0) {
                if ((z & 1) != 0) {
                    re *= d;
                }
                d *= d;
                z >>= 1;
            }
            return re;
        }

        /**
         * @see NTCalculator#divideToInteger(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Integer divideToInteger(Integer a, Integer b) {
            return a / b;
        }

        /**
         * @see NTCalculator#mod(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Integer mod(Integer a, Integer b) {
            int x = a, y = b;
            return Math.abs(x) % Math.abs(y);
        }

        /**
         * @see NTCalculator#isInteger(java.lang.Object)
         */
        @Override
        public boolean isInteger(Integer x) {
            return true;
        }

        /**
         * @see NTCalculator#isQuotient(java.lang.Object)
         */
        @Override
        public boolean isQuotient(Integer x) {
            return true;
        }

        /**
         * @see NTCalculator#gcd(java.lang.Object, java.lang.Object)
         */
        @Override
        public Integer gcd(Integer a, Integer b) {
            return MathUtils.gcd(a, b);
        }

        @Override
        public Triple<Integer, Integer, Integer> gcdUV(Integer a, Integer b) {
            int[] arr = MathUtils.gcdUV(a, b);
            return new Triple<>(arr[0], arr[1], arr[2]);
        }

        @Override
        public Integer lcm(Integer a, Integer b) {
            return MathUtils.lcm(a, b);
        }

        /**
         * @see NTCalculator#decrease(java.lang.Object)
         */
        @Override
        public Integer decrease(Integer x) {
            return x - 1;
        }

        /**
         * @see NTCalculator#increase(java.lang.Object)
         */
        @Override
        public Integer increase(Integer x) {
            return x + 1;
        }

        /**
         * @see NTCalculator#isEven(java.lang.Object)
         */
        @Override
        public boolean isEven(Integer x) {
            return (x & 1) == 0;
        }

        /**
         * @see NTCalculator#isOdd(java.lang.Object)
         */
        @Override
        public boolean isOdd(Integer x) {
            return (x & 1) != 0;
        }

        /**
         * @see NTCalculator#isPositive(java.lang.Object)
         */
        @Override
        public boolean isPositive(Integer x) {
            return x > 0;
        }

        /**
         * @see NTCalculator#reminder(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Integer reminder(Integer a, Integer b) {
            return a % b;
        }

        /**
         * @see NTCalculator#deg(java.lang.Object, java.lang.Object)
         */
        @Override
        public Integer deg(Integer a, Integer b) {
            return MathUtils.deg(a, b);
        }

        /**
         * @see NTCalculator#isExactDivide(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isExactDivide(Integer a, Integer b) {
            return a % b == 0;
        }

        /**
         * @see NTCalculator#powerAndMod(java.lang.Object, java.lang.Object, java.lang.Object)
         */
        @Override
        public Integer powerAndMod(Integer a, Integer n, Integer m) {
            return MathUtils.powerAndMod(a, n, m);
        }

        @Override
        public Integer powerAndMod(Integer a, long n, Integer m) {
            return MathUtils.powerAndMod(a, n, m);
        }

        @Override
        public BigInteger asBigInteger(Integer x) {
            return BigInteger.valueOf(x);
        }

        @Override
        public long asLong(Integer x) {
            return x;
        }

        @NotNull
        @Override
        public Class<Integer> getNumberClass() {
            return Integer.class;
        }


    }

    static class LongCalculatorExact extends LongCalculator {
        private static final LongCalculatorExact cal = new LongCalculatorExact();

        LongCalculatorExact() {
        }

        ;

        @Override
        public boolean isEqual(@NotNull Long x, @NotNull Long y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull Long x, @NotNull Long y) {
            return x.compareTo(y);
        }

        @NotNull
        @Override
        public Long add(@NotNull Long x, @NotNull Long y) {
            return Math.addExact(x, y);
        }

        @NotNull
        @Override
        public Long negate(@NotNull Long para) {
            return -para;
        }

        @NotNull
        @Override
        public Long abs(@NotNull Long para) {
            return Math.abs(para);
        }

        @NotNull
        @Override
        public Long subtract(@NotNull Long x, @NotNull Long y) {
            return Math.subtractExact(x, y);
        }

        @NotNull
        @Override
        public Long multiply(@NotNull Long x, @NotNull Long y) {
            return Math.multiplyExact(x, y);
        }

        @NotNull
        @Override
        public Long divide(@NotNull Long x, @NotNull Long y) {
            if (x % y != 0) {
                throw new ArithmeticException("Cannot divide exactly: " + x + "/" + y);
            }
            return x / y;
        }

        @NotNull
        @Override
        public Long multiplyLong(@NotNull Long p, long l) {
            return Math.multiplyExact(p, l);
        }

        @NotNull
        @Override
        public Long divideLong(@NotNull Long p, long n) {
            return p / n;
        }

        private static final Long ZERO = 0L,
                ONE = 1L;

        @NotNull
        @Override
        public Long getZero() {
            return ZERO;
        }

        @Override
        public boolean isZero(@NotNull Long para) {
            return ZERO.equals(para);
        }

        @NotNull
        @Override
        public Long getOne() {
            return ONE;
        }

        @NotNull
        @Override
        public Long reciprocal(@NotNull Long p) {
            throwFor();
            return null;
        }

        @NotNull
        @Override
        public Long squareRoot(@NotNull Long x) {
            return (long) Math.sqrt(x);
        }

        @NotNull
        @Override
        public Long pow(@NotNull Long p, long exp) {
            return exp0(p, exp);
        }

        @NotNull
        @Override
        public Long constantValue(@NotNull String name) {
            throwFor("No constant value avaliable");
            return null;
        }

        @NotNull
        public Long exp0(long a, long b) {
            long d = a, z = b;

            if (z < 0L) {
                if (d == 1L) {
                    return 1L;
                } else if (d == -1L) {
                    return (z & 1L) == 0L ? 1L : -1L;
                }
                throwFor("Negative Exp");
            } else if (z == 0L) {
                if (d == 0L) {
                    throw new ArithmeticException("0^0");
                }
                return 1L;
            }
            // log(z)
            long re = 1L;
            while (z != 0L) {
                if ((z & 1L) != 0L) {
                    re = Math.multiplyExact(re, d);
                }
                d = Math.multiplyExact(d, d);
                z >>= 1L;
            }
            return Long.valueOf(re);
        }

        @NotNull
        @Override
        public Long exp(@NotNull Long a, @NotNull Long b) {
            return exp0(a, b);
        }

        /**
         *
         */
        @Override
        public Long decrease(Long x) {
            return Math.decrementExact(x);
        }

        /**
         *
         */
        @Override
        public Long increase(Long x) {
            return Math.incrementExact(x);
        }

        /**
         *
         */
        @Override
        public Long powerAndMod(Long at, Long nt, Long mt) {
            long a = at, n = nt, mod = mt;
            if (a < 0) {
                throw new IllegalArgumentException("a<0");
            }
            if (mod == 1) {
                return 0L;
            }
            if (a == 0 || a == 1) {
                return a;
            }
            long ans = 1;
            a = a % mod;
            ans = getAns(n, mod, ans, Math.multiplyExact(a, ans), Math.multiplyExact(a, a), a);
            return ans;
        }

        private static long getAns(long n, long mod, long ans, long l, long l2, long a) {
            while (n > 0) {
                if ((n & 1) == 1) {
                    ans = l % mod;

                }
                a = l2 % mod;
                n >>= 1;
            }
            return ans;
        }

        @NotNull
        @Override
        public Class<Long> getNumberClass() {
            return Long.class;
        }
    }

    /**
     * An implements for long, which also implements {@link NTCalculator}.
     *
     * @author liyicheng
     * 2017-09-10 12:10
     */
    public static class LongCalculator extends MathCalculatorAdapter<Long> implements NTCalculator<Long> {
        private static final LongCalculator cal = new LongCalculator();

        LongCalculator() {
        }

        @Override
        public boolean isEqual(@NotNull Long x, @NotNull Long y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull Long x, @NotNull Long y) {
            return x.compareTo(y);
        }

        @Override
        public boolean isComparable() {
            return true;
        }

        @NotNull
        @Override
        public Long add(@NotNull Long x, @NotNull Long y) {
            return x + y;
        }

        @NotNull
        @Override
        public Long negate(@NotNull Long para) {
            return -para;
        }

        @NotNull
        @Override
        public Long abs(@NotNull Long para) {
            return Math.abs(para);
        }

        @NotNull
        @Override
        public Long subtract(@NotNull Long x, @NotNull Long y) {
            return x - y;
        }

        @NotNull
        @Override
        public Long multiply(@NotNull Long x, @NotNull Long y) {
            return x * y;
        }

        @NotNull
        @Override
        public Long divide(@NotNull Long x, @NotNull Long y) {
            return divideLong(x, y);
        }

        @NotNull
        @Override
        public Long multiplyLong(@NotNull Long p, long l) {
            return p * l;
        }

        @NotNull
        @Override
        public Long divideLong(@NotNull Long p, long n) {
            return p / n;
        }

        private static final Long ZERO = Long.valueOf(0),
                ONE = Long.valueOf(1);

        @NotNull
        @Override
        public Long getZero() {
            return ZERO;
        }

        @Override
        public boolean isZero(@NotNull Long para) {
            return ZERO.equals(para);
        }

        @NotNull
        @Override
        public Long getOne() {
            return ONE;
        }

        @NotNull
        @Override
        public Long reciprocal(@NotNull Long p) {
            throwFor();
            return null;
        }

        @NotNull
        @Override
        public Long squareRoot(@NotNull Long x) {
            return (long) Math.sqrt(x);
        }

        @NotNull
        @Override
        public Long pow(@NotNull Long p, long exp) {
            return exp0(p, exp);
        }

        @NotNull
        @Override
        public Long constantValue(@NotNull String name) {
            throwFor("No constant value avaliable");
            return null;
        }

        @NotNull
        @Override
        public Long exp(@NotNull Long a, @NotNull Long b) {
            return exp0(a, b);
        }

        public Long exp0(long a, long b) {
            long d = a, z = b;

            if (z < 0L) {
                if (d == 1L) {
                    return 1L;
                } else if (d == -1L) {
                    return (z & 1L) == 0L ? 1L : -1L;
                }
                throwFor("Negative Exp");
            } else if (z == 0L) {
                if (d == 0L) {
                    throw new ArithmeticException("0^0");
                }
                return 1L;
            }
            // log(z)
            long re = 1;
            while (z != 0) {
                if ((z & 1) != 0) {
                    re *= d;
                }
                d *= d;
                z >>= 1;
            }
            return re;
        }

        /**
         *
         */
        @NotNull
        @Override
        public Long divideToInteger(Long a, Long b) {
            return a / b;
        }

        /**
         *
         */
        @NotNull
        @Override
        public Long mod(Long a, Long b) {
            long x = a, y = b;
            return Math.abs(x) % Math.abs(y);
        }

        /**
         *
         */
        @Override
        public boolean isInteger(Long x) {
            return true;
        }

        /**
         * @see NTCalculator#isQuotient(java.lang.Object)
         */
        @Override
        public boolean isQuotient(Long x) {
            return true;
        }

        /**
         * @see NTCalculator#gcd(java.lang.Object, java.lang.Object)
         */
        @Override
        public Long gcd(Long a, Long b) {
            return MathUtils.gcd(a, b);
        }

        @Override
        public Triple<Long, Long, Long> gcdUV(Long a, Long b) {
            long[] arr = MathUtils.gcdUV(a, b);
            return new Triple<>(arr[0], arr[1], arr[2]);
        }

        @Override
        public Long lcm(Long a, Long b) {
            return MathUtils.lcm(a, b);
        }

        /**
         * @see NTCalculator#decrease(java.lang.Object)
         */
        @Override
        public Long decrease(Long x) {
            return x - 1;
        }

        /**
         * @see NTCalculator#increase(java.lang.Object)
         */
        @Override
        public Long increase(Long x) {
            return x + 1;
        }

        /**
         * @see NTCalculator#isEven(java.lang.Object)
         */
        @Override
        public boolean isEven(Long x) {
            return (x & 1) == 0;
        }

        /**
         * @see NTCalculator#isOdd(java.lang.Object)
         */
        @Override
        public boolean isOdd(Long x) {
            return (x & 1) != 0;
        }

        /**
         * @see NTCalculator#isPositive(java.lang.Object)
         */
        @Override
        public boolean isPositive(Long x) {
            return x > 0;
        }

        /**
         * @see NTCalculator#reminder(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Long reminder(Long a, Long b) {
            return a % b;
        }

        /**
         * @see NTCalculator#deg(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Long deg(Long a, Long b) {
            return (long) MathUtils.deg(a, b);
        }

        /**
         * @see NTCalculator#isExactDivide(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isExactDivide(Long a, Long b) {
            return a % b == 0;
        }

        @Override
        public BigInteger asBigInteger(Long x) {
            return BigInteger.valueOf(x);
        }

        @Override
        public long asLong(Long x) {
            return x;
        }


        @NotNull
        @Override
        public Class<Long> getNumberClass() {
            return Long.class;
        }
    }

    /**
     * An implements for BigInteger, which also implements {@link NTCalculator}.
     *
     * @author liyicheng
     * 2017-09-10 12:10
     */
    public static class BigIntegerCalculator extends MathCalculatorAdapter<BigInteger> implements NTCalculator<BigInteger> {

        static final BigIntegerCalculator cal = new BigIntegerCalculator();

        private BigIntegerCalculator() {
        }

        @SuppressWarnings("SuspiciousNameCombination")
        @Override
        public boolean isEqual(@NotNull BigInteger x, @NotNull BigInteger y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull BigInteger x, @NotNull BigInteger y) {
            return x.compareTo(y);
        }

        @Override
        public boolean isComparable() {
            return true;
        }

        @NotNull
        @Override
        public BigInteger add(@NotNull BigInteger x, @NotNull BigInteger y) {
            return x.add(y);
        }

        @NotNull
        @Override
        public BigInteger negate(@NotNull BigInteger para) {
            return para.negate();
        }

        @NotNull
        @Override
        public BigInteger abs(@NotNull BigInteger para) {
            return para.abs();
        }

        @NotNull
        @Override
        public BigInteger subtract(@NotNull BigInteger x, @NotNull BigInteger y) {
            return x.subtract(y);
        }


        @NotNull
        @Override
        public BigInteger getZero() {
            return BigInteger.ZERO;
        }

        @Override
        public boolean isZero(@NotNull BigInteger para) {
            return BigInteger.ZERO.equals(para);
        }

        @NotNull
        @Override
        public BigInteger multiply(@NotNull BigInteger x, @NotNull BigInteger y) {
            return x.multiply(y);
        }

        @NotNull
        @Override
        public BigInteger divide(@NotNull BigInteger x, @NotNull BigInteger y) {
            return x.divide(y);
        }

        @NotNull
        @Override
        public BigInteger getOne() {
            return BigInteger.ONE;
        }

        @NotNull
        @Override
        public BigInteger reciprocal(@NotNull BigInteger p) {
            //impossible
            throwFor();
            return null;
        }

        @NotNull
        @Override
        public BigInteger multiplyLong(@NotNull BigInteger p, long l) {
            return p.multiply(BigInteger.valueOf(l));
        }

        @NotNull
        @Override
        public BigInteger divideLong(@NotNull BigInteger p, long n) {
            return p.divide(BigInteger.valueOf(n));
        }

        @NotNull
        @Override
        public BigInteger squareRoot(@NotNull BigInteger x) {
            return BigInteger.valueOf((long) Math.sqrt(x.doubleValue()));
        }

        @NotNull
        @Override
        public BigInteger pow(@NotNull BigInteger p, long exp) {
            if (exp > Integer.MAX_VALUE) {
                throwFor("Too big.");
            }
            return p.pow((int) exp);
        }

        @NotNull
        @Override
        public BigInteger constantValue(@NotNull String name) {
            throwFor();
            return null;
        }

        @NotNull
        @Override
        public BigInteger exp(@NotNull BigInteger a, @NotNull BigInteger b) {
            try {
                int t = b.intValueExact();
                return a.pow(t);
            } catch (ArithmeticException ae) {
                throw new UnsupportedCalculationException("Exp too big:" + b.toString());
            }
        }

        /**
         *
         */
        @Override
        public BigInteger divideToInteger(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.divide(b);
        }

        /**
         * @see NTCalculator#mod(java.lang.Object, java.lang.Object)
         */
        @Override
        public BigInteger mod(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.mod(b.abs());
        }

        /**
         *
         */
        @Override
        public boolean isInteger(BigInteger x) {
            return true;
        }

        /**
         * @see NTCalculator#isQuotient(java.lang.Object)
         */
        @Override
        public boolean isQuotient(BigInteger x) {
            return true;
        }

        /**
         * @see NTCalculator#gcd(java.lang.Object, java.lang.Object)
         */
        @Override
        public BigInteger gcd(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.gcd(b);
        }

        /**
         * @see NTCalculator#decrease(java.lang.Object)
         */
        @Override
        public BigInteger decrease(@NotNull BigInteger x) {
            return x.subtract(BigInteger.ONE);
        }

        /**
         * @see NTCalculator#increase(java.lang.Object)
         */
        @Override
        public BigInteger increase(@NotNull BigInteger x) {
            return x.add(BigInteger.ONE);
        }

        /**
         * @see NTCalculator#isEven(java.lang.Object)
         */
        @Override
        public boolean isEven(@NotNull BigInteger x) {
            return !x.testBit(0);
        }

        /**
         * @see NTCalculator#isOdd(java.lang.Object)
         */
        @Override
        public boolean isOdd(@NotNull BigInteger x) {
            return x.testBit(0);
        }

        /**
         * @see NTCalculator#isPositive(java.lang.Object)
         */
        @Override
        public boolean isPositive(@NotNull BigInteger x) {
            return x.signum() > 0;
        }

        /**
         * @see NTCalculator#reminder(java.lang.Object, java.lang.Object)
         */
        @Override
        public BigInteger reminder(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.remainder(b);
        }

        /**
         * @see NTCalculator#isExactDivide(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isExactDivide(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.mod(b).equals(BigInteger.ZERO);
        }

        /**
         * @see NTCalculator#powerAndMod(java.lang.Object, java.lang.Object, java.lang.Object)
         */
        @Override
        public BigInteger powerAndMod(@NotNull BigInteger a, @NotNull BigInteger n, @NotNull BigInteger mod) {
            return a.modPow(n, mod);
        }

        @Override
        public BigInteger asBigInteger(BigInteger x) {
            return x;
        }

        @NotNull
        @Override
        public Class<BigInteger> getNumberClass() {
            return BigInteger.class;
        }
    }

    static class BigDecimalCalculator extends MathCalculatorAdapter<BigDecimal> {

        private MathContext mc;

        public static final BigDecimal PI_VALUE =
                new BigDecimal("3.1415926535897932384626433832795028", MathContext.DECIMAL128);
        public static final BigDecimal E_VALUE =
                new BigDecimal("2.7182818284590452353602874713526625", MathContext.DECIMAL128);


        public BigDecimalCalculator(MathContext mc) {
            this.mc = mc;
        }

        @Override
        public boolean isEqual(@NotNull BigDecimal x, @NotNull BigDecimal y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull BigDecimal x, @NotNull BigDecimal y) {
            return x.compareTo(y);
        }

        @Override
        public boolean isComparable() {
            return true;
        }

        @NotNull
        @Override
        public BigDecimal add(@NotNull BigDecimal x, @NotNull BigDecimal y) {
            return x.add(y);
        }

        @NotNull
        @Override
        public BigDecimal negate(@NotNull BigDecimal para) {
            return para.negate();
        }

        @NotNull
        @Override
        public BigDecimal abs(@NotNull BigDecimal para) {
            return para.abs();
        }

        @NotNull
        @Override
        public BigDecimal subtract(@NotNull BigDecimal x, @NotNull BigDecimal y) {
            return x.subtract(y);
        }

        @NotNull
        @Override
        public BigDecimal getZero() {
            return BigDecimal.ZERO;
        }

        @Override
        public boolean isZero(@NotNull BigDecimal para) {
            return BigDecimal.ZERO.equals(para);
        }

        @NotNull
        @Override
        public BigDecimal multiply(@NotNull BigDecimal x, @NotNull BigDecimal y) {
            return x.multiply(y);
        }

        @NotNull
        @Override
        public BigDecimal divide(@NotNull BigDecimal x, @NotNull BigDecimal y) {
            return x.divide(y, mc);
        }

        @NotNull
        @Override
        public BigDecimal getOne() {
            return BigDecimal.ONE;
        }

        @NotNull
        @Override
        public BigDecimal reciprocal(@NotNull BigDecimal p) {
            return BigDecimal.ONE.divide(p, mc);
        }

        @NotNull
        @Override
        public BigDecimal multiplyLong(@NotNull BigDecimal p, long l) {
            return p.multiply(BigDecimal.valueOf(l));
        }

        @NotNull
        @Override
        public BigDecimal divideLong(@NotNull BigDecimal p, long n) {
            return p.divide(BigDecimal.valueOf(n));
        }

        @NotNull
        @Override
        public BigDecimal squareRoot(@NotNull BigDecimal x) {
            return BigDecimal.valueOf(Math.sqrt(x.doubleValue()));
        }

        @NotNull
        @Override
        public BigDecimal pow(@NotNull BigDecimal p, long exp) {
            if (exp >= Integer.MAX_VALUE) {
                throwFor("Too big.");
            }
            return p.pow((int) exp);
        }

        @NotNull
        @Override
        public BigDecimal constantValue(@NotNull String name) {
            if (name.equalsIgnoreCase(STR_PI)) {
                return PI_VALUE;
            }
            if (name.equalsIgnoreCase(STR_E)) {
                return E_VALUE;
            }
            throwFor("No constant value avaliable");
            return null;
        }

        /**
         * This method only provides accuracy of double and throws exception if the number is too big.
         *
         * @param a
         * @param b
         * @return
         */
        @NotNull
        @Override
        public BigDecimal exp(@NotNull BigDecimal a, @NotNull BigDecimal b) {
            //use exp method in double instead.
            double ad = a.doubleValue();
            double ab = b.doubleValue();
            if (ad == Double.NEGATIVE_INFINITY
                    || ad == Double.POSITIVE_INFINITY ||
                    ab == Double.NEGATIVE_INFINITY
                    || ab == Double.POSITIVE_INFINITY) {
                throw new UnsupportedCalculationException("Too big.");
            }
            return BigDecimal.valueOf(Math.pow(ad, ab));
        }

        @NotNull
        @Override
        public Class<BigDecimal> getNumberClass() {
            return BigDecimal.class;
        }
    }

    static class DoubleCalculator extends MathCalculatorAdapter<Double> {

        private DoubleCalculator() {
        }

        static final DoubleCalculator dc = new DoubleCalculator();

        @Override
        public boolean isEqual(@NotNull Double x, @NotNull Double y) {
            return x.equals(y);
        }

        @Override
        public int compare(@NotNull Double x, @NotNull Double y) {
            return x.compareTo(y);
        }

        @Override
        public boolean isComparable() {
            return true;
        }

        @NotNull
        @Override
        public Double add(@NotNull Double x, @NotNull Double y) {
            return x + y;
        }

        @NotNull
        @Override
        public Double negate(@NotNull Double para) {
            return -para;
        }

        @NotNull
        @Override
        public Double abs(@NotNull Double para) {
            return Math.abs(para);
        }

        @NotNull
        @Override
        public Double subtract(@NotNull Double x, @NotNull Double y) {
            return x - y;
        }

        private static final Double ZERO = 0.0d;
        private static final Double ONE = 1.0d;

        @NotNull
        @Override
        public Double getZero() {
            return ZERO;
        }

        @NotNull
        @Override
        public Double multiply(@NotNull Double x, @NotNull Double y) {
            return x * y;
        }

        @NotNull
        @Override
        public Double divide(@NotNull Double x, @NotNull Double y) {
            return x / y;
        }

        @NotNull
        @Override
        public Double getOne() {
            return ONE;
        }

        @NotNull
        @Override
        public Double reciprocal(@NotNull Double p) {
            return 1 / p;
        }

        @NotNull
        @Override
        public Double multiplyLong(@NotNull Double p, long l) {
            return p * l;
        }

        @NotNull
        @Override
        public Double divideLong(@NotNull Double p, long n) {
            return p / n;
        }

        @NotNull
        @Override
        public Double squareRoot(@NotNull Double x) {
            return Math.sqrt(x);
        }

        @NotNull
        @Override
        public Double pow(@NotNull Double p, long exp) {
            return Math.pow(p, exp);
        }

        @NotNull
        @Override
        public Double addX(@NotNull Object... ps) {
            double sum = 0;
            for (Object d : ps) {
                sum += (Double) d;
            }
            return sum;
        }

        @NotNull
        @Override
        public Double multiplyX(@NotNull Object... ps) {
            double sum = 1;
            for (Object d : ps) {
                sum *= (Double) d;
            }
            return sum;
        }

        private static final Double pi = Math.PI;
        private static final Double e = Math.E;

        @NotNull
        @Override
        public Double constantValue(@NotNull String name) {
            if (name.equalsIgnoreCase(STR_PI)) {
                return pi;
            }
            if (name.equalsIgnoreCase(STR_E)) {
                return e;
            }
            throwFor("No constant value avaliable");
            return null;
        }

        @NotNull
        @Override
        public Double exp(@NotNull Double a, @NotNull Double b) {
            return Math.pow(a, b);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculatorAdapter#log(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Double log(@NotNull Double a, @NotNull Double b) {
            return Math.log(b) / Math.log(a);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculatorAdapter#sin(java.lang.Object)
         */
        @NotNull
        @Override
        public Double sin(@NotNull Double x) {
            return Math.sin(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculator#cos(java.lang.Object)
         */
        @NotNull
        @Override
        public Double cos(@NotNull Double x) {
            return Math.cos(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculatorAdapter#arcsin(java.lang.Object)
         */
        @NotNull
        @Override
        public Double arcsin(@NotNull Double x) {
            return Math.asin(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculator#arccos(java.lang.Object)
         */
        @NotNull
        @Override
        public Double arccos(@NotNull Double x) {
            return Math.acos(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculatorAdapter#ln(java.lang.Object)
         */
        @NotNull
        @Override
        public Double ln(@NotNull Double x) {
            return Math.log(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculatorAdapter#exp(java.lang.Object)
         */
        @NotNull
        @Override
        public Double exp(@NotNull Double x) {
            return Math.exp(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculator#tan(java.lang.Object)
         */
        @NotNull
        @Override
        public Double tan(@NotNull Double x) {
            return Math.tan(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculator#arctan(java.lang.Object)
         */
        @NotNull
        @Override
        public Double arctan(@NotNull Double x) {
            return Math.atan(x);
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.number_models.MathCalculatorAdapter#isZero(java.lang.Object)
         */
        @Override
        public boolean isZero(@NotNull Double para) {
            return ZERO.equals(para);
        }

        /**
         * @see cn.ancono.math.numberModels.MathCalculatorAdapter#nroot(java.lang.Object, long)
         */
        @NotNull
        @Override
        public Double nroot(@NotNull Double x, long n) {
            return Math.pow(x, 1d / n);
        }

        @NotNull
        @Override
        public Class<Double> getNumberClass() {
            return Double.class;
        }
    }

    static class DoubleCalculatorWithDeviation extends DoubleCalculator {
        private final double dev;

        public DoubleCalculatorWithDeviation(double dev) {
            this.dev = dev;
        }

        /* (non-Javadoc)
         * @see cn.ancono.utilities.math.MathCalculatorAdapter.DoubleCalculator#isEqual(java.lang.Double, java.lang.Double)
         */
        @Override
        public boolean isEqual(@NotNull Double x, @NotNull Double y) {
            double d = Math.abs(x - y);
            if (d < dev) {
                return true;
            }
            double p1 = Math.abs(x);
            double p2 = Math.abs(y);
            return Math.max(p1, p2) * dev >= d;
        }

        /* (non-Javadoc)
         * @see cn.ancono.utilities.math.MathCalculator#isZero(java.lang.Object)
         */
        @Override
        public boolean isZero(@NotNull Double para) {
            return Math.abs(para) <= dev;
        }

        /* (non-Javadoc)
         * @see cn.ancono.math.MathCalculatorAdapter.DoubleCalculator#compare(java.lang.Double, java.lang.Double)
         */
        @Override
        public int compare(@NotNull Double x, @NotNull Double y) {
            if (isEqual(x, y)) {
                return 0;
            }
            return super.compare(x, y);
        }

        static final DoubleCalculatorWithDeviation dc = new DoubleCalculatorWithDeviation(10E-10);
    }

    /**
     * Return an exact calculator for Integer,all operations that will cause a overflow will
     * not be operated and an exception will be thrown.<p>
     * <p>The {@link IntegerCalculator#squareRoot(Integer)}
     * The calculator does not have any constant values.
     *
     * @return a MathCalculator
     */
    @NotNull
    public static IntegerCalculator getCalIntegerExact() {
        return IntegerCalculatorExact.cal;
    }

    /**
     * Return a calculator for Integer, all the basic operations are the identity as {@code +,-,*,/}.Notice that this
     * kind of
     * calculator will not check the value or throw any overflow exception.For example, {@code pow(2,100)} is acceptable
     * and
     * the return value will be {@code -818408495}.<p>
     * The calculator does not have any constant values.
     *
     * @return a MathCalculator
     */
    @NotNull
    public static IntegerCalculator getCalInteger() {
        return IntegerCalculator.cal;
    }

    /**
     * Return an exact calculator for Long,all operations that will cause a overflow will
     * not be operated and an exception will be thrown.<p>
     * The calculator does not have any constant values.
     *
     * @return a MathCalculator
     */
    @NotNull
    public static LongCalculator getCalLongExact() {
        return LongCalculatorExact.cal;
    }

    /**
     * Return a calculator for Long, all the basic operations are the identity as {@code +,-,*,/}.<p>
     * The calculator does not have any constant values.
     *
     * @return a MathCalculator
     */
    @NotNull
    public static LongCalculator getCalLong() {
        return LongCalculator.cal;
    }

    /**
     * Return a calculator for {@linkplain BigInteger}.Notice that the  method {@code pow} has a limit
     * that {@code exp <= Integer.MAX_VALUE}.<p>
     * The calculator does not have any constant values.
     *
     * @return a MathCalculator
     */
    @NotNull
    public static BigIntegerCalculator getCalBigInteger() {
        return BigIntegerCalculator.cal;
    }

    /**
     * Return a calculator for {@linkplain BigDecimal} with the given math context.Notice that the method
     * {@code pow} has a limit that {@code exp <= 999999999}<p>
     * The calculator has {@code MathCalculator#STR_PI} and {@code MathCalculator#STR_E} as constant values,which have
     * the rounding mode of
     * {@link MathContext#DECIMAL128}.
     *
     * @param mc a math context
     * @return a MathCalculator
     */
    public static MathCalculator<BigDecimal> getCalBigDecimal(MathContext mc) {
        return new BigDecimalCalculator(mc);
    }

    /**
     * Return a calculator for Double.
     * <p>The calculator has {@code MathCalculator#STR_PI} and {@code MathCalculator#STR_E} as constant values,
     * which are the double values in Math.<p>
     * This calculator doesn't consider the deviation of double and it
     * {@link MathCalculator#isEqual(Object, Object)} method is just equal to {@code d1 == d2}.
     *
     * @return a MathCalculator
     */
    @NotNull
    public static MathCalculator<Double> getCalDouble() {
        return DoubleCalculator.dc;
    }

    /**
     * Return a calculator for Double.
     * <p>The calculator has {@code MathCalculator#STR_PI} and {@code MathCalculator#STR_E} as constant values,
     * which are the double values in Math.<p>
     * This calculator considers the deviation of double and it
     * allows a deviation of {@code 10E-10}
     *
     * @return a MathCalculator
     */
    @NotNull
    public static MathCalculator<Double> getCalculatorDoubleDev() {
        return DoubleCalculatorWithDeviation.dc;
    }

    /**
     * Return a calculator for Double.
     * <p>The calculator has {@code MathCalculator#STR_PI} and {@code MathCalculator#STR_E} as constant values,
     * which are the double values in Math.<p>
     * This calculator considers the deviation of double.
     *
     * @return a MathCalculator
     */
    public static MathCalculator<Double> getCalculatorDoubleDev(double dev) {
        return new DoubleCalculatorWithDeviation(Math.abs(dev));
    }

    static class IntegerCalModP extends IntegerCalculatorExact {
        private final int p;
        private int[] inversed;

        private int[] initInv() {
            int[] inv = new int[p];
            for (int i = 1; i < p; i++) {
                if (inv[i] != 0) {
                    continue;
                }
                for (int j = 1; j < p; j++) {
                    if (inv[j] != 0) {
                        continue;
                    }
                    if (multiply(i, j) == 1) {
                        inv[i] = j;
                        inv[j] = i;
                        break;
                    }
                }
            }
            return inv;
        }


        IntegerCalModP(int p) {
            this.p = p;
            inversed = initInv();
        }

        private int modP(int x) {
            int re = x % p;
            if (re < 0) {
                re += p;
            }
            return re;
        }

        private int modP(long x) {
            int re = (int) (x % p);
            if (re < 0) {
                re += p;
            }
            return re;
        }

        @NotNull
        @Override
        public Integer add(@NotNull Integer x, @NotNull Integer y) {
            return modP(Math.addExact(x, y));
        }

        @NotNull
        @Override
        public Integer subtract(@NotNull Integer x, @NotNull Integer y) {
            return modP(Math.subtractExact(x, y));
        }

        @NotNull
        @Override
        public Integer multiply(@NotNull Integer x, @NotNull Integer y) {
            return modP(Math.multiplyExact(x, y));
        }

        private int multiply(int x, int y) {
            return modP(Math.multiplyExact(x, y));
        }

        @NotNull
        @Override
        public Integer divide(@NotNull Integer x, @NotNull Integer y) {
            //noinspection SuspiciousNameCombination
            return multiply(x.intValue(), inverseOf(y));
        }

        private int inverseOf(int x) {
            if (x == 0) {
                ExceptionUtil.dividedByZero();
            }
            return inversed[modP(x)];
        }

        @NotNull
        @Override
        public Integer multiplyLong(@NotNull Integer p, long l) {
            return multiply(p.intValue(), modP(l));
        }

        @NotNull
        @Override
        public Integer divideLong(@NotNull Integer p, long n) {
            return multiply(p.intValue(), inverseOf(modP(n)));
        }

        @NotNull
        @Override
        public Integer reciprocal(@NotNull Integer p) {
            return inverseOf(p);
        }

        @NotNull
        @Override
        public Integer squareRoot(@NotNull Integer x) {
            int _x = x;
            for (int n = 0; n < p; n++) {
                if (multiply(n, n) == _x) {
                    return n;
                }
            }
            throw new UnsupportedCalculationException();
        }

        @NotNull
        @Override
        public Integer pow(@NotNull Integer p, long exp) {
            return MathUtils.powerAndMod(p, exp, p);
        }

        @NotNull
        @Override
        public Integer exp(@NotNull Integer a, @NotNull Integer b) {
            return MathUtils.powerAndMod(a, b, p);
        }

        @Override
        public Integer powerAndMod(Integer at, Integer nt, Integer mt) {
            return powerAndMod(at, nt.longValue(), mt);
        }

        @Override
        public Integer powerAndMod(Integer at, long n, Integer m) {
            int a = modP(at);
            int mod = m;
            if (mod == 1) {
                return 0;
            }
            if (a == 0 || a == 1) {
                return a;
            }
            int ans = 1;
            a = a % mod;
            while (n > 0) {
                if ((n & 1) == 1) {
                    ans = multiply(a, ans) % mod;
                }
                a = multiply(a, a) % mod;
                n >>= 1;
            }
            return ans;
        }

        @NotNull
        @Override
        public Integer negate(@NotNull Integer para) {
            return modP(-para);
        }

        @NotNull
        @Override
        public Integer abs(@NotNull Integer para) {
            return modP(para);
        }

        @NotNull
        @Override
        public Integer divideToInteger(Integer a, Integer b) {
            return divide(a, b);
        }
    }

    public static MathCalculator<Integer> getCalculatorIntModP(int p) {
        if (!Primes.getInstance().isPrime(p)) {
            throw new IllegalArgumentException("p must be a prime number!");
        }
        return new IntegerCalModP(p);
    }
//
//    public static RingFraction.RFCalculator<Polynomial<Fraction>> getCalPolyFraction(){
//        return RingFraction.Companion.getCalculator(Polynomial.getCalculator(Fraction.Companion.getCalculator()),Polynomial.)
//    }

}
