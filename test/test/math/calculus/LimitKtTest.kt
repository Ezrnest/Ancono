package test.math.calculus

import cn.ancono.math.calculus.Limit
import cn.ancono.math.calculus.LimitDirection
import cn.ancono.math.calculus.LimitProcess
import cn.ancono.math.calculus.LimitValue
import cn.ancono.math.calculus.expression.LimitProcessE
import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.Multinomial
import cn.ancono.math.numberModels.Term
import cn.ancono.math.numberModels.expression.ExprCalculator
import cn.ancono.math.numberModels.expression.Expression
import org.junit.Test
import kotlin.test.assertTrue

class LimitKtTest{
    val mc = ExprCalculator.instance

    @Test
    fun testPowLimit(){
        val m1 = Multinomial.valueOf("x^2 + 2x")
        val m2 = Multinomial.monomial(Term.characterPower("x", Fraction.Companion.valueOf("-3/5")))
        val p1: LimitProcessE = LimitProcess.toPositiveInf()
        val p2 = LimitProcess.toPositiveZero(mc)
        val p3 = LimitProcess.toNegativeZero(mc)
        val p4 : LimitProcessE = LimitProcess.toNegativeInf()

        assertTrue {
            val lim = Limit.limitOf(m1,p1,mc)
            lim.isPositiveInf
        }
        assertTrue {
            val lim = Limit.limitOf(m1, p2, mc)
            lim.isFinite && lim.direction == LimitDirection.RIGHT
        }
        assertTrue {
            val lim = Limit.limitOf(m1, p3, mc)
            lim.isFinite && lim.direction == LimitDirection.LEFT
        }
        assertTrue{
            Limit.limitOf(m1,p4,mc).isPositiveInf
        }


        assertTrue {
            val lim = Limit.limitOf(m2, p1, mc)
            lim.isFinite && lim.direction == LimitDirection.RIGHT

        }
        assertTrue {
            Limit.limitOf(m2,p2,mc).isPositiveInf
        }
        assertTrue {
            Limit.limitOf(m2, p3, mc).isNegativeInf
        }
        assertTrue{
            val lim = Limit.limitOf(m2,p4,mc)
            lim.isFinite && lim.direction == LimitDirection.LEFT
        }
    }
}