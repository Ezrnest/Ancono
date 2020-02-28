/**
 * 2018-02-27
 */
package cn.ancono.math.algebra.abstractAlgebra.structure;

import cn.ancono.math.algebra.abstractAlgebra.EqualRelation;
import cn.ancono.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.ancono.math.algebra.abstractAlgebra.group.Homomorphism;
import cn.ancono.math.set.MathSet;

/**
 * A group is a algebraic structure consisting of a set of elements and an operation.<p>
 * Assume the operation is "*", then
 * <ul>
 * <li>It is <i>associative</i>: {@code (a*b)*c = a*(b*c)}
 * <li>There exist an identity element e: {@code e*a = a*e = a}
 * <li>For every element a, its inverse element  <text>a<sup>-1</sup></text> exists, and
 *  	<text> a*a<sup>-1</sup> = a<sup>-1</sup>*a = e</text>
 * </ul>
 * <p>
 * Note that most of the methods defined on the interface are optional and it can throw an UnsupportedOperation
 * if necessary.
 *
 * @author liyicheng
 * 2018-02-27 17:32
 * @see <a href="https://en.wikipedia.org/wiki/Group_(mathematics)">Group</a>
 */
public interface Group<T> extends Monoid<T> {

    /**
     * Returns the group's calculator.
     */
    @Override
    GroupCalculator<T> getCalculator();

    /**
     * Gets the index of this group, which is equal to the
     * number of elements in this group. If this group contains infinite
     * elements, returns {@code -cardinality-1}.
     */
    long index();

    /**
     * Returns a set of subgroups.
     */
    MathSet<? extends Group<T>> getSubgroups();

    /**
     * Determines whether the given group is a subgroup of this group.
     *
     * @param g a group
     */
    boolean isSubgroup(Group<T> g);

    /**
     * Gets a set of normal subgroups.
     */
    MathSet<? extends Group<T>> getNormalSubgroups();

    /**
     * Determines whether the group is a normal subgroup of this group.
     */
    boolean isNormalSubgroup(Group<T> g);

    /**
     * Gets the coset of the element x of the whole group.
     *
     * @param x      an element in this group
     * @param isLeft determines whether it is a left coset.
     */
    default Coset<T, ? extends Group<T>> getCoset(T x, boolean isLeft) {
        return getCoset(x, this, isLeft);
    }

    /**
     * Gets the coset of the element x of a subgroup of this.
     *
     * @param x      an element in this group
     * @param isLeft determines whether it is a left coset.
     */
    Coset<T, ? extends Group<T>> getCoset(T x, Group<T> subGroup, boolean isLeft);

    /**
     * Gets a set of all cosets of the group <code>h</code> in this group.
     *
     * @param h      a subgroup
     * @param isLeft whether to return left coset
     */
    MathSet<? extends Coset<T, ? extends Group<T>>> getCosets(Group<T> h, boolean isLeft);

    /**
     * Returns the index of a subgroup in this group, returns
     * <text>-1-<i>(cardinality of the index)</i></text>.
     *
     * @param sub a subgroup
     */
    long indexOf(Group<T> sub);


    /**
     * Determines whether two subgroups of this group are conjugate, that is, there exists
     * an element <code>g</code> in this such that <text>g<sup>-1</sup>H<sub>1</sub>g = H<sub>2</sub></text>.
     *
     * @param h1 a subgroup
     * @param h2 another subgroup
     */
    boolean isConjugate(Group<T> h1, Group<T> h2);

    /**
     * Returns the conjugate subgroup of a subgroup of this:
     * <text>x<sup>-1</sup>Hx</text>.
     */
    Group<T> conjugateSubgroup(Group<T> h, T x);

    /**
     * Determines whether the given two elements are conjugate.
     */
    boolean isConjugate(T g1, T g2);

    /**
     * Returns a equal relation which considers conjugate subgroups
     * as the same.
     */
    default EqualRelation<Group<T>> conjugateClass() {
        return this::isConjugate;
    }

    /**
     * Returns a equal relation which considers conjugate elements
     * as the same.
     */
    default EqualRelation<T> conjugateElementClass() {
        return this::isConjugate;
    }

    /**
     * Returns the normalizer of a subgroup <code>g</code>, that is,
     * <text>{x | x<sup>-1</sup>Hx = H}</text>
     *
     * @param h a subgroup.
     * @return the normalizer of <code>g</code>
     */
    Group<T> normalizer(Group<T> h);

    /**
     * Returns the centralizer of an element <code>a</code>, that is,
     * <text>{x | x<sup>-1</sup>ax = a}</text>
     */
    Group<T> centralizer(T a);

    /**
     * Returns the centralizer of a subgroup <code>h</code>, which is
     * the intersect of the centralizer of all the elements in the subgroup.
     */
    Group<T> centralizer(Group<T> h);

    /**
     * Returns the quotient group of a normal subgroup <code>H</code>, namely
     * <text>G/H</text>.
     *
     * @param h a normal subgroup
     */
    Group<? extends Coset<T, ? extends Group<T>>> quotientGroup(Group<T> h);

    /**
     * Returns the homomorphism between this group and
     * the quotient group of a normal subgroup <code>H</code>, namely
     * <text>G/H</text>
     *
     * @param h a normal subgroup
     */
    Homomorphism<T,
            ? extends Coset<T, ? extends Group<T>>,//coset
            ? extends Group<T>,//G
            ? extends Group<? extends Coset<T, ? extends Group<T>>>,//H
            ? extends Group<T>>//K
    quotientGroupAndHomo(Group<T> h);
}
