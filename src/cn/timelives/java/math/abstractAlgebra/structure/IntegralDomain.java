/**
 * 2018-02-28
 */
package cn.timelives.java.math.abstractAlgebra.structure;

/**
 * An integral domain is both a commutative ring and a domain.
 * @author liyicheng
 * 2018-02-28 18:42
 *
 */
public interface IntegralDomain<T,R extends IntegralDomain<T, R>> extends CommutativeRing<T,R>, Domain<T,R> {

}
