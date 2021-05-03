package samples

import cn.ancono.math.algebra.linear.QuadraticForm
import cn.ancono.math.numberModels.Multinomial

fun main() {
//    val str1 = """
//        5 2 -4
//        2 8 2
//        -4 2 5
//    """.trimIndent()
//    val A = MatrixSup.parseFMatrix(str1)
//    A.printMatrix()


    val expr = "10x^2+8xy+24xz+2y^2-28yz+z^2"
    val B = QuadraticForm.representationMatrix(Multinomial.parse(expr))
    println(B)
    val (J, P) = B.toCongDiagForm()
    println(J)
    println(P)
//    (P.transportMatrix() * A * P).printMatrix()
//    val (J,P) = MatrixSup.jordanFormAndTrans(A)!!
//    J.printMatrix()
//    P.printMatrix()
//    (P.inverse() * A * P).printMatrix()
}