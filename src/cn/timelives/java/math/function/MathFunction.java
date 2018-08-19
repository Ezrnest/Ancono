package cn.timelives.java.math.function;

import cn.timelives.java.math.set.MathSet;
import cn.timelives.java.math.set.MathSets;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Function;
/**
 * MathFunction is an interface indicates math functions. Math function is a kind of special function.
 * This function must perform like a real math function:
 * <ul>
 * <li>It does NOT make change to parameter: Any parameter should not be changed in this function. 
 * <li>It is <tt>consistent</tt>:If this function is applied with identity parameters for multiple times,the
 * result should be the identity.
 * </ul>
 * 
 * @author lyc
 *
 * @param <P> parameter type 
 * @param <R> result type
 */
@FunctionalInterface
public interface MathFunction<P,R> extends Function<P,R>{
	@Override
    @NotNull
	R apply(P x);
	
	/**
	 * Returns the domain of this MathFunction, the 
	 * implementor should override this method to specify the domain.
	 * @return a MathSet representing the domain
	 */
    @NotNull
    default MathSet<P> domain() {
		return MathSets.universe();
	}
	
	@SuppressWarnings("rawtypes")
    MathFunction identity = t -> t;
	/**
	 * Returns a type-safe MathFunction whose result is the parameter itself.
	 * @return a MathFunction
	 */
	@SuppressWarnings("unchecked")
    static <T> MathFunction<T, T> identity() {
        return identity;
    }

    static <P, R> MathFunction<P, R> fromFunction(MathSet<P> domain, Function<P, R> f) {
        Objects.requireNonNull(f);
        Objects.requireNonNull(domain);
        return new MathFunction<>() {
            @NotNull
            @Override
            public R apply(P x) {
                return f.apply(x);
            }

            @NotNull
            @Override
            public MathSet<P> domain() {
                return domain;
            }
        };
    }

    static <T, S, R> MathFunction<T, R> compose(MathFunction<T, S> f, MathFunction<S, R> g) {
        return new MathFunction<>() {
            @NotNull
            @Override
            public R apply(T x) {
                return g.apply(f.apply(x));
            }

            @NotNull
            @Override
            public MathSet<T> domain() {
                return f.domain();
            }
        };
    }

    static <T, S> SVFunction<T> composeSV(MathFunction<T, S> f, MathFunction<S, T> g) {
        return new SVFunction<T>() {
            @NotNull
            @Override
            public T apply(T x) {
                return g.apply(f.apply(x));
            }

            @NotNull
            @Override
            public MathSet<T> domain() {
                return f.domain();
            }
        };
    }
}
