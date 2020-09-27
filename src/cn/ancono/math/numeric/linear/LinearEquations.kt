package cn.ancono.math.numeric.linear

import cn.ancono.math.algebra.abstractAlgebra.calculator.eval
import cn.ancono.math.algebra.linearAlgebra.Matrix
import cn.ancono.math.algebra.linearAlgebra.MatrixOperation
import cn.ancono.math.algebra.linearAlgebra.MatrixSup
import cn.ancono.math.algebra.linearAlgebra.Vector

object LinearEquations {

    /**
     * Solves linear equation `Ux = b`, where `U` is an upper triangular matrix with **non-zero** diagonal elements.
     * @param U an upper triangular matrix with **non-zero** diagonal elements.
     */
    fun <T : Any> solveUpper(U: Matrix<T>, b: Vector<T>) : Vector<T> {
        require(U.isSquare)
        require(U.rowCount == b.size)
        val mc = U.mathCalculator
        val n = U.rowCount
        @Suppress("UNCHECKED_CAST")
        val x = Array<Any>(n){
            mc.zero
        } as Array<T>
        for (i in (n-1) downTo 0) {
            var t = mc.zero
            for (k in (i + 1) until n) {
                t = mc.eval { t + U[i,k] * x[k] }
            }
            x[i] = mc.eval { (b[i]- t) / U[i,i] }
        }
        return Vector.of(mc,*x)
    }

    /**
     * Solves linear equation `Lx = b`, where `L` is an lower triangular matrix with **non-zero**
     * diagonal elements.
     * @param L  an lower triangular matrix with **non-zero**
     * diagonal elements
     */
    fun <T : Any> solveLower(L: Matrix<T>, b: Vector<T>) : Vector<T> {
        require(L.isSquare)
        require(L.rowCount == b.size)
        val mc = L.mathCalculator
        val n = L.rowCount
        val x = ArrayList<T>(n)
        for (i in 0 until n) {
            var t = mc.zero
            for (k in 0 until i) {
                t = mc.eval { t + L[i,k] * x[k] }
            }
            x += mc.eval { (b[i]- t) / L[i,i] }
        }
        return Vector.of(mc,x)
    }


    /**
     * Solve the matrix equation `AX=B` represented by the augmented matrix `M = (A,B)` using
     * Gauss-Jordan elimination method, choosing the element with the maximal absolute value
     * in the column as pivot element.
     *
     * It is required that the matrix `A` is **invertible**.
     *
     * @param M augmented matrix `M = (A,B)`
     * @see Matrices.inverseGaussJordanSteps
     */
    fun <T:Any> solveGauss(M : Matrix<T>) : Matrix<T>{
//        require(m.isSquare)
        require(M.columnCount > M.rowCount)
        val n = M.rowCount
        val mc = M.mathCalculator

        @Suppress("UNCHECKED_CAST")
        val matrix = M.values as Array<Array<T>>
        for (k in 0 until n) {
            var maxIdx = k
            var maxVal = mc.abs(matrix[k][k])
            for (i in (k + 1) until n) {
                val v = mc.abs(matrix[i][k])
                if (mc.compare(v, maxVal) > 0) {
                    maxIdx = i
                    maxVal = v
                }
            }
            if (maxIdx != k) {
                MatrixSup.exchangeRow(matrix, k, maxIdx)
            }
            val c = mc.reciprocal(matrix[k][k])
//            matrix[k][k] = mc.one
            for (j in (k + 1) until M.columnCount) {
                matrix[k][j] = mc.eval {
                    c * matrix[k][j]
                }
            }
            for (i in 0 until n) {
                if (i == k) {
                    continue
                }
                val p = mc.negate(matrix[i][k])
//                matrix[i][k] = mc.zero
                for (j in (k + 1) until M.columnCount) {
                    matrix[i][j] = mc.eval {
                        matrix[i][j] + p * matrix[k][j]
                    }
                }
            }
//            Printer.printMatrix(matrix)
        }
//        var re = Array
        val builder = Matrix.getBuilder(n,M.columnCount - n,mc)
//        for (i in 0 until n) {
//            for (j in n until M.columnCount) {
//                builder.set(matrix[i][j],i,j-n)
//            }
//        }
        builder.fillArea(0,0,matrix,0,n,n,M.columnCount-n)
        return builder.build()
    }
}