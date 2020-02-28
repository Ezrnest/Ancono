/**
 * 2018-01-25
 */
package test.math;

import cn.ancono.math.equation.SVPEquation;
import cn.ancono.math.algebra.linearAlgebra.LinearEquationSolution;
import cn.ancono.math.algebra.linearAlgebra.Matrix;
import cn.ancono.math.algebra.linearAlgebra.MatrixSup;
import cn.ancono.math.algebra.linearAlgebra.Vector;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.MathCalculator;
import cn.ancono.math.geometry.analytic.planeAG.curve.ConicSection;
import cn.ancono.math.geometry.analytic.planeAG.curve.GeneralConicSection;
import cn.ancono.utilities.ArraySup;
import cn.ancono.utilities.structure.Pair;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author liyicheng 2018-01-25 17:38
 *
 */
public class TestMatrix {

	/**
	 * 
	 */
	public TestMatrix() {
	}

	MathCalculator<Long> mc = Calculators.getCalculatorLong();
	MathCalculator<Double> mcd = Calculators.getCalculatorDoubleDev();

//	@Test
	public void testEigenEquation() {
		Matrix<Long> mat = Matrix.valueOf(new long[][] { { 1, 0 }, { 0, 4 } });
		SVPEquation<Long> equation = mat.eigenvalueEquation(), expected = SVPEquation.quadratic(1L, -5L, 4L, mc);
		assertTrue("EigenEquation:", expected.valueEquals(equation));
		mat = Matrix.valueOf(new long[][] { { 1, 2 }, { 3, 4 } });
		equation = mat.eigenvalueEquation();
		expected = SVPEquation.quadratic(1L, -5L, -2L, mc);
		assertTrue("EigenEquation:", expected.valueEquals(equation));
		mat = Matrix.valueOf(new long[][] { { 1, 2, 3 }, { 3, 4, 5 }, { 4, 5, 6 } });
		equation = mat.eigenvalueEquation();
		expected = SVPEquation.valueOf(mc, 0L, -9L, -11L, 1L);
		assertTrue("EigenEquation:", expected.valueEquals(equation));
	}

//	@Test
	public void testSolveEquation() {
		int row = 8;
		int column = 10;
		for (int i = 0; i < 100; i++) {
			double[][] mat = new double[row][];
			for (int j = 0; j < row; j++) {
				mat[j] = ArraySup.ranDoubleArr(column);
			}
			Matrix<Double> matrix = Matrix.valueOf(mat).mapTo(x -> x, mcd);
			LinearEquationSolution<Double> solution = MatrixSup.solveLinearEquation(matrix);
			if (solution.getSolutionSituation() != LinearEquationSolution.Situation.NO_SOLUTION) {
				Vector<Double> base = solution.getSpecialSolution();
				Vector<Double>[] ks = solution.getBaseSolutions();
				if (ks != null) {
					base = Vector.addVectors(base, ks);
				}
				Matrix<Double> re1 = Matrix.multiplyMatrix(matrix.subMatrix(0, 0, row - 1, column - 2), base),
						re2 = matrix.subMatrix(0, column - 1, row - 1, column - 1);
				assertTrue(re1.valueEquals(re2));
			}
		}
	}

//	@Test
	public void testSolveHomoEquation() {
		int row = 8;
		int column = 11;
		Vector<Double> zero = Vector.zeroVector(row, mcd);
		for (int i = 0; i < 100; i++) {
			double[][] mat = new double[row][];
			for (int j = 0; j < row; j++) {
				mat[j] = ArraySup.ranDoubleArr(column);
			}
			Matrix<Double> matrix = Matrix.valueOf(mat).mapTo(x -> x, mcd);
			LinearEquationSolution<Double> solution = MatrixSup.solveHomogeneousLinearEquation(matrix);
			if (solution.getSolutionSituation() != LinearEquationSolution.Situation.NO_SOLUTION) {
				Vector<Double> base = solution.getSpecialSolution();
				Vector<Double>[] ks = solution.getBaseSolutions();
				if (ks != null) {
					base = Vector.addVectors(base, ks);
				}
				Matrix<Double> re = Matrix.multiplyMatrix(matrix, base);
				assertTrue(re.valueEquals(zero));
			}
		}
	}
	
	@Test
	public void testEigenVector() {
		Matrix<Double> mat = Matrix.valueOf(new double[][] {
			{0,1,1},
			{1,0,1},
			{1,1,0}
		});
		List<Pair<Double,Vector<Double>>> list = mat.eigenvaluesAndVectors(x ->{
			return Arrays.asList(-1d,-1d,2d);
		});
		assertEquals("", list.toString(),"[[-1.0,[1.0,-1.0,0.0]], [-1.0,[1.0,0.0,-1.0]], [2.0,[-1.0,-1.0,-1.0]]]");
	}
	@Test
	public void testNormalizeUsingMatrix() {
		ConicSection<Double> cs = GeneralConicSection.generalFormula(
				2d, -Math.sqrt(3),1d,
				0d,0d,-10d, mcd);
		ConicSection<Double> afterTrans = cs.toStandardForm();
		assertTrue("",mcd.isEqual(afterTrans.getA(),2.5)&&mcd.isEqual(afterTrans.getC(),0.5));
		assertEquals("",cs.determineType(),ConicSection.Type.ELLIPSE);
	}

	@Test
    public void testDeterminant(){
	    Matrix<Integer> mat = Matrix.valueOf(new int[][]{
                {1,2,3,4},
                {0,3,4,5},
                {0,0,5,6},
                {0,0,0,4}
        });
	    assertEquals(mat.calDet().intValue(), 3 * 5 * 4);
	    var mat2 = Matrix.valueOf(new double[][]{
                {1,2,3,4,5},
                {8,0,3,4,5},
                {27,1.3,5,5,6},
                {3,4,6,7,4},
                {1,11,3,-4,3}
        });
	    assertTrue(Math.abs(mat2.calDet()-MatrixSup.fastDet(mat2)) < 0.00001);
    }
}
