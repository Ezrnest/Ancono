/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.set.Interval;
import cn.timelives.java.math.set.IntervalUnion;

/**
 * This class provides useful static methods to do transformation between equation, 
 * inequation and function, and also necessary tools.
 * @author liyicheng
 * 2017-10-06 15:52
 *
 */
public final class EquationSup {

	/**
	 * 
	 */
	private EquationSup() {
	}
	/**
	 * This method will try to solve the equation using the solution-formulas.Because 
	 * formulas are only available when {@code n<5}, if {@code n>=5},an exception will 
	 * be thrown.
	 * <p><b>Warning: this method is not fully implemented yet.</b>
	 * @return a list of solutions,including imaginary roots.
	 * @throws ArithmeticException if {@code n>=5}
	 */
	public static <T> List<T> solveUsingFormula(SVPEquation<T> sv){
		MathCalculator<T> mc = sv.getMathCalculator();
		switch(sv.mp){
		case 1:{
			return Arrays.asList(mc.negate(mc.divide(sv.getCoefficient(0), 
					sv.getCoefficient(1))));
		}
		case 2:{
			T a = sv.getCoefficient(2);
			T b = sv.getCoefficient(1);
			T c = sv.getCoefficient(0);
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
		//TODO implement the formulas
		default:{
			throw new ArithmeticException("No formula available.");
		}
		}
	}
	
	/**
	 * Returns the solution of
	 * <pre>ax^2 + bx + c <i>op</i> 0</pre>
	 * The coefficient {@code a},{@code b} and {@code c} may be zero.
	 * <p>The operation can be any.
	 * @param a the coefficient of {@code x^2}
	 * @param b the coefficient of {@code x}
	 * @param c the constant
	 * @param op the operation
	 * @param mc a {@link MathCalculator}
	 * @return the solution
	 */
	public static <T> IntervalUnion<T> solveQInequation(T a,T b,T c,Type op,MathCalculator<T> mc){
		if(mc.isZero(a)) {
			return solveLInequation(b, c, op, mc);
		}else {
			if(Inequation.isOperation(op)) {
				return SVPInequation.quadratic(a, b, c, op, mc).getSolution();
			}else {
				List<T> solution = MathUtils.solveEquation(a, b, c, mc);
				if(op == Type.EQUAL) {
					if(solution.isEmpty()) {
						return IntervalUnion.empty(mc);
					}else if(solution.size()==1) {
						return IntervalUnion.single(solution.get(0), mc);
					}else {
						T x1 = solution.get(0),
								x2 = solution.get(1);
						return IntervalUnion.valueOf(Interval.single(x1, mc), Interval.single(x2, mc));
					}
				}else {
					// NOT_EQUAL
					if(solution.isEmpty()) {
						return IntervalUnion.universe(mc);
					}else if(solution.size()==1) {
						return IntervalUnion.except(solution.get(0), mc);
					}else {
						T x1 = solution.get(0),
								x2 = solution.get(1);
						if(mc.compare(x1, x2)>0) {
							T t = x1;
							x1 = x2;
							x2 = t;
						}
						return IntervalUnion.valueOf(Interval.fromNegativeInf(x1, false, mc),
								Interval.openInterval(x1, x2, mc), Interval.toPositiveInf(x2, false, mc));
					}
				}
			}
		}
	}
	/**
	 * Returns the solution of
	 * <pre>ax + b <i>op</i> 0</pre>
	 * The coefficient {@code a} and {@code b} may be zero.
	 * <p>The operation can be any.
	 * @param a the coefficient of {@code x}
	 * @param b the constant
	 * @param op the operation
	 * @param mc a {@link MathCalculator}
	 * @return the solution
	 */
	public static <T> IntervalUnion<T> solveLInequation(T a,T b,Type op,MathCalculator<T> mc){
		if(mc.isZero(a)) {
			return solveCInequation(b, op, mc);
		}
		if(Inequation.isOperation(op)) {
			return IntervalUnion.valueOf(SVPInequation.linear(a, b, op, mc).getSolution());
		}
		T x = mc.negate(mc.divide(b, a));
		if(op == Type.EQUAL) {
			return IntervalUnion.single(x, mc);
		}else {
			// NOT_EQUAL
			return IntervalUnion.except(x, mc);
		}
	}
	/**
	 * Returns the solution of 
	 * <pre> a <i>op</i> 0</pre>
	 * The coefficient {@code a} may be zero.
	 * <p>The operation can be any.
	 * @param a the constant
	 * @param op the operation
	 * @param mc a {@link MathCalculator}
	 * @return the solution
	 */
	public static <T> IntervalUnion<T> solveCInequation(T a,Type op,MathCalculator<T> mc){
		boolean universe = op.matches(mc.compare(a, mc.getZero()));
		return universe ? IntervalUnion.universe(mc) : IntervalUnion.empty(mc);
	}
	
}
