package cn.timelives.java.utilities.structure;
/**
 * Partial order is a relation between two object.The {@linkplain PartialOrderComparator} implements 
 * this kind of relationship and following requirements must be fitted.
 * <ul>
 * 	<li>It is <i>reflexive</i>:For any comparator and any partial order, {@code compareWith(A,A)} must return {@code true}.
 * 	<li>It is <i>transitive</i>:If {@code compareWith(A,B)==true} and {@code compareWith(B,C)==true}, then {@code compareWith(A,C)}
 * 		must return {@code true}.
 * 	<li>It is <i>consistent</i>: For any partial comparable object,multiple invocations of
 *     {@code compareWith(A,B)} consistently return {@code true}
 *     or consistently return {@code false}, if no change has happened to both A and B
 *  <li>It is partially <i>symmetric</i>:If {@code compareWith(A,B)==true} and {@code compareWith(B,A)==true},then A and B are literally 
 *  	the same in this relationship,and for any object C {@code compareWith(A,C)} and {@code compareWith(B,C)} must return the same value,
 *  	{@code compareWith(C,A)} and {@code compareWith(C,B)} must return the same value too.
 * </ul>
 * This relationship is designed for the partial order in math , the using of math symbol {@literal ≧ } indicates this relationship.For instance,
 * {@literal A ≧ B} means {@code compareWith(A,B)==true}.
 * <p>
 * This relation is partially symmetric,which means even if {@code compareWith(A,B)==true},{@code compareWith(B,A)} may return either {@code true}
 * or {@code false},and if {@code compareWith(A,B)==false},{@code compareWith(B,A)} may return either {@code true} or {@code false} as well.A simple 
 * example is interval in math.We dim a partial order that {@code compare(A,B)} returns true only if {@literal A ∪  B = A} , in other words , A is bigger than B.
 * Examples are listed below:<p>
 * <ul>
 * <li>{@literal A ≧ B} and {@literal B ≧ A} : A = B = [0,1]. 
 * <li>{@literal A ≧ B} but {@literal B ≧ A} is false. A = [0,2] , B = [0,1].
 * <li>Both {@literal A ≧ B} and {@literal B ≧ A} are false. A = [0,2] , B = [1,3]. 
 * </ul>
 * <P>
 * This relationship is similar to {@link Object#equals(Object)} and {@link Comparable#compareTo(Object)} in some ways.Therefore , the relationship between
 * this partial order and {@code equals()} and {@code compareTo()} should be clear.It is highly recommended that {@literal A ≧ B && B ≧ A} should be equal 
 * to {@code A.equals(B) == true}.  
 * <p>For {@code comparaTo()} method , if implemented , if {@literal A ≧ B} , then {@code A.ompareTo(B)} should return either 
 * {0,1} or {0,-1} , which means unless {@literal B ≧ A}, the {@code compareTo()} method should return a same value (either 1 or -1) for any pair (A,B) that 
 *  {@literal A ≧ B}.In the special situation of {@literal A ≧ B && B ≧ A}, {@code A.compareTo(B)} should return {@code 0}.
 * @author lyc
 *
 * @param <E> the type of object for this comparator to compare.
 */
@FunctionalInterface
public interface PartialOrderComparator<T> {
	/**
	 * This method implements the compare of two objects.The order of the two 
	 * parameter is specific.{@code null} value is not acceptable.
	 * 
	 * 
	 * @param A an object
	 * @param B another object
	 * 
	 * @return true if {@literal A ≧ B} , else false.
	 * 
	 * @throws NullPointerException if either A or B is null.
	 */
	public boolean compareWith(T A,T B);
	
	/**
	 * Returns whether A is equal to B in the comparing rule of {@code this}.This method is 
	 * equal to {@code compareWith(A,B) && compareWith(B,A)}.
	 * @param A an object
	 * @param B another object
	 * @return {@code compareWith(A,B) && compareWith(B,A)}
	 * @throws NullPointerException if either A or B is null.
	 */
	public default boolean isEqual(T A,T B){
		return compareWith(A,B) && compareWith(B,A);
	}
	
	
}
