package cn.timelives.java.math.set;

import java.util.Objects;
import java.util.function.Function;

import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * Abstract interval is the superclass of all the interval. Interval is a set of real number which has clear 
 * upper bound and downer bound, which is often shown as {@literal [a,b]}, {@literal (a,b)},  {@literal [a,b)} or {@literal (a,b]}. 
 * The interval contains a number if the number fits: {@code downerBound <= number <= upperBound}, but whether the 
 * equal sign is suitable is dependent on the type of the interval.
 * <p>The mathematical restriction is that {@code downerBound < upperBound}, but this class also permits that 
 *  {@code downerBound <= upperBound}, but if {@code downerBound == upperBound}, the interval must be a closed interval.
 * 
 * The interval is immutable, any method that returns an interval as the result will always creates a new one.
 * <p>
 * The given calculator should implement the method {@link MathCalculator#compare(Object, Object)}, 
 * and only when the method {@code lengthOf()} is called, the method {@link MathCalculator#subtract(Object, Object)} is needed. 
 * @author lyc
 * @param T the format of number to be stored
 *
 */
public abstract class Interval<T> extends AbstractMathSet<T> implements IntersectableSet<T,Interval<T>>{
	
	protected Interval(MathCalculator<T> mc) {
		super(mc);
	}
	/**
	 * Returns {@code true} if the given number is in the range of this interval. 
	 * @param n a number 
	 * @return {@code true} if {literal  this ∋ n} , otherwise {@code false}.
	 * @throws NullPointerException if n == null
	 */
	public abstract boolean contains(T n);
	/**
	 * Returns the upper bound of this interval,which means for any number {@code n in this} , {@code n <= upperBound}.
	 * If this interval doesn't have an upper bound, {@code null} will be returned.
	 * @return the upper bound of this interval, or {@code null}
	 */
	public abstract T upperBound();
	/**
	 * Returns whether is upper bound is included in this interval.
	 * @return {@code true} if upper bound is includes, otherwise {@code false}
	 */
	public abstract boolean isUpperBoundInclusive();
	
	
	/**
	 * Returns the downer bound of this interval, which means for any number {@code n in this} , {@code n >= downerBound}.
	 *  If this interval doesn't have an downer bound, {@code null} will be returned.
	 * @return the downer bound of this interval, or {@code null}
	 */
	public abstract T downerBound();
	
	/**
	 * Returns whether is downer bound is included in this interval.
	 * @return {@code true} if downer bound is includes, otherwise {@code false}
	 */
	public abstract boolean isDownerBoundInclusive();
	
	/**
	 * Returns the length of this interval , the length of this interval is equal to {@code upperBound - downerBound}.
	 * If either upper bound or downer bound does not exist, {@code null} will be returned.
	 * @return {@code upperBound - downerBound},or {@code null}
	 */
	public abstract T lengthOf();
	/**
	 * Returns a new interval that fits {@code downerBound = this.downerBound} and {@code upperBound = n}.Whether the new 
	 * interval should include the number {@code n} is determined by this interval.For example , if this = [0,5) , then 
	 * {@code downerPart(4)} will be {@code [0,4)}.
	 * @param n a number 
	 * @return a new interval
	 * @throws NullPointerException if n == null 
	 * @throws IllegalArgumentException if {@code n<= downerBound}  or {@code n>= upperBound}
	 * @see #downerPart(T, boolean) 
	 */
	public abstract Interval<T> downerPart(T n);
	/**
	 * Returns a new interval that fits {@code downerBound = this.downerBound} and {@code upperBound = n}.Whether the new 
	 * interval should include the number {@code n} is determined by {@code include}.For example , if this = [0,5) , then 
	 * {@code downerPart(4,true)} will be {@code [0,4]}.
	 * @param n a number 
	 * @param include determines whether the bound should be inclusive
	 * @return a new interval 
	 * @throws IllegalArgumentException if {@code n<= downerBound} or {@code n>= upperBound}
	 */
	public abstract Interval<T> downerPart(T n,boolean include);
	/**
	 * Returns a new interval that fits {@code upperBound = this.upperBound} and {@code downerBound = n}.Whether the new 
	 * interval should include the number {@code n} is determined by this interval.For example , if this = [0,5) , then 
	 * {@code upperPart(1)} will be {@code [1,5)}.
	 * @param n a number 
	 * @return a new interval
	 * @throws NullPointerException if n == null 
	 * @throws IllegalArgumentException if {@code n>= upperBound} or {@code n <= downerBound}
	 * @see #upperPart(T, boolean) 
	 */
	public abstract Interval<T> upperPart(T n);
	/**
	 * Returns a new interval that fits {@code upperBound = this.upperBound} and {@code downerBound = n}.Whether the new 
	 * interval should include the number {@code n} is determined by {@code include}.For example , if this = [0,5) , then 
	 * {@code upperPart(1,false)} will be {@code (1,5)}.
	 * @param n a number 
	 * @param include determines whether the bound should be inclusive
	 * @return a new interval
	 * @throws NullPointerException if n == null 
	 * @throws IllegalArgumentException if {@code n>= upperBound} or {@code n <= downerBound}
	 * 
	 */
	public abstract Interval<T> upperPart(T n,boolean include);
	/**
	 * Returns a new interval that {@code upperBound = n} and {@code downerBound = this.downerBound}.Whether the new 
	 * interval should include the number {@code n} is determined by this interval.
	 * @param n a number 
	 * @return a new interval
	 * @throws IllegalArgumentException if {@code n <= upperBound} 
	 * @throws NullPointerException if n == null
	 */
	public abstract Interval<T> expandUpperBound(T n);
	/**
	 * Returns a new interval that {@code upperBound = n} and {@code downerBound = this.downerBound}.Whether the new 
	 * interval should include the number {@code n} is determined by {@code include}.
	 * @param n a number 
	 * @param include determines whether the bound should be inclusive
	 * @return a new interval
	 * @throws IllegalArgumentException if {@code n <= upperBound}
	 * @throws NullPointerException if n == null
	 */
	public abstract Interval<T> expandUpperBound(T n,boolean include);
	
	
	
	/**
	 * Returns a new interval that {@code downerBound = n} and {@code upperBound = this.upperBound}.Whether the new 
	 * interval should include the number {@code n} is determined by this interval.
	 * @param n a number 
	 * @return a new interval
	 * @throws IllegalArgumentException if {@code n >= downerBound}
	 * @throws NullPointerException if n == null
	 */
	public abstract Interval<T> expandDownerBound(T n);
	/**
	 * Returns a new interval that {@code downerBound = n} and {@code upperBound = this.upperBound}.Whether the new 
	 * interval should include the number {@code n} is determined by {@code include}.
	 * @param n a number 
	 * @param include determines whether the bound should be inclusive
	 * @return a new interval
	 * @throws IllegalArgumentException if {@code n >= downerBound}
	 * @throws NullPointerException if n == null
	 */
	public abstract Interval<T> expandDownerBound(T n,boolean include);
	
	/**
	 * Returns a same type interval of {@code this}, whether  the new interval should be inclusive in upper or 
	 * downer bound is determined by {@code this}.
	 * @param downerBound the downer bound of the new interval 
	 * @param upperBound the new downer bound of the new interval 
	 * @return a new interval
	 * @throws IllegalArgumentException if {@code downerBound >= upperBound}
	 */
	public abstract Interval<T> sameTypeInterval(T downerBound,T upperBound);
	
	
	/**
	 * Returns {@code true} if the given interval is in the range of this.If {@code iv} has same upper or downer bound 
	 * with {@code this} and the bound in this is exclusive while in {@code iv} is inclusive, then {@literal iv ⊆  this} is false,
	 * so {@code false} will be returned.
	 * @param iv another interval
	 * @return {@code true} if {@literal iv ⊆  this} , else {@code false}
	 */
	public abstract boolean contains(Interval<T> iv);
	/**
	 * Returns a new interval that equals to the complement of {@code this} and {@code iv} , 
	 * which is expressed as {@literal iv ∩ this} in mathematical. If the complement is empty
	 * then {@code null} will be returned.
	 * @param iv a interval, or {@code null}
	 * @return {@literal iv ∩ this} or {@code null} if the result is an empty set.
	 */
	public abstract Interval<T> intersect(Interval<T> iv);
	
	/**
	 * @see cn.timelives.java.math.set.MathSet#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public abstract <N> Interval<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	/**
	 * Returns the mathematical expression of this interval. Like {@literal (0,2)} or {@literal [2,3)}.
	 * @return a String representing this interval.
	 */
	@Override
	public abstract String toString();
	
	/**
	 * Returns the mathematical expression of this interval. Like {@literal (0,2)} or {@literal [2,3)}.
	 * @return a String representing this interval.
	 */
	@Override
	public abstract String toString(NumberFormatter<T> nf);
	
	
	/**
	 * Returns a closed interval between the two number, which must 
	 * meet the requirement that {@code downer<upper}
	 * @param downer the downer bound
	 * @param upper the upper bound
	 * @param mc a {@link MathCalculator}
	 * @return [downer,upper]
	 */
	public static <T> Interval<T> closedInterval(T downer,T upper,MathCalculator<T> mc){
		return instanceNonNull(downer, upper, true, true, mc);
	}
	/**
	 * Returns an open interval between the two number, which must 
	 * meet the requirement that {@code downer<upper}
	 * @param downer the downer bound
	 * @param upper the upper bound
	 * @param mc a {@link MathCalculator}
	 * @return (downer,upper)
	 */
	public static <T> Interval<T> openInterval(T downer,T upper,MathCalculator<T> mc){
		return instanceNonNull(downer, upper, false, false, mc);
	}
	/**
	 * Returns a left-open-right-closed interval between the two number, which must 
	 * meet the requirement that {@code downer<upper}
	 * @param downer the downer bound
	 * @param upper the upper bound
	 * @param mc a {@link MathCalculator}
	 * @return (downer,upper]
	 */
	public static <T> Interval<T> leftOpenRightClosed(T downer,T upper,MathCalculator<T> mc){
		return instanceNonNull(downer, upper, false, true, mc);
	}
	/**
	 * Returns a left-closed-right-open interval between the two number, which must 
	 * meet the requirement that {@code downer<upper}
	 * @param downer the downer bound
	 * @param upper the upper bound
	 * @param mc a {@link MathCalculator}
	 * @return [downer,upper)
	 */
	public static <T> Interval<T> leftClosedRightOpen(T downer,T upper,MathCalculator<T> mc){
		return instanceNonNull(downer, upper, true, false, mc);
	}
	/**
	 * Returns an interval from negative infinity to the upper bound.
	 * @param upper the upper bound
	 * @param closed determines whether this 
	 * @param mc a {@link MathCalculator}
	 * @return (-∞,upper) or (-∞,upper]
	 */
	public static <T> Interval<T> fromNegativeInf(T upper,boolean closed,MathCalculator<T> mc){
		return new IntervalI<T>(mc, null, Objects.requireNonNull(upper), 
				IntervalI.LEFT_OPEN_MASK | (closed ? 0 : IntervalI.RIGHT_OPEN_MASK));
	}
	/**
	 * Returns an interval from the downer bound to positive infinity.
	 * @param downer the downer bound
	 * @param closed determines whether this 
	 * @param mc a {@link MathCalculator}
	 * @return (downer,+∞) or [downer,+∞)
	 */
	public static <T> Interval<T> toPositiveInf(T downer,boolean closed,MathCalculator<T> mc){
		return new IntervalI<T>(mc, Objects.requireNonNull(downer), null, 
				IntervalI.RIGHT_OPEN_MASK | (closed ? 0 : IntervalI.LEFT_OPEN_MASK));
	}
	/**
	 * Returns the interval representing the whole real number, whose downer bound 
	 * is negative infinity and upper bound is positive infinity.
	 * @param mc a {@link MathCalculator}
	 * @return (-∞,+∞)
	 */
	public static <T> Interval<T> universe(MathCalculator<T> mc){
		return new IntervalI<T>(mc, null, null, IntervalI.RIGHT_OPEN_MASK | IntervalI.LEFT_OPEN_MASK);
	}
	
	static <T> Interval<T> instanceNonNull(T a,T b,boolean dc,boolean uc, MathCalculator<T> mc){
		return new IntervalI<T>(mc, Objects.requireNonNull(a), Objects.requireNonNull(b), dc, uc);
	}
}
