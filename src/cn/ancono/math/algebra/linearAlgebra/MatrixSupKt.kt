package cn.ancono.math.algebra.linearAlgebra

import cn.ancono.math.algebra.abstractAlgebra.calculator.eval
import cn.ancono.math.get
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
        require(m.isSquare){
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
            l.set(mc.one, k, k)
            for (i in (k + 1) until m.rowCount) {
                val lambda = mc.eval {
                    matrix[i][k] / matrix[k][k]
                }
                l.set(lambda, i, k)
                matrix[i][k] = mc.zero
                for (j in (k + 1) until m.columnCount) {
                    matrix[i][j] = mc.eval {
                        matrix[i][j] - lambda * matrix[k][j]
                    }
                }
            }
        }
        return Triple(Matrix.fromVectors(true, p),
                l.build(),
                Matrix.valueOfNoCopy(matrix, mc))
    }

    /**
     * @see Matrix.decompCholesky
     */
    fun <T : Any> decompositionCholesky(A: Matrix<T>): Matrix<T> {
        require(A.isSquare){
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
                var a = A[i][j]
                for (k in 0 until j) {
                    a = mc.eval { a - l[i][k] * l[j][k] }
                }
                a = mc.eval { a / t }
                l[i][j] = a
                // l_{ij} = (a_{ij} - sum(0,j-1,l_{il}l_{jl}))/l_{jj}
            }
        }
        return Matrix.valueOfNoCopy(l,mc)
    }

    /**
     * @see Matrix.decompCholesky
     */
    fun <T : Any> decompositionCholeskyD(A: Matrix<T>): Pair<Matrix<T>, Vector<T>> {
        require(A.isSquare){
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
                var a = A[i][j]
                for (k in 0 until j) {
                    a = mc.eval { a - l[i][k] * l[j][k] * d[k] }
                }
                l[i][j] = mc.eval { a / t }
                // l_{ij} = (a_{ij} - sum(0,j-1,d_k * l_{ik}l_{jk}))
            }
        }
        val L = Matrix.valueOfNoCopy(l,mc)
        val D = Vector.of(mc,d)
        return Pair(L,D)
    }
}

