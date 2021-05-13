package samples

import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.algebra.linear.T
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.numberModels.BigFraction
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Fraction
import java.math.BigInteger
import kotlin.random.Random

object LinearAlgebraExamplesKt {
    //Created by lyc at 2021-05-11 22:33
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

    fun example1() {
        val cal = Calculators.intModP(11)
        val m = Matrix.of(3, 3, cal,
                2, 3, 4,
                4, 5, 6,
                1, 3, 2
        )
        println("A = \n$m")
        println("Frobenious Form=\n" + m.toFrobeniusForm())

        val A = m.mapTo(Calculators.integer()) { it }
        println(A.toSmithForm())
        println(A.toHermitForm())
    }

    fun decompositions() {
        val A = Matrix(5, 5, Calculators.doubleDev()) { _, _ ->
            Random.nextDouble()
        }
        println(A)
        println("QR:")
        val (Q, R) = A.decompQR()
        println(Q.T * Q)
        println(Q * R)
        println(Q * R valueEquals A)
        println()

        println("KAN:")
        val (K, d, N) = A.decompKAN()
        println(K.T * K)
        println(d)
        println(N)
        println(K * Matrix.diag(d) * N)
        println(K * Matrix.diag(d) * N valueEquals A)




    }

    fun decompositions2() {
        val A = Matrix(5, 5, Calculators.doubleDev()) { _, _ ->
            Random.nextDouble()
        }
        println(A)

        val vs = A.columnVectors()
        val (ws, R) = Vector.orthogonalizeAndTrans(vs)
        val Q = Matrix.fromVectors(ws)
        println(Q * R)
        println(Q.T * Q)
        println(Q * R valueEquals A)
    }

    fun decompositions3() {
        val A = Matrix(4, 4, Fraction.calculator) { i, j ->
            Fraction.of(i + j + 1L)
        }
        println(A)

        val (L, R) = A.decompRank()
        println("L=")
        println(L)
        println("R=")
        println(R)
        println("LR = A: " + (L * R valueEquals A))

        val B = A.gInverse()
        println("B=")
        println(B)
        print("ABA = A: ")
        println(A * B * A valueEquals A)
    }

    fun decompositions4() {
        val A = Matrix(5, 5, Calculators.doubleDev()) { _, _ ->
            Random.nextDouble()
        }
        println(A)
        println("PLU:")
        val (P, L, U) = A.decompPLU()
        println(P * A)
        println(L * U)
        println(P * A valueEquals L * U)
    }

}

fun main() {
    LinearAlgebraExamplesKt.decompositions3()
}