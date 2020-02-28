/**
 *
 */
package cn.ancono.math.numberTheory.combination;

import cn.ancono.math.MathUtils;
import cn.ancono.math.algebra.Progression;
import cn.ancono.math.exceptions.NumberValueException;
import cn.ancono.math.numberModels.BigFraction;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberTheory.Primes;
import cn.ancono.utilities.ArraySup;

import java.math.BigInteger;

import static cn.ancono.math.MathUtils.degFactorial;

/**
 * A utility class providing some functions in combination mathematics.
 * @author liyicheng
 *
 */
public final class CombUtils {
    private CombUtils() {
    }

    private static final int MAX_FAC = 19,
            MAX_SUBFAC = 20;

    //from 0 to 19 is in the range of long
    private static final long[] fac_temp = new long[MAX_FAC + 1];

    static {
        int n = 0;
        long l = 1;
        do {
            fac_temp[n++] = l;
            l *= n;
        } while (n < fac_temp.length);
    }

    private static final long[] subfac_temp = new long[MAX_SUBFAC + 1];

    static {
        subfac_temp[0] = 1;
        subfac_temp[1] = 0;
        for (int i = 2; i <= MAX_SUBFAC; i++) {
            subfac_temp[i] = (i - 1) * (subfac_temp[i - 1] + subfac_temp[i - 2]);
        }
    }

    private static void throwFor(long n) {
        throw new ArithmeticException("n=" + n + " is out of range");
    }

    private static int intOrTooBig(long l) {
        if (l <= Integer.MAX_VALUE) {
            return (int) l;
        }
        throw new NumberValueException("Too big");
    }

    /**
     * Returns the factorial of n.
     * <pre>n!</pre>
     * Number {@code n} must be in [0,19], otherwise overflow will happen.
     * @param n a number, must be in [0,19]
     * @return the factorial of n.
     * @throws ArithmeticException if overflow occurred.
     */
    public static long factorial(int n) {
        if (n < 0 || n > MAX_FAC) {
            throwFor(n);
        }
        return fac_temp[n];
    }


    private static BigInteger multiplyPrimePowers(Primes pr, int[] pp) {
        BigInteger result = BigInteger.ONE;
        for (int i = pp.length - 1; i > -1; i--) {
            switch (pp[i]) {
                case 0: {
                    break;
                }
                case 1: {
                    result = result.multiply(BigInteger.valueOf(pr.getPrime(i)));
                    break;
                }
                default: {
                    result = result.multiply(BigInteger.valueOf(pr.getPrime(i)).pow(pp[i]));
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Returns the factorial of n. 
     * <pre>n!</pre>
     * @param n a number 
     * @return the factorial of n.
     */
    @SuppressWarnings("Duplicates")
    public static BigInteger factorialX(int n) {
        if (n < 0) {
            throwFor(n);
        }
        if (n <= MAX_FAC) {
            return BigInteger.valueOf(fac_temp[n]);
        }
        Primes pr = Primes.getInstance();
        pr.enlargePrime(n);
        final int len = pr.getCount(n);
        int[] pp = new int[len];
        for (int i = 0; i < len; i++) {
            pp[i] = intOrTooBig(MathUtils.degFactorial(pr.getPrime(i), n));
        }
        return multiplyPrimePowers(pr, pp);
    }

    /**
     * Returns the subfactorial of n.
     * <pre>!n = n! * (Σ<sub>k=0</sub><sup>n</sup> (-1)^k / k!)</pre>
     * Number {@code n} must be in [0,20], otherwise overflow will happen.
     * The result of subfactorial(0) will be 1.
     * @param n a number, must be in [0,20]
     * @return the subfactorial of n.
     * @throws ArithmeticException if overflow occurred.
     */
    public static long subfactorial(int n) {
        if (n < 0 || n > MAX_SUBFAC) {
            throwFor(n);
        }
        return subfac_temp[n];
    }

    /**
     * Returns the subfactorial of n.
     * <pre>!n = n! * (Σ<sub>k=0</sub><sup>n</sup> (-1)^k / k!)</pre>
     * The result of subfactorial(0) will be 1.
     * @param n a number, must be in [0,20]
     * @return the subfactorial of n.
     * @throws ArithmeticException if overflow occurred.
     */
    public static BigInteger subfactorialB(int n) {
        if (n < 0) {
            throwFor(n);
        }
        if (n <= MAX_SUBFAC) {
            return BigInteger.valueOf(subfac_temp[n]);
        }
        BigInteger sn = BigInteger.valueOf(subfac_temp[MAX_FAC]),
                sn_1 = BigInteger.valueOf(subfac_temp[MAX_FAC - 1]);
        int i = MAX_FAC;
        while (i < n) {
            BigInteger t = BigInteger.valueOf(i).multiply(sn.add(sn_1));
            sn_1 = sn;
            sn = t;
            i++;
        }
        return sn;
    }


    /**
     * Returns the permutation of {@code m,n}.<br/>
     * P<sub>n</sub><sup>m</sup><br/>
     * Throws an exception if overflow occurred.
     * @param n
     * @param m
     * @return permutation of {@code m,n}.
     * @throws ArithmeticException if overflow occurred.
     */
    public static long permutation(int n, int m) {
        if (m > n) {
            throw new IllegalArgumentException("m>n");
        }
        //special cases 
        if (n == m) {
            return factorial(n);
        }
        long r = n;
        n--;
        while (n != m) {
            r = Math.multiplyExact(r, n);
            n--;
        }
        return r;
    }

    /**
     * Returns the permutation of {@code m,n}.<br/>
     * <b>P</b><sub>n</sub><sup>m</sup><br/>
     * Throws an exception if overflow occurred.
     * @param n
     * @param m
     * @return permutation of {@code m,n}.
     * @throws NumberValueException if the result is too big for BigInteger.
     */
    public static BigInteger permutationB(long n, long m) {
        m = Math.abs(m);
        n = Math.abs(n);
        if (m > n) {
            throw new IllegalArgumentException("m>n");
        }
        //special cases 
        if (n == m) {
            if (n > Integer.MAX_VALUE) {
                throw new NumberValueException("Too big", n + "!");
            }
            return factorialX((int) n);
        }
        BigInteger r = BigInteger.valueOf(n);
        n--;
        while (n != m) {
            r = r.multiply(BigInteger.valueOf(n));
            n--;
        }
        return r;
    }


    /**
     * Returns
     * <code>deg(p,<b>P</b><sub>n</sub><sup>m</sup>)</code>
     * @param n
     * @param m
     * @param p the modular
     * @return
     */
    public static long degPermutation(long n, long m, long p) {
        m = Math.abs(m);
        n = Math.abs(n);
        if (m > n) {
            throw new IllegalArgumentException("m>n");
        }
        if (m == n) {
            return MathUtils.degFactorial(p, n);
        }
        return degFactorial(p, n) - degFactorial(p, m);
    }

    /**
     * Returns the combination of {@code m,n}.<br/>
     * <b>C</b><sub>n</sub><sup>m</sup><br/>
     * @param n
     * @param m
     * @return combination of {@code m,n}.
     */
    @SuppressWarnings("Duplicates")
    public static BigInteger combinationB(int n, int m) {
        if (m == 0) {
            return BigInteger.ONE;
        }
        if (m == 1) {
            return BigInteger.valueOf(n);
        }
        // n! / m!(n-m)!
        int t = n - m;
        if (t < 0) {
            throw new ArithmeticException("n<m");
        }
        Primes pr = Primes.getInstance();
        pr.enlargePrime(n);
        final int len = pr.getCount(n);
        int[] pp = new int[len];
        for (int i = 0; i < len; i++) {
            long p = pr.getPrime(i);
            pp[i] = intOrTooBig(degFactorial(p, n) - degFactorial(p, t) - degFactorial(p, m));
        }
        return multiplyPrimePowers(pr, pp);
    }

    /**
     * Returns the combination of {@code m,n}.<br/>
     * <b>C</b><sub>n</sub><sup>m</sup><br/>
     * @return combination of {@code m,n}.
     */
    public static long combination(int n, int m) {
        if (m == 0) {
            return 1;
        }
        if (m == 1) {
            return n;
        }
        // n! / m!(n-m)!
        int t = n - m;
        if (t < 0) {
            throw new ArithmeticException("n<m");
        }
        if (t < m) {
            return combination(n, t);
        }
        try {
            return permutation(n, t) / factorial(m);
        } catch (ArithmeticException ae) {
            return combinationDeg(n, m, t);
        }
    }

    @SuppressWarnings("Duplicates")
    private static long combinationDeg(int n, int m, int t) {
        Primes pr = Primes.getInstance();
        pr.enlargePrime(n);
        final int len = pr.getCount(n);
        int[] pp = new int[len];
        for (int i = 0; i < len; i++) {
            long p = pr.getPrime(i);
            pp[i] = intOrTooBig(degFactorial(p, n) - degFactorial(p, t) - degFactorial(p, m));
        }
        return MathUtils.fromFactorPowers(pp);
    }

    /**
     * Returns the binomial of (n,k), which is 
     * the coefficient of {@code x^k} in the expression 
     * of {@code (x+1)^n}. This method also supports negative 
     * {@code p,n} values.
     * @param n the power: (x+1)^n
     * @param k the power of x whose coefficient is required.
     * @return
     * @see #binomialB(int, int)
     * @see #binomialD(double, int)
     */
    public static long binomial(int n, int k) {
        if (n < 0) {
            long re = multisetNumber(-n, k);
            return k % 2 == 0 ? re : -re;
        } else if (n < k) {
            return 0;
        }
        return combination(n, k);
    }

    /**
     * Returns the binomial of (n,k), which is 
     * the coefficient of {@code x^k} in the expression 
     * of {@code (x+1)^n}. This method also supports negative 
     * {@code p,n} values.
     * @param n the power: (x+1)^n
     * @param k the power of x whose coefficient is required.
     * @return
     * @see CombUtils#binomial(int, int)
     */
    public static BigInteger binomialB(int n, int k) {
        if (n < 0) {
            BigInteger re = multisetNumberB(-n, k);
            return k % 2 == 0 ? re : re.negate();
        } else if (n < k) {
            return BigInteger.ZERO;
        }
        return combinationB(n, k);
    }

    /**
     * Returns the expand of binomial coefficient.
     * <pre>α(α-1)(α-2)...(α-k+1)/k!</pre>
     * @param α
     * @param k
     * @return
     * @see CombUtils#binomial(int, int)
     */
    @SuppressWarnings("NonAsciiCharacters")
    public static double binomialD(double α, int k) {
        double re = α;
        for (int i = 1; i < k; i++) {
            re *= α - i;
        }
        re /= factorial(k);
        return re;
    }

    /**
     * Returns the multinomial of (p,ns), the 
     * while is equal to
     * <pre>p!/n0!*n1!*...nm!</pre>
     * The sum of {@code ns} must NOT be bigger than {@code p}. 
     * @param p
     * @param ns
     * @return
     */
    public static long multinomial(int p, int... ns) {
        checkSumArray(p, ArraySup.getSum(ns), ns);
        long r = factorial(p);
        for (int n : ns) {
            r /= factorial(n);
        }
        return r;
    }

    private static void checkSumArray(int p, int sum2, int[] ns) {
        if (ns.length == 0) {
            throw new IllegalArgumentException("length==0");
        }
        if (sum2 > p) {
            throw new IllegalArgumentException("ns>p");
        }
    }

    /**
     * Returns the multinomial of (p,ns), the 
     * while is equal to
     * <pre>p!/n0!*n1!*...nm!</pre>
     * The sum of {@code ns} must NOT be bigger than {@code p}. 
     * @param p
     * @param ns
     * @return
     */
    @SuppressWarnings("Duplicates")
    public static BigInteger multinomialB(int p, int... ns) {
        checkSumArray(p, ArraySup.getSum(ns), ns);
        Primes pr = Primes.getInstance();
        pr.enlargePrime(p);
        final int len = pr.getCount(p);
        int[] pp = new int[len];
        for (int i = 0; i < len; i++) {
            long prime = pr.getPrime(i);
            long power = degFactorial(prime, p);
            for (int n : ns) {
                power -= degFactorial(prime, n);
            }
            pp[i] = intOrTooBig(power);
        }
        return multiplyPrimePowers(pr, pp);
    }

    /**
     * Returns the multiset number
     * <pre>((n,k))</pre>
     * It is the number of multisets of cardinality k, 
     * with elements taken from a finite set of cardinality n.
     * {@code n*(n+1)*(n+2)*...*(n+k-1) / k!}
     * @param n
     * @param k
     * @return
     */
    public static long multisetNumber(int n, int k) {
        if (n < 0 || k < 0) {
            throw new IllegalArgumentException();
        }
        return combination(n + k - 1, k);
    }

    /**
     * Returns the multiset number
     * <pre>((n,k))</pre>
     * It is the number of multisets of cardinality k, 
     * with elements taken from a finite set of cardinality n.
     * {@code n*(n+1)*(n+2)*...*(n+k-1) / k!}
     * @param n
     * @param k
     * @return
     */
    public static BigInteger multisetNumberB(int n, int k) {
        if (n < 0 || k < 0) {
            throw new IllegalArgumentException();
        }
        return combinationB(n + k - 1, k);
    }

    /**
     * Returns the circle permutation: Take {@code m} elements from {@code n}
     * different elements and put them in a circle.
     * <b>P</b><sub>n</sub><sup>m</sup>/m<br/>
     * @param n
     * @param m
     * @return
     */
    public static long circleP(int n, int m) {
        return permutation(n, m) / m;
    }

    /**
     * Returns the circle permutation: Take {@code m} elements from {@code n}
     * different elements and put them in a circle.
     * <b>P</b><sub>n</sub><sup>m</sup>/m<br/>
     * @param n
     * @param m
     * @return
     */
    public static BigInteger circlePB(long n, long m) {
        return permutationB(n, m).divide(BigInteger.valueOf(m));
    }

    /**
     * Returns the polygon color number: Set {@code m} colors 
     * to a polygon of {@code n} vertexes, and two vertexes 
     * of any edge share different colors.
     * <pre>(m-1)*(-1)^n+(m-1)^n</pre>
     * @param n the number of vertexes, two or bigger.
     * @param m the number of colors, positive
     * @return
     */
    public static long polyColor(int n, int m) {
        if (n < 2 || m < 1) {
            throw new IllegalArgumentException();
        }
        int t = m - 1;
        long re = n % 2 == 0 ? t : -t;
        return re + MathUtils.power(t, n);
    }

    /**
     * Returns the number of different ways of passing a 
     * ball: Among {@code n} persons, a person holds a ball, now 
     * they pass the ball for {@code m} times, and after the final 
     * passing, the first person holds the ball again.
     * <pre>[(n-1)*(-1)^m+(n-1)^m]/n</pre>
     * @param n the number of people
     * @param m times of passing
     * @return
     */
    public static long passBall(int n, int m) {
        if (n < 2 || m < 0) {
            throw new IllegalArgumentException();
        }
        int t = n - 1;
        long re = m % 2 == 0 ? t : -t;
        re += MathUtils.power(t, m);
        return re / n;
    }

    /**
     * Returns the number of partitions of the number {@code n}. A partition is a set of integers
     * and their sum is equal to {@code n}. <p>
     * For example, {@literal 4=4=1+3=2+2=1+1+2=1+1+1+1}, so there are 5 different partitions and 
     * {@code integerPartition(4)=5}. By convenience, 
     * if {@code n==0}, this method will return {@code 1} and if {@code n<0}, {@code 0} will be returned. 
     *
     * @param n an integer
     * @return the number of partitions
     */
    public static long integerPartition(long n) {
        return integerPartition(n, n);
    }

    /**
     * Returns the number of partitions of the number {@code n}, in which the biggest number is {@code m}.
     * A partition is a set of integers
     * and their sum is equal to {@code n}. <p>
     * For example, {@literal 4=4=1+3=2+2=1+1+2=1+1+1+1}, so there are 5 different partitions and 
     * {@code integerPartition(4,3)=1} and {@code integerPartition(4,2)=2}. 
     * By convenience, 
     * if {@code n==0}, this method will return {@code 1}. If {@code n<0}, {@code 0} will be returned. 
     * If {@code m<1}, {@code 0} will be returned.
     * @param n an integer
     * @param m a positive integer
     * @return the number of partitions
     */
    public static long integerPartition(long n, long m) {
        if (n == 0) {
            return 1;
        }
        if (n < 0) {
            return 0;
        }
        if (m < 1) {
            return 0;
        }
        if (m > n) {
            m = n;
        }
//		if(m>PARTITION_RECUR_THREHOLD) {
//			return integerPartitionDp(n, m);
//		}
        return integerPartitionRecur(n, m);
    }

    //	static final long PARTITION_RECUR_THREHOLD = 100; 
    static long integerPartitionRecur(long n, long m) {
        if (n <= 0 || m <= 0) {
            return 0;
        }
        if (n == 1 || m == 1) {
            return 1;
        }
        if (m == 2) {
            return n / 2 + 1;
        }
        if (n < m) {
            return integerPartitionRecur(n, n);
        }
        if (n == m) {
            return integerPartitionRecur(n, m - 1) + 1;
        }
        return integerPartitionRecur(n, m - 1) +
                integerPartitionRecur(n - m, m);
    }

    /**
     * Returns a progression <code>a(m) = C(n,m)</code>
     */
    public static Progression<Long> binomialsOf(int n) {
        //C(n,m) = n!/(m!*(n-m)!) = n! / (m-1)!*(n-m+1)! * (n-m+1)/m
        return Progression.createProgressionRecur1WithIndex(with -> {
            long prev = with.getObj();
            long m = with.getLong();
            return prev * (n - m + 1) / m;
        }, n + 1, Calculators.getCalLong(), 1L);
    }

    /**
     * Returns a progression <code>a(m) = C(n,m)</code>
     */
    public static Progression<BigInteger> binomialsBigOf(int n) {
        return Progression.createProgressionRecur1WithIndex(with -> {
            BigInteger prev = with.getObj();
            long m = with.getLong();
            var t = BigInteger.valueOf(n - m + 1);
            var mBig = BigInteger.valueOf(m);
            return prev.multiply(t).divide(mBig);
        }, n + 1, Calculators.getCalBigInteger(), BigInteger.ONE);
    }

    /**
     * Returns the inverse count of the array.
     */
    public static int inverseCount(int[] arr) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            int t = arr[i];
            for (int j = i + 1; j < arr.length; j++) {
                if (arr[j] < t) {
                    count++;
                }
            }
        }
        return count;
    }


    private static final long[] EULER_NUMBER_EVEN_LONG = new long[]{
            1, -1, 5, -61, 1385, -50521, 2702765, -199360981, 19391512145L, -2404879675441L,
            370371188237525L, -69348874393137901L};

    /**
     * Returns the n-th Euler number. The leading several terms are
     * <pre>1, 0, -1, 0, 5, 0, -61, 0, 1385, 0, -50521, 0, 2702765 ...</pre>
     * Euler number is defined by the initial value E<sub>0</sub> = 1 and the recursive formula:
     * <pre>E<sub>2n</sub> + C(2n,2n-2)E<sub>2n-2</sub> + C(2n,2n-4)E<sub>2n-4</sub> + ... + C(2n,2)E<sub>2</sub> + E<sub>0</sub> = 0</pre>
     * @param n an integer, starting from 0, and should not exceed 12(because of overflow).
     * @return the n-th Euler number.
     */
    public static long numEuler(int n) {
        if (n % 2 == 1) {
            return 0;
        }
        n /= 2;
        if (n >= EULER_NUMBER_EVEN_LONG.length) {
            throw new ArithmeticException("Euler number exceeds long for index=" + n * 2);
        }
        return EULER_NUMBER_EVEN_LONG[n];
    }


    /**
     * Returns a progression of Euler number of even index. The progression starts from 0 and has the length of <code>n</code>.<p></p>
     * The leading several terms are
     * <pre>1, -1, 5, -61, 1385, -50521, 2702765, -199360981</pre>
     * @param n the length of the progression
     */
    public static Progression<BigInteger> numEulerEvenBig(int n) {
//        if(n % 2 == 1){
//            return BigInteger.ZERO;
//        }
//        if(n < EULER_NUMBER_EVEN_LONG.length){
//            return BigInteger.valueOf(numEuler(n));
//        }
        BigInteger[] initials = ArraySup.mapTo(EULER_NUMBER_EVEN_LONG, BigInteger::valueOf, BigInteger.class);
        return Progression.createProgressionRecur((Progression<BigInteger> p, Long idx) -> {
            BigInteger sum = BigInteger.ZERO;
            var comb = binomialsBigOf(Math.toIntExact(idx * 2));
            for (int i = 0; i < idx; i++) {
                sum = sum.add(comb.get(i * 2).multiply(p.get(i)));
            }
            return sum.negate();
        }, n, Calculators.getCalBigInteger(), initials);
//        n = n/2;
//        BigInteger[] tempTable = new BigInteger[n+1];
//        for(int i=0;i<EULER_NUMBER_EVEN_LONG.length;i++){
//            tempTable =
//        }
//        for(int i=0;i<n;i++){
//            var combs = binomialsOf()
//        }
//        return null;
    }

    private static Fraction[] BernoulliNumbers;

    private static synchronized void initBernoulli() {
        if (BernoulliNumbers == null) {
            BernoulliNumbers = new Fraction[]{
                    Fraction.ONE,
                    Fraction.valueOf(1, 6),
                    Fraction.valueOf(-1, 30),
                    Fraction.valueOf(1, 42),
                    Fraction.valueOf(-1, 30),
                    Fraction.valueOf(5, 66),
                    Fraction.valueOf(-691, 2730),
                    Fraction.valueOf(7, 6),
                    Fraction.valueOf(-3617, 510),
                    Fraction.valueOf(43867, 798),
                    Fraction.valueOf(-174611, 330),
                    Fraction.valueOf(854513, 138),
                    Fraction.valueOf(-236364091, 2730),
                    Fraction.valueOf(8553103, 6),
                    Fraction.valueOf(-23749461029L, 870),
                    Fraction.valueOf(8615841276005L, 14322),
                    Fraction.valueOf(-7709321041217L, 510),
                    Fraction.valueOf(2577687858367L, 6),
            };
        }
    }

    /*
    1,  1,  -1,  1,  -1,  5,  -691,  7,  -3617,  43867,  -174611,  854513,  -236364091,  
    8553103,  -23749461029,  8615841276005,  -7709321041217,  2577687858367,  -26315271553053477373,  2929993913841559,  
     1, 6,  30,  42,  30,  66,  2730,  6,  510,  798,  330,  138,  2730,  6,  870,  14322,
      510,  6,  1919190,  6,  13530,  1806,  690,  282,  46410,  66,  1590,  798,  870,  354,  56786730
     */

    /**
     * Returns the n-th Bernoulli number. The first several terms are:
     * <pre>1, -1/2, 1/6, 0, -1/30, 0, 1/42, 0, -1/30, 0, 5/66 ...</pre>
     * The Bernoulli number is defined by the initial value B<sub>0</sub>=1 and
     * the recursive formula:
     * <pre>C(n+1,0)B<sub>0</sub> + C(n+1,1)B<sub>1</sub> + C(n+1,2)B<sub>2</sub> + ... + C(n+1,n)B<sub>n</sub> = 0</pre>
     * @param n the index, starting from 0 and it should not exceed 17(because of overflow).
     * @return the n-th Bernoulli number
     */
    public static Fraction numBernoulli(int n) {
        if (n == 1) {
            return Fraction.HALF.negate();
        }
        if (n % 2 == 1) {
            return Fraction.ZERO;
        }
        n /= 2;
        initBernoulli();
        if (n >= BernoulliNumbers.length) {
            throw new ArithmeticException("Bernoulli number overflow long for index = " + n * 2);
        }
        return BernoulliNumbers[n];
    }

    /**
     * Returns a progression containing Bernoulli number of even index. The progression's index starts from 0 and
     * is smaller than <code>n</code>.
     * <p>
     * The leading several terms are
     * <pre>1, 1/6, -1/30, 1/42, -1/30, 5/66, -691/2730, 7/6, -3617/510, 43867/798 ...</pre>
     * @param n the length of the progression
     */
    public static Progression<BigFraction> numBernoulliEvenBig(int n) {
        initBernoulli();
        BigFraction[] initials = ArraySup.mapTo(BernoulliNumbers, BigFraction::fromFraction, BigFraction.class);
        return Progression.createProgressionRecur((p, k) -> {
            BigFraction sum = BigFraction.ZERO;
            var comb = binomialsBigOf(Math.toIntExact(2 * k + 1));
            for (int i = 0; i < k; i++) {
                var term = p.get(i).multiply(comb.get(2 * i));
                sum = sum.add(term);
            }
            if (k >= 1) {
                var term = BigFraction.fromFraction(numBernoulli(1)).multiply(comb.get(1));
                sum = sum.add(term);
            }
            var coe = comb.get(2 * k);
            sum = sum.divide(coe);
            return sum.negate();
        }, n, BigFraction.getCalculator(), initials);
    }


//	static long integerPartitionDp(long n,long m) {
//		
//	}


//    public static void main(String[] args) {
//
////        print(numBernoulli(6));
////        print(numBernoulli(20));
////        print(BernoulliNumbers.length);
////        initBernoulli();
////        for(int i=0;i<10;i++){
////            printnb(BernoulliNumbers[i]+", ");
////        }
//        numBernoulliEvenBig(20).forEach(Printer::print);
//    }
////		final int n = 20;
////		long sum = 0,t = n;
////		long f = MathFunctions.power(-1, n);
////		sum += f;
////		for(int i=n-1;i>=0;i--){
////			f = -f;
////			sum += f*t;
////			t *= i;
////		}
////		Timer t = new Timer();
////		t.start();
////		BigInteger re = BigInteger.ONE;
////		for(long i=1;i<1000000;i++){
////			re = re.multiply(BigInteger.valueOf(i));
////		}
////		print("MUL "+t.end());
////		NumberFormat nf = SNFSupport.dfByDigit(4);
////		for(int i=0;i<20;i++){
////			print(nf.format(distributionBinomialB(i,20,0.5d).doubleValue()));
////		}
//		
//	}
}
