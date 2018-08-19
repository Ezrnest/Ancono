package cn.timelives.java.math;

import cn.timelives.java.math.equation.Type;
import cn.timelives.java.math.equation.inequation.SVPInequation;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberTheory.Primes;
import cn.timelives.java.math.set.IntervalUnion;
import cn.timelives.java.utilities.ArraySup;
import cn.timelives.java.utilities.ModelPatterns;

import java.math.BigInteger;
import java.util.*;

import static cn.timelives.java.utilities.Printer.print;


/**
 * Provides some useful math functions which are not included in JDK.
 * @author lyc
 *
 */
public class MathUtils {
	/**
	 * Calculate two numbers' GCD.Make sure that these two numbers are both bigger than zero.
	 * @param n1 a number
	 * @param n2 another number
	 * @return GCD of n1 and n2
	 */
	public static long gcd(long n1,long n2){
		//use Euclid's gcd algorithm
		long t;
		while(n2>0){
			 t = n2;
			n2 = n1 % n2;
			n1 = t;
		}
		return n1;
	}
	/**
	 * Calculate two numbers' GCD of int. Make sure that these two numbers are both bigger than zero.
	 * @param n1 a number
	 * @param n2 another number
	 * @return GCD of n1 and n2
	 */
	public static int gcd(int n1,int n2){
		//use Euclid's gcd algorithm
		int t;
		while(n2>0){
			 t = n2;
			n2 = n1 % n2;
			n1 = t;
		}
		return n1;
	}
	
	
	/**
	 * Calculate the two numbers' least common multiple.The parameters are required to be positive,
	 * if either of them is negative,the result is unspecified.
	 * @param n1 a number
	 * @param n2 another number
	 * @return LCM of n1 and n2.
	 */
	public static long lcm(long n1,long n2){
		long gcd = gcd(n1,n2);
		return n1/gcd * n2;
	}
	
	/**
	 * Calculate the two int numbers' least common multiple.The parameters are required to be positive,
	 * if either of them is negative,the result is unspecified.
	 * @param n1 a number
	 * @param n2 another number
	 * @return LCM of n1 and n2.
	 */
	public static int lcm(int n1,int n2){
		int gcd = gcd(n1,n2);
		return n1/gcd * n2;
	}
	
	/**
	 * Calculate the numbers' GCD.The numbers are required to be positive.
	 * @param ls an array of numbers,at least one element.
	 * @return the GCD of {@code ls}
	 */
	public static long gcd(long...ls){
		if(ls.length<2){
			return ls[0];
		}
		long gcd = gcd(ls[0],ls[1]);
		for(int i=2;i<ls.length;i++){
			gcd = gcd(gcd,ls[i]);
		}
		return gcd;
	}
	
	
	/**
	 * Calculate the numbers' GCD and LCM.The result will be stored in an array with two 
	 * elements.The numbers are required to be positive.
	 * @param ls an array of numbers,at least one element.
	 * @return an array,first element of which is the GCD of {@code ls},and the 
	 * second element is the LCM of {@code ls}.
	 */
	public static long lcm(long...ls){
		if(ls.length<2){
			return ls[0];
		}
		long lcm = ls[0];
		for(int i=1;i<ls.length;i++){
			lcm = lcm(lcm,ls[i]);
		}
		return lcm;
	}

	public static BigInteger lcm(BigInteger n1,BigInteger n2){
		BigInteger gcd = n1.gcd(n2);
		return n1.divide(gcd).multiply(n2);
	}

	/**
	 * Returns the max number k that {@code |b|%|a|^k==0} while {@code |b|%|a|^(k+1)!=0}.
	 * @param a a number except {@code 0,1,-1}.
	 * @param b another number
	 * @return deg(a,b)
	 */
	public static int deg(long a,long b){
		a = Math.abs(a);
		b = Math.abs(b);

		if(a==0 || a == 1){
			throw new IllegalArgumentException();
		}
		int k = 0;
		while(b % a ==0){
			k++;
			b = b/a;
        }
        return k;
    }
	
	/**
	 * Returns the max number k that {@code |b|%|a|^k==0} while {@code |b|%|a|^(k+1)!=0}, with 
	 * its parameters are ints.
	 * @param a a number except {@code 0,1,-1}.
	 * @param b another number
	 * @return deg(a,b)
	 */
	public static int deg(int a,int b){
		a = Math.abs(a);
		b = Math.abs(b);
		if(a==0 || a == 1){
			throw new IllegalArgumentException();
		}
		int k = 0;
		while(b % a ==0){
			k++;
			b = b/a;
		}
		return k;
	}
	
	/**
	 * Returns the result of {@code deg(|p|,|n|!)},
	 * @param p a number except {@code 0,1,-1}.
	 * @param n another number
	 * @return the result
	 */
	public static long degFactorial(long p,long n){
		p = Math.abs(p);
		n = Math.abs(n);
		if(p == 0 || p==1){
			throw new IllegalArgumentException();
		}
		long re = 0;
		while((n = n/p)!=0){
			re += n;
		}
		return re;
	}
	
	/**
	 * Returns the max number of k that {@code a^k <= b && a^(k+1) > b},this 
	 * method requires that {@code |a| > 1 && b != 0}.
	 * <p>This method is equal to {@literal [log(a,b)]} in math.
	 * @param a a number , {@code |a| > 1}
	 * @param b a number , {@code b != 0}
	 * @return the result , non-negative
	 */
	public static int maxPower(long a,long b){
		a = Math.abs(a);
		b = Math.abs(b);
		if(a==0 || a==1 || b == 0){
			throw new IllegalArgumentException();
		}
		int re = 0;
		int p = 1;
		while(p<=b){
			p *= a;
			re++;
			print(p+",re="+re);
		}
		return --re;
	}
	
	/**
	 * Calculate the square root of {@code n}.If n cannot be expressed as 
	 * a square of an integer,then {@code -1} will be returned. Throws an 
	 * exception if {@code n<0}
	 * @param n a number, positive or 0.
	 * @return the positive exact square root of n,or {@code -1}
	 */
	public static long squareRootExact(long n){
		if(n<0){
			throw new ArithmeticException();
		}
		if(n==0){
			return 0;
		}
		if(n==1){
			return 1;
		}
		long re = 1;
		//fast even number test
		while((n&1) == 0){
			n>>=1;
			if((n&1)!=0){
				return -1;
			}
			
			n>>=2;
			re<<=1;
		}
		long t = 3;
		long t2 = 9;
		while(t2 <= n){
			if( n % t == 0){
				if( n % t2!=0){
					return -1;
				}
				re *= t;
				n /= t2;
				continue;
			}
			t = t+2;
			t2 = t * t;
		}
		if(n!=1){
			return -1;
		}
		return re;
	}
	
	
	/**
	 * Return the value of n^p.
	 * @param n a number
	 * @param p > -1
	 * @throws ArithmeticException if p < 0 or p==0&&n==0
	 * @see MathUtils#power(long, int)
	 * @deprecated bad time performance
	 * @return n^p
	 */
	@Deprecated
	public static long power0(long n,int p){
		if(p<0){
			throw new ArithmeticException("Cannot calculate as integer");
		}else if(p==0){
			if(n==0){
				throw new ArithmeticException("0^0");
			}else{
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
		for(int i=1;i<p2;i++){
			powers[i] = powers[i-1] * powers[i-1];
		}
		long re = 1;
		for(int i=0;i<p2;i++){
			if((p & 1) == 1){
				re *= powers[i];
			}
			p >>>= 1;
		}
		return re;
	}
	/**
	 * Return the value of n^p.
	 * @param n a number
	 * @param p > -1
	 * @throws ArithmeticException if p < 0 or p==0&&n==0
	 * @return n ^ p
	 */
	public static long power(long n,int p){
		if(p<0){
			throw new ArithmeticException("Cannot calculate as integer");
		}else if(p==0){
			if(n==0L){
				throw new ArithmeticException("0^0");
			}else{
				return 1L;
			}
		}
		long re = 1L;
		while(p>0){
			if((p&1)!=0){
				re *= n;
			}
			n *= n;
			p>>=1;
		}
		return re;
	}
	/**
	 * Return the value of n^p.
	 * @param n a number
	 * @param p > -1
	 * @throws ArithmeticException if {@code p < 0} or {@code p==0&&n==0} or the result overflows a long
	 * @return n ^ p
	 */
	public static long powerExact(long n,int p){
		if(p<0){
			throw new ArithmeticException("Cannot calculate as integer");
		}else if(p==0){
			if(n==0){
				throw new ArithmeticException("0^0");
			}else{
				return 1;
			}
		}
		long re = 1L;
		while(p>0){
			if((p&1)!=0){
				re =  Math.multiplyExact(re, n);
			}
			n =  Math.multiplyExact(n, n);
			p>>=1;
		}
		return re;
	}
	
	
	
	
	/**
	 * Turn the vector = (x,y) anticlockwise for {@code rad}.
	 * @param x 
	 * @param y
	 * @param rad 
	 * @return
	 */
	public static double[] turnRad(double x,double y,double rad){
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
	 * @param num num>0
	 * @return
	 */
	public static int findMin2T(int num){
		int n = 1;
		while(n<num){
			n<<=1;
		}
		return n;
	}
	
	public static int[] enSureCapAndAdd(int[] arr,int pos,int ele){
		if(pos>=arr.length){
			arr = Arrays.copyOf(arr, arr.length*3/2);
		}
		arr[pos] = ele;
		return arr;
	}
	/**
	 * Return the average of a1 and a2 exactly as an integer,
	 * this method is equal to (a1+a2)/2 without overflow and 
	 * underflow.
	 * @return (a1+a2)/2
	 */
	public static int averageExactly(int a1,int a2){
		return (int)(((long)a1+(long)a2)/2);
	}
	/**
	 * Returns the positive n-th root of {@code x},or {@code -1} if it cannot be represent as  
	 * long.For example {@code rootN(1024,5) = 4}
	 * @param x a number 
	 * @param n indicate the times of root
	 * @return n-th root of {@code x}
	 * @throws IllegalArgumentException if {@code n<=0} or {@code x<0}
	 */
	public static long rootN(long x,int n){
		if(n<=0){
			throw new IllegalArgumentException("n<=0");
		}
		if(n==1){
			return x;
		}
		if(x==1L || x == 0L){
			return x;
		}
		long root = 2L;
		//try from 2.
		long t;
		while(true){
			t = powerF(root, n);
			if(t==x){
				break;
			}
			if(t<=0||t>x){
				root = -1;
				break;
			}
			root++;
		}
		return root;
	}
	/**
	 * A fast version of power.
	 * @param p
	 * @param n
	 * @return
	 */
	private static long powerF(long n,int p){
		long re = 1L;
		while(p>0){
			if((p&1)!=0){
				re *= n;
			}
			n *= n;
			p>>=1;
		}
		return re;
	}
	/**
	 * Returns {@code (a^n) % mod}, for example, {@code powerAndMod(2,2,3) = 1}.This 
	 * method will not check overflow.
	 * @param a a number, positive  
	 * @param n must be >=0, if n < 0, than it is taken as 0 and 1 will be returned.
	 * @param mod the modular, must be less than 2<<63, or overflow may happen.  
	 * @return
	 */
	public static long powerAndMod(long a,long n,long mod){
		if(a< 0){
			throw new IllegalArgumentException("a<0");
		}
		if(mod == 1){
			return 0;
		}
		if(a == 0 || a==1){
			return a;
		}
		long ans = 1;
		a = a % mod;
		while(n>0){
			if((n&1)==1){
				ans = (a*ans)%mod;
				
			}
			a = (a*a) % mod;
			n>>=1;
		}
		return ans;
	}
	/**
	 * Returns {@code (a^n) % mod}, with number as int. For example, {@code powerAndMod(2,2,3) = 1}.This 
	 * method will not check overflow.
	 * @param a a number, positive  
	 * @param n must be >=0, if n < 0, than it is taken as 0 and 1 will be returned.
	 * @param mod the modular, must be less than 2<<63, or overflow may happen.  
	 * @return
	 */
	public static int powerAndMod(int a,int n,int mod){
		if(a< 0){
			throw new IllegalArgumentException("a<0");
		}
		if(mod == 1){
			return 0;
		}
		if(a == 0 || a==1){
			return a;
		}
		int ans = 1;
		a = a % mod;
		while(n>0){
			if((n&1)==1){
				ans = (a*ans)%mod;
				
			}
			a = (a*a) % mod;
			n>>=1;
		}
		return ans;
	}
	/**
	 * Produces Miller-Rabin prime number test algorithm for the number x.If this 
	 * method returns true, then the number has the possibility of (1/4)^round of not 
	 * being a prime.
	 * @param x a number,positive
	 * @param round round number for test, positive
	 * @return true if the number passes the test.
	 */
	public static boolean doMillerRabin(long x,long round){
		//basic check
		if(x == 2 || x == 3 || x == 5 || x == 7 || x == 11 || x == 13 || 
				x == 17 || x == 19 || x  == 23 || x == 29 || x == 31){
			return true;
		}
		if(x<2){
			throw new IllegalArgumentException("x<=1");
		}
		if(round<1){
			throw new IllegalArgumentException("round < 1");
		}
		long x_1 = x - 1;
		long d = x_1;
		int s = 0;
		while((d&1)==0){
			d >>= 1;
			s++;
		}
		Random rd = new Random();
		for(int i=0;i<round;i++){
			long a = randomLong(rd,x);
			long t = powerAndMod(a, d, x);
			if(t==1){
				return false;
				
			}
			for(int r=0;r<s;r++,t = (t*t) % x){
				if(t == x_1){
					return false;
				}
			}
			
		}
		return true;
	}
	/**
	 * Produces a random long number x (x>=0 && x < bound) according to the random. 
	 * @param rd a random
	 * @param bound exclusive
	 * @return
	 */
	public static long randomLong(Random rd,long bound){
		if(bound <= Integer.MAX_VALUE){
			return rd.nextInt((int)bound);
		}
		long mask = Long.MAX_VALUE;
		while(mask >= bound){
			mask >>= 1;
		}
		mask <<= 1;
		mask--;
		while(true){
			long r = rd.nextLong() & mask;
			if(r < bound){
				return r;
			}
		}
	}
	
	/**
	 * Produces Miller-Rabin prime number test algorithm for the number x.If this 
	 * method returns true, then the number is almost a prime number( 1/(2<<50) chance of not 
	 * being one). 
	 * @param x a number,positive
	 * @return true if the number passes the test.
	 * @see MathUtils#doMillerRabin(long, long)
	 */
	public static boolean doMillerRabin(long x){
		return doMillerRabin(x,25);
	}
	/**
	 * Computes the n-th katalan number. This method do not consider the overflow. 
	 * @param n
	 * @return
	 */
	public static long calculateKatalan(int n){
		long[] katList = new long[n];
		katList[0] = 1;
		return ck0(n-1,katList);
	}
	private static long ck0(int n,long[] katList){
		if(katList[n] != 0){
			return katList[n];
		}
		long sum = 0;
		for(int i=0;i<n;i++){
			sum += ck0(i,katList) * ck0(n-i-1,katList);
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
	 * @param a the coefficient of x^2.
	 * @param b the coefficient of x.
	 * @param c the constant coefficient
	 * @param mc a MathCalculator
	 * @return the list of solution,regardless of order.
	 */
	public static <T> List<T> solveEquation(T a,T b,T c,MathCalculator<T> mc){
		//Calculate the delta
		T delta;
		{//=mc.subtract(mc.multiply(b, b), mc.multiplyLong(mc.multiply(a, c), 4l));;
			T t1 = mc.multiply(b, b);
			T t2 = mc.multiply(a, c);
			T t3 = mc.multiplyLong(t2, 4l);
			delta = mc.subtract(t1, t3);
		}
		int compare = 1;
		try{
			compare = mc.compare(delta, mc.getZero());
		}catch(UnsupportedCalculationException ex){
			try{
				if(mc.isZero(delta))
					compare = 0;
			}catch(UnsupportedCalculationException ex2){
			}
		}
//		Printer.print(delta);
		if(compare<0){
			//no solution
			return Collections.emptyList();
		}else if(compare == 0){
			List<T> so = new ArrayList<>(1);
			// -b/2a
			T re = mc.divide(mc.divideLong(b, -2l), a);
			so.add(re);
			return so;
		}else{
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
	 * @param a the coefficient of x^2.
	 * @param b the coefficient of x.
	 * @param c the constant coefficient
	 * @param mc a MathCalculator
	 * @return a list of the solutions
	 */
	public static <T> List<T> solveEquationIma(T a,T b,T c,MathCalculator<T> mc){
		T delta = mc.subtract(mc.multiply(b, b), mc.multiplyLong(mc.multiply(a, c), 4l));
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
	 * @param a the coefficient of x^2.
     * @param b the coefficient of x.
     * @param c the constant coefficient
     * @param mc a MathCalculator
	 * @param <T>
	 * @return
	 */
	public static <T> IntervalUnion<T> solveInequation(T a, T b, T c, Type op, MathCalculator<T> mc){
        return SVPInequation.quadratic(a,b,c,op,mc).getSolution();
	}
	/**
	 * Reduce the number to an array representing each digit of the radix. The 
	 * {@code number} should be the sum of 
	 * <pre>result[i] * radix^i</pre>
	 * @param number
	 * @param radix
	 * @return an array
	 */
	public static int[] radix(int number,int radix){
		if(number < radix){
			return new int[]{number};
		}
		int maxPow = maxPower(radix,number);
		print(maxPow);
		int[] res = new int[maxPow+1];
		int i = 0;
		while(number>0){
			int t = number / radix ;
			res[i++] = number - radix * t;
			number = t;
		}
		return res;
	}
	/**
	 * Returns the integer value of the square root of @{@code n}.
	 * <pre>[sqrt(n)]</pre>
	 * @param n a positive number.
	 * @return {@code [sqrt(n)]}
	 */
	public static long sqrtIntL(long n){
		if(n<0){
			throw new ArithmeticException("n<0");
		}
		if(n<16){
			return sqrtInt0(n,0);
		}
		if(n >= 9223372030926249001L){
			//to prevent the overflow
			return 3037000499L;
		}
		int p = 4;
		//find the lower bound and the upper bound.
		long high = 64l;
		while(high < n){
			p+=2;
			high *= 4;
		}
		p>>>=1;
		long low = 1l<<p;
		high = low<<1;
		return ModelPatterns.binarySearchL(low, high, (long x) ->{
			long sqr = x*x;
			print(x);
			if(sqr == n){
				return 0;
			}
			if(sqr > n){
				return 1;
			}
			if(sqr < n){
				if(sqr + 2*x + 1 > n){
					return 0;
				}
			}
			return -1;
		});
	}
	
	
	private static long sqrtInt0(long n,long lb){
		while(lb*lb <= n){
			lb++;
		}
		return lb-1;
	}
	/**
	 * Returns the arctan value of y/x.
	 * @param mc
	 * @param x
	 * @param y
	 * @return
	 */
	public static <T> T atan(MathCalculator<T> mc,T x,T y){
		if(mc.isZero(x)){
			int comp = mc.compare(y, mc.getZero());
			if(comp==0){
				throw new ArithmeticException("x=y=0!");
			}
			T pi_2 = mc.divideLong(mc.constantValue(MathCalculator.STR_PI),2l);
			return comp > 0 ? pi_2 : mc.negate(pi_2);
		}
		return mc.arctan(mc.divide(y, x));
	}
	
	/**
	 * Returns {@code x*y<=0}
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean oppositeSignum(double x,double y) {
		return (x >= 0d && y <= 0d) || (x <= 0d && y >= 0d);
	}
	
	/**
	 * Returns {@code (x-a)(y-a)<=0}
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean oppositeSide(double x,double y,double a) {
		return (x >= a && y <= a) || (x <= a && y >= a);
	}
	/**
	 * Returns {@code (x-a)(y-a)<=0}
	 * @param x
	 * @param y
	 * @return
	 */
	public static <T> boolean oppositeSide(T x,T y,T a,MathCalculator<T> mc){
		return mc.compare(x,a) *  mc.compare(y,a) <=0;
	}
	
	public static boolean sameSignum(int x,int y) {
		if(x >0) {
			return y>0;
		}else if(x == 0) {
			return y == 0;
		}else {//x<0
			return y<0;
		}
	}
	
	public static int signum(int x) {
		return Integer.compare(x, 0);
	}

	public static int signum(double x){
		return Double.compare(x,0);
	}
	
	/**
	 * Determines whether {@code x} is closer to zero than {@code y}(or equal),
	 * in other words returns {@code Math.abs(x) <= Math.abs(y)}
	 * @param x
	 * @param y
	 * @return
	 */
	public static boolean closerToZero(double x,double y) {
		return Math.abs(x) <= Math.abs(y);
	}
	
	/**
	 * Returns the so-called 'Tschebyscheff distance':
	 * {@code max(abs(x1-x2), abs(y1-y2))}
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double tschebyscheffDistance(double x1,double y1,double x2,double y2) {
		return Math.max(Math.abs(x1-x2), Math.abs(y1-y2));
	}
	
	/**
	 * Returns the distance of (x1,y2) and (x2,y2) defined in space Lp, whihc is equal to
	 * <pre>(abs(x1-x2)^p+abs(y1-y2)^p)^(1/p)</pre>
	 * If {@code p==Double.Positive}
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @param p
	 * @return
	 */
	public static double distanceP(double x1,double y1,double x2,double y2,double p) {
		if(p==Double.POSITIVE_INFINITY) {
			return tschebyscheffDistance(x1, y1, x2, y2);
		}
		if(p<=0) {
			throw new IllegalArgumentException("p<=0");
		}
		double dx = Math.abs(x1-x2),
				dy = Math.abs(y1-y2);
		return Math.pow(Math.pow(dx, p)+Math.pow(dy, p),1/p);
		
	}
	
	/**
	 * Returns the biggest number n that meets the requirements that:
	 * {@code n = k*p} where {@code k} is an integer,  
	 * {@code n <= x}.
	 * @param x a number
	 * @param p a positive number
	 * @return
	 */
	public static double maxBelow(double x,double p) {
		p = Math.abs(p);
		if(x <0) {
			double t = x%p;
			if(t == 0) {
				return x;
			}
			return x - t - p;
		}else {
			return x - x%p;
		}
	}
	
	/**
	 * Returns the biggest integer k that meets the requirements that:
	 * {@code n = k*p} and
	 * {@code n <= x}.
	 * @param x a positive number
	 * @param p a positive number
	 * @return
	 */
	public static long maxBelowK(double x,double p) {
		if(p > x) {
			return 0;
		}
		long n = 1;
		double t = p;
		while(t < x) {
			 t *= 2;
			 n = n*2;
		}
		long d = n/2;
		for(;n>d;n--) {
			if(t <= x) {
				return n;
			}
			t -= p;
		}
		return d;
	}
	/**
	 * Returns the number of factors of the integer.
	 * @return a positive integer
	 */
	public static long factorCount(long n) {
		long[][] factors = factorReduce(n);
		long num = 1;
		for(int i=0;i<factors.length;i++) {
			num*= factors[i][1]+1;
		}
		return num;
	}
	
	/**
	 * Returns a two-dimension array representing the 
	 * number's prime factors and the corresponding times.
	 * <P>For example, <text> factorReduce(6)={{2,1},{3,1}} </text>
	 * @param n
	 * @return
	 */
	public static long[][] factorReduce(long n) {
		Primes pr = Primes.getInstance();
		long[] primes = pr.getPrimesBelow(n/2+1);
		long[][] factors = new long[primes.length/4+1][];
		int count=0;
		for(long p : primes) {
			if(n % p == 0) {
				long[] pair = new long[] {p,0};
				factors = ArraySup.ensureCapacityAndAdd(factors, pair,count);
				do {
					pair[1]++;
					n = n/p;
				}while(n%p==0);
				count++;
			}
		}
		if(factors.length>count) {
			factors = Arrays.copyOf(factors, count);
		}
		return factors;
	}


	public static void main(String[] args) {
		MathObject mo = null;
	}
}
