package cn.ancono.math.numberModels.api;

import cn.ancono.math.MathObject;
import cn.ancono.math.numberModels.Fraction;
import cn.ancono.utilities.SNFSupport;

/**
 * Describes a number formatter for a number model type combined with a calculator type.
 *
 * @param <T> the type of the number model
 */
@FunctionalInterface
public interface NumberFormatter<T> {
    /**
     * Formats the given number using an subclass of {@code EqualPredicate} .
     */
    String format(T number);

//    default String format(T number, boolean bracketRequired) {
//        return format(number);
//    }

    NumberFormatter<?> toString = (NumberFormatter<Object>) Object::toString;

    NumberFormatter<?> defaultFormatter = (NumberFormatter<Object>) (number) -> {
        if (number instanceof MathObject) {
            return ((MathObject<?, ?>) number).toString(defaultFormatter());
        }
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
    static <T> NumberFormatter<T> defaultFormatter() {
        return (NumberFormatter<T>) defaultFormatter;
    }

    /**
     * Gets a number formatter for subclasses of <code>Number</code> that use a decimal formatter.
     */
    static <T extends Number> NumberFormatter<T> decimalFormatter() {
        return SNFSupport.DF::format;
    }
}
