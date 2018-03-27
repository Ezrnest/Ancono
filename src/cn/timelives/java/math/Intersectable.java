/**
 * 2017-10-05
 */
package cn.timelives.java.math;

/**
 * Describes that something is "intersectable", which provides a binary
 * operation on the object:
 * <ul>
 * <li>It is <i>reflexive</i>: {@code x.intersect(x)} should return an equivalent of {@code x}
 * <li>It is <i>symmetric</i>: {@code x.intersect(y)} should return the same as {@code y.intersect(x)}.
 * <li>It is <i>consistent</i>: the result of {@code x.intersect(y)} should return the same as long as 
 * no changes happen to {@code x} or {@code y}.
 * </ul>
 * 
 * @author liyicheng 2017-10-05 12:58
 * @see Unionable
 */
public interface Intersectable<T extends Intersectable<T>> {
	
	/**
	 * Intersect {@code this} with another object.
	 * @param x an object.
	 * @return the intersect of {@code this} and {@code x}.
	 * @throws NullPointerException if {@code x==null}
	 */
	T intersect(T x);
}
