/**
 * 2018-03-01
 */
package cn.timelives.java.math.algebra.abstractAlgebra.group.finite;

import cn.timelives.java.math.algebra.abstractAlgebra.FiniteGroups;
import cn.timelives.java.math.algebra.abstractAlgebra.GroupCalculators;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup;
import cn.timelives.java.math.numberTheory.combination.Permutation;
import cn.timelives.java.math.numberTheory.combination.Permutations;
import cn.timelives.java.math.set.FiniteSet;
import cn.timelives.java.math.set.MathSet;
import cn.timelives.java.math.set.MathSets;
import cn.timelives.java.utilities.ArraySup;
import cn.timelives.java.utilities.CollectionSup;

import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import static cn.timelives.java.utilities.Printer.print;

/**
 * @author liyicheng
 * 2018-03-01 19:13
 *
 */
public abstract class AbstractFiniteGroup<T> implements FiniteGroup<T, AbstractFiniteGroup<T>> {
	protected final GroupCalculator<T> gc;
	private MathCalculator<T> mc;
	
	/**
	 * 
	 */
	public AbstractFiniteGroup(GroupCalculator<T> gc) {
		this.gc = gc;
	}
	
	protected MathCalculator<T> getWrappedCalculator(){
		if(mc==null) {
            mc = GroupCalculators.INSTANCE.toMathCalculatorAdd(gc);
		}
		return mc;
	}
	
	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#getCalculator()
	 */
	@Override
	public GroupCalculator<T> getCalculator() {
		return gc;
	}
	
	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Monoid#identity()
	 */
	@Override
	public T identity() {
		return gc.getIdentity();
	}

	@Override
	public int[][] groupTable() {
		//mapping first
		var set = getSet();
		int size = Math.toIntExact(set.size());
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) CollectionSup.iteratorToArray(set.iterator(),size);
//		IntFunction<T> to = x -> arr[x];
		ToIntFunction<T> from = x -> ArraySup.firstIndexOf(arr,y -> gc.isEqual(x,y));
		int[][] result = new int[size][size];
		for(int i = 0;i<size;i++){
			for (int j = 0; j < size; j++) {
                T a = arr[i];
                T b = arr[j];
                T c = gc.apply(a,b);
				result[i][j] = from.applyAsInt(c);
				int t = result[i][j];
				if(t == -1){
//                    T c = gc.apply(a,b);
//                    print(a);
//                    print(b);
//                    print(c);
					throw new AssertionError();
				}
			}
		}
		return result;
	}

	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#isSubgroup(cn.timelives.java.math.algebra.abstractAlgebra.structure.Group)
	 */
	@Override
	public boolean isSubgroup(AbstractFiniteGroup<T> g) {
		if(g.gc != gc){
		    return false;
        }
	    return MathSets.containsAll(getSet(),g.getSet());
	}

	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#getNormalSubgroups()
	 */
	@Override
	public MathSet<AbstractFiniteGroup<T>> getNormalSubgroups() {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#isNormalSubgroups(cn.timelives.java.math.algebra.abstractAlgebra.structure.Group)
	 */
	@Override
	public boolean isNormalSubgroups(AbstractFiniteGroup<T> g) {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#getCoset(java.lang.Object)
	 */
	@Override
	public FiniteCoset<T> getCoset(T x,boolean isLeft) {
		return getCoset(x,this,isLeft);
	}
	
	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#getCoset(java.lang.Object, cn.timelives.java.math.algebra.abstractAlgebra.structure.Group)
	 */
	@Override
	public FiniteCoset<T> getCoset(T x, AbstractFiniteGroup<T> subGroup,boolean isLeft) {
		FiniteSet<T> set = getSet();
		List<T> list = new ArrayList<>();
		for(T t : set) {
			T y = isLeft? gc.apply(x, t) : gc.apply(t, x);
			if(!CollectionSup.contains(list, z-> gc.isEqual(z, y))) {
				list.add(y);
			}
		}
		FiniteSet<T> coset = MathSets.fromCollection(list, getWrappedCalculator());
		return new FiniteCoset<>(coset, this, subGroup);
	}
	
	/**
	 * Returns a regular representation of this finite group.
	 * @return
	 */
	public PermutationGroup regularRepresent(boolean isRight) {
	    int size = Math.toIntExact(index());
		List<Permutation> ps = new ArrayList<>(size);
		@SuppressWarnings("unchecked")
		T[] eleArr = (T[]) new Object[size];
		var eleSet = getSet();
		int i=0;
		for(T a : eleSet){
		    eleArr[i++] = a;
        }

        for(i=0;i<size;i++){
		    T a = eleArr[i];
		    int[] permutation = new int[size];
		    for(int j = 0;j<size;j++){
		        T b = eleArr[j];
                T re ;
                if(isRight){
                	//a * a_i
                    re = gc.apply(b,a);
                }else{
                	//a_i^-1 * a
                    re = gc.apply(gc.inverse(a),b);
                }
                int index = ArraySup.firstIndexOf(eleArr,x -> gc.isEqual(x,re));
                permutation[j] = index;
            }
            ps.add(Permutations.valueOf(permutation));
        }

		return PermutationGroup.groupOfChecked(MathSets.fromCollection(ps,Permutations.getMathCalculator()));
	}
	


	

	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.finite.FiniteGroup#getSubgroups()
	 */
	@Override
	public FiniteSet<AbstractFiniteGroup<T>> getSubgroups() {
        return MathSets.asSet(EqualPredicate.Companion.naturalEqual(), this, FiniteGroups.identityGroup(gc));
	}
	
	/*
	 * @see cn.timelives.java.math.algebra.abstractAlgebra.structure.Group#index()
	 */
	@Override
	public long index() {
		return getSet().size();
	}
	
	
	

}
