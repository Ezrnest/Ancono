package samples;

import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.structure.Complex;
/*
 * Created by lyc at 2020/2/29
 */
public class NumberModelSample {
    public static void useFraction1() {
        Fraction a = Fraction.of("1/2");
        System.out.println(a);
        Fraction b = Fraction.ONE;
        var c = a.add(b);
        c = c.subtract(Fraction.ZERO);
        System.out.println(c);
        c = c.multiply(a);
        System.out.println(c);
        c = c.add(1);
        System.out.println(c);
    }

    public static void useComplex() {
        var cal = Calculators.getCalDouble();
        Complex<Double> z1 = Complex.real(1.0, cal);
        z1 = z1.squareRoot();
        Complex<Double> z2 = Complex.of(1.0, 2.0, cal);
        Complex<Double> z3 = z1.multiply(z2);
        System.out.println(z3);
    }


    public static void main(String[] args) {
        useFraction1();
        useComplex();
    }
}
