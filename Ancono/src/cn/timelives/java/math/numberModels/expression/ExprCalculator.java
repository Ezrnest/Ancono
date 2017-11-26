/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.Simplifier;
import cn.timelives.java.math.numberModels.expression.Node.Add;
import cn.timelives.java.math.numberModels.expression.Node.DFunction;
import cn.timelives.java.math.numberModels.expression.Node.Fraction;
import cn.timelives.java.math.numberModels.expression.Node.MFunction;
import cn.timelives.java.math.numberModels.expression.Node.Multiply;
import cn.timelives.java.math.numberModels.expression.Node.Poly;
import cn.timelives.java.math.numberModels.expression.Node.SFunction;
import cn.timelives.java.math.numberModels.expression.Node.Type;
import static cn.timelives.java.math.numberModels.expression.Node.*;
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
	
	private final ExprFunctionHolder fs;
	
	//some constants here
	final Polynomial pOne,pZero,pMinusOne;
	
	final Expression zero,one;
	
	private static final PolyCalculator DEFAULT_CALCULATOR = PolyCalculator.DEFAULT_CALCULATOR;
	
	private static final ExprFunctionHolder DEFAULT_FUNCTIONS = ExprFunctionHolder.getDefaultKit(PolyCalculator.DEFAULT_CALCULATOR);
	
	/**
	 * 
	 */
	public ExprCalculator(PolyCalculator pc,Comparator<Node> nc,Simplifier<Polynomial> ps,
			ExprFunctionHolder holder) {
		this.pc = pc;
		this.nc = nc;
		this.ps = ps;
		this.fs = holder;
		pOne = pc.getOne();
		pZero = pc.getZero();
		pMinusOne = pc.negate(pOne);
		zero = Expression.fromPolynomial(pZero);
		one = Expression.fromPolynomial(pOne);
	}
	
	/**
	 * 
	 */
	public ExprCalculator() {
		this(DEFAULT_CALCULATOR,NodeComparator.DEFAULT,PolyCalculator.getSimplifier(),DEFAULT_FUNCTIONS);
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
		//special case for both polynomial:
		if(isPolynomial(para1.root) && isPolynomial(para2.root)) {
			Polynomial p1 = toPolynomial(para1.root).p;
			Polynomial p2 = toPolynomial(para2.root).p;
			return new Expression(Node.newPolyNode(pc.add(p1, p2), null));
		}
		List<Node> list = new ArrayList<>(2);
		Add nroot = new Add(null, null, list);
		Node rt1 = para1.root.cloneNode(nroot);
		Node rt2 = para2.root.cloneNode(nroot);
		list.add(rt1);
		list.add(rt2);
		Node root = simplify(nroot);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#negate(java.lang.Object)
	 */
	@Override
	public Expression negate(Expression para) {
		if(isPolynomial(para.root)) {
			return new Expression(Node.newPolyNode(pc.negate(toPolynomial(para.root).p), null));
		}
		Node nroot = Node.wrapCloneNodeMultiply(para.root, pMinusOne);
		nroot = simplify(nroot);
		return new Expression(nroot);
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#abs(java.lang.Object)
	 */
	@Override
	public Expression abs(Expression para) {
		Node rt = Node.wrapCloneNodeSF("abs", para.root);
		rt = simplify(rt);
		return new Expression(rt);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#subtract(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression subtract(Expression para1, Expression para2) {
		if(isPolynomial(para1.root) && isPolynomial(para2.root)) {
			Polynomial p1 = toPolynomial(para1.root).p;
			Polynomial p2 = toPolynomial(para2.root).p;
			return new Expression(Node.newPolyNode(pc.subtract(p1, p2), null));
		}
		//para1 + (-1)*para2
		Node p1 = para1.root.cloneNode(null);
		Node p2 = Node.wrapCloneNodeMultiply(para2.root, pMinusOne);
		Node root = Node.wrapNodeAM(true, p1, p2);
		root = simplify(root);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#getZero()
	 */
	@Override
	public Expression getZero() {
		return zero;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#multiply(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression multiply(Expression para1, Expression para2) {
		//special case for both polynomial:
		if (isPolynomial(para1.root) && isPolynomial(para2.root)) {
			Polynomial p1 = toPolynomial(para1.root).p;
			Polynomial p2 = toPolynomial(para2.root).p;
			return new Expression(Node.newPolyNode(pc.multiply(p1, p2), null));
		}
		Node root = Node.wrapCloneNodeAM(false, para1.root,para2.root);
		root = simplify(root);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#divide(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression divide(Expression para1, Expression para2) {
		Node root = Node.wrapCloneNodeFraction(para1.root, para2.root);
		root = simplify(root);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#getOne()
	 */
	@Override
	public Expression getOne() {
		return one;
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#reciprocal(java.lang.Object)
	 */
	@Override
	public Expression reciprocal(Expression p) {
		Fraction root = Node.wrapNodeFraction(Node.newPolyNode(pOne, null), p.root.cloneNode(null));
		Node r = simplify(root);
		return new Expression(r);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#multiplyLong(java.lang.Object, long)
	 */
	@Override
	public Expression multiplyLong(Expression p, long l) {
		//special case for both polynomial:
		if (isPolynomial(p.root)) {
			Polynomial p1 = toPolynomial(p.root).p;
			return new Expression(Node.newPolyNode(pc.multiplyLong(p1, l), null));
		}
		Node root = wrapCloneNodeMultiply(p.root,pc.valueOfLong(l));
		root = simplify(root);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#divideLong(java.lang.Object, long)
	 */
	@Override
	public Expression divideLong(Expression p, long l) {
		if (isPolynomial(p.root)) {
			Polynomial p1 = toPolynomial(p.root).p;
			return new Expression(Node.newPolyNode(pc.divideLong(p1, l), null));
		}
		Node root = wrapCloneNodeMultiply(p.root,pc.valueOfRecipLong(l));
		root = simplify(root);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#squareRoot(java.lang.Object)
	 */
	@Override
	public Expression squareRoot(Expression p) {
		return sfunction("sqr",p);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#nroot(java.lang.Object, long)
	 */
	@Override
	public Expression nroot(Expression x, long n) {
		Node root = wrapNodeDF("exp", x.root.cloneNode(null), newPolyNode(pc.valueOfRecipLong(n),null));
		root = simplify(root);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#pow(java.lang.Object, long)
	 */
	@Override
	public Expression pow(Expression p, long exp) {
		Node root = wrapNodeDF("exp", p.root.cloneNode(null), newPolyNode(pc.valueOfLong(exp),null));
		root = simplify(root);
		return new Expression(root);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#constantValue(java.lang.String)
	 */
	@Override
	public Expression constantValue(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Expression sfunction(String name,Expression p) {
		Node root = wrapCloneNodeSF(name, p.root);
		root = simplify(root);
		return new Expression(root);
	}
	
	private Expression dFunction(String name,Expression p1,Expression p2) {
		Node root = wrapCloneNodeDF(name, p1.root, p2.root);
		root = simplify(root);
		return new Expression(root);
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#exp(java.lang.Object)
	 */
	@Override
	public Expression exp(Expression x) {
		return sfunction("exp",x);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#ln(java.lang.Object)
	 */
	@Override
	public Expression ln(Expression x) {
		return sfunction("ln",x);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#sin(java.lang.Object)
	 */
	@Override
	public Expression sin(Expression x) {
		return sfunction("sin",x);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#arcsin(java.lang.Object)
	 */
	@Override
	public Expression arcsin(Expression x) {
		return sfunction("arcsin",x);
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#arccos(java.lang.Object)
	 */
	@Override
	public Expression arccos(Expression x) {
		return sfunction("arccos",x);
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#arctan(java.lang.Object)
	 */
	@Override
	public Expression arctan(Expression x) {
		return sfunction("arctan",x);
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#cos(java.lang.Object)
	 */
	@Override
	public Expression cos(Expression x) {
		return sfunction("cos",x);
	}
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#exp(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression exp(Expression a, Expression b) {
		return dFunction("exp",a,b);
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#log(java.lang.Object, java.lang.Object)
	 */
	@Override
	public Expression log(Expression a, Expression b) {
		return dFunction("log",a,b);
	}
	
	/*
	 * @see cn.timelives.java.math.numberModels.MathCalculator#tan(java.lang.Object)
	 */
	@Override
	public Expression tan(Expression x) {
		return sfunction("tan",x);
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
		case POLYNOMIAL: {
			return node;
		}
		case ADD: {
			node = polySimplifyAdd((Add)node);
			break;
		}
		case FRACTION: {
			node = polySimplifyFraction((Fraction)node);
			break;
		}
		case MULTIPLY: {
			node = polySimplifyMultiply((Multiply) node);
			break;
		}

		case S_FUNCTION: {
			node = polySimplifySFunction((SFunction)node);
			break;
		}
		case D_FUNCTION: {
			node = polySimplifyDFunction((DFunction)node);
			break;
		}
		case M_FUNCTION: {
			node = polySimplifyMFunction((MFunction)node);
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
			if(nt.getType() == Type.POLYNOMIAL) {
				// add this one
				Poly pn = (Poly) nt;
				p = pc.add(p, pn.p);
				lit.remove();
			}else if(nt != t) {
				lit.set(nt);
			}
		}
		
		if (node.children.isEmpty()) {
			Poly nn = Node.newPolyNode(p, node.parent);
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
			if(nt.getType() == Type.POLYNOMIAL) {
				// add this one
				Poly pn = (Poly) nt;
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
			Poly nn = Node.newPolyNode(p, node.parent);
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
		if(nume.getType() == Type.POLYNOMIAL) {
			Poly pnume = (Poly) nume;
			if(pc.isZero(pnume.p)) {
				return Node.newPolyNode(pc.getZero(), node.parent);
			}
			if(deno.getType() == Type.POLYNOMIAL) {
				Poly pdeno = (Poly) deno;
				try {
					Polynomial quotient = pc.divide(pnume.p, pdeno.p);
					return Node.newPolyNode(quotient, node.parent);
				}catch(UnsupportedCalculationException ex) {
					//cannot compute
				}
				List<Polynomial> list = ps.simplify(Arrays.asList(pnume.p,pdeno.p));
				nume = Node.newPolyNode(list.get(0), node);
				deno = Node.newPolyNode(list.get(1), node);
			}
		}
		node.c1 = nume;
		node.c2 = deno;
		return node;
	}
	
	Node polySimplifySFunction(SFunction node) {
		Node c = simplifyPolynomial(node.child);
		if(c.getType() == Type.POLYNOMIAL) {
			Poly p = (Poly)c;
			Polynomial result = fs.computeSingle(node.functionName, p.p);
			if(result != null) {
				return Node.newPolyNode(result, node.parent);
			}
		}
		node.child = c;
		return node;
	}
	
	Node polySimplifyDFunction(DFunction node) {
		Node c1 = simplifyPolynomial(node.c1);
		Node c2 = simplifyPolynomial(node.c2);
		if(c1.getType() == Type.POLYNOMIAL && c2.getType() == Type.POLYNOMIAL) {
			Poly p1 = (Poly)c1;
			Poly p2 = (Poly)c2;
			Polynomial result = fs.computeDouble(node.functionName, p1.p, p2.p);
			if(result != null) {
				return Node.newPolyNode(result, node.parent);
			}
		}
		node.c1 = c1;
		node.c2 = c2;
		return node;
	}
	
	Node polySimplifyMFunction(MFunction node) {
		boolean allPoly = true;
		List<Node> children = node.children;
		for(ListIterator<Node> lit = node.children.listIterator();lit.hasNext();) {
			Node t = lit.next();
			Node nt = simplifyPolynomial(t);
			if(nt.getType() != Type.POLYNOMIAL) {
				allPoly = false;
			}
			if(nt != t) {
				lit.set(nt);
			}
		}
		if(allPoly) {
			Polynomial[] ps = new Polynomial[children.size()];
			int i=0;
			for(Node n : children) {
				ps[i++] = ((Poly)n).p;
			}
			Polynomial result = fs.computeMultiple(node.functionName, ps);
			if(result != null) {
				return Node.newPolyNode(result, node.parent);
			}
		}
		return node;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
