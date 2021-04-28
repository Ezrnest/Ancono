package cn.ancono.math.geometry.analytic.space

import cn.ancono.math.*
import cn.ancono.math.numberModels.api.FlexibleNumberFormatter
import java.util.function.Function

class SpaceAffineCoordinateSystem<T> internal constructor(val o: SPoint<T>,
                                                          val i: SVector<T>,
                                                          val j: SVector<T>,
                                                          val k: SVector<T>,
                                                          mc: MathCalculator<T>) : MathObjectExtend<T>(mc) {

    private val vectorBase = SVector.createBase(i, j, k)

    override fun <N> mapTo(
            newCalculator: MathCalculator<N>,
            mapper: Function<T, N>
    ): SpaceAffineCoordinateSystem<N> {
        return SpaceAffineCoordinateSystem(
                o.mapTo(newCalculator, mapper),
                i.mapTo(newCalculator, mapper),
                j.mapTo(newCalculator, mapper),
                k.mapTo(newCalculator, mapper),
                newCalculator
        )
    }

    override fun valueEquals(obj: MathObject<T>): Boolean {
        if (obj !is SpaceAffineCoordinateSystem) {
            return false
        }
        return o.valueEquals(obj.o) && i.valueEquals(obj.i) && j.valueEquals(obj.j) && k.valueEquals(obj.k)
    }

    override fun toString(nf: FlexibleNumberFormatter<T, MathCalculator<T>>): String {
        return "{${o.toString(nf)};${i.toString(nf)},${j.toString(nf)},${k.toString(nf)}}"
    }

    fun toAbsoluteCoord(p: SPoint<T>): SPoint<T> {
        return o.moveToward(i * p.x + j * p.y + k * p.z)
    }


    fun fromAbsoluteCoord(p: SPoint<T>): SPoint<T> {
        //reduce
        val t = o.directVector(p)
        val vec = vectorBase.reduce(t)
        return vec.asPoint()
    }


    companion object {
        fun <T> valueOf(o: SPoint<T>, i: SVector<T>, j: SVector<T>, k: SVector<T>): SpaceAffineCoordinateSystem<T> {
            require(!SVector.isOnSamePlane(i, j, k))
            return SpaceAffineCoordinateSystem(o, i, j, k, o.mathCalculator)
        }
    }
}