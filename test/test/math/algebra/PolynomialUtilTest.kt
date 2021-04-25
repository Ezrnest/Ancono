package test.math.algebra

import cn.ancono.math.algebra.PolynomialUtil
import cn.ancono.math.numberModels.Calculators
import cn.ancono.math.numberModels.structure.Polynomial
import org.junit.Assert.assertEquals
import org.junit.Test

class PolynomialUtilTest {
    @Test
    fun testFindRoots() {
        val mc = Calculators.intModP(31)
        val f = Polynomial.parse("x+x^8", mc, String::toInt)
        assertEquals((0 until 31).filter { f.compute(it) == 0 }, PolynomialUtil.findRootsModP(f))
    }
}