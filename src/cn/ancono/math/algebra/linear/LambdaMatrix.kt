package cn.ancono.math.algebra.linear

import cn.ancono.math.algebra.PolynomialUtil
import cn.ancono.math.algebra.abs.calculator.EUDCalculator
import cn.ancono.math.algebra.abs.calculator.FieldCalculator
import cn.ancono.math.algebra.abs.calculator.RingCalculator
import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.minus
import cn.ancono.math.numberModels.structure.Polynomial
import java.util.*
import kotlin.math.min


sealed class LambdaPrimaryOperation<T>(val isRow: Boolean)

class Swap<T>(isRow: Boolean, val idx1: Int, val idx2: Int) : LambdaPrimaryOperation<T>(isRow)

class Multiply<T>(isRow: Boolean, val idx: Int, val k: T) : LambdaPrimaryOperation<T>(isRow)

class MultiplyAdd<T>(isRow: Boolean, val idx1: Int, val idx2: Int, val k: Polynomial<T>) : LambdaPrimaryOperation<T>(isRow) {
    init {
        require(idx1 != idx2) {
            "idx=$idx1 must be different!"
        }
    }
}
typealias LambdaMatrix<T> = Matrix<Polynomial<T>>
private typealias PData<T> = Array<Array<Polynomial<T>>>
private typealias PMC<T> = RingCalculator<Polynomial<T>>

/**
 * Transform this lambda matrix to its normal form.
 */
fun <T> LambdaMatrix<T>.toNormalForm(): LambdaMatrix<T> {
    require(row == column)
    @Suppress("UNCHECKED_CAST")
    val data = Array(row) { i ->
        Array(column) { j ->
            get(i, j)
        }
    }
    toNormalForm(data, calculator as Polynomial.PolyCalField<T>, 0)
    return Matrix.of(data, calculator)
}

private fun <T> normalFormInvFac(mat: LambdaMatrix<T>): List<Polynomial<T>> {
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

fun <T> LambdaMatrix<T>.invariantFactor(): List<Polynomial<T>> {
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
            val des = PolynomialUtil.decomposeFrac(p)
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
            val mat = matrix - Matrix.diag(lambda, matrix.column, matrix.calculator)
            val solutions = mat.kernel()
            assert(solutions.vectorLength == pr.value.size)
            for ((alpha, n) in solutions.vectors.asSequence().zip(pr.value.asSequence())) {
                trans += alpha
                var prev = alpha
                repeat(n - 1) {
                    val solu = Matrix.solveLinear(mat, prev)
                    prev = solu.special
                    trans += prev
                }
            }
        }
        val transMat = Matrix.fromVectors(trans)
        return jordan to transMat
    }

    @JvmStatic
    fun <T> toSmithForm(matrix: Matrix<T>): Matrix<T> {
        require(matrix.isSquare())
        @Suppress("UNCHECKED_CAST")
        val data = matrix.getValues() as Array<Array<T>>

        toNormalForm(data, matrix.calculator as EUDCalculator<T>, 0)
        return Matrix.of(data, matrix.calculator)
    }
}


private fun buildJordanForm(primaryFactor: Map<Fraction, List<Int>>, origin: Matrix<Fraction>): Matrix<Fraction> {
    val builder = Matrix.zero(origin.row, origin.column, origin.calculator)
    var idx = 0
    for (pr in primaryFactor) {
        val lambda = pr.key
        for (n in pr.value) {
            for (i in 0 until n) {
                builder[idx + i, idx + i] = lambda
            }
            for (i in 0 until n - 1) {
                builder[idx + i, idx + i + 1] = Fraction.ONE
            }
            idx += n
        }
    }
    return builder
}

/**
 * Transform this lambda matrix to its normal form.
 */
fun <T> LambdaMatrix<T>.toFrobeniusForm(mc: FieldCalculator<T>): Matrix<T> {
    @Suppress("UNCHECKED_CAST")
    val data = Array(row) { i ->
        Array(column) { j ->
            get(i, j)
        }
    }
//    toNormalForm(data,mathCalculator,)
    toNormalForm(data, calculator as Polynomial.PolyCalField<T>, 0)
    val builder = Matrix.zero(row, column, mc)
    var pos = 0
    val one = mc.one
    for (i in 0 until min(row, column)) {
        val t = data[i][i]
        if (t.isConstant) {
            continue
        }
        val deg = t.degree
        for (j in 0 until deg) {
            builder[pos + j, pos + deg - 1] = mc.negate(t[j])
        }
        for (j in 0 until (deg - 1)) {
            builder[pos + 1 + j, pos + j] = one
        }
        pos += deg
    }
    return builder
}


//internal fun <T> toNormalForm(data: PData<T>, mc: Polynomial.PolynomialCalculator<T>, fromIdx: Int) {
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

internal fun <T> toNormalForm(data: Array<Array<T>>, mc: EUDCalculator<T>, fromIdx: Int) {
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


private fun <T> processFirst(data: Array<Array<T>>, fromIdx: Int, mc: EUDCalculator<T>) {
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

private fun <T> doDivideCol(data: Array<Array<T>>, rs: Int, col: Int, mc: EUDCalculator<T>): Boolean {
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

private fun <T> doDivideRow(data: Array<Array<T>>, rs: Int, row: Int, mc: EUDCalculator<T>): Boolean {
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

private fun <T> doDivide(data: Array<Array<T>>, rs: Int, row: Int, col: Int, mc: EUDCalculator<T>): Boolean {
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

internal fun <T> swapRow(data: Array<Array<T>>, row1: Int, row2: Int) {
    val t = data[row1]
    data[row1] = data[row2]
    data[row2] = t
}

internal fun <T> swapCol(data: Array<Array<T>>, col1: Int, col2: Int, rowStart: Int = 0) {
    for (i in rowStart until data.size) {
        val t = data[i][col1]
        data[i][col1] = data[i][col2]
        data[i][col2] = t
    }
}

internal fun <T> multiplyRow(data: PData<T>, row: Int, k: T, colStart: Int = 0) {
    val r = data[row]
    for (i in colStart until r.size) {
        r[i] = r[i].multiply(k)
    }
}

internal fun <T> multiplyCol(data: PData<T>, col: Int, k: T, rowStart: Int = 0) {
    for (i in rowStart until data.size) {
        data[i][col] = data[i][col].multiply(k)
    }
}


internal fun <T> multiplyAndAddRow(data: Array<Array<T>>, mc: RingCalculator<T>, row1: Int, row2: Int, k: T, colStart: Int = 0) {
    for (i in colStart until data[row1].size) {
        data[row2][i] = mc.eval { data[row2][i] + k * data[row1][i] }
    }
}

internal fun <T> multiplyAndAddCol(data: Array<Array<T>>, mc: RingCalculator<T>, col1: Int, col2: Int, k: T, rowStart: Int = 0) {
    for (i in rowStart until data.size) {
        data[i][col2] = mc.eval { data[i][col2] + k * data[i][col1] }
    }
}


//fun <T> LambdaMatrix<T>.toNormalFormAndOperation(): Pair<LambdaMatrix<T>, List<LambdaPrimaryOperation<T>>> {
//
//}
//
fun <T> MutableMatrix<Polynomial<T>>.doLambdaOperation(op: LambdaPrimaryOperation<T>) {
    when (op) {
        is Swap -> {
            if (op.isRow) {
                swapRow(op.idx1, op.idx2)
            } else {
                swapCol(op.idx1, op.idx2)
            }
        }
        is Multiply -> {
            val pk = Polynomial.constant(calculator.zero.calculator, op.k)
            if (op.isRow) {
                multiplyRow(op.idx, pk)
            } else {
                multiplyCol(op.idx, pk)
            }
        }
        is MultiplyAdd -> {
            if (op.isRow) {
                multiplyAddRow(op.idx1, op.idx2, op.k)
            } else {
                multiplyAddCol(op.idx1, op.idx2, op.k)
            }
        }
    }
}

private fun <T> doLambdaOp1(data: PData<T>, op: LambdaPrimaryOperation<T>, mc: PMC<T>) {
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

fun <T> LambdaMatrix<T>.doLambdaOperations(ops: List<LambdaPrimaryOperation<T>>): LambdaMatrix<T> {
    val data = Array(row) { i ->
        Array(column) { j ->
            get(i, j)
        }
    }
    val mc = calculator
    for (op in ops) {
        doLambdaOp1(data, op, mc)
    }
    return Matrix.of(data, calculator)
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



