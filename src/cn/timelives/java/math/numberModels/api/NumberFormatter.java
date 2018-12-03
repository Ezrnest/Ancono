/**
 * 
 */
package cn.timelives.java.math.numberModels.api;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.MathObject;
import cn.timelives.java.utilities.SNFSupport;

import java.text.NumberFormat;
import java.util.Objects;

/**
 * The formatter for a type of number. This is use by the output of 
 * {@link MathObject#toString}
 * @author liyicheng
 *
 */
public interface NumberFormatter<T> extends FlexibleNumberFormatter<T,MathCalculator<T>>{
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
	static NumberFormatter<Double> decimalFormatter(int digit){
		NumberFormat nf = SNFSupport.dfByDigit(digit);
		return (n,mc)-> nf.format(n);
	}
	static final NumberFormatter<?> toString = (x,mc) -> Objects.toString(x);
	@SuppressWarnings("unchecked")
	public static <T> NumberFormatter<T> getToStringFormatter(){
		return (NumberFormatter<T>)toString;
	}

}
