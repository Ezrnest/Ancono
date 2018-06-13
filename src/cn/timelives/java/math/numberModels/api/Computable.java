package cn.timelives.java.math.numberModels.api;

import cn.timelives.java.math.numberModels.Calculators;
import cn.timelives.java.math.numberModels.MathCalculator;

import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * A computable number model can computes the value of it using a value mao and a MathCalculator.
 * @see cn.timelives.java.math.numberModels.Term
 */
public interface Computable {

    /**
     * Computes the number's value.
     * @param valueMap a value map to assign the values of variables.
     * @param mc a MathCalculator of type T
     * @return the computation result
     */
    <T> T compute(Function<String,T> valueMap, MathCalculator<T> mc);


    /**
     * Computes the number's value using double. Implements this method
     * for better performance on double.
     * <p></p>
     * The default implement of this method is <pre>
     *     return compute(x -> valueMap.applyAsDouble(x),Calculators.getCalculatorDoubleDev());
     * </pre>
     * @param valueMap a value mao
     * @return
     * @throws ArithmeticException if the result exceeds double
     */
    default double computeDouble(ToDoubleFunction<String> valueMap){
        return compute(valueMap::applyAsDouble,Calculators.getCalculatorDoubleDev());
    }
}
