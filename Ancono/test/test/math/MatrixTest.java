/**
 * 2018-01-25
 */
package test.math;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import cn.timelives.java.math.equation.SVPEquation;
import cn.timelives.java.math.linearAlgebra.Matrix;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.MathCalculator;
/**
 * @author liyicheng
 * 2018-01-25 17:38
 *
 */
public class MatrixTest {

	/**
	 * 
	 */
	public MatrixTest() {
	}
	MathCalculator<Long> mc = Calculators.getCalculatorLong();
	@Test
	public void textEigenEquation() {
		Matrix<Long> mat = Matrix.valueOf(new long[][] {
			{1,0},
			{0,4}
		});
		SVPEquation<Long> equation = mat.eigenvalueEquation(),
				expected = SVPEquation.quadratic(1L, -5L, 4L, mc);
		assertTrue("EigenEquation:",expected.valueEquals(equation));
		mat = Matrix.valueOf(new long[][] {
			{1,2},
			{3,4}
		});
		equation = mat.eigenvalueEquation();
		expected = SVPEquation.quadratic(1L, -5L, -2L, mc);
		assertTrue("EigenEquation:",expected.valueEquals(equation));
		mat = Matrix.valueOf(new long[][] {
			{1,2,3},
			{3,4,5},
			{4,5,6}
		});
		equation = mat.eigenvalueEquation();
		expected = SVPEquation.valueOf(mc, 0L,-9L,-11L,1L);
		assertTrue("EigenEquation:",expected.valueEquals(equation));
	}
}
