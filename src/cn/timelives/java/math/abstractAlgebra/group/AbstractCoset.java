/**
 * 2018-03-05
 */
package cn.timelives.java.math.abstractAlgebra.group;

import cn.timelives.java.math.abstractAlgebra.structure.Coset;
import cn.timelives.java.math.abstractAlgebra.structure.Group;

/**
 * @author liyicheng
 * 2018-03-05 19:56
 *
 */
public abstract class AbstractCoset<T, G extends Group<T,G>> implements Coset<T, G> {
	protected final G g,sub;
	/**
	 * 
	 */
	public AbstractCoset(G g,G sub) {
		this.g = g;
		this.sub = sub;
	}

	

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Coset#getGroup()
	 */
	@Override
	public G getGroup() {
		return g;
	}

	/*
	 * @see cn.timelives.java.math.abstractAlgebra.structure.Coset#getSubGroup()
	 */
	@Override
	public G getSubGroup() {
		return sub;
	}


}
