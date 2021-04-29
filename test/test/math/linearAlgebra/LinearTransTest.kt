package test.math.linearAlgebra

import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.algebra.linear.VectorBasis
import cn.ancono.math.algebra.linear.mapping.LinearMapping
import cn.ancono.math.algebra.linear.mapping.LinearTrans
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.plus
import org.junit.Test

class LinearTransTest{
    val mc = Fraction.calculator
    val par: (String) -> Fraction = Fraction.Companion::of

    @Test
    fun testTransMatrix() {
        val str = """
            1 2 3 2
            -1 0 3 1
            2 1 5 -1
            1 1 2 2
        """.trimIndent()
        val mat = MatrixSup.parseMatrixD(str, mc, par)
        val (e1, e2, e3, e4) = Vector.unitVectors(4, mc)
        val base1 = VectorBasis.createFullBase(e1, e2, e3, e4)
        val base2 = VectorBasis.createFullBase(e4, e3, e2, e1)
        val base3 = VectorBasis.createFullBase(e1, e1 + e2, e1 + e2 + e3, e1 + e2 + e3 + e4)
        val trans = LinearTrans.underBase(mat, base1)
        println(trans.transMatrixUnder(base2))
        println(trans.transMatrixUnder(base3))
    }

    @Test
    fun testKerIm(){
        var str = """
            1 2 1 -3 2
            2 1 1 1 -3
            1 1 2 2 -2
            2 3 -5 -17 10
        """.trimIndent()
        val mat = MatrixSup.parseFMatrix(str)
        val tran = LinearMapping.fromMatrix(mat)
        str = """
            1 2 1 2
            2 1 1 3
            1 1 2 -5
        """.trimIndent()
        val imageDesired = VectorBasis.createBase(MatrixSup.parseFMatrix(str).rowVectors())
        assert(imageDesired.equivalentTo(tran.image))
        str = """
            -1 3 -2 1 0
            9 -11 5 0 4
        """.trimIndent()
        val kernelDesired = VectorBasis.createBase(MatrixSup.parseFMatrix(str).rowVectors())
        assert(kernelDesired.equivalentTo(tran.kernel))
    }
}