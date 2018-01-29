/**
 * 2017-10-09
 */
package test.math;

import static cn.timelives.java.utilities.Printer.print;

import org.junit.Test;

import cn.timelives.java.math.equation.EquationSup;
import cn.timelives.java.math.equation.Type;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.MathCalculator;
/**
 * @author liyicheng
 * 2017-10-09 19:26
 *
 */
public class TestEquation {

	/**
	 * 
	 */
	public TestEquation() {
	}
	MathCalculator<Double> mc = Calculators.getCalculatorDouble();
	
	@Test
	public void testSolve() {
		print(EquationSup.solveQInequation(1d, 2d, 1d, Type.LESS_OR_EQUAL, mc));
		print(EquationSup.solveQInequation(1d, -2d, -3d, Type.LESS_OR_EQUAL, mc));
		print(EquationSup.solveQInequation(0d, 2d, -3d, Type.GREATER, mc));
		print(EquationSup.solveQInequation(1d, -2d, -3d, Type.NOT_EQUAL, mc));
	}
}
