/**
 * 2017-10-13
 */
package cn.timelives.java.math.function;

import cn.timelives.java.math.MathObject;
import cn.timelives.java.math.calculus.SDerivable;
import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.numberModels.api.FlexibleNumberFormatter;
import org.jetbrains.annotations.NotNull;

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
    @NotNull
    @Override
    public abstract <N> CombinedFunction<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator);

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
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().add(f.apply(x), g.apply(x));
		}

		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
        @NotNull
        @Override
        public <N> Add<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return new Add<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}

		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
			return f.toString(nf)+" + "+g.toString(nf);
		}

		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
			if(!(obj instanceof Add)) {
				return false;
			}
			Add<T> add = (Add<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}


    }

    public static class AddD<T> extends Add<T> implements SDerivable<T, Add<T>> {


		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		AddD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
         * @see cn.timelives.java.math.calculus.SDerivable#derive()
		 */
        @NotNull
        @Override
		public Add<T> derive() {
            if (!(f instanceof SDerivable) || !(g instanceof SDerivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> fx = (SDerivable<T, AbstractSVFunction<T>>) f;
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> gx = (SDerivable<T, AbstractSVFunction<T>>) g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
            if (f_ instanceof SDerivable && g_ instanceof SDerivable) {
                return new AddD<>(f_, g_, getMc());
			}
            return new Add<>(f_, g_, getMc());
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
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().subtract(f.apply(x), g.apply(x));
		}

		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
        @NotNull
        @Override
        public <N> Subtract<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return new Subtract<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}

		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
			return f.toString(nf)+" - "+g.toString(nf);
		}

		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
			if(!(obj instanceof Subtract)) {
				return false;
			}
			Subtract<T> add = (Subtract<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}


    }

    public static class SubtractD<T> extends Subtract<T> implements SDerivable<T, Subtract<T>> {


		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		SubtractD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
         * @see cn.timelives.java.math.calculus.SDerivable#derive()
		 */
        @NotNull
        @Override
		public Subtract<T> derive() {
            if (!(f instanceof SDerivable) || !(g instanceof SDerivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> fx = (SDerivable<T, AbstractSVFunction<T>>) f;
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> gx = (SDerivable<T, AbstractSVFunction<T>>) g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
            if (f_ instanceof SDerivable && g_ instanceof SDerivable) {
                return new SubtractD<>(f_, g_, getMc());
			}
            return new Subtract<>(f_, g_, getMc());
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
		 */
		Multiply(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
		 * @see cn.timelives.java.math.function.SVFunction#apply(java.lang.Object)
		 */
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().multiply(f.apply(x), g.apply(x));
		}

		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
        @NotNull
        @Override
        public <N> Multiply<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return new Multiply<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}

		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
			return "("+f.toString(nf)+")*("+g.toString(nf)+")";
		}

		/*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
			if(!(obj instanceof Multiply)) {
				return false;
			}
			Multiply<T> add = (Multiply<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}


    }

    public static class MultiplyD<T> extends Multiply<T> implements SDerivable<T, Add<T>> {


		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		MultiplyD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
         * @see cn.timelives.java.math.calculus.SDerivable#derive()
		 */
        @NotNull
        @Override
		public Add<T> derive() {
            if (!(f instanceof SDerivable) || !(g instanceof SDerivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> fx = (SDerivable<T, AbstractSVFunction<T>>) f;
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> gx = (SDerivable<T, AbstractSVFunction<T>>) g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
            if (f_ instanceof SDerivable && g_ instanceof SDerivable) {
                MultiplyD<T> m1 = new MultiplyD<>(f_, g, getMc());
                MultiplyD<T> m2 = new MultiplyD<>(f, g_, getMc());
                return new AddD<>(m1, m2, getMc());
			}
            Multiply<T> m1 = new Multiply<>(f_, g, getMc());
            Multiply<T> m2 = new Multiply<>(f, g_, getMc());
            return new Add<>(m1, m2, getMc());
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
        @NotNull
        @Override
        public T apply(@NotNull T x) {
            return getMc().divide(f.apply(x), g.apply(x));
		}

		/*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
        @NotNull
        @Override
        public <N> Divide<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return new Divide<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}

		/*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
			return "("+f.toString(nf)+")/("+g.toString(nf)+")";
		}

        /*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
			if(!(obj instanceof Divide)) {
				return false;
			}
			Divide<T> add = (Divide<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}


    }

    public static class DivideD<T> extends Divide<T> implements SDerivable<T, Divide<T>> {


		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		DivideD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
         * @see cn.timelives.java.math.calculus.SDerivable#derive()
		 */
        @NotNull
        @Override
		public Divide<T> derive() {
            if (!(f instanceof SDerivable) || !(g instanceof SDerivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> fx = (SDerivable<T, AbstractSVFunction<T>>) f;
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> gx = (SDerivable<T, AbstractSVFunction<T>>) g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
            if (f_ instanceof SDerivable && g_ instanceof SDerivable) {
                MultiplyD<T> m1 = new MultiplyD<>(f_, g, getMc());
                MultiplyD<T> m2 = new MultiplyD<>(f, g_, getMc());
                SubtractD<T> s = new SubtractD<>(m1, m2, getMc());
                MultiplyD<T> m3 = new MultiplyD<>(g, g, getMc());
                return new DivideD<>(s, m3, getMc());
			}
            Multiply<T> m1 = new Multiply<>(f_, g, getMc());
            Multiply<T> m2 = new Multiply<>(f, g_, getMc());
            Subtract<T> s = new Subtract<>(m1, m2, getMc());
            Multiply<T> m3 = new Multiply<>(g, g, getMc());
            return new Divide<>(s, m3, getMc());
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
        @NotNull
        @Override
        public T apply(@NotNull T x) {
			return g.apply(f.apply(x));
		}

        /*
		 * @see cn.timelives.java.math.function.CombinedFunction#mapTo(java.util.function.Function, cn.timelives.java.math.MathCalculator)
		 */
        @NotNull
        @Override
        public <N> Combine<N> mapTo(@NotNull Function<T, N> mapper, @NotNull MathCalculator<N> newCalculator) {
			return new Combine<>(f.mapTo(mapper, newCalculator), g.mapTo(mapper, newCalculator), newCalculator);
		}

        /*
		 * @see cn.timelives.java.math.function.AbstractSVFunction#toString(cn.timelives.java.math.numberModels.api.NumberFormatter)
		 */
        @NotNull
        @Override
        public String toString(@NotNull FlexibleNumberFormatter<T, MathCalculator<T>> nf) {
			return "("+f.toString(nf)+")/("+g.toString(nf)+")";
		}

        /*
		 * @see cn.timelives.java.math.FlexibleMathObject#valueEquals(cn.timelives.java.math.FlexibleMathObject)
		 */
		@Override
        public boolean valueEquals(@NotNull MathObject<T> obj) {
			if(!(obj instanceof Combine)) {
				return false;
			}
			Combine<T> add = (Combine<T>) obj;
			return f.valueEquals(add.f) && g.valueEquals(add.g);
		}


    }

    public static class CombineD<T> extends Combine<T> implements SDerivable<T, Multiply<T>> {


		/**
		 * @param f
		 * @param g
		 * @param mc
		 */
		CombineD(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
			super(f, g, mc);
		}

		/*
         * @see cn.timelives.java.math.calculus.SDerivable#derive()
		 */
        @NotNull
        @Override
		public Multiply<T> derive() {
            if (!(f instanceof SDerivable) || !(g instanceof SDerivable)) {
				throw new UnsupportedOperationException("Not Deriable");
			}
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> fx = (SDerivable<T, AbstractSVFunction<T>>) f;
			@SuppressWarnings("unchecked")
            SDerivable<T, AbstractSVFunction<T>> gx = (SDerivable<T, AbstractSVFunction<T>>) g;
			AbstractSVFunction<T> f_ = fx.derive();
			AbstractSVFunction<T> g_ = gx.derive();
            if (f_ instanceof SDerivable && g_ instanceof SDerivable) {
                return new MultiplyD<>(f_, new CombineD<>(f, g_, getMc()), getMc());
			}
            return new Multiply<>(f_, new Combine<>(f, g_, getMc()), getMc());
		}
	}

    public static <T> Add<T> addOf(AbstractSVFunction<T> f,AbstractSVFunction<T> g,MathCalculator<T> mc){
		return new Add<>(Objects.requireNonNull(f),Objects.requireNonNull(g),mc);
	}

    public static <T> Add<T> addOfDerivable(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
        return new AddD<>(Objects.requireNonNull(f), Objects.requireNonNull(g), mc);
    }

    public static <T> CombineD<T> combineOfDerivable(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
        return new CombineD<>(Objects.requireNonNull(f),
                Objects.requireNonNull(g), mc);
    }

    public static <T> MultiplyD<T> multiplyOfDerivable(AbstractSVFunction<T> f, AbstractSVFunction<T> g, MathCalculator<T> mc) {
        return new MultiplyD<>(Objects.requireNonNull(f),
                Objects.requireNonNull(g), mc);
    }
	
}
