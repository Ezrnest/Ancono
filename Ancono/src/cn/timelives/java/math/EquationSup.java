/**
 * 2017-10-06
 */
package cn.timelives.java.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.timelives.java.math.numberModels.MathCalculator;

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
		MathCalculator<T> mc = sv.mc;
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

}
