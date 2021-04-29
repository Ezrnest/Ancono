package samples;

import cn.ancono.math.algebra.linear.Matrix;
import cn.ancono.math.geometry.projective.MobiusTrans;
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
        var cal = Calculators.doubleCal();
        Complex<Double> z1 = Complex.real(1.0, cal);
        z1 = z1.squareRoot();
        Complex<Double> z2 = Complex.of(1.0, 2.0, cal);
        Complex<Double> z3 = z1.multiply(z2);
        System.out.println(z3);
    }

    public static void mobiusTrans() {
        var mc = Calculators.doubleDev();
        var one = Complex.one(mc);
        var i = Complex.i(mc);
        var n_i = i.negate();
        var f = MobiusTrans.Companion.to01Inf(one, i, n_i);
        //creates a Mobius transformation that maps 1,i,-1 to 0,1,inf
        System.out.println(f);
        System.out.println(f.apply(i));
        System.out.println(f.inverse().apply(Complex.inf(mc)));
    }

    public static void zModP1() {
        var mc = Calculators.intModP(29);
        var x = 4;
        var y = mc.reciprocal(x);
        System.out.println(y); // y = 22
        System.out.println(mc.multiply(x, y));
        // 4 * 22 = 88 = 1 + 3 * 29 = 1
    }

    public static void zModP() {
        var mc = Calculators.intModP(29);
        System.out.println();
        var matrix = Matrix.of(2, 2, mc,
                1, 2,
                3, 4);
        System.out.println("M = \n" + matrix);
        var inv = matrix.inverse();
        System.out.println("Inverse of M in Z mod 29 is \n" + inv);
        System.out.println("Check their product:");
        System.out.println(matrix.multiply(inv));
    }


    public static void main(String[] args) {
//        useFraction1();
//        useComplex();
//        mobiusTrans();
        zModP();
    }
}
