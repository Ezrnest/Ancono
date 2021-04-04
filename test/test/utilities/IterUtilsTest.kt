package test.utilities

import cn.ancono.utilities.IterUtils
import org.junit.Test
import kotlin.test.assertTrue

class IterUtilsTest {

    @Test
    fun testProduct() {
        assertTrue {
            IterUtils.prod(3..5, 2..3, 1..1, 2..3).toSet().size == 3 * 2 * 1 * 2
        }
        assertTrue {
            IterUtils.prodIdx(3..5, 2..3, 1..1, 2..3).toSet().size == 3 * 2 * 1 * 2
        }
        assertTrue {
            IterUtils.prodIdx(3..5, 2..3, 1..1, 2..3).map { it.toList() }.zip(
                    IterUtils.prod(3..5, 2..3, 1..1, 2..3)
            ).all { (a, b) -> a == b }
        }
    }
}