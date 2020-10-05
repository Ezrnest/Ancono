package test

import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.times
import java.util.function.Function

/*
 * Created at 2018/12/28 16:38
 * @author  liyicheng
 */
fun main(args: Array<String>) {
    p2()
}
fun p2(){
    val mcd = Calculators.getCalDoubleDev()
    val str = """
            1 2 5
            1 0 1
            0 1 2
        """.trimIndent()
    val mat = MatrixSup.parseFMatrix(str).mapTo(Function { it.toDouble() },mcd)
    val (Q, R) = mat.decompQR()
    Q.printMatrix()
    R.printMatrix()
    (Q * R).printMatrix()
}

fun p3(){
    val mc = ExprCalculator.instance
    val str = """
        a -1 c
        5 b 3
        1-c 0 -a
    """.trimIndent()
    val mat = MatrixSup.parseMatrixD(str, mc, mc::parse)
    mat.printMatrix()
    println(mat.calDet())
    mat.adjugate().printMatrix()

//    mat.inverse().printMatrix()
}