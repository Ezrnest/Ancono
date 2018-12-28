/**
 * 2017-11-26
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.exceptions.UnsatisfiedCalculationResultException;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.MultinomialCalculator;
import cn.timelives.java.math.numberModels.expression.Node.*;
import cn.timelives.java.math.numberModels.expression.simplification.NodeHelper;
import cn.timelives.java.math.numberModels.expression.spi.SimplificationService;
import cn.timelives.java.math.numberModels.structure.Polynomial;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.structure.Pair;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.util.*;
import java.util.Map.Entry;

import static cn.timelives.java.utilities.Printer.print;


/**
 * A class provides basic simplification strategies.
 *
 * @author liyicheng 2017-11-26 15:22
 */
@SuppressWarnings("Duplicates")
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
    public static final String PROP_ENABLE_EXPAND = "enableExpand";


    /**
     * Determines whether to converts all fractions/reciprocals to exponent.<p>
     * For example, if this property is enabled, fraction <code>a/b</code> will
     * be converted to <code>a*exp(b,-1)</code> and reciprocal function <code>reciprocal(a)</code>
     * will be converted to <code>exp(a,-1)</code>.
     */
    public static final String PROP_FRACTION_TO_EXP = "fractionToExp";


    public static final String TAG_ALGEBRA = "algebra";
    public static final Set<String> TAG_ALGEBRA_SET = Collections
            .unmodifiableSet(CollectionSup.createHashSet(TAG_ALGEBRA));
    public static final Set<Type> TYPES_FUNCTION =
            CollectionSup.unmodifiableEnumSet(Type.S_FUNCTION, Type.D_FUNCTION, Type.M_FUNCTION);

    public static final Set<Type> TYPES_UNIVERSE = Collections.unmodifiableSet(EnumSet.allOf(Type.class));

    /**
     * Determines whether to regularize the expression, merging
     */
    public static final String TAG_REGULARIZE = "regularize";
    public static final Set<String> TAG_REGULARIZE_SET = Collections
            .unmodifiableSet(CollectionSup.createHashSet(TAG_REGULARIZE));

    public static final String PRIMARY_FUNCTION = "primaryFunction";
    public static final Set<String> TAG_PRIMARY_SET = Collections
            .unmodifiableSet(CollectionSup.createHashSet(PRIMARY_FUNCTION));

    public static final String TRIGONOMETRIC_FUNCTION = "trigonometricFunction";
    public static final Set<String> TAG_TRIGONOMETRIC_SET = Collections
            .unmodifiableSet(CollectionSup.createHashSet(TRIGONOMETRIC_FUNCTION));

    public static abstract class SimplifyPoly extends SimpleStrategy {
        public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.POLYNOMIAL));

        /**
         * @param description
         */
        public SimplifyPoly(String description) {
            super(TAG_REGULARIZE_SET, types, null, "Simp");
        }

    }

    public static abstract class Merge extends SimpleStrategy {

        /**
         * @param types
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
     * <p>
     * , where {@code p1} and {@code p2} are two polynomials and <i>expr</i> is any
     * type of node.
     *
     * @author liyicheng 2017-11-26 15:38
     */
    public static abstract class Collect extends SimpleStrategy {
        public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.ADD));

        /**
         *
         */
        public Collect() {
            super(TAG_ALGEBRA_SET, types, null, "Collect expressions which have a common factor,"
                    + " which is shown as a series of function node multiplied.");
        }

        public Collect(Set<String> tags, String description) {
            super(tags, types, null, description);
        }

        /*
         */
        @Override
        protected abstract Node simplifyAdd(Add node, ExprCalculator mc);
    }

    static final class SimplifyFraction extends SimpleStrategy {
        public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.ADD, Type.FRACTION, Type.MULTIPLY));

        /**
         *
         */
        SimplifyFraction() {
            super(TAG_ALGEBRA_SET, types, null, "Simplifies fraction:Add, multiply and fraction.");
        }

        /**
         * a/b + c/b -> (a+c)/b
         */
        @Override
        protected Node simplifyAdd(Add node, ExprCalculator mc) {
            List<Node> children = node.children;
            //search add
            int count = 0;
            for (Node n : children) {
                if (n.getType() == Type.FRACTION) {
                    count++;
                }
            }
            if (count < 2) {
                return null;
            }
            //try merge
            //map : (denominator, numerators)
            TreeMap<Node, List<Node>> map = new TreeMap<>(mc.getNodeComparator());
            for (Node n : children) {
                if (n.getType() == Type.FRACTION) {
                    Fraction f = (Fraction) n;
                    CollectionSup.accumulateMap(map, f.c2, f.c1, ArrayList::new);
                }
            }
            if (map.size() == count) {
                //nothing can be merged.
                return null;
            }
            //remove singles
            map.entrySet().removeIf(en -> en.getValue().size() <= 1);
            children.removeIf(x -> {
                if (x.getType() == Type.FRACTION) {
                    Fraction f = (Fraction) x;
                    return map.containsKey(f.c2);
                } else {
                    return false;
                }
            });
            for (Entry<Node, List<Node>> en : map.entrySet()) {
                Node deno = en.getKey();
                List<Node> numes = en.getValue();
                Fraction f = Node.wrapNodeFraction(Node.wrapNodeAM(true, numes), deno);
                children.add(f);
                f.parent = node;
            }
            return mc.simplify(node, 2);
        }

        /**
         * If a fraction is contained in the multiplication, then turns the multiplication into a fraction.
         */
        @Override
        protected Node simplifyMultiply(Multiply node, ExprCalculator mc) {
            List<Node> children = node.children;
            //search fraction
            int count = 0;
            for (Node n : children) {
                if (n.getType() == Type.FRACTION) {
                    count++;
                }
            }
            if (count == 0) {
                return null;
            }//TODO

            List<Node> numes = new ArrayList<>(),
                    denos = new ArrayList<>();
            for (Node n : children) {
                if (n.getType() == Type.FRACTION) {
                    Fraction f = (Fraction) n;
                    numes.add(f.c1);
                    f.c1.parent = node;
                    denos.add(f.c2);
                } else {
                    numes.add(n);
                    n.parent = node;
                }
            }
            NodeWithChildren parent = node.parent;
            node.children = numes;
            Node deno = Node.wrapNodeAM(false, denos);
            Fraction frac = Node.wrapNodeFraction(node, deno);
            frac.parent = parent;

            node.resetSimIdentifier();
            return mc.simplify(frac, 1);
        }

        /*
         */
        @Override
        protected Node simplifyFraction(Fraction node, ExprCalculator mc) {
            Node c1 = node.c1,
                    c2 = node.c2;
            Type t1 = c1.getType(),
                    t2 = c2.getType();
            if (!((t1 == Type.MULTIPLY || t1 == Type.POLYNOMIAL) && (t2 == Type.MULTIPLY || t2 == Type.POLYNOMIAL))) {
                return null;
            }
            Multinomial pnume = Node.getPolynomialPart(c1, mc),
                    pdeno = Node.getPolynomialPart(c2, mc);
            boolean sim = false;
            if (t1 == Type.MULTIPLY && t2 == Type.MULTIPLY) {
                sim = simplifyDivideNode((Multiply) c1, (Multiply) c2, mc);
            }

            List<Multinomial> list = Arrays.asList(pnume, pdeno);
            list = mc.getPolynomialSimplifier().simplify(list);
            if (!sim && mc.getMultinomialCalculator().isEqual(pnume, list.get(0))) {
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


    static boolean simplifyDivideNode(Multiply m1, Multiply m2, ExprCalculator mc) {
        List<Node> c1 = m1.children;
        List<Node> c2 = m2.children;
        Comparator<Node> nc = mc.getNodeComparator();
        m1.doSort(nc);
        m2.doSort(nc);
        boolean sim = false;
        for (ListIterator<Node> it1 = c1.listIterator(c1.size()); it1.hasPrevious(); ) {
            Node n1 = it1.previous();
            int index = Collections.binarySearch(c2, n1, nc);
            if (index > -1) {
                sim = true;
                c2.remove(index);
                it1.remove();
            }
        }
        return sim;
    }

    /**
     * Deals with the expansion of multiply when it has an Add.
     *
     * @author liyicheng
     * 2017-12-01 19:41
     */
    public static class Expand extends SimpleStrategy {
        public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.MULTIPLY));

        /**
         * Deals with the expansion of multiply when it has an Add.
         */
        public Expand() {
            super(TAG_ALGEBRA_SET, types, null);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.SimpleStrategy#simplifyMultiply(cn.timelives.java.math.numberModels.expression.Node.Multiply, cn.timelives.java.math.numberModels.expression.ExprCalculator)
         */
        @Override
        protected Node simplifyMultiply(Multiply node, ExprCalculator mc) {
            if (!Boolean.parseBoolean(mc.getProperty(PROP_ENABLE_EXPAND))) {
                return null;
            }
            Add add = null;
            List<Node> children = node.children;
            for (Node n : children) {
                if (n.getType() == Type.ADD) {
                    add = (Add) n;
                    break;
                }
            }
            if (add == null) {
                return null;
            }


            children.remove(add);
            //attach the node to add, then return
            // (x+y)*a*b*...
            //=(x*a*b*...) + (y*a*b*...)
            //expand the node
            List<Node> adds = add.children;
            List<Node> nchildren = new ArrayList<>(adds.size() + 1);

            for (Node n : adds) {
                //multiply each single node
                List<Node> factors = new ArrayList<>(children.size() + 1);
                Multiply mul = new Multiply(add, node.p, factors);
                for (Node factor : children) {
                    factor = factor.cloneNode(mul);
                    factors.add(factor);
                }
                factors.add(n);
                n.parent = mul;
                nchildren.add(mul);
            }
            if (add.p != null) {
                Poly polynode = Node.newPolyNode(add.p, null);
                List<Node> factors = new ArrayList<>(children.size() + 1);
                Multiply mul = new Multiply(add, node.p, factors);
                for (Node factor : children) {
                    factor = factor.cloneNode(mul);
                    factors.add(factor);
                }
                factors.add(polynode);
                polynode.parent = mul;
                nchildren.add(mul);
            }
            for (ListIterator<Node> it = nchildren.listIterator(); it.hasNext(); ) {
                Multiply m = (Multiply) it.next();
                //recursion
                Node result = simplifyMultiply(m, mc);
                if (result != null) {
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

    public static abstract class SimplifyFunction extends SimpleStrategy {

        /**
         * @param tags
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
     *
     * @author liyicheng
     * 2017-12-01 20:14
     */
    @SuppressWarnings("Duplicates")
    public static abstract class SimplifyMultiplyStruct extends SimpleStrategy {
        public static final Set<Type> types = Collections.unmodifiableSet(EnumSet.of(Type.MULTIPLY, Type.FRACTION));

        /**
         * @param tags assigns the tags of this multiply.
         */
        public SimplifyMultiplyStruct(Set<String> tags) {
            super(tags, types, null);
        }

        public SimplifyMultiplyStruct(Set<String> tags, String description) {
            super(tags, types, null, description);
        }

        /*
         * @see cn.timelives.java.math.numberModels.expression.SimStraImpl#simplifyMultiply(cn.timelives.java.math.numberModels.expression.Node.Multiply, cn.timelives.java.math.numberModels.expression.ExprCalculator)
         */
        @Override
        protected Node simplifyMultiply(Multiply node, ExprCalculator mc) {
            List<Node> list = node.children;
            if (!firstGlance(list, mc)) {
                return null;
            }
            List<Pair<Node, BigInteger>> collect = new ArrayList<>(list.size());
            //copy first
            list = new ArrayList<>(list);
            filterAndAdd(list, mc, false, collect);
            if (collect.isEmpty()) {
                return null;
            }
            List<Pair<Node, BigInteger>> result = simplify(collect, mc);
            if (result == null) {
                return null;
            } else {
                addToTheList(list, result, node, mc);
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
            if (firstGlance(nume, mc) || firstGlance(deno, mc)) {
                simplify = true;
            }
            simplify = shouldSimplify(mc, c1, nume, simplify);
            simplify = shouldSimplify(mc, c2, deno, simplify);
            if (!simplify) {
                return null;
            }
            List<Pair<Node, BigInteger>> collect = new ArrayList<>(nume.size() + deno.size());
            filterAndAdd(nume, mc, false, collect);
            filterAndAdd(deno, mc, true, collect);
            if (collect.isEmpty()) {
                return null;
            }
            List<Pair<Node, BigInteger>> result = simplify(collect, mc);
            if (result == null) {
                return null;
            } else {
                addToTheList(nume, deno, result, mc);
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

        private boolean shouldSimplify(ExprCalculator mc, Node c1, List<Node> nume, boolean simplify) {
            if (!simplify) {
                if (c1.getType() == Type.MULTIPLY) {
                    Multiply mul = (Multiply) c1;
                    nume.set(0, Node.newPolyNode(Node.getPolynomialOrDefault(mul, mc), null));
                    nume.addAll(mul.children);
                    if (firstGlance(nume, mc)) {
                        simplify = true;
                    }
                }
            }
            return simplify;
        }

        private static final BigInteger NEGATIVE_ONE = BigInteger.ONE.negate();

        private void filterAndAdd(List<Node> list, ExprCalculator mc, boolean deno, List<Pair<Node, BigInteger>> collect) {
            for (ListIterator<Node> it = list.listIterator(list.size()); it.hasPrevious(); ) {
                Node n = it.previous();
                BigInteger pow = deno ? NEGATIVE_ONE : BigInteger.ONE;
                if (accept(n, pow, mc)) {
                    collect.add(new Pair<>(n, pow));
                    it.remove();
                    continue;
                }
                Pair<Node, BigInteger> pair = Node.peelExpStructure(n, mc);
                if (pair != null) {
                    if (deno) {
                        pair.setSecond(pair.getSecond().negate());
                    }
                    if (accept(pair.getFirst(), pair.getSecond(), mc)) {
                        collect.add(pair);
                        it.remove();
                    }
                }
            }
        }

        private void addToTheList(List<Node> list, List<Pair<Node, BigInteger>> result, NodeWithChildren parent, ExprCalculator mc) {
            for (Pair<Node, BigInteger> en : result) {
                Node node = Node.buildExpStructure(en.getFirst(), en.getSecond(), mc);
                node.parent = parent;
                list.add(node);
            }
        }

        private void addToTheList(List<Node> nume, List<Node> deno, List<Pair<Node, BigInteger>> result, ExprCalculator mc) {
            for (Pair<Node, BigInteger> en : result) {
                BigInteger pow = en.getSecond();
                if (pow.signum() < 0) {
                    deno.add(Node.buildExpStructure(en.getFirst(), pow.negate(), mc));
                } else {
                    nume.add(Node.buildExpStructure(en.getFirst(), pow, mc));
                }

            }
        }

        protected Node buildExpStructure(Node n, BigInteger pow, ExprCalculator mc) {
            return Node.buildExpStructure(n, pow, mc);
        }


        /**
         * Quickly tests the nodes to decide whether the simplification is necessary.
         * This method is used to filter firstly.
         */
        protected abstract boolean firstGlance(List<Node> nodes, ExprCalculator ec);

        /**
         * Determines whether to accept a node that is multiplied.
         *
         * @param n
         * @param pow
         * @param ec
         * @return
         */
        protected abstract boolean accept(Node n, BigInteger pow, ExprCalculator ec);

        /**
         * Performs simplify to the list, returns null if nothing is simplified.
         *
         * @param nodes
         * @return
         */
        protected abstract List<Pair<Node, BigInteger>> simplify(List<Pair<Node, BigInteger>> nodes, ExprCalculator ec);
    }

    @SuppressWarnings("Duplicates")
    public static abstract class SimplifyAddStruct extends SimpleStrategy {
        public static final Set<Type> TYPES = CollectionSup.unmodifiableEnumSet(Type.ADD);

        /**
         * @param tags assigns the tags of this multiply.
         */
        public SimplifyAddStruct(Set<String> tags) {
            super(tags, TYPES, null);
        }

        public SimplifyAddStruct(Set<String> tags, String description) {
            super(tags, TYPES, null, description);
        }

        @Override
        protected Node simplifyAdd(Add node, ExprCalculator mc) {
            List<Node> list = node.children;
            if (!firstGlance(list, mc)) {
                return null;
            }
            List<Pair<Node, Multinomial>> collect = new ArrayList<>(list.size());
            //copy first
            list = new ArrayList<>(list);
            filterAndAdd(list, mc, collect);
            if (collect.isEmpty()) {
                return null;
            }
            List<Pair<Node, Multinomial>> result = simplify(collect, mc);
            if (result == null) {
                return null;
            }
            addToTheList(list, result, node, mc);
            node.resetSimIdentifier();
            node.children = list;
            return mc.simplify(node);

        }

        private void filterAndAdd(List<Node> list, ExprCalculator mc, List<Pair<Node, Multinomial>> collect) {
            for (ListIterator<Node> it = list.listIterator(list.size()); it.hasPrevious(); ) {
                Node n = it.previous();
                var coe = Multinomial.ONE;
                if (accept(n, coe, mc)) {
                    collect.add(new Pair<>(n, coe));
                    it.remove();
                    continue;
                }
                var pair0 = Node.unwrapMultiply(n, mc);
                if (pair0 != null) {
                    var pair = pair0.swapped();
                    if (accept(pair.getFirst(), coe, mc)) {
                        collect.add(pair);
                        it.remove();
                    }
                }
            }
        }

        private void addToTheList(List<Node> list, List<Pair<Node, Multinomial>> result, NodeWithChildren parent, ExprCalculator mc) {
            for (var en : result) {
                Node node = Node.wrapNodeMultiply(en.getFirst(), en.getSecond());
                node.parent = parent;
                list.add(node);
            }
        }


        /**
         * Simplify the list, the list can be modified. Returns null if nothing is simplified.
         * The list contains pairs of node with it coefficient.
         */
        protected abstract List<Pair<Node, Multinomial>> simplify(List<Pair<Node, Multinomial>> nodes, ExprCalculator mc);

        /**
         * Quickly tests the nodes to decide whether the simplification is necessary.
         * This method is used to filter firstly.
         */
        protected abstract boolean firstGlance(List<Node> nodes, ExprCalculator ec);

        /**
         * Determines whether to accept a node that is multiplied.
         */
        protected abstract boolean accept(Node node, Multinomial coe, ExprCalculator ec);


    }

//	public static void addRegularization(List<SimpleStrategy> list) {
//		list.add(new SimplifyPoly("Regularization") {
//			/*
//			 * @see cn.timelives.java.math.numberModels.expression.SimpleStrategy#simplifyPolynomial(cn.timelives.java.math.numberModels.expression.Node.Poly, cn.timelives.java.math.numberModels.expression.ExprCalculator)
//			 */
//			@Override
//			protected Node simplifyPolynomial(Poly node, ExprCalculator mc) {
//				return Node.newPolyNode(node.p, node.parent);
//			}
//		});
//	}

    public static void addBasicAlgebra(List<SimpleStrategy> list) {
        list.add(new Merge(CollectionSup.unmodifiableEnumSet(Type.ADD, Type.MULTIPLY, Type.FRACTION)) {
            /**
             * Flatten several Adds in an Add: (a+b)+(c+d)+(e+f) -> a+b+c+d+e+f
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
                Multinomial p = Node.getPolynomialOrDefault(node, mc);
                MultinomialCalculator pc = mc.getMultinomialCalculator();
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

            /**
             * Flatten several Multiplies in a Multiply: (a*b)*(c*d)*(e*f) -> a*b*c*d*e*f
             */
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
                Multinomial p = Node.getPolynomialOrDefault(node, mc);
                MultinomialCalculator pc = mc.getMultinomialCalculator();
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

            /**
             * Flatten fraction: (a/b)/(c/d) -> ad / bc
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
        list.add(new CollectByMultinomial());
        list.add(new SimplifyFraction());
        list.add(new CollectFraction());
        list.add(new CollectFactors());
        list.add(new Expand());

    }

    /**
     * n1*n2*a + n1*n2*b -> n1*n2*(a+b), where a,b are multinomial.
     */
    static class CollectByMultinomial extends Collect {
        /**
         * n1*n2*a + n1*n2*b -> n1*n2*(a+b), where a,b are multinomial.
         */
        CollectByMultinomial() {
            super(TAG_ALGEBRA_SET, "n1*n2*a + n1*n2*b -> n1*n2*(a+b), where a,b are multinomial.");
        }


        @Override
        protected Node simplifyAdd(Add node, ExprCalculator mc) {
            if (node.getNumberOfChildren() <= 1) {
                return null;
            }
            List<Node> children = node.children;
            TreeMap<List<Node>, Multinomial> map = new TreeMap<>(CollectionSup.collectionComparator(mc.getNodeComparator()));
            boolean collected = false;
            MultinomialCalculator pc = mc.getMultinomialCalculator();
            Multinomial pOne = mc.getPOne();
            // separate to
            for (Node n : children) {
                Pair<Multinomial, List<Node>> p = Node.unwrapMultiplyList(n, mc);
                Multinomial pn = pOne;
                List<Node> list;
                if (p != null) {
                    pn = p.getFirst();
                    list = p.getSecond();
                } else {
                    list = Collections.singletonList(n);
                }
                Multinomial poly = map.get(list);
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
            for (Entry<List<Node>, Multinomial> en : map.entrySet()) {
                Multinomial p = en.getValue();
                if (pc.isZero(p)) {
                    continue;
                }
                List<Node> list = en.getKey();
                Node n;
                if (list.size() == 1) {
                    if (!pc.isEqual(p, pOne)) {
                        n = Node.wrapNodeMultiply(list.get(0), p);
                    } else {
                        n = list.get(0);
                    }
                } else {
                    if (!pc.isEqual(p, pOne)) {
                        n = Node.wrapNodeAM(false, list, p);
                    } else {
                        n = Node.wrapNodeAM(false, list);
                    }
                }
                children.add(n);
                n.parent = node;
            }
            return mc.simplify(node, 0);
        }
    }

    /**
     * Collects: a*b*c + b*c*e -> (b*c)(a+c), requires not enableExpand
     */
    static class CollectFactors extends Collect {

        private boolean containsNode(List<Node> list, Node target, MultinomialCalculator pc) {
            for (Node m : list) {
                if (target.equalNode(m, pc)) {
                    return true;
                }
            }
            return false;
        }

        private List<Node> gcdNode(List<Node> dest, List<Node> nodes, MultinomialCalculator pc) {
            if (dest == null) {
                return new ArrayList<>(nodes);
            }
            dest.removeIf(n -> !containsNode(nodes, n, pc));
            return dest;
        }

        private List<Node> gcdNode(List<Node> gcd, Node n, MultinomialCalculator pc) {
            if (gcd == null) {
                gcd = new ArrayList<>();
                gcd.add(n);
                return gcd;
            }
            boolean contains = containsNode(gcd, n, pc);
            gcd.clear();
            if (contains) {
                gcd.add(n);
            }
            return gcd;
        }

        private Node removeFactor(Node n, List<Node> factor, MultinomialCalculator pc) {
            if (n.getType() == Type.MULTIPLY) {
                var m = (Multiply) n;
                m.children.removeIf(x -> containsNode(factor, x, pc));
                if (m.children.isEmpty()) {
                    return Node.newPolyNode(Objects.requireNonNullElse(m.p, Multinomial.ONE));
                }
                m.resetSimIdentifier();
                return m;
            } else {
                return Node.newPolyNode(Multinomial.ONE);
            }
        }

        @Override
        protected Node simplifyAdd(Add node, ExprCalculator mc) {
            if (Boolean.parseBoolean(mc.getProperty(SimplificationStrategies.PROP_ENABLE_EXPAND))) {
                return null;
            }
            if (node.children.size() <= 1) {
                return null;
            }
            if (node.p != null && !node.p.isZero()) {
                return null;
            }
            List<Node> children = node.children;
            MultinomialCalculator pc = mc.getMultinomialCalculator();
            List<Node> gcd = null;
            // we find the maximum shared node, if there is not, return, take
            for (Node n : children) {
                if (n.getType() == Type.MULTIPLY) {
                    var mul = (Multiply) n;
                    gcd = gcdNode(gcd, mul.children, pc);
                } else {
                    gcd = gcdNode(gcd, n, pc);
                }

            }
            if (gcd == null || gcd.isEmpty()) {
                return null;
            }
            List<Node> newChildren = new ArrayList<>(node.children.size());
            for (Node n : children) {
                var t = removeFactor(n, gcd, pc);
                newChildren.add(t);
                t.parent = node;//we reuse the Add node
            }
            node.children = newChildren;
            node.resetSimIdentifier();
            // Add * gcd
            gcd.add(node);

            var result = Node.wrapNodeAM(false, gcd);

            return mc.simplify(result, 1);
        }
    }

    /**
     * Merges several fractions in an Add node.
     */
    static class CollectFraction extends Collect {
        @Override
        protected Node simplifyAdd(Add node, ExprCalculator mc) {
            if (node.getNumberOfChildren() <= 1 && node.p == null) {
                return null;
            }
            if (!Boolean.parseBoolean(mc.getProperty(PROP_MERGE_FRACTION))) {
                return null;
            }
            List<Node> children = node.children;
            int count = 0;
            for (Node n : children) {
                if (n.getType() == Type.FRACTION) {
                    count++;
                }
            }
            if (count == 0) {
                return null;
            }
            List<Node> numes = new ArrayList<>(children.size()),
                    denos = new ArrayList<>(count);
            for (Node n : children) {
                if (n.getType() == Type.FRACTION) {
                    Fraction f = (Fraction) n;
                    denos.add(f.c2);
                }
            }
            for (Node n : children) {
                List<Node> mul = new ArrayList<>(count + 1);
                if (n.getType() == Type.FRACTION) {
                    Fraction f = (Fraction) n;
                    for (Node nd : denos) {
                        if (nd != f.c2) {
                            mul.add(nd.cloneNode(null));
                        }
                    }
                    mul.add(f.c1);
                } else {
                    for (Node nd : denos) {
                        mul.add(nd.cloneNode(null));
                    }
                    mul.add(n);
                }
                Node m = Node.wrapNodeAM(false, mul);
                numes.add(m);
            }
            if (node.p != null) {
                Poly p = Node.newPolyNode(node.p, null);
                List<Node> mul = new ArrayList<>(count + 1);
                for (Node nd : denos) {
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
    }

    public static void addSqrStrategies(List<SimpleStrategy> list) {
        list.add(new SimplifyFunction(TAG_ALGEBRA_SET, "sqr", "convert sqr to exp") {
            /*
             */
            @Override
            protected Node simplifySFunction(SFunction node, ExprCalculator mc) {
                //sqr node
                if (!node.getFunctionName().equals("sqr")) {
                    throw new AssertionError();
                }
                Node n = Node.wrapNodeDF("exp", node.child, Node.newPolyNode(mc.getMultinomialCalculator().divideLong(mc.getPOne(), 2L), null));
                n.parent = node;
                return mc.simplify(n, 1);
            }
        });
    }


    public static void addExpStrategies(List<SimpleStrategy> list) {
        list.add(new SimplifyFunction(TAG_PRIMARY_SET, "exp", "exp(ln(x))->x") {
            /**
             * exp(ln(x))->x
             */
            @Override
            protected Node simplifySFunction(SFunction node, ExprCalculator mc) {
                Node child = node.child;
                if (Node.isFunctionNode(child, "ln", 1)) {
                    SFunction ln = (SFunction) child;
                    //exp(ln(x)) = x
                    return ln.child; //no need to simplify further
                }
                if (child.getType() == Type.MULTIPLY) {
                    var mul = (Node.Multiply) child;
                    Node ln = null;
                    for (Iterator<Node> iterator = mul.children.iterator(); iterator.hasNext(); ) {
                        Node n = iterator.next();
                        if (Node.isFunctionNode(n, ExprFunction.FUNCTION_NAME_LN, 1)) {
                            ln = n;
                            iterator.remove();
                            break;
                        }
                    }
                    if (ln != null) {
                        mul.resetSimIdentifier();
                        return mc.simplify(Node.wrapCloneNodeDF(ExprFunction.FUNCTION_NAME_EXP, ln, mul));
                    }
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
                if (Node.isPolynomial(c1, Multinomial.E, mc)) {
                    return Node.wrapNodeSF("exp", c2);
                }
                if (c2.getType() == Type.POLYNOMIAL) {
                    var exp = ((Poly) c2).p;
                    var mulCal = mc.getMultinomialCalculator();
                    if (mulCal.isEqual(exp, Multinomial.ZERO)) {
                        return Node.newPolyNode(Multinomial.ONE);
                    } else if (mulCal.isEqual(exp, Multinomial.ONE)) {
                        return c1.cloneNode();
                    } else if (mulCal.isEqual(exp, Multinomial.NEGATIVE_ONE)) {
                        if (c1.getType() == Type.FRACTION) {
                            var temp = (Fraction) c1;
                            var nume = temp.c2;
                            var deno = temp.c1;
                            return Node.wrapCloneNodeFraction(nume, deno);
                        }
                    }
                }
                if (Node.isFunctionNode(c1, "exp", 2)) {
                    DFunction df = (DFunction) c1;
                    //exp(exp(x,p1),p2) = exp(x,p1*p2)
                    CombinedNode mul = Node.wrapNodeAM(false, df.c2, c2);
                    node.c1 = df.c1;
                    node.c2 = mul;
                    mul.parent = node;
                    node.resetSimIdentifier();
                    return mc.simplify(node, 1);
                } else if (Node.isFunctionNode(c1, "exp", 1)) {
                    SFunction sf = (SFunction) c1;
                    //exp(exp(p1),p2) = exp(p1*p2)
                    CombinedNode mul = Node.wrapNodeAM(false, sf.child, c2);
                    mul.parent = sf;
                    sf.child = mul;
                    sf.parent = node.parent;
                    return mc.simplify(sf, 1);
                } else {
                    return null;
                }
            }
        });
        list.add(new SimplifyMultiplyStruct(TAG_PRIMARY_SET) {
            //x * exp(x,y) * exp(x,z) = exp(x,1+y+z)
            @Override
            protected List<Pair<Node, BigInteger>> simplify(List<Pair<Node, BigInteger>> nodes, ExprCalculator ec) {
                TreeMap<Node, List<Pair<Node, BigInteger>>> map = new TreeMap<>(ec.getNodeComparator());
                for (Pair<Node, BigInteger> p : nodes) {
                    var node = p.getFirst();
                    if (Node.isFunctionNode(node, "exp", 2)) {
                        DFunction df = (DFunction) p.getFirst();
                        CollectionSup.accumulateMap(map, df.c1, new Pair<>(df.c2, p.getSecond()), ArrayList::new);
                    } else {
                        CollectionSup.accumulateMap(map, node,
                                new Pair<>(Node.newPolyNode(Multinomial.ONE), p.getSecond()), ArrayList::new);
                    }

                }
                if (map.size() >= nodes.size()) {
                    return null;
                }
                List<Pair<Node, BigInteger>> nlist = new ArrayList<>(map.size());
                for (Entry<Node, List<Pair<Node, BigInteger>>> en : map.entrySet()) {
                    Node down = en.getKey();
                    List<Pair<Node, BigInteger>> powers = en.getValue();
                    if (powers.size() == 1) {
                        Pair<Node, BigInteger> p = powers.get(0);
                        Node exponent = Node.wrapNodeMultiply(p.getFirst(), Multinomial.valueOf(p.getSecond()));
                        Pair<Node, BigInteger> result = new Pair<>(Node.wrapNodeDF("exp", down, exponent),
                                BigInteger.ONE);
                        nlist.add(result);
                    } else {
                        List<Node> adds = new ArrayList<>(powers.size());
                        for (Pair<Node, BigInteger> pair : powers) {
                            Node exponent = Node.wrapNodeMultiply(pair.getFirst(), Multinomial.valueOf(pair.getSecond()));
                            adds.add(exponent);
                        }
                        Node expo = Node.wrapNodeAM(true, adds);
                        Node result = Node.wrapNodeDF("exp", down, expo);
                        nlist.add(new Pair<>(result, BigInteger.ONE));
                    }
                }
                return nlist;
            }

            @Override
            protected boolean firstGlance(List<Node> nodes, ExprCalculator ec) {
//				for(Node n : nodes) {
//					if(Node.isFunctionNode(n, "exp", 2)) {
//						return true;
//					}
//				}
//				return false;
                return nodes.size() > 0;
            }

            @Override
            protected boolean accept(Node n, BigInteger pow, ExprCalculator ec) {
                return true;
//			    return Node.isFunctionNode(n, "exp", 2);
            }
        });
        list.add(new SimplifyFunction(TAG_PRIMARY_SET, ExprFunction.FUNCTION_NAME_RECIPROCAL, "reciprocal(x) -> exp(x,-1)") {
            @Nullable
            @Override
            protected Node simplifySFunction(SFunction node, ExprCalculator mc) {
                return Node.wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP, node.child.cloneNode(), Node.newPolyNode(Multinomial.NEGATIVE_ONE));
            }
        });
        list.add(new ExpPossibleReduction());
        list.add(new ExpConvertFraction());
    }

    static class ExpPossibleReduction extends SimpleStrategy {

        public ExpPossibleReduction() {
            super(TAG_PRIMARY_SET, CollectionSup.unmodifiableEnumSet(Type.MULTIPLY), null, "(p*q) * exo(q,-1) -> p, where p,q are multinomial");
        }

        @Override
        protected @Nullable Node simplifyMultiply(Multiply node, ExprCalculator mc) {
            if (node.p == null) {
                return null;
            }
            Multinomial m = node.p;
            if(m.isOne()){
                return null;
            }
            List<Node> children = node.children;
            boolean reduced = false;
            for (Node n : children) {
                if (!NodeHelper.isExpAsReciprocal(n, mc)) {
                    continue;
                }
                DFunction exp = (DFunction) n;
                Node base = exp.c1;
                if (!Node.isPolynomial(base)) {
                    continue;
                }
                Poly poly = (Poly) base;
                Multinomial toDivide = poly.p;
                var re = Multinomial.simplifyFraction(m, toDivide);
                if(m.equals(re[0])){
                    continue;
                }
                m = re[0];
                exp.setFirst(Node.newPolyNode(re[1]));
                exp.resetSimIdentifier();
                reduced = true;
                if (m.isOne()) {
                    break;
                }
            }
            if(!reduced){
                return null;
            }
            node.p = m;
            return mc.simplify(node,1);
        }
    }

    static class ExpConvertFraction extends SimpleStrategy{

        public ExpConvertFraction() {
            super(TAG_ALGEBRA_SET, CollectionSup.unmodifiableEnumSet(Type.FRACTION),null, "1/x -> exp(x,-1)");
        }

        @Override
        protected @Nullable Node simplifyFraction(Fraction node, ExprCalculator mc) {
            if(!Boolean.parseBoolean(mc.getProperty(SimplificationStrategies.PROP_FRACTION_TO_EXP))){
                return null;
            }
            Node nume = node.getC1();
            if(!Node.isPolynomial(nume,Multinomial.ONE,mc)){
                return null;
            }
            return Node.buildExpStructure(node.c2,BigInteger.ONE.negate(),mc);
        }
    }

    public static void addTriStrategies(List<SimpleStrategy> list) {

        /*
         * sin(x)^2+cos(x)^2 -> 1
         */
        list.add(new SimplifyAddStruct(TAG_TRIGONOMETRIC_SET, "sin(x)^2+cos(x)^2 -> 1") {


            @Override
            protected boolean firstGlance(List<Node> nodes, ExprCalculator ec) {
                for (Node n : nodes) {
                    if (NodeHelper.isExp2(n, ec)) {
                        return true;
                    } else if (n.getType() == Type.MULTIPLY) {
                        var mul = (Multiply) n;
                        if (mul.containMatches(x -> NodeHelper.isExp2(x, ec))) {
                            return true;
                        }
                    }
                }
                return false;
            }


            @Override
            protected boolean accept(Node n, Multinomial pow, ExprCalculator ec) {
                if (!NodeHelper.isExp2(n, ec)) {
                    return false;
                }
                var exp = (DFunction) n;
                var sc = exp.c1;
                return Node.isFunctionNode(sc, ExprFunction.FUNCTION_NAME_SIN, 1) ||
                        Node.isFunctionNode(sc, ExprFunction.FUNCTION_NAME_COS, 1);
            }

            @Override
            protected List<Pair<Node, Multinomial>> simplify(List<Pair<Node, Multinomial>> nodes, ExprCalculator ec) {
                //must be forms of exp(sin(node),2) or exp(cos(node),2)
                NavigableMap<Node, Pair<Multinomial, Multinomial>> map = new TreeMap<>(ec.getNodeComparator());
                boolean[] accumulated = new boolean[]{false};
                for (var p : nodes) {
                    var sc = (SFunction) ((DFunction) p.getFirst()).c1;
                    var coe = p.getSecond();
                    boolean isSin = sc.functionName.equals(ExprFunction.FUNCTION_NAME_SIN);
                    map.compute(sc.child, (key, val) -> {
                        if (val == null) {
                            return pairOf(coe, isSin);
                        } else {
                            accumulated[0] = true;
                            return addPair(coe, isSin, val);
                        }
                    });
                }
                if (!accumulated[0]) {
                    return null;
                }
                Multinomial constResults = Multinomial.ZERO;
                List<Pair<Node, Multinomial>> result = new ArrayList<>(map.size());
                var mc = ec.getMultinomialCalculator();
                for (var entry : map.entrySet()) {
                    Node n = entry.getKey();
                    var p = entry.getValue();
                    var sinCoe = p.getFirst();
                    var cosCoe = p.getSecond();
                    if (mc.isZero(sinCoe)) {
                        //exp(cos(node),2)
                        var exp = Node.buildExpStructure(Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_COS, n), BigInteger.TWO, ec);
                        result.add(new Pair<>(exp, cosCoe));
                    } else {
                        sinCoe = sinCoe.subtract(cosCoe);
                        constResults = constResults.add(cosCoe);
                        var exp = Node.buildExpStructure(Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_SIN, n), BigInteger.TWO, ec);
                        result.add(new Pair<>(exp, sinCoe));
                    }
                }
                result.add(new Pair<>(Node.newPolyNode(constResults), Multinomial.ONE));
                return result;
            }

            private Pair<Multinomial, Multinomial> pairOf(Multinomial m, boolean isFirst) {
                Multinomial f, s;
                if (isFirst) {
                    f = m;
                    s = Multinomial.ZERO;
                } else {
                    f = Multinomial.ZERO;
                    s = m;
                }
                return new Pair<>(f, s);
            }

            private Pair<Multinomial, Multinomial> addPair(Multinomial m, boolean isFirst, Pair<Multinomial, Multinomial> p) {
                if (isFirst) {
                    return new Pair<>(m.add(p.getFirst()), p.getSecond());
                } else {
                    return new Pair<>(p.getFirst(), m.add(p.getSecond()));
                }
            }

        });
    }


    /**
     * Add some settings to the calculator. This method is usually used when the result is required.
     * <ul>
     * <li>Enables the calculator to merge fraction: <text>a/b+c/d = (ad+bc)/bd</text>
     * <li>Enables the calculator to expand multiplication: <text>a*(b+c) = a*b + a*c</text>
     * <li>Add tag : {@link #TAG_REGULARIZE}, {@link #TRIGONOMETRIC_FUNCTION}
     * </ul>
     */
    public static void setCalRegularization(ExprCalculator ec) {
        ec.setProperty(SimplificationStrategies.PROP_MERGE_FRACTION, "true");
        ec.setProperty(PROP_ENABLE_EXPAND, "true");
        ec.tagAdd(TAG_REGULARIZE);
        ec.tagAdd(TRIGONOMETRIC_FUNCTION);
        addSpiSimplification(ec);
    }

    private static void addSpiSimplification(ExprCalculator ec) {
        if (!enableSpi) {
            return;
        }
        loadService();
        for (SimplificationService ss : serviceLoader) {
//            print(ss.getClass());
            ec.tagAddAll(ss.getTags());
            ss.getProperties().forEach(ec::setProperty);
            ss.getStrategies().forEach(ec.getSimStraHolder()::addStrategy);
        }
    }

    public static List<SimpleStrategy> getDefaultStrategies() {
        List<SimpleStrategy> list = new ArrayList<>();
        addBasicAlgebra(list);
        addSqrStrategies(list);
        addExpStrategies(list);
        addTriStrategies(list);
        return list;
    }

    public static Set<String> getDefaultTags() {
        Set<String> set = new HashSet<>();
        set.addAll(TAG_ALGEBRA_SET);
        set.addAll(TAG_PRIMARY_SET);
        return set;
    }

    private static void loadService() {
        if (serviceLoader == null) {
            serviceLoader = ServiceLoader.load(SimplificationService.class);
        }
    }

    /**
     * Sets whether to load spi for simplification. {@link SimplificationService}
     */
    public static void setEnableSpi(boolean enable) {
        enableSpi = enable;
    }

    private static boolean enableSpi = false;
    private static ServiceLoader<SimplificationService> serviceLoader;

    //debugging code below:

    public static void main(String[] args) {
        var mc = ExprCalculator.getNewInstance();
        mc.setProperty(PROP_ENABLE_EXPAND, "false");
        var f = mc.parseExpr("x*exp(x+1,-1)+exp(x+1,-1)");
        var f2 = mc.parseExpr("sin(y)/sin(x) + 1/sin(x)");
        var f3 = mc.parseExpr("(1+sin(y))exp(sin(x),-1)");
        print(f);

        print(f2);
        print(f3);
    }

//    public static void main(String[] args) {
////        enableSpi = true;
//        var mc = ExprCalculator.getNewInstance();
////        mc.tagAdd(SimplificationStrategies.TRIGONOMETRIC_FUNCTION);
//        var f = mc.parseExpr("cos(cos(cos(cos(x))))");
////        var re = mc.simplify(expr);
////        re.listNode();
////        print(re);
////        re.root.recurApplyConsumer(Node::resetSimIdentifier,100);
////        print(mc.simplify(re));
//        var f_6 = mc.differential(f,"x",1);
//        print(f_6);
//        var f_7 = Calculus.derivation(f_6,"x");
//        print(f_7);
//        mc.checkValidTreeStrict(f_7.root);
//        print(mc.simplify(f_7));
////        ExprCalculator.Companion.setShowSimSteps$AnconoKotlin(true);
//        print(mc.simplify(f_7.root));
////        var f_6 = mc.differential(f_5);
////        print(f_6);
//    }
}
