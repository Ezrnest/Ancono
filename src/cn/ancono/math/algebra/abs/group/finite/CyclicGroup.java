package cn.ancono.math.algebra.abs.group.finite;

import cn.ancono.math.MathUtils;
import cn.ancono.math.algebra.abs.calculator.AbelGroupCal;
import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.algebra.abs.structure.AbelianGroup;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.math.set.FiniteSet;
import cn.ancono.math.set.MathSets;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * An implementation of cyclic group using the type of Integer.
 */
public class CyclicGroup extends AbstractFiniteGroup<Integer> implements AbelianGroup<Integer> {
    /**
     * number of elements
     */
    final int size;
    /**
     * The minimum generator
     */
    final int generator;
    /**
     * size * generator
     */
    final int mod;

    final CyclicCalInt mc;

    /**
     *
     */
    CyclicGroup(int size, int generator, int mod) {
        mc = new CyclicCalInt(mod);
        this.size = size;
        this.generator = generator;
        this.mod = mod;
    }

    CyclicGroup(int size) {
        this(size, 1, size);
    }


    @Override
    public AbelGroupCal<Integer> getAbelCal() {
        return mc;
    }

    @Override
    public FiniteSet<Integer> getSet() {
        var list = new ArrayList<Integer>(size);
        for (int n = 0; n < mod; n += generator) {
            list.add(n);
        }
        return MathSets.fromCollection(list, Calculators.integer());
    }

    /**
     * Gets a set of generators of this cyclic group.
     *
     * @return a finite set
     */
    public FiniteSet<Integer> generators() {
        var list = new ArrayList<Integer>(size / 2);
        for (int i = 1, n = generator; i < size; i++) {
            if (MathUtils.gcd(i, size) == 1) {
                list.add(n);
            }
            n += generator;
        }
        list.trimToSize();
        return MathSets.fromCollection(list, Calculators.integer());
    }

    @NotNull
    @Override
    public FiniteSet<CyclicGroup> getSubgroups() {
        int num = Math.toIntExact(MathUtils.factorCount(size));
        var list = new ArrayList<CyclicGroup>(num);
        list.add(IDENTITY);
        list.add(this);
        for (int i = 2, n = 2 * generator; i < size; i++) {
            if (size % i == 0) {
                list.add(new CyclicGroup(size / i, n, mod));
            }
            n += generator;
        }
        return MathSets.fromCollection(list, EqualPredicate.naturalEqual(CyclicGroup.class));
    }

    @NotNull
    @Override
    public FiniteSet<CyclicGroup> getNormalSubgroups() {
        return getSubgroups();
    }

    public FiniteSet<CyclicGroup> getSubgroupsAsCyclic() {
        int num = Math.toIntExact(MathUtils.factorCount(size));
        var list = new ArrayList<CyclicGroup>(num);
        list.add(IDENTITY);
        for (int i = 2; i < size; i++) {
            if (size % i == 0) {
                list.add(new CyclicGroup(i, generator * size / i, mod));
            }
        }
        list.add(this);
        return MathSets.fromCollection(list, EqualPredicate.naturalEqual(CyclicGroup.class));
    }

    /**
     * Determines whether the group can be a subgroup of this after
     * homomorphism.
     *
     * @param cg
     * @return
     */
    public boolean isSubgroupHomo(CyclicGroup cg) {
        return size % cg.size == 0;
    }

    static class CyclicCalInt implements AbelGroupCal<Integer> {
        final int mod;

        //int range: 0<=x < mod
        CyclicCalInt(int mod) {
            this.mod = mod;
        }

        @NotNull
        @Override
        public Integer negate(@NotNull Integer x) {
            int _x = x;
            if (_x == 0) {
                return x;
            } else {
                return mod - _x;
            }
        }

        @NotNull
        @Override
        public Integer getZero() {
            return 0;
        }

        @NotNull
        @Override
        public Integer add(@NotNull Integer x, @NotNull Integer y) {
            return (x + y) % mod;
        }

        @Override
        public boolean isEqual(@NotNull Integer x, @NotNull Integer y) {
            return x.equals(y);
        }

        @NotNull
        @Override
        public Integer multiplyLong(@NotNull Integer x, long n) {
            long t = x * n;
            if (t >= 0) {
                return (int) (t % mod);
            } else {
                return (int) ((-t) % mod);
            }
        }

    }

    private static final CyclicGroup IDENTITY = new CyclicGroup(1);

    public static CyclicGroup getIdentityGroup() {
        return IDENTITY;
    }

    /**
     * Creates a cyclic group of the given size.
     *
     * @param size
     * @return
     */
    public static CyclicGroup createGroup(int size) {
        return new CyclicGroup(size);
    }

//    public static void main(String[] args) {
//        createGroup(12).getSubgroupsAsCyclic().forEach( x -> Printer.print(CollectionSup.iteratorToArray(x.getSet().iterator(),x.size)));
////        printMatrix(new CyclicGroup(3).groupTable());
////        PermutationGroup.alternatingGroups(3).getSet().forEach(Printer::print);
////        printMatrix(PermutationGroup.alternatingGroups(3).groupTable());
////        print(FiniteGroups.homoEquals(new CyclicGroup(3),PermutationGroup.alternatingGroups(3)));
//
//    }
}
