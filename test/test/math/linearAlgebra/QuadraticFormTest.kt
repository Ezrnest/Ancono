package test.math.linearAlgebra

import cn.ancono.math.algebra.linearAlgebra.MatrixSup
import cn.ancono.math.times
import org.junit.Assert.*
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
        val (J, P) = A.congruenceDiagForm()
        kotlin.test.assertTrue {
            (P.transportMatrix() * A * P).valueEquals(J)
        }
    }


}