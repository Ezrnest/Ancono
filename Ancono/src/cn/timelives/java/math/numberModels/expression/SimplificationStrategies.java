/**
 * 2017-11-26
 */
package cn.timelives.java.math.numberModels.expression;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.math.numberModels.expression.Node.Add;
import cn.timelives.java.math.numberModels.expression.Node.CombinedNode;
import cn.timelives.java.math.numberModels.expression.Node.DFunction;
import cn.timelives.java.math.numberModels.expression.Node.Fraction;
import cn.timelives.java.math.numberModels.expression.Node.Multiply;
import cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren;
import cn.timelives.java.math.numberModels.expression.Node.Poly;
import cn.timelives.java.math.numberModels.expression.Node.SFunction;
import cn.timelives.java.math.numberModels.expression.Node.Type;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.structure.Pair;

/**
 * A class provides basic simplification strategies.
 * 
 * @author liyicheng 2017-11-26 15:22
 *
 */
public final class SimplificationStrategies {

	/**
	 * 
	 */
	private SimplificationStrategies() {
	}
	
	/**
	 * Determines whether to merge fraction: a/b+c/d = (ad+bc)/bd
	 */
	public static final String PROP_MERGE_FRACTION = "mergeFraction";
	/**
	 * Determines whether to expand multiplication:<pre>a*(b+c) = a*b + a*c</pre>
	 */
	public static final String	PROP_ENABLE_EXPAND = "enableExpand";
	
	public static final String TAG_ALGEBRA = "algebra";
	public static final Set<String> TAG_ALGEBRA_SET = Collections
			.unmodifiableSet(CollectionSup.createHashSet(TAG_ALGEBRA));
	public static final Set<Type> TYPES_FUNCTION = 
			CollectionSup.unmodifiableEnumSet(Type.S_FUNCTION,Type.D_FUNCTION,Type.M_FUNCTION);
	
	public static final String TAG_REGULARIZE = "regularize";
	public static final Set<String> TAG_REGULARIZE_SET = Collections
			.unmodifiableSet(CollectionSup.createHashSet(TAG_REGULARIZE));
	
	public static final String PRIMARY_FUNCTION = "primaryFunction";
	public static final Set<String> TAG_PRIMARY_SET = Collections
			.unmodifiableSet(CollectionSup.createHashSet(PRIMARY_FUNCTION));
	
	
	
	public static abstract class SimplifyPoly extends SimpleStrategy {
		public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.POLYNOMIAL));
		/**
		 * @param tags
		 * @param types
		 * @param fname
		 * @param description
		 */
		public SimplifyPoly(String description) {
			super(TAG_REGULARIZE_SET, types, null, "Simp");
		}
		
	}
	
	public static abstract class Merge extends SimpleStrategy {

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
	 * The collect strategy applies to Add, which will try to collect expression of
	 * the following type:
	 * 
	 * <pre>
	 *  <i>expr</i>*p1 + <i>expr</i>*p2
	 * </pre>
	 * 
	 * , where {@code p1} and {@code p2} are two polynomials and <i>expr</i> is any
	 * type of node.
	 * 
	 * @author liyicheng 2017-11-26 15:38
	 *
	 */
	public static abstract class Collect extends SimpleStrategy {
		public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.ADD));

		/**
		 * @param tags
		 * @param types
		 * @param rname
		 */
		public Collect() {
			super(TAG_ALGEBRA_SET, types, null,"Collect expressions which have a common factor,"
					+ " which is shown as a series of function node multiplied.");
		}

		/*
		 */
		@Override
		protected abstract Node simplifyAdd(Add node, ExprCalculator mc);
	}
	
	static final class SimplifyFraction extends SimpleStrategy{
		public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.ADD,Type.FRACTION,Type.MULTIPLY));

		/**
		 * @param tags
		 * @param types
		 * @param fname
		 */
		SimplifyFraction() {
			super(TAG_ALGEBRA_SET, types, null,"Simplifies fraction:Add, multiply and fraction.");
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.SimpleStrategy#simplifyAdd(cn.timelives.java.math.numberModels.expression.Node.Add, cn.timelives.java.math.numberModels.expression.ExprCalculator)
		 */
		@Override
		protected Node simplifyAdd(Add node, ExprCalculator mc) {
			List<Node> children = node.children;
			//search add
			int count = 0;
			for(Node n : children) {
				if(n.getType() == Type.FRACTION) {
					count++;
				}
			}
			if(count < 2) {
				return null;
			}
			//try merge
			TreeMap<Node,List<Node>> map = new TreeMap<>(mc.nc);
			for(Node n: children) {
				if(n.getType() == Type.FRACTION) {
					Fraction f = (Fraction)n;
					CollectionSup.accumulateMap(map, f.c2, f.c1, ArrayList::new);
				}
			}
			if(map.size()==count) {
				//nothing can be merged.
				return null;
			}
			map.entrySet().removeIf(en -> en.getValue().size() <= 1);
			children.removeIf(x -> {
				if(x.getType() == Type.FRACTION) {
					Fraction f = (Fraction) x;
					return map.containsKey(f.c2);
				}else {
					return false;
				}
			});
			for(Entry<Node,List<Node>> en : map.entrySet()) {
				Node deno = en.getKey();
				List<Node> numes = en.getValue();
				Fraction f = Node.wrapNodeFraction(Node.wrapNodeAM(true, numes), deno);
				children.add(f);
				f.parent = node;
			}
			return mc.simplify(node, 2);
		}
		
		/**
		 * If a fraction is contained in the multiply, then turns the multiply into a fraction. 
		 */
		@Override
		protected Node simplifyMultiply(Multiply node, ExprCalculator mc) {
			List<Node> children = node.children;
			//search fraction
			int count = 0;
			for(Node n : children) {
				if(n.getType() == Type.FRACTION) {
					count++;
				}
			}
			if(count == 0) {
				return null;
			}//TODO
			
			List<Node> numes = new ArrayList<>(),
					denos = new ArrayList<>();
			for(Node n : children) {
				if(n.getType() == Type.FRACTION) {
					Fraction f = (Fraction) n;
					numes.add(f.c1);
					denos.add(f.c2);
				}else {
					numes.add(n);
				}
			}
			NodeWithChildren parent = node.parent;
			node.children = numes;
			Node deno = Node.wrapNodeAM(false, denos);
			Fraction frac = Node.wrapNodeFraction(node, deno);
			frac.parent = parent;
			
			node.resetSimIdentifier();
			return mc.simplify(frac,1);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.SimpleStrategy#simplifyFraction(cn.timelives.java.math.numberModels.expression.Node.Fraction, cn.timelives.java.math.numberModels.expression.ExprCalculator)
		 */
		@Override
		protected Node simplifyFraction(Fraction node, ExprCalculator mc) {
			Node c1 = node.c1,
					c2 = node.c2;
			Type t1 = c1.getType(),
					t2 = c2.getType();
			if(!((t1 == Type.MULTIPLY || t1 == Type.POLYNOMIAL) && (t2 == Type.MULTIPLY || t2==Type.POLYNOMIAL))) {
				return null;
			}
			Polynomial pnume = Node.getPolynomialPart(c1, mc),
					pdeno = Node.getPolynomialPart(c2, mc);
			boolean sim = false;
			if(t1 == Type.MULTIPLY && t2 == Type.MULTIPLY) {
				sim = simplifyDivideNode((Multiply)c1,(Multiply)c2,mc);
			}
			
			List<Polynomial> list = Arrays.asList(pnume,pdeno);
			list = mc.ps.simplify(list);
			if(!sim && mc.pc.isEqual(pnume, list.get(0))){
				//nothing is changed.
				return null;
			}
			pnume = list.get(0);
			pdeno = list.get(1);
			c1 = Node.setPolynomialPart(c1, pnume);
			c2 = Node.setPolynomialPart(c2, pdeno);
			c1.resetSimIdentifier();
			c2.resetSimIdentifier();
			node.c1 = c1;
			node.c2 = c2;
			
			return mc.simplify(node, 1);
		}
		
	}
	
	
	static boolean simplifyDivideNode(Multiply m1,Multiply m2,ExprCalculator mc) {
		List<Node> c1 = m1.children;
		List<Node> c2 = m2.children;
		Comparator<Node> nc = mc.nc;
		m1.doSort(nc);
		m2.doSort(nc);
		boolean sim = false;
		for(ListIterator<Node> it1 = c1.listIterator(c1.size());it1.hasPrevious();) {
			Node n1 = it1.previous();
			int index = Collections.binarySearch(c2, n1, nc);
			if(index > -1) {
				sim = true;
				c2.remove(index);
				it1.remove();
			}
		}
		return sim;
	}
	/**
	 * Deals with the expansion of multiply when it has an Add.
	 * @author liyicheng
	 * 2017-12-01 19:41
	 *
	 */
	public static class Expand extends SimpleStrategy{
		public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.MULTIPLY));
		/**
		 * @param tags
		 * @param types
		 * @param fname
		 */
		public Expand() {
			super(TAG_ALGEBRA_SET, types, null);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.SimpleStrategy#simplifyMultiply(cn.timelives.java.math.numberModels.expression.Node.Multiply, cn.timelives.java.math.numberModels.expression.ExprCalculator)
		 */
		@Override
		protected Node simplifyMultiply(Multiply node, ExprCalculator mc) {
			if(!Boolean.parseBoolean(mc.getProperty(PROP_ENABLE_EXPAND))) {
				return null;
			}
			Add add = null;
			List<Node> children = node.children;
			for(Node n : children) {
				if(n.getType() == Type.ADD) {
					add = (Add)n;
					break;
				}
			}
			if(add == null) {
				return null;
			}
			
			
			children.remove(add);
			//attach the node to add, then return
			// (x+y)*a*b*...  
			//=(x*a*b*...) + (y*a*b*...)
			//expand the node
			List<Node> adds = add.children;
			List<Node> nchildren = new ArrayList<>(adds.size()+1);
			
			for(Node n : adds) {
				//multiply each single node
				List<Node> factors = new ArrayList<>(children.size()+1);
				Multiply mul = new Multiply(add, node.p, factors);
				for(Node factor: children) {
					factor = factor.cloneNode(mul);
					factors.add(factor);
				}
				factors.add(n);
				n.parent = mul;
				nchildren.add(mul);
			}
			if(add.p!= null) {
				Poly polynode = Node.newPolyNode(add.p, null);
				List<Node> factors = new ArrayList<>(children.size()+1);
				Multiply mul = new Multiply(add, node.p, factors);
				for(Node factor: children) {
					factor = factor.cloneNode(mul);
					factors.add(factor);
				}
				factors.add(polynode);
				polynode.parent = mul;
				nchildren.add(mul);
			}
			for(ListIterator<Node> it = nchildren.listIterator();it.hasNext();) {
				Multiply m = (Multiply)it.next();
				//recursion
				Node result = simplifyMultiply(m, mc);
				if(result != null) {
					it.set(result);
				}
			}
			
			add.resetSimIdentifier();
			add.children = nchildren;
			add.p = null;
			add.parent = node.parent;
			
			return mc.simplify(add, 2);
		}
		
	}
	
	public static abstract class SimplifyFunction extends SimpleStrategy{
		
		/**
		 * @param tags
		 * @param types
		 * @param fname
		 * @param description
		 */
		public SimplifyFunction(Set<String> tags, String fname, String description) {
			super(tags, TYPES_FUNCTION, fname, description);
		}
	}
	
	
	/**
	 * Deals with a common situation when two or more factors are multiplied or divided, the factors 
	 * may has an exponent which is an BigInteger. The simplifier should decide whether to simplify the 
	 * multiplication or not.
	 * @author liyicheng
	 * 2017-12-01 20:14
	 *
	 */
	public static abstract class SimplifyMultiplyStruct extends SimpleStrategy{
		public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.MULTIPLY,Type.FRACTION));
		/**
		 * @param tags assigns the tags of this multiply.
		 */
		public SimplifyMultiplyStruct(Set<String> tags) {
			super(tags, types, null);
		}
		
		
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.SimStraImpl#simplifyMultiply(cn.timelives.java.math.numberModels.expression.Node.Multiply, cn.timelives.java.math.numberModels.expression.ExprCalculator)
		 */
		@Override
		protected Node simplifyMultiply(Multiply node, ExprCalculator mc) {
			List<Node> list = node.children;
			if(!firstGlance(list, mc)) {
				return null;
			}
			List<Pair<Node,BigInteger>> collect = new ArrayList<>(list.size()/2);
			//copy first
			list = new ArrayList<>(list);
			filterAndAdd(list, mc, false, collect);
			if(collect.isEmpty()) {
				return null;
			}
			List<Pair<Node,BigInteger>> result = simplify(collect,mc);
			if(result == null) {
				return null;
			}else {
				addToTheList(list, result,node, mc);
				node.resetSimIdentifier();
				node.children = list;
				return mc.simplify(node);
			}
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.SimStraImpl#simplifyFraction(cn.timelives.java.math.numberModels.expression.Node.Fraction, cn.timelives.java.math.numberModels.expression.ExprCalculator)
		 */
		@Override
		protected Node simplifyFraction(Fraction node, ExprCalculator mc) {
			Node c1 = node.c1,
					c2 = node.c2;
			List<Node> nume = new ArrayList<>(1),
					deno = new ArrayList<>(1);
			nume.add(c1);
			deno.add(c2);
			boolean simplify = false;
			if(firstGlance(nume, mc) || firstGlance(deno, mc)) {
				simplify = true;
			}
			if(!simplify) {
				if(c1.getType() == Type.MULTIPLY) {
					Multiply mul = (Multiply) c1;
					nume.set(0, Node.newPolyNode(Node.getPolynomialOrDefault(mul, mc), null));
					nume.addAll(mul.children);
					if(firstGlance(nume, mc)) {
						simplify = true;
					}
				}
			}
			if(!simplify) {
				if(c2.getType() == Type.MULTIPLY) {
					Multiply mul = (Multiply) c2;
					deno.set(0, Node.newPolyNode(Node.getPolynomialOrDefault(mul, mc), null));
					deno.addAll(mul.children);
					if(firstGlance(deno, mc)) {
						simplify = true;
					}
				}
			}
			if(!simplify) {
				return null;
			}
			List<Pair<Node,BigInteger>> collect = new ArrayList<>(nume.size()+deno.size());
			filterAndAdd(nume, mc, false, collect);
			filterAndAdd(deno, mc, true, collect);
			if(collect.isEmpty()) {
				return null;
			}
			List<Pair<Node,BigInteger>> result = simplify(collect,mc);
			if(result == null) {
				return null;
			}else {
				addToTheList(nume, deno,result, mc);
				node.resetSimIdentifier();
				c1 = Node.wrapNodeAM(false, nume);
				c2 = Node.wrapNodeAM(false, deno);
				c1.parent = node;
				c2.parent = node;
				node.c1 = c1;
				node.c2 = c2;
				return mc.simplify(node);
			}
		}
		
		private static final BigInteger NEGATIVE_ONE = BigInteger.ONE.negate();
		private void filterAndAdd(List<Node> list,ExprCalculator mc,boolean deno,List<Pair<Node,BigInteger>> collect) {
			for(ListIterator<Node> it=list.listIterator(list.size());it.hasPrevious();) {
				Node n = it.previous();
				BigInteger pow = deno ?NEGATIVE_ONE:BigInteger.ONE;
				if(accept(n, pow,mc)) {
					collect.add(new Pair<>(n,pow));
					it.remove();
					continue;
				}
				Pair<Node,BigInteger> pair = Node.peelExpStructure(n, mc);
				if(pair!=null) {
					if(deno) {
						pair.setSecond(pair.getSecond().negate());
					}
					if(accept(n, pow,mc)) {
						collect.add(pair);
						it.remove();
						continue;
					}
				}
			}
		}
		
		private void addToTheList(List<Node> list,List<Pair<Node,BigInteger>> result,NodeWithChildren parent, ExprCalculator mc) {
			for(Pair<Node,BigInteger> en : result) {
				Node node = Node.buildExpStructure(en.getFirst(), en.getSecond(), mc);
				node.parent = parent;
				list.add(node);
			}
		}
		private void addToTheList(List<Node> nume,List<Node> deno,List<Pair<Node,BigInteger>> result, ExprCalculator mc) {
			for(Pair<Node,BigInteger> en : result) {
				BigInteger pow = en.getSecond();
				if(pow.signum() < 0) {
					deno.add(Node.buildExpStructure(en.getFirst(), pow.negate(), mc));
				}else {
					nume.add(Node.buildExpStructure(en.getFirst(), pow, mc));
				}
				
			}
		}
		
		protected Node buildExpStructure(Node n,BigInteger pow, ExprCalculator mc) {
			return Node.buildExpStructure(n, pow, mc);
		}
		
		
		
		/**
		 * Quickly tests the nodes to decide whether the simplification is necessary.
		 * This method is used to filter firstly. 
		 */
		protected abstract boolean firstGlance(List<Node> nodes,ExprCalculator ec);
		
		protected abstract boolean accept(Node n,BigInteger pow, ExprCalculator mc);
		
		/**
		 *  Performs simplify to the list, returns null if nothing is simplified.
		 * @param nodes
		 * @return
		 */
		protected abstract List<Pair<Node,BigInteger>> simplify(List<Pair<Node,BigInteger>> nodes, ExprCalculator ec);
	}
	
	
	public static void addRegularization(List<SimpleStrategy> list) {
		list.add(new SimplifyPoly("Regularization") {
			/*
			 * @see cn.timelives.java.math.numberModels.expression.SimpleStrategy#simplifyPolynomial(cn.timelives.java.math.numberModels.expression.Node.Poly, cn.timelives.java.math.numberModels.expression.ExprCalculator)
			 */
			@Override
			protected Node simplifyPolynomial(Poly node, ExprCalculator mc) {
				return Node.newPolyNode(node.p.regularizeExponent(), node.parent);
			}
		});
	}
	
	public static void addBasicAlgebra(List<SimpleStrategy> list) {
		list.add(new Merge(CollectionSup.unmodifiableEnumSet(Type.ADD, Type.MULTIPLY, Type.FRACTION)) {
			/**
			 * This method simplify
			 */
			@Override
			protected Node simplifyAdd(Add node, ExprCalculator mc) {
				List<Node> children = node.children;
				boolean hasAdd = false;
				int add = 0;
				for (Node n : children) {
					if (n.getType() == Type.ADD) {
						hasAdd = true;
						add += ((Add) n).getNumberOfChildren();
					} else {
						add++;
					}
				}
				if (!hasAdd) {
					return null;
				}
				List<Node> nChildren = new ArrayList<>(add);
				Polynomial p = Node.getPolynomialOrDefault(node, mc);
				PolyCalculator pc = mc.pc;
				for (Node n : children) {
					if (n.getType() == Type.ADD) {
						Add sub = (Add) n;
						p = pc.add(p, Node.getPolynomialOrDefault(sub, mc));
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
					return null;
				}
				List<Node> nChildren = new ArrayList<>(num);
				Polynomial p = Node.getPolynomialOrDefault(node, mc);
				PolyCalculator pc = mc.pc;
				for (Node n : children) {
					if (n.getType() == Type.MULTIPLY) {
						Multiply sub = (Multiply) n;
						p = pc.multiply(p, Node.getPolynomialOrDefault(sub, mc));
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
				if (node.c1.getType() == Type.FRACTION) {
					Fraction f1 = (Fraction) node.c1;
					if (node.c2.getType() == Type.FRACTION) {
						Fraction f2 = (Fraction) node.c2;
						Node nume = Node.wrapNodeAM(false, f1.c1, f2.c2);
						Node deno = Node.wrapNodeAM(false, f1.c2, f2.c1);
						node.setBoth(nume, deno);

					} else {
						Node nume = f1.c1;
						Node deno = Node.wrapCloneNodeAM(false, f1.c2, node.c2);
						node.setBoth(nume, deno);
					}
					return mc.simplify(node, 1);
				} else if (node.c2.getType() == Type.FRACTION) {
					Fraction f2 = (Fraction) node.c2;
					Node nume = Node.wrapNodeAM(false, node.c1, f2.c2);
					Node deno = f2.c1;
					node.setBoth(nume, deno);
					return mc.simplify(node, 1);
				}
				return null;
			}
		});
		list.add(new Collect() {
			@Override
			protected Node simplifyAdd(Add node, ExprCalculator mc) {
				if(node.getNumberOfChildren()<=1) {
					return null;
				}
				List<Node> children = node.children;
				TreeMap<List<Node>, Polynomial> map = new TreeMap<>(CollectionSup.listComparator(mc.nc));
				boolean collected = false;
				PolyCalculator pc = mc.pc;
				Polynomial pOne = mc.pOne;
				// separate to
				for (Node n : children) {
					Pair<Polynomial, List<Node>> p = Node.unwrapMultiplyList(n, mc);
					Polynomial pn = pOne;
					List<Node> list;
					if (p != null) {
						pn = p.getFirst();
						list = p.getSecond();
					}else {
						list = Collections.singletonList(n);
					}
					Polynomial poly = map.get(list);
					if (poly != null) {
						collected = true;
						map.put(list, pc.add(poly, pn));
					} else {
						map.put(list, pn);
					}
				}
				if (!collected) {
					return null;
				}
				children.clear();
				for (Entry<List<Node>, Polynomial> en : map.entrySet()) {
					Polynomial p = en.getValue();
					if (pc.isZero(p)) {
						continue;
					}
					List<Node> list = en.getKey();
					Node n;
					if(list.size() == 1) {
						if (!pc.isEqual(p, pOne)) {
							n = Node.wrapNodeMultiply(list.get(0), p);
						}else {
							n = list.get(0);
						}
					}else {
						if (!pc.isEqual(p, pOne)) {
							n = Node.wrapNodeAM(false, list,p);
						}else {
							n = Node.wrapNodeAM(false, list);
						}
					}
					children.add(n);
					n.parent = node;
				}
				return mc.simplify(node, 0);
			}
		});
		list.add(new SimplifyFraction());
		list.add(new Collect() {
			@Override
			protected Node simplifyAdd(Add node, ExprCalculator mc) {
				if(node.getNumberOfChildren()<=1 && node.p == null) {
					return null;
				}
				if(!Boolean.parseBoolean(mc.getProperty(PROP_MERGE_FRACTION))){
					return null;
				}
				List<Node> children = node.children;
				int count = 0;
				for(Node n : children) {
					if(n.getType() == Type.FRACTION) {
						count ++;
					}
				}
				if(count == 0) {
					return null;
				}
				List<Node> numes = new ArrayList<>(children.size()),
						denos = new ArrayList<>(count);
				for(Node n : children) {
					if(n.getType() == Type.FRACTION) {
						Fraction f = (Fraction)n;
						denos.add(f.c2);
					}
				}
				for(Node n : children) {
					List<Node> mul = new ArrayList<>(count+1);
					if(n.getType() == Type.FRACTION) {
						Fraction f = (Fraction) n;
						for(Node nd : denos) {
							if(nd != f.c2) {
								mul.add(nd.cloneNode(null));
							}
						}
						mul.add(f.c1);
					}else {
						for(Node nd : denos) {
							mul.add(nd.cloneNode(null));
						}
						mul.add(n);
					}
					Node m = Node.wrapNodeAM(false, mul);
					numes.add(m);
				}
				if (node.p != null) {
					Poly p = Node.newPolyNode(node.p, null);
					List<Node> mul = new ArrayList<>(count+1);
					for(Node nd : denos) {
						mul.add(nd.cloneNode(null));
					}
					mul.add(p);
					Node m = Node.wrapNodeAM(false, mul);
					numes.add(m);
				}
				Node nume = Node.wrapNodeAM(true, numes);
				Node deno = Node.wrapNodeAM(false, denos);
				Fraction f = Node.wrapNodeFraction(nume, deno);
				f.parent = node.parent;
				return mc.simplify(f, 2);
			}
		});
		list.add(new Expand());
		
	}
	
	
	public static void addSqrStrategies(List<SimpleStrategy> list) {
		list.add(new SimplifyFunction(TAG_ALGEBRA_SET,"sqr","convert sqr to exp") {
			/*
			 * @see cn.timelives.java.math.numberModels.expression.SimStraImpl#simplifySFunction(cn.timelives.java.math.numberModels.expression.Node.SFunction, cn.timelives.java.math.numberModels.expression.ExprCalculator)
			 */
			@Override
			protected Node simplifySFunction(SFunction node, ExprCalculator mc) {
				//sqr node 
				if(node.getFunctionName().equals("sqr")==false) {
					throw new AssertionError();
				}
				Node n =  Node.wrapNodeDF("exp", node.child, Node.newPolyNode(mc.pc.divideLong(mc.pOne, 2l), null));
				n.parent = node;
				return n;
			}
		});
	}
	
	
	public static void addExpStrategies(List<SimpleStrategy> list) {
		list.add(new SimplifyFunction(TAG_PRIMARY_SET,"exp","") {
			/*
			 * @see cn.timelives.java.math.numberModels.expression.SimStraImpl#simplifySFunction(cn.timelives.java.math.numberModels.expression.Node.SFunction, cn.timelives.java.math.numberModels.expression.ExprCalculator)
			 */
			@Override
			protected Node simplifySFunction(SFunction node, ExprCalculator mc) {
				if(Node.isFunctionNode(node.child, "ln", 1)) {
					SFunction ln = (SFunction) node.child;
					//exp(ln(x)) = x
					return ln.child;
				}
				return null;
			}
			
			/*
			 * @see cn.timelives.java.math.numberModels.expression.SimStraImpl#simplifyDFunction(cn.timelives.java.math.numberModels.expression.Node.DFunction, cn.timelives.java.math.numberModels.expression.ExprCalculator)
			 */
			@Override
			protected Node simplifyDFunction(DFunction node, ExprCalculator mc) {
				Node c1 = node.c1,
						c2 = node.c2;
				if(Node.isPolynomial(c1,mc.pc.constantValue(MathCalculator.STR_E),mc)) {
					return Node.wrapNodeSF("exp", c2);
				}
				if(Node.isFunctionNode(c1, "exp", 2)) {
					DFunction df = (DFunction)c1;
					//exp(exp(x,p1),p2) = exp(x,p1*p2)
					CombinedNode mul = Node.wrapNodeAM(false, df.c2, c2);
					node.c2 = mul;
					mul.parent = node;
					node.resetSimIdentifier();
					return mc.simplify(node, 1);
				}else if(Node.isFunctionNode(c1, "exp", 1)) {
					SFunction sf = (SFunction)c1;
					//exp(exp(x,p1),p2) = exp(x,p1*p2)
					CombinedNode mul = Node.wrapNodeAM(false, sf.child, c2);
					mul.parent = sf;
					sf.child = mul;
					sf.parent = node.parent;
					return mc.simplify(sf, 1);
				}else {
					return null;
				}
			}
		});
		
		list.add(new SimplifyMultiplyStruct(TAG_PRIMARY_SET) {
			//exp(x,y) * exp(x,z) = exp(x,y+z)
			@Override
			protected List<Pair<Node, BigInteger>> simplify(List<Pair<Node, BigInteger>> nodes, ExprCalculator ec) {
				TreeMap<Node,List<Pair<Node,BigInteger>>> map = new TreeMap<>(ec.nc);
				for(Pair<Node,BigInteger> p : nodes) {
					DFunction df = (DFunction) p.getFirst();
					CollectionSup.accumulateMap(map, df.c1, new Pair<>(df.c2,p.getSecond()),ArrayList::new);
				}
				if(map.size() >= nodes.size()) {
					return null;
				}
				List<Pair<Node,BigInteger>> nlist = new ArrayList<>(map.size());
				for(Entry<Node,List<Pair<Node,BigInteger>>> en : map.entrySet()) {
					Node down = en.getKey();
					List<Pair<Node,BigInteger>> powers = en.getValue();
					if(powers.size() == 1) {
						Pair<Node,BigInteger> p = powers.get(0);
						Node exponent = Node.wrapNodeMultiply(p.getFirst(), ec.pc.valueOfBigInteger(p.getSecond()));
						Pair<Node, BigInteger> result = new Pair<>(Node.wrapNodeDF("exp", down, exponent),
								BigInteger.ONE);
						nlist.add(result);
					}else {
						List<Node> adds = new ArrayList<>(powers.size());
						for(Pair<Node,BigInteger> pair : powers) {
							Node exponent = Node.wrapNodeMultiply(pair.getFirst(), ec.pc.valueOfBigInteger(pair.getSecond()));
							adds.add(exponent);
						}
						Node expo = Node.wrapNodeAM(true, adds);
						Node result = Node.wrapNodeDF("exp", down, expo);
						nlist.add(new Pair<>(result,BigInteger.ONE));
					}
				}
				return nlist;
			}
			
			@Override
			protected boolean firstGlance(List<Node> nodes, ExprCalculator ec) {
				for(Node n : nodes) {
					if(Node.isFunctionNode(n, "exp", 2)) {
						return true;
					}
				}
				return false;
			}
			
			@Override
			protected boolean accept(Node n, BigInteger pow, ExprCalculator mc) {
				return Node.isFunctionNode(n, "exp", 2);
			}
		});
	}
	/**
	 * Add some settings to the calculator. This method is usually used when the result is required.
	 * <ul>
	 * <li>Enables the calculator to merge fraction: <text>a/b+c/d = (ad+bc)/bd</text>
	 * <li>Enables the calculator to expand multiplication: <text>a*(b+c) = a*b + a*c</text>
	 * <li>Add tag : {@link #TAG_REGULARIZE}
	 * </ul>
	 * @param ec
	 */
	public static void setCalRegularization(ExprCalculator ec) {
		ec.setProperty(SimplificationStrategies.PROP_MERGE_FRACTION, "true");
		ec.setProperty(PROP_ENABLE_EXPAND, "true");
		ec.tagAdd(TAG_REGULARIZE);
	}

	public static List<SimpleStrategy> getDefaultStrategies() {
		List<SimpleStrategy> list = new ArrayList<>();
		addRegularization(list);
		addBasicAlgebra(list);
		addSqrStrategies(list);
		addExpStrategies(list);
		return list;
	}
	
	public static Set<String> getDefaultTags(){
		Set<String> set = new HashSet<>();
		set.addAll(TAG_ALGEBRA_SET);
		set.addAll(TAG_PRIMARY_SET);
		return set;
	}
	
}
