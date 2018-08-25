package cn.timelives.java.math.numberModels.expression.simplification;

import cn.timelives.java.math.numberModels.Multinomial;
import cn.timelives.java.math.numberModels.expression.ExprCalculator;
import cn.timelives.java.math.numberModels.expression.ExprFunction;
import cn.timelives.java.math.numberModels.expression.Node;

public final class NodeHelper {
    private NodeHelper(){}

    public static boolean isExp2(Node n, ExprCalculator ec){
        if(Node.isFunctionNode(n, ExprFunction.FUNCTION_NAME_EXP,2)){
            var exp = (Node.DFunction) n;
            return Node.isPolynomial(exp.getC2(), Multinomial.TWO, ec);
        }
        return false;
    }
}
