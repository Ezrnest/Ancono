package cn.timelives.java.math;

import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.api.NumberFormatter;

import java.util.function.Function;

/**
 * Describes an object that is used in math and is flexible for all type of number models which are used in math.
 * The corresponding math calculator should be given when such an object is created and the calculator may 
 * be used. For example, an interval may extend this abstract class and the type of bound can be switched from 
 * Integer to Double , or other kind of math number. 
 * <p>
 * @author lyc
 * @param <T> the kind of object used, usually a subclass of number
 * @see MathCalculator
 */
public abstract class MathObject<T> extends FlexibleMathObject<T, MathCalculator<T>>{
	/**
	 * Create a flexible math object with the given MathCalculator,the MathCalculator should not 
	 * be null.
	 * @param mc
	 */
	protected MathObject(MathCalculator<T> mc){
		super(mc);
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.MathCalculatorHolder#getMathCalculator()
	 */
	@Override
	public MathCalculator<T> getMathCalculator() {
		return mc;
	}



	/**
	 * Map this object using the number type {@code T} to a new object using the number type {@code N}. This 
	 * method is a core method of {@link MathObject}. The subclasses can always changes the return
	 * type to it instead of just returning a FlexibleMathObject. 
	 * @param newCalculator a new calculator of type {@code N}
	 * @param mapper the function used in mapping.
	 * @param <N> the new number type.
	 * @return a new FlexibleMathObject of type N
	 */
	public abstract <N> MathObject<N> mapTo(Function<T,N> mapper, MathCalculator<N> newCalculator);
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
	 * Determines whether the two objects using the same number type is the same. In this method,
	 * {@link MathCalculator#isEqual(Object, Object)} is used instead of {@code Object.equals()} method.
	 * This method is basically equal to {@link #valueEquals(MathObject, Function)} as
	 * {@code this.valueEquals(obj,x -> x)}
	 * 
	 * @param obj another FlexibleMathObject
	 * @return {@code true} if this is equal to obj , else {@code false}.
	 * @throws ClassCastException if {@code obj} is not using number type {@code T}
	 */
	public abstract boolean valueEquals(MathObject<T> obj);
	
	
	
	/**
	 * Determines whether the two objects are the same according to the given mapper and the calculator.This 
	 * method is based on math definition so this method should not simply use {@code equal()} method , instead,
	 * {@linkplain MathCalculator#isEqual(Object, Object)} should be used when comparing two numbers. This method 
	 *  provides the equality in math.
	 * @param obj another object, type is the same as this
	 * @param mapper a function
	 * @param <N> another type of number
	 * @return {@code true} if this is equal to obj , else {@code false}.
	 * @throws ClassCastException if {@code obj} is not using number type {@code N}
	 */
	public <N> boolean valueEquals(MathObject<N> obj, Function<N,T> mapper){
		return valueEquals(obj.mapTo(mapper, mc));
	}
	/**
	 * Returns a String representing this object, the {@link NumberFormatter} should 
	 * be used whenever a number is presented.
	 * @param nf
	 * @return
	 */
	public abstract String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf);
	
	/**
	 * Returns a String representing this object, it is recommended that 
	 * the output of the number model should be formatted 
	 * through {@link NumberFormatter#format(Object, MathCalculator)}.
	 * @return
	 */
	@Override
	public String toString() {
		return toString(FlexibleNumberFormatter.getToStringFormatter());
	}
	
	
}
