/**
 *
 */
package cn.ancono.math.numberModels.api;

import cn.ancono.math.MathCalculator;
import cn.ancono.math.MathObject;
import cn.ancono.utilities.SNFSupport;

import java.text.NumberFormat;
import java.util.Objects;

/**
 * The formatter for a type of number. This is use by the output of 
 * {@link MathObject#toString}
 * @author liyicheng
 *
 */
public interface NumberFormatter<T> extends FlexibleNumberFormatter<T, MathCalculator<T>> {
    /**
     * Formats the given number, using a {@link MathCalculator}.
     * @param number
     * @param mc
     * @return
     */
    String format(T number, MathCalculator<T> mc);

    /**
     * Returns a decimal formatter for number.
     * @param digit the digit to show.
     * @return
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

    static final NumberFormatter<?> toString = (x, mc) -> Objects.toString(x);

    @SuppressWarnings("unchecked")
    public static <T> NumberFormatter<T> getToStringFormatter() {
        return (NumberFormatter<T>) toString;
    }

}
