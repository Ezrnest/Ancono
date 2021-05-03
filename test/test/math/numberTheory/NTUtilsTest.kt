package test.math.numberTheory

import cn.ancono.math.numberTheory.NTUtils
import cn.ancono.math.numberTheory.Primes
import org.junit.Assert.assertEquals
import org.junit.Test

class NTUtilsTest {

    @Test
    fun testKroneckerSymbol() {
        for (p in Primes.getInstance().getPrimesBelow(100)) {
            assertEquals(0, NTUtils.kroneckerSymbol(0, p))
            val quadratic = (0 until p).map { x -> x * x % p }.toSet()
            for (x in 1 until p) {
                val k = if (x in quadratic) {
                    1
                } else {
                    -1
                }
                assertEquals(k, NTUtils.kroneckerSymbol(x, p))
            }
        }
    }

    @Test
    fun testSqrtModP() {
        for (p in Primes.getInstance().getArray(20)) {
            for (x in 0 until p) {
                val a = (x * x) % p
                val s = NTUtils.sqrtModP(a, p)
//                assertEquals("Sqrt of $a mod $p should be $x",x,)
                assertEquals(a, (s * s) % p)
            }
        }
    }

    @Test
    fun testSolveDiophantine() {
        val p = NTUtils.solveDiophantine(2, 97)
        assertEquals(5L to 6L, p)
    }
}