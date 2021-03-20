package samples;

import cn.ancono.math.algebra.linear.Matrix;
import cn.ancono.math.algebra.linear.QuadraticForm;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.math.numberModels.Multinomial;

/*
 * Created by lyc at 2020/2/29
 */
public class MatrixSample {
    public static void sample1() {
        var cal = Calculators.integer();
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

    public static void quadraticForm() {
        var expr = "10x^2+8xy+24xz+2y^2-28yz+z^2";
        var A = QuadraticForm.representationMatrix(Multinomial.valueOf(expr));
        System.out.println("A = ");
        A.printMatrix();
        var pair = A.congruenceDiagForm();
        var J = pair.getFirst();
        var P = pair.getSecond();
        System.out.println("P = ");
        P.printMatrix();
        System.out.println("J = ");
        J.printMatrix();
        System.out.println("P^T * A * P = ");
        // P^T*A*P = J
        Matrix.multiply(P.transpose(), A, P).printMatrix();
    }


    public static void main(String[] args) {
        sample1();
        System.out.println();
        quadraticForm();
    }
}
