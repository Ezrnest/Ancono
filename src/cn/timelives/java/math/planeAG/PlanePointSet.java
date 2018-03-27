/**
 * 
 */
package cn.timelives.java.math.planeAG;

/**
 * Plane point set is a point set that can determines whether a given point 
 * is in the set.
 * @author liyicheng
 *
 */
public interface PlanePointSet<T> {
	/**
	 * Determines whether the given point is contained.
	 * @param p a point
	 * @return {@code true} if {@code p} is contained.
	 */
	boolean contains(Point<T> p);
}
