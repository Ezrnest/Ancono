package test.math.linearAlgebra

import cn.ancono.math.numberModels.Calculators.IntegerCalculator
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.function.MathFunction
import org.junit.Test
import test.math.TestUtils
import kotlin.test.assertTrue

/*
 * Created by liyicheng at 2020-03-10 13:36
 */
class MatrixSupTest {
    @Test
    fun toHermitFrom() {
        val cal = Calculators.getCalIntegerExact()
        val m = MatrixSup.parseMatrix("[[1 2 3][4 5 6][7 8 9]]", cal) { s: String -> s.toInt() }
        var w = m.toSmithForm()
        w = w.applyFunction { para: Int? -> cal.abs(para!!) }
        TestUtils.assertValueEquals(Matrix.diag(arrayOf(1, 3, 0), cal), w)
    }

    @Test
    fun modularInverse() {
        val mc = Calculators.getCalIntModN(26)
        val A = MatrixSup.parseMatrixD(
            """
            3 21 20
            4 15 23
            6 14 5
        """.trimIndent(), mc, Integer::parseInt
        )
        val result = MatrixSup.parseMatrixD(
            """
            13 21 23
            10  3 19
            24  8 13
        """.trimIndent(), mc, Integer::parseInt
        )
        val B = MatrixSup.inverseInRing(A)
        assertTrue {
            B.valueEquals(result)
        }

    }
}