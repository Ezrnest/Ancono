package samples;

import cn.ancono.math.algebra.AlgebraUtil;
import cn.ancono.math.algebra.DecomposedPoly;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.structure.Polynomial;

/*
 * Created by lyc at 2020/3/1
 */
public class PolynomialSample {
    public static void computeProduct() {
        var cal = Calculators.getCalDouble();
        // we use double as the type of the coefficient of the polynomials
        var f = Polynomial.valueOf(cal, 1.0, 1.0, 2.0, 3.0); // 1 + x + 2x^2 + 3 x^3
        var g = Polynomial.binomialPower(2.0, 3, cal); // (x-2)^3
        System.out.println("f(x) = " + f);
        System.out.println("g(x) = " + g);
        var h = f.multiply(g);
        System.out.println("f(x)g(x) = " + h);
    }

    public static void computeGCD() {
        var calInt = Calculators.getCalInteger();
        var cal = Fraction.getCalculator();
        var f = Polynomial.valueOf(calInt, 1, 2, 1).mapTo(Fraction::of, cal); // 1 + 2x + x^2
        var g = Polynomial.valueOf(calInt, -2, -1, 1).mapTo(Fraction::of, cal); // -2 - x + x^2
        System.out.println("f(x) = " + f);
        System.out.println("g(x) = " + g);
        var h = f.gcd(g);
        System.out.println("gcd(f(x),g(x)) = " + h);
    }

    public static void partialFractionDecomposition() {
        var cal = Calculators.getCalLong();
        var f1 = Polynomial.valueOf(cal, 0L, 1L);
        var f2 = Polynomial.valueOf(cal, 1L, 1L).pow(2);
        var f3 = Polynomial.valueOf(cal, 2L, 1L).pow(3);

        var f = f1.multiply(f2).multiply(f3);
        var result = AlgebraUtil.partialFractionInt(Polynomial.one(cal), f);
        var parts = result.stream().map(p -> p.getFirst() + " * 1/" + p.getSecond()).reduce((x, y) -> x + " + " + y);
        System.out.println("1/(" + f + ") = ");
        System.out.println(parts.orElseThrow());
    }


    public static void main(String[] args) {
        partialFractionDecomposition();
        System.out.println();
        computeProduct();
        System.out.println();
        computeGCD();
    }
}
