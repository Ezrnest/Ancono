package cn.ancono.utilities;

import java.text.DecimalFormat;

/**
 * generate simple number format easily
 *
 * @author lyc
 */
public class SNFSupport {
    /**
     * An available 3-precision formatter.
     */
    public static final DecimalFormat DF = new DecimalFormat("0.###");
    static final int DEFAULT_DIGIT = 3;

    /**
     * generate a DecimalFormat
     *
     * @param digit
     * @return
     */
    public static DecimalFormat dfByDigit(int digit) {
        if (digit == DEFAULT_DIGIT)
            return DF;
        StringBuilder sb = new StringBuilder();
        sb.append("0.");
        for (int i = 0; i < digit; i++) {
            sb.append('#');
        }
        return new DecimalFormat(sb.toString());
    }

    public static String format(double number, int minSicDigit, int maxShownDigit) {


        return null;
    }

    /**
     * Formats a number with the default number format.
     *
     * @param number
     * @return
     */
    public static String format(Number number) {
        return DF.format(number);
    }
}
