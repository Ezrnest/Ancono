package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.linearAlgebra.Matrix;
import cn.timelives.java.utilities.ArraySup;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
	/**
	 * Gets the numerator of this Fraction.
	 * @return numerator
	 */
	public long getNumerator() {
		return numerator;
	}
	/**
	 * Gets the denominator of this Fraction.
	 * @return denominator
	 */
	public long getDenominator() {
		return denominator;
	}

	/**
	 * Gets the sign number of this Fraction.
	 * @return sign number
	 */
	public int getSignum() {
		return signum;
	}

	/**
	 * Determines whether this fraction is an integer.
	 * @return
	 */
	public boolean isInteger(){
		return denominator == 1L;
	}

	public boolean isNegative(){
		return signum<0;
	}
	public boolean isPositive(){
		return signum>0;
	}

	public boolean isZero(){return signum==0;}
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
		long g = MathUtils.gcd(num, den);
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
		
		
		//to prevent potential overflow, simplify num and den
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
		long den = MathUtils.lcm(denominator,frac.denominator);
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
				deno = MathUtils.power(numerator, n);
				nume = MathUtils.power(denominator, n);
			} else {
				sign = 1;
				nume = MathUtils.power(numerator, n);
				deno = MathUtils.power(denominator, n);
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
		
		an = MathUtils.rootN(an, bd);
		ad = MathUtils.rootN(ad, bd);
		if(an==-1 || ad==-1){
			throw new ArithmeticException("Cannot Find Root");
		}
		an = MathUtils.power(an, bn);
		ad = MathUtils.power(ad, bn);
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
	/**
	 * Returns a {@code Fraction} whose value is the integer part
	 * of the quotient {@code (this / divisor)} rounded down.
	 *
	 * @param  divisor value by which this {@code Fraction} is to be divided.
	 * @return The integer part of {@code this / divisor}.
	 * @throws ArithmeticException if {@code divisor==0}
	 */
	public Fraction divideToIntegralValue(Fraction divisor){
		if(signum==0){
			return ZERO;
		}
		Fraction re = this.divide(divisor);
		return Fraction.valueOf(re.longValue());

	}

	public Fraction[] divideAndRemainder(Fraction divisor){
		Fraction[] result = new Fraction[2];

		result[0] = this.divideToIntegralValue(divisor);
		result[1] = this.minus(result[0].multiply(divisor));
		return result;
	}

	/**
	 * Returns a {@code Fraction} whose value is {@code (this % divisor)}.
	 *
	 * <p>The remainder is given by
	 * {@code this.subtract(this.divideToIntegralValue(divisor).multiply(divisor))}.
	 * Note that this is <em>not</em> the modulo operation (the result can be
	 * negative).
	 *
	 * @param  divisor value by which this {@code Fraction} is to be divided.
	 * @return {@code this % divisor}.
	 * @throws ArithmeticException if {@code divisor==0}
	 */
	public Fraction remainder(Fraction divisor) {
		Fraction divrem[] = this.divideAndRemainder(divisor);
		return divrem[1];
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

	/**
	 * Returns a String representation of this fraction, adds brackets if this
	 * fraction is not an integer. This method can be used to eliminate confusion
	 * when this fraction is a part of an expression.
	 * @return a string
	 */
	public String toStringWithBracket(){
		if(signum==0){
			return "0";
		}
		if(isInteger()){
			return signum < 0 ? "-"+Long.toString(numerator)
					:  Long.toString(numerator);
		}
		StringBuilder sb = new StringBuilder('(');
		if(signum<0)
			sb.append('-');
		sb.append(numerator).append('/').append(denominator);
		sb.append(')');
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
//			if(f.signum == 0 && this.signum == 0) {
//				return true;
//			}
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
		long deno = MathUtils.power(10L, precision-1);
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
	 * Returns the best approximate fraction of the double number. The numerator and 
	 * the denominator of the fraction are both smaller than {@code bound}.
	 * @param x a number
	 * @param bound the bound of the fraction, must be at least one.
	 * @return
	 */
	public static Fraction bestApproximate(double x,long bound) {
		if(bound < 1) {
			throw new IllegalArgumentException("Bad bound: "+bound);
		}
		int signum;
		if(x>0){
			signum = 1;
		}else if(x==0){
			return ZERO;
		}else{
			signum = -1;
			x = -x;
		}
		long[] es = new long[4];
		long[] f = null;
		long m = 1;
		double y = 1;
		int i = 0;
		while(true) {
			double reminder =  x % y;
			long l = Math.round((x - reminder)/y);
			x = y;
			y = reminder;
			
			long t = m*l;
			if(t > bound ||  t < 0 ||  Double.isNaN(y)) {
				break;
			}
			m = t;
			es = ArraySup.ensureCapacityAndAdd(es, l, i);
			long[] ft = computeContinousFraction(es, i);
			if(Math.max(ft[0], ft[1]) > bound || ft[0]< 0 || ft[1] <0) {
				break;
			}
			i++;
			f = ft;
		}
		if(f == null) {
			return Fraction.ZERO;
		}
		return new Fraction(f[0],f[1],signum);
	}
	
	private static long[] computeContinousFraction(long[] array,int index) {
		long nume = array[index];
		long deno = 1;
		
		for(index--;index>-1;index--) {
			long nn = array[index] * nume +deno;
			long nd = nume;
			nume = nn;
			deno = nd;
		}
		return new long[] {nume,deno};
	}
	
	/**
	 * Identify the given expression
	 */
	public static final Pattern EXPRESSION_PATTERN = Pattern.compile("[\\+\\-]?\\d+(\\/\\d+)?");
	// *([\\+\\-]?\\d+(\\/\\d+)?) * another replacement which 
	/**
	 * Return a fraction representing the value of the given expression.The text given should be like :
	 * {@code "[\\+\\-]?\\d+(\\/\\d+)?"} as regular expression
	 * @param expr the expression
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
		throw new NumberFormatException("Illegal Fraction:"+expr);
		
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
	
	
	
	
	
//	public static void main(String[] args) {
////		print(computeContinousFraction(new long[] {2,3,3,11,2}, 4));
////		Fraction f = bestApproximate(M,10);
//		print(f);
////		print((double)f.numerator/f.denominator);
//	}
	
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
		public boolean isComparable() {
			return true;
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
				long noe = MathUtils.squareRootExact(p.numerator);
				long deo = MathUtils.squareRootExact(p.denominator);
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
			
			an = MathUtils.rootN(an, bd);
			ad = MathUtils.rootN(ad, bd);
			if(an==-1 || ad==-1){
				throw new UnsupportedCalculationException("Cannot Find Root");
			}
			an = MathUtils.power(an, bn);
			ad = MathUtils.power(ad, bn);
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
			long gcd = MathUtils.gcd(numes);
			long lcm = MathUtils.lcm(denos);
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
