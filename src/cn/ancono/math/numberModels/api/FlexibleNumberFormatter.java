package cn.ancono.math.numberModels.api;

import cn.ancono.math.numberModels.Fraction;
import cn.ancono.utilities.SNFSupport;

/**
 * Describes a number formatter for a number model type combined with a calculator type.
 *
 * @param <T> the type of the number model
 */
public interface FlexibleNumberFormatter<T> {
    /**
     * Formats the given number using an subclass of {@code EqualPredicate} .
     */
    String format(T number);

    FlexibleNumberFormatter<?> toString = (FlexibleNumberFormatter<Object>) Object::toString;

    FlexibleNumberFormatter<?> defaultFormatter = (FlexibleNumberFormatter<Object>) (number) -> {
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
    static <T> FlexibleNumberFormatter<T> defaultFormatter() {
        return (FlexibleNumberFormatter<T>) defaultFormatter;
    }

    /**
     * Gets a number formatter for subclasses of <code>Number</code> that use a decimal formatter.
     */
    static <T extends Number> FlexibleNumberFormatter<T> decimalFormatter() {
        return SNFSupport.DF::format;
    }
}
