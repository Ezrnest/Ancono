package test.math.calculus

import cn.timelives.java.math.calculus.Limit
import cn.timelives.java.math.calculus.LimitDirection
import cn.timelives.java.math.calculus.LimitProcess
import cn.timelives.java.math.calculus.LimitValue
import cn.timelives.java.math.calculus.expression.LimitProcessE
import cn.timelives.java.math.numberModels.Fraction
import cn.timelives.java.math.numberModels.Multinomial
import cn.timelives.java.math.numberModels.Term
import cn.timelives.java.math.numberModels.expression.ExprCalculator
import cn.timelives.java.math.numberModels.expression.Expression
import org.junit.Test
import kotlin.test.assertTrue

class LimitKtTest{
    val mc = ExprCalculator.newInstance

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