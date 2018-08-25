/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression.simplification;

import cn.timelives.java.math.numberModels.expression.Node;
import cn.timelives.java.math.numberModels.expression.simplification.SimplificationStrategy;

import java.util.Set;

/**
 * @author liyicheng
 * 2017-11-25 18:21
 *
 */
public interface SpecificStrategy extends SimplificationStrategy {
	/**
	 * Gets the type(s) of node that this simplification strategy aims at.
	 * @return
	 */
	public Set<Node.Type> registerTypes();
	
	
	/**
	 * Returns the name of the function that this simplification strategy aims at. Returns 
	 * {@code null} if it doesn't focus on the function name.
	 * @return
	 */
	public String registerFunctionName();
}
