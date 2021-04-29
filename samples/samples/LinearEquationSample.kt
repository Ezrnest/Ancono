package samples

import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.numberModels.BigFraction
import cn.ancono.math.times
import java.math.BigInteger


/*
 * Created by liyicheng at 2020-09-26 17:55
 */


object LinearEquationSample {
    fun solveHilbertMatrixEquation(){
        val n = 12
        val mc = BigFraction.calculator
        val H = Matrix.of(n, n, mc) { i, j ->
            BigFraction.valueOf(BigInteger.ONE, BigInteger.valueOf(i + j + 1L))
        }
        val x = Vector.of(n, mc) {
            mc.one
        }
        val b = H * x
        println(b)
    }

    fun solve1() {
        val matrix = MatrixSup.parseFMatrix("""
            0 0 0
            0 1 2
            0 0 1
        """.trimIndent())
        val space = matrix.solutionSpace()
        println(space)
    }
}

fun main() {

    LinearEquationSample.solve1()
}