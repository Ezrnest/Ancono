package cn.ancono.math.equation;

import cn.ancono.math.MathCalculator;

/**
 * Equation is an abstract class for equations in math.
 * An equation can be presented as <pre><i>f(x)</i> = 0</pre>, where
 * <i>f(x)</i> is a MathFunction, and {@link MathCalculator#isZero(Object)} is
 * used to determine the solution.
 *
 * @param <T>
 * @author lyc
 */
public abstract class Equation<T, S> extends AbstractCompareStructure<T, S> {

    protected Equation(MathCalculator<T> mc) {
        super(mc, Type.EQUAL);
    }

}
