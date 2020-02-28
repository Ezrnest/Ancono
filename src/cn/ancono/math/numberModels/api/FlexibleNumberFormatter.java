package cn.ancono.math.numberModels.api;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.algebra.abstractAlgebra.calculator.EqualPredicate;
import cn.ancono.utilities.SNFSupport;

public interface FlexibleNumberFormatter<T, S extends EqualPredicate<T>> {
    /**
     * Formats the given number, using a {@link MathCalculator}.
     *
     * @param number
     * @param mc
     * @return
     */
    String format(T number, S mc);

    FlexibleNumberFormatter<?, ?> toString = (FlexibleNumberFormatter<Object, EqualPredicate<Object>>) (number, mc) -> number.toString();

    /**
     * Returns a number formatter that simply calls toString().
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    static <T, S extends EqualPredicate<T>> FlexibleNumberFormatter<T, S> getToStringFormatter() {
        return (FlexibleNumberFormatter<T, S>) toString;
    }

    static <T extends Number, S extends EqualPredicate<T>> FlexibleNumberFormatter<T, S> getNumberFormatter() {
        return (d, mc) -> SNFSupport.DF.format(d);
    }
}
