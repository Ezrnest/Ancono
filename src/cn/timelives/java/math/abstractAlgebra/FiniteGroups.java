/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra;

import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.abstractAlgebra.group.finite.AbstractFiniteGroup;
import cn.timelives.java.math.abstractAlgebra.structure.finite.FiniteGroup;
import cn.timelives.java.math.function.MathBinaryOperator;
import cn.timelives.java.math.set.FiniteSet;
import cn.timelives.java.math.set.MathSets;
import cn.timelives.java.utilities.ArraySup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * A utility class for limited groups.
 * @author liyicheng
 * 2018-02-27 17:52
 *
 */
public final class FiniteGroups {

	/**
	 * 
	 */
	private FiniteGroups() {
	}
	
	static class FiniteGroupImpl<T> extends AbstractFiniteGroup<T>{
		
		private final FiniteSet<T> set;
		
		/**
		 * @param gc
		 */
		public FiniteGroupImpl(GroupCalculator<T> gc,FiniteSet<T> set) {
			super(gc);
			this.set = set;
		}

		
		


		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.LimitedGroup#getSet()
		 */
		@Override
		public FiniteSet<T> getSet() {
			return set;
		}





		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.finite.FiniteGroup#getSubgroups()
		 */
		@Override
		public FiniteSet<AbstractFiniteGroup<T>> getSubgroups() {
			// TODO Auto-generated method stub
			return null;
		}




		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#index()
		 */
		@Override
		public long index() {
			return set.size();
		}




		/*
		 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#isSubgroup(cn.timelives.java.math.abstractAlgebra.structure.Group)
		 */
		@Override
		public boolean isSubgroup(AbstractFiniteGroup<T> g) {
			// TODO Auto-generated method stub
			return false;
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
	public static <T> T[][] generateGroupTable(FiniteGroup<T,?> g) {
		FiniteSet<T> set = g.getSet();
		int size = ArraySup.castToArrayLength(set.size() + 1);
		MathBinaryOperator<T> f = g.getCalculator();
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
	public static <T> AbstractFiniteGroup<T> createGroup(GroupCalculator<T> f,T...elements){
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
		FiniteSet<T> set = MathSets.fromCollection(list,GroupCalculators.toMathCalculatorAdd(f));
		return new FiniteGroupImpl<>(f, set);
	}
}
