package samples;

import cn.ancono.math.numberModels.expression.ExprCalculator;
import cn.ancono.math.numberModels.expression.Expression;

public class ExpressionSample {

    public static void useExpression() {
        var cal = Expression.getCalculator();
        var f1 = cal.parseExpr("(x^2+3x+2)/(x+1)+sin(Pi/2)+exp(t)");
        System.out.println(f1);
        var f2 = cal.parseExpr("y+1");
        System.out.println(f2);
        var f3 = cal.divide(f1, f2);
        System.out.println(f3);
    }


    public static void main(String[] args) {
        useExpression();
    }
}
