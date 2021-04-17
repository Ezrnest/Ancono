package test.math.calculus

import cn.ancono.math.calculus.HermiteOrthPoly
import cn.ancono.math.calculus.LaguerreOrthPoly
import cn.ancono.math.calculus.LegendreOrthPoly
import cn.ancono.math.calculus.TchebychevOrthPoly
import cn.ancono.math.numberModels.Fraction
import org.junit.Test

class DefinedPolynomialsTest {
    @Test
    fun testLegendreOrthPoly() {
        val mc = Fraction.calculator
        val ps = LegendreOrthPoly(mc)
        kotlin.test.assertTrue {
            val p3 = ps.sequence.take(4).last()
            p3.first() == Fraction.of("5/2")
        }
    }

    @Test
    fun testTchebychevOrthPoly() {
        val mc = Fraction.calculator
        val ps = TchebychevOrthPoly(mc)
        kotlin.test.assertTrue {
            val p3 = ps.sequence.take(4).last()
            p3.first() == Fraction.of("4")
        }
    }

    @Test
    fun testLaguerreOrthPoly() {
        val mc = Fraction.calculator
        val ps = LaguerreOrthPoly(mc)
        kotlin.test.assertTrue {
            val p3 = ps.sequence.take(4).last()
            p3.first() == Fraction.of("-1/6")
        }
    }

    @Test
    fun testHermiteOrthPoly() {
        val mc = Fraction.calculator
        val ps = HermiteOrthPoly(mc)
        kotlin.test.assertTrue {
            val p3 = ps.sequence.take(5).last()
            p3.get(2) == Fraction.of("-6")
        }
    }


}