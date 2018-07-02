/**
 * 2018-02-27
 */
package cn.timelives.java.math.algebra.abstractAlgebra.structure.finite;

import cn.timelives.java.math.algebra.abstractAlgebra.group.finite.PermutationGroup;
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Group;
import cn.timelives.java.math.set.FiniteSet;

/**
 * A limited group is a group with limited elements.
 * @author liyicheng
 * 2018-02-27 17:50
 *
 */
public interface FiniteGroup<T,G extends FiniteGroup<T, G>> extends Group<T,G> {

    /**
     * Gets a set of elements in this group. The first element must be
     * the identity element of this group.
     * @return a finite set
     */
	@Override
	FiniteSet<T> getSet();
	
	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#getSubgroups()
	 */
	@Override
	FiniteSet<G> getSubgroups();

	/**
	 * Gets a group table of this finite group, which is a two-dimension array. The size of
	 * both dimensions is the index of this group.
	 * @return
	 */
	int[][] groupTable();


    /**
     * Returns a regular representation of this finite group.
     * @return
     */
	PermutationGroup regularRepresent(boolean isRight);


    /**
     * Returns {@code regularRepresent(true)}
     * @return
     */
	default PermutationGroup regularRepresent(){
	    return regularRepresent(true);
    }
}
