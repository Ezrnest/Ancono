package test.math.numberModels

import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Tensor
import cn.ancono.math.numberModels.TensorUtils
import cn.ancono.math.numberModels.api.minus
import cn.ancono.math.numberModels.api.times
import cn.ancono.math.numberModels.get
import org.junit.Assert.*
import org.junit.Test

class TensorTest {

    fun testAdd() {
        val mc = Calculators.integer()
        val shape = intArrayOf(2, 8)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
        val u = Tensor.of(shape, mc) { it.sum() }
        assertTrue((u - u).isZero())
    }

    @Test
    fun testView() {
        val mc = Calculators.integer()
        val shape = intArrayOf(2, 8)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
        val u = Tensor.of(shape, mc) { it.sum() }
        assertEquals(2, u.slice(null, 0..-1 step 2).slice(0, 0..-1 step 2).size)
    }

    @Test
    fun testSet() {
        val mc = Calculators.integer()
        val shape = intArrayOf(2, 8)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
//        val s1 =
        val u = Tensor.of(shape, mc) { it.sum() }
        u[1, 1] = 3
        assertEquals(3, u[1, 1])
    }

    @Test
    fun testWedge() {
        val mc = Calculators.integer()
        val shape = intArrayOf(2, 3)
        val shape2 = intArrayOf(3, 2)
        val u = Tensor.of(shape, mc) { it.sum() }
        val w = Tensor.of(shape2, mc) { it[0] }
//        println(u)
//        println(w)
//        println()
        assertTrue(u.wedge(w).slice(0, 1).valueEquals(w))
    }

    @Test
    fun testEinsum() {
        val mc = Calculators.integer()
        val shape = intArrayOf(3, 3)
        val shape2 = intArrayOf(3, 3)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
        val u = Tensor.of(shape, mc) { it.sum() }
        val w = Tensor.of(shape2, mc) { it[0] }
        var r = TensorUtils.einsum(listOf(u, w),
                intArrayOf(3, 3),
                intArrayOf(1),
                listOf(intArrayOf(0, 0, 1, 1), intArrayOf(0, 0, 1, 1)),
                listOf(intArrayOf(), intArrayOf()),
                mc)//element-wise multiplication
        assertTrue(r.valueEquals(u * w))


        r = TensorUtils.einsum(listOf(u),
                intArrayOf(3),
                intArrayOf(1),
                listOf(intArrayOf(0, 0, 1, 0)),
                listOf(intArrayOf()),
                mc)//diagonal elements
        assertTrue(r.valueEquals(Tensor.of(intArrayOf(3), mc) { it[0] * 2 }))

        r = TensorUtils.einsum(listOf(u),
                intArrayOf(1),
                intArrayOf(3),
                listOf(intArrayOf()),
                listOf(intArrayOf(0, 0, 1, 0)),
                mc) // trace
        assertEquals(6, r[0])

        r = TensorUtils.einsum(listOf(u, w),
                intArrayOf(3, 3, 3, 3),
                intArrayOf(1),
                listOf(intArrayOf(0, 0, 1, 1), intArrayOf(0, 2, 1, 3)),
                listOf(intArrayOf(), intArrayOf()),
                mc) // wedge(outer product)
        assertTrue(r.valueEquals(u.wedge(w)))
    }

    @Test
    fun testEinsum2() {
        val mc = Calculators.integer()
        val shape = intArrayOf(3, 3)
        val shape2 = intArrayOf(3, 3)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
        val u = Tensor.of(shape, mc) { it[0] + 2 * it[1] }
        val w = Tensor.of(shape2, mc) { it[0] }

        assertEquals(9, Tensor.einsum("ii", u)[0]) // trace
        assertEquals(27, Tensor.einsum("ij->", u)[0]) // sum
        assertEquals(3, Tensor.einsum("ii->i", u)[1]) // diagonal
        assertEquals(4, Tensor.einsum("ij->ji", u)[2, 0]) // transpose

        assertEquals(12, Tensor.einsum("ij,ij->ij", u, w)[2, 2]) // element-wise multiplication

        assertEquals(13, Tensor.einsum("ij,jk->ik", u, w)[1, 1]) // matrix multiplication
    }

}