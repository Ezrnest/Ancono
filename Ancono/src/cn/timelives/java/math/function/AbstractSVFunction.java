/**
 * 2017-10-06
 */
package cn.timelives.java.math.function;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import cn.timelives.java.math.FieldMathObject;
import cn.timelives.java.math.calculus.Derivable;
import cn.timelives.java.math.numberModels.Fraction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;
import cn.timelives.java.math.set.Interval;
import cn.timelives.java.math.set.IntervalUnion;

/**
 * This class provides some basic useful functions such as 
 * @author liyicheng
 * 2017-10-06 10:02
 * 
 */
public abstract class AbstractSVFunction<T> extends FieldMathObject<T> implements SVFunction<T>{

	/**
	 * @param mc
	 */
	protected AbstractSVFunction(MathCalculator<T> mc) {
		super(mc);
	}
	
	/**
	 * Returns the String representation of this function, the prefix 'f(x)=' 
	 * should not be included.
	 */
	@Override
	public abstract String toString(NumberFormatter<T> nf);
	
	
	/*
	 * @see cn.timelives.java.math.FlexibleMathObject#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
	 */
	@Override
	public abstract <N> AbstractSVFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	/**
	 * Describe the function:
	 * <pre>ln(x)</pre>
	 * @author liyicheng
	 * 2017-10-10 18:37
	 *
	 * @param <T>
	 */
	public static final class Ln<T> extends AbstractSVFunction<T> implements Derivable<T,Power<T>>{
		/**
		 * @param mc
		 */
		Ln(MathCalculator<T> mc) {
			super(mc);
		}

		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.ln(x);
		}

		/*
		 * @see cn.timelives.java.math.calculus.Derivable#derive()
		 */
		@Override
		public Power<T> derive() {
			return new Power<>(mc,mc.getOne(),Fraction.NEGATIVE_ONE);
		}
		
		/*
		 * @see cn.timelives.java.math.function.MathFunction#domain()
		 */
		@Override
		public Interval<T> domain() {
			return Interval.positive(mc);
		}

		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
		 */
		@Override
		public <N> Ln<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Ln<>(newCalculator);
		}

		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(FieldMathObject<T> obj) {
			if(obj instanceof Log) {
					if(mc.isEqual(((Log<T>)obj).a, mc.constantValue(MathCalculator.STR_E))) {
						return true;
					}
				return false;
			}
			if(! (obj instanceof Ln)) {
				return false;
			}
			return true; 
		}

		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#toString(cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public String toString(NumberFormatter<T> nf) {
			return "ln(x)";
		}
	}
	/**
	 * Describe the function:
	 * <pre>log<sup>a</sup>x</pre>
	 *  where {@literal a>0 && a!=1}
	 * @author liyicheng
	 * 2017-10-10 18:37
	 * @param <T>
	 */
	public static final class Log<T> extends AbstractSVFunction<T> implements Derivable<T,Power<T>>{
		private final T a;
		/**
		 * @param mc
		 */
		protected Log(MathCalculator<T> mc,T a) {
			super(mc);
			this.a = a;
		}
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.log(a, x);
		}
		/*
		 * @see cn.timelives.java.math.calculus.Derivable#derive()
		 */
		@Override
		public Power<T> derive() {
			return new Power<>(mc, mc.reciprocal(mc.ln(a)), Fraction.NEGATIVE_ONE);
		}
		/*
		 * @see cn.timelives.java.math.function.MathFunction#domain()
		 */
		@Override
		public Interval<T> domain() {
			return Interval.positive(mc);
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public String toString(NumberFormatter<T> nf) {
			return "log("+nf.format(a, mc)+",x)";
		}
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
		 */
		@Override
		public <N> Log<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Log<>(newCalculator,mapper.apply(a));
		}
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(FieldMathObject<T> obj) {
			if(obj instanceof Ln) {
				return ((Ln<T>)obj).valueEquals(this);
			}
			if(! (obj instanceof Log)) {
				return false;
			}
			Log<T> log = (Log<T>) obj;
			return mc.isEqual(a, log.a);
		}
		
		
		
	}
	
	/**
	 * Describes the power function:
	 * <pre>a*x^n</pre>
	 * where n is a rational number.
	 * @author liyicheng
	 * 2017-10-10 19:04
	 *
	 * @param <T>
	 */
	public static final class Power<T> extends AbstractSVFunction<T> implements Derivable<T,Power<T>>{
		/**
		 * @param mc
		 */
		Power(MathCalculator<T> mc,T a,Fraction n) {
			super(mc);
			this.a = a;
			this.n = n;
		}
		/**
		 * @param mc
		 */
		Power(MathCalculator<T> mc,Fraction n) {
			this(mc,mc.getOne(),n);
		}
		/**
		 * 
		 */
		Power(MathCalculator<T> mc) {
			this(mc,mc.getZero(),Fraction.ONE);
		}
		
		private final T a;
		private final Fraction n;
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			if(n.getSignum()==0) {
				return a;
			}
			if(mc.isZero(a)) {
				return mc.getZero();
			}
			T t;
			if(n.getDenominator()==1) {
				t =  mc.pow(x, n.getNumerator());
			}else if(n.getNumerator()==1){
				t =  mc.nroot(x, n.getDenominator());
			}else {
				t= mc.nroot(mc.pow(x, n.getNumerator()), n.getDenominator());
			}
			if(n.getSignum() <0) {
				t = mc.reciprocal(t);
			}
			return mc.multiply(a, t);
		}
		/*
		 * @see cn.timelives.java.math.calculus.Derivable#derive()
		 */
		@Override
		public Power<T> derive() {
			if(mc.isZero(a)) {
				return this;
			}
			if(n.getSignum() == 0) {
				return new Power<>(mc,mc.getZero(),Fraction.ONE);
			}
			Fraction _n = n.minus(Fraction.ONE);
			T _a = mc.divideLong(mc.multiplyLong(a, n.getNumerator()), n.getDenominator());
			return new Power<T>(mc, _a, _n);
		}
		
		private IntervalUnion<T> domain;
		
		/**
		 * Gets the a:<pre>a*x^n</pre>
		 * @return the a
		 */
		public T getA() {
			return a;
		}
		
		/**
		 * Gets the n:<pre>a*x^n</pre>
		 * @return the n as a fraction
		 */
		public Fraction getN() {
			return n;
		}
		
		/*
		 * @see cn.timelives.java.math.function.MathFunction#domain()
		 */
		@Override
		public IntervalUnion<T> domain() {
			if(domain ==null) {
				IntervalUnion<T> dom;
				if(n.getSignum()>=0) {
					if(n.getDenominator() % 2 == 1) {
						dom =  IntervalUnion.universe(mc);
					}else {
						dom = IntervalUnion.valueOf(Interval.toPositiveInf(mc.getZero(), true, mc));
					}
				}else {
					if(n.getDenominator() % 2 == 1) {
						dom =  IntervalUnion.except(mc.getZero(),mc);
					}else {
						dom = IntervalUnion.valueOf(Interval.positive(mc));
					}
				}
				domain = dom;
			}
			return domain;
			
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public String toString(NumberFormatter<T> nf) {
			if(mc.isZero(a)) {
				return "0";
			}else if(n.getSignum()==0) {
				return nf.format(a, mc);
			}
			StringBuilder sb = new StringBuilder();
			if(!mc.isEqual(a, mc.getOne())) {
				sb.append(nf.format(a, mc));
			}
			sb.append("x");
			if(n.getDenominator() == 1 && n.getSignum() > 0) {
				if(n.getNumerator()!=1) {
					sb.append("^").append(n.getNumerator());
				}
			}else {
				sb.append("^(").append(n.toString()).append(")");
			}
			return null;
		}
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
		 */
		@Override
		public <N> Power<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Power<N>(newCalculator, mapper.apply(a), n);
		}
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(FieldMathObject<T> obj) {
			if(!(obj instanceof Power)) {
				return false;
			}
			Power<T> p = (Power<T>) obj;
			if(mc.isZero(a)) {
				return mc.isZero(p.a);
			}
			return mc.isEqual(a, p.a) && n.equals(p.n);
		}
		
	}
	/**
	 * Describes the exponential function:
	 * <pre>c*a^x</pre>
	 * where {@code c!=0 && a > 0 && a!=1}
	 * @author liyicheng
	 * 2017-10-10 19:04
	 *
	 * @param <T>
	 */
	public static final class Exp<T> extends AbstractSVFunction<T> implements Derivable<T,Exp<T>>{
		private final T a,c;
		/**
		 * @param mc
		 */
		Exp(MathCalculator<T> mc,T c,T a) {
			super(mc);
			this.a = a;
			this.c = c;
		}
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.multiply(c,mc.exp(a, x));
		}
		/*
		 * @see cn.timelives.java.math.calculus.Derivable#derive()
		 */
		@Override
		public Exp<T> derive() {
			return new Exp<>(mc,mc.multiply(c, mc.ln(a)),a);
		}
		
		/**
		 * Gets the a:<pre>c*a^x</pre>
		 * @return the a
		 */
		public T getA() {
			return a;
		}
		
		/**
		 * Gets the c:<pre>c*a^x</pre>
		 * @return the c
		 */
		public T getC() {
			return c;
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public String toString(NumberFormatter<T> nf) {
			StringBuilder sb = new StringBuilder();
			if(!mc.isEqual(mc.getOne(), a)) {
				sb.append(nf.format(a, mc));
			}
			String as = nf.format(a, mc);
			if(as.length()==1) {
				sb.append(as);
			}else {
				sb.append('(').append(as).append(')');
			}
			sb.append("^x");
			
			return sb.toString();
		}
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
		 */
		@Override
		public <N> Exp<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Exp<N>(newCalculator, mapper.apply(c), mapper.apply(a));
		}
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(FieldMathObject<T> obj) {
			if(obj instanceof Ex) {
				return ((Ex<T>)obj).valueEquals(this);
			}
			if(! (obj instanceof Exp)) {
				return false;
			}
			Exp<T> exp = (Exp<T>) obj;
			return mc.isEqual(a, exp.a) && mc.isEqual(c, exp.c);
		}
	}
	
	/**
	 * Returns the power function:
	 * <pre>e^x</pre>
	 * where {@code e} is the natural base of logarithm.
	 * @author liyicheng
	 * 2017-10-10 19:04
	 *
	 * @param <T>
	 */
	public static final class Ex<T> extends AbstractSVFunction<T> implements Derivable<T,Ex<T>>{
		/**
		 * @param mc
		 */
		Ex(MathCalculator<T> mc) {
			super(mc);
		}
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.exp(x);
		}
		/*
		 * @see cn.timelives.java.math.calculus.Derivable#derive()
		 */
		@Override
		public Ex<T> derive() {
			return this;
		}
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.NumberFormatter)
		 */
		@Override
		public String toString(NumberFormatter<T> nf) {
			return "e^x";
		}
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.timelives.java.math.numberModels.MathCalculator)
		 */
		@Override
		public <N> Ex<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Ex<N>(newCalculator);
		}
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(FieldMathObject<T> obj) {
			if(obj instanceof Exp) {
				Exp<T> exp = (Exp<T>)obj;
				return mc.isEqual(mc.getOne(), exp.c) && mc.isEqual(mc.constantValue(MathCalculator.STR_E), exp.a);
			}
			return obj instanceof Ex;
		}
	}
	private static final Map<MathCalculator<?>,Ex<?>> expmap = new ConcurrentHashMap<>();
	private static final Map<MathCalculator<?>,Ln<?>> lnmap = new ConcurrentHashMap<>();
	
	/**
	 * Returns the function : {@literal e^x}
	 * @param mc a {@link MathCalculator}
	 * @return {@literal e^x}
	 */
	public static <T> Ex<T> naturalExp(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		Ex<T> ex = (Ex<T>) expmap.get(mc);
		if(ex == null) {
			ex = new Ex<>(mc);
			expmap.put(mc, ex);
		}
		return ex;
	}
	
	/**
	 * Returns the function : {@literal ln(x)}
	 * @param mc a {@link MathCalculator}
	 * @return {@literal ln(x)}
	 */
	public static <T> Ln<T> naturalLog(MathCalculator<T> mc){
		@SuppressWarnings("unchecked")
		Ln<T> ex = (Ln<T>) lnmap.get(mc);
		if(ex == null) {
			ex = new Ln<>(mc);
			lnmap.put(mc, ex);
		}
		return ex;
	}
	
	/**
	 * Returns the exponential function:
	 * <pre>c*a^x</pre>
	 * where {@code c!=0 && a > 0 && a!=1}
	 * @param mc a {@link MathCalculator}
	 * @return <pre>c*a^x</pre>
	 */
	public static <T> Exp<T> exp(T a,T c,MathCalculator<T> mc){
		if(mc.isZero(c)) {
			throw new IllegalArgumentException("c == 0");
		}
		if(mc.compare(a, mc.getZero())<=0 || mc.isEqual(mc.getOne(), a)) {
			throw new IllegalArgumentException("a <= 0 || a==1");
		}
		return new Exp<T>(mc, c, a);
	}
	/**
	 * Returns the exponential function:
	 * <pre>a^x</pre>
	 * where {@code a > 0 && a!=1}
	 * @param mc a {@link MathCalculator}
	 * @return <pre>a^x</pre>
	 */
	public static <T> Exp<T> exp(T a,MathCalculator<T> mc){
		if(mc.compare(a, mc.getZero())<=0 || mc.isEqual(mc.getOne(), a)) {
			throw new IllegalArgumentException("a <= 0 || a==1");
		}
		return new Exp<T>(mc, mc.getOne(), a);
	}
	/**
	 * Returns the function:
	 * <pre>log<sup>a</sup>x</pre>
	 * where {@literal a>0 && a!=1}
	 * @param a the base
	 * @param mc a {@link MathCalculator}
	 * @return <pre>log<sup>a</sup>x</pre>
	 */
	public static <T> Log<T> log(T a,MathCalculator<T> mc){
		if(mc.compare(a, mc.getZero())<=0 || mc.isEqual(mc.getOne(), a)) {
			throw new IllegalArgumentException("a <= 0 || a==1");
		}
		return new Log<T>(mc, a);
	}
	/**
	 * Returns the power function:
	 * <pre>a*x^n</pre>
	 * where n is a rational number.
	 * @param a
	 * @param n
	 * @param mc a {@link MathCalculator}
	 * @return <pre>a*x^n</pre>
	 */
	public static <T> Power<T> pow(T a,Fraction n,MathCalculator<T> mc){
		if(mc.isZero(a)) {
			return new Power<>(mc);
		}
		return new Power<>(mc,a,n);
	}
	
	/**
	 * Returns the power function:
	 * <pre>x^n</pre>
	 * where n is a rational number.
	 * @param n
	 * @param mc a {@link MathCalculator}
	 * @return <pre>x^n</pre>
	 */
	public static <T> Power<T> pow(Fraction n,MathCalculator<T> mc){
		return new Power<>(mc, n);
	}
	
}
