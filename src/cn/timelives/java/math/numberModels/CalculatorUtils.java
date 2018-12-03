/**
 * 2017-10-07
 */
package cn.timelives.java.math.numberModels;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.MathUtils;
import cn.timelives.java.math.function.SVFunction;

import java.math.BigInteger;
import java.util.Comparator;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;

/**
 * Contains some utilities for MathCalculator
 * @author liyicheng
 * 2017-10-07 15:19
 *
 */
public final class CalculatorUtils {

	/**
	 * 
	 */
	private CalculatorUtils() {
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
		T re = valueOfLong(x.getNumerator(),mc);
		if(x.getDenominator() !=1){
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

    /**
     * Computes the sum of a arithmetic progression which starts from {@code start} and has the difference of
     * {@code step}, and all its elements are smaller or equal to {@code end}.
     */
	public static <T> T sigma(T start, T end, T step,MathCalculator<T> mc){
		if(!mc.isComparable()){
            throw new ArithmeticException("MathCalculator of type "+mc.getNumberClass()+" is not comparable.");
        }
        T t =start;
		int signum = mc.compare(start,end);
		if(signum>0){
		    return mc.getZero();
        }
        T re = mc.getZero();
        do{
            re = mc.add(re,t);
            t = mc.add(t,step);
        }while (mc.compare(t,end)<=0);
        return re;
	}

    /**
     * Computes the sum of applying {@code f} to a arithmetic progression which starts from {@code start} and has the difference of
     * {@code step}, and all its elements are smaller or equal to {@code end}.
     */
    public static <T> T sigma(T start, T end, T step,MathCalculator<T> mc, SVFunction<T> f){
        if(!mc.isComparable()){
            throw new ArithmeticException("MathCalculator of type "+mc.getNumberClass()+" is not comparable.");
        }
        T t =start;
        int signum = mc.compare(start,end);
        if(signum>0){
            return mc.getZero();
        }
        T re = mc.getZero();
        do{
            re = mc.add(re,f.apply(t));
            t = mc.add(t,step);
        }while (mc.compare(t,end)<=0);
        return re;
    }

    public static <T> T sigma(int startInclusive, int endExclusive, MathCalculator<T> mc, IntFunction<T> f){
        T re = mc.getZero();
        for(int i=startInclusive;i<endExclusive;i++){
            re = mc.add(re,f.apply(i));
        }
        return re;
    }

    public static <T> T multiplyAll(int startInclusive, int endExclusive, MathCalculator<T> mc, IntFunction<T> f){
        T re = mc.getOne();
        for(int i=startInclusive;i<endExclusive;i++){
            re = mc.multiply(re,f.apply(i));
        }
        return re;
    }

    /**
     * Computes the result of multiplying all the elements in a arithmetic progression which starts from {@code start} and has the difference of
     * {@code step}, and all its elements are smaller or equal to {@code end}.
     */
    public static <T> T multiplyAll(T start, T end, T step, MathCalculator<T> mc){
        if(!mc.isComparable()){
            throw new ArithmeticException("MathCalculator of type "+mc.getNumberClass()+" is not comparable.");
        }
        T t =start;
        int signum = mc.compare(start,end);
        if(signum>0){
            return mc.getZero();
        }
        T re = mc.getZero();
        do{
            re = mc.multiply(re,t);
            t = mc.add(t,step);
        }while (mc.compare(t,end)<=0);
        return re;
    }

    /**
     * Computes the result of multiplying all the result of apply {@code f} to the elements of a arithmetic progression
     * which starts from {@code start} and has the difference of
     * {@code step}, and all its elements are smaller or equal to {@code end}.
     */
    public static <T> T multiplyAll(T start, T end, T step, MathCalculator<T> mc, SVFunction<T> f){
        if(!mc.isComparable()){
            throw new ArithmeticException("MathCalculator of type "+mc.getNumberClass()+" is not comparable.");
        }
        T t =start;
        int signum = mc.compare(start,end);
        if(signum>0){
            return mc.getZero();
        }
        T re = mc.getZero();
        do{
            re = mc.multiply(re,f.apply(t));
            t = mc.add(t,step);
        }while (mc.compare(t,end)<=0);
        return re;
    }
}
