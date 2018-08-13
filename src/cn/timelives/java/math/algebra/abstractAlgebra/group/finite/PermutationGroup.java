/**
 * 2018-03-05
 */
package cn.timelives.java.math.algebra.abstractAlgebra.group.finite;

import cn.timelives.java.math.algebra.abstractAlgebra.FiniteGroups;
import cn.timelives.java.math.algebra.abstractAlgebra.GroupCalculators;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.numberTheory.combination.Permutation;
import cn.timelives.java.math.numberTheory.combination.Permutations;
import cn.timelives.java.math.set.FiniteSet;

import java.util.HashMap;
import java.util.Map;

import static cn.timelives.java.utilities.Printer.print;

/**
 * @author liyicheng
 * 2018-03-05 17:11
 *
 */
public class PermutationGroup extends AbstractFiniteGroup<Permutation> {
	private final FiniteSet<Permutation> set;
	private final int permutationSize;
	/**
	 * 
	 */
	PermutationGroup(int n,FiniteSet<Permutation> set) {
		super(getPermutationCalculator(n));
		permutationSize = n;
		this.set = set;
	}
	
	/**
	 * Gets the permutation size.
	 * @return the permutation size
	 */
	public int getPermutationSize() {
		return permutationSize;
	}

    @Override
    public PermutationGroup regularRepresent(boolean isRight) {
        return this;
    }

    @Override
    public PermutationGroup regularRepresent() {
        return this;
    }


    /*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup#getSet()
	 */
	@Override
	public FiniteSet<Permutation> getSet() {
		return set;
	}

	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#index()
	 */
	@Override
	public long index() {
		return set.size();
	}
	
	
	private static final Map<Integer,GroupCalculator<Permutation>> pcMap = new HashMap<>();
	
	private static void checkPositive(int n) {
		if(n<=0) {
			throw new IllegalArgumentException("n<=0");
		}
	}
	
	/**
	 * Gets the group calculator for n-size permutation.
	 * @param n a positive number
	 * @return
	 */
	public static GroupCalculator<Permutation> getPermutationCalculator(int n){
		checkPositive(n);
		GroupCalculator<Permutation> pc = pcMap.get(n);
		if(pc==null) {
            pc = GroupCalculators.INSTANCE.createComposing(Permutations.identity(n));
			pcMap.put(n, pc);
		}
		return pc;
	}
	
	/**
	 * Returns a permutation group that contains all the n-size permutations.
	 * This method only supports n smaller than 13.
	 * @param n
	 * @return
	 */
	public static PermutationGroup symmetricGroups(int n) {
		if(n <= 0 || n > 12) {
			throw new IllegalArgumentException("Invalid n="+n);
		}
		FiniteSet<Permutation> set = Permutations.universe(n);
		return new PermutationGroup(n, set);
	}

    /**
     * Returns a permutation group that contains all the n-size even permutations.
     * @param n a positive integer
     * @return
     */
	public static PermutationGroup alternatingGroups(int n){
        FiniteSet<Permutation> set = Permutations.even(n);
        return new PermutationGroup(n, set);
    }

    /**
	 * Returns a permutation without checking whether the set is closed to the composing of 
	 * permutations for better efficiency.
	 * @param set
	 * @return
	 */
	public static PermutationGroup groupOfChecked(FiniteSet<Permutation> set) {
		int n = set.get(0).size();
		return new PermutationGroup(n, set);
	}

}
