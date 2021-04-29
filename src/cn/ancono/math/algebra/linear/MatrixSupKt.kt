package cn.ancono.math.algebra.linear

import cn.ancono.math.algebra.abs.calculator.EUDCalculator
import cn.ancono.math.algebra.abs.calculator.UnitRingCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.api.NumberFormatter
import cn.ancono.math.numberModels.api.colIndices
import cn.ancono.math.numberModels.api.requireSquare
import cn.ancono.math.numberTheory.IntCalculator
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
    fun <T> decompositionLU(m: Matrix<T>): Triple<Matrix<T>, Matrix<T>, Matrix<T>> {
        require(m.isSquare()) {
            "The matrix must be square!"
        }
        val mc = m.mathCalculator
        val n = m.row
        val matrix = m.toMutable()
//        val operations = mutableListOf<MatrixOperation<T>>()
        val p = Matrix.identity(m.row, mc).rowVectors()
        val l = Matrix.zero(n, n, mc)

        for (k in 0 until m.row) {
            var maxIdx = k
            var maxVal = mc.abs(matrix[k, k])
            for (i in (k + 1) until m.row) {
                val v = mc.abs(matrix[i, k])
                if (mc.compare(v, maxVal) > 0) {
                    maxIdx = i
                    maxVal = v
                }
            }
            if (maxIdx != k) {
                matrix.swapRow(k, maxIdx)
                Collections.swap(p, k, maxIdx)
            }
            l[k, k] = mc.one
            for (i in (k + 1) until m.row) {
                val lambda = mc.eval {
                    -(matrix[i, k] / matrix[k, k])
                }
                l[i, k] = lambda
                matrix[i, k] = mc.zero
                matrix.multiplyAddRow(k, i, lambda, k + 1)
//                for (j in (k + 1) until m.column) {
//                    matrix[i,j] = mc.eval {
//                        matrix[i,j] - lambda * matrix[k][j]
//                    }
//                }
            }
        }
        return Triple(
                Matrix.fromVectors(p),
                l,
                matrix
        )
    }

    /**
     * @see Matrix.decompCholesky
     */
    fun <T> decompositionCholesky(A: Matrix<T>): Matrix<T> {
        require(A.isSquare()) {
            "The matrix must be square!"
        }
        val mc = A.mathCalculator
        val n = A.row

        @Suppress("LocalVariableName")
        val L = Matrix.zero(n, n, mc)
        for (j in 0 until n) {
            var t = A[j, j]
            for (k in 0 until j) {
                t = mc.eval { t - L[j, k] * L[j, k] }
            }
            t = mc.eval { squareRoot(t) }
            L[j, j] = t
            // l_{jj} = sqrt(a_{jj} - sum(0,j-1, l_{jk}^2))


            for (i in (j + 1) until n) {
                var a = A[i, j]
                for (k in 0 until j) {
                    a = mc.eval { a - L[i, k] * L[j, k] }
                }
                a = mc.eval { a / t }
                L[i, j] = a
                // l_{ij} = (a_{ij} - sum(0,j-1,l_{il}l_{jl}))/l_{jj}
            }
        }
        return L
    }

    /**
     * @see Matrix.decompCholesky
     */
    fun <T> decompositionCholeskyD(A: Matrix<T>): Pair<Matrix<T>, Vector<T>> {
        require(A.isSquare()) {
            "The matrix must be square!"
        }
        val mc = A.mathCalculator
        val n = A.row

        @Suppress("LocalVariableName")
        val L = Matrix.zero(n, n, mc)
        val d = ArrayList<T>(n)

        for (j in 0 until n) {
            var t = A[j, j]
            for (k in 0 until j) {
                t = mc.eval { t - L[j, k] * L[j, k] * d[k] }
            }
            d += t
            // d_j = a_{jj} - sum(0,j-1, l_{jk}^2)
            L[j, j] = mc.one
            // l_{jj} = a_{jj} - sum(0,j-1, l_{jk}^2)

            for (i in (j + 1) until n) {
                var a = A[i, j]
                for (k in 0 until j) {
                    a = mc.eval { a - L[i, k] * L[j, k] * d[k] }
                }
                L[i, j] = mc.eval { a / t }
                // l_{ij} = (a_{ij} - sum(0,j-1,d_k * l_{ik}l_{jk}))
            }
        }
        return L to Vector.of(d, mc)
    }


    /**
     * Computes the inverse of the matrix on an Euclidean domain.
     */
    fun <T> inverseInEUD(M: Matrix<T>): Matrix<T> {
        //TODO check correctness
        M.requireSquare()
        val n = M.column
        val mc = M.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val euc = M.mathCalculator as EUDCalculator<T>


        @Suppress("UNCHECKED_CAST")
        val A = Matrix.of(n, 2 * n, mc) { i, j ->
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
//        Printer.printMatrix(A)
        // to upper triangle
        for (j in 0 until n) {
            var i = j
            while (mc.isZero(A[i, j]) && i < n) {
                i++
            }
            if (i == n) {
                ExceptionUtil.notInvertible()
            }
            if (i != j) {
                A.swapRow(i, j)
            }
            i++
            outer@
            while (true) {
                val p = A[j, j]

                while (i < n) {
                    val (q, r) = euc.divideAndRemainder(A[i, j], p)
                    A.multiplyAddRow(j, i, euc.negate(q), j)
//                    Printer.printMatrix(A)
                    if (euc.isZero(r)) {
                        i++
                        continue
                    }
                    A.swapRow(j, i)
                    continue@outer
                }
                try {
                    val k = mc.reciprocal(p)
                    A.multiplyRow(j, k, j)
                } catch (e: ArithmeticException) {
                    ExceptionUtil.notInvertible()
                }
            }
        }

        for (j1 in (n - 1) downTo 1) {
            for (j2 in 0 until j1) {
                val k = mc.negate(A[j2, j1])
                A.multiplyAddRow(j1, j2, k, j1)
//                MatrixSup.multiplyAndAddRow(A, j1, j2, j1, k, mc)
            }
        }

//        val builder = Matrix.getBuilder(n, M.columnCount - n, mc)
//        builder.fillArea(0, 0, A, 0, n, n, n)

        return M.subMatrix(0, M.row, n, M.column)


    }

    /**
     * Computes the 'inverse' of the given matrix on a unit ring. This method simply compute the adjugate matrix and
     * divide it with the determinant (so it is time-consuming).
     *
     * This method can be used to compute the modular inverse of a matrix on Z/Zn, where n is not necessarily a prime.
     */
    fun <T> inverseInRing(M: Matrix<T>): Matrix<T> {
        val mc = M.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val rc = M.mathCalculator as UnitRingCalculator<T>
        val det = M.det()
        if (!rc.isUnit(det)) {
            ExceptionUtil.notInvertible()
        }
        return M.adjugate().applyAll { x -> mc.divide(x, det) }
    }

    /**
     * Transform this matrix to Hermit Form. It is required that the calculator is a
     * [cn.ancono.math.algebra.abs.calculator.EUDCalculator].
     */
    fun <T> toHermitForm(m: AbstractMatrix<T>): Matrix<T> {
        val mc = m.mathCalculator as IntCalculator<T>
        val mat = m.toMutable()
        //        @SuppressWarnings("unchecked")
//        T[] temp = (T[]) new Object[m.row];
        var i = m.row - 1
        var j = m.column - 1
        var k = m.column - 1
        val l = if (m.row <= m.column) {
            0
        } else {
            m.row - m.column + 1
        }
        while (true) {
            while (j > 0) {
                j--
                if (!mc.isZero(mat[i, j])) {
                    break
                }
            }
            if (j > 0) {
                val (d, u, v) = mc.gcdUV(mat[i, k], mat[i, j])
                val k1 = mc.divideToInteger(mat[i, k], d)
                val k2 = mc.divideToInteger(mat[i, j], d)
                for (p in 0 until m.row) {
                    val t = mc.add(mc.multiply(u, mat[k, p]), mc.multiply(v, mat[j, p]))
                    mat[j, p] = mc.subtract(mc.multiply(k1, mat[j, p]), mc.multiply(k2, mat[k, p]))
                    mat[k, p] = t
                }
            } else {
                var b = mat[i, k]
                if (mc.isNegative(b)) {
                    for (p in 0 until m.row) {
                        mat[k, p] = mc.negate(mat[k, p])
                    }
                    b = mc.negate(b)
                }
                if (mc.isZero(b)) {
                    k++
                } else {
                    for (t in k + 1 until m.column) {
                        val q = mc.divideToInteger(mat[i, t], b)
                        mat.multiplyAddRow(k, j, mc.negate(q))
//                        for (p in 0 until m.row) {
//                            mat[j,p] = mc.subtract(mat[j,p], mc.multiply(q, mat[k,p]))
//                        }
                    }
                }
                j = if (i <= l) {
                    break
                } else {
                    i--
                    k--
                    k
                }
            }
        }
        val zero = mc.zero
        for (r in 0 until m.row) {
            for (c in 0 until k) {
                mat[r, c] = zero
            }
        }
        return mat
    }

    fun <T> toLatexString(M: Matrix<T>, formatter: NumberFormatter<T> = NumberFormatter.defaultFormatter(), displayType: String = "pmatrix"): String = buildString {
        val mc = M.mathCalculator
        append("\\begin{$displayType}")
        appendLine()
        for (i in 0 until M.row) {
            M.colIndices.joinTo(this, separator = " & ", postfix = "\\\\") { j ->
                formatter.format(M[i, j], mc)
            }
            appendLine()
        }
        append("\\end{$displayType}")
    }
}

