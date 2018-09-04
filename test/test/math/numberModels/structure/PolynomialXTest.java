package test.math.numberModels.structure;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.Fraction;
import cn.timelives.java.math.numberModels.expression.Node;
import cn.timelives.java.math.numberModels.structure.PolynomialX;
import org.junit.Test;

import static org.junit.Assert.*;

public class PolynomialXTest {
    private MathCalculator<Fraction> mc = Fraction.Companion.getCalculator();
    @Test
    public void difference() {
        var p = PolynomialX.valueOf(Calculators.getCalculatorInteger(),1,2,-3,4,5).mapTo(Fraction.Companion::valueOf,mc);
        assertEquals("p.difference() = p(n)-p(n-1)", p.difference().compute(Fraction.Companion.getONE()), p.compute(mc.getOne()).subtract(p.compute(mc.getZero())));

    }

    @Test
    public void sumOfN() {
        var p = PolynomialX.valueOf(Calculators.getCalculatorInteger(),1,2,-3,4,5).mapTo(Fraction.Companion::valueOf,mc);
        assertTrue("p.sumOfN().difference() = p",p.sumOfN().difference().valueEquals(p));
        assertEquals("p.difference().sumOfN() = p + C", 0, p.difference().sumOfN().subtract(p).getDegree());

    }
}