/**
 *
 */
package cn.ancono.math.numberModels.api;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.utilities.SNFSupport;

import java.text.NumberFormat;

/**
 * The formatter for a type of number. This is use by the output of 
 * {@link MathObject#toString}
 * @author liyicheng
 *
 */
public interface NumberFormatter<T> extends FlexibleNumberFormatter<T, MathCalculator<T>> {
    /**
     * Formats the given number, using a {@link MathCalculator}.
     */
    String format(T number, MathCalculator<T> mc);

    /**
     * Returns a decimal formatter for number.
     * @param digit the digit to show.
     */
    static <T extends Number> NumberFormatter<T> decimalFormatter(int digit) {
        NumberFormat nf = SNFSupport.dfByDigit(digit);
        return (n, mc) -> nf.format(n);
    }

    /**
     * Returns a decimal formatter of 3 digits for number.
     */
    static <T extends Number> NumberFormatter<T> decimalFormatter() {
        return decimalFormatter(3);
    }

    NumberFormatter<Object> defaultFormatter = (number, mc) -> {
        try {
            return SNFSupport.format((Number) number);
        } catch (IllegalArgumentException ignore) {
        }
        return number.toString();
    };

    /**
     * Returns a number formatter that simply calls <code>toString()</code>.
     */
    @SuppressWarnings("unchecked")
    public static <T> NumberFormatter<T> defaultFormatter() {
        return (NumberFormatter<T>) defaultFormatter;
    }

}
