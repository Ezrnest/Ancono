/**
 * 2017-10-13
 */
package cn.timelives.java.math.function;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.algebra.calculus.Derivable;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;

import java.util.Objects;
import java.util.function.Function;

/**
 * A combined function is a combination of two functions, f(x) and g(x).
 * @author liyicheng
 * 2017-10-13 19:18
 *
 */
public abstract class CombinedFunction<T> extends AbstractSVFunction<T> {
	protected final AbstractSVFunction<T> f,g;
	
	/**
	 * 
	 */
	CombinedFunction(AbstractSVFunction<T> f,AbstractSVFunction<T> g,MathCalculator<T> mc) {
		super(mc);
		this.f = f;
		this.g = g;
	}
	
	
	
	/*
	 * @see cn.timelives.java.math.function.AbstractSVFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
	 */
	@Override
	public abstract <N> CombinedFunction<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator);
	
	/**
	 * Defines the combined function:
	 * <pre>f(x) + g(x)</pre>
	 * @author liyicheng
	 * 2017-10-13 19:25
	 * 
	 * @param <T>
	 */
	public static class Add<T> extends CombinedFunction<T>{
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		Add(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}
		
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.add(f.apply(x), g.apply(x));
		}
		
		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> Add<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Add<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
		@Override
		public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
			return f.toString(nf)+" + "+g.toString(nf);
		}
		
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			if(!(obj instanceof Add)) {
				return false;
			}
			Add<T> add = (Add<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}

		

	}
	
	public static class AddD<T> extends Add<T> implements Derivable<T, Add<T>>{
		
		
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		AddD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
		 * @see cn.timelives.java.math.algebra.calculus.Derivable#derive()
		 */
		@Override
		public Add<T> derive() {
			if(!(f instanceof Derivable) || !(g instanceof Derivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> fx = (Derivable<T, AbstractSVFunction<T>>)f;
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> gx = (Derivable<T, AbstractSVFunction<T>>)g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
			if(f_ instanceof Derivable && g_ instanceof Derivable) {
				return new AddD<>(f_,g_,mc);
			}
			return new Add<>(f_,g_,mc);
		}
	}
	

	/**
	 * Defines the combined function:
	 * <pre>f(x) - g(x)</pre>
	 * @author liyicheng
	 * 2017-10-13 19:25
	 * 
	 * @param <T>
	 */
	public static class Subtract<T> extends CombinedFunction<T>{
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		Subtract(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}
		
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.subtract(f.apply(x), g.apply(x));
		}
		
		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> Subtract<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Subtract<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
		@Override
		public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
			return f.toString(nf)+" - "+g.toString(nf);
		}
		
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			if(!(obj instanceof Subtract)) {
				return false;
			}
			Subtract<T> add = (Subtract<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}

		

	}
	
	public static class SubtractD<T> extends Subtract<T> implements Derivable<T, Subtract<T>>{
		
		
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		SubtractD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
		 * @see cn.timelives.java.math.algebra.calculus.Derivable#derive()
		 */
		@Override
		public Subtract<T> derive() {
			if(!(f instanceof Derivable) || !(g instanceof Derivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> fx = (Derivable<T, AbstractSVFunction<T>>)f;
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> gx = (Derivable<T, AbstractSVFunction<T>>)g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
			if(f_ instanceof Derivable && g_ instanceof Derivable) {
				return new SubtractD<>(f_,g_,mc);
			}
			return new Subtract<>(f_,g_,mc);
		}
	}
	
	/**
	 * Defines the combined function:
	 * <pre>f(x) * g(x)</pre>
	 * @author liyicheng
	 * 2017-10-13 19:25
	 * 
	 * @param <T>
	 */
	public static class Multiply<T> extends CombinedFunction<T>{
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		Multiply(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}
		
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.multiply(f.apply(x), g.apply(x));
		}
		
		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> Multiply<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Multiply<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
		@Override
		public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
			return "("+f.toString(nf)+")*("+g.toString(nf)+")";
		}
		
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			if(!(obj instanceof Multiply)) {
				return false;
			}
			Multiply<T> add = (Multiply<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}

		

	}
	
	public static class MultiplyD<T> extends Multiply<T> implements Derivable<T, Add<T>>{
		
		
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		MultiplyD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
		 * @see cn.timelives.java.math.algebra.calculus.Derivable#derive()
		 */
		@Override
		public Add<T> derive() {
			if(!(f instanceof Derivable) || !(g instanceof Derivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> fx = (Derivable<T, AbstractSVFunction<T>>)f;
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> gx = (Derivable<T, AbstractSVFunction<T>>)g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
			if(f_ instanceof Derivable && g_ instanceof Derivable) {
				MultiplyD<T> m1 = new MultiplyD<>(f_, g, mc);
				MultiplyD<T> m2 = new MultiplyD<>(f, g_, mc);
				return new AddD<>(m1,m2,mc);
			}
			Multiply<T> m1 = new Multiply<>(f_, g, mc);
			Multiply<T> m2 = new Multiply<>(f, g_, mc);
			return new Add<>(m1,m2,mc);
		}
	}
	
	/**
	 * Defines the combined function:
	 * <pre>f(x) / g(x)</pre>
	 * @author liyicheng
	 * 2017-10-13 19:25
	 * 
	 * @param <T>
	 */
	public static class Divide<T> extends CombinedFunction<T>{
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		Divide(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}
		
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return mc.divide(f.apply(x), g.apply(x));
		}
		
		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> Divide<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Divide<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
		@Override
		public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
			return "("+f.toString(nf)+")/("+g.toString(nf)+")";
		}
		
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			if(!(obj instanceof Divide)) {
				return false;
			}
			Divide<T> add = (Divide<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}

		

	}
	
	public static class DivideD<T> extends Divide<T> implements Derivable<T, Divide<T>>{
		
		
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		DivideD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
		 * @see cn.timelives.java.math.algebra.calculus.Derivable#derive()
		 */
		@Override
		public Divide<T> derive() {
			if(!(f instanceof Derivable) || !(g instanceof Derivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> fx = (Derivable<T, AbstractSVFunction<T>>)f;
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> gx = (Derivable<T, AbstractSVFunction<T>>)g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
			if(f_ instanceof Derivable && g_ instanceof Derivable) {
				MultiplyD<T> m1 = new MultiplyD<>(f_, g, mc);
				MultiplyD<T> m2 = new MultiplyD<>(f, g_, mc);
				SubtractD<T> s = new SubtractD<>(m1, m2, mc);
				MultiplyD<T> m3 = new MultiplyD<>(g, g, mc);
				return new DivideD<>(s,m3,mc);
			}
			Multiply<T> m1 = new Multiply<>(f_, g, mc);
			Multiply<T> m2 = new Multiply<>(f, g_, mc);
			Subtract<T> s = new Subtract<>(m1, m2, mc);
			Multiply<T> m3 = new Multiply<>(g, g, mc);
			return new Divide<>(s,m3,mc);
		}
	}
	
	/**
	 * Defines the combined function:
	 * <pre>g(f(x))</pre>
	 * @author liyicheng
	 * 2017-10-13 19:25
	 * 
	 * @param <T>
	 */
	public static class Combine<T> extends CombinedFunction<T>{
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		Combine(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}
		
		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
		@Override
		public T apply(T x) {
			return g.apply(f.apply(x));
		}
		
		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
		@Override
		public <N> Combine<N> mapTo(Function<T, N> mapper, MathCalculator<N> newCalculator) {
			return new Combine<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}
		
		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
		@Override
		public String toString(FlexibleNumberFormatter<T,MathCalculator<T>> nf) {
			return "("+f.toString(nf)+")/("+g.toString(nf)+")";
		}
		
		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
		public boolean valueEquals(MathObject<T> obj) {
			if(!(obj instanceof Combine)) {
				return false;
			}
			Combine<T> add = (Combine<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}

		

	}
	
	public static class CombineD<T> extends Combine<T> implements Derivable<T, Multiply<T>>{
		
		
		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		CombineD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
		 * @see cn.timelives.java.math.algebra.calculus.Derivable#derive()
		 */
		@Override
		public Multiply<T> derive() {
			if(!(f instanceof Derivable) || !(g instanceof Derivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> fx = (Derivable<T, AbstractSVFunction<T>>)f;
			@SuppressWarnings("unchecked")
			Derivable<T, AbstractSVFunction<T>> gx = (Derivable<T, AbstractSVFunction<T>>)g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
			if(f_ instanceof Derivable && g_ instanceof Derivable) {
				return new MultiplyD<>(f_, new CombineD<>(f, g_, mc), mc);
			}
			return new Multiply<>(f_, new Combine<>(f, g_, mc), mc);
		}
	}
	
	public static <T> Add<T> addOf(AbstractSVFunction<T> f,AbstractSVFunction<T> g,MathCalculator<T> mc){
		return new Add<>(Objects.requireNonNull(f),Objects.requireNonNull(g),mc);
	}
}
