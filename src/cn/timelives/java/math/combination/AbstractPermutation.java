/**
 * 2018-03-01
 */
package cn.timelives.java.math.combination;

import java.util.ArrayList;
import java.util.List;

/**
 * @author liyicheng
 * 2018-03-01 19:49
 *
 */
public abstract class AbstractPermutation implements Permutation {
	protected final int size;
	/**
	 * 
	 */
	public AbstractPermutation(int size) {
		this.size = size;
	}
	
	/*
	 * @see cn.timelives.java.math.combination.Permutation#size()
	 */
	@Override
	public int size() {
		return size;
	}
	
	/*
	 * @see cn.timelives.java.math.combination.Permutation#compose(cn.timelives.java.math.combination.Permutation)
	 */
	@Override
	public Permutation compose(Permutation before) {
		return Permutations.valueOf(apply(before.getArray()));
	}
	
	/*
	 * @see cn.timelives.java.math.combination.Permutation#andThen(cn.timelives.java.math.combination.Permutation)
	 */
	@Override
	public Permutation andThen(Permutation before) {
		return before.compose(this);
	}
	
	/*
	 * @see cn.timelives.java.math.combination.Permutation#reduce()
	 */
	@Override
	public List<Transposition> decomposeTransposition() {
		int size = size();
		List<Transposition> list = new ArrayList<>(size);
		int[] arr = getArray();
		for(int i=0;i<size;i++) {
			int j = arr[i];
			if(j==i) {
				continue;
			}
			list.add(new Permutations.Swap(size, i, j));
			arr[i] = arr[j];
			arr[j] = j;
		}
		return list;
	}
	
	/*
	 * @see cn.timelives.java.math.combination.Permutation#rotateReduce()
	 */
	@Override
	public List<Cycle> decompose() {
		int[] arr = getArray();
		int length = arr.length;
		List<Cycle> list = new ArrayList<>(size);
		boolean[] mark = new boolean[length];
		for(int i=0;i<length;i++) {
			if(mark[i]) {
				continue;
			}
			int t = i;
			boolean[] temp = new boolean[length];
			int n=0;
			while(temp[t]!=true) {
				temp[t] = true;
				t = arr[t];
				n++;
			}
			if(n==1) {
				mark[i] = true;
				continue;
			}
			int[] elements = new int[n];
			for(int j=0;j<n;j++) {
				elements[j] = t;
				mark[t] = true;
				t = arr[t];
			}
			list.add(new Permutations.Rotate(length,elements));
		}
		return list;
	}
	
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof AbstractPermutation)) {
			return false;
		}
		return Permutations.isEqual(this, (AbstractPermutation)obj);
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');
		for(int i : getArray()) {
			sb.append(i).append(',');
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(')');
		return sb.toString();
	}
}
