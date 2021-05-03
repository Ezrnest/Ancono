package cn.ancono.math.numberModels.api;

import cn.ancono.math.algebra.abs.calculator.EqualPredicate;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.utilities.SNFSupport;

/**
 * Describes a number formatter for a number model type combined with a calculator type.
 *
 * @param <T> the type of the number model
 * @param <S> the type of the calculator
 */
public interface FlexibleNumberFormatter<T, S extends EqualPredicate<T>> {
    /**
     * Formats the given number using an subclass of {@code EqualPredicate} .
     */
    String format(T number, S mc);

    FlexibleNumberFormatter<?, ?> toString = (FlexibleNumberFormatter<Object, EqualPredicate<Object>>) (number, mc) -> number.toString();

    FlexibleNumberFormatter<?, ?> defaultFormatter = (FlexibleNumberFormatter<Object, EqualPredicate<Object>>) (number, mc) -> {
        if (number instanceof Number && (!(number instanceof Fraction))) {
            try {
                return SNFSupport.format((Number) number);
            } catch (IllegalArgumentException ignore) {
            }
        }
        return number.toString();
    };

    /**
     * Returns a number formatter that tries to format the number using a default decimal formatter first and
     * <code>toString()</code> for fallback.
     */
    @SuppressWarnings("unchecked")
    static <T, S extends EqualPredicate<T>> FlexibleNumberFormatter<T, S> defaultFormatter() {
        return (FlexibleNumberFormatter<T, S>) defaultFormatter;
    }

    /**
     * Gets a number formatter for subclasses of <code>Number</code> that use a decimal formatter.
     */
    static <T extends Number, S extends EqualPredicate<T>> FlexibleNumberFormatter<T, S> decimalFormatter() {
        return (d, mc) -> SNFSupport.DF.format(d);
    }
}
