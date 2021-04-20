package test.utilities

import cn.ancono.math.discrete.combination.CombUtils
import cn.ancono.utilities.IterUtils
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class IterUtilsTest {

    @Test
    fun testProduct() {
        assertEquals(3 * 2 * 1 * 2, IterUtils.prod(5 downTo 3, 2..3, 1..1, 2..3).toSet().size)
        assertTrue {
            IterUtils.prodIdx(3..5, 2..3, 1..1, 2..3).toSet().size == 3 * 2 * 1 * 2
        }
        assertTrue {
            IterUtils.prodIdx(3..5, 2..3, 1..1, 2..3).map { it.toList() }.zip(
                    IterUtils.prod(3..5, 2..3, 1..1, 2..3)
            ).all { (a, b) -> a == b }
        }
    }

    @Test
    fun testStep() {
        assertEquals(4 * 2, IterUtils.prodIdx(1..10 step 2, 2..3).count())
    }

    @Test
    fun testGap() {
        val b = 10
        val a = 1
        val n = 3

        assertEquals(CombUtils.combination(b - a + 1, n).toInt(), IterUtils.idxOrdered(n, a, b).count())

        assertEquals(CombUtils.combination(b - a + n, n).toInt(), IterUtils.idxOrderedEq(n, a, b).count())
    }
}