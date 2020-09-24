package cn.ancono.math.algebra.linearAlgebra

import cn.ancono.math.algebra.abstractAlgebra.calculator.eval
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
        require(m.isSquare)
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
}

