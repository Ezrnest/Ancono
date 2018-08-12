/**
 * 2017-10-07
 */
package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.MathCalculator;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.LongFunction;

/**
 * Contains some utilities for MathCalculator
 * @author liyicheng
 * 2017-10-07 15:19
 *
 */
public final class Utils {

	/**
	 * 
	 */
	private Utils() {
	}
	
	/**
	 * Returns a BiPredicate:
	 * {@code (x,y)->mc.isEqual(x, mapper.apply(y))}
	 * @param mc a {@link MathCalculator}
	 * @param mapper the map function
	 * @return
	 */
	public static <T,S> BiPredicate<T, S> mappedIsEqual(MathCalculator<T> mc,Function<S,T> mapper){
		return (x,y)->mc.isEqual(x, mapper.apply(y));
	}
	
	public static <T> T max(T a,T b,Comparator<T> mc){
		int comp = mc.compare(a,b);
		return comp>0? a : b;
	}
	public static <T> T min(T a,T b,Comparator<T> mc){
		int comp = mc.compare(a,b);
		return comp<0? a : b;
	}

	public static <T> Function<BigInteger,T> parserBigInteger(MathCalculator<T> mc){
		return x -> valueOfBigInteger(x,mc);
	}

	public static <T> Function<Fraction,T> parserFraction(MathCalculator<T> mc){
		return x -> valueOfFraction(x,mc);
	}

	public static <T> LongFunction<T> parserLong(MathCalculator<T> mc){
		return x -> valueOfLong(x,mc);
	}

	public static <T> T valueOfBigInteger(BigInteger x,MathCalculator<T> mc){
		return mc.multiplyLong(mc.getOne(),x.longValueExact());
	}

	public static <T> T valueOfFraction(Fraction x,MathCalculator<T> mc){
		if(x.isZero()){
			return mc.getZero();
		}
		T re = valueOfLong(x.getNumerator(), mc);
		if (x.getDenominator() != 1) {
			re = mc.divideLong(re, x.getDenominator());
		}
		if(x.isNegative()){
			re = mc.negate(re);
		}
		return re;
	}


	public static <T> T valueOfLong(long x,MathCalculator<T> mc){
		return mc.multiplyLong(mc.getOne(),x);
	}
}
