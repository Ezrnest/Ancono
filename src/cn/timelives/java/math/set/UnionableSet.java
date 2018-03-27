/**
 * 2017-10-05
 */
package cn.timelives.java.math.set;

import cn.timelives.java.math.Unionable;

/**
 * A set that is "unionable". The implementor must ensure that 
 * if {@code x.contains(e)==true}, then for any {@code y}, {@code x.union(y).contains(e)==true}.
 * a set implements this 
 * @author liyicheng
 * 2017-10-05 13:07
 * @see IntersectableSet
 *
 */
public interface UnionableSet<T,S extends UnionableSet<T,S>> extends MathSet<T>, Unionable<S>{
	
	
}
