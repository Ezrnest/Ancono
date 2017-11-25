/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.Simplifier;
import cn.timelives.java.math.numberModels.expression.Node.Add;
import cn.timelives.java.math.numberModels.expression.Node.Fraction;
import cn.timelives.java.math.numberModels.expression.Node.Multiply;
import cn.timelives.java.math.numberModels.expression.Node.PolyNode;
import cn.timelives.java.math.numberModels.expression.Node.Type;

/**
 * @author liyicheng
 * 2017-11-24 18:27
 *
 */
public class ExprCalculator implements MathCalculator<Expression> {
	/**
	 * The polynomial calculator of this calculator
	 */
	private final PolyCalculator pc;
	private final Simplifier<Polynomial> ps;
	private final Comparator<Node> nc;
	/**
	 * 
	 */
	public ExprCalculator(PolyCalculator pc,Comparator<Node> nc,Simplifier<Polynomial> ps) {
		this.pc = pc;
		this.nc = nc;
		this.ps = ps;
	}
	
	/**
	 * 
	 */
	public ExprCalculator() {
		pc = PolyCalculator.DEFAULT_CALCULATOR;
		nc = NodeComparator.DEFAULT;
		ps = PolyCalculator.getSimplifier();
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#isEqual(java.lang.Object, java.lang.Object)
	 */
	@Override
	public boolean isEqual(Expression para1, Expression para2) {
		return para1.root.equalNode(para2.root,pc);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Expression para1, Expression para2) {
		return nc.compare(para1.root, para2.root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#add(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression add(Expression para1, Expression para2) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#negate(java.lang.Object)
	 */
	@Override
	public Expression negate(Expression para) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#abs(java.lang.Object)
	 */
	@Override
	public Expression abs(Expression para) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#subtract(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression subtract(Expression para1, Expression para2) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#getZero()
	 */
	@Override
	public Expression getZero() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#multiply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression multiply(Expression para1, Expression para2) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#divide(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression divide(Expression para1, Expression para2) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#getOne()
	 */
	@Override
	public Expression getOne() {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#reciprocal(java.lang.Object)
	 */
	@Override
	public Expression reciprocal(Expression p) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#multiplyLong(java.lang.Object, long)
	 */
	@Override
	public Expression multiplyLong(Expression p, long l) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#divideLong(java.lang.Object, long)
	 */
	@Override
	public Expression divideLong(Expression p, long l) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#squareRoot(java.lang.Object)
	 */
	@Override
	public Expression squareRoot(Expression p) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#nroot(java.lang.Object, long)
	 */
	@Override
	public Expression nroot(Expression x, long n) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#pow(java.lang.Object, long)
	 */
	@Override
	public Expression pow(Expression p, long exp) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#constantValue(java.lang.String)
	 */
	@Override
	public Expression constantValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#exp(java.lang.Object)
	 */
	@Override
	public Expression exp(Expression x) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#ln(java.lang.Object)
	 */
	@Override
	public Expression ln(Expression x) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#sin(java.lang.Object)
	 */
	@Override
	public Expression sin(Expression x) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#arcsin(java.lang.Object)
	 */
	@Override
	public Expression arcsin(Expression x) {
		// TODO Auto-generated method stub
		return null;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#getNumberClass()
	 */
	@Override
	public Class<?> getNumberClass() {
		return Expression.class;
	}
	
	public Expression simplify(Expression x) {
		Node root = x.root.cloneNode(null);
		root = simplify(root);
		return new Expression(root);
	}
	
	/**
	 * Performs simplify to the Expression
	 * @param expr
	 * @return
	 */
	Node simplify(Node root) {
		root =  simplifyPolynomial(root);
		return root;
	}
	
	
	
	/**
	 * Try to merge polynomials in the expression as well as possible. For example, 
	 * @param x
	 * @return
	 */
	Node simplifyPolynomial(Node node) {
		switch (node.getType()) {
		case ADD: {
			node = polySimplifyAdd((Add)node);
			break;
		}
		case D_FUNCTION: {
			break;
		}
		case FRACTION: {
			node = polySimplifyFraction((Fraction)node);
			
			break;
		}
		case MULTIPLY: {
			node = polySimplifyMultiply((Multiply)node);
			break;
		}
		case M_FUNCTION: {
			break;
		}
		case NUMBER: {
			return node;
		}
		case S_FUNCTION: {
			break;
		}
		default: {
			break;
		}

		}
		return node;
	}
	
	Node polySimplifyAdd(Add node) {
		Polynomial p = node.p;
		if(p == null) {
			p = pc.getZero();
		}
		List<Node> children = node.children;
		for(ListIterator<Node> lit = node.children.listIterator(children.size());lit.hasPrevious();) {
			Node t = lit.previous();
			Node nt = simplifyPolynomial(t);
			if(nt.getType() == Type.NUMBER) {
				// add this one
				PolyNode pn = (PolyNode) nt;
				p = pc.add(p, pn.p);
				lit.remove();
			}else if(nt != t) {
				lit.set(nt);
			}
		}
		
		if (node.children.isEmpty()) {
			PolyNode nn = Node.newPolyNode(p, node.parent);
			return nn;
		}
		if(pc.isZero(p)) {
			node.p = null;
		}
		return node;
	}
	
	Node polySimplifyMultiply(Multiply node) {
		Polynomial p = node.p;
		if(p == null) {
			p = pc.getOne();
		}
		List<Node> children = node.children;
		for(ListIterator<Node> lit = node.children.listIterator(children.size());lit.hasPrevious();) {
			Node t = lit.previous();
			Node nt = simplifyPolynomial(t);
			if(nt.getType() == Type.NUMBER) {
				// add this one
				PolyNode pn = (PolyNode) nt;
				p = pc.multiply(p, pn.p);
				if(pc.isZero(p)) {
					//*0
					break;
				}
				lit.remove();
			}else if(nt != t) {
				lit.set(nt);
			}
		}
		
		if (node.children.isEmpty() || pc.isZero(p)) {
			PolyNode nn = Node.newPolyNode(p, node.parent);
			return nn;
		}
		if(pc.isEqual(p,pc.getOne())) {
			node.p = null;
		}
		return node;
	}

	Node polySimplifyFraction(Fraction node) {
		Node nume = simplifyPolynomial(node.c1);
		Node deno = simplifyPolynomial(node.c2);
		if(nume.getType() == Type.NUMBER) {
			PolyNode pnume = (PolyNode) nume;
			if(pc.isZero(pnume.p)) {
				return Node.newPolyNode(pc.getZero(), node.parent);
			}
			if(deno.getType() == Type.NUMBER) {
				PolyNode pdeno = (PolyNode) deno;
				try {
					Polynomial quotient = pc.divide(pnume.p, pdeno.p);
					return Node.newPolyNode(quotient, node.parent);
				}catch(UnsupportedCalculationException ex) {
					//cannot compute
				}
				List<Polynomial> list = ps.simplify(Arrays.asList(pnume.p,pdeno.p));
				nume = Node.newPolyNode(list.get(0), node);
				deno = Node.newPolyNode(list.get(1), node);
				return node;
			}
		}
		return node;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
