/**
 * 2018-03-05
 */
package cn.ancono.math.function;

import cn.ancono.math.property.Invertible;
import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

/**
 * A bijection function is a function between the elements of two sets,
 * where each element of one set is paired with exactly one element of the other set,
 * and each element of the other set is paired with exactly one element of the first set.
 * <p>
 * In addition to method {@code apply(P)}, an inverted method of {@code deply(R)} is added.
 * <p>
 * See: <a href="https://en.wikipedia.org/wiki/Bijection">Bijection</a>
 *
 * @author liyicheng
 * 2018-03-05 17:35
 */
public interface Bijection<P, R> extends MathFunction<P, R>, Invertible<Bijection<R, P>> {

    /**
     * "Deplies" the function, returns such {@code x} that {@code this.apply(x)=y}.
     *
     * @param y the parameter
     * @return <pre>f<sup>-1</sup>(y)</pre>
     */
    P deply(@NotNull R y);


    /**
     * Returns the inverse of this bijection, which is notated in math as
     * <pre>f<sup>-1</sup></pre>
     *
     * @return <pre>f<sup>-1</sup></pre>
     * @see cn.ancono.math.property.Invertible#inverse()
     */
    @Override
    default Bijection<R, P> inverse() {
        Bijection<P, R> f = this;
        return new Bijection<>() {

            @NotNull
            @SuppressWarnings("SuspiciousNameCombination")//inverse
            @Override
            public P apply(@NotNull R x) {
                return f.deply(x);
            }

            @SuppressWarnings("SuspiciousNameCombination")//inverse
            @Override
            public R deply(@NotNull P y) {
                return f.apply(y);
            }

            /*
             * @see cn.ancono.math.function.Bijection#inverse()
             */
            @Override
            public Bijection<P, R> inverse() {
                return f;
            }
        };
    }

    @SuppressWarnings("rawtypes")
    Bijection IDENTITY = new Bijection() {
        @Override
        public Object deply(@NotNull Object y) {
            return y;
        }

        @NotNull
        @Override
        public Object apply(@NotNull Object x) {
            return x;
        }

        @Override
        public Bijection inverse() {
            return this;//identity
        }
    };

    /**
     * Composes two bijection to a new one, applying {@code before} first.
     *
     * @param before the function that applies first
     * @param <V>    the parameter type of {@code before}.
     * @return a composed bijection
     * @see #compose(Function)
     * @see #andThenBi(Bijection)
     */
    default <V> Bijection<V, R> composeBi(Bijection<V, P> before) {
        Objects.requireNonNull(before);
        Bijection<P, R> after = this;
        return new Bijection<>() {
            @Override
            public V deply(@NotNull R y) {
                return before.deply(after.deply(y));
            }

            @NotNull
            @Override
            public R apply(@NotNull V x) {
                return after.apply(before.apply(x));
            }
        };
    }

    /**
     * Composes two bijection to a new one, applying {@code this} first.
     *
     * @param after the function that applies after {@code this}
     * @param <V>   the return type of {@code after}.
     * @return a composed bijection
     * @see #andThen(Function)
     * @see #composeBi(Bijection)
     */
    @SuppressWarnings("unused")
    default <V> Bijection<P, V> andThenBi(Bijection<R, V> after) {
        return after.composeBi(this);
    }

    /**
     * Gets the identity function of bijection.
     *
     * @param <T> the parameter and return type of the bijection
     * @return the identity function
     */
    @SuppressWarnings("unchecked")
    static <T> Bijection<T, T> identity() {
        return IDENTITY;
    }

    static <P, R> Bijection<P, R> of(MathFunction<P, R> f, MathFunction<R, P> inverse) {
        return new Bijection<>() {
            @NotNull
            @Override
            public R apply(@NotNull P x) {
                return f.apply(x);
            }

            @Override
            public P deply(@NotNull R y) {
                //noinspection SuspiciousNameCombination
                return inverse.apply(y);
            }
        };
    }

    /**
     * Returns a bijection between Integer and type T. It is required that the given elements are
     * mutually not equal.
     */
    @SafeVarargs
    static <T> Bijection<Integer, T> indexMapping(T... elements) {
        HashMap<T, Integer> map = new HashMap<>(elements.length);
        for (int i = 0; i < elements.length; i++) {
            map.put(Objects.requireNonNull(elements[i]), i);
        }
        if (map.size() != elements.length) {
            throw new IllegalArgumentException();
        }
        return Bijection.of(x -> elements[x], y -> Objects.requireNonNull(map.get(y)));
    }
}
