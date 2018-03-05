/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.structure;

import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.set.MathSet;

/**
 * A group is a algebraic structure consisting of a set of elements and an operation.<p>
 * Assume the operation is "*", then
 * <ul>
 * <li>It is <i>associative</i>: {@code (a*b)*c = a*(b*c)}
 * <li>There exist an identity element e: {@code e*a = a*e = a}
 * <li>For every element a, its inverse element  <text>a<sup>-1</sup></text> exists, and 
 *  	<text> a*a<sup>-1</sup> = a<sup>-1</sup>*a = e</text>
 * </ul>
 * @see <a href="https://en.wikipedia.org/wiki/Group_(mathematics)">Group</a>
 * @author liyicheng
 * 2018-02-27 17:32
 *
 */
public interface Group<T,G extends Group<T,G>> extends Monoid<T> {
	
	/**
	 * Returns the group's calculator.
	 */
	@Override
	GroupCalculator<T> getCalculator();
	
	/**
	 * Gets the index of this group, which is equal to the 
	 * number of elements in this group. If this group contains infinite 
	 * elements, returns {@code -cardinality-1}.
	 * @return
	 */
	long index();
	
	/**
	 * Returns a set of subgroups.
	 * @return
	 */
	MathSet<G> getSubgroups();
	
	/**
	 * Determines whether the given group is a subgroup of this group.
	 * @param g a group
	 * @return
	 */
	boolean isSubgroup(G g);
	
	/**
	 * Gets a set of normal subgroups.
	 * @return
	 */
	MathSet<G> getNormalSubgroups();
	
	/**
	 * Determines whether the group is a normal subgroup of this group.
	 * @return
	 */
	boolean isNormalSubgroups(G g);
	
	/**
	 * Gets the coset of the element x of the whole group.
	 * @param x
	 * @param isLeft determines whether it is a left coset.
	 * @return
	 */
	default Coset<T,G> getCoset(T x,boolean isLeft){
		@SuppressWarnings("unchecked")
		G g = (G)this;
		return getCoset(x,g,isLeft);
	}
	/**
	 * Gets the coset of the element x of a subgroup of this.
	 * @param x
	 * @param isLeft determines whether it is a left coset.
	 * @return
	 */
	Coset<T,G> getCoset(T x,G subGroup,boolean isLeft);
}
