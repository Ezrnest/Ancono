package cn.ancono.math.algebra.linear

import cn.ancono.math.MathCalculator
import cn.ancono.math.numberModels.api.colIndices
import cn.ancono.math.numberModels.api.rowIndices
import java.util.*


/*
 * Created by liyicheng at 2021-04-27 19:33
 */


class TransposeMatrixView<T>(
        val origin: AbstractMatrix<T>) :
        Matrix<T>(origin.mathCalculator, origin.column, origin.row) {
    override fun getChecked(i: Int, j: Int): T {
        return origin[j, i]
    }

    override fun getColumn(col: Int): Vector<T> {
        return origin.getRow(col)
    }

    override fun getRow(row: Int): Vector<T> {
        return origin.getColumn(row)
    }

    override fun transpose(): Matrix<T> {
        if (origin is Matrix) {
            return origin
        }
        return Matrix.copyOf(origin)
    }
}

class SubMatrixView<T>
internal constructor(mc: MathCalculator<T>, row: Int, column: Int,
                     val dRow: Int, val dCol: Int, val m: AbstractMatrix<T>) : Matrix<T>(mc, row, column) {
    override fun getChecked(i: Int, j: Int): T {
        return m[i + dRow, j + dCol]
    }

    companion object {
        fun <T> subMatrixOf(m: AbstractMatrix<T>, rowStart: Int, rowEnd: Int, colStart: Int, colEnd: Int): SubMatrixView<T> {
            val r = rowEnd - rowStart
            val c = colEnd - colStart
            require(0 <= rowStart && rowEnd <= m.row && r > 0)
            require(0 <= colStart && colEnd <= m.row && c > 0)
            return SubMatrixView(m.mathCalculator, r, c, rowStart, colStart, m)
        }
    }
}

class FactorMatrixView<T>
internal constructor(
        val rowMap: IntArray, val columnMap: IntArray,
        val m: AbstractMatrix<T>)
    : Matrix<T>(m.mathCalculator, rowMap.size, columnMap.size) {

    override fun getChecked(i: Int, j: Int): T {
        return m[rowMap[i], columnMap[j]]
    }

    companion object {
        fun <T> factorOf(m: AbstractMatrix<T>, rows: IntArray, columns: IntArray): FactorMatrixView<T> {
            return factorOf(m, rows.toSortedSet(), columns.toSortedSet())
        }

        fun <T> factorOf(m: AbstractMatrix<T>, rs: SortedSet<Int>, cs: SortedSet<Int>): FactorMatrixView<T> {
            require(rs.isNotEmpty() && cs.isNotEmpty())
            require(rs.all { it in m.rowIndices })
            require(cs.all { it in m.colIndices })
            return FactorMatrixView(rs.toIntArray(), cs.toIntArray(), m)
        }

        fun <T> cofactorOf(m: AbstractMatrix<T>, rows: IntArray, columns: IntArray): FactorMatrixView<T> {

            //            val cs = columns.toSet()
//
//            require(cs.all { it in m.colIndices } && cs.size < m.column)
//            val rowMap = IntArray(m.row - rs.size)
//            val colMap = IntArray(m.column - cs.size)
            fun makeMap(rows: IntArray, row: Int): IntArray {
                val rs = rows.toSet()
                require(rs.all { it in m.rowIndices } && rs.size < m.row)
                val rowMap = IntArray(row - rs.size)
                var l = 0
                for (i in m.rowIndices) {
                    if (i !in rs) {
                        rowMap[l++] = i
                    }
                }
                return rowMap
            }

            val rowMap = makeMap(rows, m.row)
            val colMap = makeMap(columns, m.column)
            return FactorMatrixView(rowMap, colMap, m)
        }


    }
}