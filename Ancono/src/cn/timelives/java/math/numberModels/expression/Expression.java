/**
 * 2017-11-23
 */
package cn.timelives.java.math.numberModels.expression;

import static cn.timelives.java.utilities.Printer.print;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.expression.Node.Type;
import cn.timelives.java.utilities.Printer;
import cn.timelives.java.utilities.Timer;

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

	public static Expression fromPolynomial(Polynomial p) {
		return new Expression(Node.newPolyNode(p, null));
	}

	

}
