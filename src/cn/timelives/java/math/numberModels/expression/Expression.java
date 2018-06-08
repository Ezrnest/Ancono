/**
 * 2017-11-23
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.utilities.Printer;

import java.io.PrintWriter;
import java.util.Objects;

/**
 * Expression is the most universal number model to show a number.
 * 
 * @author liyicheng 2017-11-23 21:31
 *
 */
public final class Expression {

	/**
	 * The root node of the expression.
	 */
	final Node root;

	/**
	 * 
	 */
	Expression(Node root) {
		this.root = Objects.requireNonNull(root);
	}

	private String expr;

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (expr == null) {
			expr = root.toString();
		}
		return expr;
	}

	public void listNode(PrintWriter out) {
		PrintWriter pw = Printer.getOutput();
		Printer.reSet(out);
		root.listNode(0);
		Printer.reSet(pw);
	}

	public void listNode() {
		root.listNode(0);
	}

	public Node getRoot() {
		return root;
	}

	/**
	 * Creates an expression from a multinomial.
	 * @param p
	 * @return
	 */
	public static Expression fromMultinomial(Multinomial p) {
		return new Expression(Node.newPolyNode(p, null));
	}

	

}
