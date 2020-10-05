/**
 * 2018-02-27
 */
package cn.ancono.math.algebra.abs.structure;

import cn.ancono.math.algebra.abs.EqualRelation;
import cn.ancono.math.algebra.abs.calculator.GroupCalculator;
import cn.ancono.math.algebra.abs.group.Homomorphism;
import cn.ancono.math.set.FiniteSet;
import cn.ancono.math.set.InfiniteSet;
import cn.ancono.math.set.MathSet;
import org.jetbrains.annotations.NotNull;

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
    default long index() {
        var s = getSet();
        if (s instanceof FiniteSet) {
            return ((FiniteSet<T>) s).size();
        } else if (s instanceof InfiniteSet) {
            return -((InfiniteSet<T>) s).cardinalNumber() - 1;
        }
        throw new UnsupportedOperationException();
    }


    /**
     * Returns a set of subgroups.
     */
    default MathSet<? extends Group<T>> getSubgroups() {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines whether the given group is a subgroup of this group.
     *
     * @param g a group
     */
    default boolean isSubgroup(@NotNull Group<T> g) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a set of normal subgroups.
     */
    default MathSet<? extends Group<T>> getNormalSubgroups() {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines whether the group is a normal subgroup of this group.
     */
    default boolean isNormalSubgroup(@NotNull Group<T> g) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets the coset of the element x of the whole group.
     *
     * @param x      an element in this group
     * @param isLeft determines whether it is a left coset.
     */
    default Coset<T, ? extends Group<T>> getCoset(@NotNull T x, boolean isLeft) {
        return getCoset(x, this, isLeft);
    }

    /**
     * Gets the coset of the element x of a subgroup of this.
     *
     * @param x      an element in this group
     * @param isLeft determines whether it is a left coset.
     */
    default Coset<T, ? extends Group<T>> getCoset(@NotNull T x, @NotNull Group<T> subGroup, boolean isLeft) {
        throw new UnsupportedOperationException();
    }

    /**
     * Gets a set of all cosets of the group <code>h</code> in this group.
     *
     * @param h      a subgroup
     * @param isLeft whether to return left coset
     */
    default MathSet<? extends Coset<T, ? extends Group<T>>> getCosets(@NotNull Group<T> h, boolean isLeft) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the index of a subgroup in this group, returns
     * <text>-1-<i>(cardinality of the index)</i></text>.
     *
     * @param sub a subgroup
     */
    default long indexOf(@NotNull Group<T> sub) {
        throw new UnsupportedOperationException();
    }


    /**
     * Determines whether two subgroups of this group are conjugate, that is, there exists
     * an element <code>g</code> in this such that <text>g<sup>-1</sup>H<sub>1</sub>g = H<sub>2</sub></text>.
     *
     * @param h1 a subgroup
     * @param h2 another subgroup
     */
    default boolean isConjugate(@NotNull Group<T> h1, @NotNull Group<T> h2) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the conjugate subgroup of a subgroup of this:
     * <text>x<sup>-1</sup>Hx</text>.
     */
    default Group<T> conjugateSubgroup(@NotNull Group<T> h, T x) {
        throw new UnsupportedOperationException();
    }

    /**
     * Determines whether the given two elements are conjugate.
     */
    default boolean isConjugate(T g1, T g2) {
        throw new UnsupportedOperationException();
    }

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
     * Returns the normalizer of a subgroup <code>h</code>, that is,
     * <text>{x | x<sup>-1</sup>Hx = H}</text>
     *
     * @param h a subgroup.
     * @return the normalizer of <code>h</code>
     */
    default Group<T> normalizer(Group<T> h) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the centralizer of an element <code>a</code>, that is,
     * <text>{x | x<sup>-1</sup>ax = a}</text>
     */
    default Group<T> centralizer(T a) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the centralizer of a subgroup <code>h</code>, which is
     * the intersect of the centralizer of all the elements in the subgroup.
     */
    default Group<T> centralizer(Group<T> h) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the quotient group of a normal subgroup <code>H</code>, namely
     * <text>G/H</text>.
     *
     * @param h a normal subgroup
     */
    default Group<? extends Coset<T, ? extends Group<T>>> quotientGroup(Group<T> h) {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the homomorphism between this group and
     * the quotient group of a normal subgroup <code>H</code>, namely
     * <text>G/H</text>
     *
     * @param h a normal subgroup
     */
    default Homomorphism<T,
            ? extends Coset<T, ? extends Group<T>>,//coset
            ? extends Group<T>,//G
            ? extends Group<? extends Coset<T, ? extends Group<T>>>,//H
            ? extends Group<T>>//K
    quotientGroupAndHomo(Group<T> h) {
        throw new UnsupportedOperationException();
    }
}
