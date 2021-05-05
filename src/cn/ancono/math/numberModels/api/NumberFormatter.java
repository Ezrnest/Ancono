/**
 *
 */
package cn.ancono.math.numberModels.api;

import cn.ancono.math.MathObject;

/**
 * The formatter for a type of number. This is use by the output of
 * {@link MathObject#toString}
 *
 * @author liyicheng
 */
//public interface FlexibleNumberFormatter<T> extends FlexibleNumberFormatter<T> {
//    /**
//     * Formats the given number, using a {@link MathCalculator}.
//     */
//    String format(T number, MathCalculator<T> mc);
//
//    /**
//     * Returns a decimal formatter for number.
//     * @param digit the digit to show.
//     */
//    static <T extends Number> FlexibleNumberFormatter<T> decimalFormatter(int digit) {
//        NumberFormat nf = SNFSupport.dfByDigit(digit);
//        return (n, mc) -> nf.format(n);
//    }
//
//    /**
//     * Returns a decimal formatter of 3 digits for number.
//     */
//    static <T extends Number> FlexibleNumberFormatter<T> decimalFormatter() {
//        return decimalFormatter(3);
//    }
//
//    FlexibleNumberFormatter<Object> defaultFormatter = (number, mc) -> {
//        if (number instanceof Number && (!(number instanceof Fraction))) {
//            try {
//                return SNFSupport.format((Number) number);
//            } catch (IllegalArgumentException ignore) {
//            }
//        }
//        return number.toString();
//    };
//
//    /**
//     * Returns a number formatter that simply calls <code>toString()</code>.
//     */
//    @SuppressWarnings("unchecked")
//    public static <T> FlexibleNumberFormatter<T> defaultFormatter() {
//        return (NumberFormatter<T>) defaultFormatter;
//    }
//
//}
