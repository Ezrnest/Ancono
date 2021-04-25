/**
 * 2018-02-27
 */
package cn.ancono.math.algebra.abs.structure.finite;

import cn.ancono.math.MathUtils;
import cn.ancono.math.algebra.abs.FiniteGroups;
import cn.ancono.math.algebra.abs.group.Homomorphism;
import cn.ancono.math.algebra.abs.group.NotASubgroupException;
import cn.ancono.math.algebra.abs.group.finite.PermutationGroup;
import cn.ancono.math.algebra.abs.structure.Coset;
import cn.ancono.math.algebra.abs.structure.Group;
import cn.ancono.math.set.FiniteSet;
import org.jetbrains.annotations.NotNull;

/**
 * A limited group is a group with limited elements.
 *
 * @author liyicheng
 * 2018-02-27 17:50
 */
public interface FiniteGroup<T> extends Group<T> {

    /**
     * Gets a set of elements in this group. The first element must be
     * the identity element of this group.
     *
     * @return a finite set
     */
    @Override
    FiniteSet<T> getSet();

    /*
     * @see cn.ancono.math.algebra.abstractAlgebra.structure.Group#getSubgroups()
     */
    @Override
    FiniteSet<? extends FiniteGroup<T>> getSubgroups();

    @Override
    FiniteSet<? extends FiniteGroup<T>> getNormalSubgroups();


    @Override
    Coset<T, ? extends FiniteGroup<T>> getCoset(@NotNull T x, @NotNull Group<T> subGroup, boolean isLeft);

    @Override
    default Coset<T, ? extends FiniteGroup<T>> getCoset(@NotNull T x, boolean isLeft) {
        return getCoset(x, this, isLeft);
    }


    @Override
    FiniteSet<? extends Coset<T, ? extends FiniteGroup<T>>> getCosets(@NotNull Group<T> h, boolean isLeft);

    @Override
    default long indexOf(@NotNull Group<T> sub) {
        if (!isSubgroup(sub)) {
            throw new NotASubgroupException();
        }
        // lagrange theorem
        return index() / sub.index();
    }

    /**
     * Gets a group table of this finite group, which is a two-dimension array. The size of
     * both dimensions is the index of this group. For example, the group table of a triple group should be
     * <pre>
     * 0 1 2
     * 1 2 0
     * 2 0 1
     * </pre>
     * Assuming an array named <code>arr</code> contains all the elements in this group, where
     * <code>arr[0]</code> is the identity element,
     * the element of table[i][j] should be the result of apply(arr[i], arr[j]). Consequently, the
     * first row or column of the group table is equal to <code>arr</code>. The index of elements
     * except the identity element is not specifically required.
     */
    int[][] groupTable();


    /**
     * Returns a regular representation of this finite group.
     */
    PermutationGroup regularRepresent(boolean isRight);


    /**
     * Returns {@code regularRepresent(true)}
     */
    default PermutationGroup regularRepresent() {
        return regularRepresent(true);
    }


    @Override
    FiniteGroup<T> conjugateSubgroup(@NotNull Group<T> h, T x);

    @Override
    FiniteGroup<T> normalizer(Group<T> h);

    FiniteGroup<T> centralizer(T a);

    FiniteGroup<T> centralizer(Group<T> h);

    @Override
    FiniteGroup<? extends Coset<T, ? extends FiniteGroup<T>>> quotientGroup(Group<T> h);

    @Override
    Homomorphism<T,
            ? extends Coset<T, ? extends FiniteGroup<T>>,
            ? extends FiniteGroup<T>,
            ? extends FiniteGroup<? extends Coset<T, ? extends FiniteGroup<T>>>,
            ? extends FiniteGroup<T>> quotientGroupAndHomo(Group<T> h);


    /**
     * Returns the commutator group of this group.
     *
     * @return commutators
     */
    default FiniteGroup<T> commutatorGroup() {
        return FiniteGroups.commutatorGroup(this);
    }


    /**
     * Returns the order of <code>x</code> in the finite group.
     */
    default long order(T x) {
        long e = index();
        var factors = MathUtils.factorReduce(e);
        var mc = getCalculator();
        var one = mc.getIdentity();
        for (long[] factor : factors) {
            var p = factor[0];
            e = e / MathUtils.pow(p, (int) factor[1]);
            var x1 = mc.gpow(x, e);
            while (!mc.isEqual(x1, one)) {
                x1 = mc.gpow(x1, p);
                e *= p;
            }
        }
        return e;
    }
}
