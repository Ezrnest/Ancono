package cn.ancono.math.algebra.linear


/*
 * Created by liyicheng at 2021-04-29 21:56
 */


class LinearEquationSolution<T>
private constructor(
        val type: SolutionType,
        private val sv: Vector<T>?,
        val solutionSpace: VectorBasis<T>) {

    enum class SolutionType {
        EMPTY,
        SINGLE,
        INFINITE
    }

    val special: Vector<T>
        get() = sv ?: throw ArithmeticException("No solution!")

    fun isSingle(): Boolean {
        return type == SolutionType.SINGLE
    }

    fun isEmpty(): Boolean {
        return type == SolutionType.EMPTY
    }

    fun isInfinite(): Boolean {
        return type == SolutionType.INFINITE
    }

    fun notEmpty(): Boolean {
        return type != SolutionType.EMPTY
    }

    override fun toString(): String {
        return when (type) {
            SolutionType.EMPTY -> "(No solution)"
            SolutionType.SINGLE -> special.toString()
            SolutionType.INFINITE -> buildString {
                append(special)
                solutionSpace.elements.withIndex().joinTo(this, " + ", " + ") { (i, v) ->
                    "k_${i + 1} * $v"
                }
            }
        }
    }


    companion object {

        fun <T> of(special: Vector<T>, basis: VectorBasis<T>, solvable: Boolean): LinearEquationSolution<T> {
            if (!solvable) {
                return LinearEquationSolution(SolutionType.EMPTY, null, basis)
            }
            if (basis.rank == 0) {
                return LinearEquationSolution(SolutionType.SINGLE, special,
                        VectorBasis.zeroBase(special.size, special.mathCalculator))
            }
            return LinearEquationSolution(SolutionType.INFINITE, special, basis)
        }

        fun <T> of(t: Triple<Vector<T>, VectorBasis<T>, Boolean>): LinearEquationSolution<T> {
            val (special, basis, solvable) = t
            return of(special, basis, solvable)
        }
    }
}

