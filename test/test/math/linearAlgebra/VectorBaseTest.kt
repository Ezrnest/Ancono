package test.math.linearAlgebra

import cn.ancono.math.algebra.linear.MatrixSup
import cn.ancono.math.algebra.linear.Vector
import cn.ancono.math.algebra.linear.VectorBasis
import cn.ancono.math.algebra.linear.space.AffineSpace
import cn.ancono.math.numberModels.Fraction
import org.junit.Test

/**
 * Contains tests for vector base and linear space.
 */
class VectorBaseTest{
    val mc = Fraction.calculator
    val par: (String) -> Fraction = Fraction.Companion::of

    @Test
    fun testIntersect(){
        var str = """
            1 0 -1 0
            0 1 2 1
            2 1 0 1
        """.trimIndent()
        val mat1 = MatrixSup.parseFMatrix(str)
        str = """
            -1 1 1 1
            1 -1 -3 -1
            -1 1 -1 1
        """.trimIndent()
        val mat2 = MatrixSup.parseMatrixD(str, mc, par)
//        println(mat1)
//        println(mat2)
//        val a1 = mat1.getRow(0)
//        val a2 = mat1.getRow(1)
//        val a3 = mat1.getRow(2)
//        val b1 = mat2.getRow(0)
//        val b2 = mat2.getRow(1)
//        val b3 = mat2.getRow(2)
        val vb1 = Vector.maxIndependent(mat1.rowVectors())
//        println(vb1)
        val vb2 = Vector.maxIndependent(mat2.rowVectors())
//        println(vb2)

        val desired = MatrixSup.parseVector("1 -1 -3 -1",mc,par)
        val re = vb1.intersect(vb2)
//        println(re)
        assert(re.vectors.first().isParallel(desired))
    }

    @Test
    fun testIntersect2() {
        val p1 = MatrixSup.parseVector("1 2", mc, par)
        val v1 = MatrixSup.parseVector("1 1", mc, par)
        val p2 = MatrixSup.parseVector("3 4", mc, par)
        val v2 = MatrixSup.parseVector("-1 1", mc, par)
        val sp1 = AffineSpace.valueOf(p1, v1)
        val sp2 = AffineSpace.valueOf(p2, v2)
        val re = sp1.intersect(sp2)!!
        assert(re.originVector.valueEquals(p2))
    }

    @Test
    fun testIntersect3(){
        val str = """
            5 -3 0 0
            -6 5 1 0
            -5 4 0 1
            -11 3 0 0
            8 -1 1 0
            10 -2 0 1
            1 0 -1 2
        """.trimIndent()
        val mat = MatrixSup.parseFMatrix(str)
        val (s1, a1, a2, s2, b1) = mat.rowVectors()
        val b2 = mat.getRow(5)// no component6()
        val sp1 = AffineSpace.valueOf(s1, a1, a2)
        val sp2 = AffineSpace.valueOf(s2, b1, b2)
        val re = sp1.intersect(sp2)!!
        val desired = AffineSpace.valueOf(mat.getRow(6))
//        val s1 = MatrixSup.parseFMatrix()
//        println(re)
//        println(desired)
        assert(re.valueEquals(desired))
    }

    @Test
    fun testTransMatrix(){
        var str = """
            1 0 -1
            2 1 1
            1 1 1
        """.trimIndent()
        val mat1 = MatrixSup.parseMatrixD(str, mc, par)
        str = """
            0 1 1
            -1 1 0
            1 2 1
        """.trimIndent()
        val mat2 = MatrixSup.parseMatrixD(str, mc, par)
        val vb1 = VectorBasis.createBase(mat1.rowVectors())
        val vb2 = VectorBasis.createBase(mat2.rowVectors())
        str = """
            |0 1 1
            |-1 -3 -2
            |2 4 4
        """.trimMargin()
        val desired = MatrixSup.parseMatrixD(str, mc, par)
        assert(vb1.transMatrix(vb2).valueEquals(desired))

    }

    @Test
    fun testTransMatrix2(){
        var str = """
            1 1 0 1
            2 1 3 0
            1 1 0 0
            0 1 -1 -1
        """.trimIndent()

        val base1 = VectorBasis.createBase(MatrixSup.parseFMatrix(str).rowVectors())
        str = """
            1 0 0 1
            0 0 1 -1
            2 1 0 3
            -1 0 1 2
        """.trimIndent()
        val base2 = VectorBasis.createBase(MatrixSup.parseFMatrix(str).rowVectors())
        str = """
            -1/2 -1/2 3/2 4
            -1/2 1/2 -1/2 1
            5/2 -1/2 3/2 -7
            -3/2 1/2 -3/2 2
        """.trimIndent()
        val desired = MatrixSup.parseFMatrix(str)
        assert(desired.valueEquals(base1.transMatrix(base2)))
    }
}