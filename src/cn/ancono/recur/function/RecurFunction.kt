package cn.ancono.recur.function


/*
 * Created by liyicheng at 2020-03-03 18:57
 */
/**
 * Represents the recursive function in recursion theory.
 *
 *
 * @author liyicheng
 */
interface RecurFunction {
    /**
     * The count of parameters of this function. A non-zero integer.
     */
    val paraCount: Int

    /**
     *  Computes the value. If the number of the parameters does not match the function's [paraCount], an exception
     *  should be thrown.
     */
    fun apply(vararg paras: Int): Int

    /**
     *  Computes the value as if this is a single variable function.
     */
    fun apply(x: Int): Int {
        require(paraCount == 1)
        val para = intArrayOf(x)
        return apply(*para)
    }

    /**
     *  Computes the value as if this is a function with two arguments.
     */
    fun apply(x1: Int, x2: Int): Int {
        require(paraCount == 2)
        val para = intArrayOf(x1, x2)
        return apply(*para)
    }

    operator fun invoke(vararg paras: Int): Int = apply(*paras)

    operator fun invoke(x: Int): Int = apply(x)

}

open class CombineFunction(override val paraCount: Int, open val f: RecurFunction, open val gs: List<RecurFunction>) : RecurFunction {

    override fun apply(vararg paras: Int): Int {
//        require(paras.size == paraCount)
        val t = gs.map { it.apply(*paras) }.toIntArray()
        return f.apply(*t)
    }

    override fun apply(x: Int): Int {
        val t = gs.map { it.apply(x) }.toIntArray()
        return f.apply(*t)
    }

    override fun apply(x1: Int, x2: Int): Int {
        val t = gs.map { it.apply(x1, x2) }.toIntArray()
        return f.apply(*t)
    }
}

