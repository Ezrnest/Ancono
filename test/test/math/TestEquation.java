/**
 * 2017-10-09
 */
package test.math;

import cn.ancono.math.equation.EquationSup;
import cn.ancono.math.equation.Type;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.MathCalculator;
import org.junit.Test;

import static cn.ancono.utilities.Printer.print;
/**
 * @author liyicheng
 * 2017-10-09 19:26
 *
 */
public class TestEquation {

    /**
     *
     */
    public TestEquation() {
    }

    MathCalculator<Double> mc = Calculators.doubleCal();

    @Test
    public void testSolve() {
        print(EquationSup.INSTANCE.solveQInequation(1d, 2d, 1d, Type.LESS_OR_EQUAL, mc));
        print(EquationSup.INSTANCE.solveQInequation(1d, -2d, -3d, Type.LESS_OR_EQUAL, mc));
        print(EquationSup.INSTANCE.solveQInequation(0d, 2d, -3d, Type.GREATER, mc));
        print(EquationSup.INSTANCE.solveQInequation(1d, -2d, -3d, Type.NOT_EQUAL, mc));
    }
}
