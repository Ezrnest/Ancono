/**
 * 2017-11-25
 */
package cn.timelives.java.math.numberModels.expression;

/**
 * A simplification strategy
 * @author liyicheng
 * 2017-11-25 18:19
 *
 */
public interface SimplificationStrategy {
	
	/**
	 * Simplify the node, always return a non-null value as the substitution 
	 * for the original node, and returning the original one is also acceptable(which means 
	 * the node is not simplified so well that it should be replaced). 
	 * @param node
	 * @return
	 */
	public Node simplifyNode(Node node,ExprCalculator mc);
	
}
