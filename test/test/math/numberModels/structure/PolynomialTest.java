package test.math.numberModels.structure;

import cn.ancono.math.algebra.abs.calculator.FieldCalculator;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.structure.Polynomial;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PolynomialTest {
    private FieldCalculator<Fraction> mc = Fraction.getCalculator();
    private Calculators.IntegerCalculator mci = Calculators.integer();

    @Test
    public void multiply() {
        var f = Polynomial.of(mci, 1, 2, 1);
        var g = Polynomial.of(mci, 1, 1);
        var h = Polynomial.of(mci, 1, 3, 3, 1);
        assertTrue("(x^2+2x+1)(x+1) = x^3+3x^2+3x+1", h.valueEquals(f.multiply(g)));
    }

    @Test
    public void difference() {
        var p = Polynomial.of(Calculators.integer(), 1, 2, -3, 4, 5).mapTo(mc, Fraction::of);
        assertEquals("p.difference() = p(n)-p(n-1)", p.difference().compute(Fraction.ONE), p.compute(mc.getOne()).subtract(p.compute(mc.getZero())));

    }

    @Test
    public void sumOfN() {
        var p = Polynomial.of(Calculators.integer(), 1, 2, -3, 4, 5).mapTo(mc, Fraction::of);
        assertTrue("p.sumOfN().difference() = p", p.sumOfN().difference().valueEquals(p));
        assertEquals("p.difference().sumOfN() = p + C", 0, p.difference().sumOfN().subtract(p).getLeadingPower());
    }

    @Test
    public void testGcd() {
        var calInt = Calculators.integer();
        var cal = Fraction.getCalculator();
        var f = Polynomial.of(calInt, 1, 2, 1).mapTo(cal, Fraction::of); // 1 + 2x + x^2
        var g = Polynomial.of(calInt, -2, -1, 1).mapTo(cal, Fraction::of); // -2 - x + x^2
        var h = Polynomial.of(calInt, 1, 1).mapTo(cal, Fraction::of);
        assertTrue("", h.valueEquals(f.gcd(g)));
    }
}