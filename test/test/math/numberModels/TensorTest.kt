package test.math.numberModels

import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.Tensor
import cn.ancono.math.numberModels.api.minus
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
        assertEquals(2, u[null, 0..-1 step 2][0, 0..-1 step 2].size)
    }

    @Test
    fun testSet() {
        val mc = Calculators.integer()
        val shape = intArrayOf(2, 8)
//    val v = Tensor.zeros(mc, *shape)
//    val w = Tensor.ones(mc, *shape)
        val u = Tensor.of(shape, mc) { it.sum() }
        u[1, 1] = 3
    }
}