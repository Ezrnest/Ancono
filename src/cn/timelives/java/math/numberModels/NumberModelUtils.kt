package cn.timelives.java.math.numberModels

import cn.timelives.java.math.numberModels.api.GroupNumberModel
import cn.timelives.java.math.numberModels.api.RingNumberModel
import cn.timelives.java.math.numberModels.api.plus
import cn.timelives.java.math.numberModels.api.times

fun <T : GroupNumberModel<T>> sigma(ts : Array<T>, startInclusive : Int = 0,endExclusive : Int = ts.size) : T{
    require(startInclusive<endExclusive) {"require: startInclusive < endExclusive"}
    return when (startInclusive) {
        endExclusive - 1 -> ts[startInclusive]
        endExclusive - 2 -> ts[startInclusive] + ts[startInclusive+1]
        else -> {
            val mid = (startInclusive+endExclusive)/2
            sigma(ts,startInclusive,mid)+ sigma(ts,mid,endExclusive)
        }
    }
}

fun <T : RingNumberModel<T>> multiplyAll(ts : Array<T>, startInclusive : Int = 0, endExclusive : Int = ts.size) : T{
    require(startInclusive<endExclusive) {"require: startInclusive < endExclusive"}
    return when (startInclusive) {
        endExclusive - 1 -> ts[startInclusive]
        endExclusive - 2 -> ts[startInclusive] * ts[startInclusive+1]
        else -> {
            val mid = (startInclusive+endExclusive)/2
            multiplyAll(ts,startInclusive,mid) * multiplyAll(ts,mid,endExclusive)
        }
    }
}