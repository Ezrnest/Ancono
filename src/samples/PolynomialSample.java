package samples;

import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.structure.Polynomial;

public class PolynomialSample {
    public static void computeProduct() {
        var cal = Calculators.getCalDouble();
        // we use double as the type of the coefficient of the polynomials
        var f = Polynomial.valueOf(cal, 1.0, 1.0, 2.0, 3.0); // 1 + x + 2x^2 + 3 x^3
        var g = Polynomial.binomialPower(2.0, 3, cal); // (x-2)^3
        System.out.println("f(x) = " + f);
        System.out.println(g);
        var h = f.multiply(g);
        System.out.println(h);
    }


    public static void main(String[] args) {
        computeProduct();
    }
}
