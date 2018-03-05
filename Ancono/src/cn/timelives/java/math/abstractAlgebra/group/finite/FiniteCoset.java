/**
 * 2018-03-05
 */
package cn.timelives.java.math.abstractAlgebra.group.finite;

import cn.timelives.java.math.abstractAlgebra.group.AbstractCoset;
import cn.timelives.java.math.set.FiniteSet;
/**
 * @author liyicheng
 * 2018-03-05 19:59
 *
 */
public class FiniteCoset<T> extends AbstractCoset<T, AbstractFiniteGroup<T>> {
	protected final FiniteSet<T> set;
	
	/**
	 * 
	 */
	public FiniteCoset(FiniteSet<T> set,AbstractFiniteGroup<T> g,AbstractFiniteGroup<T> sub) {
		super(g,sub);
		this.set = set; 
	}

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Coset#index()
	 */
	@Override
	public long index() {
		return set.size();
	}

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Coset#getRepresentatives()
	 */
	@Override
	public FiniteSet<T> getRepresentatives() {
		return set;
	}

	/*
	 * @see cn.timelives.java.math.set.MathSet#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T t) {
		return set.contains(t);
	}
}
