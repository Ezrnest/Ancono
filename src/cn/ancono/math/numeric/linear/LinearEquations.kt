package cn.ancono.math.numeric.linear

import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.component1
import cn.ancono.math.component2
import java.util.*
import kotlin.collections.ArrayList

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
        val builder = Matrix.getBuilder(n, M.columnCount - n, mc)
//        for (i in 0 until n) {
//            for (j in n until M.columnCount) {
//                builder.set(matrix[i][j],i,j-n)
//            }
//        }
        builder.fillArea(0, 0, matrix, 0, n, n, M.columnCount - n)
        return builder.build()
    }

    private fun <T : Any> solveDiagonal(D: Matrix<T>, b: Vector<T>): Vector<T> {
        val n = b.size
        val mc = b.mathCalculator
        return Vector.of(b.mathCalculator, (0 until n).map { i ->
            mc.divide(b[i], D[i, i])
        })
    }

    /**
     * Applies Cholesky's method to solve the linear equation `Ax=b`.
     */
    fun <T : Any> solveCholesky(A: Matrix<T>, b: Vector<T>): Vector<T> {
        val (L, D) = A.decompCholeskyD()
        val y = solveLower(L, b)
        val z = solveDiagonal(D, y)
        return solveUpper(L.transpose(), z)
    }

    /**
     * Solves a tri-diagonal linear equation.
     */
    fun <T : Any> solveTriDiag(diag: Vector<T>, upper: Vector<T>, lower: Vector<T>, b: Vector<T>): Vector<T> {
        val n = diag.size
        require(upper.size == n - 1 && lower.size == n - 1)
        require(b.size == n)
        val mc = diag.mathCalculator
        val u = ArrayList<T>(n)
        val l = ArrayList<T>(n)
        val c = upper

        u.add(diag[0])
        l.add(mc.zero)
        for (i in 1 until n) {
            l += mc.eval { lower[i - 1] / u.last() }
            u += mc.eval { diag[i] - l[i] * c[i - 1] }
        }

        val y = ArrayList<T>(n)
        y += b[0]
        for (i in 1 until n) {
            y += mc.eval { b[i] - l[i] * y[i - 1] }
        }

        val x = ArrayList<T>(Collections.nCopies(n, mc.zero))
        x[n - 1] = mc.eval { y[n - 1] / u[n - 1] }
        for (i in n - 2 downTo 0) {
            x[i] = mc.eval {
                (y[i] - c[i] * x[i + 1]) / u[i]
            }
        }
        return Vector.of(mc, x)
    }


}