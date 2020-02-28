package test.math.numberModels.structure;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.Fraction;
import cn.timelives.java.math.numberModels.structure.Polynomial;
import org.junit.Test;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.*;

public class PolynomialTest {
    private MathCalculator<Fraction> mc = Fraction.getCalculator();
    private MathCalculator<Integer> mci = Calculators.getCalculatorInteger();
    @Test
    public void difference() {
        var p = Polynomial.valueOf(Calculators.getCalculatorInteger(),1,2,-3,4,5).mapTo(Fraction::valueOf,mc);
        assertEquals("p.difference() = p(n)-p(n-1)", p.difference().compute(Fraction.ONE), p.compute(mc.getOne()).subtract(p.compute(mc.getZero())));

    }

    @Test
    public void sumOfN() {
        var p = Polynomial.valueOf(Calculators.getCalculatorInteger(),1,2,-3,4,5).mapTo(Fraction::valueOf,mc);
        assertTrue("p.sumOfN().difference() = p",p.sumOfN().difference().valueEquals(p));
        assertEquals("p.difference().sumOfN() = p + C", 0, p.difference().sumOfN().subtract(p).getDegree());
    }

    @Test
    public void testGcd(){
        var f = Polynomial.valueOf(mci,1,1,0,1).mapTo(Fraction::valueOf,mc);
        var mcp = Polynomial.getCalculator(mc);
        print(f.derivative());
        print(mcp.gcd(f,f.derivative()));
    }
}