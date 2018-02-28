/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.structure;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.abstractAlgebra.calculator.SemigroupCalculator;
import cn.timelives.java.math.function.MathBinaryOperator;
import cn.timelives.java.math.set.AbstractLimitedSet;
import cn.timelives.java.math.set.CollectionSet;
import cn.timelives.java.math.set.LimitedSet;
import cn.timelives.java.math.set.MathSets;
import cn.timelives.java.utilities.ArraySup;

/**
 * A utility class for limited groups.
 * @author liyicheng
 * 2018-02-27 17:52
 *
 */
public final class LimitedGroups {

	/**
	 * 
	 */
	private LimitedGroups() {
	}
	
	static class LimitedGroupImpl<T> implements LimitedGroup<T>{
		
		private final GroupCalculator<T> gc;
		
		private final LimitedSet<T> set;
		
		/**
		 * @param gc
		 */
		public LimitedGroupImpl(GroupCalculator<T> gc,LimitedSet<T> set) {
			super();
			this.gc = gc;
			this.set = set;
		}

		
		
		
		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.Monoid#identity()
		 */
		@Override
		public T identity() {
			return gc.getIdentity();
		}

		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.Semigroup#getOperation()
		 */
		@Override
		public MathBinaryOperator<T> getOperation() {
			return gc::apply;
		}

		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.LimitedGroup#getSet()
		 */
		@Override
		public LimitedSet<T> getSet() {
			return set;
		}




		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.Semigroup#getCalculator()
		 */
		@Override
		public GroupCalculator<T> getCalculator() {
			return gc;
		}
		
	}
	
	/**
	 * Generates a group table, the group table is a two-dimension array. The length
	 * of each dimension is {@code 1+ <i>size of the group</i>}. The element
	 * arr[0][0] is {@code null} and the remaining part of the first row and first
	 * column contains all the elements of the group in order. The rest of the array
	 * satisfies {@code arr[i][j] = g.getOperation().apply(arr[i][0],arr[0][j])}.
	 * 
	 * @return
	 */
	public static <T> T[][] generateGroupTable(LimitedGroup<T> g) {
		LimitedSet<T> set = g.getSet();
		int size = ArraySup.castToArrayLength(set.size() + 1);
		MathBinaryOperator<T> f = g.getOperation();
		@SuppressWarnings("unchecked")
		T[][] arr = (T[][]) Array.newInstance(g.identity().getClass(), size, size);
		{
			int i = 1;
			for (T t : set) {
				arr[0][i] = t;
				arr[i][0] = t;
				i++;
			}
		}
		for (int i = 1; i < size; i++) {
			for (int j = 1; j < size; j++) {
				arr[i][j] = f.apply(arr[i][0], arr[0][j]);
			}
		}

		return arr;
	}
	
	
	@SafeVarargs
	public static <T> LimitedGroup<T> createGroup(GroupCalculator<T> f,T...elements){
		List<T> list = new ArrayList<>(elements.length+1);
		list.add(f.getIdentity());
		for(T x : elements) {
			if(f.isEqual(x, f.getIdentity())==false) {
				list.add(x);
			}
		}
		for(int i=1;i<list.size();i++) {
			T x = list.get(i);
			for(int j=1;j<list.size();j++) {
				T y = list.get(j);
				T z = f.apply(x, y);
				boolean contains = false;
				for(T t : list) {
					if(f.isEqual(t, z)) {
						contains = true;
						break;
					}
				}
				if(!contains) {
					list.add(z);
				}
				
			}
		}
		LimitedSet<T> set = MathSets.fromCollection(list,GroupCalculator.toMathCalculatorAdd(f));
		return new LimitedGroupImpl<>(f, set);
	}
}
