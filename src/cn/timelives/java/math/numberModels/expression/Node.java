/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.MultinomialCalculator;
import cn.timelives.java.math.numberModels.Term;
import cn.timelives.java.math.numberModels.api.Computable;
import cn.timelives.java.math.numberModels.api.NumberFormatter;
import cn.timelives.java.math.numberModels.expression.anno.AllowModify;
import cn.timelives.java.math.numberModels.expression.anno.DisallowModify;
import cn.timelives.java.math.numberModels.expression.simplification.SimplificationStrategy;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.structure.Pair;
import org.jetbrains.annotations.Nullable;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;

import static cn.timelives.java.utilities.Printer.*;

/**
 * <h3>General description</h3>
 * A node is a single unit in the expression tree. Each node has its direct reference to its
 * parent({@code null} if it is the root node), and circular references must be avoided. Node
 * is generally an immutable object to users, but for those who want to implement expression
 * extensions(such as SimplifyStrategy), essential and useful methods are accessible.
 * <h3>Implementation</h3>
 * (For users who want to implement {@link SimplificationStrategy}.)<br>
 * In addition to its parent, a node also holds an integer value as its simplification
 * identifier. An ExprCulculator has it unique simplification identifier which will be set
 * to the nodes that it has fully simplified, and the calculator will not try to simplify nodes with it
 * simplification identifier to speed the simplification. Therefore, if a node is modified and it may be
 * simplified again, the method {@link #resetSimIdentifier()} should be called.
 * <p>
 * <h3>Notes for mutability</h3>
 * First, Expression must be immutable for users. That is, whatever method is called to an Expression, it should
 * always return a new value. However, Node is mutable because usually simplification and other basic processes, which
 * is not visible to users of Expression, modifies little parts of the Expression and it may cause greatly to maintain
 * immutability. Methods that use Node are recommended to declare the mutability of its node parameters explicitly.
 * Those who
 *
 * @author liyicheng
 * 2017-11-24 17:32
 */
public abstract class Node implements Computable, Serializable {
    private static final MultinomialCalculator POLY_CALCULATOR = Multinomial.getCalculator();
    NodeWithChildren parent;
    /**
     * An identifier indicating the simplifier used previously.
     */
    transient int simIdentifier;

    /**
     *
     */
    Node(NodeWithChildren parent) {
        this.parent = parent;
    }

    /**
     * Determines whether this node is equal to the other node in terms of it content it stores. Notice that this method
     * is not the identity to {@code equals()}.
     *
     * @param n
     * @return
     */
    public abstract boolean equalNode(Node n, MultinomialCalculator pc);

    public abstract Type getType();

    /**
     * Returns the node's parent, if the node is a root node, then {@code null} will be
     * returned.
     *
     * @return
     */
    public NodeWithChildren parent() {
        return parent;
    }

    boolean removeFromParent() {
        if (parent != null) {
            parent.remove(this);
            parent = null;
            return true;
        }
        return false;
    }

    /**
     * Resets the simplification identifier of this node.
     */
    public void resetSimIdentifier() {
        simIdentifier = 0;
    }

    /*
     * @see java.lang.Object#toString()
     */
    protected abstract void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended);

    /**
     * List this node and all it sub-nodes.
     *
     * @param level
     */
    public abstract void listNode(int level);

    /**
     * Applies the recursion operation to the node, this method can possibly modify
     * the node structure instead of creating a copy and is designed for calculator
     * performing operations such as simplification.
     * <p>
     * The function will be recursively applied to all the child nodes of the node in
     * deep-first order, and then be applied to the node itself.
     * <p></p>
     * For example, {@code node.recurApply(x -> {print(x);return x},Integer.MAX_VALUE)}
     * lists all the nodes.
     * <P></P>
     * <p>
     * The method should return a Node that will be the replacement of the original node.
     *
     * @param f     the function that should be applied to the node
     * @param depth the depth of the recursion
     * @return the replacement of this node.
     */
    public abstract Node recurApply(Function<Node, Node> f, int depth);

    /**
     * Applies the recursion operation to the node, this method shouldn't modify the node.
     * <p>
     * The function will be recursively applied to all the child nodes of the node in
     * deep-first order, and then be applied to the node itself.
     * <p></p>
     * For example, {@code node.recurApply(x -> print(x),Integer.MAX_VALUE)}
     * lists all the nodes.
     * <P></P>
     * <p>
     * @param f     the function that should be applied to the node
     * @param depth the depth of the recursion
     */
    public abstract void recurApplyConsumer(Consumer<Node> f, int depth);

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
     * Returns the deep clone of the node, all its child nodes will be
     * cloned as well. The parent of the new node will be set as given.
     */
    public abstract Node cloneNode(NodeWithChildren parent);

    public Node cloneNode(){
        return cloneNode(null);
    }

    /**
     * The type of the nodes. Each type is associated with a specific class of Node.
     * The result why functions are divided into three parts is that they
     *
     * @author liyicheng
     * 2017-11-24 17:40
     */
    public enum Type {
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

    public interface FunctionNode {
        String getFunctionName();

        int getParameterLength();
    }



    /**
     * A node with children is a branch node(or root node). The node's children can
     * be accessed via {@link #getChildren(int)} and {@link #getChildrenList()}.
     *
     * @author liyicheng
     * 2017-11-25 18:23
     */
    public static abstract class NodeWithChildren extends Node {

        /**
         * @param parent
         */
        NodeWithChildren(NodeWithChildren parent) {
            super(parent);
        }

        public abstract boolean contains(Node child);

        /**
         * Replace the specific node with the new one.
         *
         * @param original
         * @param replacement
         * @return
         */
        abstract boolean replace(Node original, Node replacement);

        abstract boolean remove(Node n);

        public abstract int getNumberOfChildren();

        /**
         * Gets the child at the specified position.
         *
         * @param index
         * @return
         */
        public abstract Node getChildren(int index);

        /**
         * Get a copy of the list of the children of this node.
         *
         * @return
         */
        public abstract List<Node> getChildrenList();

        public List<Node> getChildrenListCopy(){
            return new ArrayList<>(getChildrenList());
        }

        /**
         * Performs the sort operation with the given Comparator<Node>.
         * Ignores the call if this kind of node requires its children's order.
         *
         * @param nc
         */
        abstract void doSort(Comparator<Node> nc);

        public abstract boolean containMatches(Predicate<Node> test);
    }

    /**
     * A children node is a node which children as a list.
     *
     * @author liyicheng
     * 2017-12-01 19:10
     */
    public static abstract class ListChildNode extends NodeWithChildren {
        List<Node> children;
        boolean sortable;

        ListChildNode(NodeWithChildren parent, List<Node> children, boolean sortable) {
            super(parent);
            this.children = Objects.requireNonNull(children);
            this.sortable = sortable;
        }


        /**
         * Gets the children.
         *
         * @return the children
         */
        List<Node> getChildren() {
            return children;
        }

        /**
         * Adds the child node to this father node.
         *
         * @param child
         */
        void addChild(Node child) {
            children.add(child);
            child.parent = this;
            resetSimIdentifier();
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#remove(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        boolean remove(Node n) {
            boolean b = children.remove(n);
            if (b) {
                resetSimIdentifier();
            }
            return b;
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
            if (replacement == null) {
                throw new NullPointerException();
            }
            int index = children.indexOf(original);
            if (index == -1) {
                return false;
            }
            children.set(index, replacement);
            resetSimIdentifier();
            return true;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#contains(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public boolean contains(Node child) {
            return children.contains(child);
        }

        @Override
        public boolean containMatches(Predicate<Node> test) {
            for (Node n : children) {
                if (test.test(n)) {
                    return true;
                }
            }
            return false;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#doSort(cn.timelives.java.math.numberModels.expression.Comparator<Node>)
         */
        @Override
        void doSort(Comparator<Node> nc) {
            if (sortable) {
                children.sort(nc);
                resetSimIdentifier();
            }
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#listNode(int)
         */
        @Override
        public void listNode(int level) {
            print_(level, '	', true);
            print(getType());
            for (Node n : children) {
                n.listNode(level + 1);
            }
        }

        @Override
        public Node recurApply(Function<Node, Node> f, int depth) {
            List<Node> children = this.children;
            if (depth > 0) {
                depth--;
                for (ListIterator<Node> lit = children.listIterator(); lit.hasNext(); ) {
                    Node t = lit.next();
                    Node nt = t.recurApply(f, depth);
                    if (nt != t) {
                        lit.set(nt);
                        nt.parent = this; // Set the parent
                    }
                }
            }
            return f.apply(this);
        }

        @Override
        public void recurApplyConsumer(Consumer<Node> f, int depth) {
            if (depth > 0) {
                depth--;
                for(Node n : children){
                    n.recurApplyConsumer(f,depth);
                }
            }
            f.accept(this);
        }
    }

    /**
     * A MulNode contains a Multinomial and several sub-nodes.
     *
     * @author liyicheng
     * 2017-11-24 17:39
     */
    public static abstract class CombinedNode extends ListChildNode {
        Multinomial p;

        /**
         * @param parent
         */
        CombinedNode(NodeWithChildren parent, Multinomial p, List<Node> children) {
            super(parent, children, true);
            this.p = p;
        }


        /**
         * Gets the Multinomial
         *
         * @return the Multinomial
         */
        @Nullable
        public Multinomial getPolynomial() {
            return p;
        }

        /**
         * Sets the p.
         *
         * @param p the p to set
         */
        void setPolynomial(Multinomial p) {
            this.p = p;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#listNode(int)
         */
        @Override
        public void listNode(int level) {
            print_(level, '	', true);
            print(getType());
            print_(level + 1, '	', true);
            print(p);
            for (Node n : children) {
                n.listNode(level + 1);
            }
        }

    }

    /**
     * A single node only has one child.
     *
     * @author liyicheng
     * 2017-11-25 18:25
     */
    public static abstract class SingleNode extends NodeWithChildren {
        Node child;

        /**
         * @param parent
         */
        SingleNode(NodeWithChildren parent, Node child) {
            super(parent);
            this.child = child;
        }

        /**
         * Gets the child.
         *
         * @return the child
         */
        public Node getChild() {
            return child;
        }


        @Override
        public Node recurApply(Function<Node, Node> f, int depth) {
            if (depth > 0) {
                child = child.recurApply(f, depth - 1);
            }
            child.parent = this;
            return f.apply(this);
        }

        @Override
        public void recurApplyConsumer(Consumer<Node> f, int depth) {
            if(depth > 0){
                child.recurApplyConsumer(f,depth-1);
            }
            f.accept(this);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#contains(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public boolean contains(Node child) {
            return Objects.equals(child, this.child);
        }

        @Override
        public boolean containMatches(Predicate<Node> test) {
            return test.test(child);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#remove(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        boolean remove(Node n) {
            Objects.requireNonNull(n);
            if (Objects.equals(child, n)) {
                child = null;
                resetSimIdentifier();
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
            if (original.equals(child)) {
                child = replacement;
                resetSimIdentifier();
                return true;
            } else {
                return false;
            }
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildren(int)
         */
        @Override
        public Node getChildren(int index) {
            if (index != 0) {
                throw new IndexOutOfBoundsException("For index=" + index);
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
            print_(level, '	', true);
            print(getType());
            child.listNode(level + 1);
        }
    }

    public static abstract class BiNode extends NodeWithChildren {
        protected final boolean sortable;
        Node c1, c2;

        /**
         * @param parent
         */
        BiNode(NodeWithChildren parent, Node c1, Node c2, boolean sortable) {
            super(parent);
            this.c1 = c1;
            this.c2 = c2;
            this.sortable = sortable;
        }

        /**
         * Gets the c1.
         *
         * @return the c1
         */
        public Node getC1() {
            return c1;
        }

        /**
         * Gets the c2.
         *
         * @return the c2
         */
        public Node getC2() {
            return c2;
        }

        @Override
        public Node recurApply(Function<Node, Node> f, int depth) {
            if (depth > 0) {
                c1 = c1.recurApply(f, depth - 1);
                try {
                    var t = c2.recurApply(f, depth - 1);
                    Objects.requireNonNull(t);
                    c2 = t;
                } catch (Exception e) {

                    e.printStackTrace();
                }
                c1.parent = c2.parent = this;
            }
            return f.apply(this);
        }

        @Override
        public void recurApplyConsumer(Consumer<Node> f, int depth) {
            if(depth > 0){
                c1.recurApplyConsumer(f, depth - 1);
                c2.recurApplyConsumer(f, depth - 1);
            }
            f.accept(this);
        }

        void setBoth(Node n1, Node n2) {
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
            return Objects.equals(c1, child) || Objects.equals(c2, child);
        }

        @Override
        public boolean containMatches(Predicate<Node> test) {
            return test.test(c1) || test.test(c2);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#remove(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        boolean remove(Node n) {
            if (n == null) {
                throw new NullPointerException();
            }
            if (Objects.equals(c1, n)) {
                c1 = null;
                resetSimIdentifier();
                return true;
            }
            if (Objects.equals(c2, n)) {
                c2 = null;
                resetSimIdentifier();
                return true;
            }
            return false;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#replace(cn.timelives.java.math.numberModels.expression.Node, cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public boolean replace(Node original, Node replacement) {
            if (replacement == null) {
                throw new NullPointerException();
            }
            if (c1.equals(original)) {
                c1 = replacement;
                resetSimIdentifier();
                return true;
            }
            if (c2.equals(original)) {
                c2 = replacement;
                resetSimIdentifier();
                return true;
            }
            return false;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildren(int)
         */
        @Override
        public Node getChildren(int index) {
            if (index < 0 || index > 1) {
                throw new IndexOutOfBoundsException("For index=" + index);
            }
            return index == 0 ? c1 : c2;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.NodeWithChildren#getChildrenList()
         */
        @Override
        public List<Node> getChildrenList() {
            return Arrays.asList(c1, c2);
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
            if (sortable) {
                int comp = nc.compare(c1, c2);
                if (comp > 0) {
                    Node t = c1;
                    c1 = c2;
                    c2 = t;
                }
                resetSimIdentifier();
            }
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#listNode(int)
         */
        @Override
        public void listNode(int level) {
            print_(level, '	', true);
            print(getType());
            c1.listNode(level + 1);
            c2.listNode(level + 1);
        }
    }

    /**
     * A polynomial node which only contains a polynomial.
     *
     * @author liyicheng
     * 2017-11-25 20:28
     */
    public static final class Poly extends Node {
        final Multinomial p;

        Poly(NodeWithChildren parent, Multinomial p) {
            super(parent);
            this.p = Objects.requireNonNull(p);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public boolean equalNode(Node n, MultinomialCalculator pc) {
            if (!(n instanceof Poly)) {
                return false;
            }
            Poly po = (Poly) n;
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
         * Gets the Multinomial.
         *
         * @return the Multinomial
         */
        public Multinomial getPolynomial() {
            return p;
        }

        @Override
        public Node recurApply(Function<Node, Node> f, int depth) {
            return depth >= 0 ? f.apply(this) : this;
        }

        @Override
        public void recurApplyConsumer(Consumer<Node> f, int depth) {
            f.accept(this);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#clone()
         */
        @Override
        public Poly cloneNode(NodeWithChildren parent) {
            Poly re = new Poly(parent, p);
            re.simIdentifier = simIdentifier;
            return re;
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
            print_(level, '	', true);
            printnb("Poly:");
            print(p);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.api.NumberFormatter)
         */
        @Override
        public void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended) {
            String str = nf.format(p, POLY_CALCULATOR);
            if (str.length() == 1) {
                braketRecommended = false;
            }
            if (braketRecommended) {
                sb.append('(');
            }
            sb.append(str);
            if (braketRecommended) {
                sb.append(')');
            }
        }

        @Override
        public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
            return p.compute(valueMap, mc);
        }

        @Override
        public double computeDouble(ToDoubleFunction<String> valueMap) {
            return p.computeDouble(valueMap);
        }
    }

    /**
     * Describes the add operation.
     *
     * @author liyicheng
     * 2017-11-25 20:28
     */
    public static final class Add extends CombinedNode {
        /**
         * @param parent
         * @param p
         * @param children
         */
        Add(NodeWithChildren parent, Multinomial p, List<Node> children) {
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
        public boolean equalNode(Node n, MultinomialCalculator pc) {
            if (!(n instanceof Add)) {
                return false;
            }
            Add node = (Add) n;
            return polyEquals(p, node.p, pc) && CollectionSup.listEqual(children, node.children, (x, y) -> x.equalNode(y, pc));
        }


        /*
         */
        @Override
        public Add cloneNode(NodeWithChildren parent) {
            List<Node> nchildren = new ArrayList<>(getNumberOfChildren());
            Add clone = new Add(parent, p, nchildren);
            for (Node n : children) {
                nchildren.add(n.cloneNode(clone));
            }
            clone.simIdentifier = simIdentifier;
            return clone;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.api.NumberFormatter)
         */
        @Override
        public void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended) {
            if (braketRecommended) {
                sb.append('(');
            }
            if (p != null) {
//				if(p.equals(Multinomial.ZERO)) {
//					//should be simplified by the tree to set the polynomial to null
//				}
                sb.append(nf.format(p, POLY_CALCULATOR));
                sb.append('+');
            }
            for (Node n : children) {
                n.toString(sb, nf, false);
                sb.append('+');
            }
                sb.deleteCharAt(sb.length() - 1);
            if (braketRecommended) {
                sb.append(')');
            }
        }

        @Override
        public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
            T re = p.compute(valueMap, mc);
            for (Node n : children) {
                re = mc.add(re, n.compute(valueMap, mc));
            }
            return re;
        }

        @Override
        public double computeDouble(ToDoubleFunction<String> valueMap) {
            double re = p.computeDouble(valueMap);
            for (Node n : children) {
                re += n.computeDouble(valueMap);
            }
            return re;
        }
    }

    public static final class Multiply extends CombinedNode {
        /**
         * @param parent
         * @param p
         * @param children
         */
        Multiply(NodeWithChildren parent, Multinomial p, List<Node> children) {
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
        public boolean equalNode(Node n, MultinomialCalculator pc) {
            if (!(n instanceof Multiply)) {
                return false;
            }
            Multiply node = (Multiply) n;
            return polyEquals(p, node.p, pc) && CollectionSup.listEqual(children, node.children, (x, y) -> x.equalNode(y, pc));
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public Multiply cloneNode(NodeWithChildren parent) {
            List<Node> nchildren = new ArrayList<>(getNumberOfChildren());
            Multiply clone = new Multiply(parent, p, nchildren);
            for (Node n : children) {
                nchildren.add(n.cloneNode(clone));
            }
            clone.simIdentifier = simIdentifier;
            return clone;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.api.NumberFormatter, boolean)
         */
        @Override
        public void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended) {
            if (braketRecommended) {
                sb.append('(');
            }
            if (p != null) {
                if (!p.equals(Multinomial.ONE)) {
                    //should be simplified by the tree to set the polynomial to null
                }
                if (p.equals(Multinomial.NEGATIVE_ONE)) {
                    sb.append('-');
                } else {
                    sb.append('(');
                    sb.append(nf.format(p, POLY_CALCULATOR));
                    sb.append(')');
                    sb.append('*');
                }

            }
            for (Node n : children) {
                n.toString(sb, nf, true);
                sb.append('*');
            }
            sb.deleteCharAt(sb.length() - 1);
            if (braketRecommended) {
                sb.append(')');
            }

        }

        @Override
        public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
            T re = p.compute(valueMap, mc);
            for (Node n : children) {
                re = mc.multiply(re, n.compute(valueMap, mc));
            }
            return re;
        }

        @Override
        public double computeDouble(ToDoubleFunction<String> valueMap) {
            double re = p.computeDouble(valueMap);
            for (Node n : children) {
                re *= n.computeDouble(valueMap);
            }
            return re;
        }

    }

    /**
     * The node contains a single-parameter function.
     *
     * @author liyicheng
     * 2017-11-26 13:13
     */
    public static final class SFunction extends SingleNode implements FunctionNode {

        final String functionName;

        /**
         * @param parent
         * @param child
         */
        SFunction(NodeWithChildren parent, Node child, String name) {
            super(parent, child);
            this.functionName = Objects.requireNonNull(name);
        }

        /**
         * Gets the functionName.
         *
         * @return the functionName
         */
        public String getFunctionName() {
            return functionName;
        }

        @Override
        public int getParameterLength() {
            return 1;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public boolean equalNode(Node n, MultinomialCalculator pc) {
            if (!(n instanceof SFunction)) {
                return false;
            }
            SFunction node = (SFunction) n;
            return functionName.equals(node.functionName) &&
                    nodeEquals(child, node.child, pc);
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
            clone.simIdentifier = simIdentifier;
            return clone;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.api.NumberFormatter, boolean)
         */
        @Override
        protected void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended) {
            sb.append(functionName).append('(');
            child.toString(sb, nf, false);
            sb.append(')');
        }

        @Override
        public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
            T x = child.compute(valueMap, mc);
            return ExprFunction.findFunctionAndApply(mc, functionName, x);
        }

        @Override
        public double computeDouble(ToDoubleFunction<String> valueMap) {
            double x = child.computeDouble(valueMap);
            return ExprFunction.findFunctionAndApply(Calculators.getCalculatorDoubleDev(), functionName, x);
        }
    }

    public static final class DFunction extends BiNode implements FunctionNode {

        final String functionName;

        /**
         * @param parent
         * @param c1
         * @param c2
         */
        DFunction(NodeWithChildren parent, Node c1, Node c2, String name, boolean sortable) {
            super(parent, c1, c2, sortable);
            this.functionName = name;
        }

        /**
         * Gets the functionName.
         *
         * @return the functionName
         */
        public String getFunctionName() {
            return functionName;
        }

        @Override
        public int getParameterLength() {
            return 2;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#equalNode(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public boolean equalNode(Node n, MultinomialCalculator pc) {
            if (!(n instanceof DFunction)) {
                return false;
            }
            DFunction node = (DFunction) n;
            return functionName.equals(node.functionName) &&
                    nodeEquals(c1, node.c1, pc) &&
                    nodeEquals(c2, node.c2, pc);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#getType()
         */
        @Override
        public Type getType() {
            return Type.D_FUNCTION;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public DFunction cloneNode(NodeWithChildren parent) {
            Node nc1 = c1.cloneNode(null),
                    nc2 = c2.cloneNode(null);
            DFunction clone = new DFunction(parent, nc1, nc2, functionName, sortable);
            nc1.parent = clone;
            nc2.parent = clone;
            clone.simIdentifier = simIdentifier;
            return clone;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.api.NumberFormatter, boolean)
         */
        @Override
        protected void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended) {
            sb.append(functionName).append('(');
            if(c1==null){
                sb.append("null");
            }else{
                c1.toString(sb, nf, false);
            }
            sb.append(',');
            if(c2 == null){
                sb.append("null");
            }else{
                c2.toString(sb, nf, false);
            }
            sb.append(')');
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
            T p1 = c1.compute(valueMap, mc);
            T p2 = c2.compute(valueMap, mc);
            return ExprFunction.findFunctionAndApply(mc, functionName, p1, p2);
        }

        @Override
        public double computeDouble(ToDoubleFunction<String> valueMap) {
            double p1 = c1.computeDouble(valueMap);
            double p2 = c2.computeDouble(valueMap);
            return ExprFunction.findFunctionAndApply(Calculators.getCalculatorDoubleDev(), functionName, p1, p2);
        }

    }

    public static final class MFunction extends ListChildNode implements FunctionNode {
        final String functionName;

        /**
         * @param parent
         * @param children
         * @param sortable
         */
        MFunction(NodeWithChildren parent, List<Node> children, String name, boolean sortable) {
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
        public boolean equalNode(Node n, MultinomialCalculator pc) {
            if (!(n instanceof MFunction)) {
                return false;
            }
            MFunction node = (MFunction) n;
            if (!functionName.equals(node.functionName)) {
                return false;
            }
            return CollectionSup.listEqual(children, node.children, (x, y) -> x.equalNode(y, pc));

        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public MFunction cloneNode(NodeWithChildren parent) {
            List<Node> nchildren = new ArrayList<>(getNumberOfChildren());
            MFunction clone = new MFunction(parent, nchildren, functionName, sortable);
            for (Node n : children) {
                nchildren.add(n.cloneNode(clone));
            }
            clone.simIdentifier = simIdentifier;
            return clone;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.api.NumberFormatter, boolean)
         */
        @Override
        protected void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended) {
            sb.append(functionName).append('(');
            for (Node n : children) {
                n.toString(sb, nf, false);
                sb.append(',');
            }
            sb.setCharAt(sb.length() - 1, ')');
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node.FunctionNode#getFunctionName()
         */
        @Override
        public String getFunctionName() {
            return functionName;
        }

        @Override
        public int getParameterLength() {
            return children.size();
        }

        @Override
        public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
            @SuppressWarnings("unchecked")
            T[] paras = (T[]) new Object[children.size()];
            int pos = 0;
            for (Node n : children) {
                paras[pos] = n.compute(valueMap, mc);
                pos++;
            }
            return ExprFunction.findFunctionAndApply(mc, functionName, paras);
        }

    }

    public static final class Fraction extends BiNode {

        /**
         * @param parent
         * @param c1
         * @param c2
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
        public boolean equalNode(Node n, MultinomialCalculator pc) {
            if (!(n instanceof Fraction)) {
                return false;
            }
            Fraction node = (Fraction) n;
            return nodeEquals(c1, node.c1, pc) &&
                    nodeEquals(c2, node.c2, pc);

        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#cloneNode(cn.timelives.java.math.numberModels.expression.Node)
         */
        @Override
        public Fraction cloneNode(NodeWithChildren parent) {
            Node nc1 = c1.cloneNode(null),
                    nc2 = c2.cloneNode(null);
            Fraction clone = new Fraction(parent, nc1, nc2);
            nc1.parent = clone;
            nc2.parent = clone;
            clone.simIdentifier = simIdentifier;
            return clone;
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.Node#toString(java.lang.StringBuilder, cn.timelives.java.math.numberModels.api.NumberFormatter, boolean)
         */
        @Override
        protected void toString(StringBuilder sb, NumberFormatter<Multinomial> nf, boolean braketRecommended) {
            if (braketRecommended) {
                sb.append('(');
            }
            c1.toString(sb, nf, true);
            sb.append('/');
            c2.toString(sb, nf, true);
            if (braketRecommended) {
                sb.append(')');
            }
        }

        @Override
        public <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc) {
            T nu = c1.compute(valueMap, mc);
            T de = c2.compute(valueMap, mc);
            return mc.divide(nu, de);
        }

        @Override
        public double computeDouble(ToDoubleFunction<String> valueMap) {
            double nu = c1.computeDouble(valueMap);
            double de = c2.computeDouble(valueMap);
            return nu / de;
        }


    }
    /**
     * Determines whether two node are equal using {@link Node#equalNode(Node, MultinomialCalculator)}, or they are both {@code null}.
     */
    public static boolean nodeEquals(Node a, Node b, MultinomialCalculator pc) {
        return (a == b) || (a != null && a.equalNode(b, pc));
    }

    static boolean polyEquals(Multinomial a, Multinomial b, MultinomialCalculator pc) {
        return (a == b) || (a != null && pc.isEqual(a, b));
    }

    public static Poly newPolyNode(Multinomial p, NodeWithChildren parent) {
        return new Poly(parent, p);
    }

    public static Poly newPolyNode(Multinomial p) {
        return newPolyNode(p, null);
    }

    static boolean replaceChildNode(Node n, Node replacement) {
        if (n.parent != null) {
            return n.parent.replace(n, replacement);
        }
        return false;
    }

    /**
     * Wraps the nodes' clones with either Add or Multiply. The newly created node has no parent node.
     */
    public static CombinedNode wrapCloneNodeAM(boolean isAdd, Node n1, Node n2) {
        CombinedNode root;
        if (isAdd) {
            Add add = new Add(null, null, new ArrayList<>(2));
            add.addChild(n1.cloneNode(add));
            add.addChild(n2.cloneNode(add));
            root = add;
        } else {
            Multiply mul = new Multiply(null, null, new ArrayList<>(2));
            mul.addChild(n1.cloneNode(mul));
            mul.addChild(n2.cloneNode(mul));
            root = mul;
        }
        return root;
    }
    private static Node amPolyOrDefault(boolean isAdd, Multinomial p){
        if(p==null){
            if(isAdd){
                return newPolyNode(Multinomial.ZERO);
            }else{
                return newPolyNode(Multinomial.ONE);
            }
        }else{
            return newPolyNode(p);
        }
    }
    /**
     * Wraps the nodes' clones with either Add or Multiply. The newly created node has no parent node.
     */
    public static Node wrapCloneNodeAM(boolean isAdd, List<Node> ns) {
        return wrapCloneNodeAM(isAdd, ns,null);
    }

    /**
     * Wraps the nodes  with either Add or Multiply. The newly created node has no parent node.
     */
    public static Node wrapCloneNodeAM(boolean isAdd, List<Node> ns, Multinomial p) {
        if(ns.isEmpty()){
            return amPolyOrDefault(isAdd,p);
        }
        CombinedNode root;
        List<Node> list = new ArrayList<>(ns.size());
        if (isAdd) {
            root = new Add(null, null, list);
        } else {
            root = new Multiply(null, null, list);
        }
        for (Node n : ns) {
            list.add(n.cloneNode(root));
        }
        root.p = p;
        return root;
    }

    /**
     * Wraps the nodes with either Add or Multiply. The newly created node has no parent node.
     */
    public static Node wrapNodeAM(boolean isAdd, List<Node> ns) {
        return wrapNodeAM(isAdd,ns,null);
    }

    /**
     * Wraps the nodes  with either Add or Multiply. The newly created node has no parent node.
     * @return
     */
    public static Node wrapNodeAM(boolean isAdd, List<Node> ns, Multinomial p) {
        if(ns.isEmpty()){
            return amPolyOrDefault(isAdd,p);
        }
        CombinedNode root;
        if (isAdd) {
            root = new Add(null, null, ns);
        } else {
            root = new Multiply(null, null, ns);
        }
        for (Node n : ns) {
            n.parent = root;
        }
        root.p = p;
        return root;
    }

//	public abstract T recurApply(Function<Node,T> f)

    /**
     * Wraps the nodes with either Add or Multiply. The newly created node has no parent node.
     * This method will try to clean the nodes original link to their parent node
     */
    public static CombinedNode wrapNodeAM(boolean isAdd, Node n1, Node n2) {
        CombinedNode root;
        List<Node> list = new ArrayList<>(2);
        list.add(n1);
        list.add(n2);
        if (isAdd) {
            root = new Add(null, null, list);
        } else {
            root = new Multiply(null, null, list);
        }
        n1.parent = root;
        n2.parent = root;
        return root;
    }

    /**
     * Returns {@code n*x} without any simplification.
     *
     * @param n
     * @param x
     * @return
     */
    public static Multiply wrapCloneNodeMultiply(Node n, Multinomial x) {
        List<Node> list = new ArrayList<>(1);
        Multiply nroot = new Multiply(null, x, list);
        Node rt = n.cloneNode(nroot);
        list.add(rt);
        return nroot;
    }

    /**
     * Returns {@code n*x} without any simplification.
     * <p>The node has no parent!
     *
     * @param n
     * @param x
     * @return
     */
    public static Multiply wrapNodeMultiply(Node n, Multinomial x) {
        n.removeFromParent();
        List<Node> list = new ArrayList<>(1);
        Multiply nroot = new Multiply(null, x, list);
        list.add(n);
        n.parent = nroot;
        return nroot;
    }

    /**
     * Returns {@code n+x} without any simplification.
     * <p>The node has no parent!
     *
     * @param n
     * @param x
     * @return
     */
    public static Add wrapNodeAdd(Node n, Multinomial x) {
        n.removeFromParent();
        List<Node> list = new ArrayList<>(1);
        Add nroot = new Add(null, x, list);
        list.add(n);
        n.parent = nroot;
        return nroot;
    }

    /**
     * Returns {@code n+x} without any simplification.
     *
     * @param n
     * @param x
     * @return
     */
    public static Add wrapCloneNodeAdd(Node n, Multinomial x) {
        List<Node> list = new ArrayList<>(1);
        Add nroot = new Add(null, x, list);
        Node rt = n.cloneNode(nroot);
        list.add(rt);
        return nroot;
    }

    /**
     * Wraps the node to a single function of the given name, the node will be modified (sets its parent).
     */
    public static SFunction wrapNodeSF(String fname,@AllowModify Node n) {
        n.removeFromParent();
        SFunction root = new SFunction(null, n, fname);
        n.parent = root;
        return root;
    }

    /**
     * Wraps the node to a single function of the given name, the node will be cloned.
     */
    public static SFunction wrapCloneNodeSF(String fname,@DisallowModify Node n) {
        SFunction root = new SFunction(null, null, fname);
        root.child = n.cloneNode(root);
        return root;
    }

    public static Fraction wrapCloneNodeFraction(Node nume, Node deno) {
        Fraction root = new Fraction(null, null, null);
        root.c1 = nume.cloneNode(root);
        root.c2 = deno.cloneNode(root);
        return root;
    }

    static void linkToBiNode(Node c1, Node c2, BiNode root) {
        c1.parent = root;
        c2.parent = root;
    }

    public static Fraction wrapNodeFraction(Node nume, Node deno) {
        Fraction root = new Fraction(null, nume, deno);
        linkToBiNode(nume, deno, root);
        return root;
    }

    public static boolean isPolynomial(Node n) {
        return n.getType() == Type.POLYNOMIAL;
    }

    public static boolean isPolynomial(Node n, Multinomial p, ExprCalculator ec) {
        if (n.getType() != Type.POLYNOMIAL) {
            return false;
        }
        Poly poly = (Poly) n;
        return ec.getMultinomialCalculator().isEqual(poly.p, p);
    }

    public static Poly toPolynomial(Node n) {
        return (Poly) n;
    }

    public static Multinomial getPolynomialOrDefault(CombinedNode node, ExprCalculator ec) {
        Multinomial p = node.p;
        if (p == null) {
            return node.getType() == Type.ADD ? ec.getPZero() : ec.getPOne();
        }
        return p;
    }

    /**
     * Gets the polynomial part in the node, returns {@code null} if the node doesn't
     * contain the polynomial part. If this node is Add, returns 0 if the actual part is null, and
     * if this node is Multiply, returns 1 if null.
     */
    public static Multinomial getPolynomialPart(Node node, ExprCalculator mc) {
        if (node instanceof CombinedNode) {
            CombinedNode cn = (CombinedNode) node;
            return getPolynomialOrDefault(cn, mc);
        }
        if (node.getType() == Type.POLYNOMIAL) {
            return ((Poly) node).p;
        }
        return null;
    }

    /**
     * Gets the polynomial part in the node, returns {@code null} if there is
     *
     * @param node
     * @param p
     * @return
     */
    static Node setPolynomialPart(Node node, Multinomial p) {
        if (node instanceof CombinedNode) {
            CombinedNode cn = (CombinedNode) node;
            cn.p = p;
            return node;
        }
        if (node.getType() == Type.POLYNOMIAL) {
            return newPolyNode(p, node.parent);
        }
        return node;
    }

    /**
     * Returns the name of the function of the node if the node is a function node, or returns {@code null}.
     *
     * @param n
     * @return
     */
    public static String getFunctionName(Node n) {
        if (n instanceof FunctionNode) {
            return ((FunctionNode) n).getFunctionName();
        }
        return null;
    }

    public static boolean isFunctionNode(Node n, String fname, int argumentLength) {
        if (!fname.equals(getFunctionName(n))) {
            return false;
        }
        Type ty = n.getType();
        if (ty == Type.S_FUNCTION) {
            return argumentLength == 1;
        } else if (ty == Type.D_FUNCTION) {
            return argumentLength == 2;
        } else {
            MFunction mf = (MFunction) n;
            return argumentLength == mf.getNumberOfChildren();
        }
    }

    public static DFunction wrapCloneNodeDF(String fname, Node n1, Node n2) {
        DFunction root = new DFunction(null, null, null, fname, false);
        root.c1 = n1.cloneNode(root);
        root.c2 = n2.cloneNode(root);
        if(root.c2 == null){
            print("?");
        }
        return root;
    }

    public static MFunction wrapCloneNodeMF(String fname, List<Node> nodes, boolean sortable) {
        MFunction root = new MFunction(null, CollectionSup.mapList(nodes, Node::cloneNode), fname, sortable);
        for (Node n : nodes) {
            n.parent = root;
        }
        return root;
    }

    public static DFunction wrapNodeDF(String fname, Node n1, Node n2) {
        if(n2 == null){
            print("?");
        }
        return wrapNodeDF(fname, n1, n2, false);
    }

    public static DFunction wrapNodeDF(String fname, Node n1, Node n2, boolean sortable) {
        DFunction root = new DFunction(null, n1, n2, fname, sortable);
        linkToBiNode(n1, n2, root);
        return root;
    }

    public static MFunction wrapNodeMF(String fname, List<Node> nodes, boolean sortable) {
        MFunction root = new MFunction(null, nodes, fname, sortable);
        for (Node n : nodes) {
            n.parent = root;
        }
        return root;
    }



    public static Pair<Multinomial, Node> unwrapMultiply(Node node, ExprCalculator ec) {
        if (node.getType() != Type.MULTIPLY) {
            return null;
        }
        Multiply m = (Multiply) node;
        if (m.getNumberOfChildren() == 1) {
            Multinomial p = m.p;
            if (p == null) {
                p = ec.getPOne();
            }
            return new Pair<>(p, m.getChildren(0));
        }
        return null;
    }

    static Pair<Multinomial, List<Node>> unwrapMultiplyList(Node node, ExprCalculator ec) {
        if (node.getType() != Type.MULTIPLY) {
            return null;
        }
        Multiply m = (Multiply) node;
        Multinomial p = m.p;
        if (p == null) {
            p = ec.getPOne();
        }
        return new Pair<>(p, m.children);
    }

    public static Pair<Node, BigInteger> peelExpStructure(Node node, ExprCalculator ec) {
        if (node.getType() != Type.D_FUNCTION) {
            return null;
        }
        DFunction df = (DFunction) node;
        if (!df.functionName.equals("exp")) {
            return null;
        }
        if (df.c2.getType() != Type.POLYNOMIAL) {
            return null;
        }
        BigInteger pow = Multinomial.asBigInteger(((Poly) df.c2).p);
        if (pow == null) {
            return null;
        }
        Node sub = df.c1;
        return new Pair<>(sub, pow);
    }

    static Node buildExpStructure(Node n, BigInteger pow, ExprCalculator ec) {
        if (pow.equals(BigInteger.ONE)) {
            return n;
        } else if (pow.equals(BigInteger.ZERO)) {
            return newPolyNode(ec.getPOne(), null);
        }
        Poly p = newPolyNode(Multinomial.monomial(Term.valueOf(pow)), null);
        DFunction sf = new DFunction(null, n, p, "exp", false);
        p.parent = sf;
        n.parent = sf;
        return sf;
    }
}
