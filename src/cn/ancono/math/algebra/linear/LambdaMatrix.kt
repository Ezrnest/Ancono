package cn.ancono.math.algebra.linear

import cn.ancono.math.*
import cn.ancono.math.algebra.AlgebraUtil
import cn.ancono.math.algebra.abs.calculator.EUDCalculator
import cn.ancono.math.algebra.abs.calculator.RingCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.structure.Polynomial
import java.util.*
import kotlin.math.min


sealed class LambdaPrimaryOperation<T : Any>(val isRow: Boolean)

class Swap<T : Any>(isRow: Boolean, val idx1: Int, val idx2: Int) : LambdaPrimaryOperation<T>(isRow)

class Multiply<T : Any>(isRow: Boolean, val idx: Int, val k: T) : LambdaPrimaryOperation<T>(isRow)

class MultiplyAdd<T : Any>(isRow: Boolean, val idx1: Int, val idx2: Int, val k: Polynomial<T>) : LambdaPrimaryOperation<T>(isRow) {
    init {
        require(idx1 != idx2) {
            "idx=$idx1 must be different!"
        }
    }
}
typealias LambdaMatrix<T> = Matrix<Polynomial<T>>
private typealias PData<T> = Array<Array<Polynomial<T>>>
private typealias PMC<T> = MathCalculator<Polynomial<T>>

/**
 * Transform this lambda matrix to its normal form.
 */
fun <T : Any> LambdaMatrix<T>.toNormalForm(): LambdaMatrix<T> {
    require(row == column)
    @Suppress("UNCHECKED_CAST")
    val data = Array<Array<Polynomial<T>>>(row) { i ->
        Array(column) { j ->
            get(i, j)
        }
    }
    toNormalForm(data, mathCalculator as Polynomial.PolynomialCalculator<T>, 0)
    return DMatrix(data, row, column, mathCalculator)
}

private fun <T : Any> normalFormInvFac(mat: LambdaMatrix<T>): List<Polynomial<T>> {
    val list = arrayListOf<Polynomial<T>>()
    for (i in 0 until mat.column) {
        val t = mat[i, i]
        if (t.isOne) {
            continue
        }
        list.add(t)
    }
    return list
}

fun <T : Any> LambdaMatrix<T>.invariantFactor(): List<Polynomial<T>> {
    return normalFormInvFac(toNormalForm())
}

@Suppress("UNCHECKED_CAST")
object LambdaMatrixSup {
    @JvmStatic
    fun jordanFormAndTrans(matrix: Matrix<Fraction>): Pair<Matrix<Fraction>, Matrix<Fraction>>? {
        val lamM = matrix.charMatrix()
        val nForm = lamM.toNormalForm()
        val invFac = normalFormInvFac(nForm)
        val primaryFactor = TreeMap<Fraction, ArrayList<Int>>()
        for (p in invFac) {
            val des = AlgebraUtil.decomposeFrac(p)
            for (pr in des.decomposed) {
                if (pr.first.degree >= 2) {
                    return null
                }
                val lambda = pr.first[0].negate()
                primaryFactor.compute(lambda) { _, arr ->
                    if (arr == null) {
                        arrayListOf(pr.second)
                    } else {
                        arr.add(pr.second)
                        arr
                    }
                }
            }
        }
        val jordan = buildJordanForm(primaryFactor, matrix)
        val trans = arrayListOf<Vector<Fraction>>()
        for (pr in primaryFactor) {
            val lambda = pr.key
            val mat = matrix - Matrix.diag(lambda, matrix.column, matrix.mathCalculator)
            val solutions = mat.solutionSpace()
            assert(solutions.vectorLength == pr.value.size)
            for ((alpha, n) in solutions.vectors.asSequence().zip(pr.value.asSequence())) {
                trans += alpha
                var prev = alpha
                repeat(n - 1) {
                    val solu = MatrixSup.solveLinearEquation(mat, prev.toColumnVector())
                    prev = solu.specialSolution
                    trans += prev
                }
            }
        }
        val transMat = Matrix.fromVectors(false, trans)
        return jordan to transMat
    }

    @JvmStatic
    fun <T : Any> toSmithForm(matrix: Matrix<T>): Matrix<T> {
        require(matrix.isSquare)
        @Suppress("UNCHECKED_CAST")
        val data = Array(matrix.row) { i ->
            Array<Any>(matrix.column) { j ->
                matrix.get(i, j)
            }
        } as Array<Array<T>>

        toNormalForm(data, matrix.mathCalculator as EUDCalculator<T>, 0)
        return DMatrix(data, matrix.row, matrix.column, matrix.mathCalculator)
    }
}


private fun buildJordanForm(primaryFactor: Map<Fraction, List<Int>>, origin: Matrix<Fraction>): Matrix<Fraction> {
    val builder = Matrix.getBuilder(origin.row, origin.column, origin.mathCalculator)
    var idx = 0
    for (pr in primaryFactor) {
        val lambda = pr.key
        for (n in pr.value) {
            for (i in 0 until n) {
                builder.set(lambda, idx + i, idx + i)
            }
            for (i in 0 until n - 1) {
                builder.set(Fraction.ONE, idx + i, idx + i + 1)
            }
            idx += n
        }
    }
    return builder.build()
}

/**
 * Transform this lambda matrix to its normal form.
 */
fun <T : Any> LambdaMatrix<T>.toFrobeniusForm(mc: MathCalculator<T>): Matrix<T> {
    @Suppress("UNCHECKED_CAST")
    val data = Array<Array<Polynomial<T>>>(row) { i ->
        Array(column) { j ->
            get(i, j)
        }
    }
//    toNormalForm(data,mathCalculator,)
    toNormalForm(data, mathCalculator as Polynomial.PolynomialCalculator<T>, 0)
    val builder = Matrix.getBuilder(row, column, mc)
    var pos = 0
    val one = mc.one
    for (i in 0 until min(row, column)) {
        val t = data[i][i]
        if (t.isConstant) {
            continue
        }
        val deg = t.degree
        for (j in 0 until deg) {
            builder.set(mc.negate(t[j]), pos + j, pos + deg - 1)
        }
        for (j in 0 until (deg - 1)) {
            builder.set(one, pos + 1 + j, pos + j)
        }
        pos += deg
    }
    return builder.build()
}


//internal fun <T : Any> toNormalForm(data: PData<T>, mc: Polynomial.PolynomialCalculator<T>, fromIdx: Int) {
//    if (fromIdx >= data.size) {
//        return
//    }
//    processFirst(data, fromIdx, mc)
//
//    for (i in (fromIdx + 1) until data.size) {
//        for (j in (fromIdx + 1) until data.size) {
//            while (!doDivide(data, fromIdx, i, j, mc)) {
//                processFirst(data, fromIdx, mc)
//            }
//        }
//    }
//    val head = data[fromIdx][fromIdx]
//    if (!head.isZero()) {
//        data[fromIdx][fromIdx] = head.monic()
//    }
//    toNormalForm(data, mc, fromIdx + 1)

//}

internal fun <T : Any> toNormalForm(data: Array<Array<T>>, mc: EUDCalculator<T>, fromIdx: Int) {
    if (fromIdx >= data.size) {
        return
    }
    processFirst(data, fromIdx, mc)

    for (i in (fromIdx + 1) until data.size) {
        for (j in (fromIdx + 1) until data.size) {
            while (!doDivide(data, fromIdx, i, j, mc)) {
                processFirst(data, fromIdx, mc)
            }
        }
    }
    toNormalForm(data, mc, fromIdx + 1)
}


private fun <T : Any> processFirst(data: Array<Array<T>>, fromIdx: Int, mc: EUDCalculator<T>) {
    var i = fromIdx + 1
    while (i < data.size) {
        if (!doDivideCol(data, fromIdx, i, mc)) {
            i = fromIdx + 1
            continue
        }
        if (!doDivideRow(data, fromIdx, i, mc)) {
            i = fromIdx + 1
            continue
        }
        i++
    }
}

private fun <T : Any> doDivideCol(data: Array<Array<T>>, rs: Int, col: Int, mc: EUDCalculator<T>): Boolean {
    val p = data[rs][rs]
    if (mc.isZero(data[rs][col])) {
        return true
    }
    if (mc.isZero(p)) {
        swapCol(data, rs, col, rs)
        return true
    }
    val (q, r) = mc.divideAndRemainder(data[rs][col], p)
    multiplyAndAddCol(data, mc, rs, col, mc.negate(q), rs)
    return if (mc.isZero(r)) {
        true
    } else {
        swapCol(data, rs, col, rs)
        false
    }
}

private fun <T : Any> doDivideRow(data: Array<Array<T>>, rs: Int, row: Int, mc: EUDCalculator<T>): Boolean {
    val p = data[rs][rs]
    if (mc.isZero(data[row][rs])) {
        return true
    }
    if (mc.isZero(p)) {
        swapCol(data, rs, row, rs)
        return true
    }
    val (q, r) = mc.divideAndRemainder(data[row][rs], p)
    multiplyAndAddRow(data, mc, rs, row, mc.negate(q), rs)
    return if (mc.isZero(r)) {
        true
    } else {
        swapRow(data, rs, row)
        false
    }
}

private fun <T : Any> doDivide(data: Array<Array<T>>, rs: Int, row: Int, col: Int, mc: EUDCalculator<T>): Boolean {
    val p = data[rs][rs]
    val (q, r) = mc.divideAndRemainder(data[row][col], p)
    return if (mc.isZero(r)) {
        true
    } else {
        multiplyAndAddRow(data, mc, row, rs, mc.one, rs + 1)
        multiplyAndAddCol(data, mc, rs, col, mc.negate(q), rs)
        swapCol(data, rs, col, rs)
        false
    }

}

internal fun <T : Any> swapRow(data: Array<Array<T>>, row1: Int, row2: Int) {
    val t = data[row1]
    data[row1] = data[row2]
    data[row2] = t
}

internal fun <T : Any> swapCol(data: Array<Array<T>>, col1: Int, col2: Int, rowStart: Int = 0) {
    for (i in rowStart until data.size) {
        val t = data[i][col1]
        data[i][col1] = data[i][col2]
        data[i][col2] = t
    }
}

internal fun <T : Any> multiplyRow(data: PData<T>, row: Int, k: T, colStart: Int = 0) {
    val r = data[row]
    for (i in colStart until r.size) {
        r[i] = r[i].multiply(k)
    }
}

internal fun <T : Any> multiplyCol(data: PData<T>, col: Int, k: T, rowStart: Int = 0) {
    for (i in rowStart until data.size) {
        data[i][col] = data[i][col].multiply(k)
    }
}


internal fun <T : Any> multiplyAndAddRow(data: Array<Array<T>>, mc: RingCalculator<T>, row1: Int, row2: Int, k: T, colStart: Int = 0) {
    for (i in colStart until data[row1].size) {
        data[row2][i] = mc.eval { data[row2][i] + k * data[row1][i] }
    }
}

internal fun <T : Any> multiplyAndAddCol(data: Array<Array<T>>, mc: RingCalculator<T>, col1: Int, col2: Int, k: T, rowStart: Int = 0) {
    for (i in rowStart until data.size) {
        data[i][col2] = mc.eval { data[i][col2] + k * data[i][col1] }
    }
}


//fun <T : Any> LambdaMatrix<T>.toNormalFormAndOperation(): Pair<LambdaMatrix<T>, List<LambdaPrimaryOperation<T>>> {
//
//}
//
fun <T : Any> LambdaMatrix<T>.doLambdaOperation(op: LambdaPrimaryOperation<T>): LambdaMatrix<T> {
    when (op) {
        is Swap -> {
            return if (op.isRow) {
                exchangeRow(op.idx1, op.idx2)
            } else {
                exchangeColumn(op.idx1, op.idx2)
            }
        }
        is Multiply -> {
            val pk = Polynomial.constant(mathCalculator.zero.mathCalculator, op.k)
            return if (op.isRow) {
                multiplyNumberRow(pk, op.idx)
            } else {
                multiplyNumberColumn(pk, op.idx)
            }
        }
        is MultiplyAdd -> {
            return if (op.isRow) {
                multiplyAndAddRow(op.k, op.idx1, op.idx2)
            } else {
                multiplyAndAddColumn(op.k, op.idx1, op.idx2)
            }
        }
    }
}

private fun <T : Any> doLambdaOp1(data: PData<T>, op: LambdaPrimaryOperation<T>, mc: PMC<T>) {
    when (op) {
        is Swap -> {
            return if (op.isRow) {
                swapRow(data, op.idx1, op.idx2)
            } else {
                swapCol(data, op.idx1, op.idx2)
            }
        }
        is Multiply -> {
            return if (op.isRow) {
                multiplyRow(data, op.idx, op.k)
            } else {
                multiplyCol(data, op.idx, op.k)
            }
        }
        is MultiplyAdd -> {
            return if (op.isRow) {
                multiplyAndAddRow(data, mc, op.idx1, op.idx2, op.k)
            } else {
                multiplyAndAddCol(data, mc, op.idx1, op.idx2, op.k)
            }
        }
    }
}

fun <T : Any> LambdaMatrix<T>.doLambdaOperations(ops: List<LambdaPrimaryOperation<T>>): LambdaMatrix<T> {
    @Suppress("UNCHECKED_CAST")
    val data = Array<Array<Polynomial<T>>>(row) { i ->
        Array(column) { j ->
            get(i, j)
        }
    }
    val mc = mathCalculator
    for (op in ops) {
        doLambdaOp1(data, op, mc)
    }
    return DMatrix(data, row, column, mathCalculator)
}

//fun main(args: Array<String>) {
//    val mcd = Calculators.getCalculatorDoubleDev()
//    val str = """
//        -1 0 1
//        3 2 -2
//        -5 1 4
//    """.trimIndent()
//    val A = MatrixSup.parseMatrixD(str, mcd) { it.toDouble() }
//    println(MatrixSup.eigenmatrix(A).toNormalForm().contentToString(Polynomial.composedFormatter(NumberFormatter.decimalFormatter())))
//}


