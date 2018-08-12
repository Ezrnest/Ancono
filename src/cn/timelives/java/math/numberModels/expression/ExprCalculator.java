/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.MultinomialCalculator;
import cn.timelives.java.math.numberModels.Term;
import cn.timelives.java.math.numberModels.api.Simplifier;
import cn.timelives.java.math.numberModels.expression.Node.*;

import java.util.*;

import static cn.timelives.java.math.numberModels.expression.Node.*;
import static cn.timelives.java.utilities.Printer.print;

/**
 * Expression Calculator deals with the calculation of the Expression. Unlike
 * most types of {@link MathCalculator} which have few things to configure,
 * expression calculator provides a wide variety of configurations and plug-ins
 * that enable the calculator to handle customized calculation.
 * <h3>Functions</h3> In addition to basic math operations like add, multiply
 * and so on, the Expression also allows functions. A function is identified
 * with it own name and the number of parameters. The expression calculator
 * allows users to set the functions that the calculator should recognize by
 * assigning an instance of {@link ExprFunctionHolder} when creating a
 * calculator. Then the expression calculator can handle the functions and
 * compute them. A more detailed instruction of expression function can be found
 * in {@link ExprFunction}.
 * <h3>Simplification</h3> Expressions can be mathematically equal but are of
 * different Expression, and one of the possible forms can be simpler than
 * others and is more efficient. Therefore, proper simplification is essential
 * for expression calculator. Generally, there are two types of simplification.
 * <P>
 * One of them is polynomial simplification, which is already defined in the
 * calculator. The calculator doing polynomial simplification will try to add,
 * subtract, multiply, divide and calculate the functions available as long as
 * the result can be expressed with polynomials. It is normally the basic and
 * default simplification strategy.
 * <p>
 * The other type is
 * 
 * 
 * 
 * 
 * 
 * <h3></h3> Each ExprCalculator has a level of simplification, which determines
 * how far the calculator should perform the simplification. Generally, a higher
 * level of simplification means that the calculator will try to simplify the
 * expression by using more high-leveled {@link SimplificationStrategy}, thus
 * making the simplification more thorough. However, it is not necessarily
 * better to set the level of simplification as high as possible, because a
 * higher level of simplification can also consume lots of time when the
 * expression cannot be simplified. Therefore, a suitable level should be set
 * according to the task. The following is some basic levels:
 * <ul>
 * <li>level = 0 : Multinomial
 * <p>
 * In this level the calculator will try to
 * <li>level = 100 : Merge
 * <p>
 * In this level the calculator will try
 * 
 * <ul>
 * 
 * @author liyicheng 2017-11-24 18:27
 *
 */
public class ExprCalculator implements MathCalculator<Expression> {
	/**
	 * The polynomial calculator of this calculator
	 */
	final MultinomialCalculator pc;
	final Simplifier<Multinomial> ps;
	final Comparator<Node> nc;
	final ExprFunctionHolder fs;
	final SimStraHolder ss;
	final Set<String> enabledTags;
	final Map<String,String> properties;
	// some constants here
	final Multinomial pOne, pZero, pMinusOne;

	final Expression zero, one;
	
	private int simplificationIdentifier;
	
	private static final MultinomialCalculator DEFAULT_CALCULATOR = Multinomial.getCalculator();

	private static final ExprFunctionHolder DEFAULT_FUNCTIONS = ExprFunctionHolder
			.getDefaultKit(Multinomial.getCalculator());


	/**
	 * 
	 */
	public ExprCalculator(MultinomialCalculator pc, Comparator<Node> nc, Simplifier<Multinomial> ps, ExprFunctionHolder holder,
                          SimStraHolder ss) {
		this.pc = pc;
		this.nc = nc;
		this.ps = ps;
		this.fs = holder;
		this.ss = ss;
		enabledTags = SimplificationStrategies.getDefaultTags();
		properties = new HashMap<>();
		pOne = pc.getOne();
		pZero = pc.getZero();
		pMinusOne = pc.negate(pOne);
		zero = Expression.fromMultinomial(pZero);
		one = Expression.fromMultinomial(pOne);
		updateSimplificationIdentifier();
	}

	/**
	 * 
	 */
	public ExprCalculator() {
		this(DEFAULT_CALCULATOR, NodeComparator.DEFAULT, Multinomial.getSimplifier(), DEFAULT_FUNCTIONS,
				SimStraHolder.getDefault());
	}
	
	private void updateSimplificationIdentifier() {
		int si = pc.hashCode();
		si = si*31 + ps.hashCode();
		si = si*31 + fs.hashCode();
		si = si*31 + ss.hashCode();
		si = si*31 + properties.hashCode();
		si = si*31 + enabledTags.hashCode();
		if(si == 0) {
			si = 1;
		}
		simplificationIdentifier = si;
	}
	/**
	 * Gets a property from this calculator.
	 * @param key
	 * @return
	 */
	public String getProperty(String key) {
		return properties.get(key);
	}
	/**
	 * Sets a property for this calculator.
	 * @param key
	 * @return
	 */
	public void setProperty(String key,String value) {
		properties.put(key, value);
		updateSimplificationIdentifier();
	}
	
	/**
	 * Gets the pc.
	 * 
	 * @return the pc
	 */
	public MultinomialCalculator getMultinomialCalculator() {
		return pc;
	}

	/**
	 * Gets the ps.
	 * 
	 * @return the ps
	 */
	public Simplifier<Multinomial> getPolynomialSimplifier() {
		return ps;
	}

	/**
	 * Gets the nc.
	 * 
	 * @return the nc
	 */
	public Comparator<Node> getNodeComparator() {
		return nc;
	}

	/**
	 * Gets the fs.
	 * 
	 * @return the fs
	 */
	public ExprFunctionHolder getFunctionHolder() {
		return fs;
	}

	/**
	 * Gets the ss.
	 * 
	 * @return the ss
	 */
	public SimStraHolder getSimStraHolder() {
		return ss;
	}

	/**
	 * Gets the enabledTags.
	 * 
	 * @return the enabledTags
	 */
	public Set<String> getEnabledTags() {
		return new HashSet<>(enabledTags);
	}

	@Override
	public boolean isComparable() {
		return false;
	}


	/**
	 * @param o
	 * @return
	 * @see Set#contains(Object)
	 */
	public boolean tagContains(Object o) {
		return enabledTags.contains(o);
	}

	/**
	 * @param e
	 * @return
	 * @see Set#add(Object)
	 */
	public boolean tagAdd(String e) {
		boolean b =  enabledTags.add(e);
		updateSimplificationIdentifier();
		return b;
	}

	/**
	 * @param o
	 * @return
	 * @see Set#remove(Object)
	 */
	public boolean tagRemove(Object o) {
		boolean b =  enabledTags.remove(o);
		updateSimplificationIdentifier();
		return b;
	}

	/**
	 * @param c
	 * @return
	 * @see Set#addAll(Collection)
	 */
	public boolean tagAddAll(Collection<? extends String> c) {
		boolean b = enabledTags.addAll(c);
		updateSimplificationIdentifier();
		return b;
	}

	/**
	 *
	 * @see Set#clear()
	 */
	public void tagClear() {
		enabledTags.clear();
		updateSimplificationIdentifier();
	}
	
	public void setTags(Set<String> set) {
		enabledTags.clear();
		enabledTags.addAll(set);
		updateSimplificationIdentifier();
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#isEqual(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public boolean isEqual(Expression para1, Expression para2) {
		return para1.root.equalNode(para2.root, pc);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#compare(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public int compare(Expression para1, Expression para2) {
		throw new UnsupportedCalculationException();
//		return nc.compare(para1.root, para2.root);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#add(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Expression add(Expression para1, Expression para2) {
		// special case for both polynomial:
		if (isPolynomial(para1.root) && isPolynomial(para2.root)) {
			Multinomial p1 = toPolynomial(para1.root).p;
			Multinomial p2 = toPolynomial(para2.root).p;
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
	 * @see
	 * cn.timelives.java.math.MathCalculator#negate(java.lang.Object)
	 */
	@Override
	public Expression negate(Expression para) {
		if (isPolynomial(para.root)) {
			return new Expression(Node.newPolyNode(pc.negate(toPolynomial(para.root).p), null));
		}
		Node nroot = Node.wrapCloneNodeMultiply(para.root, pMinusOne);
		nroot = simplify(nroot);
		return new Expression(nroot);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#abs(java.lang.Object)
	 */
	@Override
	public Expression abs(Expression para) {
		Node rt = Node.wrapCloneNodeSF("abs", para.root);
		rt = simplify(rt);
		return new Expression(rt);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#subtract(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Expression subtract(Expression para1, Expression para2) {
		if (isPolynomial(para1.root) && isPolynomial(para2.root)) {
			Multinomial p1 = toPolynomial(para1.root).p;
			Multinomial p2 = toPolynomial(para2.root).p;
			return new Expression(Node.newPolyNode(pc.subtract(p1, p2), null));
		}
		// para1 + (-1)*para2
		Node p1 = para1.root.cloneNode(null);
		Node p2 = Node.wrapCloneNodeMultiply(para2.root, pMinusOne);
		Node root = Node.wrapNodeAM(true, p1, p2);
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#getZero()
	 */
	@Override
	public Expression getZero() {
		return zero;
	}
	
	/*
	 * @see cn.timelives.java.math.MathCalculator#isZero(java.lang.Object)
	 */
	@Override
	public boolean isZero(Expression para) {
		return isEqual(zero, para);
	}
	
	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#multiply(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Expression multiply(Expression para1, Expression para2) {
		// special case for both polynomial:
		if (isPolynomial(para1.root) && isPolynomial(para2.root)) {
			Multinomial p1 = toPolynomial(para1.root).p;
			Multinomial p2 = toPolynomial(para2.root).p;
			return new Expression(Node.newPolyNode(pc.multiply(p1, p2), null));
		}
		Node root = Node.wrapCloneNodeAM(false, para1.root, para2.root);
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#divide(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Expression divide(Expression para1, Expression para2) {
		Node root = Node.wrapCloneNodeFraction(para1.root, para2.root);
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#getOne()
	 */
	@Override
	public Expression getOne() {
		return one;
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#reciprocal(java.lang.
	 * Object)
	 */
	@Override
	public Expression reciprocal(Expression p) {
		Fraction root = Node.wrapNodeFraction(Node.newPolyNode(pOne, null), p.root.cloneNode(null));
		Node r = simplify(root);
		return new Expression(r);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#multiplyLong(java.lang.
	 * Object, long)
	 */
	@Override
	public Expression multiplyLong(Expression p, long l) {
		// special case for both polynomial:
		if (isPolynomial(p.root)) {
			Multinomial p1 = toPolynomial(p.root).p;
			return new Expression(Node.newPolyNode(pc.multiplyLong(p1, l), null));
		}
		Node root = wrapCloneNodeMultiply(p.root, Multinomial.valueOf(l));
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#divideLong(java.lang.
	 * Object, long)
	 */
	@Override
	public Expression divideLong(Expression p, long l) {
		if (isPolynomial(p.root)) {
			Multinomial p1 = toPolynomial(p.root).p;
			return new Expression(Node.newPolyNode(pc.divideLong(p1, l), null));
		}
		Node root = wrapCloneNodeMultiply(p.root, Multinomial.monomial(Term.valueOfRecip(l)));
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#squareRoot(java.lang.
	 * Object)
	 */
	@Override
	public Expression squareRoot(Expression p) {
		return sfunction("sqr", p);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#nroot(java.lang.Object,
	 * long)
	 */
	@Override
	public Expression nroot(Expression x, long n) {
		Node root = wrapNodeDF("exp", x.root.cloneNode(null), newPolyNode(Multinomial.monomial(Term.valueOfRecip(n)), null));
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#pow(java.lang.Object,
	 * long)
	 */
	@Override
	public Expression pow(Expression p, long exp) {
		Node root = wrapNodeDF("exp", p.root.cloneNode(null), newPolyNode(Multinomial.valueOf(exp), null));
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#constantValue(java.lang.
	 * String)
	 */
	@Override
	public Expression constantValue(String name) {
		try {
			Multinomial p = pc.constantValue(name);
			return Expression.fromMultinomial(p);
		}catch(UnsupportedCalculationException uce) {
			throw uce;
		}
//		return null;
	}

	private Expression sfunction(String name, Expression p) {
		Node root = wrapCloneNodeSF(name, p.root);
		root = simplify(root);
		return new Expression(root);
	}

	private Expression dFunction(String name, Expression p1, Expression p2) {
		Node root = wrapCloneNodeDF(name, p1.root, p2.root);
		root = simplify(root);
		return new Expression(root);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#exp(java.lang.Object)
	 */
	@Override
	public Expression exp(Expression x) {
		return sfunction("exp", x);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#ln(java.lang.Object)
	 */
	@Override
	public Expression ln(Expression x) {
		return sfunction("ln", x);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#sin(java.lang.Object)
	 */
	@Override
	public Expression sin(Expression x) {
		return sfunction("sin", x);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#arcsin(java.lang.Object)
	 */
	@Override
	public Expression arcsin(Expression x) {
		return sfunction("arcsin", x);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#arccos(java.lang.Object)
	 */
	@Override
	public Expression arccos(Expression x) {
		return sfunction("arccos", x);
	}

	/*
	 * @see
	 * cn.timelives.java.math.MathCalculator#arctan(java.lang.Object)
	 */
	@Override
	public Expression arctan(Expression x) {
		return sfunction("arctan", x);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#cos(java.lang.Object)
	 */
	@Override
	public Expression cos(Expression x) {
		return sfunction("cos", x);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#exp(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Expression exp(Expression a, Expression b) {
		return dFunction("exp", a, b);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#log(java.lang.Object,
	 * java.lang.Object)
	 */
	@Override
	public Expression log(Expression a, Expression b) {
		return dFunction("log", a, b);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#tan(java.lang.Object)
	 */
	@Override
	public Expression tan(Expression x) {
		return sfunction("tan", x);
	}

	/*
	 * @see cn.timelives.java.math.MathCalculator#getNumberClass()
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
	 * 
	 * @param root
	 * @return
	 */
	Node simplify(Node root) {
		return simplify(root, Integer.MAX_VALUE);
	}
	/**
	 * Simplifies the node with the given depth. Assigning depth = 0 means only
	 * simplify the node. The given node will be simplified regardless of its simplification identifier.
	 * @param root
	 * @param depth
	 * @return
	 */
	Node simplify(Node root, int depth) {
		checkValidTree(root);
		root.resetSimIdentifier();
		if(depth == 0) {
			//special case: avoid recursion.
			return simplifyNode(root);
		}
		return root.recurApply(x ->{
			if(simplificationIdentifier == x.simIdentifier) {
				//simplified
				return x;
			}
			return simplifyNode(x);
		}, depth);
	}
	private static long count = 0;
	/**
	 * Simplify the single node, this method will only simplify only node and 
	 * there will be no recursion. 
	 * @param node
	 * @return
	 */
	private Node simplifyNode(Node node) {
		if(debugEnabled) {
			count ++ ;
			if(count % 100 == 0)
				print("Simplify: "+node.getType()+" : "+node.hashCode());//TODO
		}
		node = simplifyPolynomial(node,0);
		doSort(node, 0);
		node = simplifyWithStrategyNoRecur(node);
		node.simIdentifier = simplificationIdentifier;
		return node;
	}

	/**
	 * Try to merge polynomials in the expression as well as possible. For example,
	 * 
	 * @param node
	 * @param depth
	 * @return
	 */
	Node simplifyPolynomial(Node node, int depth) {
		if(node.simIdentifier == simplificationIdentifier) {
			return node;
		}
		switch (node.getType()) {
		case POLYNOMIAL: {
			return node;
		}
		case ADD: {
			node = polySimplifyAdd((Add) node, depth);
			break;
		}
		case FRACTION: {
			node = polySimplifyFraction((Fraction) node, depth);
			break;
		}
		case MULTIPLY: {
			node = polySimplifyMultiply((Multiply) node, depth);
			break;
		}

		case S_FUNCTION: {
			node = polySimplifySFunction((SFunction) node, depth);
			break;
		}
		case D_FUNCTION: {
			node = polySimplifyDFunction((DFunction) node, depth);
			break;
		}
		case M_FUNCTION: {
			node = polySimplifyMFunction((MFunction) node, depth);
			break;
		}
		}
		return node;
	}

	Node setParentAndReturn(Node original, Node returned) {
		returned.parent = original.parent;
		return returned;
	}

	Node polySimplifyAdd(Add node, int depth) {
		Multinomial p = node.p;
		if (p == null) {
			p = pc.getZero();
		}
		List<Node> children = node.children;
		for (ListIterator<Node> lit = node.children.listIterator(children.size()); lit.hasPrevious();) {
			Node t = lit.previous();
			Node nt = depth > 0 ? simplifyPolynomial(t, depth - 1) : t;
			if (nt.getType() == Type.POLYNOMIAL) {
				// add this one
				Poly pn = (Poly) nt;
				p = pc.add(p, pn.p);
				lit.remove();
			} else if (nt != t) {
				lit.set(nt);
			}

		}
		if (children.isEmpty()) {
			Poly nn = Node.newPolyNode(p, node.parent);
			return nn;
		}
		if (pc.isZero(p)) {
			if (children.size() == 1) {
				return setParentAndReturn(node, children.get(0));
			}
			node.p = null;
		} else {
			node.p = p;
		}
		return node;
	}

	Node polySimplifyMultiply(Multiply node, int depth) {
		Multinomial p = node.p;
		if (p == null) {
			p = pc.getOne();
		}
		List<Node> children = node.children;
		for (ListIterator<Node> lit = node.children.listIterator(children.size()); lit.hasPrevious();) {
			Node t = lit.previous();
			Node nt = depth > 0 ? simplifyPolynomial(t, depth - 1) : t;
			if (nt.getType() == Type.POLYNOMIAL) {
				// add this one
				Poly pn = (Poly) nt;
				p = pc.multiply(p, pn.p);
				if (pc.isZero(p)) {
					// *0
					break;
				}
				lit.remove();
			} else if (nt != t) {
				lit.set(nt);
			}
		}

		if (node.children.isEmpty() || pc.isZero(p)) {
			Poly nn = Node.newPolyNode(p, node.parent);
			return nn;
		}
		if (pc.isEqual(p, pc.getOne())) {
			if (children.size() == 1) {
				return setParentAndReturn(node, children.get(0));
			}
			node.p = null;
		} else {
			node.p = p;
		}
		return node;
	}

	Node polySimplifyFraction(Fraction node, int depth) {
		Node nume = depth > 0 ? simplifyPolynomial(node.c1, depth - 1) : node.c1;
		Node deno = depth > 0 ? simplifyPolynomial(node.c2, depth - 1) : node.c2;
		if (nume.getType() == Type.POLYNOMIAL) {
			Poly pnume = (Poly) nume;
			if (pc.isZero(pnume.p)) {
				return Node.newPolyNode(pc.getZero(), node.parent);
			}
			if (deno.getType() == Type.POLYNOMIAL) {
				Poly pdeno = (Poly) deno;
				try {
					Multinomial quotient = pc.divide(pnume.p, pdeno.p);
					return Node.newPolyNode(quotient, node.parent);
				} catch (UnsupportedCalculationException ex) {
					// cannot compute
				}
				List<Multinomial> list = ps.simplify(Arrays.asList(pnume.p, pdeno.p));
				nume = Node.newPolyNode(list.get(0), node);
				deno = Node.newPolyNode(list.get(1), node);
			}
		} else if (deno.getType() == Type.POLYNOMIAL) {
			Poly pdeno = (Poly) deno;
			try {
				Multinomial _p = pc.reciprocal(pdeno.p);
				nume.parent = null;
				if (pc.isEqual(pOne, _p)) {
					nume.parent = node.parent;
					return nume;
				}
				Node n = Node.wrapNodeMultiply(nume, _p);
				n.parent = node.parent;
				return n;
			} catch (UnsupportedCalculationException ex) {
			}
		}
		node.c1 = nume;
		if(deno.parent==null) {
			deno.getClass();
		}
		node.c2 = deno;
		return node;
	}

	Node polySimplifySFunction(SFunction node, int depth) {
		Node c = depth > 0 ? simplifyPolynomial(node.child, depth - 1) : node.child;
		if (c.getType() == Type.POLYNOMIAL) {
			Poly p = (Poly) c;
			Multinomial result = fs.computeSingle(node.functionName, p.p);
			if (result != null) {
				return Node.newPolyNode(result, node.parent);
			}
		}
		node.child = c;
		return node;
	}

	Node polySimplifyDFunction(DFunction node, int depth) {
		Node c1 = depth > 0 ? simplifyPolynomial(node.c1, depth - 1) : node.c1;
		Node c2 = depth > 0 ? simplifyPolynomial(node.c2, depth - 1) : node.c2;
		if (c1.getType() == Type.POLYNOMIAL && c2.getType() == Type.POLYNOMIAL) {
			Poly p1 = (Poly) c1;
			Poly p2 = (Poly) c2;
			Multinomial result = fs.computeDouble(node.functionName, p1.p, p2.p);
			if (result != null) {
				return Node.newPolyNode(result, node.parent);
			}
		}
		node.c1 = c1;
		node.c2 = c2;
		return node;
	}

	Node polySimplifyMFunction(MFunction node, int depth) {
		boolean allPoly = true;
		List<Node> children = node.children;
		for (ListIterator<Node> lit = children.listIterator(); lit.hasNext();) {
			Node t = lit.next();
			Node nt = depth > 0 ? simplifyPolynomial(t, depth - 1) : t;
			if (nt.getType() != Type.POLYNOMIAL) {
				allPoly = false;
			}
			if (nt != t) {
				lit.set(nt);
			}
		}
		if (allPoly) {
			Multinomial[] ps = new Multinomial[children.size()];
			int i = 0;
			for (Node n : children) {
				ps[i++] = ((Poly) n).p;
			}
			Multinomial result = fs.computeMultiple(node.functionName, ps);
			if (result != null) {
				return Node.newPolyNode(result, node.parent);
			}
		}
		return node;
	}







	Node simplifyWithStrategy(Node node, int depth) {
		if(depth == 0) {
			return ss.performSimplification(node, enabledTags, this);
		}
		return node.recurApply(x -> ss.performSimplification(x, enabledTags, this), depth);
	}
	
	Node simplifyWithStrategyNoRecur(Node node) {
		return ss.performSimplification(node, enabledTags, this);
	}
	

	Node doSort(Node node, int depth) {
		return node.recurApply(x -> {
			if (x instanceof NodeWithChildren) {
				NodeWithChildren nwc = (NodeWithChildren) x;
				nwc.doSort(nc);
			}
			return x;
		}, depth);
	}

	Node simplifyPolyAndSort(Node n) {
		n = simplifyPolynomial(n, Integer.MAX_VALUE);
		n = doSort(n, Integer.MAX_VALUE);
		return n;
	}
	
	public void checkValidTree(Node n) {
		n.recurApply(x -> {
			if(x != n) {
				if(x.parent == null) {
					throw new AssertionError("For node: "+x.toString());
				}
			}
			return x;
		}, Integer.MAX_VALUE);
	}


    /**
     * Substitutes the multinomial for the given character to the expression. This method only supports
     * integral exponent for the character.
     * @param expr
     * @param ch
     * @param val
     * @return
     */
	public Expression substitute(Expression expr, String ch, Multinomial val) {
		Node root = expr.root.cloneNode(null);
		root = root.recurApply(x -> {
			Multinomial p = Node.getPolynomialPart(x, this);
			if(p!=null) {
				p = p.replace(ch,val);
				Node n = Node.setPolynomialPart(x, p);
//				if(n.parent == null) {
//					n.getClass();
//				}
				return n;
			}
			return x;
		}, Integer.MAX_VALUE);
		root = simplify(root);
		return new Expression(root);
	}

    /**
     * Substitutes the give expression {@code sub} for the character to the expression.
     * @param expr
     * @param ch
     * @param sub
     * @return
     */
	public Expression substitute(Expression expr, String ch, Expression sub) {
	    Node root = expr.root.cloneNode(null);
	    root = root.recurApply(x -> {
            Multinomial p = Node.getPolynomialPart(x, this);
            if(p==null) {
                return x;
            }
            Node afterSub = replaceMultinomial(p,ch,sub);
            if(afterSub == null){
                //not changed
                return x;
            }
            if(x.getType() == Type.POLYNOMIAL){
                //replace the whole
                afterSub.parent = x.parent;
                return afterSub;
            }
            CombinedNode comb = (CombinedNode)x;
            comb.addChild(afterSub);
            comb.setPolynomial(pOne);
            return comb;
        },Integer.MAX_VALUE);
//	    root.listNode(0);
        root = simplify(root);
        return new Expression(root);
    }

	static Node replaceMultinomial(Multinomial mul, String ch, Expression sub) {
	    int count = mul.containsCharCount(ch);
	    if(count == 0){
	        return null;
        }
        List<Node> nodes = new ArrayList<>(count);
	    Multinomial remains = mul.removeAll(x -> x.containsChar(ch));
	    for(Term t : mul.getTerms()){
	        if(!t.containsChar(ch)){
	            continue;
            }
            var pow = t.getCharacterPower(ch);
	        Term re = t.removeChar(ch);
	        DFunction nodeExp = Node.wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP,
                    sub.root.cloneNode(null),
                    Node.newPolyNode(Multinomial.monomial(Term.valueOf(pow)),null));
			Multiply nodeMul = Node.wrapNodeMultiply(nodeExp, Multinomial.monomial(re));
	        nodes.add(nodeMul);
        }
        return Node.wrapNodeAM(true,nodes,remains);
    }




	public Expression parseExpr(String expr){
		Expression expression = Expression.valueOf(expr);
		return simplify(expression);
	}
	
	/**
	 * Gets a default instance of the ExprCalculator.
	 * @return
	 */
	public static ExprCalculator getInstance() {
		return new ExprCalculator();
	}
	
	static boolean debugEnabled = false;
}
