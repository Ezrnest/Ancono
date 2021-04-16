/**
 *
 */
package cn.ancono.math.numberTheory;

import java.util.Arrays;

/**
 * The class for getting prime numbers, this class supports
 * long as prime number result to give out, and
 * caches the result.
 *
 * @author liyicheng
 */
public final class Primes {
    private static final Primes pr = new Primes();
    private long[] par =
            new long[]{2, 3, 5, 7,
                    11, 13, 17, 19,
                    23, 29, 31};
    //the max number searched (not prime number)
    private long max_number = 31;

    /**
     * Gets the max searched number.
     */
    public long getMaxSearched() {
        return max_number;
    }

    /**
     * Gets the count of primes now have.
     *
     * @return
     */
    public int getPrimeCount() {
        return max_index;
    }


    /**
     * exclusive
     */
    private int max_index = par.length;

    /**
     * Ensures that the prime instance has computed more than {@code num} primes.
     */
    public boolean ensurePrimesNumber(int num) {
        if (num >= max_index) {
            synchronized (this) {
                if (num >= max_index) {
                    return fillPrimes(num + 1, max_index);
                }
            }
        }
        return true;
    }

    public void enlargePrime(long target) {
        if (target <= max_number) {
            return;
        }
        synchronized (this) {
            if (target <= max_number) {
                return;
            }
            enlarge0(target);
        }
    }

    /**
     * Determines whether primes below upper bound are all calculated.
     */
    public boolean isPrimesAvailable(long upperBound) {
        return upperBound <= max_number;
    }

    private boolean fillPrimes(int nLen, int from) {
        if (max_number == Long.MAX_VALUE) {
            return false;
        }
        //from inclusive
        long[] nArr;
        if (nLen <= par.length) {
            nArr = par;
        } else {
            var expanded = par.length * 3 / 2;
            if (expanded < nLen) {
                nArr = Arrays.copyOf(par, nLen);
            } else {
                nArr = Arrays.copyOf(par, expanded);
            }
        }

        int i = from;
        long t = max_number + 1;
        if (t % 2 == 0) {
            t++;
        }

        INDEX:
        //noinspection Duplicates
        while (i < nLen) {
            while (!isPrime0(t, nArr)) {
                if (t == Long.MAX_VALUE) {
                    break INDEX;
                }
                t += 2;
            }
            nArr[i++] = t;
//			if(i % 10000 == 0){
//				print(i+": "+ t);
//			}
            t += 2;
        }
        max_number = t;
        max_index = i;
        par = nArr;
        return i >= nLen;
    }

    private boolean fillPrimes(int from) {
        long[] arr = par;
        int nLen = arr.length;
        int i = from;
        long t = max_number + 1;
        if (t % 2 == 0) {
            t++;
        }
        INDEX:
        //noinspection Duplicates
        while (i < nLen) {
            while (!isPrime0(t, arr)) {
                if (t == Long.MAX_VALUE) {
                    break INDEX;
                }
                t += 2;
            }
            arr[i++] = t;
//			if(i % 10000 == 0){
//				print(i+": "+ t);
//			}
            t += 2;
        }
        max_number = t;
        max_index = i;
        return i >= nLen;
    }


    private void enlarge0(long target) {
        long[] nArr;
        nArr = Arrays.copyOf(par, par.length * 3 / 2);

        int i = max_index;
        long t = max_number + 1;
        if (t % 2 == 0) {
            t++;
        }
        while (t <= target) {
            if (isPrime0(t, nArr)) {
                if (i == nArr.length) {
                    nArr = Arrays.copyOf(nArr, nArr.length * 3 / 2);
                }
                nArr[i++] = t;
            }
            t++;
        }
        par = nArr;
        max_number = t;
        max_index = i;
        if (i < nArr.length) {
            fillPrimes(i);
        }
    }

    /**
     * Determines whether the number is prime. Enlarge the
     * prime number array if necessary. Returns {@code false}
     * to all input smaller than 2.
     *
     */
    public boolean isPrime(long n) {
        if (n < 2) {
            return false;
        }
        if (n == 2 || n == 3) {
            return true;
        }
        long sqr = (long) Math.sqrt(n) + 1;
        enlargePrime(sqr);
        return isPrimeSqr(n, sqr);
    }

    private static boolean isPrime0(long n, long[] par) {
        long sqr = (long) Math.sqrt(n) + 1;
        long t;
        int i = 0;
        while ((t = par[i++]) <= sqr) {
            if (n % t == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isPrimeSqr(long n, long sqr) {
        long t;
        int i = 0;
        while (i < par.length && (t = par[i++]) <= sqr) {
            if (n % t == 0) {
                return false;
            }
        }
        return true;

    }

    private static void throwForO(long index) {
        throw new ArithmeticException("Overflow for index=" + index);
    }

    /**
     * Gets the n-th prime (index starts from zero). For example,
     * {@code getPrime(0)} returns {@code 2}, and {@code getPrime(2)} returns {@code 5}.<p>
     * This method will throw an exception if the required prime overflows
     * long.
     *
     * @param index
     * @return
     */
    public long getPrime(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("index < 0 ");
        }
        if (!ensurePrimesNumber(index)) {
            throwForO(index);
        }
        return par[index];
    }

    /**
     * Gets an array of prime numbers.
     *
     * @param length
     * @return
     */
    public long[] getArray(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("length < 1");
        }
        if (!ensurePrimesNumber(length - 1)) {
            throwForO(length - 1);
        }
        return Arrays.copyOf(par, length);
    }

    /**
     * Gets an array of primes smaller or ethan {@code bound}.
     *
     * @param bound
     * @return
     */
    public long[] getPrimesBelow(long bound) {
        enlargePrime(bound);
        int index = Arrays.binarySearch(par, 0, max_index, bound);
        if (index < 0) {
            index = -index - 1;
        }
        return Arrays.copyOf(par, index);
    }


    /**
     * Returns the number of prime numbers that is smaller than or equal to {@code bound}.
     */
    public int getCount(int bound) {
        enlargePrime(bound);
//		print(par);
        int t = Arrays.binarySearch(par, 0, max_index, bound);
        if (t >= 0) {
            return t + 1;
        }
        return -(t + 1);
    }

    /**
     * Returns the instance.
     *
     * @return
     */
    public static Primes getInstance() {
        return pr;
    }

    public int indexOf(long p) {
        return Arrays.binarySearch(par, 0, max_index, p);
    }

//    public static void main(String[] args) {
//        Primes pr = getInstance();
////		print(pr.getArray(100));
//        print(pr.isPrime(2));
////		print(pr.getPrimesBelow(1000));
//    }

}
