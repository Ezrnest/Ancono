/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.print_;
import static cn.timelives.java.utilities.Printer.printnb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.structure.Pair;

/**
 * A node is a single unit in the expression tree. Each node has its direct reference to its 
 * parent({@code null} if it is the root node), and circular references must be avoided. Node 
 * is generally an immutable object to users, but for those who want to implement expression 
 * extensions(such as SimplifyStrategy), essential and useful methods are accessible.
 * @author liyicheng
 * 2017-11-24 17:32
 *
 */
public abstract class Node {
	private static final PolyCalculator POLY_CALCULATOR = PolyCalculator.DEFAULT_CALCULATOR;
	NodeWithChildren parent;
	/**
	 * 
	 */
	Node(NodeWithChildren parent) {
		this.parent = parent;
	}
	
	/**
	 * The type of the nodes. Each type is associated with a specific class of Node.
	 * The result why functions are divided into three parts is that they 
	 * @author liyicheng
	 * 2017-11-24 17:40
	 *
	 */
	public enum Type{
		/**
		 * A simple polynomial.
		 * <p>{@link Node.Poly}
		 */
		POLYNOMIAL,
		/**
		 * Add, which contains a polynomial and several nodes.
		 * <p>{@link Node.Add}
		 */
		ADD,
		/**
		 * Multiply, which contains a polynomial and several nodes.
		 * <p>{@link Node.Multiply}
		 */
		MULTIPLY,
		/**
		 * Fraction, which contains a node as numerator and another node as denominator
		 * <p>{@link Node.Fraction}
		 */
		FRACTION,
		/**
		 * Single-variable function such as abs,sin
		 * <p>{@link Node.SFunction}
		 */
		S_FUNCTION,
		/**
		 * A function that accepts two parameters, such as exp(x,y)
		 * <p>{@link Node.DFunction}
		 */
		D_FUNCTION,
		/**
		 * A function that accepts three or more parameters.
		 * <p>{@link Node.MFunction}
		 */
		M_FUNCTION;
		
		public static boolean isFunction(Type ty) {
			return ty == Type.S_FUNCTION || ty == Type.D_FUNCTION || ty == M_FUNCTION;
		}
	}
	
	/**
	 * Determines whether this node is equal to the other node in terms of it content it stores. Notice that this method 
	 * is not the same to {@code equals()}
	 * @param n
	 * @return
	 */
	public abstract boolean equalNode(Node n,PolyCalculator pc);
	
	public abstract Type getType();
	
	/**
	 * Returns the node's parent, if the node is a root node, then {@code null} will be 
	 * returned.
	 * @return
	 */
	public NodeWithChildren parent() {
		return parent;
	}
	
	boolean removeFromParent() {
		if(parent!=null) {
			parent.remove(this);
			parent = null;
			return true;
		}
		return false;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	protected abstract void toString(StringBuilder sb,NumberFormatter<Polynomial> nf,boolean braketRecommended);
	
	/**
	 * List this node and all it sub-nodes.
	 * @param level
	 */
	public abstract void listNode(int level);
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		toString(sb, NumberFormatter.getToStringFormatter(), false);
		return sb.toString();
	}
	
	/**
	 * Determines whether two node are equal using {@link Node#equalNode(Node)}, or they are both {@code null}.
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean nodeEquals(Node a,Node b,PolyCalculator pc) {
		 return (a == b) || (a != null && a.equalNode(b,pc));
	}
	
	static boolean polyEquals(Polynomial a,Polynomial b,PolyCalculator pc) {
		return (a == b) || (a != null && pc.isEqual(a, b));
	}
	/**
	 * Returns the clone of the node, all its child nodes will be 
	 * cloned as well. The parent of the new node will be set as given.
	 */
	public abstract Node cloneNode(NodeWithChildren parent);
	
	/**
	 * A node with children is a branch node(or root node). The node's children can 
	 * be accessed via {@link #getChildren(int)} and {@link #getChildrenList()}. 
	 *
	 * @author liyicheng
	 * 2017-11-25 18:23
	 *
	 */
	public static abstract class NodeWithChildren extends Node{

		/**
		 * @param parent
		 */
		NodeWithChildren(NodeWithChildren parent) {
			super(parent);
		}
		
		public abstract boolean contains(Node child);
		/**
		 * Replace the specific node with the new one.
		 * @param original
		 * @param replacement
		 * @return
		 */
		abstract boolean replace(Node original,Node replacement);
		
		abstract boolean remove(Node n);
		
		public abstract int getNumberOfChildren();
		/**
		 * Gets the child at the specified position.
		 * @param index
		 * @return
		 */
		public abstract Node getChildren(int index);
		/**
		 * Get a copy of the list of the children of this node.
		 * @return
		 */
		public abstract List<Node> getChildrenList();
		/**
		 * Performs the sort operation with the given Comparator<Node>.
		 * Ignores the call if this kind of node requires its children's order.
		 * @param nc
		 */
		abstract void doSort(Comparator<Node> nc);
		
		
		
	}
	
	
	
	
//	public abstract boolean equalStructure(Node n);
	public static abstract class ChildrenNode extends NodeWithChildren{
		List<Node> children;
		boolean sortable;
		ChildrenNode(NodeWithChildren parent,List<Node> children,boolean sortable) {
			super(parent);
			this.children = Objects.requireNonNull(children);
			this.sortable = sortable;
		}
		
		
		/**
		 * Gets the children.
		 * @return the children
		 */
		List<Node> getChildren() {
			return children;
		}
		
		/**
		 * Adds the child node to this father node.
		 * @param child
		 */
		void addChild(Node child) {
			children.add(child);
			child.parent = this;
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#remove(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		boolean remove(Node n) {
			return children.remove(n);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildrenList()
		 */
		@Override
		public List<Node> getChildrenList() {
			return Collections.unmodifiableList(children);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getNumberOfChildren()
		 */
		@Override
		public int getNumberOfChildren() {
			return children.size();
		}
		
		/*
		* @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildren(int)
		*/
		@Override
		public Node getChildren(int index) {
			return children.get(index);
		}
		
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChild#replace(cn.timelives.java.math.numberModels.expression.Node, cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		boolean replace(Node original, Node replacement) {
			if(replacement == null) {
				throw new NullPointerException();
			}
			int index = children.indexOf(original);
			if(index == -1) {
				return false;
			}
			children.set(index, replacement);
			return true;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#contains(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean contains(Node child) {
			return children.contains(child);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#doSort(cn.timelives.java.math.numberModels.expression.Comparator<Node>)
		 */
		@Override
		void doSort(Comparator<Node> nc) {
			if(sortable) {
				children.sort(nc);
			}
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#listNode(int)
		 */
		@Override
		public void listNode(int level) {
			print_(level,'	',true);
			print(getType());
			for(Node n : children) {
				n.listNode(level+1);
			}
		}
	}
	
	/**
	 * A MulNode contains a Polynomial and several sub-nodes.
	 * @author liyicheng
	 * 2017-11-24 17:39
	 *
	 */
	public static abstract class CombinedNode extends ChildrenNode{
		Polynomial p;
		/**
		 * @param parent
		 */
		CombinedNode(NodeWithChildren parent,Polynomial p,List<Node> children) {
			super(parent,children,true);
			this.p = p;
		}
		
		
		/**
		 * Gets the Polynomial
		 * @return the Polynomial
		 */
		public Polynomial getPolynomial() {
			return p;
		}
		
		/**
		 * Sets the p.
		 * @param p the p to set
		 */
		void setPolynomial(Polynomial p) {
			this.p = p;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#listNode(int)
		 */
		@Override
		public void listNode(int level) {
			print_(level,'	',true);
			print(getType());
			print_(level+1,'	',true);
			print(p);
			for(Node n : children) {
				n.listNode(level+1);
			}
		}
		
	}
	
	
	/**
	 * A single node only has one child.
	 * @author liyicheng
	 * 2017-11-25 18:25
	 *
	 */
	public static abstract class SingleNode extends NodeWithChildren{
		/**
		 * @param parent
		 */
		SingleNode(NodeWithChildren parent,Node child) {
			super(parent);
			this.child = child;
		}

		Node child;
		
	
		/**
		 * Gets the child.
		 * @return the child
		 */
		public Node getChild() {
			return child;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#contains(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean contains(Node child) {
			return Objects.equals(child, this.child);
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#remove(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		boolean remove(Node n) {
			Objects.requireNonNull(n);
			if (Objects.equals(child, n)) {
				child = null;
				return true;
			}
			return false;
		}

		/*
		 * @see
		 * cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#replace(
		 * cn.timelives.java.math.numberModels.expression.Node,
		 * cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean replace(Node original, Node replacement) {
			if (replacement == null) {
				throw new NullPointerException();
			}
			if(original.equals(child)) {
				child = replacement;
				return true;
			}else {
				return false;
			}
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildren(int)
		 */
		@Override
		public Node getChildren(int index) {
			if(index != 0) {
				throw new IndexOutOfBoundsException("For index="+index);
			}
			return child;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getNumberOfChildren()
		 */
		@Override
		public int getNumberOfChildren() {
			return 1;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildrenList()
		 */
		@Override
		public List<Node> getChildrenList() {
			return Collections.singletonList(child);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#doSort(cn.timelives.java.math.numberModels.expression.Comparator<Node>)
		 */
		@Override
		void doSort(Comparator<Node> nc) {
			//just one
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#listNode(int)
		 */
		@Override
		public void listNode(int level) {
			print_(level,'	',true);
			print(getType());
			child.listNode(level+1);
		}
	}
	
	public static abstract class BiNode extends NodeWithChildren{
		Node c1,c2;
		protected final boolean sortable;
		/**
		 * @param parent
		 */
		BiNode(NodeWithChildren parent,Node c1,Node c2,boolean sortable) {
			super(parent);
			this.c1 = c1;
			this.c2 = c2;
			this.sortable = sortable;
		}
		
		/**
		 * Gets the c1.
		 * @return the c1
		 */
		public Node getC1() {
			return c1;
		}
		
		/**
		 * Gets the c2.
		 * @return the c2
		 */
		public Node getC2() {
			return c2;
		}
		
		void setBoth(Node n1,Node n2) {
			c1 = n1;
			c2 = n2;
			n1.parent = this;
			n2.parent = this;
		}
	
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#contains(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean contains(Node child) {
			return Objects.equals(c1, child) ||Objects.equals(c2, child);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#remove(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		boolean remove(Node n) {
			if(n==null) {
				throw new NullPointerException();
			}
			if( Objects.equals(c1, n)) {
				c1 = null;
				return true;
			}
			if( Objects.equals(c2, n)) {
				c2 = null;
				return true;
			}
			return false;
		}

		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#replace(cn.timelives.java.math.numberModels.expression.Node, cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean replace(Node original, Node replacement) {
			if(replacement == null) {
				throw new NullPointerException();
			}
			if(c1.equals(original)) {
				c1 = replacement;
				return true;
			}
			if(c2.equals(original)) {
				c2 = replacement;
				return true;
			}
			return false;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildren(int)
		 */
		@Override
		public Node getChildren(int index) {
			if(index < 0 || index >1) {
				throw new IndexOutOfBoundsException("For index="+index);
			}
			return index == 0 ? c1 : c2;
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildrenList()
		 */
		@Override
		public List<Node> getChildrenList() {
			return Arrays.asList(c1,c2);
		}
		
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getNumberOfChildren()
		 */
		@Override
		public int getNumberOfChildren() {
			return 2;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#doSort(cn.timelives.java.math.numberModels.expression.Comparator<Node>)
		 */
		@Override
		void doSort(Comparator<Node> nc) {
			if(sortable) {
				int comp = nc.compare(c1, c2);
				if(comp>0) {
					Node t = c1;
					c1 = c2;
					c2 = t;
				}
			}
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#listNode(int)
		 */
		@Override
		public void listNode(int level) {
			print_(level,'	',true);
			print(getType());
			c1.listNode(level+1);
			c2.listNode(level+1);
		}
	}
	/**
	 * A polynomial node which only contains a polynomial. 
	 * @author liyicheng
	 * 2017-11-25 20:28
	 *
	 */
	public static final class Poly extends Node{
		final Polynomial p;
		Poly(NodeWithChildren parent,Polynomial p) {
			super(parent);
			this.p = Objects.requireNonNull(p); 
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof Poly)) {
				return false;
			}
			Poly po = (Poly)n;
			return pc.isEqual(p, po.p);
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.POLYNOMIAL;
		}
		
		/**
		 * Gets the Polynomial.
		 * @return the Polynomial
		 */
		public Polynomial getPolynomial() {
			return p;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#clone()
		 */
		@Override
		public Poly cloneNode(NodeWithChildren parent) {
			return new Poly(parent, p);
		}
		
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString()
		 */
		@Override
		public String toString() {
			return p.toString();
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#listNode(java.io.PrintStream, int)
		 */
		@Override
		public void listNode(int level) {
			print_(level,'	',true);
			printnb("Poly:");
			print(p);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public void toString(StringBuilder sb, NumberFormatter<Polynomial> nf,boolean braketRecommended) {
			String str = nf.format(p, POLY_CALCULATOR);
			if(str.length() == 1) {
				braketRecommended = false;
			}
			if(braketRecommended) {
				sb.append('(');
			}
			sb.append(str);
			if(braketRecommended) {
				sb.append(')');
			}
		}
	}
	/**
	 * Describes the add operation.
	 * @author liyicheng
	 * 2017-11-25 20:28
	 *
	 */
	public static final class Add extends CombinedNode{
		/**
		 * @param parent
		 * @param p
		 * @param children
		 */
		Add(NodeWithChildren parent, Polynomial p, List<Node> children) {
			super(parent, p, children);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.ADD;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof Add)) {
				return false;
			}
			Add node = (Add)n;
			return polyEquals(p, node.p, pc) && CollectionSup.listEqual(children, node.children, (x,y)->x.equalNode(y,pc));
		}
		
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public Add cloneNode(NodeWithChildren parent) {
			List<Node> nchildren = new ArrayList<>(getNumberOfChildren());
			Add clone = new Add(parent,p,nchildren);
			for(Node n : children) {
				nchildren.add(n.cloneNode(clone));
			}
			return clone;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public void toString(StringBuilder sb, NumberFormatter<Polynomial> nf,boolean braketRecommended) {
			if(braketRecommended) {
				sb.append('(');
			}
			if(p != null) {
//				if(p.equals(Polynomial.ZERO)) {
//					//should be simplified by the tree to set the polynomial to null
//				}
				sb.append(nf.format(p, POLY_CALCULATOR));
				sb.append('+');
			}
			for(Node n : children) {
				n.toString(sb, nf,false);
				sb.append('+');
			}
			sb.deleteCharAt(sb.length()-1);
			if(braketRecommended) {
				sb.append(')');
			}
		}
	}
	public static final class Multiply extends CombinedNode{
		/**
		 * @param parent
		 * @param p
		 * @param children
		 */
		Multiply(NodeWithChildren parent, Polynomial p, List<Node> children) {
			super(parent, p, children);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.MULTIPLY;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof Multiply)) {
				return false;
			}
			Multiply node = (Multiply)n;
			return polyEquals(p, node.p, pc) && CollectionSup.listEqual(children, node.children, (x,y)->x.equalNode(y,pc));
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public Multiply cloneNode(NodeWithChildren parent) {
			List<Node> nchildren = new ArrayList<>(getNumberOfChildren());
			Multiply clone = new Multiply(parent,p,nchildren);
			for(Node n : children) {
				nchildren.add(n.cloneNode(clone));
			}
			return clone;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.NumberFormatter, boolean)
		 */
		@Override
		public void toString(StringBuilder sb, NumberFormatter<Polynomial> nf, boolean braketRecommended) {
//			if(braketRecommended) {
//				sb.append('(');
//			}
			if(p != null) {
				if(!p.equals(Polynomial.ONE)) {
					//should be simplified by the tree to set the polynomial to null
				}
				if (p.equals(Polynomial.NEGATIVE_ONE)) {
					sb.append('-');
				} else {
					sb.append('(');
					sb.append(nf.format(p, POLY_CALCULATOR));
					sb.append(')');
					sb.append('*');
				}
				
			}
			for(Node n : children) {
				n.toString(sb, nf,true);
				sb.append('*');
			}
			sb.deleteCharAt(sb.length()-1);
//			if(braketRecommended) {
//				sb.append('(');
//			}
			
		}
		
	}
	static interface FunctionNode{
		String getFunctionName();
	}
	/**
	 * The node contains a single-parameter function.
	 * @author liyicheng
	 * 2017-11-26 13:13
	 *
	 */
	public static final class SFunction extends SingleNode implements FunctionNode{
		

		final String functionName;
		
		/**
		 * @param parent
		 * @param child
		 */
		SFunction(NodeWithChildren parent, Node child,String name) {
			super(parent, child);
			this.functionName = Objects.requireNonNull(name);
		}
		
		/**
		 * Gets the functionName.
		 * @return the functionName
		 */
		public String getFunctionName() {
			return functionName;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof SFunction)) {
				return false;
			}
			SFunction node = (SFunction)n;
			return functionName.equals(node.functionName)&&
					nodeEquals(child, node.child,pc);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.S_FUNCTION;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public SFunction cloneNode(NodeWithChildren parent) {
			Node nchild = child.cloneNode(null);
			SFunction clone = new SFunction(parent, nchild, functionName);
			nchild.parent = clone;
			return clone;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.NumberFormatter, boolean)
		 */
		@Override
		protected void toString(StringBuilder sb, NumberFormatter<Polynomial> nf, boolean braketRecommended) {
			sb.append(functionName).append('(');
			child.toString(sb, nf, false);
			sb.append(')');
		}
		
	}
	
	public static final class DFunction extends BiNode implements FunctionNode{

		/**
		 * @param parent
		 * @param c1
		 * @param c2
		 */
		DFunction(NodeWithChildren parent, Node c1, Node c2,String name,boolean sortable) {
			super(parent, c1, c2,sortable);
			this.functionName = name;
		}

		final String functionName;
		
		/**
		 * Gets the functionName.
		 * @return the functionName
		 */
		public String getFunctionName() {
			return functionName;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof DFunction)) {
				return false;
			}
			DFunction node = (DFunction)n;
			return functionName.equals(node.functionName)&&
					nodeEquals(c1,node.c1,pc)&&
					nodeEquals(c2,node.c2,pc);
		}

		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.S_FUNCTION;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public DFunction cloneNode(NodeWithChildren parent) {
			Node nc1 = c1.cloneNode(null),
					nc2 = c2.cloneNode(null);
			DFunction clone = new DFunction(parent, c1,c2, functionName,sortable);
			nc1.parent = clone;
			nc2.parent = clone;
			return clone;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.NumberFormatter, boolean)
		 */
		@Override
		protected void toString(StringBuilder sb, NumberFormatter<Polynomial> nf, boolean braketRecommended) {
			sb.append(functionName).append('(');
			c1.toString(sb, nf, false);
			sb.append(',');
			c2.toString(sb, nf, false);
			sb.append(')');
		}
		
	}
	
	public static final class MFunction extends ChildrenNode implements FunctionNode{
		final String functionName;
		/**
		 * @param parent
		 * @param children
		 * @param sortable
		 */
		MFunction(NodeWithChildren parent, List<Node> children, String name,boolean sortable) {
			super(parent, children, sortable);
			this.functionName = name;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.M_FUNCTION;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof MFunction)) {
				return false;
			}
			MFunction node = (MFunction)n;
			if(! functionName.equals(node.functionName)) {
				return false;
			}
			return CollectionSup.listEqual(children, node.children, (x,y)->x.equalNode(y,pc));

		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public MFunction cloneNode(NodeWithChildren parent) {
			List<Node> nchildren = new ArrayList<>(getNumberOfChildren());
			MFunction clone = new MFunction(parent,nchildren,functionName,sortable);
			for(Node n : children) {
				nchildren.add(n.cloneNode(clone));
			}
			return clone;
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.NumberFormatter, boolean)
		 */
		@Override
		protected void toString(StringBuilder sb, NumberFormatter<Polynomial> nf, boolean braketRecommended) {
			sb.append(functionName).append('(');
			for(Node n : children) {
				n.toString(sb, nf, false);
				sb.append(',');
			}
			sb.setCharAt(sb.length()-1, ')');
		}

		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.FunctionNode#getFunctionName()
		 */
		@Override
		public String getFunctionName() {
			return functionName;
		}
	}
	
	public static final class Fraction extends BiNode{

		/**
		 * @param parent
		 * @param c1
		 * @param c2
		 * @param sortable
		 */
		Fraction(NodeWithChildren parent, Node c1, Node c2) {
			super(parent, c1, c2, false);
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.FRACTION;
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof Fraction)) {
				return false;
			}
			Fraction node = (Fraction)n;
			return nodeEquals(c1, node.c1,pc) &&
					nodeEquals(c2, node.c2,pc);

		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public Fraction cloneNode(NodeWithChildren parent) {
			Node nc1 = c1.cloneNode(null),
					nc2 = c2.cloneNode(null);
			Fraction clone = new Fraction(parent, c1,c2);
			nc1.parent = clone;
			nc2.parent = clone;
			return clone;
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.NumberFormatter, boolean)
		 */
		@Override
		protected void toString(StringBuilder sb, NumberFormatter<Polynomial> nf, boolean braketRecommended) {
			if(braketRecommended) {
				sb.append('(');
			}
			c1.toString(sb, nf, true);
			sb.append('/');
			c2.toString(sb, nf, braketRecommended);
			if(braketRecommended) {
				sb.append(')');
			}
		}
	}
	
	public static Poly newPolyNode(Polynomial p,NodeWithChildren parent) {
		return new Poly(parent, p);
	}
	
	
	static boolean replaceChildNode(Node n,Node replacement) {
		if(n.parent!=null) {
			return n.parent.replace(n, replacement);
		}
		return false;
	}
	
	/**
	 * Wraps the nodes' clones with either Add or Multiply. The newly created node has no parent node.
	 * @param isAdd
	 * @param n
	 * @return
	 */
	public static Node wrapCloneNodeAM(boolean isAdd, Node n1,Node n2) {
		NodeWithChildren root;
		if(isAdd) {
			Add add = new Add(null, null, new ArrayList<>(2));
			add.addChild(n1.cloneNode(add));
			add.addChild(n2.cloneNode(add));
			root = add;
		}else {
			Multiply mul = new Multiply(null, null, new ArrayList<>(2));
			mul.addChild(n1.cloneNode(mul));
			mul.addChild(n2.cloneNode(mul));
			root = mul;
		}
		return root;
	}
	/**
	 * Wraps the nodes' clones with either Add or Multiply. The newly created node has no parent node.
	 * @param isAdd
	 * @param n
	 * @return
	 */
	public static Node wrapCloneNodeAM(boolean isAdd, List<Node> ns) {
		NodeWithChildren root;
		List<Node> list = new ArrayList<>(ns.size());
		if(isAdd) {
			root = new Add(null, null, new ArrayList<>(2));
		}else {
			root = new Multiply(null, null, new ArrayList<>(2));
		}
		for(Node n : ns) {
			list.add(n.cloneNode(root));
		}
		return root;
	}
	
	/**
	 * Wraps the nodes' with either Add or Multiply. The newly created node has no parent node.
	 * This method will try to clean the nodes original link to their parent node 
	 * @param isAdd
	 * @param n
	 * @return
	 */
	public static Node wrapNodeAM(boolean isAdd, Node n1,Node n2) {
		NodeWithChildren root;
		n1.removeFromParent();
		n2.removeFromParent();
		List<Node> list = new ArrayList<>(2);
		list.add(n1);
		list.add(n2);
		if(isAdd) {
			root = new Add(null, null, list);
		}else {
			root = new Multiply(null, null, list);
		}
		n1.parent = root;
		n2.parent = root;
		return root;
	}
	
	/**
	 * Returns {@code n*x} without any simplification.
	 * @param n
	 * @param x
	 * @return
	 */
	public static Multiply wrapCloneNodeMultiply(Node n,Polynomial x) {
		List<Node> list = new ArrayList<>(1);
		Multiply nroot = new Multiply(null, x, list);
		Node rt = n.cloneNode(nroot);
		list.add(rt);
		return nroot;
	}
	

	/**
	 * Returns {@code n*x} without any simplification.
	 * <p>The node has no parent!
	 * @param n
	 * @param x
	 * @return
	 */
	public static Multiply wrapNodeMultiply(Node n,Polynomial x) {
		n.removeFromParent();
		List<Node> list = new ArrayList<>(1);
		Multiply nroot = new Multiply(null, x, list);
		list.add(n);
		n.parent = nroot;
		return nroot;
	}
	
	/**
	 * Returns {@code n+x} without any simplification.
	 * @param n
	 * @param x
	 * @return
	 */
	public static Add wrapCloneNodeAdd(Node n,Polynomial x) {
		List<Node> list = new ArrayList<>(1);
		Add nroot = new Add(null, x, list);
		Node rt = n.cloneNode(nroot);
		list.add(rt);
		return nroot;
	}
	/**
	 * 
	 * @param fname
	 * @param n
	 * @return
	 */
	public static SFunction wrapNodeSF(String fname,Node n) {
		n.removeFromParent();
		SFunction root = new SFunction(null, n, fname);
		n.parent =root;
		return root;
	}
	/**
	 * 
	 * @param fname
	 * @param n
	 * @return
	 */
	public static SFunction wrapCloneNodeSF(String fname,Node n) {
		SFunction root = new SFunction(null, null, fname);
		root.child = n.cloneNode(root);
		return root;
	}
	
	public static Fraction wrapCloneNodeFraction(Node nume,Node deno) {
		Fraction root = new Fraction(null, null,null);
		root.c1 = nume.cloneNode(root);
		root.c2 = deno.cloneNode(root);
		return root;
	}
	
	static void linkToBiNode(Node c1,Node c2,BiNode root) {
		c1.removeFromParent();
		c2.removeFromParent();
		c1.parent = root;
		c2.parent = root;
	}
	
	public static Fraction wrapNodeFraction(Node nume,Node deno) {
		Fraction root = new Fraction(null, nume,deno);
		linkToBiNode(nume,deno,root);
		return root;
	}
	
	public static boolean isPolynomial(Node n ) {
		return n.getType() == Type.POLYNOMIAL;
	}
	
	public static Poly toPolynomial(Node n) {
		return (Poly)n;
	}
	
	public static Polynomial getPolynomialOrZero(CombinedNode node,ExprCalculator ec) {
		Polynomial p = node.p;
		if(p == null) {
			return ec.pZero;
		}
		return p;
	}
	
	/**
	 * Returns the name of the function of the node if the node is a function node, or returns {@code null}.
	 * @param n
	 * @return
	 */
	public static String getFunctionName(Node n) {
		if(n instanceof FunctionNode) {
			return ((FunctionNode)n).getFunctionName();
		}
		return null;
	}
	
	public static DFunction wrapCloneNodeDF(String fname,Node n1,Node n2) {
		DFunction root = new DFunction(null,null,null,fname,false);
		root.c1 = n1.cloneNode(root);
		root.c2 = n2.cloneNode(root);
		return root;
	}
	
	public static DFunction wrapNodeDF(String fname,Node n1,Node n2) {
		DFunction root = new DFunction(null, n1,n2, fname,false);
		linkToBiNode(n1,n2,root);
		return root;
	}
	
	public static Pair<Polynomial,Node> unwrapMultiply(Node node,ExprCalculator ec){
		if(node.getType() != Type.MULTIPLY) {
			return null;
		}
		Multiply m = (Multiply) node;
		if(m.getNumberOfChildren()==1) {
			Polynomial p = m.p;
			if(p==null) {
				p = ec.pOne;
			}
			return new Pair<Polynomial, Node>(p, m.getChildren(0));
		}
		return null;
	}
	
}
