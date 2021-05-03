package cn.ancono.math.algebra.abs

import cn.ancono.math.function.Bijection
import cn.ancono.math.function.MathFunction
import org.jetbrains.annotations.NotNull


/**
 * A marker interface for a homomorphism mapping.
 * Created at 2018/10/10 18:36
 * @author  liyicheng
 */
@FunctionalInterface
interface HomomorphismMapping<T, S> : MathFunction<T, S> {
    companion object {
        fun <T, S> fromFunction(f: java.util.function.Function<T, S>): HomomorphismMapping<T, S> = object : HomomorphismMapping<T, S> {
            override fun apply(x: T): S {
                return f.apply(x)
            }
        }

        fun <T, S, R> compose(f: HomomorphismMapping<S, R>, g: HomomorphismMapping<T, S>) =
                fromFunction(MathFunction.compose(f, g))

        fun <T, S, R> andThen(f: HomomorphismMapping<T, S>, g: HomomorphismMapping<S, R>) =
                fromFunction(MathFunction.andThen(f, g))

    }
}

/**
 * A marker interface for a isomorphism mapping.
 * Created at 2018/10/10 18:36
 * @author  liyicheng
 */
interface IsomorphismMapping<T, S> : HomomorphismMapping<T, S>, Bijection<T, S> {


    companion object {
        fun <T, S> fromBijection(f: Bijection<T, S>): IsomorphismMapping<T, S> =
                object : IsomorphismMapping<T, S> {
                    override fun apply(x: T): S {
                        return f.apply(x)
                    }

                    override fun deply(y: @NotNull S): T {
                        return f.deply(y)
                    }

                }

        fun <T, S, R> compose(f: IsomorphismMapping<S, R>, g: IsomorphismMapping<T, S>) =
                fromBijection(f.composeBi(g))

        fun <T, S, R> andThen(f: IsomorphismMapping<T, S>, g: IsomorphismMapping<S, R>) = compose(g, f)

        private val IDENTITY: IsomorphismMapping<*, *> = object : IsomorphismMapping<Any, Any> {
            override fun apply(x: Any): Any {
                return x
            }

            override fun deply(y: @NotNull Any): Any {
                return y
            }

        }

        fun <T> identity(): IsomorphismMapping<T, T> {
            @Suppress("UNCHECKED_CAST")
            return IDENTITY as IsomorphismMapping<T, T>
        }


    }
}