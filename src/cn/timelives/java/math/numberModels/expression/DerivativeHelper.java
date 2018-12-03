package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.calculus.Calculus;
import cn.timelives.java.math.exceptions.UnsupportedCalculationException;
import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.expression.anno.DisallowModify;

import java.util.*;
import java.util.function.BiFunction;

import static cn.timelives.java.math.numberModels.expression.ExprFunction.FUNCTION_NAME_EXP;
import static cn.timelives.java.utilities.Printer.print;

/**
 * Helps compute derivative of expression.
 */
public class DerivativeHelper {
    private DerivativeHelper() {
    }

    /**
     * Computes the derivative of the node.
     *
     * @param node         a node that won't be modified.
     * @param variableName the variable to derive
     * @return a new node representing the derivative
     */
    public static Node derivativeNode(@DisallowModify Node node, String variableName) {
        switch (node.getType()) {
            case POLYNOMIAL: {
                Node.Poly poly = (Node.Poly) node;
                return Node.newPolyNode(Calculus.derivation(poly.p, variableName), null);
            }
            case ADD: {
                return dNodeAdd((Node.Add) node, variableName);
            }
            case MULTIPLY: {
                return dNodeMultiply((Node.Multiply) node, variableName);
            }
            case FRACTION: {
                return dNodeFraction((Node.Fraction) node, variableName);
            }
            case S_FUNCTION:
            case D_FUNCTION:
            case M_FUNCTION:
                return DISPATCHER.derivation(node, variableName);
            default:
                throw new AssertionError();
        }
    }


    private static Node dNodeAdd(@DisallowModify Node.Add node, String variableName) {
        var np = node.p == null ? null : Calculus.derivation(node.p, variableName);
        var list = new ArrayList<Node>(node.children.size());
        for (Node n : node.children) {
            list.add(derivativeNode(n, variableName));
        }
        return Node.wrapNodeAM(true, list, np);
    }

    private static Node dNodeMultiply(@DisallowModify Node.Multiply node, String variableName) {
        //multiply rules :
        // (f(x) * g(x))' = f'(x)*g(x) + f(x)*g'(x)
        var poly = node.p;
        if (poly == null || !poly.containsChar(variableName)) {
            //constant
            var result = dMultiply(node.children, variableName);
            return poly == null ? result : Node.wrapNodeMultiply(result, poly); // no clone
        }
        var p_ = Calculus.derivation(node.p, variableName);
        if (node.children.isEmpty()) {
            return Node.newPolyNode(p_, null);
        }
        Node n_ = dMultiply(node.children, variableName);
        Node partA = Node.wrapNodeMultiply(n_, poly);
        Node partB = Node.wrapNodeMultiply(Node.wrapCloneNodeAM(false, node.children), p_);
        return Node.wrapNodeAM(true, partA, partB); // no clone
    }


    /**
     * @param nodes        require clone
     * @param variableName
     * @return new node
     */
    private static Node dMultiply(List<Node> nodes, String variableName) {
        int size = nodes.size();
        if (size == 1) {
            return derivativeNode(nodes.get(0), variableName);
        }
        Node a, b, a_, b_;
        if (size == 2) {
            a = nodes.get(0).cloneNode(null);
            b = nodes.get(1).cloneNode(null);
            a_ = derivativeNode(a, variableName);
            b_ = derivativeNode(b, variableName);
        } else {
            a = nodes.get(0).cloneNode(null);
            List<Node> remains = nodes.subList(1, nodes.size());
            b = Node.wrapCloneNodeAM(false, remains);
            a_ = derivativeNode(a, variableName);
            b_ = dMultiply(remains, variableName);
        }
        Node partA = Node.wrapNodeAM(false, a, b_),
                partB = Node.wrapNodeAM(false, a_, b);
        return Node.wrapNodeAM(true, partA, partB);
    }

    private static Node dNodeFraction(@DisallowModify Node.Fraction node, String variableName) {
        //(f(x)/g(x))' = ( f'(x)g(x) - f(x)g'(x) )/g(x)^2
        Node f = node.c1.cloneNode(null);
        Node g = node.c2.cloneNode(null);
        Node f_ = derivativeNode(f, variableName);
        Node g_ = derivativeNode(g, variableName);
        var p1 = Node.wrapNodeAM(false, f_, g);
        var p2 = Node.wrapNodeAM(false, f, g_);
        p2.p = Multinomial.NEGATIVE_ONE;
        Node nume = Node.wrapNodeAM(true, p1, p2);
        Node deno = Node.wrapNodeDF(FUNCTION_NAME_EXP, g, Node.newPolyNode(Multinomial.valueOf(2), null));
        return Node.wrapNodeFraction(nume, deno);
    }

    private static final FunctionDerivatorDispatcher DISPATCHER = new FunctionDerivatorDispatcher();


    public interface FunctionDerivator {
        /**
         * Determines whether this FunctionDerivator supports the function of
         * the functionName and parameterLength.
         *
         * @param functionName    the name of the function
         * @param parameterLength the length of the parameter
         * @return true if it supports the function
         */
        boolean accept(String functionName, int parameterLength);

        /**
         * Computes the derivation of the node, the node is always an
         * instance of FunctionNode
         */
        Node derivation(@DisallowModify Node node, String variableName);
    }

    private static Node unsupportedDerivatorFunction(String functionName, int parameterLength) {
        throw new UnsupportedCalculationException("Cannot compute derivative of " + functionName +
                " with " + parameterLength + " parameter(s)");
    }

    private static Node dReciprocal(@DisallowModify Node.SFunction node, String variableName) {
        // 1/f(x) -> f'(x) / (f(x))^2
        Node fx = node.child;
        Node fx_ = derivativeNode(fx, variableName);
        Node cos = Node.wrapCloneNodeDF(ExprFunction.FUNCTION_NAME_EXP, fx, Node.newPolyNode(Multinomial.valueOf(-2L)));
        return Node.wrapNodeAM(false, fx_, cos);
    }


    private static Node dSin(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child;
        Node fx_ = derivativeNode(fx, variableName);
        Node cos = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_COS, fx);
        return Node.wrapNodeAM(false, fx_, cos);
    }

    private static Node dCos(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child;
        Node fx_ = derivativeNode(fx, variableName);
        Node sin = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_SIN, fx);
        var result = Node.wrapNodeAM(false, fx_, sin);
        result.p = Multinomial.NEGATIVE_ONE;
        return result;
    }

    private static Node dTan(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child;
        Node fx_ = derivativeNode(fx, variableName);
        Node cos = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_COS, fx);
        Node squarecos = Node.wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP, cos, Node.newPolyNode(Multinomial.TWO));
        Node result = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_RECIPROCAL, squarecos);
        return Node.wrapNodeAM(false, fx_, result);
    }

    private static Node dLn(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child;
        Node fx_ = derivativeNode(fx, variableName);
        Node result = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_RECIPROCAL, fx);
        return Node.wrapNodeAM(false, fx_, result);
    }

    private static Node dExp(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child;
//        Objects.requireNonNull(fx);
        Node fx_ = derivativeNode(fx, variableName);
        return Node.wrapNodeAM(false, fx_, node.cloneNode());
    }

    private static Node dSquareRoot(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child;
        Node fx_ = derivativeNode(fx, variableName);
        Node result = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_RECIPROCAL, node);
        var r = Node.wrapNodeAM(false, fx_, result);
        r.p = Multinomial.valueOf("1/2");
        return r;
    }

    private static Node dNegate(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child;
        Node fx_ = derivativeNode(fx, variableName);
        return Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_NEGATE, fx_);
    }

    private static Node dExp2(@DisallowModify Node.DFunction node, String variableName) {
        Node base = node.c1;
        Node exponent = node.c2;
        Objects.requireNonNull(base);
        Objects.requireNonNull(exponent);

        //a^b = e^(b * ln(a))
        Node lna = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_LN, base);
        Node newExponent = Node.wrapCloneNodeAM(false, exponent, lna);
        Node expart = Node.wrapNodeSF(ExprFunction.FUNCTION_NAME_EXP, newExponent);
        return derivativeNode(expart, variableName);
    }

    private static Node dArcsin(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child.cloneNode(null);
        Node fx_ = derivativeNode(fx, variableName);
        //arcsin(x)' = 1/sqr(1-x^2)
        Node negativeX2 = Node.wrapNodeMultiply(
                Node.wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP, fx, Node.newPolyNode(Multinomial.TWO)),
                Multinomial.NEGATIVE_ONE);
        Node oneMinus = Node.wrapNodeAdd(negativeX2, Multinomial.ONE);
        Node sqr = Node.wrapCloneNodeDF(
                ExprFunction.FUNCTION_NAME_EXP, oneMinus, Node.newPolyNode(Multinomial.valueOf("-1/2")));
        return Node.wrapNodeAM(false, fx_, sqr);
    }

    private static Node dArccos(@DisallowModify Node.SFunction node, String variableName) {
        var result = dArcsin(node, variableName);
        Node.setPolynomialPart(result, Multinomial.NEGATIVE_ONE);
        return result;
    }

    private static Node dArctan(@DisallowModify Node.SFunction node, String variableName) {
        Node fx = node.child.cloneNode(null);
        Node fx_ = derivativeNode(fx, variableName);
        //arcsin(x)' = 1/(1+x^2)
        Node x2 = Node.wrapNodeDF(ExprFunction.FUNCTION_NAME_EXP, fx, Node.newPolyNode(Multinomial.TWO));
        Node oneAdd = Node.wrapNodeAdd(x2, Multinomial.ONE);
        Node fraction = Node.wrapNodeFraction(Node.newPolyNode(Multinomial.ONE), oneAdd);
        return Node.wrapNodeAM(false, fx_, fraction);
    }

    private static Node dLog2(@DisallowModify Node.DFunction node, String variableName) {
        //log(a,b) = ln(b) / ln(a)
        Node a = node.c1;
        Node b = node.c2;
        Node lna = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_LN, a);
        Node lnb = Node.wrapCloneNodeSF(ExprFunction.FUNCTION_NAME_LN, b);
        return derivativeNode(Node.wrapNodeFraction(lnb, lna), variableName);
    }

    static {
        //initialize the derivators
        addSingles();
        addDoubles();
        addExtensions();
    }

    private static void addExtensions() {
        addDerivator(new FunctionDerivator() {
            @Override
            public boolean accept(String functionName, int parameterLength) {
                return parameterLength == 1;
            }

            @Override
            public Node derivation(Node node, String variableName) {
                if (node instanceof Node.SFunction) {
                    return dFunction((Node.SFunction) node, variableName);
                }
                return null;
            }
        });
    }

    private static void addDoubles() {
        addDFunctionDerivator(ExprFunction.FUNCTION_NAME_EXP, DerivativeHelper::dExp2);
        addDFunctionDerivator(ExprFunction.FUNCTION_NAME_LOG, DerivativeHelper::dLog2);
    }

    private static void addSingles() {
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_RECIPROCAL,DerivativeHelper::dReciprocal);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_SIN, DerivativeHelper::dSin);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_COS, DerivativeHelper::dCos);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_TAN, DerivativeHelper::dTan);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_LN, DerivativeHelper::dLn);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_EXP, DerivativeHelper::dExp);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_SQR, DerivativeHelper::dSquareRoot);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_NEGATE, DerivativeHelper::dNegate);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_ARCSIN, DerivativeHelper::dArcsin);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_ARCCOS, DerivativeHelper::dArccos);
        addSFunctionDerivator(ExprFunction.FUNCTION_NAME_ARCTAN, DerivativeHelper::dArctan);
    }

    private static Node dFunction(Node.SFunction node, String variableName) {
        Node gx = node.child.cloneNode(null);
        Node gx_ = derivativeNode(gx, variableName);
        String functionName_ = node.functionName + "'";
        Node fx_ = Node.wrapCloneNodeSF(functionName_, gx);
        return Node.wrapNodeAM(false, gx_, fx_);
    }


    /**
     * Adds customized derivative-calculating function for single-variable function.
     *
     * @param functionName the name of the target function
     * @param derivator    a BiFunction to calculate derivative
     */
    public static void addSFunctionDerivator(String functionName,
                                             BiFunction<Node.SFunction, String, Node> derivator) {
        DISPATCHER.addSFunction(functionName, derivator);
    }

    /**
     * Adds customized derivative-calculating function for binary-variable function.
     *
     * @param functionName the name of the target function
     * @param derivator    a BiFunction to calculate derivative
     */
    public static void addDFunctionDerivator(String functionName,
                                             BiFunction<Node.DFunction, String, Node> derivator) {
        DISPATCHER.addDFunction(functionName, derivator);
    }

    /**
     * Adds customized derivative-calculating function for multi-variable function.
     *
     * @param functionName the name of the target function
     * @param derivator    a BiFunction to calculate derivative
     */
    public static void addMFunctionDerivator(String functionName,
                                             BiFunction<Node.MFunction, String, Node> derivator) {
        DISPATCHER.addMFunction(functionName, derivator);
    }

    /**
     * Adds customized derivator, which will be dispatched when computing derivative.
     *
     * @param fd a FunctionDerivator
     */
    public static void addDerivator(FunctionDerivator fd) {
        DISPATCHER.addDerivator(fd);
    }


    private static class FunctionDerivatorDispatcher implements FunctionDerivator {
        private final Map<String, BiFunction<Node.SFunction, String, Node>> sFunction = new HashMap<>();
        private final Map<String, BiFunction<Node.DFunction, String, Node>> dFunction = new HashMap<>();
        private final Map<String, BiFunction<Node.MFunction, String, Node>> mFunction = new HashMap<>();
        private final List<FunctionDerivator> wildcards = new ArrayList<>();

        @Override
        public boolean accept(String functionName, int parameterLength) {
            return true;
        }

        @Override
        public Node derivation(Node node, String variableName) {
            var type = node.getType();
            Node result = null;
            switch (type) {
                case S_FUNCTION: {
                    result = dSFunction((Node.SFunction) node, variableName);
                    break;
                }
                case D_FUNCTION: {
                    result = dDFunction((Node.DFunction) node, variableName);
                    break;
                }
                case M_FUNCTION: {
                    result = dMFunction((Node.MFunction) node, variableName);
                    break;
                }
            }
            if (result != null) {
                return result;
            }
            Node.FunctionNode fnode = (Node.FunctionNode) node;
            String fName = fnode.getFunctionName();
            int pLen = fnode.getParameterLength();
            for (FunctionDerivator fd : wildcards) {
                if (fd.accept(fName, pLen)) {
                    result = fd.derivation(node, variableName);
                    if (result != null) {
                        return result;
                    }
                }
            }
            return unsupportedDerivatorFunction(fName, pLen);
        }

        private Node dSFunction(Node.SFunction node, String variableName) {
            String name = node.functionName;
            var derivator = sFunction.get(name);
            if (derivator == null) {
                return null;
            }
            return derivator.apply(node, variableName);
        }

        private Node dDFunction(Node.DFunction node, String variableName) {
            String name = node.functionName;
            var derivator = dFunction.get(name);
            if (derivator == null) {
                return null;
            }
            return derivator.apply(node, variableName);
        }

        private Node dMFunction(Node.MFunction node, String variableName) {
            String name = node.functionName;
            var derivator = mFunction.get(name);
            if (derivator == null) {
                return null;
            }
            return derivator.apply(node, variableName);
        }

        public void addSFunction(String functionName,
                                 BiFunction<Node.SFunction, String, Node> derivator) {
            sFunction.put(functionName, derivator);
        }

        public void addDFunction(String functionName,
                                 BiFunction<Node.DFunction, String, Node> derivator) {
            dFunction.put(functionName, derivator);
        }

        public void addMFunction(String functionName,
                                 BiFunction<Node.MFunction, String, Node> derivator) {
            mFunction.put(functionName, derivator);
        }

        public void addDerivator(FunctionDerivator fd) {
            wildcards.add(fd);
        }

    }

//    public static void main(String[] args) {
//        ExprCalculator ec = ExprCalculator.Companion.getNewInstance();
//        Expression expr = Expression.valueOf("f(x)g(x)h(x)F_(x)");
//        SimplificationStrategies.setCalRegularization(ec);
//        expr = ec.simplify(expr);
//        print("Expression is: " + expr);
//        var re = Calculus.derivation(expr, "x");
////        re.listNode();
//        re = ec.simplify(re);
//        print(re);
//    }
}
