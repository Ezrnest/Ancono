package cn.ancono.math.numberModels.expression.simplification;

import cn.ancono.math.numberModels.Multinomial;
import cn.ancono.math.numberModels.expression.ExprCalculator;
import cn.ancono.math.numberModels.expression.ExprFunction;
import cn.ancono.math.numberModels.expression.Node;

public final class NodeHelper {
    private NodeHelper() {
    }

    public static boolean isExp2(Node n, ExprCalculator ec) {
        if (Node.isFunctionNode(n, ExprFunction.FUNCTION_NAME_EXP, 2)) {
            var exp = (Node.DFunction) n;
            return Node.isPolynomial(exp.getC2(), Multinomial.TWO, ec);
        }
        return false;
    }

    /**
     * Determines whether the node is exp(base,pow), if it is,
     * returns the base, else returns null.
     */
    public static boolean isPow(Node n, ExprCalculator ec, int pow) {
        if (Node.isFunctionNode(n, ExprFunction.FUNCTION_NAME_EXP, 2)) {
            var exp = (Node.DFunction) n;
            return Node.isPolynomial(exp.getC2(), Multinomial.of(pow), ec);
        }
        return false;
    }

    public static boolean isExpAsReciprocal(Node n, ExprCalculator ec) {
        if (Node.isFunctionNode(n, ExprFunction.FUNCTION_NAME_EXP, 2)) {
            var exp = (Node.DFunction) n;
            return Node.isPolynomial(exp.getC2(), Multinomial.NEGATIVE_ONE, ec);
        }
        return false;
    }
}
