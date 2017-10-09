package cn.timelives.java.math.equation;

import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * Equation is an abstract class for equations in math. 
 * An equation can be presented as <pre><i>f(x)</i> = 0</pre>, where 
 * <i>f(x)</i> is a MathFunction, and {@link MathCalculator#isZero(Object)} is 
 * used to determine the solution.
 * @author lyc
 *
 * @param <T>
 */
public abstract class Equation<T,S> extends AbstractCompareStructure<T,S> {

	protected Equation(MathCalculator<T> mc) {
		super(mc,Type.EQUAL);
	}
	
}
