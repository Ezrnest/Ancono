package test.math.numberModels

import cn.ancono.math.numberModels.Fraction
import org.junit.Assert.assertEquals
import org.junit.Test

class FractionTest {
    @Test
    fun testCreate() {
        val a = Fraction.of(0, 1)
        assertEquals(Fraction.ZERO, a)
        val b = Fraction.of(3, -4)
        assertEquals(Fraction.of(-3, 4), b)
        assertEquals(b, Fraction.of(-6, 8))
    }

    @Test
    fun testAdd() {
        val a = Fraction.ONE
        assertEquals(Fraction.of(2), a.plus(a))
        assertEquals(Fraction.ZERO, a - a)

        val b = Fraction.of(3, -4)
        assertEquals(Fraction.of(1, 4), b + a)
        assertEquals(Fraction.of(-7, 4), b - a)

        val c = Fraction.of(1, 6)
        assertEquals(Fraction.of("-7/12"), b + c)
        assertEquals(Fraction.of("-11/12"), b - c)

        assertEquals(b + a, b + 1)
        assertEquals(b - a, b - 1)
    }

    @Test
    fun testMultiply() {
        val a = Fraction.of("-3/4")
        val b = Fraction.of("6/5")
        assertEquals(Fraction.of("-9/10"), a * b)
        assertEquals(Fraction.of("-5/8"), a / b)
        assertEquals(Fraction.of(-3), a * 4)
        assertEquals(Fraction.of("-1/4"), a / 3)
    }

}