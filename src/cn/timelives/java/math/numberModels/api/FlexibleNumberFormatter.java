package cn.timelives.java.math.numberModels.api;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.algebra.abstractAlgebra.calculator.EqualPredicate;

public interface FlexibleNumberFormatter<T,S extends EqualPredicate<T>> {
    /**
     * Formats the given number, using a {@link MathCalculator}.
     * @param number
     * @param mc
     * @return
     */
    String format(T number, S mc);

    FlexibleNumberFormatter<?,?> toString = new FlexibleNumberFormatter<Object, EqualPredicate<Object>>() {
        @Override
        public String format(Object number, EqualPredicate mc) {
            return number.toString();
        }
    };
    /**
     * Returns a number formatter that simply calls toString().
     * @return
     */
    @SuppressWarnings("unchecked")
    static <T,S extends EqualPredicate<T>> FlexibleNumberFormatter<T,S> getToStringFormatter() {
        return (FlexibleNumberFormatter<T,S>) toString;
    }
}
