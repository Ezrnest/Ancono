package cn.ancono.math.numeric.linear

import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.MatrixOperation
import cn.ancono.math.algebra.linear.MatrixSup

object Matrices {

    /**
     * Computes the sequence of elementary operations transforming the given matrix to identity matrix with
     * Gauss-Jordan method. The method is generally the same as Gauss elimination method, but it will
     * choose the element with maximal absolute value in the leading column as the pivot.
     *
     * This method requires that the given matrix is invertible.
     *
     * **Note**: This method is not designed for numerical computation but for demonstration.
     *
     * For example, assume the matrix is
     *
     *     1 2 3
     *     4 8 4
     *     1 0 2
     *
     * then the operations are:
     *
     *     (swap row 0,1)
     *     4 8 4
     *     1 2 3
     *     1 0 2
     *     -----
     *     (multiple row 0 by 1/4)
     *     1 2 1
     *     1 2 3
     *     1 0 2
     *     -----
     *     (multiple row 0 by -1 and add it to row 1)
     *     1 2 1
     *     0 0 2
     *     1 0 2
     *     -----
     *     (multiple row 0 by -1 and add it to row 2)
     *     1 2 1
     *     0 0 2
     *     0 -2 1
     *     -----
     *     (swap row 1,2)
     *     1 2 1
     *     0 -2 1
     *     0 0 2
     *     ----
     *     (multiple row 1 by -1/2)
     *     1 2 1
     *     0 1 -1/2
     *     0 0 2
     *     ----
     *     (multiple row 1 by -2 and add it to row 0)
     *     1 0 2
     *     0 1 -1/2
     *     0 0 2
     *     ----
     *     (multiple row 2 by 1/2)
     *     1 0 2
     *     0 1 -1/2
     *     0 0 1
     *     ----
     *     (multiple row 2 by -2 and add it to row 0)
     *     1 0 0
     *     0 1 -1/2
     *     0 0 1
     *     ----
     *     (multiple row 2 by 1/2 and add it to row 1)
     *     1 0 0
     *     0 1 0
     *     0 0 1
     * @param m an invertible matrix
     */
    fun <T : Any> inverseGaussJordanSteps(m: Matrix<T>): List<MatrixOperation<T>> {
        require(m.isSquare)
        val mc = m.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val matrix = m.values as Array<Array<T>>
        val operations = mutableListOf<MatrixOperation<T>>()
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
                operations += MatrixOperation.exchangeRow<T>(k, maxIdx)
            }
            val c = mc.reciprocal(matrix[k][k])
//            matrix[k][k] = mc.one
            for (j in (k + 1) until m.columnCount) {
                matrix[k][j] = mc.eval {
                    c * matrix[k][j]
                }
            }
            operations += MatrixOperation.multiplyRow(k, c)
            for (i in 0 until m.rowCount) {
                if (i == k) {
                    continue
                }
                val p = mc.negate(matrix[i][k])
//                matrix[i][k] = mc.zero
                for (j in (k + 1) until m.columnCount) {
                    matrix[i][j] = mc.eval {
                        matrix[i][j] + p * matrix[k][j]
                    }
                }
                operations += MatrixOperation.multiplyAddRow(k, i, p)
            }
//            Printer.printMatrix(matrix)
        }

        return operations
    }


    /**
     * Returns the condition number of the matrix defined by p-norm, which is defined by:
     *
     *     |A|_p |A^(-1)|_p
     *
     * If the given [p] is `null`, then infinity norm will
     * be used.
     *
     * It is required that the given matrix is invertible.
     *
     * @param m an invertible matrix
     * @param p a number `>=1`, or `null`.
     */
    fun <T : Any> cond(m : Matrix<T>, p : T?) : T{
        val mc = m.mathCalculator
        val n = m.inverse()

        return mc.eval{
            m.normP(p) * n.normP(p)
        }
    }

}

//fun main() {
//    val m = MatrixSup.parseFMatrix(
//            """
//            2 1 -3 -1
//            3 1 0 7
//            -1 2 4 -2
//            1 0 -1 5
//        """.trimIndent()
//    )
//    val steps = LinearEquations.inverseGaussJordanSteps(m)
//    var A = m
//    var B = Matrix.identity(m.rowCount,m.mathCalculator)
//    for (s in steps) {
//        A = A.doOperation(s)
//        A.printMatrix()
//        B = B.doOperation(s)
//        B.printMatrix()
//        Printer.print_()
//    }
//
//}