package cn.ancono.math.algebra.linear

import cn.ancono.math.algebra.abs.calculator.EUDCalculator
import cn.ancono.math.algebra.abs.calculator.UnitRingCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.component1
import cn.ancono.math.component2
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.utilities.structure.Pair
import java.util.*


/**
 * A kotlin-based implementation of some matrix-related methods.
 */
internal object MatrixSupKt {
    /**
     * Computes the (general) LU decomposition of the given matrix `A` , returns a tuple of matrices `(P,L,U)` such that
     * `PA = LU`, `P` is a permutation matrix, `L` is a lower triangular matrix with 1 as diagonal elements, and
     * `U` is a upper triangular matrix.
     *
     * It is required that the matrix is invertible.
     *
     * **Note**: This method is not designed for numerical computation but for demonstration.
     *
     * @return
     */
    fun <T : Any> decompositionLU(m: Matrix<T>): Triple<Matrix<T>, Matrix<T>, Matrix<T>> {
        require(m.isSquare) {
            "The matrix must be square!"
        }
        val mc = m.mathCalculator
        val n = m.rowCount

        @Suppress("UNCHECKED_CAST")
        val matrix = m.values as Array<Array<T>>
//        val operations = mutableListOf<MatrixOperation<T>>()
        val p = Matrix.identity(m.rowCount, mc).rowVectors()
        val l = Matrix.getBuilder(n, n, mc)

        for (k in 0 until m.rowCount) {
            var maxIdx = k
            var maxVal = mc.abs(matrix[k][k])
            for (i in (k + 1) until m.rowCount) {
                val v = mc.abs(matrix[i][k])
                if (mc.compare(v, maxVal) > 0) {
                    maxIdx = i
                    maxVal = v
                }
            }
            if (maxIdx != k) {
                MatrixSup.exchangeRow(matrix, k, maxIdx)
                Collections.swap(p, k, maxIdx)
            }
            l.set(k, k, mc.one)
            for (i in (k + 1) until m.rowCount) {
                val lambda = mc.eval {
                    matrix[i][k] / matrix[k][k]
                }
                l.set(i, k, lambda)
                matrix[i][k] = mc.zero
                for (j in (k + 1) until m.columnCount) {
                    matrix[i][j] = mc.eval {
                        matrix[i][j] - lambda * matrix[k][j]
                    }
                }
            }
        }
        return Triple(
            Matrix.fromVectors(true, p),
            l.build(),
            Matrix.valueOfNoCopy(matrix, mc)
        )
    }

    /**
     * @see Matrix.decompCholesky
     */
    fun <T : Any> decompositionCholesky(A: Matrix<T>): Matrix<T> {
        require(A.isSquare) {
            "The matrix must be square!"
        }
        val mc = A.mathCalculator
        val n = A.rowCount

        @Suppress("UNCHECKED_CAST")
        val l = Array(n) {
            Array<Any>(n) {
                mc.zero
            }
        } as Array<Array<T>>
        for (j in 0 until n) {
            var t = A[j, j]
            for (k in 0 until j) {
                t = mc.eval { t - l[j][k] * l[j][k] }
            }
            t = mc.eval { squareRoot(t) }
            l[j][j] = t
            // l_{jj} = sqrt(a_{jj} - sum(0,j-1, l_{jk}^2))


            for (i in (j + 1) until n) {
                var a = A[i, j]
                for (k in 0 until j) {
                    a = mc.eval { a - l[i][k] * l[j][k] }
                }
                a = mc.eval { a / t }
                l[i][j] = a
                // l_{ij} = (a_{ij} - sum(0,j-1,l_{il}l_{jl}))/l_{jj}
            }
        }
        return Matrix.valueOfNoCopy(l, mc)
    }

    /**
     * @see Matrix.decompCholesky
     */
    fun <T : Any> decompositionCholeskyD(A: Matrix<T>): Pair<Matrix<T>, Vector<T>> {
        require(A.isSquare) {
            "The matrix must be square!"
        }
        val mc = A.mathCalculator
        val n = A.rowCount

        @Suppress("UNCHECKED_CAST")
        val l = Array(n) {
            Array<Any>(n) {
                mc.zero
            }
        } as Array<Array<T>>
        val d = ArrayList<T>(n)

        for (j in 0 until n) {
            var t = A[j, j]
            for (k in 0 until j) {
                t = mc.eval { t - l[j][k] * l[j][k] * d[k] }
            }
            d += t
            // d_j = a_{jj} - sum(0,j-1, l_{jk}^2)
            l[j][j] = mc.one
            // l_{jj} = a_{jj} - sum(0,j-1, l_{jk}^2)


            for (i in (j + 1) until n) {
                var a = A[i, j]
                for (k in 0 until j) {
                    a = mc.eval { a - l[i][k] * l[j][k] * d[k] }
                }
                l[i][j] = mc.eval { a / t }
                // l_{ij} = (a_{ij} - sum(0,j-1,d_k * l_{ik}l_{jk}))
            }
        }
        val L = Matrix.valueOfNoCopy(l, mc)
        val D = Vector.vOf(mc, d)
        return Pair(L, D)
    }


    /**
     * Computes the inverse of the matrix on an Euclidean domain.
     */
    fun <T : Any> inverseInEUD(M: Matrix<T>): Matrix<T> {
        //TODO check correctness
        M.requireSquare()
        val n = M.column
        val mc = M.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val euc = M.mathCalculator as EUDCalculator<T>


        @Suppress("UNCHECKED_CAST")
        val A = Array(n) { i ->
            Array<Any>(2 * n) { j ->
                when {
                    j < n -> {
                        M[i, j]
                    }
                    i == j - n -> {
                        euc.one
                    }
                    else -> {
                        euc.zero
                    }
                }
            }
        } as Array<Array<T>>
//        Printer.printMatrix(A)
        // to upper triangle
        for (j in 0 until n) {
            var i = j
            while (mc.isZero(A[i][j]) && i < n) {
                i++
            }
            if (i == n) {
                ExceptionUtil.notInvertible()
            }
            if (i != j) {
                MatrixSup.exchangeRow(A, j, i)
            }
            i++
            outer@
            while (true) {
                val p = A[j][j]

                while (i < n) {
                    val (q, r) = euc.divideAndRemainder(A[i][j], p)
                    MatrixSup.multiplyAndAddRow(A, j, i, j, euc.negate(q), mc)
//                    Printer.printMatrix(A)
                    if (euc.isZero(r)) {
                        i++
                        continue
                    }
                    MatrixSup.exchangeRow(A, j, i)
                    continue@outer
                }
                try {
                    val k = mc.reciprocal(p)
                    MatrixSup.multiplyNumberRow(A, j, j, k, mc)
                } catch (e: ArithmeticException) {
                    ExceptionUtil.notInvertible()
                }
            }
        }

        for (j1 in (n - 1) downTo 1) {
            for (j2 in 0 until j1) {
                val k = mc.negate(A[j2][j1])
                MatrixSup.multiplyAndAddRow(A, j1, j2, j1, k, mc)
            }
        }

        val builder = Matrix.getBuilder(n, M.columnCount - n, mc)
        builder.fillArea(0, 0, A, 0, n, n, n)
        return builder.build()


    }

    /**
     * Computes the 'inverse' of the given matrix on a unit ring. This method simply compute the adjugate matrix and
     * divide it with the determinant (so it is time-consuming).
     *
     * This method can be used to compute the modular inverse of a matrix on Z/Zn, where n is not necessarily a prime.
     */
    fun <T : Any> inverseInRing(M: Matrix<T>): Matrix<T> {
        val mc = M.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val rc = M.mathCalculator as UnitRingCalculator<T>
        val det = M.calDet()
        if (!rc.isUnit(det)) {
            ExceptionUtil.notInvertible()
        }
        return M.adjugate().applyFunction { x -> mc.divide(x, det) }
    }

    fun <T : Any> toLatexString(M: Matrix<T>, formatter: NumberFormatter<T> = NumberFormatter.defaultFormatter(), displayType: String = "pmatrix"): String = buildString {
        val mc = M.mathCalculator
        append("\\begin{$displayType}")
        appendLine()
        for (i in 0 until M.rowCount) {
            (0 until M.columnCount).joinTo(this, separator = " & ", postfix = "\\\\") { j ->
                formatter.format(M[i, j], mc)
            }
            appendLine()
        }
        append("\\end{$displayType}")
    }
}

