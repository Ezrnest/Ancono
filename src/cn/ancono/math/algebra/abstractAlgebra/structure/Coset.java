/**
 * 2018-03-01
 */
package cn.ancono.math.algebra.abstractAlgebra.structure;

import cn.ancono.math.set.MathSet;

/**
 * If <i>G</i> is a group, and <i>H</i> is a subgroup of <i>G</i>, and g is an element of <i>G</i>, then
 * <p>
 * g<i>H</i> = { gh : h an element of <i>H</i> } is the left coset of <i>H</i> in <i>G</i> with respect to
 * g, and <i>H</i>g = { hg : h an element of <i>H</i> } is the right coset of <i>H</i> in <i>G</i> with
 * respect to g.
 *
 * @author liyicheng 2018-03-01 18:41
 */
public interface Coset<T, G extends Group<T>> extends MathSet<T> {
    /**
     * Gets the group <i>G</i>.
     *
     * @return
     */
    G getGroup();

    /**
     * Gets the subgroup <i>H</i>.
     *
     * @return
     */
    G getSubGroup();

    /**
     * Gets the index of this coset, which is equal to the
     * number of elements in this coset. If this coset contains infinite
     * elements, returns {@code -cardinality-1}.
     *
     * @return
     */
    long index();

    /**
     * Returns the representatives of this coset.
     *
     * @return
     */
    MathSet<T> getRepresentatives();


}
