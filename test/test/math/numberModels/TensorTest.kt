package test.math.numberModels

import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Tensor
import cn.ancono.math.numberModels.api.minus
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
}