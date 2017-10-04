package cn.timelives.java.math.numberModels;
/**
 * Simplifiable object is object that can be simplified.Usually,some FlexibleMathObject are 
 * simplifiable,and because the {@code simplify} method may cost a long time and in following 
 * calculations,the simplification is almost meaningless.Therefore,the programmer should decide 
 * when to simplify the object.
 * <p>
 * For example,a line that {@code 2x+4y+6=0} can be simplified to {@code x+2y+3=0},and a line that 
 * {@literal (a+1)x + (a^2+2a+1)y + a^2-1 = 0 (|a|!=1)} can be simplified to {@literal x + (a+1)y + a-1 = 0}.
 * This kind of simplification in many situations is very necessary and can produce a faster performance.
 * <p>
 * A simplifiable object not always need a simplifier to supply with the simplification, but 
 * the simplifying work it can do is limited. 
 * @author lyc
 * @param T the type of the number that the object is using.
 * @param S the type of the object
 * @see Simplifier
 */
public interface Simplifiable<T,S extends Simplifiable<T,S>> {
	/**
	 * Simplify this object,this method should not change this object itself,
	 * but return a new object.
	 * @return a new simplified object
	 */
	public S simplify();
	/**
	 * Simplify this object with the given Simplifier,this method should not change this object itself,
	 * but return a new object.
	 * @param sim
	 * @return a new simplified object
	 */
	public S simplify(Simplifier<T> sim);
	
}
