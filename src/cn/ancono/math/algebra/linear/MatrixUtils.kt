package cn.ancono.math.algebra.linear

import cn.ancono.math.algebra.abs.calculator.EUDCalculator
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.UnitRingCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.api.*
import java.util.*


/**
 *
 */
@Suppress("LocalVariableName") // we often use upper letter to name a matrix
object MatrixUtils {
    /**
     * Returns the QR-decomposition of a square matrix `A = QR`, where `Q` is an orthogonal matrix and `R` is an
     * upper-triangle matrix. If this matrix is invertible, there is only one decomposition.
     *
     * @return `(Q, R)` as a pair
     */
    fun <T> decompQR(A: AbstractMatrix<T>): Pair<Matrix<T>, Matrix<T>> {
        //Re-written by lyc at 2021-04-30 13:00
        A.requireSquare()
        val vs = A.columnVectors()
        val mc = A.calculator
        val R = Matrix.zero(A.row, A.column, mc)
        val ws = ArrayList<MutableVector<T>>(A.row)
        for (i in 0 until A.row) {
            val u = Vector.copyOf(vs[i])
            for (j in 0 until i) {
                val k = u.inner(ws[j])
                u.addMulAssign(mc.negate(k), ws[j])
                R[j, i] = k
            }
            if (!u.isZero()) {
                val length = u.norm()
                R[i, i] = length
                u.divAssign(length)
            }
            ws += u
        }
        val Q = Matrix.fromVectors(ws)
        return Q to R
    }

    private fun <T> checkSymmetric(A: AbstractMatrix<T>) {
        A.requireSquare()
        val mc = A.calculator
        for (i in 0 until A.row) {
            for (j in 0 until i) {
                require(mc.isEqual(A[i, j], A[j, i])) {
                    "Not symmetric!"
                }
            }
        }
    }

    /**
     * Returns the congruence diagonal normal form `J` of matrix `A` and the corresponding transformation `P`,
     * which satisfies
     *
     *     P.T * A * P = J
     *
     * @return `(J, P)`.
     */
    fun <T> toCongDiagonalForm(A: AbstractMatrix<T>): Pair<Matrix<T>, Matrix<T>> {
        checkSymmetric(A)
        //Re-written by lyc at 2021-04-30 13:00
        val n = A.row
        val mc = A.calculator as FieldCalculator
        val x = AMatrix.zero(2 * n, n, A.calculator)
        x.setAll(0, 0, A)
        val one = mc.one
        for (i in 0 until n) {
            x[i + n, i] = one
        }
        var pos = 0
        while (pos < n) {
//            println(x)
            if (mc.isZero(x[pos, pos])) {
                var pi = -1
                var pj = -1
                SEARCH@ for (i in pos until n) {
                    for (j in pos..i) {
                        if (!mc.isZero(x[j, j])) {
                            pi = i
                            pj = j
                            break@SEARCH
                        }
                    }
                }
                if (pj < 0) {
                    break
                }
                if (pj != pos) {
                    x.multiplyAddRow(pj, pos, one)
                    x.multiplyAddCol(pj, pos, one)
                }
                x.multiplyAddRow(pi, pos, one)
                x.multiplyAddCol(pi, pos, one)

            }
            for (i in pos + 1 until n) {
                if (mc.isZero(x[pos, i])) {
                    continue
                }
                val k = mc.negate(mc.divide(x[pos, i], x[pos, pos]))
                x.multiplyAddRow(pos, i, k)
                x.multiplyAddCol(pos, i, k)
            }
            pos++
        }
        val m1 = x.subMatrix(0, 0, n, n)
        val m2 = x.subMatrix(n, 0, 2 * n, n)
        return m1 to m2
    }

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
    fun <T> decompositionLU(m: AbstractMatrix<T>): Triple<Matrix<T>, Matrix<T>, Matrix<T>> {
        require(m.isSquare()) {
            "The matrix must be square!"
        }
        val mc = m.calculator as RealCalculator
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
                    matrix[i, k] / matrix[k, k]
                }
                l[i, k] = lambda
                matrix[i, k] = mc.zero
                matrix.multiplyAddRow(k, i, mc.negate(lambda), k + 1)
//                for (j in (k + 1) until m.column) {
//                    matrix[i,j] = mc.eval {
//                        matrix[i,j] - lambda * matrix[k][j]
//                    }
//                }
            }
        }
        return Triple(
                Matrix.fromVectors(p, false),
                l,
                matrix
        )
    }

    /**
     * Decomposes a symmetric semi-positive definite matrix `A = L L^T`, where
     * `L` is a lower triangular matrix.
     *
     * @return a lower triangular matrix `L`.
     */
    fun <T> decompositionCholesky(A: AbstractMatrix<T>): Matrix<T> {
        require(A.isSquare()) {
            "The matrix must be square!"
        }
        val mc = A.calculator as RealCalculator
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
     * Decomposes a symmetric matrix `A = L D L^T`, where
     * `L` is a lower triangular matrix and `D` is a diagonal matrix.
     *
     * @return `(L, diag(D))`, where `L` is a lower triangular matrix, `diag(D)` is a vector of diagonal elements
     * of `D`.
     */
    fun <T> decompositionCholeskyD(A: AbstractMatrix<T>): Pair<Matrix<T>, Vector<T>> {
        require(A.isSquare()) {
            "The matrix must be square!"
        }
        val mc = A.calculator as FieldCalculator
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
    fun <T> inverseInEUD(M: AbstractMatrix<T>): Matrix<T> {
        //TODO check correctness
        M.requireSquare()
        val n = M.column
        val mc = M.calculator as FieldCalculator

        @Suppress("UNCHECKED_CAST")
        val euc = M.calculator as EUDCalculator<T>


        @Suppress("UNCHECKED_CAST")
        val A = Matrix(n, 2 * n, mc) { i, j ->
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
            }
        }

        return A.subMatrix(0, n, M.row, M.column)


    }

    /**
     * Transform this matrix to Hermit Form. It is required that the calculator is a
     * [cn.ancono.math.algebra.abs.calculator.EUDCalculator].
     */
    fun <T> toHermitForm(m: AbstractMatrix<T>): Matrix<T> {
        val mc = m.calculator as IntCalculator<T>
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

    /**
     * Computes the 'inverse' of the given matrix on a unit ring. This method simply compute the adjugate matrix and
     * divide it with the determinant (so it is time-consuming).
     *
     * This method can be used to compute the modular inverse of a matrix on `Z/Zn`, where n is not necessarily a prime.
     */
    fun <T> inverseInRing(M: AbstractMatrix<T>): Matrix<T> {
        val rc = M.calculator as UnitRingCalculator<T>
        val det = M.det()
        if (!rc.isUnit(det)) {
            ExceptionUtil.notInvertible()
        }
        val adj = M.adjoint().toMutable()
        adj.divAssign(det)
        return adj
    }


    fun <T> toLatexString(M: Matrix<T>, formatter: NumberFormatter<T> = NumberFormatter.defaultFormatter(),
                          displayType: String = "pmatrix"): String = buildString {
        append("\\begin{$displayType}")
        appendLine()
        for (i in 0 until M.row) {
            M.colIndices.joinTo(this, separator = " & ", postfix = "\\\\") { j ->
                formatter.format(M[i, j])
            }
            appendLine()
        }
        append("\\end{$displayType}")
    }
}

