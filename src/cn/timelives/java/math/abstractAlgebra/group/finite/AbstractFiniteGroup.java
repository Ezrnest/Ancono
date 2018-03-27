/**
 * 2018-03-01
 */
package cn.timelives.java.math.abstractAlgebra.group.finite;

import cn.timelives.java.math.abstractAlgebra.GroupCalculators;
import cn.timelives.java.math.abstractAlgebra.calculator.GroupCalculator;
import cn.timelives.java.math.abstractAlgebra.structure.finite.FiniteGroup;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.set.FiniteSet;
import cn.timelives.java.math.set.MathSet;
import cn.timelives.java.math.set.MathSets;
import cn.timelives.java.utilities.CollectionSup;

import java.util.ArrayList;
import java.util.List;

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
			mc = GroupCalculators.toMathCalculatorAdd(gc);
		}
		return mc;
	}
	
	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#getCalculator()
	 */
	@Override
	public GroupCalculator<T> getCalculator() {
		return gc;
	}
	
	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Monoid#identity()
	 */
	@Override
	public T identity() {
		return gc.getIdentity();
	}
	
	
	

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#isSubgroup(cn.timelives.java.math.abstractAlgebra.structure.Group)
	 */
	@Override
	public boolean isSubgroup(AbstractFiniteGroup<T> g) {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#getNormalSubgroups()
	 */
	@Override
	public MathSet<AbstractFiniteGroup<T>> getNormalSubgroups() {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#isNormalSubgroups(cn.timelives.java.math.abstractAlgebra.structure.Group)
	 */
	@Override
	public boolean isNormalSubgroups(AbstractFiniteGroup<T> g) {
		throw new UnsupportedOperationException();
	}

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#getCoset(java.lang.Object)
	 */
	@Override
	public FiniteCoset<T> getCoset(T x,boolean isLeft) {
		return getCoset(x,this,isLeft);
	}
	
	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#getCoset(java.lang.Object, cn.timelives.java.math.abstractAlgebra.structure.Group)
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
		//TODO
		return null;
	}
	
	/**
	 * Returns {@code regularRepresent(true)}
	 * @return
	 */
	public PermutationGroup regularRepresent() {
		return regularRepresent(true);
	}
	
	

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.finite.FiniteGroup#getSubgroups()
	 */
	@Override
	public FiniteSet<AbstractFiniteGroup<T>> getSubgroups() {
		throw new UnsupportedOperationException();
	}
	
	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Group#index()
	 */
	@Override
	public long index() {
		return getSet().size();
	}
	
	
	

}
