package cn.ancono.math.numberModels.api;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.numberModels.Calculators;

import java.util.Map;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;

/**
 * A computable number model can computes the value of it using a value mao and a MathCalculator.
 *
 * @see cn.ancono.math.numberModels.Term
 */
public interface Computable {

    /**
     * Computes the number's value.
     *
     * @param valueMap a value map to assign the values of variables.
     * @param mc       a MathCalculator of type T
     * @return the computation result
     */
    <T> T compute(Function<String, T> valueMap, MathCalculator<T> mc);


    /**
     * Computes the number's value using double. Implements this method
     * for better performance on double.
     * <p></p>
     * The default implement of this method is <pre>
     *     return compute(x -> valueMap.applyAsDouble(x),Calculators.getCalculatorDoubleDev());
     * </pre>
     *
     * @param valueMap a value map
     * @return the result as double
     * @throws ArithmeticException if the result exceeds double
     */
    default double computeDouble(ToDoubleFunction<String> valueMap) {
        return compute(valueMap::applyAsDouble, Calculators.doubleDev());
    }


    ToDoubleFunction<String> ASSIGN_ONE = ch -> 1d;

    ToDoubleFunction<String> DEFAULT_ASSIGNMENT = name -> {
        //noinspection Duplicates
        switch (name) {
            case MathCalculator.STR_E:
                return Math.E;
            case MathCalculator.STR_PI:
                return Math.PI;
        }
        return 1d;
    };

    ToDoubleFunction<String> DEFAULT_OR_EXCEPTION = name -> {
        //noinspection Duplicates
        switch (name) {
            case MathCalculator.STR_E:
                return Math.E;
            case MathCalculator.STR_PI:
                return Math.PI;
        }
        throw new IllegalArgumentException();
    };

    /**
     * Returns a composed function which will apply default values to variables if
     * the given valueMap assigns the value to NaN. The default values assign {@linkplain MathCalculator#STR_PI}
     * to {@linkplain Math#PI}, {@linkplain MathCalculator#STR_E}
     * to {@linkplain Math#E}, and remaining variables to one.
     *
     * @param valueMap a mapping function which can return NaN.
     */
    static ToDoubleFunction<String> withDefault(ToDoubleFunction<String> valueMap) {
        return name -> {
            double re = valueMap.applyAsDouble(name);
            if (!Double.isNaN(re)) {
                return re;
            }
            return DEFAULT_ASSIGNMENT.applyAsDouble(name);
        };
    }

    /**
     * Returns a composed function which will apply default values to variables if
     * the given valueMap doesn't contain the variable name.
     *
     * @param valueMap a map
     */
    static ToDoubleFunction<String> withDefault(Map<String, Double> valueMap) {
        return name -> {
            Double d = valueMap.get(name);
            if (d != null) {
                return d;
            }
            return DEFAULT_ASSIGNMENT.applyAsDouble(name);
        };
    }

}
