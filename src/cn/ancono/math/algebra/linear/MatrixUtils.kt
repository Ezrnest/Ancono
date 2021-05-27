package cn.ancono.math.algebra.linear

import cn.ancono.math.algebra.abs.calculator.*
import cn.ancono.math.exceptions.ExceptionUtil
import cn.ancono.math.numberModels.api.*
import java.util.*
import kotlin.math.min


/**
 *
 */
@Suppress("LocalVariableName") // we often use upper letter to name a matrix
object MatrixUtils {
    private fun <T> decompQR0(A: AbstractMatrix<T>): Pair<Matrix<T>, MutableMatrix<T>> {
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


    /**
     * Returns the QR-decomposition of a square matrix `A = QR`, where `Q` is an orthogonal matrix and `R` is an
     * upper-triangle matrix. If this matrix is invertible, there is only one decomposition.
     *
     * @return `(Q, R)` as a pair
     */
    fun <T> decompQR(A: AbstractMatrix<T>): Pair<Matrix<T>, Matrix<T>> {
        return decompQR0(A)
    }

    /**
     * Returns the QR-decomposition of a square matrix `A = KAN`, where `K` is an orthogonal matrix, `D` diagonal and
     * `R` upper-triangle matrix.
     * If this matrix is invertible, there is only one decomposition.
     *
     * @return `(K,A,N)` as a triple
     */
    fun <T> decompKAN(A: AbstractMatrix<T>): Triple<Matrix<T>, Vector<T>, Matrix<T>> {
        //Created by lyc at 2021-05-11 20:25
        val (Q, R) = decompQR0(A)
        val d = R.diag()
        val one = (A.calculator as UnitRingCalculator).one
        for (i in 0 until R.row) {
            R[i, i] = one
            R.divideRow(i, d[i], i + 1)
        }
        return Triple(Q, d, R)
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
        val mc = m.calculator as OrderedFieldCal
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
     *
     * It is required that the calculator of `M` is an instance of EUDCalculator.
     */
    fun <T> inverseInEUD(M: AbstractMatrix<T>): Matrix<T> {
        //TODO check correctness
        M.requireSquare()
        val n = M.column
        val mc = M.calculator as EUDCalculator<T>

        val A = Matrix.zero(n, 2 * n, mc)
        A.setAll(0, 0, M)
        for (i in 0 until n) {
            A[i, i + n] = mc.one
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
                    // gcd
                    val (q, r) = mc.divideAndRemainder(A[i, j], p)
                    A.multiplyAddRow(j, i, mc.negate(q), j)
//                    Printer.printMatrix(A)
                    if (mc.isZero(r)) {
                        i++
                        continue
                    }
                    A.swapRow(j, i, j)
                    continue@outer
                }
                if (!mc.isUnit(p)) {
                    ExceptionUtil.notInvertible()
                }
                A[j, j] = mc.one
                A.divideRow(j, p, j + 1)
                break
            }

        }

        for (j1 in (n - 1) downTo 1) {
            for (j2 in 0 until j1) {
                val k = mc.negate(A[j2, j1])
                A.multiplyAddRow(j1, j2, k, j1)
            }
        }
        return A.subMatrix(0, n, A.row, A.column)


    }


    internal fun <T> toUpperEUD0(M: MutableMatrix<T>, column: Int = M.column): List<Int> {
        val mc = M.calculator as EUDCalculator<T>
        val row = M.row
        var i = 0
        val pivots = ArrayList<Int>(min(M.row, column))
        for (j in 0 until column) {
            if (i >= row) {
                break
            }
            var found = false
            for (i2 in i until row) {
                if (mc.isZero(M[i2, j])) {
                    continue
                }
                found = true
                if (i2 != i) {
                    M.swapRow(i2, i)
                }
                break
            }
            if (!found) {
                //not found
                continue
            }
            for (i2 in (i + 1) until row) {
                if (mc.isZero(M[i2, j])) {
                    continue
                }
                val a = M[i, j]
                val b = M[i2, j]
                val (d, u, v) = mc.gcdUVMin(a, b)
                // uni-modular transform
                val a1 = mc.divideToInteger(M[i, j], d)
                val b1 = mc.divideToInteger(M[i2, j], d)
                M.transformRows(i, i2, u, v, mc.negate(b1), a1, j)
            }
            pivots += j
            i++
        }
        return pivots
    }

    internal fun <T> toEchelonEUD0(M: MutableMatrix<T>, column: Int = M.column): List<Int> {
        val pivots = toUpperEUD0(M, column)
        val mc = M.calculator as EUDCalculator<T>
        for (i in pivots.lastIndex downTo 0) {
            val j = pivots[i]
            val d = M[i, j]
            for (k in (i - 1) downTo 0) {
                if (mc.isZero(M[k, j])) {
                    continue
                }
                val q = mc.eval { -divideToInteger(M[k, j], d) }
                M.multiplyAddRow(i, k, q, j)
            }
        }
        return pivots
    }

    internal fun <T> toHermitForm0(M: MutableMatrix<T>, column: Int = M.column): List<Int> {
        val pivots = toUpperEUD0(M, column)
        val mc = M.calculator as IntCalculator<T>
        for (i in pivots.lastIndex downTo 0) {
            val j = pivots[i]
            if (mc.isNegative(M[i, j])) {
                M.negateRow(i)
            }
            val d = M[i, j]
            for (k in (i - 1) downTo 0) {
                if (mc.isZero(M[k, j])) {
                    continue
                }
                var q = mc.eval { -divideToInteger(M[k, j], d) }
                if (mc.isNegative(M[k, j])) {
                    q = mc.increase(q)
                }
                M.multiplyAddRow(i, k, q, j)
            }
        }
        return pivots
    }

    /**
     * Transform this matrix to (row) Hermit Form. It is required that the calculator is an
     * [IntCalculator].
     */
    fun <T> toHermitForm(A: AbstractMatrix<T>): Matrix<T> {
        val M = A.toMutable()
        toHermitForm0(M)
        return M
    }

    fun <T> toHermitFormU(m: AbstractMatrix<T>): Pair<Matrix<T>, Matrix<T>> {
        val n = m.row
        val col = m.column
        val mc = m.calculator as IntCalculator
        val expanded = AMatrix.zero(n, n + col, mc)
        expanded.setAll(0, 0, m)
        for (i in 0 until n) {
            expanded[i, i + col] = mc.one
        }
        toHermitForm0(expanded, column = col)
        val H = expanded.subMatrix(0, 0, n, col)
        val U = expanded.subMatrix(0, col, n, n + col)
        return H to U
    }

    fun <T> toEchelonEUD(A: AbstractMatrix<T>): Matrix<T> {
        val M = A.toMutable()
        toEchelonEUD0(M)
        return M
    }

    fun <T> toEchelonEUDU(m: AbstractMatrix<T>): Pair<Matrix<T>, Matrix<T>> {
        val n = m.row
        val col = m.column
        val mc = m.calculator as EUDCalculator
        val expanded = AMatrix.zero(n, n + col, mc)
        expanded.setAll(0, 0, m)
        for (i in 0 until n) {
            expanded[i, i + col] = mc.one
        }
        toEchelonEUD0(expanded, column = col)
        val H = expanded.subMatrix(0, 0, n, col)
        val U = expanded.subMatrix(0, col, n, n + col)
        return H to U
    }


    fun <T> kernelLattice(m: AbstractMatrix<T>): List<Vector<T>> {
        val M = m.toMutable()
        val pivots = toEchelonEUD0(M)
        return MatrixImpl.nullSpaceGenerator(M, m.column, pivots)

    }

//    fun <T> toHermitFormU()

    /**
     * Computes the 'inverse' of the given matrix over a unit ring. This method simply compute the adjoint matrix and
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

