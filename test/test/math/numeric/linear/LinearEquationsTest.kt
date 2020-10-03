package test.math.numeric.linear

import cn.ancono.math.algebra.linearAlgebra.Matrix
import cn.ancono.math.algebra.linearAlgebra.Vector
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numeric.linear.LinearEquations
import cn.ancono.math.times
import org.junit.Test

import org.junit.Assert.*
import kotlin.random.Random

class LinearEquationsTest {
    val mc = Fraction.calculator

    @Test
    fun solveUpper() {
        val n = 4
        val U = Matrix.of(n, n, mc) { i, j ->
            if (i > j) {
                Fraction.ZERO
            } else {
                Fraction.of(i + j + 1L)
            }
        }
        val x = Vector.ones(n, mc)
        val b = U * x
        val x1 = LinearEquations.solveUpper(U, b)
        assertTrue(x.valueEquals(x1))
    }

    @Test
    fun solveLower() {
        val n = 4
        val L = Matrix.of(n, n, mc) { i, j ->
            if (i > j) {
                Fraction.ZERO
            } else {
                Fraction.of(i + j + 1L)
            }
        }.transpose()
        val x = Vector.ones(n, mc)
        val b = L * x
        val x1 = LinearEquations.solveLower(L, b)
        assertTrue(x.valueEquals(x1))
    }

    @Test
    fun solveGauss() {
        val n = 4
        val mc = Calculators.getCalDoubleDev()
        val A = Matrix.of(n, n, mc) { _, _ ->
            Random.nextDouble()
        }
        val X = Matrix.of(n,n,mc){ _, _ ->
            Random.nextDouble()
        }
        val B = A * X
        val M = Matrix.concatColumn(A,B)
        val X1 = LinearEquations.solveGauss(M)
        assertTrue(X.valueEquals(X1))
    }
}