/**
 * 2017-11-26
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.EnumSet;
import java.util.Set;

import cn.timelives.java.math.numberModels.expression.Node.Add;
import cn.timelives.java.math.numberModels.expression.Node.DFunction;
import cn.timelives.java.math.numberModels.expression.Node.Fraction;
import cn.timelives.java.math.numberModels.expression.Node.MFunction;
import cn.timelives.java.math.numberModels.expression.Node.Multiply;
import cn.timelives.java.math.numberModels.expression.Node.SFunction;
import cn.timelives.java.math.numberModels.expression.Node.Type;

/**
 * @author liyicheng
 * 2017-11-26 13:34
 *
 */
public abstract class SimStraImpl implements SpecificStrategy,TaggedStrategy{
	protected final Set<String> tags;
	protected final Set<Node.Type> types;
	protected final String fname;
	/**
	 * 
	 */
	public SimStraImpl(Set<String> tags,Set<Node.Type> types,String fname) {
		this.tags = tags;
		this.types = types;
		this.fname = fname;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.expression.TaggedStrategy#isAcceptable(java.util.Set)
	 */
	@Override
	public boolean isAcceptable(Set<String> tags) {
		for(String s : tags) {
			if(this.tags.contains(s)) {
				return true;
			}
		}
		return false;
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.expression.SpecificStrategy#registerFunctionName()
	 */
	@Override
	public String registerFunctionName() {
		return fname;
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.expression.SpecificStrategy#registerType()
	 */
	@Override
	public Set<Type> registerTypes() {
		return types;
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.expression.TaggedStrategy#getTags()
	 */
	@Override
	public Set<String> getTags() {
		return tags;
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.expression.SimplificationStrategy#simplifyNode(cn.timelives.java.math.numberModels.expression.Node)
	 */
	@Override
	public Node simplifyNode(Node node,ExprCalculator mc) {
		Type t = node.getType();
		if(!types.contains(t)) {
			return node;
		}
		switch (t) {
		case ADD:
			return simplifyAdd((Add)node,mc);
		case MULTIPLY:
			return simplifyMultiply((Multiply)node,mc);
		case FRACTION:
			return simplifyFraction((Fraction)node,mc);
		case M_FUNCTION:
			return simplifyMFunction((MFunction)node,mc);
		case S_FUNCTION:
			return simplifySFunction((SFunction)node,mc);
		case D_FUNCTION:
			return simplifyDFunction((DFunction)node,mc);
		default:
			break;
		}
		return node;
	}
	/**
	 * Performs the simplification for a node Add. Ignore this method if this 
	 * simplifier doesn't register the type.
	 * @param node a node
	 * @return the node after simplification.
	 */
	protected Node simplifyAdd(Add node,ExprCalculator mc) {
		return node;
	}
	/**
	 * Performs the simplification for a node Multiply. Ignore this method if this 
	 * simplifier doesn't register the type.
	 * @param node a node
	 * @return the node after simplification.
	 */
	protected Node simplifyMultiply(Multiply node,ExprCalculator mc) {
		return node;
	}
	/**
	 * Performs the simplification for a node Fraction. Ignore this method if this 
	 * simplifier doesn't register the type.
	 * @param node a node
	 * @return the node after simplification.
	 */
	protected Node simplifyFraction(Fraction node,ExprCalculator mc) {
		return node;
	}
	/**
	 * Performs the simplification for a node SFunction. Ignore this method if this 
	 * simplifier doesn't register the type.
	 * @param node a node
	 * @return the node after simplification.
	 */
	protected Node simplifySFunction(SFunction node,ExprCalculator mc) {
		return node;
	}
	/**
	 * Performs the simplification for a node DFunction. Ignore this method if this 
	 * simplifier doesn't register the type.
	 * @param node a node
	 * @return the node after simplification.
	 */
	protected Node simplifyDFunction(DFunction node,ExprCalculator mc) {
		return node;
	}
	/**
	 * Performs the simplification for a node MFunction. Ignore this method if this 
	 * simplifier doesn't register the type.
	 * @param node a node
	 * @return the node after simplification.
	 */
	protected Node simplifyMFunction(MFunction node,ExprCalculator mc) {
		return node;
	}
}
