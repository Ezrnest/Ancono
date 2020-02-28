package cn.ancono.math.numberModels

import cn.ancono.math.numberModels.api.GroupNumberModel
import cn.ancono.math.numberModels.api.RingNumberModel
import cn.ancono.math.numberModels.api.plus
import cn.ancono.math.numberModels.api.times


object NumberModelUtils {
    @JvmStatic
    fun <T : GroupNumberModel<T>> sigma(ts: Array<T>, startInclusive: Int = 0, endExclusive: Int = ts.size): T {
        require(startInclusive < endExclusive) { "require: startInclusive < endExclusive" }
        return when (startInclusive) {
            endExclusive - 1 -> ts[startInclusive]
            endExclusive - 2 -> ts[startInclusive] + ts[startInclusive + 1]
            else -> {
                val mid = (startInclusive + endExclusive) / 2
                sigma(ts, startInclusive, mid) + sigma(ts, mid, endExclusive)
            }
        }
    }

    @JvmStatic
    fun <T : RingNumberModel<T>> multiplyAll(ts: Array<T>, startInclusive: Int = 0, endExclusive: Int = ts.size): T {
        require(startInclusive < endExclusive) { "require: startInclusive < endExclusive" }
        return when (startInclusive) {
            endExclusive - 1 -> ts[startInclusive]
            endExclusive - 2 -> ts[startInclusive] * ts[startInclusive + 1]
            else -> {
                val mid = (startInclusive + endExclusive) / 2
                multiplyAll(ts, startInclusive, mid) * multiplyAll(ts, mid, endExclusive)
            }
        }
    }

    @JvmStatic
    fun <T : GroupNumberModel<T>, K, M : MutableMap<K, T>> accumulateMapAdd(map: M, key: K, v: T) {
        map.merge(key, v) { x, y -> x + y }
    }

    @JvmStatic
    fun <T : RingNumberModel<T>, K, M : MutableMap<K, T>> accumulateMapMultiply(map: M, key: K, v: T) {
        map.merge(key, v) { x, y -> x * y }
    }
}


