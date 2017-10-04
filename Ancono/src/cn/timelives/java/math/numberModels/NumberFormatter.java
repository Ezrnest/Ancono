/**
 * 
 */
package cn.timelives.java.math.numberModels;

import java.text.NumberFormat;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.utilities.SNFSupport;

/**
 * The formatter for a type of number. This is use by the output of 
 * {@link FlexibleMathObject#toString}
 * @author liyicheng
 *
 */
public interface NumberFormatter<T> {
	/**
	 * Formats the given number, using a {@link MathCalculator}.
	 * @param number
	 * @param mc
	 * @return
	 */
	String format(T number, MathCalculator<T> mc);
	/**
	 * Returns a decimal formatter for double.
	 * @param digit the digit to show.
	 * @return
	 */
	public static NumberFormatter<Double> decimalFormatter(int digit){
		NumberFormat nf = SNFSupport.dfByDigit(digit);
		return (n,mc)-> nf.format(n);
	}
	
	
	public static final NumberFormatter<?> toString = (n,mc) -> n.toString();
	/**
	 * Returns a number formatter that simply calls toString().
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> NumberFormatter<T> getToStringFormatter() {
		return (NumberFormatter<T>) toString;
	}
}
