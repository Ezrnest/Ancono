/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import java.util.List;
import java.util.function.Predicate;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.function.SVFunction;

/**
 * Describes the type of single variable compare structure.
 * @author liyicheng
 * 2017-10-06 19:34
 *
 */
public interface SVCompareStructure<T> extends CompareStructure<T>,Predicate<T>{
	
	/*
	 * @see cn.timelives.java.math.CompareStructure#getVariableCount()
	 */
	@Override
	default int getVariableCount() {
		return 1;
	}
	
	/**
	 * Gets the left part of the SVCompareStructure as a {@link SVFunction}.
	 * @return a SVFunction
	 */
	default SVFunction<T> asFunction(){
		return this::compute;
	}
	/**
	 * Computes the value of {@code x}.
	 * @param x a value
	 * @return the result
	 */
	T compute(T x);
	
	/**
	 * Determines whether the given value {@code x} is the solution of 
	 * this.
	 * @param x a number
	 * @return {@code true} if {@code x} is the solution
	 */
	boolean isSolution(T x);
	
	/*
	 * @see java.util.function.Predicate#test(java.lang.Object)
	 */
	@Override
	default boolean test(T x) {
		return isSolution(x);
	}
	/*
	 * @see cn.timelives.java.math.CompareStructure#isSolution(java.util.List)
	 */
	@Override
	default boolean isSolution(List<T> x) {
		return isSolution(x.get(0));
	}
	
	@Override
	default MathFunction<List<T>, T> getFunction() {
		return list -> asFunction().apply(list.get(0));
	}
}
