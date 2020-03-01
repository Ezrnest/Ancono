package test.math.numberModels.structure;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.structure.Polynomial;
import org.junit.Test;

import static cn.ancono.utilities.Printer.print;
import static org.junit.Assert.*;

public class PolynomialTest {
    private MathCalculator<Fraction> mc = Fraction.getCalculator();
    private MathCalculator<Integer> mci = Calculators.getCalInteger();
    @Test
    public void difference() {
        var p = Polynomial.valueOf(Calculators.getCalInteger(), 1, 2, -3, 4, 5).mapTo(Fraction::of, mc);
        assertEquals("p.difference() = p(n)-p(n-1)", p.difference().compute(Fraction.ONE), p.compute(mc.getOne()).subtract(p.compute(mc.getZero())));

    }

    @Test
    public void sumOfN() {
        var p = Polynomial.valueOf(Calculators.getCalInteger(), 1, 2, -3, 4, 5).mapTo(Fraction::of, mc);
        assertTrue("p.sumOfN().difference() = p",p.sumOfN().difference().valueEquals(p));
        assertEquals("p.difference().sumOfN() = p + C", 0, p.difference().sumOfN().subtract(p).getDegree());
    }

    @Test
    public void testGcd(){
        var f = Polynomial.valueOf(mci, 1, 1, 0, 1).mapTo(Fraction::of, mc);
        var mcp = Polynomial.getCalculator(mc);
        print(f.derivative());
        print(mcp.gcd(f,f.derivative()));
    }
}