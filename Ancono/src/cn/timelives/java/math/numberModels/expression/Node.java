/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.print_;
import static cn.timelives.java.utilities.Printer.printnb;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import cn.timelives.java.utilities.CollectionSup;

/**
 * A node is a single unit in the expression tree. Each node has its direct reference to its 
 * parent({@code null} if it is the root node), and circular references must be avoided.
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
	
	public enum Type{
		NUMBER,
		ADD,
		MULTIPLY,
		FRACTION,
		/**
		 * Single-variable function such as abs,sin
		 */
		S_FUNCTION,
		/**
		 * A function that accepts two parameters, such as exp(x,y)
		 */
		D_FUNCTION,
		/**
		 * A function that accepts three or more parameters.
		 */
		M_FUNCTION;
		
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
	
	
	static abstract class NodeWithChildren extends Node{

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
		
		public abstract int getNumberOfChildren();
		
		public abstract Node getChildren(int index);
		
		/**
		 * Performs the sort operation with the given NodeComparator.
		 * Ignores the call if this kind of node requires its children's order.
		 * @param nc
		 */
		abstract void doSort(NodeComparator nc);
		
		/**
		 * Get a copy of the list of the children of this node.
		 * @return
		 */
		public abstract List<Node> getChildrenList();
		
	}
	
	
	
	
//	public abstract boolean equalStructure(Node n);
	public static abstract class ChildrenNode extends NodeWithChildren{
		final List<Node> children;
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
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#doSort(cn.timelives.java.math.numberModels.expression.NodeComparator)
		 */
		@Override
		void doSort(NodeComparator nc) {
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
	
	
	
	public static abstract class SingleNode extends NodeWithChildren{
		/**
		 * @param parent
		 */
		SingleNode(NodeWithChildren parent,Node child) {
			super(parent);
			this.child = Objects.requireNonNull(child);
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
			return this.child.equals(child);
		}
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#replace(cn.timelives.java.math.numberModels.expression.Node, cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean replace(Node original, Node replacement) {
			if(replacement == null) {
				throw new NullPointerException();
			}
			if(child.equals(original)) {
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
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#doSort(cn.timelives.java.math.numberModels.expression.NodeComparator)
		 */
		@Override
		void doSort(NodeComparator nc) {
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
	
		
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#contains(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean contains(Node child) {
			return c1.equals(child) || c2.equals(child);
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
		 * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#doSort(cn.timelives.java.math.numberModels.expression.NodeComparator)
		 */
		@Override
		void doSort(NodeComparator nc) {
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
	public static final class PolyNode extends Node{
		final Polynomial p;
		PolyNode(NodeWithChildren parent,Polynomial p) {
			super(parent);
			this.p = Objects.requireNonNull(p); 
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
		 */
		@Override
		public boolean equalNode(Node n,PolyCalculator pc) {
			if(!(n instanceof PolyNode)) {
				return false;
			}
			PolyNode po = (PolyNode)n;
			return pc.isEqual(p, po.p);
		}
		/*
		 * @see cn.timelives.java.math.numberModels.expression.Node#getType()
		 */
		@Override
		public Type getType() {
			return Type.NUMBER;
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
		public PolyNode cloneNode(NodeWithChildren parent) {
			return new PolyNode(parent, p);
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
			if(braketRecommended) {
				sb.append('(');
			}
			sb.append(nf.format(p, POLY_CALCULATOR));
			if(braketRecommended) {
				sb.append(')');
			}
		}
	}
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

	public static final class SFunction extends SingleNode{
		

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
	
	public static final class DFunction extends BiNode{

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
	
	public static final class MFunction extends ChildrenNode{
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
	
	public static PolyNode newPolyNode(Polynomial p,NodeWithChildren parent) {
		return new PolyNode(parent, p);
	}
	
	static boolean replaceChildNode(Node n,Node replacement) {
		if(n.parent!=null) {
			return n.parent.replace(n, replacement);
		}
		return false;
	}
	
}
