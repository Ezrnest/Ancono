/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.structure.finite;

import cn.timelives.java.math.abstractAlgebra.structure.Group;
import cn.timelives.java.math.set.FiniteSet;

/**
 * A limited group is a group with limited elements.
 * @author liyicheng
 * 2018-02-27 17:50
 *
 */
public interface FiniteGroup<T,G extends FiniteGroup<T, G>> extends Group<T,G> {
	
	@Override
	FiniteSet<T> getSet();
	
	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#getSubgroups()
	 */
	@Override
	FiniteSet<G> getSubgroups();
	
	
}
