package test.math.prob

import cn.ancono.math.prob.RandomVariables
import cn.ancono.math.prob.minus
import org.junit.Assert.assertEquals
import org.junit.Test

/*
 * Created by liyicheng at 2020-06-02 17:51
 */
class RandomVariableTest {

    @Test
    fun t1() {
        val x = RandomVariables.uniform()
        val y = x - x
        assertEquals(0.0, y.sample(), 0.000001)
    }

}