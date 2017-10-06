/**
 * 
 */
package cn.timelives.java.math.calculus;

import java.util.function.DoubleUnaryOperator;

import cn.timelives.java.math.function.SVPFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.function.AbstractSVPFunction;

/**
 * A utility class for some calculus calculations. This class 
 * @author liyicheng
 *
 */
public final class Calculus {
	/**
	 * 
	 */
	private Calculus() {
	}
	/**
	 * Computes the integral of the function by using small rectangles to compute a 
	 * approximation. The function must be dimmed in {@code [a,b]}. The accuracy of the 
	 * result will be based on the {@code delta} given, while the function itself will 
	 * influence the accuracy of the result.  
	 * @param fx the function
	 * @param a the starting of the area 
	 * @param b the ending of the area
	 * @param delta the width of small intervals, positive 
	 * @return integral
	 */
	public static double integralApproximationLinear(DoubleUnaryOperator fx,double a,double b,double delta){
		if(delta < 0){
			throw new IllegalArgumentException("delta < 0"); 
		}
		if(a > b){
			throw new IllegalArgumentException("a > b");
		}
		return integralApp0(fx,a,b,delta);
	}
	private static final long default_division = 1000_0000;
	
	/**
	 * Computes the integral of the function by using small rectangles to compute a 
	 * approximation. The function must be dimmed in {@code [a,b]}. This method will 
	 * divide the interval to {@code 10E8}  parts.
	 * @param fx the function
	 * @param a the starting of the area 
	 * @param b the ending of the area
	 * @return integral
	 */
	public static double integralApproximationLinear(DoubleUnaryOperator fx,double a,double b){
		return integralApproximationLinear(fx,a,b,default_division);
	}
	/**
	 * Computes the integral of the function by using small rectangles to compute a 
	 * approximation. The function must be dimmed in {@code [a,b]}. This method will 
	 * divide the whole interval averagely to {@code division} parts and computes all of them. 
	 * @param fx the function
	 * @param a the starting of the area 
	 * @param b the ending of the area
	 * @param division the number of intervals to divide
	 * @return integral
	 */
	public static double integralApproximationLinear(DoubleUnaryOperator fx,double a,double b,long division){
		if(division < 0){
			throw new IllegalArgumentException("division < 0"); 
		}
		if(a > b){
			throw new IllegalArgumentException("a > b");
		}
		double delta = (b-a) / division;
		return integralApp0(fx,a,b,delta);
	}
	
	private static double integralApp0(DoubleUnaryOperator fx,double a,double b,double delta){
		double x = a;
		double in = 0;
		in += fx.applyAsDouble(x);
		while((x+=delta)<b){
			in += 2*fx.applyAsDouble(x);
		}
		in += fx.applyAsDouble(b);
		in /= 2;
		in *= delta;
		return in;
	}
	/**
	 * Computes the integral of the function by using the Simpson formula.
	 * @param fx the function
	 * @param a the starting of the area 
	 * @param b the ending of the area
	 * @param n the parameter determining the accuracy of this method, the bigger this argument is, the longer time
	 * this method will take and the result will be more accurate.
	 * @return integral
	 */
	public static double integralApproximationSimpson(DoubleUnaryOperator fx,double a,double b,long n){
		if(n <= 0){
			throw new IllegalArgumentException("n < 0"); 
		}
		if(a > b){
			throw new IllegalArgumentException("a > b");
		}
		double in = 0;
		double x = a;
		double delta = (b-a) /2 /n;
		in += fx.applyAsDouble(x);
		for(long i=0;i<n;i++){
			x += delta;
			in += 4 * fx.applyAsDouble(x);
			x += delta;
			in += 2 * fx.applyAsDouble(x);
		}
		in += fx.applyAsDouble(b);
		in /= 3;
		in *= delta;
		return in;
	}
	
	/**
	 * Computes the integral of the function by using the Simpson formula.
	 * @param fx the function
	 * @param a the starting of the area 
	 * @param b the ending of the area
	 * @return integral
	 */
	public static double integralApproximationSimpson(DoubleUnaryOperator fx,double a,double b){
		return integralApproximationSimpson(fx, a, b, default_division);
	}
	/**
	 * Returns the derivation of a single variable polynomial function.
	 * @param f
	 * @return
	 */
	public static <T> AbstractSVPFunction<T> derivation(SVPFunction<T> f){
		@SuppressWarnings("unchecked")
		T[] cns = (T[]) new Object[f.getMaxPower()];
		MathCalculator<T> mc = f.getMathCalculator();
		for(int i=1;i<=f.getMaxPower();i++){
			//(Ax^i)' = iA*x^(i-1)
			cns[i-1] = mc.multiplyLong(f.getCoefficient(i), i);
		}
		return AbstractSVPFunction.createFunction(mc, cns);
	}
	
}
