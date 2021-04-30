package test

import cn.ancono.math.algebra.abs.calculator.eval
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.api.times
import cn.ancono.math.numberModels.expression.ExprCalculator

/*
 * Created at 2018/12/28 16:38
 * @author  liyicheng
 */
fun main() {
    val mc = Calculators.intModN(26)
    mc.eval { subtract(multiply(5, 15), multiply(23, 14)) }.also { println(it) }
//    p2()
}

fun p2() {
    val mcd = Calculators.doubleDev()
    val str = """
            1 2 5
            1 0 1
            0 1 2
        """.trimIndent()
    val mat = MatrixSup.parseFMatrix(str).mapTo(mcd) { it.toDouble() }
    val (Q, R) = mat.decompQR()
    println(Q)
    println(R)
    println(Q * R)
}

fun p3() {
    val mc = ExprCalculator.instance
    val str = """
        a -1 c
        5 b 3
        1-c 0 -a
    """.trimIndent()
    val mat = MatrixSup.parseMatrixD(str, mc, mc::parse)
    println(mat)
    println(mat.det())
    println(mat.adjoint())

//    mat.inverse().printMatrix()
}