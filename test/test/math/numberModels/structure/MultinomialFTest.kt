package test.math.numberModels.structure

import cn.ancono.math.numberModels.Fraction
import cn.ancono.math.numberModels.api.plus
import cn.ancono.math.numberModels.structure.MultinomialF
import cn.ancono.math.numberModels.structure.TermF
import org.junit.Assert.*
import org.junit.Test

class MultinomialFTest {
    @Test
    fun testParse() {
        val t = TermF.parseChar("ab^2c{pi}^-2z")
        assertEquals(mapOf(
                "a" to 1,
                "b" to 2,
                "c" to 1,
                "pi" to -2,
                "z" to 1
        ), t)
    }

    @Test
    fun testParse2() {
        val mc = Fraction.calculator
        val f = MultinomialF.parse("1*ab-2*cd{pi}^2z", mc, Fraction::of)
        assertEquals(setOf("a", "b", "c", "d", "pi", "z"), f.characters)
        println(f)
    }

    @Test
    fun testAdd() {
        val mc = Fraction.calculator
        val f = MultinomialF.parse("1*ab", mc, Fraction::of)
        val g = MultinomialF.parse("1*ab+2*cd", mc, Fraction::of)
        println(f + g)
    }

    @Test
    fun testCreate() {
        val mc = Fraction.calculator
        val f = MultinomialF.of(mc,
                Fraction.ONE to "ab^2",
                Fraction.NEGATIVE_ONE to "ab^2"
        )
        println(f)
    }
}