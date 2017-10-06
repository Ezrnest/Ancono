package cn.timelives.java.math.numberModels;
import static cn.timelives.java.utilities.Printer.print;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.timelives.java.math.MathFunctions;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.linearAlgebra.Matrix;

/**
 * A simple class that provides fractional calculation which means unless either numerator or denominator 
 * is out of range of long, no precision will be lost.This class provides some math calculation with satisfying 
 * results,as well normal time-performance.This class is used by {@link Matrix} as number's format.
 * @author lyc
 *
 */
public class Fraction extends Number implements Comparable<Fraction>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8236721041547778971L;
	/**
	 * The numerator and denominator of this fraction,
	 * which must be each-prime.
	 * Also make sure that denominator != 0
	 */
	protected final long numerator,denominator;
	public long getNumerator() {
		return numerator;
	}

	public long getDenominator() {
		return denominator;
	}


	public int getSignum() {
		return signum;
	}

	/**
	 * The sign number of this fraction,1 for positive,
	 * 0 for and only for zero,and -1 for negative.
	 */
	protected final int signum;
	
	/**
	 * A Fraction representing {@code 0} with zero as numerator , 
	 * one as denominator and zero for sign number. 
	 */
	public static final Fraction ZERO = new Fraction(0,1,0);
	/**
	 * A Fraction representing {@code 1}.
	 */
	public static final Fraction ONE = new Fraction(1,1,1);
	/**
	 * A Fraction representing {@code -1}
	 */
	public static final Fraction NEGATIVE_ONE = new Fraction(1,1,-1);
	
	
	
	//numerator,denominator
	/**
	 * A constructor without checking num and den.
	 * @param num
	 * @param den
	 */
	Fraction(long num,long den,int signum){
		if(den==0)
			throw new IllegalArgumentException("Zero for denominator");
		this.numerator = num;
		this.denominator = den;
		this.signum = signum;
	}
	
	
	
	
	@Override
	public int intValue() {
		return signum *(int) (numerator/denominator);
	}

	@Override
	public long longValue() {
		long value = numerator /  denominator;
		return signum > 0 ? value : 
				signum == 0 ? 0L : -value;
	}

	@Override
	public float floatValue() {
		float value = (float)numerator / (float) denominator;
		return signum > 0 ? value : 
				signum == 0 ? 0f : -value;
	}

	@Override
	public double doubleValue() {
		double value = (double)numerator / (double) denominator;
		return signum > 0 ? value : 
				signum == 0 ? 0d : -value;
	}
	
	
	private static long[] gcdNumAndDen(long num,long den){
		long[] re = new long[2];
		long g = MathFunctions.gcd(num, den);
		re[0] = num / g;
		re[1] = den / g;
		return re;
	}
	/**
	 * Return the value of {@code this * num}
	 * @param num multiplier
	 * @return {@code this * num}
	 */
	public Fraction multiply(long num){
		if(num==0){
			return ZERO;
		}
		int signum = this.signum;
		if(num<0){
			num = -num;
			signum = -signum;
		}
		
		
		//to prevent potential overflow,simplify num and den
		long[] dAn = gcdNumAndDen(denominator,num);
		long nNum = dAn[1] * numerator;
		//new numerator
		return new Fraction(nNum,dAn[0],signum);
	}
	
	/**
	 * Return the value of {@code this / num}
	 * @param num divider,zero is not allowed.
	 * @return {@code this / num}
	 * @throws IllegalArgumentException if fra == 0.
	 */
	public Fraction divide(long num){
		if(num==0){
			throw new IllegalArgumentException("Divide by zero :  / 0");
		}
		int signum = this.signum;
		if(num<0){
			num = -num;
			signum = -signum;
		}
		
		
		//to prevent potential overflow,simplify num and den
		long[] nAn = gcdNumAndDen(numerator,num);
		long nDen = nAn[1] * denominator;
		//new numerator
		return new Fraction(nAn[0],nDen,signum); 
	}
	
	/**
	 * Return the value of {@code -this }
	 * @return {@code -this }
	 */
	public Fraction negative(){
		if(this.signum==0)
			return ZERO;
		return new Fraction(this.numerator,this.denominator,-this.signum);
	}
	
	/**
	 * Return the value of {@code 1/this}
	 * @return {@code 1/this}
	 * @throws IllegalArgumentException if this == 0.
	 */
	public Fraction reciprocal(){
		if(this.signum==0){
			throw new ArithmeticException("Zero to reciprocal");
		}
		return new Fraction(this.denominator,this.numerator,this.signum);
	}
	/**
	 * Return the value of {@code this * fra}
	 * @param fra another fraction
	 * @return {@code this * fra}
	 */
	public Fraction multiply(Fraction fra){
		if(this.signum == 0 || fra.signum ==0 ){
			return ZERO;
		}
		
		long[] n1D2 = gcdNumAndDen(this.numerator, fra.denominator);
		long[] n2D1 = gcdNumAndDen(fra.numerator, this.denominator);
		return new Fraction(
				n1D2[0]*n2D1[0],
				n1D2[1]*n2D1[1],
				this.signum == fra.signum ? 1 : -1);
	}
	/**
	 * Return the value of {@code this / fra}
	 * @param fra divider
	 * @return {@code this / fra}
	 * @throws IllegalArgumentException if fra == 0.
	 */
	public Fraction divide(Fraction fra){
		if(fra.signum==0){
			throw new IllegalArgumentException("Divide by zero :  / 0");
		}
		if(this.signum == 0){
			return ZERO;
		}
		//exchange fra's numerator and denominator .
		long[] n1D2 = gcdNumAndDen(this.numerator, fra.numerator);
		long[] n2D1 = gcdNumAndDen(fra.denominator, this.denominator);
		return new Fraction(
				n1D2[0]*n2D1[0],
				n1D2[1]*n2D1[1],
				this.signum == fra.signum ? 1 : -1);
	}
	
	/**
	 * Return the value of {@code this + num}
	 * @param num a number 
	 * @return {@code this + num}
	 */
	public Fraction add(long num){
		if((num > 0 && signum <0 )||
				(num <0 && signum >0)){
			num = - num; 
		}
		long nNum = numerator + num * denominator;
		int signum = this.signum;
		if(nNum < 0){
			signum = -signum;
			nNum = - nNum;
		}
		return new Fraction(nNum,denominator,signum);
	}
	/**
	 * Return the value of {@code this - num}
	 * @param num a number 
	 * @return {@code this - num}
	 */
	public Fraction minus(long num){
		if((num < 0 && signum <0 )||
				(num >0 && signum >0)){
			num = - num; 
		}
		long nNum = numerator + num * denominator;
		int signum = this.signum;
		if(nNum < 0){
			signum = -signum;
			nNum = - nNum;
		}
		return new Fraction(nNum,denominator,signum);
	}
	/**
	 * Return the value of {@code this + frac}
	 * @param frac a fraction 
	 * @return {@code this + frac}
	 */
	public Fraction add(Fraction frac){
		long num = this.signum * this.numerator * frac.denominator +
					frac.signum * frac.numerator * this.denominator;
		if(num==0){
			return ZERO;
		}
		int signum;
		if(num<0){
			num = - num;
			signum = -1;
		}else{
			signum = 1;
		}
		long den = this.denominator * frac.denominator;
		long[] nAd = gcdNumAndDen(num, den);
		return new Fraction(nAd[0],nAd[1],signum);
	}
	
	
	
	/**
	 * Return the value of {@code this - frac}
	 * @param frac a fraction 
	 * @return {@code this - frac}
	 */
	public Fraction minus(Fraction frac){
		long num = this.signum * this.numerator * frac.denominator -
					frac.signum * frac.numerator * this.denominator;
		if(num==0){
			return ZERO;
		}
		long den = this.denominator * frac.denominator;
		int signum;
		if(num<0){
			num = - num;
			signum = -1;
		}else{
			signum = 1;
		}
		long[] nAd = gcdNumAndDen(num, den);
		return new Fraction(nAd[0],nAd[1],signum);
	}
	/**
	 * Return the value of this^n while n is an integer.This method is generally faster 
	 * than using {@link #multiply(Fraction)} because no GCD calculation will be done.
	 * <p><b>Attention:</b> this method does NOT check underflow or overflow , so please notice the range of {@code n}
	 * @param n
	 * @return
	 * @throws ArithmeticException if this == 0 and n <=0
	 */
	public Fraction pow(int n){
		if(signum==0){
			if(n==0){
				throw new ArithmeticException("0^0");
			}else{
				return Fraction.ZERO;
			}
		}else{
			if(n==0){
				return Fraction.ONE;
			}
			int sign ;
			long deno,nume;
			if(n<0){
				sign = (n & 1) == 1 ? -1 : 1;
				
				//exchange two 
				deno = MathFunctions.power(numerator, n);
				nume = MathFunctions.power(denominator, n);
			} else {
				sign = 1;
				nume = MathFunctions.power(numerator, n);
				deno = MathFunctions.power(denominator, n);
			}
			
			return new Fraction(nume,deno,sign);
		}
	}
	
	/**
	 * Returns {@code this^exp}.{@code exp} can have a denominator ,which means 
	 * the method will calculate the n-th root of {@code this},but this method will 
	 * only return the positive root if there are two roots.<p>
	 * This method will throw ArithmeticException if such 
	 * operation cannot be done in Fraction.
	 * @param exp an exponent
	 * @return the result of {@code this^exp}
	 */
	public Fraction exp(Fraction exp) {
		
		if(exp.signum==0){
			if(this.signum==0){
				throw new ArithmeticException("0^0");
			}
			return Fraction.ONE;
			
		}
		if(this.signum==0 ){
			return Fraction.ZERO;
		}else if(this.numerator==1L && this.denominator==1L){
			// +- 1
			if(this.signum==1){
				return Fraction.ONE;
			}else{
				if(exp.denominator%2 == 0){
					throw new ArithmeticException("Negative in Square");
				}
				return Fraction.NEGATIVE_ONE;
			}
		}
		int signum = 1;
		if(this.signum< 0 ){
			if(exp.denominator % 2 ==0)
				throw new ArithmeticException("Negative in Square");
			signum = -1;
		}
		
		boolean swap = false;
		if(exp.signum<0){
			swap = true;
		}
		//we first check whether the Fraction b has a denominator
		if(exp.numerator>Integer.MAX_VALUE || exp.denominator > Integer.MAX_VALUE){
			throw new ArithmeticException("Too big in exp");
		}
		int bn = (int)exp.numerator;
		int bd = (int)exp.denominator;
		//try it
		long an = this.numerator;
		long ad = this.denominator;
		
		an = MathFunctions.rootN(an, bd);
		ad = MathFunctions.rootN(ad, bd);
		if(an==-1 || ad==-1){
			throw new ArithmeticException("Cannot Find Root");
		}
		an = MathFunctions.power(an, bn);
		ad = MathFunctions.power(ad, bn);
		if(swap){
			return new Fraction(ad,an,signum);
		}
		return new Fraction(an, ad, signum);
	}
	
	/**
	 * Return {@code this^2}.The fastest and most convenient way to do this 
	 * calculation.
	 * @return this^2
	 */
	public Fraction squareOf(){
		if(signum==0){
			return Fraction.ZERO;
		}
		return new Fraction(numerator*numerator,
				denominator*denominator,1);
	}
	
	
	public static Fraction valueOf(long number){
		if(number==0){
			return ZERO;
		}else if(number == 1){
			return ONE;
		}else if(number == -1){
			return NEGATIVE_ONE;
		}else if(number>0){
			return new Fraction(number,1,1);
		}else{
			return new Fraction(-number,1,-1);
		}
	}
	/**
	 * Return a fraction representing the value of numerator/denominator,proper reduction
	 *  will be done.
	 * @param numerator
	 * @param denominator
	 * @return
	 */
	public static Fraction valueOf(long numerator,long denominator){
		
		if(numerator==0){
			return ZERO;
		}
		int signum = 1;
		if(numerator<0){
			numerator = - numerator;
			signum = -signum;
		}
		if(denominator<0){
			denominator = - denominator;
			signum = -signum;
		}
		
		long[] nAd = gcdNumAndDen(numerator, denominator);
		return new Fraction(nAd[0],nAd[1],signum);
	}
	
	private static final int maxPrecision = (int) Math.log10(Long.MAX_VALUE) -1;
	
	/**
	 * Return a fraction that is closet to the value of {@code d} but is small than {@code d},
	 * the returned fraction's both numerator and denominator are smaller than 
	 * 10<sup>{@code precision}</sup>.
	 * @param d a number 
	 * @return a fraction
	 */
	public static Fraction valueOfDouble(double d , int precision){
		if(precision<=0 || precision > maxPrecision){
			throw new IllegalArgumentException("Bad precision:"+precision);
		}
		int signum ;
		if(d>0){
			signum = 1;
		}else if(d==0){
			return ZERO;
		}else{
			signum = -1;
			d = -d;
		}
		long deno = MathFunctions.power(10L, precision-1);
		double bound = deno;
//		deno*= 10L;
		while(d < bound){
			d *= 10d;
		}
		long nume = (long) d;
		long[] nAd = gcdNumAndDen(nume, deno);
		return new Fraction(nAd[0],nAd[1],signum);
	}
	
	
	/**
	 * Identify the given expression
	 */
	public static final Pattern EXPRESSION_PATTERN = Pattern.compile("[\\+\\-]?\\d+(\\/\\d+)?");
	// *([\\+\\-]?\\d+(\\/\\d+)?) * another replacement which 
	/**
	 * Return a fraction representing the value of the given expression.The text given should be like :
	 * {@code "[\\+\\-]?\\d+(\\/\\d+)?"} as regular expression
	 * @param exp
	 * @return
	 */
	public static Fraction valueOf(String expr){
		Matcher m = EXPRESSION_PATTERN.matcher(expr);
		if(m.matches()){
			String[] nAd = expr.split("/");
			long l1 = Long.parseLong(nAd[0]);
			if(nAd.length>1){
				
				long l2=Long.parseLong(nAd[1]);
				return valueOf(l1,l2);
			}else{
				return valueOf(l1);
			}
		}
		throw new IllegalArgumentException("Illegal Fraction:"+expr);
		
	}
	
	
	
	/**
	 * Return the String expression of this fraction.
	 */
	@Override
	public String toString(){
		if(signum==0){
			return "0";
		}
		if(denominator==1){
			return signum < 0 ? "-"+Long.toString(numerator) 
					:  Long.toString(numerator);
		}
		StringBuilder sb = new StringBuilder();
		if(signum<0)
			sb.append('-');
		sb.append(numerator).append('/').append(denominator);
		return sb.toString();
	}
	@Override
	public int hashCode() {
		long hash = signum * denominator;
		hash = hash * 31 + numerator;
		return (int)((hash>>>32) ^ hash);
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Fraction){
			Fraction f = (Fraction) obj;
			return (f.signum == this.signum &&
					f.numerator == this.numerator && 
					f.denominator == this.denominator);
		}
		return false;
	}
	/**
	 * Compare two fractions , return -1 if this fraction is smaller than f,0 if equal,or 1
	 * if this fraction is bigger than f.The method is generally equal to return {@code sgn(this-frac)}
	 * @return -1,0 or 1 if this is smaller than,equal to or bigger than f. 
	 */
	@Override
	public int compareTo(Fraction frac) {
		long num = this.signum * this.numerator * frac.denominator -
				frac.signum * frac.numerator * this.denominator;
		return num > 0 ? 1 
				: num ==0 ? 0 : -1;
		
	}
	/**
	 * Return 1 number , 0 , -1 number if the given fraction is bigger than , equal to or smaller than {@code n}.
	 * @param f a number as Fraction 
	 * @param n a number
	 * @return a positive number , 0 , a negative number if the given fraction is bigger than , equal to or smaller than {@code n}.
	 */
	public static int compareFraction(Fraction f , long n){
		return Long.signum(f.signum * f.numerator - f.denominator * n );
	}
	/**
	 * Return 1 , 0 , -1 if the given fraction is bigger than , equal to or smaller than {@code n}.
	 * @param f a number as Fraction 
	 * @param n a number
	 * @return a positive number , 0 , a negative number if the given fraction is bigger than , equal to or smaller than {@code n}.
	 */
	public static int compareFraction(Fraction f , double n){
		double d = f.doubleValue() - n;
		return d < 0 ? -1 : d==0 ? 0 : 1;
	}
	
	
	
	
	
	public static void main(String[] args) {
		
		Fraction f = valueOfDouble(3.544d,4);
		System.out.println(new BigDecimal(1.2d));
		print(f);
		print((double)f.numerator/f.denominator);
	}
	
	/**
	 * Get the calculator of the class Fraction,the calculator ignores overflow.
	 * <p>The calculator does not have any constant values.
	 * @return
	 */
	public static FractionCalculator getCalculator(){
		return FractionCalculator.cal;
	}
	
	
	
	

	public static class FractionCalculator extends MathCalculatorAdapter<Fraction>{
		static final FractionCalculator cal = new FractionCalculator();
		/* (non-Javadoc)
		 * @see cn.timelives.java.utilities.math.MathCalculator#getNumberClass()
		 */
		@Override
		public Class<Fraction> getNumberClass() {
			return Fraction.class;
		}
		@Override
		public boolean isEqual(Fraction para1, Fraction para2) {
			return para1.equals(para2);
		}

		@Override
		public int compare(Fraction para1, Fraction para2) {
			return para1.compareTo(para2);
		}

		@Override
		public Fraction add(Fraction para1, Fraction para2) {
			return para1.add(para2);
		}

		@Override
		public Fraction negate(Fraction para) {
			return para.negative();
		}

		@Override
		public Fraction abs(Fraction para) {
			return new Fraction(para.numerator,para.denominator,1);
		}

		@Override
		public Fraction subtract(Fraction para1, Fraction para2) {
			return para1.minus(para2);
		}

		@Override
		public Fraction getZero() {
			return ZERO;
		}
		@Override
		public boolean isZero(Fraction para) {
			return ZERO.equals(para);
		}

		@Override
		public Fraction multiply(Fraction para1, Fraction para2) {
			return para1.multiply(para2);
		}

		@Override
		public Fraction divide(Fraction para1, Fraction para2) {
			return para1.divide(para2);
		}

		@Override
		public Fraction getOne() {
			return ONE;
		}

		@Override
		public Fraction reciprocal(Fraction p) {
			return p.reciprocal();
		}

		@Override
		public Fraction multiplyLong(Fraction p, long l) {
			return p.multiply(l);
		}

		@Override
		public Fraction divideLong(Fraction p, long l) {
			return p.divide(l);
		}

		@Override
		public Fraction squareRoot(Fraction p) {
			if(p.signum==0){
				return Fraction.ZERO;
			}else if(p.signum>0){
				long noe = MathFunctions.squareRootExact(p.numerator);
				long deo = MathFunctions.squareRootExact(p.denominator);
				if(noe!=-1&&deo!=-1){
					return new Fraction(noe,deo,1);
				}
			}
			
			throw new UnsupportedCalculationException();
		}

		@Override
		public Fraction pow(Fraction p, long exp) {
			if(p.signum==0){
				return exp == 0 ? Fraction.ONE : Fraction.ZERO;
			}
			int signum = exp % 2 == 0 ? 1 : p.signum;
			if( p.denominator == 1 && p.numerator == 1){
				return signum == p.signum ? p : p.negative();
			}
			if(exp == 0){
				return Fraction.ONE;
			}
			long no,de;
			if(exp < 0){
				exp =  - exp;
				no = p.denominator;
				de = p.numerator;
			}else{
				no = p.numerator;
				de = p.denominator;
			}
			long noR = 1l , deR = 1l;
			while(exp!=0){
				if((exp&1)!=0){
					noR*=no;
					deR*=de;
				}
				no*=no;
				de*=de;
				exp>>=1;
			}
			
			
			return new Fraction(noR, deR, signum);
		}

		@Override
		public Fraction constantValue(String name) {
			throw new UnsupportedCalculationException("No constant value avaliable");
		}

		@Override
		public Fraction exp(Fraction a, Fraction b) {
			
			if(b.signum==0){
				if(a.signum==0){
					throw new ArithmeticException("0^0");
				}
				return Fraction.ONE;
				
			}
			if(a.signum==0 ){
				return Fraction.ZERO;
			}else if(a.numerator==1L && a.denominator==1L){
				// +- 1
				if(a.signum==1){
					return Fraction.ONE;
				}else{
					if(b.denominator%2 == 0){
						throw new ArithmeticException("Negative in Square");
					}
					return Fraction.NEGATIVE_ONE;
				}
			}
			int signum = 1;
			if(a.signum< 0 ){
				if(b.denominator % 2 ==0)
					throw new ArithmeticException("Negative in Square");
				signum = -1;
			}
			
			boolean swap = false;
			if(b.signum<0){
				swap = true;
			}
			//we first check whether the Fraction b has a denominator
			if(b.numerator>Integer.MAX_VALUE || b.denominator > Integer.MAX_VALUE){
				throw new UnsupportedCalculationException("Too big in exp");
			}
			int bn = (int)b.numerator;
			int bd = (int)b.denominator;
			//try it
			long an = a.numerator;
			long ad = a.denominator;
			
			an = MathFunctions.rootN(an, bd);
			ad = MathFunctions.rootN(ad, bd);
			if(an==-1 || ad==-1){
				throw new UnsupportedCalculationException("Cannot Find Root");
			}
			an = MathFunctions.power(an, bn);
			ad = MathFunctions.power(ad, bn);
			if(swap){
				return new Fraction(ad,an,signum);
			}
			return new Fraction(an, ad, signum);
		}
	}

	/**
	 * Get the Simplifier of the class Fraction,this simplifier will take the input numbers 
	 * as coefficient of a equation and multiply or divide them with a factor that makes them 
	 * all become integer values.The first fraction will be ensure to be positive.
	 * <p>This simplifier will ignore overflows.
	 * @return a simplifier
	 */
	public static Simplifier<Fraction> getFractionSimplifier(){
		return fs;
	}
	
	static final FractionSimplifier fs = new FractionSimplifier();
	
	static class FractionSimplifier implements Simplifier<Fraction>{
		private FractionSimplifier() {
		}
		
		@Override
		public List<Fraction> simplify(List<Fraction> numbers) {
			//first find the GCD of numerator and LCM of denominator.
			final int len = numbers.size();
			long[] numes = new long[len];
			long[] denos = new long[len];
			int[] signs = new int[len];
			int i = 0;
			for(Iterator<Fraction> it = numbers.listIterator();it.hasNext();i++){
				Fraction f = it.next();
				numes[i] = f.numerator;
				denos[i] = f.denominator;
				signs[i] = f.signum;
			}
			long gcd = MathFunctions.gcd(numes);
			long lcm = MathFunctions.lcm(denos);
//			Printer.print(lcm);
			for(i=0;i<len;i++){
				numes[i] = numes[i] / gcd * (lcm/denos[i]);
			}
			//denos are all set to one.
			if(signs[0]==-1){
				for(i=0;i<len;i++){
					signs[i] = -signs[i];
				}
			}
			List<Fraction> list = new ArrayList<Fraction>(len);
			for(i=0;i<len;i++){
				list.add(new Fraction(numes[i], 1L, signs[i]));
			}
			return list;
		}
	}
}
