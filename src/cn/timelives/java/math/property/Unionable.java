/**
 * 2017-10-05
 */
package cn.timelives.java.math.property;

/**
 * Describes that something is "unionable", which provides a binary
 * operation on the object:
 * <ul>
 * <li>It is <i>reflexive</i>: {@code x.union(x)} should return an equivalent of {@code x}.
 * <li>It is <i>symmetric</i>: {@code x.union(y)} should return the same as {@code y.union(x)}.
 * <li>It is <i>consistent</i>: the result of {@code x.union(y)} should return the same as long as 
 * no changes happen to {@code x} or {@code y}.
 * </ul>
 * @author liyicheng 2017-10-05 12:58
 * @see Intersectable
 *
 */
public interface Unionable<T extends Unionable<T>> {
	
	/**
	 * Union {@code this} with another object.
	 * @param x an object.
	 * @return the union of {@code this} and {@code x}.
	 * @throws NullPointerException if {@code x==null}
	 */
	T union(T x);
}
