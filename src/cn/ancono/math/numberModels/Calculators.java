/**
 * 2017-09-22
 */
package cn.ancono.math.numberModels;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathUtils;
import cn.ancono.math.exceptions.ExceptionUtil;
import cn.ancono.math.exceptions.UnsupportedCalculationException;
import cn.ancono.math.numberTheory.IntCalculator;
import cn.ancono.math.numberTheory.NTUtils;
import cn.ancono.math.numberTheory.Primes;
import cn.ancono.math.numberTheory.ZModPCalculator;
import kotlin.Triple;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.util.List;

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
     */
    public static <T> boolean between(@NotNull T x, @NotNull T a, @NotNull T b, MathCalculator<T> mc) {
        return mc.compare(a, x) * mc.compare(x, b) > 0;
    }

    /**
     * Returns {@code (x-a)(y-a)<=0}
     */
    public static <T> boolean oppositeSign(T x, T y, T a, MathCalculator<T> mc) {
        return mc.compare(x, a) * mc.compare(y, a) <= 0;
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
        public Integer multiplyLong(@NotNull Integer p, long l) {
            return Math.toIntExact(p * l);
        }


        @NotNull
        @Override
        public Integer squareRoot(@NotNull Integer x) {
            int s = (int) MathUtils.squareRootExact(x);
            if (s < 0) {
                throw new UnsupportedCalculationException("No square root for " + x);
            }
            return s;
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
                return 1;
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
            return re;
        }

        @Override
        public Integer decrease(Integer x) {
            return Math.decrementExact(x);
        }

        @Override
        public Integer increase(Integer x) {
            return Math.incrementExact(x);
        }

        /**
         * @see IntCalculator#powMod(java.lang.Object, java.lang.Object, java.lang.Object)
         */
        @Override
        public Integer powMod(Integer at, Integer nt, Integer mt) {
            return MathUtils.powMod(at, nt, mt);
        }


        /* (non-Javadoc)
         * @see cn.ancono.cn.ancono.utilities.math.MathCalculator#getNumberClass()
         */
        @NotNull
        @Override
        public Class<Integer> getNumberClass() {
            return Integer.class;
        }
    }

    /**
     * An implements for integer, which also implements {@link IntCalculator}.
     *
     * @author liyicheng
     * 2017-09-10 12:10
     */
    public static class IntegerCalculator extends MathCalculatorAdapter<Integer> implements IntCalculator<Integer> {
        private static final IntegerCalculator cal = new IntegerCalculator();

        IntegerCalculator() {
        }

        @Override
        public boolean isUnit(@NotNull Integer x) {
            int t = x;
            return t == 1 || t == -1;
        }

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
        public Integer abs(@NotNull Integer x) {
            return Math.abs(x);
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
            return MathUtils.divideExact(x, y);
        }

        @Override
        public @NotNull Integer exactDivide(@NotNull Integer x, @NotNull Integer y) {
            return MathUtils.divideExact(x, y);
        }

        @NotNull
        @Override
        public Integer multiplyLong(@NotNull Integer p, long l) {
            return (int) (p * l);
        }

        @NotNull
        @Override
        public Integer divideLong(@NotNull Integer p, long n) {
            if (p % n != 0) {
                ExceptionUtil.notExactDivision(p, n);
            }
            return (int) (p / n);
        }

        @NotNull
        @Override
        public Integer getZero() {
            return 0;
        }

        @Override
        public boolean isZero(@NotNull Integer para) {
            return para == 0;
        }

        @NotNull
        @Override
        public Integer getOne() {
            return 1;
        }

        @NotNull
        @Override
        public Integer reciprocal(@NotNull Integer p) {
            if (p == 1) {
                return 1;
            } else if (p == -1) {
                return -1;
            }
//            ExceptionUtil.not
            throw new UnsupportedCalculationException("No inverse for " + p);
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
            throwFor("No constant value available");
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
         * @see IntCalculator#divideToInteger(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Integer divideToInteger(@NotNull Integer a, @NotNull Integer b) {
            return a / b;
        }

        /**
         * @see IntCalculator#mod(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Integer mod(@NotNull Integer a, @NotNull Integer b) {
            int x = a, y = b;
            return Math.abs(x) % Math.abs(y);
        }

//        /**
//         * @see IntCalculator#isInteger(java.lang.Object)
//         */
//        @Override
//        public boolean isInteger(Integer x) {
//            return true;
//        }

//        /**
//         * @see IntCalculator#isQuotient(java.lang.Object)
//         */
//        @Override
//        public boolean isQuotient(Integer x) {
//            return true;
//        }

        /**
         * @see IntCalculator#gcd(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Integer gcd(@NotNull Integer a, @NotNull Integer b) {
            return MathUtils.gcd(a, b);
        }

        @Override
        public @NotNull Triple<Integer, Integer, Integer> gcdUV(@NotNull Integer a, @NotNull Integer b) {
            int[] arr = MathUtils.gcdUV(a, b);
            return new Triple<>(arr[0], arr[1], arr[2]);
        }

        @Override
        public Integer lcm(Integer a, Integer b) {
            return MathUtils.lcm(a, b);
        }

        /**
         * @see IntCalculator#decrease(java.lang.Object)
         */
        @Override
        public Integer decrease(Integer x) {
            return x - 1;
        }

        /**
         * @see IntCalculator#increase(java.lang.Object)
         */
        @Override
        public Integer increase(Integer x) {
            return x + 1;
        }

        /**
         * @see IntCalculator#isEven(java.lang.Object)
         */
        @Override
        public boolean isEven(Integer x) {
            return (x & 1) == 0;
        }

        /**
         * @see IntCalculator#isOdd(java.lang.Object)
         */
        @Override
        public boolean isOdd(Integer x) {
            return (x & 1) != 0;
        }

        /**
         * @see IntCalculator#isPositive(java.lang.Object)
         */
        @Override
        public boolean isPositive(Integer x) {
            return x > 0;
        }

        /**
         * @see IntCalculator#remainder(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Integer remainder(@NotNull Integer a, @NotNull Integer b) {
            return a % b;
        }

        /**
         * @see IntCalculator#deg(java.lang.Object, java.lang.Object)
         */
        @Override
        public Integer deg(Integer a, Integer b) {
            return MathUtils.deg(a, b);
        }

        /**
         * @see IntCalculator#isExactDivide(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isExactDivide(@NotNull Integer a, @NotNull Integer b) {
            return a % b == 0;
        }

        /**
         * @see IntCalculator#powMod(java.lang.Object, java.lang.Object, java.lang.Object)
         */
        @Override
        public Integer powMod(Integer a, Integer n, Integer m) {
            return MathUtils.powMod(a, n, m);
        }

        @Override
        public @NotNull Integer powerAndMod(@NotNull Integer x, long n, @NotNull Integer m) {
            return MathUtils.powMod(x, n, m);
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
        public Integer of(long x) {
            return Math.toIntExact(x);
        }

        @NotNull
        @Override
        public Integer of(@NotNull Fraction x) {
            if (x.isInteger()) {
                return x.toInt();
            }
            throw new ArithmeticException();
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
        public Long abs(@NotNull Long x) {
            return Math.abs(x);
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
            return MathUtils.divideExact(x, y);
        }

        @NotNull
        @Override
        public Long multiplyLong(@NotNull Long p, long l) {
            return Math.multiplyExact(p, l);
        }

        @NotNull
        @Override
        public Long divideLong(@NotNull Long p, long n) {
            return MathUtils.divideExact(p, n);
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
            return re;
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
        public Long powMod(Long at, Long nt, Long mt) {
            return MathUtils.powMod(at, nt, mt);
        }

        @NotNull
        @Override
        public Class<Long> getNumberClass() {
            return Long.class;
        }
    }

    /**
     * An implements for long, which also implements {@link IntCalculator}.
     *
     * @author liyicheng
     * 2017-09-10 12:10
     */
    public static class LongCalculator extends MathCalculatorAdapter<Long> implements IntCalculator<Long> {
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

        @Override
        public Long sum(@NotNull List<? extends Long> ps) {
            long re = 0;
            for (var l : ps) {
                re += l;
            }
            return re;
        }

        @NotNull
        @Override
        public Long negate(@NotNull Long para) {
            return -para;
        }

        @NotNull
        @Override
        public Long abs(@NotNull Long x) {
            return Math.abs(x);
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

        @Override
        public boolean isUnit(@NotNull Long x) {
            long t = x;
            return t == 1 || t == -1;
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
        public Long divideToInteger(@NotNull Long a, @NotNull Long b) {
            return a / b;
        }

        /**
         *
         */
        @NotNull
        @Override
        public Long mod(@NotNull Long a, @NotNull Long b) {
            long x = a, y = b;
            return Math.abs(x) % Math.abs(y);
        }

        @Override
        public Long powMod(Long a, Long n, Long m) {
            return MathUtils.powMod(a, n, m);
        }

        @Override
        public @NotNull Long powerAndMod(@NotNull Long x, long n, @NotNull Long m) {
            return MathUtils.powMod(x, n, m);
        }

        //        /**
//         *
//         */
//        @Override
//        public boolean isInteger(Long x) {
//            return true;
//        }

//        /**
//         * @see IntCalculator#isQuotient(java.lang.Object)
//         */
//        @Override
//        public boolean isQuotient(Long x) {
//            return true;
//        }

        /**
         * @see IntCalculator#gcd(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Long gcd(@NotNull Long a, @NotNull Long b) {
            return MathUtils.gcd(a, b);
        }

        @Override
        public @NotNull Triple<Long, Long, Long> gcdUV(@NotNull Long a, @NotNull Long b) {
            long[] arr = MathUtils.gcdUV(a, b);
            return new Triple<>(arr[0], arr[1], arr[2]);
        }

        @Override
        public Long lcm(Long a, Long b) {
            return MathUtils.lcm(a, b);
        }

        /**
         * @see IntCalculator#decrease(java.lang.Object)
         */
        @Override
        public Long decrease(Long x) {
            return x - 1;
        }

        /**
         * @see IntCalculator#increase(java.lang.Object)
         */
        @Override
        public Long increase(Long x) {
            return x + 1;
        }

        /**
         * @see IntCalculator#isEven(java.lang.Object)
         */
        @Override
        public boolean isEven(Long x) {
            return (x & 1) == 0;
        }

        /**
         * @see IntCalculator#isOdd(java.lang.Object)
         */
        @Override
        public boolean isOdd(Long x) {
            return (x & 1) != 0;
        }

        /**
         * @see IntCalculator#isPositive(java.lang.Object)
         */
        @Override
        public boolean isPositive(Long x) {
            return x > 0;
        }

        /**
         * @see IntCalculator#remainder(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Long remainder(@NotNull Long a, @NotNull Long b) {
            return a % b;
        }

        /**
         * @see IntCalculator#deg(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public Long deg(Long a, Long b) {
            return (long) MathUtils.deg(a, b);
        }

        /**
         * @see IntCalculator#isExactDivide(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isExactDivide(@NotNull Long a, @NotNull Long b) {
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
        public Long of(@NotNull Fraction x) {
            if (x.isInteger()) {
                return x.toLong();
            }
            throw new ArithmeticException();
        }

        @NotNull
        @Override
        public Long of(long x) {
            return x;
        }

        @NotNull
        @Override
        public Class<Long> getNumberClass() {
            return Long.class;
        }
    }

    /**
     * An implements for BigInteger, which also implements {@link IntCalculator}.
     *
     * @author liyicheng
     * 2017-09-10 12:10
     */
    public static class BigIntegerCalculator extends MathCalculatorAdapter<BigInteger> implements IntCalculator<BigInteger> {

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
        public BigInteger abs(@NotNull BigInteger x) {
            return x.abs();
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

        @Override
        public boolean isUnit(@NotNull BigInteger x) {
            return x.abs().equals(BigInteger.ONE);
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
        @NotNull
        @Override
        public BigInteger divideToInteger(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.divide(b);
        }

        /**
         * @see IntCalculator#mod(java.lang.Object, java.lang.Object)
         */
        @Override
        public @NotNull BigInteger mod(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.mod(b.abs());
        }

//        /**
//         *
//         */
//        @Override
//        public boolean isInteger(BigInteger x) {
//            return true;
//        }

//        /**
//         * @see IntCalculator#isQuotient(java.lang.Object)
//         */
//        @Override
//        public boolean isQuotient(BigInteger x) {
//            return true;
//        }

        /**
         * @see IntCalculator#gcd(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public BigInteger gcd(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.gcd(b);
        }

        /**
         * @see IntCalculator#decrease(java.lang.Object)
         */
        @Override
        public BigInteger decrease(@NotNull BigInteger x) {
            return x.subtract(BigInteger.ONE);
        }

        /**
         * @see IntCalculator#increase(java.lang.Object)
         */
        @Override
        public BigInteger increase(@NotNull BigInteger x) {
            return x.add(BigInteger.ONE);
        }

        /**
         * @see IntCalculator#isEven(java.lang.Object)
         */
        @Override
        public boolean isEven(@NotNull BigInteger x) {
            return !x.testBit(0);
        }

        /**
         * @see IntCalculator#isOdd(java.lang.Object)
         */
        @Override
        public boolean isOdd(@NotNull BigInteger x) {
            return x.testBit(0);
        }

        /**
         * @see IntCalculator#isPositive(java.lang.Object)
         */
        @Override
        public boolean isPositive(@NotNull BigInteger x) {
            return x.signum() > 0;
        }

        /**
         * @see IntCalculator#remainder(java.lang.Object, java.lang.Object)
         */
        @NotNull
        @Override
        public BigInteger remainder(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.remainder(b);
        }

        /**
         * @see IntCalculator#isExactDivide(java.lang.Object, java.lang.Object)
         */
        @Override
        public boolean isExactDivide(@NotNull BigInteger a, @NotNull BigInteger b) {
            return a.mod(b).equals(BigInteger.ZERO);
        }

        /**
         * @see IntCalculator#powMod(java.lang.Object, java.lang.Object, java.lang.Object)
         */
        @Override
        public BigInteger powMod(@NotNull BigInteger a, @NotNull BigInteger n, @NotNull BigInteger mod) {
            return a.modPow(n, mod);
        }

        @Override
        public @NotNull BigInteger powerAndMod(@NotNull BigInteger x, long n, @NotNull BigInteger m) {
            return x.modPow(BigInteger.valueOf(n), m);
        }

        @Override
        public BigInteger asBigInteger(BigInteger x) {
            return x;
        }

        @NotNull
        @Override
        public BigInteger of(long x) {
            return BigInteger.valueOf(x);
        }

        @NotNull
        @Override
        public BigInteger of(@NotNull Fraction x) {
            if (x.isInteger()) {
                return BigInteger.valueOf(x.toLong());
            }
            throw new ArithmeticException();
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
        public BigDecimal abs(@NotNull BigDecimal x) {
            return x.abs();
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
            return p.divide(BigDecimal.valueOf(n), mc);
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
        public BigDecimal of(long x) {
            return BigDecimal.valueOf(x);
        }

        @NotNull
        @Override
        public BigDecimal of(@NotNull Fraction x) {
            return BigDecimal.valueOf(x.toDouble());
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
        public Double abs(@NotNull Double x) {
            return Math.abs(x);
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


        @Override
        public Double sum(@NotNull List<? extends Double> ps) {
            double sum = 0;
            for (Double d : ps) {
                sum += d;
            }
            return sum;
        }

        @Override
        public Double product(@NotNull List<? extends Double> ps) {
            double result = 1;
            for (var d : ps) {
                result *= d;
            }
            return result;
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
        public Double of(long x) {
            return (double) x;
        }

        @NotNull
        @Override
        public Double of(@NotNull Fraction x) {
            return x.toDouble();
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
         * @see cn.ancono.cn.ancono.utilities.math.MathCalculatorAdapter.DoubleCalculator#isEqual(java.lang.Double, java.lang.Double)
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
         * @see cn.ancono.cn.ancono.utilities.math.MathCalculator#isZero(java.lang.Object)
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
    public static IntegerCalculator integerExact() {
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
    public static IntegerCalculator integer() {
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
    public static LongCalculator longExact() {
        return LongCalculatorExact.cal;
    }

    /**
     * Return a calculator for Long, all the basic operations are the identity as {@code +,-,*,/}.<p>
     * The calculator does not have any constant values.
     *
     * @return a MathCalculator
     */
    @NotNull
    public static LongCalculator longCal() {
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
    public static BigIntegerCalculator bigInteger() {
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
    public static MathCalculator<BigDecimal> bigDecimal(MathContext mc) {
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
    public static MathCalculator<Double> doubleCal() {
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
    public static MathCalculator<Double> doubleDev() {
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
    public static MathCalculator<Double> doubleDev(double dev) {
        return new DoubleCalculatorWithDeviation(Math.abs(dev));
    }

    public static class ZModNCalculator extends MathCalculatorAdapter<Integer> {
        protected final int n;

        ZModNCalculator(int n) {
            this.n = n;

        }

        protected int modN(int x) {
            int re = x % n;
            if (re < 0) {
                re += n;
            }
            return re;
        }

        protected int modN(long x) {
            int re = (int) (x % n);
            if (re < 0) {
                re += n;
            }
            return re;
        }

        public int getModular() {
            return n;
        }

        @NotNull
        @Override
        public Integer getOne() {
            return 1;
        }

        @NotNull
        @Override
        public Integer getZero() {
            return 0;
        }

        @Override
        public int compare(@NotNull Integer x, @NotNull Integer y) {
            return x.compareTo(y);
        }

        @Override
        public boolean isComparable() {
            return true;
        }


        @Override
        public boolean isZero(@NotNull Integer para) {
            return modN(para) == 0;
        }

        @Override
        public boolean isEqual(@NotNull Integer x, @NotNull Integer y) {
            return modN(x - y) == 0;
        }

        @NotNull
        @Override
        public Integer add(@NotNull Integer x, @NotNull Integer y) {
            return modN((long) x + y);
        }

        @NotNull
        @Override
        public Integer subtract(@NotNull Integer x, @NotNull Integer y) {
            return modN((long) x - y);
        }

        @NotNull
        @Override
        public Integer multiply(@NotNull Integer x, @NotNull Integer y) {
            return multiply(x.intValue(), y.intValue());
        }

        private int multiply(int x, int y) {
            return modN((long) x * y);
        }

        @Override
        public boolean isUnit(@NotNull Integer x) {
            return MathUtils.gcd(x, n) == 1;
        }


        protected int inverseOf(int x) {
            var p = MathUtils.gcdUV(x, n);
            var gcd = p[0];
            var y = p[1];
            if (gcd != 1) {
                ExceptionUtil.notInvertible();
            }
            return y;
        }


        @NotNull
        @Override
        public Integer reciprocal(@NotNull Integer x) {
            return inverseOf(x);
        }

        @NotNull
        @Override
        public Integer divide(@NotNull Integer x, @NotNull Integer y) {
            //noinspection SuspiciousNameCombination
            return multiply(x.intValue(), inverseOf(y));
        }
        //        protected int divide(int a, int b) {
//
//        }
//
//        @NotNull
//        @Override
//        public Integer divide(@NotNull Integer x, @NotNull Integer y) {
//            return divide(x.intValue(), y.intValue());
//        }


        @NotNull
        @Override
        public Integer multiplyLong(@NotNull Integer p, long l) {
            return multiply(p.intValue(), modN(l));
        }

        @NotNull
        @Override
        public Integer divideLong(@NotNull Integer x, long n) {
            return multiply(x.intValue(), inverseOf(modN(n)));
        }

        //        @NotNull
//        @Override
//        public Integer divideLong(@NotNull Integer p, long n) {
//            return divide(p.intValue(), modN(n));
//        }


        @NotNull
        @Override
        public Integer squareRoot(@NotNull Integer x) {
            int _x = x;
            for (int n = 0; n < this.n; n++) {
                if (multiply(n, n) == _x) {
                    return n;
                }
            }
            //TODO better implement
            throw new ArithmeticException("Not square root for x=" + x + " in Z mod " + n);
        }

        @NotNull
        @Override
        public Integer pow(@NotNull Integer p, long exp) {
            return MathUtils.powMod(p, exp, this.n);
        }

        @NotNull
        @Override
        public Integer exp(@NotNull Integer a, @NotNull Integer b) {
            return MathUtils.powMod(a, b, n);
        }

//        @Override
//        public Integer powerAndMod(Integer at, Integer nt, Integer mt) {
//            return powerAndMod(at, nt.longValue(), mt);
//        }
//
//        @Override
//        public Integer powerAndMod(Integer at, long n, Integer m) {
//            int a = modP(at);
//            int mod = m;
//            if (mod == 1) {
//                return 0;
//            }
//            if (a == 0 || a == 1) {
//                return a;
//            }
//            int ans = 1;
//            a = a % mod;
//            while (n > 0) {
//                if ((n & 1) == 1) {
//                    ans = multiply(a, ans) % mod;
//                }
//                a = multiply(a, a) % mod;
//                n >>= 1;
//            }
//            return ans;
//        }

        @NotNull
        @Override
        public Integer negate(@NotNull Integer para) {
            return modN(-para);
        }

        @NotNull
        @Override
        public Integer abs(@NotNull Integer x) {
            return modN(x);
        }

        @NotNull
        @Override
        public Integer of(long x) {
            return modN(x);
        }
    }


    static class ZModPCalculatorCached extends ZModNCalculator implements ZModPCalculator<Integer> {
        private final int[] inverse;

        private int[] initInv() {
            int[] inv = new int[n];
            inv[1] = 1;
            for (int i = 2; i < n; i++) {
                if (inv[i] != 0) {
                    continue;
                }
                for (int j = 2; j < n; j++) {
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

        @Override
        public boolean isUnit(@NotNull Integer x) {
            return inverse[x] != 0;
        }

        ZModPCalculatorCached(int p) {
            super(p);
            inverse = initInv();
        }

        @Override
        public long getP() {
            return getModular();
        }

        @Override
        protected int inverseOf(int x) {
            x = modN(x);
            if (inverse[x] == 0) {
                ExceptionUtil.notInvertible();
            }
            return inverse[x];
        }

        @Override
        public @NotNull Integer squareRoot(@NotNull Integer x) {
            return (int) NTUtils.INSTANCE.sqrtModP(x, getP());
        }

    }

    static class ZModPCalculatorGCD extends ZModNCalculator implements ZModPCalculator<Integer> {
        ZModPCalculatorGCD(int n) {
            super(n);
        }

        @Override
        public long getP() {
            return getModular();
        }

        @Override
        public @NotNull Integer squareRoot(@NotNull Integer x) {
            return (int) NTUtils.INSTANCE.sqrtModP(x, getP());
        }
    }

    static class ZMod2Calculator extends ZModNCalculator implements ZModPCalculator<Integer> {
        ZMod2Calculator() {
            super(2);
        }

        @Override
        public long getP() {
            return 2;
        }

        @Override
        public boolean isEqual(@NotNull Integer x, @NotNull Integer y) {
            return (x - y) % 2 == 0;
        }

        @Override
        public @NotNull Integer add(@NotNull Integer x, @NotNull Integer y) {
            return (x + y) % 2;
        }

        @Override
        public @NotNull Integer subtract(@NotNull Integer x, @NotNull Integer y) {
            return (x + y) % 2; // -y = y
        }


        @Override
        public @NotNull Integer multiply(@NotNull Integer x, @NotNull Integer y) {
            return (x * y) % 2;
        }

        @Override
        public boolean isUnit(@NotNull Integer x) {
            return x % 2 == 1;
        }

        @Override
        protected int inverseOf(int x) {
            if (x % 2 == 0) {
                ExceptionUtil.notInvertible();
            }
            return x;
        }

        @Override
        public @NotNull Integer divide(@NotNull Integer x, @NotNull Integer y) {
            if (y % 2 == 0) {
                ExceptionUtil.dividedByZero();
            }
            return x;
        }

        @Override
        public @NotNull Integer multiplyLong(@NotNull Integer p, long l) {
            return (int) ((p * (l % 2)) % 2);
        }

        @Override
        public @NotNull Integer divideLong(@NotNull Integer x, long n) {
            if (n % 2 == 0) {
                ExceptionUtil.dividedByZero();
            }
            return x;
        }

        @Override
        public @NotNull Integer squareRoot(@NotNull Integer x) {
            return x;
        }

        @Override
        public @NotNull Integer pow(@NotNull Integer p, long exp) {
            if (p == 0 && exp == 0) {
                ExceptionUtil.zeroExponent();
            }
            return p;
        }

        @Override
        public @NotNull Integer exp(@NotNull Integer a, @NotNull Integer b) {
            if (a == 0 && b == 0) {
                ExceptionUtil.zeroExponent();
            }
            return a;
        }

        @Override
        public @NotNull Integer negate(@NotNull Integer para) {
            return para;
        }

        @Override
        public @NotNull Integer abs(@NotNull Integer x) {
            return x;
        }


        static final ZMod2Calculator INSTANCE = new ZMod2Calculator();
    }

    private static final int PRIME_CHECK_THRESHOLD = 1024;
    private static final int USE_CACHE_THRESHOLD = 1024;

    /**
     * Gets a calculator of `Z_2`, the binary field.
     */
    public static ZModPCalculator<Integer> intMod2() {
        return ZMod2Calculator.INSTANCE;
    }

    /**
     * Returns a calculator for prime field <code>Z<sub>p</sub></code>, where <code>p</code> is a prime number.
     * The implementation guarantees that no overflow will happen in this calculator.
     * <p></p>
     * <p>
     * It is required that the given integer p is a prime number.
     * <p>
     */
    public static ZModPCalculator<Integer> intModP(int p) {
        if (p == 2) {
            return intMod2();
        }
        if (p <= PRIME_CHECK_THRESHOLD) {
            if (!Primes.getInstance().isPrime(p)) {
                throw new IllegalArgumentException("p must be a prime number!");
            }
        } else {
            var x = BigInteger.valueOf(p);
            if (!x.isProbablePrime(100)) {
                throw new IllegalArgumentException("p must be a prime number!");
            }
        }
        if (p <= USE_CACHE_THRESHOLD) {
            return new ZModPCalculatorCached(p);
        } else {
            return new ZModPCalculatorGCD(p);
        }
    }

    /**
     * Returns a calculator for ring <code>Z<sub>n</sub></code>, where <code>n >= 2</code>.
     */
    public static MathCalculator<Integer> intModN(int n) {
        if (n < 2) {
            throw new IllegalArgumentException("It is required that n >= 2");
        }
        return new ZModNCalculator(n);
    }


    static class BooleanCalculator extends MathCalculatorAdapter<Boolean> {

        @NotNull
        @Override
        public Boolean getOne() {
            return true;
        }

        @NotNull
        @Override
        public Boolean getZero() {
            return false;
        }

        @Override
        public boolean isZero(@NotNull Boolean x) {
            return !x;
        }

        @Override
        public boolean isEqual(@NotNull Boolean x, @NotNull Boolean y) {
            return x == y;
        }

        @Override
        public int compare(@NotNull Boolean x, @NotNull Boolean y) {
            return Boolean.compare(x, y);
        }

        @Override
        public boolean isComparable() {
            return true;
        }

        @NotNull
        @Override
        public Boolean add(@NotNull Boolean x, @NotNull Boolean y) {
            return x ^ y;
        }

        @NotNull
        @Override
        public Boolean negate(@NotNull Boolean x) {
            return x;
        }

        @NotNull
        @Override
        public Boolean abs(@NotNull Boolean x) {
            return x;
        }

        @NotNull
        @Override
        public Boolean subtract(@NotNull Boolean x, @NotNull Boolean y) {
            return add(x, y);
        }

        @NotNull
        @Override
        public Boolean multiply(@NotNull Boolean x, @NotNull Boolean y) {
            return x && y;
        }

        @NotNull
        @Override
        public Boolean divide(@NotNull Boolean x, @NotNull Boolean y) {
            if (!y) {
                ExceptionUtil.dividedByZero();
            }
            return x;
        }

        @NotNull
        @Override
        public Boolean multiplyLong(@NotNull Boolean x, long n) {
            return x && n % 2 != 0;
        }

        @NotNull
        @Override
        public Boolean divideLong(@NotNull Boolean x, long n) {
            if (n % 2 == 0) {
                ExceptionUtil.dividedByZero();
            }
            return x;
        }

        @NotNull
        @Override
        public Boolean reciprocal(@NotNull Boolean x) {
            if (!x) {
                ExceptionUtil.dividedByZero();
            }
            return true;
        }

        @NotNull
        @Override
        public Boolean squareRoot(@NotNull Boolean x) {
            return x;
        }

        @NotNull
        @Override
        public Boolean nroot(@NotNull Boolean x, long n) {
            return x;
        }

        @NotNull
        @Override
        public Class<Boolean> getNumberClass() {
            return Boolean.class;
        }

        @NotNull
        @Override
        public Boolean pow(@NotNull Boolean x, long n) {
            return x;
        }

        @NotNull
        @Override
        public Boolean of(long x) {
            return x % 2 != 0;
        }

        static BooleanCalculator INSTANCE = new BooleanCalculator();
    }


    /**
     * Returns a calculator for boolean as binary field F2.
     */
    public static MathCalculator<Boolean> bool() {
        return BooleanCalculator.INSTANCE;
    }

}
