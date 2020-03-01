package samples;

import cn.ancono.math.algebra.linearAlgebra.Matrix;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;

import static cn.ancono.math.numberModels.Fraction.of;

public class MatrixSample {
    public static void sample1() {
        var cal = Calculators.getCalInteger();
        var calFrac = Fraction.getCalculator();
        var m1 = Matrix.of(cal, 2, 2,
                1, 2,
                4, 5)
                .mapTo(Fraction::of, calFrac);
        var m2 = Matrix.of(cal, 2, 2,
                3, -6,
                -4, 8)
                .mapTo(Fraction::of, calFrac);
        var m3 = Matrix.multiply(m1, m2);
        m3.printMatrix();
        var det = m3.calDet();
        var rank = m3.calRank();
        System.out.println("Det of the matrix: " + det);
        System.out.println("Rank of the matrix: " + rank);
    }


    public static void main(String[] args) {
        sample1();
    }
}
