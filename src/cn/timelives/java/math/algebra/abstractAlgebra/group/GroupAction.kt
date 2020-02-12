package cn.timelives.java.math.algebra.abstractAlgebra.group

import cn.timelives.java.math.algebra.abstractAlgebra.structure.Group
import cn.timelives.java.math.function.MathFunction
import cn.timelives.java.math.set.MathSet


/**
 * Created at 2018/10/14 13:24
 * @see <a href="https://en.wikipedia.org/wiki/Group_action">Group Action</a>
 * @author  liyicheng
 */
interface GroupAction<T,G : Group<T>,X> {
    val group : G
    val set : MathSet<X>

    /**
     * Describes whether this group action is a left action.
     */
    val isLeft : Boolean

    /**
     *  Applies an element in the group to an element in the set.
     */
    fun apply(g : T,x : X) : X

    /**
     * Returns the orbit of [x].
     */
    fun orb(x : X) : MathSet<X>

    /**
     * Returns the stabilizers as a group.
     */
    fun stab(x : X) : Group<T>
}

fun <T,X> GroupAction<T,*,X>.asMathFunction() : MathFunction<Pair<T,X>,X>{
    return MathFunction { (g,x) -> apply(g,x) }
}