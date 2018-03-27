/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation;

import cn.timelives.java.math.function.SVFunction;

import java.util.function.Predicate;

/**
 * Describes the type of single variable compare structure.
 * @author liyicheng
 * 2017-10-06 19:34
 *
 */
public interface SVCompareStructure<T> extends Predicate<T>,CompareStructure<T, T>{
	
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
}
