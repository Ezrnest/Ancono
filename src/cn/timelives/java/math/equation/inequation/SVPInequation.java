/**
 * 2017-10-06
 */
package cn.timelives.java.math.equation.inequation;

import cn.timelives.java.math.*;
import cn.timelives.java.math.algebra.Polynomial;
import cn.timelives.java.math.equation.Type;
import cn.timelives.java.math.function.AbstractSVPFunction;
import cn.timelives.java.math.function.AbstractSVPFunction.LinearFunction;
import cn.timelives.java.math.function.QuadraticFunction;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import cn.timelives.java.math.numberModels.Utils;
import cn.timelives.java.math.property.Solveable;
import cn.timelives.java.math.set.Interval;
import cn.timelives.java.math.set.IntervalUnion;

import java.util.List;
import java.util.function.Function;

/**
 * SVPEquation stands for <i>single variable polynomial inequation</i>.
 * Generally, the inequation can be shown as 
 * <pre>an*x^n + ... + a1*x + a0 <i>op</i> 0 , (an!=0,n>=0)</pre>
 * where <i>op</i> is one of the inequation operation(Inequation{@link #getInquationType()}).
 * @author liyicheng
 * 2017-10-06 09:33
 *
 */
public abstract class SVPInequation<T> extends SVInquation<T> implements Polynomial<T> {
	protected final int mp;
	
	/**
	 * @param mc
	 * @param op
	 */
	protected SVPInequation(MathCalculator<T> mc, Type op, int mp) {
		super(mc, op);
		this.mp = mp;
	}
	/*
	 * @see cn.timelives.java.math.algebra.Polynomial#getMaxPower()
	 */
	@Override
	public int getDegree() {
		return mp;
	}
	/**
	 * Determine whether the two inequations are equal, this method only 
	 * compare the corresponding coefficient. 
	 * <p>Therefore, for example, 
	 * {@literal 2x>0} and {@literal x>0} are considered to be not the same.
	 * This assures that if two equations are equal, then the functions returned
	 * by {@link #asFunction()} are equal. 
	 * 
	 */
	@Override
	public boolean valueEquals(MathObject<T> obj) {
		if(obj == this) {
			return true;
		}
		if (!(obj instanceof SVPInequation)) {
			return false;
		}
		SVPInequation<T> sv = (SVPInequation<T>) obj;
		return op == sv.op && Polynomial.isEqual(this,sv, mc::isEqual);
	}
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject, java.util.function.Function)
	 */
	@Override
	public <N> boolean valueEquals(MathObject<N> obj, Function<N, T> mapper) {
		if(obj == this) {
			return true;
		}
		if (!(obj instanceof SVPInequation)) {
			return false;
		}
		SVPInequation<N> sv = (SVPInequation<N>) obj;
		return op == sv.op && Polynomial.isEqual(this,sv, Utils.mappedIsEqual(mc, mapper));
	}
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(this == obj) {
			return true;
		}
		if(!(obj instanceof SVPInequation)) {
			return false;
		}
		SVPInequation<?> sv = (SVPInequation<?>)obj;
		return op == sv.op && Polynomial.isEqual(this, sv);
	}
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#hashCode()
	 */
	@Override
	public int hashCode() {
		return op.hashCode()*31 + Polynomial.hashCodeOf(this);
	}
	
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
		StringBuilder sb = new StringBuilder(Polynomial.stringOf(this, mc, nf));
		sb.append(' ').append(op.toString());
		sb.append(" 0");
		return sb.toString();
	}
	
	
	
	/*
	 * @see cn.timelives.java.math.SingleVInquation#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
	 */
	@Override
	public abstract <N> SVPInequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	
	
	static class FromFunction<T> extends SVPInequation<T>{
		final AbstractSVPFunction<T> f;
		/**
		 * @param mc
		 * @param op
		 */
		protected FromFunction(MathCalculator<T> mc, Type op,AbstractSVPFunction<T> f) {
			super(mc, op,f.getDegree());
			this.f = f;
		}
		/*
		 * @see cn.timelives.java.math.algebra.Polynomial#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			return f.getCoefficient(n);
		}
		
		/*
		 * @see cn.timelives.java.math.equation.inequation.SVPInequation#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> SVPInequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new FromFunction<>(newCalculator, op, f.mapTo(mapper, newCalculator));
		}
		/*
		 * @see cn.timelives.java.math.equation.SVCompareStructure#compute(java.lang.Object)
		 */
		@Override
		public T compute(T x) {
			return f.apply(x);
		}
	}
	
	/**
	 * Linear inequation.
	 * <pre>ax + b <i>op</i> 0</pre>
	 * The coefficient {@code a} must NOT be zero.
	 * @author liyicheng
	 * 2017-10-09 18:43
	 *
	 * @param <T>
	 */
	public static final class LinearInequation<T> extends SVPInequation<T> implements Solveable<T>{
		private final AbstractSVPFunction.LinearFunction<T> f;
		/**
		 * @param mc
		 * @param op
		 * @param f
		 */
		LinearInequation(MathCalculator<T> mc, Type op,AbstractSVPFunction.LinearFunction<T> f) {
			super(mc, op, 1);
			this.f = f;
		}
		
		/*
		 * @see cn.timelives.java.math.algebra.Polynomial#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			return f.getCoefficient(n);
		}
		
		/*
		 * @see cn.timelives.java.math.equation.SVCompareStructure#compute(java.lang.Object)
		 */
		@Override
		public T compute(T x) {
			return f.apply(x);
		}
		
		/*
		 * @see cn.timelives.java.math.equation.CompareStructure#asFunction()
		 */
		@Override
		public LinearFunction<T> asFunction() {
			return f;
		}

		/*
		 * @see cn.timelives.java.math.equation.inequation.SVPInequation#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> LinearInequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new LinearInequation<>(newCalculator, op, f.mapTo(mapper, newCalculator));
		}
		
		private Interval<T> solution;
		
		
		/**
		 * Returns an interval union representing the solution of this linear equation.
		 */
		@Override
		public Interval<T> getSolution() {
			if(solution==null) {
				T a = f.getCoefficient(1);
				T b = f.getCoefficient(0);
				//ax+b ? 0
					// x ? -b/a
				T x = mc.negate(mc.divide(b, a));
				Type op = this.op;
				if (compareZero(a) < 0) {
					op = op.negative();
				}
				switch (op) {
				case GREATER: {
					solution = Interval.toPositiveInf(x, false, mc);
					break;
				}
				case GREATER_OR_EQUAL: {
					solution = Interval.toPositiveInf(x, true, mc);
					break;
				}
				case LESS: {
					solution = Interval.fromNegativeInf(x, false, mc);
					break;
				}
				case LESS_OR_EQUAL: {
					solution = Interval.fromNegativeInf(x, true, mc);
					break;
				}
				default: {
					throw new AssertionError("The inequation type '" + op + "' is not valid");
				}
				}
			}
			
			return solution;
		}
	}
	/**
	 * Quadratic inequation:
	 * <pre>ax^2 + bx + c <i>op</i> 0</pre>
	 * It is required that {@code a!=0}.
	 * @author liyicheng
	 * 2017-10-09 18:43
	 *
	 * @param <T>
	 */
	public static final class QuadraticInequation<T> extends SVPInequation<T> implements Solveable<T> {
		private final QuadraticFunction<T> f;
		/**
		 * @param mc
		 * @param op
		 * @param f
		 */
		QuadraticInequation(MathCalculator<T> mc, Type op,QuadraticFunction<T> f) {
			super(mc, op, 2);
			this.f = f;
		}

		/*
		 * @see cn.timelives.java.math.algebra.Polynomial#getCoefficient(int)
		 */
		@Override
		public T getCoefficient(int n) {
			return f.getCoefficient(n);
		}

		/*
		 * @see cn.timelives.java.math.equation.SVCompareStructure#compute(java.lang.Object)
		 */
		@Override
		public T compute(T x) {
			return f.apply(x);
		}
		private IntervalUnion<T> solution;
		/*
		 * @see cn.timelives.java.math.property.Solveable#getSolution()
		 */
		@Override
		public IntervalUnion<T> getSolution() {
			if(solution == null) {
				IntervalUnion<T> solu;
				T a = f.getA();
				List<T> x1x2 = MathUtils.solveEquation(a, f.getB(), f.getC(), mc);
				Type op = this.op;
				if(compareZero(a)<0) {
					op = op.negative();
				}
				if(x1x2.size()==0) {
					if(op==Type.LESS ||op == Type.LESS_OR_EQUAL) {
						solu = IntervalUnion.empty(mc);
					}else {
						solu = IntervalUnion.universe(mc);
					}
				}else if(x1x2.size()==1) {
					T x = x1x2.get(0);
					switch (op) {
					case GREATER: {
						solu = IntervalUnion.except(x, mc);
						break;
					}
					case GREATER_OR_EQUAL: {
						solu = IntervalUnion.universe(mc);
						break;
					}
					case LESS: {
						solu = IntervalUnion.empty(mc);
						break;
					}
					case LESS_OR_EQUAL: {
						solu = IntervalUnion.single(x, mc);
						break;
					}
					default: {
						throw new AssertionError("The inequation type '" + op + "' is not valid");
					}
					}
				}else {
					T x1 = x1x2.get(0),
							x2 = x1x2.get(1);
					if(mc.compare(x1, x2)>0) {
						T t = x1;
						x1 = x2;
						x2 = t;
					}
					switch (op) {
					case GREATER: {
						solu = IntervalUnion.valueOf(Interval.fromNegativeInf(x1, false, mc),
								Interval.toPositiveInf(x2, false, mc));
						break;
					}
					case GREATER_OR_EQUAL: {
						solu = IntervalUnion.valueOf(Interval.fromNegativeInf(x1, true, mc),
								Interval.toPositiveInf(x2, true, mc));
						break;
					}
					case LESS: {
						solu = IntervalUnion.valueOf(Interval.openInterval(x1, x2, mc));
						break;
					}
					case LESS_OR_EQUAL: {
						solu = IntervalUnion.valueOf(Interval.closedInterval(x1, x2, mc));
						break;
					}
					default: {
						throw new AssertionError("The inequation type '" + op + "' is not valid");
					}
					}
				}
				solution = solu;
			}
			
			return solution;
		}

		/*
		 * @see cn.timelives.java.math.equation.inequation.SVPInequation#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> QuadraticInequation<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new QuadraticInequation<>(newCalculator, op, f.mapTo(mapper, newCalculator));
		}
		
	}
	
	/**
	 * Creates an SVPInequation from a list of coefficients. The index of the 
	 * coefficient is considered as the corresponding power of {@code x}. 
	 * For example, a list [1,2,3] represents for {@literal 3x^2+2x+1}.
	 * @param coes a list of coefficient
	 * @param op the inequation operation type
	 * @param mc a {@link MathCalculator}
	 * @return an inequation
	 */
	public static <T> SVPInequation<T> valueOf(List<T> coes,Type op,MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		T[] arr = (T[]) coes.toArray();
		for(int i=0;i<arr.length;i++) {
			if(arr[i]==null) {
				throw new NullPointerException("null in list: index = "+i);
			}
		}
		return new FromFunction<>(mc, op, AbstractSVPFunction.valueOf(coes, mc));
	}
	
	/**
	 * Creates an linear inequation.
	 * <pre>ax + b <i>op</i> 0</pre>
	 * The coefficient {@code a} must NOT be zero.
	 * @param a the coefficient of {@code x}
	 * @param b the constant
	 * @param op the inequation operation
	 * @param mc a {@link MathCalculator}
	 * @return a new LinearInequation
	 */
	public static <T> LinearInequation<T> linear(T a,T b,Type op,MathCalculator<T> mc){
		return new LinearInequation<>(mc, op, AbstractSVPFunction.linear(a, b, mc));
	}
	/**
	 * Creates a new quadratic inequation.
	 * <pre>ax^2 + bx + c <i>op</i> 0</pre>
	 * The coefficient {@code a} must NOT be zero.
	 * @param a the coefficient of {@code x^2}
	 * @param b the coefficient of {@code x}
	 * @param c the constant
	 * @param op the inequation operation
	 * @param mc a {@link MathCalculator}
	 * @return a new QuadraticInequation
	 */
	public static <T> QuadraticInequation<T> quadratic(T a,T b,T c,Type op,MathCalculator<T> mc){
		return new QuadraticInequation<>(mc, op, AbstractSVPFunction.quadratic(a, b, c, mc));
	}
	
}

	
