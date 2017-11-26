/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.EnumSet;

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
	public EnumSet<Node.Type> registerType();
	
	
	/**
	 * Returns the name of the function that this simplification strategy aims at.
	 * @return
	 */
	public String registerFunctionName();
}
