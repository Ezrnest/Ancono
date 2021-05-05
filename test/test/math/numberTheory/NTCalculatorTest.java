package test.math.numberTheory;

import cn.ancono.math.algebra.abs.calculator.FieldCalculator;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.structure.Polynomial;
import org.junit.Assert;
import org.junit.Test;
import test.math.TestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.PrimitiveIterator;
import java.util.Random;

/*
 * Created by liyicheng at 2020-03-09 18:09
 */
public class NTCalculatorTest {

    @Test
    public void gcdUV1() {
        var cal = Calculators.integer();
        var rd = new Random();
        int bound = 1000;
        for (int i = 0; i < 100; i++) {
            int a = rd.nextInt(bound * 2) - bound;
            int b = rd.nextInt(bound * 2) - bound;
            var duv = cal.gcdUV(a, b);
            int d = duv.getFirst();
            int u = duv.getSecond();
            int v = duv.getThird();
            Assert.assertEquals("", d, u * a + v * b);
        }
    }

    private Polynomial<Fraction> randomPolynomial(Random rd, FieldCalculator<Fraction> cal) {
        int bound = 5;
        PrimitiveIterator.OfInt ints = rd.ints(-bound, bound).iterator();
        int deg = rd.nextInt(3) + 1;
        List<Fraction> coes = new ArrayList<>(deg + 1);
        for (int i = 0; i <= deg; i++) {
            coes.add(Fraction.of(ints.next()));
        }
        return Polynomial.of(cal, coes);
    }

    @Test
    public void gcdUV2() {
        var calFrac = Fraction.getCalculator();
        var cal = Polynomial.calculator(calFrac);
        var rd = new Random();


        for (int i = 0; i < 100; i++) {
            var a = randomPolynomial(rd, calFrac);
            var b = randomPolynomial(rd, calFrac);

            var duv = cal.gcdUV(a, b);
            var d = duv.getFirst();
            var u = duv.getSecond();
            var v = duv.getThird();
            var t = u.multiply(a).add(v.multiply(b));
            TestUtils.assertValueEquals(d, t);
        }
    }
}