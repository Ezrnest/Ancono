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
import cn.timelives.java.math.numberModels.expression.Node.*;
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
	
	public void listNode() {
		root.listNode(0);
	}
	
	public Node getRoot() {
		return root;
	}
	
	public static Expression fromPolynomial(Polynomial p) {
		return new Expression(Node.newPolyNode(p, null));
	}
	
	public static void main(String[] args) {
		
		ArrayList<Node> list = new ArrayList<>();
		Poly[] ps = new Poly[4];
		ps[0] = new Poly(null, Polynomial.ONE);
		ps[1] = new Poly(null, Polynomial.ZERO);
		ps[2] = new Poly(null, Polynomial.NEGATIVE_ONE);
		ps[3] = new Poly(null, Polynomial.valueOf("0"));
		Multiply m = new Multiply(null, Polynomial.NEGATIVE_ONE, list);
		Add add = new Add(m,Polynomial.ZERO,Collections.singletonList(ps[0]));
		Node n = new SFunction(add,ps[3],"exp");
		list.add(add);
		list.add(ps[2]);
		ps[2].parent = m;
		list.add(n);
		Expression expr = new Expression(m);
		print(expr);
		ExprCalculator exc = new ExprCalculator();
		expr.listNode(Printer.getOutput());
		print(exc.simplify(expr));
		expr = exc.subtract(expr, exc.getZero());
		expr.listNode();
		expr = exc.cos(expr);
		print(expr);
	}
	
}
