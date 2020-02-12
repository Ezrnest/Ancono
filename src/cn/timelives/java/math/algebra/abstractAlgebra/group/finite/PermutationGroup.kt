package cn.timelives.java.math.algebra.abstractAlgebra.group.finite

import cn.timelives.java.math.algebra.abstractAlgebra.FiniteGroups
import cn.timelives.java.math.algebra.abstractAlgebra.GroupCalculators
import cn.timelives.java.math.algebra.abstractAlgebra.HomomorphismMapping
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator
import cn.timelives.java.math.algebra.abstractAlgebra.structure.Group
import cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup
import cn.timelives.java.math.numberModels.Calculators
import cn.timelives.java.math.numberTheory.combination.Permutation
import cn.timelives.java.math.numberTheory.combination.Permutations
import cn.timelives.java.math.set.FiniteSet
import cn.timelives.java.math.set.MathSet
import cn.timelives.java.math.set.MathSets

import java.util.*

import cn.timelives.java.utilities.Printer.print

/**
 * @author liyicheng
 * 2018-03-05 17:11
 */
class PermutationGroup
/**
 *
 */
internal constructor(
        /**
         * Gets the permutation size.
         * @return the permutation size
         */
        val permutationSize: Int, private val set: FiniteSet<Permutation>) : AbstractFiniteGroup<Permutation>(getPermutationCalculator(permutationSize)) {

    override fun regularRepresent(isRight: Boolean): PermutationGroup {
        return this
    }

    override fun regularRepresent(): PermutationGroup {
        return this
    }

    /*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup#getSet()
	 */
    override fun getSet(): FiniteSet<Permutation> {
        return set
    }

    /*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#index()
	 */
    override fun index(): Long {
        return set.size()
    }

    /**
     * Returns a subgroup of this permutation group that only contains even permutations.
     */
    fun evenPermutations(): PermutationGroup {
        var containsOdd = false
        for (p in set) {
            if (!p.isEven) {
                containsOdd = true
            }
        }
        if (!containsOdd) {
            return this
        }
        val list = ArrayList<Permutation>(Math.toIntExact(set.size() / 2))
        for (p in set) {
            if (p.isEven) {
                list.add(p)
            }
        }
        return PermutationGroup(permutationSize,
                MathSets.fromCollection(list, EqualPredicate.naturalEqual()))
    }

    /**
     * Returns the stabilizers of several points [n] in this group as a subgroup.
     */
    fun stabilizer(vararg n : Int) : PermutationGroup {
        requireNotNull(n)
        val re = MathSets.filter(set,Permutations.getMathCalculator()) { it.apply(n).contentEquals(n) }
        return PermutationGroup(permutationSize,re)
    }

    /**
     * Returns the stabilizers of point [n] in this group as a subgroup.
     */
    fun stabilizer(n : Int) : PermutationGroup {
        val re = MathSets.filter(set,Permutations.getMathCalculator()) { it.apply(n) == n }
        return PermutationGroup(permutationSize,re)
    }

    /**
     * Returns the orbit of a point [n].
     */
    fun orb(n : Int) : FiniteSet<Int>{
        val orb = TreeSet<Int>()
        for( p in set){
            orb += p.apply(n)
        }
        return MathSets.fromCollection(orb,Calculators.getCalculatorInteger())
    }

    companion object{


        private val pcMap = HashMap<Int, GroupCalculator<Permutation>>()

        private fun checkPositive(n: Int) {
            if (n <= 0) {
                throw IllegalArgumentException("n<=0")
            }
        }

        /**
         * Gets the group calculator for n-size permutation.
         * @param n a positive number
         * @return
         */
        @JvmStatic
        fun getPermutationCalculator(n: Int): GroupCalculator<Permutation> {
            checkPositive(n)
            var pc: GroupCalculator<Permutation>? = pcMap[n]
            if (pc == null) {
                pc = GroupCalculators.createComposing(Permutations.identity(n))
                pcMap[n] = pc
            }
            return pc
        }

        /**
         * Returns a permutation group that contains all the n-size permutations.
         * This method only supports n smaller than 13.
         */
        @JvmStatic
        fun symmetricGroup(n: Int): PermutationGroup {
            if (n <= 0 || n > 12) {
                throw IllegalArgumentException("Invalid n=$n")
            }
            val set = Permutations.universe(n)
            return PermutationGroup(n, set)
        }

        /**
         * Returns a permutation group that contains all the n-size even permutations.
         * @param n a positive integer
         * @return
         */
        @JvmStatic
        fun alternatingGroups(n: Int): PermutationGroup {
            val set = Permutations.even(n)
            return PermutationGroup(n, set)
        }

        /**
         * Returns a permutation without checking whether the set is closed to the composing of
         * permutations for better efficiency.
         * @param set
         * @return
         */
        @JvmStatic
        fun groupOfChecked(set: FiniteSet<Permutation>): PermutationGroup {
            val n = set.get(0).size()
            return PermutationGroup(n, set)
        }


        /**
         * Returns a permutation group that is generated from the given permutations.
         * @param ps permutations
         */
        @JvmStatic
        fun generateFrom(vararg ps: Permutation): PermutationGroup {
            val size = ps[0].size()
            val set = TreeSet<Permutation>()
            set.add(Permutations.identity(size))
            val waitings = TreeSet<Permutation>()
            for (p in ps) {
                if (p.size() != size) {
                    throw IllegalArgumentException("Size mismatch!")
                }
                waitings.add(p)
            }
            while (!waitings.isEmpty()) {
                val n = waitings.pollFirst() ?: break
                if (set.contains(n)) {
                    continue
                }
                for (m in set) {
                    var t = m.compose(n)
                    addToWaitingsIfNotExist(set, waitings, n, t)
                    t = m.andThen(n)
                    addToWaitingsIfNotExist(set, waitings, n, t)
                }
                set.add(n)
                addToWaitingsIfNotExist(set, waitings, n, n.inverse())
            }
            return PermutationGroup(size, MathSets.fromCollection(set, EqualPredicate.naturalEqual()))
        }

        //	private static int[] MATH_INDEXED_ARRAY = new int[]{1,2,3,4};
        //
        //	static Set<Permutation> tempSet = new TreeSet<>();
        @JvmStatic
        private fun addToWaitingsIfNotExist(set: TreeSet<Permutation>, waitings: TreeSet<Permutation>, n: Permutation, t: Permutation) {
            if (!set.contains(t) && !waitings.contains(t) && n != t) {
                //            if(tempSet.contains(t)){
                //                print("?");
                //            }
                //            tempSet.add(t);
                //            print(t.apply(MATH_INDEXED_ARRAY));
                waitings.add(t)
            }
        }
    }


}


