/**
 * 2018-02-27
 */
package cn.timelives.java.math.abstractAlgebra.structure;

import cn.timelives.java.math.set.LimitedSet;

/**
 * A limited group is a group with limited elements.
 * @author liyicheng
 * 2018-02-27 17:50
 *
 */
public interface LimitedGroup<T> extends Group<T> {
	
	@Override
	LimitedSet<T> getSet();
	
	
}
