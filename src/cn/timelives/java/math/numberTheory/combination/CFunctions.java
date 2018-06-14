/**
 * 
 */
package cn.timelives.java.math.numberTheory.combination;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.numberTheory.Primes;
import cn.timelives.java.math.exceptions.NumberValueException;
import cn.timelives.java.utilities.ArraySup;

import java.math.BigInteger;

import static cn.timelives.java.math.MathUtils.degFactorial;
/**
 * A utility class providing some functions in combination mathematics.
 * @author liyicheng
 *
 */
public final class CFunctions {
	private CFunctions(){}
	private static final int MAX_FAC = 19,
			MAX_SUBFAC = 20;
	
	//from 0 to 19 is in the range of long
	static final long[] fac_temp = new long[MAX_FAC+1];
	static{
		int n=0;
		long l=1;
		do{
			fac_temp[n++] = l;
			l *= n;
		}while(n<fac_temp.length);
	}
	static final long[] subfac_temp = new long[MAX_SUBFAC+1];
	static{
		subfac_temp[0] = 1;
		subfac_temp[1] = 0;
		for(int i=2;i<=MAX_SUBFAC;i++){
			subfac_temp[i] = (i-1)*(subfac_temp[i-1]+subfac_temp[i-2]);
		}
	}
	
	private static void throwFor(long n){
		throw new ArithmeticException("n="+n+" is out of range");
	}
	
	private static int intOrTooBig(long l){
		if(l <= Integer.MAX_VALUE ){
			return (int)l;
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
	public static long factorial(int n){
		if(n<0 || n > MAX_FAC){
			throwFor(n);
		}
		return fac_temp[n];
	}
	
	
	private static BigInteger multiplyPrimePowers(Primes pr,int[] pp){
		BigInteger result = BigInteger.ONE;
		for(int i=pp.length-1;i>-1;i--){
			switch(pp[i]){
			case 0:{
				break;
			}
			case 1:{
				result = result.multiply(BigInteger.valueOf(pr.getPrime(i)));
				break;
			}
			default:{
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
	public static BigInteger factorialX(int n){
		if(n<0){
			throwFor(n);
		}
		if(n <= MAX_FAC){
			return BigInteger.valueOf(fac_temp[n]);
		}
		Primes pr = Primes.getInstance();
		pr.enlargePrime(n);
		final int len = pr.getCount(n);
		int[] pp = new int[len];
		for(int i=0;i<len;i++){
			pp[i] = intOrTooBig(MathUtils.degFactorial(pr.getPrime(i), n));
		}
		return multiplyPrimePowers(pr,pp);
	}
	
	/**
	 * Returns the subfactorial of n.
	 * <pre>!n</pre>
	 * Number {@code n} must be in [0,20], otherwise overflow will happen.
	 * The result of subfactorial(0) will be 1.
	 * @throws ArithmeticException if overflow occurred.
	 * @param n a number, must be in [0,20]
	 * @return  the subfactorial of n.
	 * @throws ArithmeticException if overflow occurred.
	 */
	public static long subfactorial(int n){
		if(n<0 || n > MAX_SUBFAC){
			throwFor(n);
		}
		return subfac_temp[n];
	}
	/**
	 * Returns the subfactorial of n.
	 * <pre>!n</pre>
	 * The result of subfactorial(0) will be 1.
	 * @throws ArithmeticException if overflow occurred.
	 * @param n a number, must be in [0,20]
	 * @return  the subfactorial of n.
	 * @throws ArithmeticException if overflow occurred.
	 */
	public static BigInteger subfactorialB(int n){
		if(n<0){
			throwFor(n);
		}
		if(n<=MAX_SUBFAC){
			return BigInteger.valueOf(subfac_temp[n]);
		}
		BigInteger sn = BigInteger.valueOf(subfac_temp[MAX_FAC]),
				sn_1 = BigInteger.valueOf(subfac_temp[MAX_FAC-1]);
		int i = MAX_FAC;
		while(i<n){
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
	 * @return  permutation of {@code m,n}.
	 * @throws ArithmeticException if overflow occurred.
	 */
	public static long permutation(int n,int m){
		if(m>n){
			throw new IllegalArgumentException("m>n"); 
		}
		//special cases 
		if(n==m){
			return factorial(n);
		}
		long r = n;
		n--;
		while(n!=m){
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
	 * @return  permutation of {@code m,n}.
	 * @throws NumberValueException if the result is too big for BigInteger.
	 */
	public static BigInteger permutationB(long n,long m){
		m = Math.abs(m);
		n = Math.abs(n);
		if(m>n){
			throw new IllegalArgumentException("m>n"); 
		}
		//special cases 
		if(n==m){
			if(n>Integer.MAX_VALUE){
				throw new NumberValueException("Too big",n+"!");
			}
			return factorialX((int)n);
		}
		BigInteger r = BigInteger.valueOf(n);
		n--;
		while(n!=m){
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
	public static long degPermutation(long n,long m,long p){
		m = Math.abs(m);
		n = Math.abs(n);
		if(m>n){
			throw new IllegalArgumentException("m>n"); 
		}
		if(m==n){
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
	public static BigInteger combinationB(int n,int m){
		if(m==0){
			return BigInteger.ONE;
		}
		if(m==1){
			return BigInteger.valueOf(n);
		}
		// n! / m!(n-m)!
		int t = n-m;
		if(t < 0){
			throw new ArithmeticException("n<m");
		}
		Primes pr = Primes.getInstance();
		pr.enlargePrime(n);
		final int len = pr.getCount(n);
		int[] pp = new int[len];
		for(int i=0;i<len;i++){
			long p = pr.getPrime(i);
			pp[i] = intOrTooBig(degFactorial(p,n)-degFactorial(p, t) - degFactorial(p, m));
		}
		return multiplyPrimePowers(pr,pp);
	}
	/**
	 * Returns the combination of {@code m,n}.<br/>
	 * <b>C</b><sub>n</sub><sup>m</sup><br/>
	 * @param n
	 * @param m
	 * @return combination of {@code m,n}.
	 */
	public static long combination(int n,int m){
		if(m==0){
			return 1;
		}
		if(m==1){
			return n;
		}
		// n! / m!(n-m)!
		int t = n-m;
		if(t < 0){
			throw new ArithmeticException("n<m");
		}
		if(t < m){
			return combination(n, t);
		}
		//TODO better implement required.
		return permutation(n, t) / factorial(m);
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
	public static long binomial(int n,int k){
		if(n<0){
			long re = multisetNumber(-n, k);
			return k % 2 == 0 ? re : -re;
		}else if(n<k){
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
	 * @see CFunctions#binomial(int, int)
	 */
	public static BigInteger binomialB(int n,int k){
		if(n<0){
			BigInteger re = multisetNumberB(-n, k);
			return k % 2 == 0 ? re : re.negate();
		}else if(n<k){
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
	 * @see CFunctions#binomial(int, int)
	 */
	public static double binomialD(double α,int k){
		double re = α;
		for(int i=1;i<k;i++){
			re *= α-i;
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
	public static long multinomial(int p,int...ns){
		if(ns.length==0){
			throw new IllegalArgumentException("length==0");
		}
		int sum = ArraySup.getSum(ns);
		if(sum>p){
			throw new IllegalArgumentException("ns>p");
		}
		long r =  factorial(p);
		for(int n:ns){
			r /= factorial(n);
		}
		return r;
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
	public static BigInteger multinomialB(int p,int...ns){
		if(ns.length==0){
			throw new IllegalArgumentException("length==0");
		}
		int sum = ArraySup.getSum(ns);
		if(sum>p){
			throw new IllegalArgumentException("ns>p");
		}
		Primes pr = Primes.getInstance();
		pr.enlargePrime(p);
		final int len = pr.getCount(p);
		int[] pp = new int[len];
		for(int i=0;i<len;i++){
			long prime = pr.getPrime(i);
			long power = degFactorial(prime, p);
			for(int n : ns){
				power -= degFactorial(prime, n);
			}
			pp[i] = intOrTooBig(power);
		}
		return multiplyPrimePowers(pr,pp);
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
	public static long multisetNumber(int n,int k){
		if(n<0 || k < 0){
			throw new IllegalArgumentException();
		}
		return combination(n+k-1, k);
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
	public static BigInteger multisetNumberB(int n,int k){
		if(n<0 || k < 0){
			throw new IllegalArgumentException();
		}
		return combinationB(n+k-1, k);
	}
	
	/**
	 * Returns the circle permutation: Take {@code m} elements from {@code n}
	 * different elements and put them in a circle.
	 * <b>P</b><sub>n</sub><sup>m</sup>/m<br/>
	 * @param n
	 * @param m
	 * @return
	 */
	public static long circleP(int n,int m){
		return permutation(n, m)/m;
	}
	/**
	 * Returns the circle permutation: Take {@code m} elements from {@code n}
	 * different elements and put them in a circle.
	 * <b>P</b><sub>n</sub><sup>m</sup>/m<br/>
	 * @param n
	 * @param m
	 * @return
	 */
	public static BigInteger circlePB(long n,long m){
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
	public static long polyColor(int n,int m){
		if(n<2||m<1){
			throw new IllegalArgumentException();
		}
		int t = m-1;
		long re = n % 2 == 0 ? t : -t;
		return  re + MathUtils.power(t, n);
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
	public static long passBall(int n,int m){
		if(n<2||m<0){
			throw new IllegalArgumentException();
		}
		int t = n-1;
		long re = m % 2 == 0 ? t : -t;
		re +=MathUtils.power(t,m);
		return re/n;
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
	public static long integerPartition(long n,long m) {
		if(n==0) {
			return 1;
		}
		if(n<0) {
			return 0;
		}
		if(m<1) {
			return 0;
		}
		if(m>n) {
			m=n;
		}
//		if(m>PARTITION_RECUR_THREHOLD) {
//			return integerPartitionDp(n, m);
//		}
		return integerPartitionRecur(n, m);
	}
//	static final long PARTITION_RECUR_THREHOLD = 100; 
	/**
	 * 
	 * @param n
	 * @param m
	 * @return
	 */
	static long integerPartitionRecur(long n,long m) {
		if(n<=0||m<=0) {
			return 0;
		}
		if(n==1 || m == 1) {
			return 1;
		}
		if(m==2) {
			return n/2+1;
		}
		if(n<m) {
			return integerPartitionRecur(n, n);
		}
		if(n==m) {
			return integerPartitionRecur(n, m-1)+1;
		}
		return integerPartitionRecur(n, m-1)+
				integerPartitionRecur(n-m, m);
	}
//	static long integerPartitionDp(long n,long m) {
//		
//	}
	
	
//	public static void main(String[] args) {
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
