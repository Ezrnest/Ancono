/**
 * 2018-03-05
 */
package cn.timelives.java.math.abstractAlgebra.group.finite;

import java.util.HashMap;
import java.util.Map;

import cn.timelives.java.math.abstractAlgebra.GroupCalculators;
import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.combination.Permutation;
import cn.timelives.java.math.combination.Permutations;
import cn.timelives.java.math.set.FiniteSet;

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
	
	
	
	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.finite.FiniteGroup#getSet()
	 */
	@Override
	public FiniteSet<Permutation> getSet() {
		return set;
	}

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#index()
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
			pc = GroupCalculators.createComposing(Permutations.identity(n));
			pcMap.put(n, pc);
		}
		return pc;
	}
	
}