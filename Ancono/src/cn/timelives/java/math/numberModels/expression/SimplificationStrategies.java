/**
 * 2017-11-26
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.expression.Node.Add;
import cn.timelives.java.math.numberModels.expression.Node.Fraction;
import cn.timelives.java.math.numberModels.expression.Node.Multiply;
import cn.timelives.java.math.numberModels.expression.Node.Type;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.structure.Pair;

/**
 * A class provides basic simplification strategies.
 * @author liyicheng
 * 2017-11-26 15:22
 *
 */
public final class SimplificationStrategies {

	/**
	 * 
	 */
	private SimplificationStrategies() {
	}
	
	public static final String TAG_ALGEBRA = "algebra";
	public static final Set<String> TAG_ALGEBRA_SET = Collections.unmodifiableSet(CollectionSup.createHashSet(TAG_ALGEBRA));
	
	public static abstract class Merge extends SimStraImpl{

		/**
		 * @param tags
		 * @param types
		 * @param fname
		 */
		public Merge(Set<Type> types) {
			super(TAG_ALGEBRA_SET, types, null);
		}
		
	}
	
	
	/**
	 * The collect strategy applies to Add, which will try to collect expression of the following type:
	 * <pre> <i>expr</i>*p1 + <i>expr</i>*p2</pre>, where {@code p1} and {@code p2} are 
	 * two polynomials and <i>expr</i> is any type of node.
	 * @author liyicheng
	 * 2017-11-26 15:38
	 *
	 */
	public static abstract class Collect extends SimStraImpl{
		public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.ADD));
		
		/**
		 * @param tags
		 * @param types
		 * @param rname
		 */
		public Collect() {
			super(TAG_ALGEBRA_SET, types, null);
		}
		
		/*
		 */
		@Override
		protected abstract Node simplifyAdd(Add node, ExprCalculator mc);
	}
	
	
	
	public static List<SimStraImpl> getDefaultStrategies(){
		List<SimStraImpl> list = new ArrayList<>();
		list.add(new Merge(CollectionSup.unmodifiableEnumSet(Type.ADD,Type.MULTIPLY,Type.FRACTION)) {
			/*
			 */
			@Override
			protected Node simplifyAdd(Add node, ExprCalculator mc) {
				List<Node> children = node.children;
				boolean hasAdd = false;
				int add = 0;
				for(Node n : children) {
					if(n.getType() == Type.ADD) {
						hasAdd = true;
						add+=((Add)n).getNumberOfChildren();
					}else {
						add ++;
					}
				}
				if(!hasAdd) {
					return node;
				}
				List<Node> nChildren = new ArrayList<>(add);
				Polynomial p = Node.getPolynomialOrZero(node, mc);
				PolyCalculator pc = mc.pc;
				for(Node n : children) {
					if(n.getType() == Type.ADD) {
						Add sub = (Add)n;
						p = pc.add(p, Node.getPolynomialOrZero(sub, mc));
						for(Node _n : sub.children) {
							_n.parent = node;
							nChildren.add(_n);
						}
					}else {
						n.parent = node;
						nChildren.add(n);
					}
				}
				node.children = nChildren;
				node.p = p;
				return mc.simplify(node, 0);
			}

			@Override
			protected Node simplifyMultiply(Multiply node, ExprCalculator mc) {
				List<Node> children = node.children;
				boolean hasMul = false;
				int num = 0;
				for (Node n : children) {
					if (n.getType() == Type.MULTIPLY) {
						hasMul = true;
						num += ((Multiply) n).getNumberOfChildren();
					} else {
						num++;
					}
				}
				if (!hasMul) {
					return node;
				}
				List<Node> nChildren = new ArrayList<>(num);
				Polynomial p = Node.getPolynomialOrZero(node, mc);
				PolyCalculator pc = mc.pc;
				for (Node n : children) {
					if (n.getType() == Type.MULTIPLY) {
						Multiply sub = (Multiply) n;
						p = pc.multiply(p, Node.getPolynomialOrZero(sub, mc));
						for (Node _n : sub.children) {
							_n.parent = node;
							nChildren.add(_n);
						}
					} else {
						n.parent = node;
						nChildren.add(n);
					}
				}
				node.children = nChildren;
				node.p = p;
				return mc.simplify(node, 0);
			}

			/*
			 */
			@Override
			protected Node simplifyFraction(Fraction node, ExprCalculator mc) {
				
				if(node.c1.getType() == Type.FRACTION) {
					Fraction f1 = (Fraction) node.c1;
					if(node.c2.getType() == Type.FRACTION) {
						Fraction f2 = (Fraction) node.c2;
						Node nume = Node.wrapNodeAM(false, f1.c1, f2.c2);
						Node deno = Node.wrapNodeAM(false, f1.c2, f2.c1);
						node.setBoth(nume, deno);
						
					}else {
						Node nume = f1.c1;
						Node deno = Node.wrapCloneNodeAM(false, f1.c2, node.c2);
						node.setBoth(nume, deno);
					}
					return mc.simplify(node,1);
				}else if(node.c2.getType() == Type.FRACTION) {
					Fraction f2 = (Fraction) node.c2;
					Node nume = Node.wrapNodeAM(false, node.c1, f2.c2);
					Node deno = f2.c1;
					node.setBoth(nume, deno);
					return mc.simplify(node,1);
				}
				return node;
			}
		});
		list.add(new Collect() {
			@Override
			protected Node simplifyAdd(Add node, ExprCalculator mc) {
				List<Node> children = node.children;
				TreeMap<Node,Polynomial> map = new TreeMap<>(mc.nc);
				boolean collected = false;
				PolyCalculator pc = mc.pc;
				Polynomial pOne = mc.pOne;
				//separate to
				for(Node n : children) {
					Pair<Polynomial, Node> p = Node.unwrapMultiply(n, mc);
					Polynomial pn = pOne;
					if(p!=null) {
						pn = p.getFirst();
						n = p.getSecond();
					}
					Polynomial poly = map.get(n);
					if (poly != null) {
						collected = true;
						map.put(n, pc.add(poly, pn));
					} else {
						map.put(n, pn);
					}
				}
				if(!collected) {
					return node;
				}
				children.clear();
				for(Entry<Node,Polynomial> en : map.entrySet()) {
					Polynomial p = en.getValue();
					if(pc.isZero(p)) {
						continue;
					}
					Node n = en.getKey();
					n.parent = null;
					if(!pc.isEqual(p, pOne)) {
						n = Node.wrapNodeMultiply(n, p);
					}
					children.add(n);
					n.parent = node;
				}
				return mc.simplify(node,0);
			}
		});
		
		return list;
	}
	
	
}
