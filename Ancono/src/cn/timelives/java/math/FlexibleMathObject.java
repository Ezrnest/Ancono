/**
 * 2018-03-05
 */
package cn.timelives.java.math;

import java.util.Objects;

import cn.timelives.java.math.abstractAlgebra.calculator.EqualPredicate;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * @author liyicheng
 * 2018-03-05 20:19
 *
 */
public abstract class FlexibleMathObject<T, S extends EqualPredicate<T>> implements CalculatorHolder<T, S> {
	protected final S mc;
	/**
	 * 
	 */
	public FlexibleMathObject(S mc) {
		this.mc = Objects.requireNonNull(mc,"Calculator must not be null!"); 
	}

	/*
	 * @see cn.timelives.java.math.MathCalculatorHolder#getMathCalculator()
	 */
	@Override
	public S getMathCalculator() {
		return mc;
	}

	
	

	/**
	 * The equals method describes the equivalence in program of two math objects instead of the equal in math. 
	 * However
	 * If the type of number is different, then {@code false} will be returned.
	 */
	@Override
	public boolean equals(Object obj){
		return super.equals(obj);
	}
	
	/**
	 * A good {@code hashCode} method is recommended for every subclass extends the FlexibleMathObject, and 
	 * this method should be implemented whenever {@code equals()} is implemented.
	 */
	@Override
	public int hashCode(){
		return super.hashCode();
	}
	/**
	 * Returns a String representing this object, the {@link NumberFormatter} should 
	 * be used whenever a number is presented.
	 * @param nf
	 * @return
	 */
	public abstract String toString(NumberFormatter<T> nf);
	
	/**
	 * Returns a String representing this object, it is recommended that 
	 * the output of the number model should be formatted 
	 * through {@link NumberFormatter#format(Object, MathCalculator)}.
	 * @param nf
	 * @return
	 */
	@Override
	public String toString() {
		return toString(NumberFormatter.getToStringFormatter());
	}

}
