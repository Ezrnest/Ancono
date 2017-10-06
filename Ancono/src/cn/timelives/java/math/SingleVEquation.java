package cn.timelives.java.math;

import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.function.SVFunction;
import cn.timelives.java.math.numberModels.MathCalculator;

/**
 * A single variable equation.The number of variable is one.
 * @author lyc
 *
 */
public abstract class SingleVEquation<T> extends Equation<T> {

	protected SingleVEquation(MathCalculator<T> mc) {
		super(mc);
	}

	@Override
	public final int getVariableCount() {
		return 1;
	}
	/**
	 * Determines whether {@code x} is the solution of this equation.
	 * @param x a number
	 * @return {@code true} if x is the solution of this equation.
	 */
	public abstract boolean isSolution(T x) ;
	
	/**
	 * Gets the left part of the equation as a {@link SVFunction}.
	 * @return a SVFunction
	 */
	public abstract SVFunction<T> left();
	/**
	 * Gets the right part of the equation as a {@link SVFunction}.
	 * @return a SVFunction
	 */
	public abstract SVFunction<T> right();
	
	/*
	 * @see cn.timelives.java.math.CompareStructure#getLeft()
	 */
	@Override
	public MathFunction<List<T>, T> getLeft() {
		return list -> left().apply(list.get(0));
	}
	
	/*
	 * @see cn.timelives.java.math.CompareStructure#getRight()
	 */
	@Override
	public MathFunction<List<T>, T> getRight() {
		return list -> right().apply(list.get(0));
	}
	
	
	
	@Override
	public final boolean isSolution(List<T> so){
		if(so.size()!=1){
			throw new IllegalArgumentException("Number doesn't match");
		}
		return isSolution(so.get(0));
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.Equation#mapTo(java.util.function.Function, cn.timelives.java.math.number_models.MathCalculator)
	 */
	@Override
	public abstract <N> SingleVEquation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
}
