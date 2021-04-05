package samples;

import cn.ancono.math.algebra.AlgebraUtil;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.Multinomial;
import cn.ancono.math.numberModels.structure.Polynomial;

import java.util.Collections;

/*
 * Created by lyc at 2020/3/1
 */
public class PolynomialSample {
    public static void computeProduct() {
        var cal = Calculators.doubleCal();
        // we use double as the type of the coefficient of the polynomials
        var f = Polynomial.of(cal, 1.0, 1.0, 2.0, 3.0); // 1 + x + 2x^2 + 3 x^3
        var g = Polynomial.binomialPower(2.0, 3, cal); // (x-2)^3
        System.out.println("f(x) = " + f);
        System.out.println("g(x) = " + g);
        var h = f.multiply(g);
        System.out.println("f(x)g(x) = " + h);
    }

    public static void computeGCD() {
        var cal = Fraction.getCalculator();
        var f = Polynomial.parse("x^2 + 2x+1", cal, Fraction::of);
        var g = Polynomial.parse("x^2 - x - 2", cal, Fraction::of);
        System.out.println("f(x) = " + f);
        System.out.println("g(x) = " + g);
        var h = f.gcd(g);
        System.out.println("gcd(f(x),g(x)) = " + h);
    }

    public static void computeGCD2() {
        var f = Multinomial.parse("x^2+2xy+y^2"); // = (x+y)^2
        var g = Multinomial.parse("x^2 + xy+xz+yz"); // = (x+y)(x+z)
        var h = Multinomial.gcd(f, g);
        System.out.println("f = " + f);
        System.out.println("g = " + g);
        System.out.println("gcd(f,g) = " + h);
    }

    public static void partialFractionDecomposition() {
        var cal = Calculators.longCal();
        var f1 = Polynomial.of(cal, 0L, 1L);
        var f2 = Polynomial.of(cal, 1L, 1L).pow(2);
        var f3 = Polynomial.of(cal, 2L, 1L).pow(3);

        var f = f1.multiply(f2).multiply(f3);
        var result = AlgebraUtil.partialFractionInt(Polynomial.one(cal), f);
        var parts = result.stream().map(p -> p.getFirst() + " * 1/" + p.getSecond()).reduce((x, y) -> x + " + " + y);
        System.out.println("1/(" + f + ") = ");
        System.out.println(parts.orElseThrow());
    }

    public static void findComplement() {
        var mc = Calculators.intModP(2); // calculator for field Z_2
        var g = Polynomial.parse("x^11 + x^10 + x^6 + x^5 + x^4 + x^2 +1", mc, Integer::parseInt);
        System.out.println("g(x) = " + g);
        var p = Polynomial.parse("x^23+1", mc, Integer::parseInt);
        var pc = Polynomial.getCalculator(mc);
        var h = pc.divideToInteger(p, g);
        System.out.println("The inverse of g(x) in Z2[x]/(x^23+1) is: " + h);
    }


    public static void main(String[] args) {
        partialFractionDecomposition();
        System.out.println();
        computeProduct();
        System.out.println();
        computeGCD();
        System.out.println();
        computeGCD2();
        System.out.println();
        findComplement();
    }
}
