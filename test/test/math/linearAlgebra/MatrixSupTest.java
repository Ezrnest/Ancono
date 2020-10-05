package test.math.linearAlgebra;

import cn.ancono.math.algebra.linear.Matrix;
import cn.ancono.math.algebra.linear.MatrixSup;
import cn.ancono.math.numberModels.Calculators;
import org.junit.Test;
import test.math.TestUtils;

/*
 * Created by liyicheng at 2020-03-10 13:36
 */
public class MatrixSupTest {

    @Test
    public void toHermitFrom() {
        var cal = Calculators.getCalIntegerExact();
        var m = MatrixSup.parseMatrix("[[1 2 3][4 5 6][7 8 9]]", cal, Integer::parseInt);
        var w = m.toSmithForm();
        w = w.applyFunction(cal::abs);
        TestUtils.assertValueEquals(Matrix.diag(new Integer[]{1, 3, 0}, cal), w);
    }

}