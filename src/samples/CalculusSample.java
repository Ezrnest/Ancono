package samples;

import cn.ancono.math.calculus.Limit;
import cn.ancono.math.calculus.LimitProcess;
import cn.ancono.math.numberModels.expression.Expression;

public class CalculusSample {
    public static void limitSample() {
        var mc = Expression.getCalculator();
        var expr = mc.parse("sin(x)/x");
        var result = Limit.limitOf(expr, LimitProcess.Companion.toZero(mc), mc);
        System.out.println("as x -> 0, lim sin(x)/x = " + result);
        //result = 1
    }


    public static void main(String[] args) {
        limitSample();
    }
}
