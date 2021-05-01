package samples

import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.algebra.linear.T
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.numberModels.BigFraction
import cn.ancono.math.numberModels.Calculators
import java.math.BigInteger
import kotlin.random.Random


/*
 * Created by liyicheng at 2020-09-26 17:55
 */


object LinearEquationExamples {
    fun solveHilbertMatrixEquation() {
        val n = 12
        val mc = BigFraction.calculator
        val H = Matrix(n, n, mc) { i, j ->
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
        val space = matrix.kernel()
        println(space)
    }

    fun kernelAndImage() {
        val p = 3
        val n = 5
        val matrix = Matrix(n, n, Calculators.intModP(p)) { _, _ ->
            Random.nextInt(0, p)
        } // create a random n * n matrix
        val kernel = matrix.kernel()
        val image = matrix.image()
        println(matrix)
        println(kernel)
        println(image)
        println(kernel.rank + image.rank) // = n
    }

    fun decompositions() {
        val A = Matrix(5, 5, Calculators.doubleDev()) { _, _ ->
            Random.nextDouble()
        }
        println(A)
        val (Q, R) = A.decompQR()
        println(Q.T * Q)
        println(Q * R)
        println((Q * R).valueEquals(A))
        println()
        val (P, L, U) = A.decompLU()
        println(P * A)
        println(A * P)
        println(L * U)
        println((P * A).valueEquals(L * U))

//        A.toCongDiagForm()
    }
}

fun main() {

    LinearEquationExamples.decompositions()
}