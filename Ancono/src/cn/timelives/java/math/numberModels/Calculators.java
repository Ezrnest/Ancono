/**
 * 2017-09-22
 */
package cn.timelives.java.math.numberModels;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;

/**
 * Provides some utility methods for {@link MathCalculator}
 * @author liyicheng
 * 2017-09-22 20:35
 *
 */
public final class Calculators {
	/**
	 * 
	 */
	private Calculators() {
	}
	/**
	 * Determines whether the two numbers are the same in sign, which means they are both positive, negative or zero. 
	 * @param x
	 * @param y
	 * @param mc
	 * @return
	 */
	public static <T> boolean isSameSign(T x,T y,MathCalculator<T> mc){
		T z = mc.getZero();
		return mc.compare(x, z) == mc.compare(y, z);
	}
	
	/**
	 * Returns the sign number of {@code x}.
	 * @param x
	 * @param mc
	 * @return
	 */
	public static <T> int signum(T x,MathCalculator<T> mc){
		return mc.compare(x, mc.getZero());
	}
	
	public static <T> boolean isPositive(T x,MathCalculator<T> mc){
		return signum(x,mc) > 0;
	}
	
	public static <T> boolean isNegative(T x,MathCalculator<T> mc){
		return signum(x,mc) < 0;
	}
	
	public static <T> T square(T x,MathCalculator<T> mc){
		return mc.multiply(x, x);
	}
	
	public static <T> T cube(T x,MathCalculator<T> mc){
		return mc.multiply(x, mc.multiply(x, x));
	}
	
	public static <T> T doubleOf(T x,MathCalculator<T> mc){
		return mc.multiplyLong(x, 2l);
	}
	
	public static <T> T half(T x,MathCalculator<T> mc){
		return mc.divideLong(x, 2l);
	}
	
	public static <T> T plus1(T x,MathCalculator<T> mc){
		return mc.add(x, mc.getOne());
	}
	public static <T> T minus1(T x,MathCalculator<T> mc){
		return mc.add(x, mc.getOne());
	}
	
	
	
	private static void throwFor() throws UnsupportedCalculationException{
		throw new UnsupportedCalculationException("Adapter");
	}
	
	private static void throwFor(String s) throws UnsupportedCalculationException{
		throw new UnsupportedCalculationException(s);
	}
	static class IntegerCalculatorExact extends IntegerCalculator{
		private static final IntegerCalculatorExact cal = new IntegerCalculatorExact();
		
		IntegerCalculatorExact(){};
		
		@Override
		public boolean isEqual(Integer para1, Integer para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(Integer para1, Integer para2) {
			return para1.compareTo(para2);
		}

		@Override
		public Integer add(Integer para1, Integer para2) {
			return Math.addExact(para1, para2);
		}

		@Override
		public Integer negate(Integer para) {
			return -para;
		}

		@Override
		public Integer abs(Integer para) {
			return Math.abs(para);
		}

		@Override
		public Integer subtract(Integer para1, Integer para2) {
			return Math.subtractExact(para1, para2);
		}

		@Override
		public Integer multiply(Integer para1, Integer para2) {
			return Math.multiplyExact(para1, para2);
		}

		@Override
		public Integer divide(Integer para1, Integer para2) {
			return para1/para2;
		}

		@Override
		public Integer multiplyLong(Integer p, long l) {
			return Math.toIntExact(p*l);
		}

		@Override
		public Integer divideLong(Integer p, long l) {
			return (int)(p/l);
		}

		@Override
		public Integer getZero() {
			return Integer.valueOf(0);
		}
		
		@Override
		public boolean isZero(Integer para){
			return Integer.valueOf(0).equals(para);
		};

		@Override
		public Integer getOne() {
			return Integer.valueOf(1);
		}

		@Override
		public Integer reciprocal(Integer p) {
			throwFor("Integer value");
			return null;
		}

		@Override
		public Integer squareRoot(Integer p) {
			return (int)Math.sqrt(p);
		}

		@Override
		public Integer pow(Integer p, long exp) {
			int n = p;
			//range check 
			if( n==0 || n == 1){
				return p;
			}
			if(n == -1){
				return exp % 2 == 0 ? Integer.valueOf(1) : Integer.valueOf(-1);
			}
			if(exp == 0){
				return Integer.valueOf(1);
			}
			if(exp >= Integer.SIZE || exp < 0 ){
				//impossible exponent
				throw new ArithmeticException("For exp:"+exp);
			}
			int ex = (int) exp;
			int re = n;
			for(int i=1;i<ex;i++){
				re = Math.multiplyExact(re, n);
			}
			return re;
		}
		
		@Override
		public Integer constantValue(String name) {
			throwFor("No constant value avaliable");
			return null;
		}
		
		@Override
		public Integer exp(Integer a, Integer b) {
			int d = a,z = b;
			
			if(z < 0){
				if(d ==1 ){
					return 1;
				}else if(d==-1){
					return (z&1)==0 ? 1 : -1;
				}
				throwFor("Negative Exp");
			}else if(z == 0){
				if(d==0){
					throw new ArithmeticException("0^0");
				}
				return 1;
			}
			// log(z)
			int re = 1;
			while (z != 0) {
				if ((z & 1) != 0) {
					re = Math.multiplyExact(re,d);
				}
				d = Math.multiplyExact(d, d);
				z >>= 1;
			}
			return Integer.valueOf(re);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter.IntegerCalculator#decrease(java.lang.Integer)
		 */
		@Override
		public Integer decrease(Integer x) {
			return Math.decrementExact(x);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter.IntegerCalculator#increase(java.lang.Integer)
		 */
		@Override
		public Integer increase(Integer x) {
			return Math.incrementExact(x);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#powerAndMod(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@Override
		public Integer powerAndMod(Integer at, Integer nt, Integer mt) {
			int a = at, n = nt, mod = mt;
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
					ans = Math.multiplyExact(a, ans)%mod;
					
				}
				a = Math.multiplyExact(a, a) % mod;
				n>>=1;
			}
			return ans;
		}
		
		
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.MathCalculator#getNumberClass()
		 */
		@Override
		public Class<Integer> getNumberClass() {
			return Integer.class;
		}
	}
	/**
	 * An implements for integer, which also implements {@link NTCalculator}.
	 * @author liyicheng
	 * 2017-09-10 12:10
	 *
	 */
	public static class IntegerCalculator extends MathCalculatorAdapter<Integer>implements NTCalculator<Integer>{
		private static final IntegerCalculator cal = new IntegerCalculator();
		
		IntegerCalculator(){};
		
		@Override
		public boolean isEqual(Integer para1, Integer para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(Integer para1, Integer para2) {
			return para1.compareTo(para2);
		}

		@Override
		public Integer add(Integer para1, Integer para2) {
			return para1+para2;
		}

		@Override
		public Integer negate(Integer para) {
			return -para;
		}

		@Override
		public Integer abs(Integer para) {
			return Math.abs(para);
		}

		@Override
		public Integer subtract(Integer para1, Integer para2) {
			return para1-para2;
		}

		@Override
		public Integer multiply(Integer para1, Integer para2) {
			return para1*para2;
		}

		@Override
		public Integer divide(Integer para1, Integer para2) {
			return para1/para2;
		}

		@Override
		public Integer multiplyLong(Integer p, long l) {
			return (int)(p*l);
		}

		@Override
		public Integer divideLong(Integer p, long l) {
			return (int)(p/l);
		}

		@Override
		public Integer getZero() {
			return Integer.valueOf(0);
		}
		
		@Override
		public boolean isZero(Integer para) {
			return Integer.valueOf(0).equals(para);
		}
		@Override
		public Integer getOne() {
			return Integer.valueOf(1);
		}
		@Override
		public Integer reciprocal(Integer p) {
			throwFor("Integer value");
			return null;
		}
		@Override
		public Integer squareRoot(Integer p) {
			return (int)Math.sqrt(p);
		}

		@Override
		public Integer pow(Integer p, long exp) {
			int n = p;
			if(exp == 0){
				return Integer.valueOf(1);
			}
			//range check 
			if( n==0 || n == 1){
				return p;
			}
			if(n == -1){
				return exp % 2 == 0 ? Integer.valueOf(1) : Integer.valueOf(-1);
			}
			if(exp < 0 ){
				//impossible exponent
				throwFor("exp = "+exp+" < 0");
			}
			int re = n;
			for(long i=1;i<exp;i++){
				re = re * n;
			}
			return re;
		}
		
		@Override
		public Integer constantValue(String name) {
			throwFor("No constant value avaliable");
			return null;
		}
		
		@Override
		public Integer exp(Integer a, Integer b) {
			int d = a,z = b;
			
			if(z < 0){
				if(d ==1 ){
					return 1;
				}else if(d==-1){
					return (z&1)==0 ? 1 : -1;
				}
				throwFor("Negative Exp");
			}else if(z == 0){
				if(d==0){
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
			return Integer.valueOf(re);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#divideToInteger(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Integer divideToInteger(Integer a, Integer b) {
			return a/b;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#mod(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Integer mod(Integer a, Integer b) {
			int x = a,y = b;
			return Math.abs(x) % Math.abs(y);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isInteger(java.lang.Object)
		 */
		@Override
		public boolean isInteger(Integer x) {
			return true;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isQuotient(java.lang.Object)
		 */
		@Override
		public boolean isQuotient(Integer x) {
			return true;
		}

		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#gcd(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Integer gcd(Integer a, Integer b) {
			return MathUtils.gcd(a,b);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#decrease(java.lang.Object)
		 */
		@Override
		public Integer decrease(Integer x) {
			return x-1;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#increase(java.lang.Object)
		 */
		@Override
		public Integer increase(Integer x) {
			return x+1;
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isEven(java.lang.Object)
		 */
		@Override
		public boolean isEven(Integer x) {
			return (x&1) == 0;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isOdd(java.lang.Object)
		 */
		@Override
		public boolean isOdd(Integer x) {
			return (x&1) != 0;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isPositive(java.lang.Object)
		 */
		@Override
		public boolean isPositive(Integer x) {
			return x>0;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#reminder(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Integer reminder(Integer a, Integer b) {
			return a%b;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#deg(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Integer deg(Integer a, Integer b) {
			return MathUtils.deg(a, b);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isExactDivide(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isExactDivide(Integer a, Integer b) {
			return a%b==0;
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#powerAndMod(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@Override
		public Integer powerAndMod(Integer a, Integer n, Integer m) {
			return MathUtils.powerAndMod(a, n, m);
		}
		
		@Override
		public Class<Integer> getNumberClass() {
			return Integer.class;
		}

		
	}
	static class LongCalculatorExact extends LongCalculator{
		private static final LongCalculatorExact cal = new LongCalculatorExact();
		
		LongCalculatorExact(){};
		
		@Override
		public boolean isEqual(Long para1, Long para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(Long para1, Long para2) {
			return para1.compareTo(para2);
		}

		@Override
		public Long add(Long para1, Long para2) {
			return Math.addExact(para1, para2);
		}

		@Override
		public Long negate(Long para) {
			return -para;
		}

		@Override
		public Long abs(Long para) {
			return Math.abs(para);
		}

		@Override
		public Long subtract(Long para1, Long para2) {
			return Math.subtractExact(para1, para2);
		}

		@Override
		public Long multiply(Long para1, Long para2) {
			return Math.multiplyExact(para1, para2);
		}

		@Override
		public Long divide(Long para1, Long para2) {
			return para1/para2;
		}

		@Override
		public Long multiplyLong(Long p, long l) {
			return Math.multiplyExact(p, l);
		}

		@Override
		public Long divideLong(Long p, long l) {
			return p/l;
		}

		private static final Long ZERO = Long.valueOf(0),
				ONE = Long.valueOf(1);

		@Override
		public Long getZero() {
			return ZERO;
		}
		
		@Override
		public boolean isZero(Long para) {
			return ZERO.equals(para);
		}
		@Override
		public Long getOne() {
			return ONE;
		}

		@Override
		public Long reciprocal(Long p) {
			throwFor();
			return null;
		}

		@Override
		public Long squareRoot(Long p) {
			return (long)Math.sqrt(p);
		}

		@Override
		public Long pow(Long p, long exp) {
			return exp0(p,exp);
		}
		
		@Override
		public Long constantValue(String name) {
			throwFor("No constant value avaliable");
			return null;
		}
		public Long exp0(long a, long b) {
			long d = a,z = b;
			
			if(z < 0l){
				if(d ==1l ){
					return 1l;
				}else if(d==-1l){
					return (z&1l)==0l ? 1l : -1l;
				}
				throwFor("Negative Exp");
			}else if(z == 0l){
				if(d==0l){
					throw new ArithmeticException("0^0");
				}
				return 1l;
			}
			// log(z)
			long re = 1l;
			while (z != 0l) {
				if ((z & 1l) != 0l) {
					re = Math.multiplyExact(re,d);
				}
				d = Math.multiplyExact(d, d);
				z >>= 1l;
			}
			return Long.valueOf(re);
		}
		
		@Override
		public Long exp(Long a, Long b) {
			return exp0(a,b);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter.IntegerCalculator#decrease(java.lang.Integer)
		 */
		@Override
		public Long decrease(Long x) {
			return Math.decrementExact(x);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter.IntegerCalculator#increase(java.lang.Integer)
		 */
		@Override
		public Long increase(Long x) {
			return Math.incrementExact(x);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#powerAndMod(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@Override
		public Long powerAndMod(Long at, Long nt, Long mt) {
			long a = at, n = nt, mod = mt;
			if(a< 0){
				throw new IllegalArgumentException("a<0");
			}
			if(mod == 1){
				return 0l;
			}
			if(a == 0 || a==1){
				return a;
			}
			long ans = 1;
			a = a % mod;
			while(n>0){
				if((n&1)==1){
					ans = Math.multiplyExact(a, ans)%mod;
					
				}
				a = Math.multiplyExact(a, a) % mod;
				n>>=1;
			}
			return ans;
		}
		
		@Override
		public Class<Long> getNumberClass() {
			return Long.class;
		}
	}
	/**
	 * An implements for long, which also implements {@link NTCalculator}.
	 * @author liyicheng
	 * 2017-09-10 12:10
	 *
	 */
	public static class LongCalculator extends MathCalculatorAdapter<Long> implements NTCalculator<Long> {
		private static final LongCalculator cal = new LongCalculator();
		
		LongCalculator(){};
		
		@Override
		public boolean isEqual(Long para1, Long para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(Long para1, Long para2) {
			return para1.compareTo(para2);
		}

		@Override
		public Long add(Long para1, Long para2) {
			return para1+para2;
		}

		@Override
		public Long negate(Long para) {
			return -para;
		}

		@Override
		public Long abs(Long para) {
			return Math.abs(para);
		}

		@Override
		public Long subtract(Long para1, Long para2) {
			return para1-para2;
		}

		@Override
		public Long multiply(Long para1, Long para2) {
			return para1*para2;
		}

		@Override
		public Long divide(Long para1, Long para2) {
			return para1/para2;
		}

		@Override
		public Long multiplyLong(Long p, long l) {
			return p * l;
		}

		@Override
		public Long divideLong(Long p, long l) {
			return p/l;
		}
		
		private static final Long ZERO = Long.valueOf(0),
									ONE = Long.valueOf(1);
		
		@Override
		public Long getZero() {
			return ZERO;
		}
		
		@Override
		public boolean isZero(Long para) {
			return ZERO.equals(para);
		}
		@Override
		public Long getOne() {
			return ONE;
		}

		@Override
		public Long reciprocal(Long p) {
			throwFor();
			return null;
		}

		@Override
		public Long squareRoot(Long p) {
			return (long)Math.sqrt(p);
		}

		@Override
		public Long pow(Long p, long exp) {
			return exp0(p,exp);
		}
		
		@Override
		public Long constantValue(String name) {
			throwFor("No constant value avaliable");
			return null;
		}
		@Override
		public Long exp(Long a, Long b) {
			return exp0(a,b);
		}
		public Long exp0(long a, long b) {
			long d = a,z = b;
			
			if(z < 0L){
				if(d ==1L ){
					return 1L;
				}else if(d==-1l){
					return (z&1L)==0l ? 1l : -1l;
				}
				throwFor("Negative Exp");
			}else if(z == 0l){
				if(d==0l){
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
			return Long.valueOf(re);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#divideToLong(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Long divideToInteger(Long a, Long b) {
			return a/b;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#mod(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Long mod(Long a, Long b) {
			long x = a,y = b;
			return Math.abs(x) % Math.abs(y);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isLong(java.lang.Object)
		 */
		@Override
		public boolean isInteger(Long x) {
			return true;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isQuotient(java.lang.Object)
		 */
		@Override
		public boolean isQuotient(Long x) {
			return true;
		}

		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#gcd(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Long gcd(Long a, Long b) {
			return MathUtils.gcd(a,b);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#decrease(java.lang.Object)
		 */
		@Override
		public Long decrease(Long x) {
			return x-1;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#increase(java.lang.Object)
		 */
		@Override
		public Long increase(Long x) {
			return x+1;
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isEven(java.lang.Object)
		 */
		@Override
		public boolean isEven(Long x) {
			return (x&1) == 0;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isOdd(java.lang.Object)
		 */
		@Override
		public boolean isOdd(Long x) {
			return (x&1) != 0;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isPositive(java.lang.Object)
		 */
		@Override
		public boolean isPositive(Long x) {
			return x>0;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#reminder(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Long reminder(Long a, Long b) {
			return a%b;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#deg(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Long deg(Long a, Long b) {
			return (long) MathUtils.deg(a, b);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isExactDivide(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isExactDivide(Long a, Long b) {
			return a%b==0;
		}
		
		
		@Override
		public Class<Long> getNumberClass() {
			return Long.class;
		}
	}
	/**
	 * An implements for BigInteger, which also implements {@link NTCalculator}.
	 * @author liyicheng
	 * 2017-09-10 12:10
	 *
	 */
	public static class BigIntegerCalculator extends MathCalculatorAdapter<BigInteger>  implements NTCalculator<BigInteger> {
		
		static final BigIntegerCalculator cal = new BigIntegerCalculator();
		
		private BigIntegerCalculator() {
		}
		
		@Override
		public boolean isEqual(BigInteger para1, BigInteger para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(BigInteger para1, BigInteger para2) {
			return para1.compareTo(para2);
		}

		@Override
		public BigInteger add(BigInteger para1, BigInteger para2) {
			return para1.add(para2);
		}

		@Override
		public BigInteger negate(BigInteger para) {
			return para.negate();
		}

		@Override
		public BigInteger abs(BigInteger para) {
			return para.abs();
		}

		@Override
		public BigInteger subtract(BigInteger para1, BigInteger para2) {
			return para1.subtract(para2);
		}
		
		
		
		@Override
		public BigInteger getZero() {
			return BigInteger.ZERO;
		}
		@Override
		public boolean isZero(BigInteger para) {
			return BigInteger.ZERO.equals(para);
		}
		@Override
		public BigInteger multiply(BigInteger para1, BigInteger para2) {
			return para1.multiply(para2);
		}

		@Override
		public BigInteger divide(BigInteger para1, BigInteger para2) {
			return para1.divide(para2);
		}

		@Override
		public BigInteger getOne() {
			return BigInteger.ONE;
		}

		@Override
		public BigInteger reciprocal(BigInteger p) {
			//impossible 
			throwFor();
			return null;
		}

		@Override
		public BigInteger multiplyLong(BigInteger p, long l) {
			return p.multiply(BigInteger.valueOf(l));
		}

		@Override
		public BigInteger divideLong(BigInteger p, long l) {
			return p.divide(BigInteger.valueOf(l));
		}

		@Override
		public BigInteger squareRoot(BigInteger p) {
			return BigInteger.valueOf((long)Math.sqrt(p.doubleValue()));
		}

		@Override
		public BigInteger pow(BigInteger p, long exp) {
			if(exp > Integer.MAX_VALUE){
				throwFor("Too big.");
			}
			return p.pow((int)exp);
		}
		
		@Override
		public BigInteger constantValue(String name) {
			throwFor();
			return null;
		}
		@Override
		public BigInteger exp(BigInteger a, BigInteger b) {
			try{
				int t = b.intValueExact();
				return a.pow(t);
			}catch(ArithmeticException ae){
				throw new UnsupportedCalculationException("Exp too big:"+b.toString());
			}
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#divideToBigInteger(java.lang.Object, java.lang.Object)
		 */
		@Override
		public BigInteger divideToInteger(BigInteger a, BigInteger b) {
			return a.divide(b);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#mod(java.lang.Object, java.lang.Object)
		 */
		@Override
		public BigInteger mod(BigInteger a, BigInteger b) {
			return a.mod(b.abs());
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isBigInteger(java.lang.Object)
		 */
		@Override
		public boolean isInteger(BigInteger x) {
			return true;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isQuotient(java.lang.Object)
		 */
		@Override
		public boolean isQuotient(BigInteger x) {
			return true;
		}

		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#gcd(java.lang.Object, java.lang.Object)
		 */
		@Override
		public BigInteger gcd(BigInteger a, BigInteger b) {
			return a.gcd(b);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#decrease(java.lang.Object)
		 */
		@Override
		public BigInteger decrease(BigInteger x) {
			return x.subtract(BigInteger.ONE);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#increase(java.lang.Object)
		 */
		@Override
		public BigInteger increase(BigInteger x) {
			return x.add(BigInteger.ONE);
		}
		
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isEven(java.lang.Object)
		 */
		@Override
		public boolean isEven(BigInteger x) {
			return !x.testBit(0);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isOdd(java.lang.Object)
		 */
		@Override
		public boolean isOdd(BigInteger x) {
			return x.testBit(0);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isPositive(java.lang.Object)
		 */
		@Override
		public boolean isPositive(BigInteger x) {
			return x.signum()>0;
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#reminder(java.lang.Object, java.lang.Object)
		 */
		@Override
		public BigInteger reminder(BigInteger a, BigInteger b) {
			return a.remainder(b);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#isExactDivide(java.lang.Object, java.lang.Object)
		 */
		@Override
		public boolean isExactDivide(BigInteger a, BigInteger b) {
			return a.mod(b).equals(BigInteger.ZERO);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.NTCalculator#powerAndMod(java.lang.Object, java.lang.Object, java.lang.Object)
		 */
		@Override
		public BigInteger powerAndMod(BigInteger a, BigInteger n, BigInteger mod) {
			return a.modPow(n, mod);
		}
		
		@Override
		public Class<BigInteger> getNumberClass() {
			return BigInteger.class;
		}
	}
	static class BigDecimalCalculator extends MathCalculatorAdapter<BigDecimal>{
		
		private MathContext mc;
		
		public static final BigDecimal PI_VALUE=
				new BigDecimal("3.1415926535897932384626433832795028",MathContext.DECIMAL128);
		public static final BigDecimal E_VALUE=
				new BigDecimal("2.7182818284590452353602874713526625",MathContext.DECIMAL128);
		
		
		public BigDecimalCalculator(MathContext mc) {
			this.mc = mc;
		}
		
		@Override
		public boolean isEqual(BigDecimal para1, BigDecimal para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(BigDecimal para1, BigDecimal para2) {
			return para1.compareTo(para2);
		}

		@Override
		public BigDecimal add(BigDecimal para1, BigDecimal para2) {
			return para1.add(para2);
		}

		@Override
		public BigDecimal negate(BigDecimal para) {
			return para.negate();
		}

		@Override
		public BigDecimal abs(BigDecimal para) {
			return para.abs();
		}

		@Override
		public BigDecimal subtract(BigDecimal para1, BigDecimal para2) {
			return para1.subtract(para2);
		}

		@Override
		public BigDecimal getZero() {
			return BigDecimal.ZERO;
		}
		
		@Override
		public boolean isZero(BigDecimal para) {
			return BigDecimal.ZERO.equals(para);
		}

		@Override
		public BigDecimal multiply(BigDecimal para1, BigDecimal para2) {
			return para1.multiply(para2);
		}

		@Override
		public BigDecimal divide(BigDecimal para1, BigDecimal para2) {
			return para1.divide(para2,mc);
		}

		@Override
		public BigDecimal getOne() {
			return BigDecimal.ONE;
		}

		@Override
		public BigDecimal reciprocal(BigDecimal p) {
			return BigDecimal.ONE.divide(p,mc);
		}

		@Override
		public BigDecimal multiplyLong(BigDecimal p, long l) {
			return p.multiply(BigDecimal.valueOf(l));
		}

		@Override
		public BigDecimal divideLong(BigDecimal p, long l) {
			return p.divide(BigDecimal.valueOf(l));
		}

		@Override
		public BigDecimal squareRoot(BigDecimal p) {
			return BigDecimal.valueOf(Math.sqrt(p.doubleValue()));
		}

		@Override
		public BigDecimal pow(BigDecimal p, long exp) {
			if(exp >= Integer.MAX_VALUE){
				throwFor("Too big.");
			}
			return p.pow((int)exp);
		}
		@Override
		public BigDecimal constantValue(String name) {
			if(name.equalsIgnoreCase(STR_PI)){
				return PI_VALUE;
			}
			if(name.equalsIgnoreCase(STR_E)){
				return E_VALUE;
			}
			throwFor("No constant value avaliable");
			return null;
		}
		/**
		 * This method only provides accuracy of double and throws exception if the number is too big.
		 * @param a
		 * @param b
		 * @return
		 */
		@Override
		public BigDecimal exp(BigDecimal a, BigDecimal b) {
			//use exp method in double instead.
			double ad = a.doubleValue();
			double ab = b.doubleValue();
			if(ad == Double.NEGATIVE_INFINITY 
					|| ad == Double.POSITIVE_INFINITY ||
					ab == Double.NEGATIVE_INFINITY 
					|| ab == Double.POSITIVE_INFINITY){
				throw new UnsupportedCalculationException("Too big.");
			}
			return BigDecimal.valueOf(Math.pow(ad, ab));
		}
		@Override
		public Class<BigDecimal> getNumberClass() {
			return BigDecimal.class;
		}
	}
	
	static class DoubleCalculator extends MathCalculatorAdapter<Double>{
		
		private DoubleCalculator(){}
		
		static final DoubleCalculator dc = new DoubleCalculator();
		
		@Override
		public boolean isEqual(Double para1, Double para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(Double para1, Double para2) {
			return para1.compareTo(para2);
		}

		@Override
		public Double add(Double para1, Double para2) {
			return para1 + para2;
		}

		@Override
		public Double negate(Double para) {
			return - para;
		}

		@Override
		public Double abs(Double para) {
			return Math.abs(para);
		}

		@Override
		public Double subtract(Double para1, Double para2) {
			return para1 - para2 ;
		}
		
		private static final Double ZERO = Double.valueOf(0.0d);
		private static final Double ONE = Double.valueOf(1.0d);
		
		@Override
		public Double getZero() {
			return ZERO;
		}

		@Override
		public Double multiply(Double para1, Double para2) {
			return para1 * para2 ;
		}

		@Override
		public Double divide(Double para1, Double para2) {
			return para1/para2;
		}

		@Override
		public Double getOne() {
			return ONE;
		}

		@Override
		public Double reciprocal(Double p) {
			return 1 / p;
		}

		@Override
		public Double multiplyLong(Double p, long l) {
			return p * l;
		}

		@Override
		public Double divideLong(Double p, long l) {
			return p / l;
		}

		@Override
		public Double squareRoot(Double p) {
			return Math.sqrt(p);
		}

		@Override
		public Double pow(Double p, long exp) {
			return Math.pow(p, exp);
		}
		
		@Override
		public Double addX(Object... ps) {
			double sum = 0;
			for(Object d : ps){
				sum += (Double)d;
			}
			return sum;
		}
		
		@Override
		public Double multiplyX(Object... ps) {
			double sum = 1;
			for(Object d : ps){
				sum *=  (Double)d;
			}
			return sum;
		}
		
		private static final Double pi = Double.valueOf(Math.PI);
		private static final Double e = Double.valueOf(Math.E);
		
		@Override
		public Double constantValue(String name) {
			if(name.equalsIgnoreCase(STR_PI)){
				return pi;
			}
			if(name.equalsIgnoreCase(STR_E)){
				return e;
			}
			throwFor("No constant value avaliable");
			return null;
		}
		@Override
		public Double exp(Double a, Double b) {
			return Math.pow(a, b);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculatorAdapter#log(java.lang.Object, java.lang.Object)
		 */
		@Override
		public Double log(Double a, Double b) {
			return Math.log(b)/Math.log(a);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculatorAdapter#sin(java.lang.Object)
		 */
		@Override
		public Double sin(Double x) {
			return Math.sin(x);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculator#cos(java.lang.Object)
		 */
		@Override
		public Double cos(Double x) {
			return Math.cos(x);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculatorAdapter#arcsin(java.lang.Object)
		 */
		@Override
		public Double arcsin(Double x) {
			return Math.asin(x);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculator#arccos(java.lang.Object)
		 */
		@Override
		public Double arccos(Double x) {
			return Math.acos(x);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculatorAdapter#ln(java.lang.Object)
		 */
		@Override
		public Double ln(Double x) {
			return Math.log(x);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculatorAdapter#exp(java.lang.Object)
		 */
		@Override
		public Double exp(Double x) {
			return Math.exp(x);
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculator#tan(java.lang.Object)
		 */
		@Override
		public Double tan(Double x) {
			return Math.tan(x);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculator#arctan(java.lang.Object)
		 */
		@Override
		public Double arctan(Double x) {
			return Math.atan(x);
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.number_models.MathCalculatorAdapter#isZero(java.lang.Object)
		 */
		@Override
		public boolean isZero(Double para) {
			return ZERO.equals(para);
		}
		/**
		 * @see cn.timelives.java.math.numberModels.MathCalculatorAdapter#nroot(java.lang.Object, long)
		 */
		@Override
		public Double nroot(Double x, long n) {
			return Math.pow(x, 1d/n);
		}
		
		@Override
		public Class<Double> getNumberClass() {
			return Double.class;
		}
	}
	
	static class DoubleCalcualtorWithDeviation extends DoubleCalculator{
		private final double dev;
		public DoubleCalcualtorWithDeviation(double dev){
			this.dev = dev;
		}
		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.MathCalculatorAdapter.DoubleCalculator#isEqual(java.lang.Double, java.lang.Double)
		 */
		@Override
		public boolean isEqual(Double para1, Double para2) {
			double d = Math.abs(para1-para2);
			double p1 = Math.abs(para1);
			double p2 = Math.abs(para2);
			return Math.max(p1, p2)* dev >= d;
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.MathCalculator#isZero(java.lang.Object)
		 */
		@Override
		public boolean isZero(Double para) {
			return Math.abs(para) <= dev;
		}
		
		/* (non-Javadoc)
		 * @see cn.timelives.java.math.MathCalculatorAdapter.DoubleCalculator#compare(java.lang.Double, java.lang.Double)
		 */
		@Override
		public int compare(Double para1, Double para2) {
			if(isEqual(para1, para2)){
				return 0;
			}
			return super.compare(para1, para2);
		}
		
		static final DoubleCalcualtorWithDeviation dc = new DoubleCalcualtorWithDeviation(10E-10);
	}
	
	/**
	 * Return an exact calculator for Integer,all operations that will cause a overflow will 
	 * not be operated and an exception will be thrown.<p>
	 * <p>The {@link #squareRoot(Integer)}
	 * The calculator does not have any constant values.
	 * @return a MathCalculator
	 */
	public static IntegerCalculator getCalculatorIntegerExact(){
		return IntegerCalculatorExact.cal;
	}
	
	/**
	 * Return a calculator for Integer, all the basic operations are the same as {@code +,-,*,/}.Notice that this kind of 
	 * calculator will not check the value or throw any overflow exception.For example, {@code pow(2,100)} is acceptable and  
	 * the return value will be {@code -818408495}.<p>
	 * The calculator does not have any constant values.
	 * @return a MathCalculator
	 */
	public static IntegerCalculator getCalculatorInteger(){
		return IntegerCalculator.cal;
	}
	
	/**
	 * Return an exact calculator for Long,all operations that will cause a overflow will 
	 * not be operated and an exception will be thrown.<p>
	 * The calculator does not have any constant values.
	 * @return a MathCalculator
	 */
	public static LongCalculator getCalculatorLongExact(){
		return LongCalculatorExact.cal;
	}
	
	/**
	 * Return a calculator for Long, all the basic operations are the same as {@code +,-,*,/}.<p>
	 * The calculator does not have any constant values.
	 * @return a MathCalculator
	 */
	public static LongCalculator getCalculatorLong(){
		return LongCalculator.cal;
	}
	
	/**
	 * Return a calculator for {@linkplain BigInteger}.Notice that the  method {@code pow} has a limit 
	 * that {@code exp <= Integer.MAX_VALUE}.<p>
	 * The calculator does not have any constant values.
	 * @return a MathCalculator
	 */
	public static BigIntegerCalculator getCalculatorBigInteger(){
		return BigIntegerCalculator.cal;
	}
	/**
	 * Return a calculator for {@linkplain BigDecimal} with the given math context.Notice that the method 
	 * {@code pow} has a limit that {@code exp <= 999999999}<p>
	 * The calculator has {@value #STR_PI} and {@value #STR_E} as constant values,which have the rounding mode of 
	 * {@link MathContext#DECIMAL128}.
	 * @param mc a math context
	 * @return a MathCalculator
	 */
	public static MathCalculator<BigDecimal> getCalculatorBigDecimal(MathContext mc){
		return new BigDecimalCalculator(mc);
	}
	/**
	 * Return a calculator for Double.
	 * <p>The calculator has {@value #STR_PI} and {@value #STR_E} as constant values,
	 * which are the double values in Math.<p>
	 * This calculator doesn't consider the deviation of double and it 
	 * {@link MathCalculator#isEqual(Object, Object)} method is just equal to {@code d1 == d2}.
	 * @return a MathCalculator
	 */
	public static MathCalculator<Double> getCalculatorDouble(){
		return DoubleCalculator.dc;
	}
	/**
	 * Return a calculator for Double.
	 * <p>The calculator has {@value #STR_PI} and {@value #STR_E} as constant values,
	 * which are the double values in Math.<p>
	 * This calculator considers the deviation of double and it 
	 * allows a deviation of {@code 10E-10}
	 * @return a MathCalculator
	 */
	public static MathCalculator<Double> getCalculatorDoubleDev(){
		return DoubleCalcualtorWithDeviation.dc;
	}
	/**
	 * Return a calculator for Double.
	 * <p>The calculator has {@value #STR_PI} and {@value #STR_E} as constant values,
	 * which are the double values in Math.<p>
	 * This calculator considers the deviation of double.
	 * @return a MathCalculator
	 */
	public static MathCalculator<Double> getCalculatorDoubleDev(double dev){
		return new DoubleCalcualtorWithDeviation(Math.abs(dev));
	}
}
