/**
 * 2017-09-09
 */
package cn.timelives.java.math.set;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import cn.timelives.java.math.FlexibleMathObject;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.utilities.CollectionSup;
import cn.timelives.java.utilities.ModelPatterns;

/**
 * A union of intervals.
 * @author liyicheng
 * 2017-09-09 11:38
 *
 */
public class IntervalUnion<T> extends AbstractMathSet<T>{
	private List<Interval<T>> is;
	/**
	 * @param mc
	 * @param is a list of intervals, sorted.
	 */
	IntervalUnion(MathCalculator<T> mc,List<Interval<T>> is) {
		super(mc);
	}

	/**
	 * @see cn.timelives.java.math.set.MathSet#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(T t) {
		int pos=findSmallerDownerBound(t);
		if(pos >= 0 ){
			return is.get(pos).contains(t);
		}
		if(pos == -1){
			return false;
		}
		pos = - pos - 2;
		
		return is.get(pos).contains(t);
	}
	/**
	 * Returns the interval that contains {@code t}, or returns 
	 * {@code null} if it is not contained in all intervals.
	 * @param t a number
	 * @return
	 */
	public Interval<T> findInverval(T t){
		int pos=findSmallerDownerBound(t);
		Interval<T> candidate ;
		if(pos == -1){
			return null;
		}
		if(pos >= 0 ){
			candidate = is.get(pos);
		}else {
			pos = - pos - 2;
			candidate = is.get(pos);
		}
		if(candidate.contains(t)) {
			return candidate;
		}else {
			return null;
		}
	}
	
	/**
	 * Find the closet downer bound which is smaller to t.
	 * @param t
	 * @return
	 */
	private int findSmallerDownerBound(T t) {
		return ModelPatterns.binarySearch(0, is.size(), x -> {
			T downer = is.get(x).downerBound();
			if (downer == null) {
				return -1;
			}
			return mc.compare(downer, t);
		});
	}
	
	public IntervalUnion<T> unionWith(Interval<T> v){
		return null;
	}
	
	
	/**
	 * @see cn.timelives.java.math.set.MathSet#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public <N> IntervalUnion<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
		return new IntervalUnion<>(newCalculator, CollectionSup.mapList(is, x -> x.mapTo(mapper, newCalculator)));
	}

	/**
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
	 */
	@Override
	public boolean valueEquals(FlexibleMathObject<T> obj) {
		// TODO Auto-generated method stub
		return false;
	}

	/**
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		for(Interval<T> in : is){
			sb.append(in.toString(nf));
			sb.append('∪');
		}
		return sb.toString();
	}
	
	
	public static <T> IntervalUnion<T> valueOf(List<Interval<T>> intervals){
		
		//TODO
		return null;
	}

}
