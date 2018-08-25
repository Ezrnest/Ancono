/**
 * 2017-11-28
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.MultinomialCalculator;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.Node;
import cn.timelives.java.math.numberModels.expression.Node.*;
import cn.timelives.java.math.numberModels.expression.simplification.SimStraImpl;
import cn.timelives.java.math.numberModels.expression.simplification.SimplificationStrategy;

import java.util.List;
import java.util.Set;

/**
 * Provides an abstract class to extend for users who want to customize {@link SimplificationStrategy}.
 * @author liyicheng
 * 2017-11-28 18:04
 *
 */
public abstract class SimpleStrategy extends SimStraImpl {

	/**
	 * @param tags
	 * @param types
	 * @param fname
	 */
	public SimpleStrategy(Set<String> tags, Set<Type> types, String fname) {
		super(tags, types, fname);
	}

	public SimpleStrategy(Set<String> tags, Set<Type> types, String fname, String description) {
		super(tags, types, fname, description);
	}
	
	/**
	 * Directly gets the children of the node, without getting a copy.
	 * @param n a ListChildNode
	 * @return the children
	 */
	protected final List<Node> getChildren(ListChildNode n){
		return n.children;
	}
	/**
	 * Sets the children for the node.
	 * @param n
	 * @param children
	 */
	protected final void setChildren(ListChildNode n, List<Node> children) {
		n.children = children;
	}
	/**
	 * Sets the children for the node.
	 * @param f
	 * @param c1
	 * @param c2
	 */
	protected final void setChildren(BiNode f, Node c1, Node c2) {
		f.c1 = c1;
		f.c2 = c2;
	}
	/**
	 * Sets the children for the node.
	 * @param n
	 * @param p
	 */
	protected final void setPolynomial(Node n, Multinomial p) {
		Node.setPolynomialPart(n, p);
	}
	/**
	 * Sets the children for the node.
	 * @param f
	 * @param c
	 */
	protected final void setChildren(SingleNode f, Node c) {
		f.child = c;
	}

	protected final void setParent(Node n, NodeWithChildren parent) {
		n.parent = parent;
	}
	
	/**
	 * Wraps the nodes to a new node either add or multiply. The parent 
	 * of the newly-created node is not set but the parent of the nodes in 
	 * the list will be set. 
	 * @param isAdd determines the type, {@code true} to indicate Add.
	 * @param nodes a list of nodes, must be modifiable but shouldn't be reused after this method. 
	 * @param p a polynomial, or null.
	 * @return a node, either of type Add or Multiply.
	 */
	protected final CombinedNode wrapNodeAM(boolean isAdd, List<Node> nodes, Multinomial p) {
		return Node.wrapNodeAM(isAdd, nodes, p);
	}
	/**
	 * Wraps the nodes to a new node as a single function. The parent 
	 * of the newly-created node is not set but the parent of the node given will be 
	 * set.
	 * @param fname the name of the function
	 * @param n the node to wrap in
	 * @return a new SFunction node
	 */
	protected final SFunction wrapNodeSF(String fname, Node n) {
		return Node.wrapNodeSF(fname, n);
	}
	/**
	 * Wraps the nodes to a fraction node. The parent 
	 * of the newly-created node is not set but the parent of the nodes given will be 
	 * set.
	 * @param nume the node as the numerator
	 * @param deno the node as the denominator
	 * @return a new Fraction node
	 */
	protected final Fraction wrapNodeFraction(Node nume, Node deno) {
		return Node.wrapNodeFraction(nume, deno);
	}
	/**
	 * Wraps the nodes to a double-argument-function node. The parent 
	 * of the newly-created node is not set but the parent of the nodes given will be 
	 * set.
	 * @param fname the name of the function
	 * @param n1 the first argument
	 * @param n2 the second argument
	 * @param sortable determines whether the order of the argument can be changed.
	 * @return a new DFunction node
	 */
	protected final DFunction wrapNodeDF(String fname, Node n1, Node n2, boolean sortable) {
		return Node.wrapNodeDF(fname, n1, n2);
	}
	/**
	 * Wraps the nodes to a multiple-argument-function node. The parent 
	 * of the newly-created node is not set but the parent of the nodes given will be 
	 * set.
	 * @param fname the name of the function
	 * @param nodes the arguments
	 * @param sortable determines whether the order of the argument can be changed.
	 * @return a new MFunction node
	 */
	protected final MFunction wrapNodeMF(String fname, List<Node> nodes, boolean sortable) {
		return Node.wrapNodeMF(fname, nodes, sortable);
	}
	
	protected final MultinomialCalculator getMultiCalculator(ExprCalculator mc) {
		return mc.getMultinomialCalculator();
	}


	protected final Node simplifyNode(Node n, int depth, ExprCalculator mc) {
		return mc.simplify(n,depth);
	}

	protected final Node simplifyPoly(Node n, int depth, ExprCalculator mc) {
		return mc.simplifyPolynomial(n, depth);
	}

	protected final void sortNode(Node n, int depth, ExprCalculator mc) {
		mc.doSort(n, depth);
	}

	protected final Node simplifyWithStrategy(Node n, int depth, ExprCalculator mc) {
		return mc.simplifyWithStrategy(n, depth);
	}

}
