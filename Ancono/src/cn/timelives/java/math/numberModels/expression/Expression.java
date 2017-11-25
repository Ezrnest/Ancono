/**
 * 2017-11-23
 */
package cn.timelives.java.math.numberModels.expression;

import static cn.timelives.java.utilities.Printer.print;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import cn.timelives.java.math.addableSet.AdditiveSet;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.expression.Node.Add;
import cn.timelives.java.math.numberModels.expression.Node.Multiply;
import cn.timelives.java.math.numberModels.expression.Node.PolyNode;
import cn.timelives.java.utilities.Printer;

/**
 * Expression is the most universal number model to show a number.
 * @author liyicheng
 * 2017-11-23 21:31
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
		if(expr == null) {
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
	
	public Node getRoot() {
		return root;
	}
	
	
	public static void main(String[] args) {
		
		ArrayList<Node> list = new ArrayList<>();
		PolyNode[] ps = new PolyNode[3];
		ps[0] = new PolyNode(null, Polynomial.ONE);
		ps[1] = new PolyNode(null, Polynomial.ZERO);
		ps[2] = new PolyNode(null, Polynomial.NEGATIVE_ONE);
		Multiply m = new Multiply(null, Polynomial.NEGATIVE_ONE, list);
		Add add = new Add(m,Polynomial.ZERO,Collections.singletonList(ps[0]));
		list.add(add);
		list.add(ps[2]);
		Expression expr = new Expression(m);
		print(expr);
		ExprCalculator exc = new ExprCalculator();
		expr.listNode(Printer.getOutput());
		print(exc.simplify(expr));
	}
	
}
