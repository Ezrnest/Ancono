package test.math.linearAlgebra

import cn.ancono.math.T
import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.component1
import cn.ancono.math.component2
import cn.ancono.math.times
import org.junit.Test

class QuadraticFormTest {
    @Test
    fun test1() {
        val str1 = """
        3 1 0 1
        1 3 -1 -1
        0 -1 3 1
        1 -1 1 3
    """.trimIndent()
        val A = MatrixSup.parseFMatrix(str1)
        val (J, P) = A.toCongDiagForm()
        kotlin.test.assertTrue {
            (P.T * A * P).valueEquals(J)
        }
    }


}