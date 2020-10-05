package cn.ancono.math.algebra.abs

import cn.ancono.math.function.Bijection
import cn.ancono.math.function.MathFunction


/**
 * A marker interface for a homomorphism mapping.
 * Created at 2018/10/10 18:36
 * @author  liyicheng
 */
@FunctionalInterface
interface HomomorphismMapping<T : Any, S : Any> : MathFunction<T, S> {
    companion object {
        fun <T : Any, S : Any> fromFunction(f: java.util.function.Function<T, S>): HomomorphismMapping<T, S> = object : HomomorphismMapping<T, S> {
            override fun apply(x: T): S {
                return f.apply(x)
            }
        }

        fun <T : Any, S : Any, R : Any> compose(f: HomomorphismMapping<S, R>, g: HomomorphismMapping<T, S>) =
                fromFunction(MathFunction.compose(f, g))

        fun <T : Any, S : Any, R : Any> andThen(f: HomomorphismMapping<T, S>, g: HomomorphismMapping<S, R>) =
                fromFunction(MathFunction.andThen(f, g))

    }
}

/**
 * A marker interface for a isomorphism mapping.
 * Created at 2018/10/10 18:36
 * @author  liyicheng
 */
interface IsomorphismMapping<T : Any, S : Any> : HomomorphismMapping<T, S>, Bijection<T, S> {


    companion object {
        fun <T : Any, S : Any> fromBijection(f: Bijection<T, S>): IsomorphismMapping<T, S> =
                object : IsomorphismMapping<T, S> {
                    override fun apply(x: T): S {
                        return f.apply(x)
                    }

                    override fun deply(y: S): T {
                        return f.deply(y)
                    }

                }

        fun <T : Any, S : Any, R : Any> compose(f: IsomorphismMapping<S, R>, g: IsomorphismMapping<T, S>) =
                fromBijection(f.composeBi(g))

        fun <T : Any, S : Any, R : Any> andThen(f: IsomorphismMapping<T, S>, g: IsomorphismMapping<S, R>) = compose(g, f)

        private val IDENTITY: IsomorphismMapping<*, *> = object : IsomorphismMapping<Any, Any> {
            override fun apply(x: Any): Any {
                return x
            }

            override fun deply(y: Any): Any {
                return y
            }

        }

        fun <T : Any> identity(): IsomorphismMapping<T, T> {
            @Suppress("UNCHECKED_CAST")
            return IDENTITY as IsomorphismMapping<T, T>
        }


    }
}