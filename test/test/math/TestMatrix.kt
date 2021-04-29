/**
 * 2018-01-25
 */
package test.math

import cn.ancono.math.MathCalculator
import cn.ancono.math.algebra.linear.LinearEquationSolution
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.Matrix.Companion.of
import cn.ancono.math.algebra.linear.asColumnMatrix
import cn.ancono.math.equation.SVPEquation
import cn.ancono.math.geometry.analytic.plane.curve.ConicSection
import cn.ancono.math.geometry.analytic.plane.curve.GeneralConicSection
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Fraction.Companion.calculator
import cn.ancono.math.numberModels.api.times
import org.junit.Assert
import org.junit.Test
import java.util.*

/**
 * @author liyicheng 2018-01-25 17:38
 */
class TestMatrix
/**
 *
 */
{
    var mc: MathCalculator<Long> = Calculators.longCal()
    var mcd = Calculators.doubleDev()

    private fun <T> isUpperTriangular(m: Matrix<T>): Boolean {
        val mc = m.mathCalculator
        val n = Math.min(m.row, m.column)
        for (i in 0 until n) {
            for (j in 0 until i) {
                if (!mc.isZero(m[i, j])) {
                    return false
                }
            }
        }
        return true
    }

    //	@Test
    @Test
    fun testEigenEquation() {
        var mat = of(arrayOf(arrayOf(1L, 0L), arrayOf(0L, 4L)), Calculators.longCal())
        var equation = mat.charEquation()
        var expected: SVPEquation<Long> = SVPEquation.quadratic(1L, -5L, 4L, mc)
        Assert.assertTrue("EigenEquation:", expected.valueEquals(equation))
        mat = of(arrayOf(arrayOf(1, 2), arrayOf(3, 4)), Calculators.longCal())
        equation = mat.charEquation()
        expected = SVPEquation.quadratic(1L, -5L, -2L, mc)
        Assert.assertTrue("EigenEquation:", expected.valueEquals(equation))
        mat = of(arrayOf(arrayOf(1, 2, 3), arrayOf(3, 4, 5), arrayOf(4, 5, 6)), Calculators.longCal())
        equation = mat.charEquation()
        expected = SVPEquation.valueOf(mc, 0L, -9L, -11L, 1L)
        Assert.assertTrue("EigenEquation:", expected.valueEquals(equation))
    }

    @Test
    fun testSolveEquation() {
        val row = 8
        val column = 10
        for (i in 0..99) {
            var rd = Random()
            val matrix: Matrix<Double> = of(row, column, mcd) { _, _ ->
                rd.nextDouble()
            }
            val solution: LinearEquationSolution<Double> = Matrix.solveLinearExpanded(matrix)
            if (solution.notEmpty()) {
                var base = solution.special
//                val ks  = solution.solutionSpace
//                if (ks != null) {
//                    base = Vector.addAll(base, ks)
//                }
                val re1: Matrix<Double> = matrix.subMatrix(0, 0, row - 1, column - 2) * base.asColumnMatrix()
                val re2 = matrix.subMatrix(0, column - 1, row - 1, column - 1)
                Assert.assertTrue(re1.valueEquals(re2))
            }
        }
    }

    //	@Test
    fun testSolveHomoEquation() {
        val row = 8
        val column = 11
        for (i in 0..99) {
            var rd = Random()
            val matrix: Matrix<Double> = of(row, column, mcd) { _, _ ->
                rd.nextDouble()
            }
            val solution = Matrix.solveHomo(matrix)
            val n = Matrix.fromVectors(solution.vectors)
            val re = matrix * n
            Assert.assertTrue(re.isZero())
//                var base: Vector<Double?>? = solution.getSpecialSolution()
//                val ks: Array<Vector<Double>> = solution.getBaseSolutions()
//                if (ks != null) {
//                    base = Vector.addAll(base, ks)
//                }
//                val re: Matrix<Double> = Matrix.multiply(matrix, base)
//                Assert.assertTrue(re.valueEquals(zero))
        }
    }

    @Test
    fun testEigenVector() {
        val mat: Matrix<Double> = of(arrayOf(arrayOf(0.0, 1.0, 1.0), arrayOf(1.0, 0.0, 1.0), arrayOf(1.0, 1.0, 0.0)), mcd)
        val list = mat.eigenvaluesAndVectors { listOf(-1.0, -1.0, 2.0) }
        Assert.assertEquals("", list.toString(), "[[-1.0,[1.0,-1.0,0.0]], [-1.0,[1.0,0.0,-1.0]], [2.0,[-1.0,-1.0,-1.0]]]")
    }

    @Test
    fun testNormalizeUsingMatrix() {
        val cs: ConicSection<Double> = GeneralConicSection.generalFormula(
                2.0, -Math.sqrt(3.0), 1.0,
                0.0, 0.0, -10.0, mcd)
        val afterTrans = cs.toStandardForm()
        Assert.assertTrue("", mcd.isEqual(afterTrans.a, 2.5) && mcd.isEqual(afterTrans.c, 0.5))
        Assert.assertEquals("", cs.determineType(), ConicSection.Type.ELLIPSE)
    }

    //    @Test
    //    public void testDeterminant() {
    //        Matrix<Integer> mat = Matrix.of(new int[][]{
    //                {1, 2, 3, 4},
    //                {0, 3, 4, 5},
    //                {0, 0, 5, 6},
    //                {0, 0, 0, 4}
    //        });
    //        assertEquals(mat.calDet().intValue(), 3 * 5 * 4);
    //        var mat2 = Matrix.of(new double[][]{
    //                {1, 2, 3, 4, 5},
    //                {8, 0, 3, 4, 5},
    //                {27, 1.3, 5, 5, 6},
    //                {3, 4, 6, 7, 4},
    //                {1, 11, 3, -4, 3}
    //        });
    //        assertTrue(Math.abs(mat2.calDet() - MatrixSup.fastDet(mat2)) < 0.00001);
    //    }
    @Test
    fun testDecompositionLU() {
        val A: Matrix<Fraction> = of(arrayOf(
                arrayOf(1, 2, 3, 4),
                arrayOf(2, 3, 4, 5),
                arrayOf(3, 2, 5, 6),
                arrayOf(6, -2, -8, 4)), Calculators.integer())
                .mapTo(calculator) { Fraction.of(it.toLong()) }
        val (P, L, U) = A.decompLU()
        //        P.printMatrix();
//        L.printMatrix();
        Assert.assertTrue("L should be lower triangular.", isUpperTriangular(L.transpose()))
        Assert.assertTrue("U should be upper triangular.", isUpperTriangular(U))
        //        U.printMatrix();
        val m1 = P * A
        val m2 = L * U
        Assert.assertTrue("PA = LU", m1.valueEquals(m2))
    }

    @Test
    fun testDecompCholesky() {
        val rd = Random()
        val B: Matrix<Double> = of(5, 5, Calculators.doubleDev()) { _: Int?, _: Int? -> rd.nextDouble() }
        val A = B.multiply(B.transpose())
        val L = A.decompCholesky()
        //        A.congruenceDiagForm().getFirst().printMatrix();
//        L.printMatrix();
//        A.printMatrix();
        val R = L * L.transpose()
        //        R.printMatrix();
//        var A = Matrix.of(new double[][]{
//                {4, -1, 1},
//                {-1, 4.25, 2.75},
//                {1, 2.75, 3.5}
//        });
//        var L = A.decompCholesky();
//        L.printMatrix();
//        var R = Matrix.multiply(L,L.transpose());
        Assert.assertTrue("A = LL^T", A.valueEquals(R))
    }

    @Test
    fun testDecompCholeskyD() {
        val rd = Random()
        val B: Matrix<Double> = of(5, 5, Calculators.doubleDev()) { _: Int?, _: Int? -> rd.nextDouble() }
        val A = B * B.transpose()
        val (L, D) = A.decompCholeskyD()

//        A.congruenceDiagForm().getFirst().printMatrix();
//        L.printMatrix();
//        A.printMatrix();
        val R = Matrix.product(L, D, L.transpose())

//        R.printMatrix();
//        var A = Matrix.of(new double[][]{
//                {4, -1, 1},
//                {-1, 4.25, 2.75},
//                {1, 2.75, 3.5}
//        });
//        var L = A.decompCholesky();
//        L.printMatrix();
//        var R = Matrix.multiply(L,L.transpose());
        Assert.assertTrue("A = LDL^T", A.valueEquals(R))
    }
}