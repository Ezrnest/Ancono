/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.structure;

/**
 * A domain is a nonzero ring in which {@code ab = 0} implies 
 * {@code a = 0} {@code or b = 0}. (Sometimes such a ring is said to "have the zero-product property"
 * @author liyicheng
 * 2018-02-28 18:38
 *
 */
public interface Domain<T,R extends Domain<T, R>> extends Ring<T,R>{
	
}
