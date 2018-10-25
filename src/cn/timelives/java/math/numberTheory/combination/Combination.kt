package cn.timelives.java.math.numberTheory.combination



class CEnumer(n: Int, val m: Int) : Enumer(n){
    override fun enumration(): MutableList<IntArray> {
        val list = ArrayList<IntArray>(enumCount.toInt())
        for(ar in iterator()){
            list.add(ar)
        }
        return list
    }

    override fun iterator(): MutableIterator<IntArray> {
        return enumComb(n,m)
    }

    override fun getEnumCount(): Long {
        return CFunctions.combination(n,m)
    }

}
private val empty = BooleanArray(0)
fun enumComb(n: Int, m: Int): MutableIterator<IntArray> {
    require(n >= m)
    return object : MutableIterator<IntArray> {
        override fun remove() {
            throw UnsupportedOperationException()
        }

        var current = empty
        var hasNext: Boolean? = null
        override fun hasNext(): Boolean {
            hasNext?.apply { return this }
            if (current === empty) {
                hasNext = true
                return true
            }
            return hasNext0().also { hasNext = it }
        }

        private fun hasNext0(): Boolean {
            val arr = current
            for (i in 0 until arr.size) {
                if (arr[i]) {
                    val nextPos = findNextPos(arr, i)
                    if (nextPos > -1) {
                        return true
                    }
                }
            }
            return false
        }

        private fun findNextPos(arr: BooleanArray, pos: Int): Int {
            val next = pos+1
            if(next < arr.size && !arr[next]){
                return next
            }
            return -1
        }

        private fun moveAllToLeft(arr: BooleanArray, pos: Int) {
            var count = 0
            for (i in 0 until pos) {
                if (arr[i]) {
                    count++
                    arr[i] = false
                }
            }
            for (i in 0 until count) {
                arr[i] = true
            }
        }


        override fun next(): IntArray {
            hasNext?.apply {
                if (!this) {
                    throw NoSuchElementException()
                }
            }
            if (current === empty) {
                current = initArr()
            } else {
                transformArr()
            }
            hasNext = null
            return booleanArrToInt(current)
        }

        private fun booleanArrToInt(br : BooleanArray) : IntArray{
            val ir = IntArray(m)
            var idx = 0
            for(b in br.withIndex()){
                if(b.value){
                    ir[idx] = b.index
                    idx++
                }
            }
            return ir
        }

        private fun transformArr() {
            val arr = current
            var pos = -1
            var nPos = -1
            for (i in 0 until arr.size) {
                if (arr[i]) {
                    val nextPos = findNextPos(arr, i)
                    if (nextPos > -1) {
                        pos = i
                        nPos = nextPos
                        break
                    }
                }
            }
            if (pos == -1) {
                throw NoSuchElementException()
            }

            arr[nPos] = true
            arr[pos] = false

            moveAllToLeft(arr, pos)

        }

        private fun initArr(): BooleanArray {
            return BooleanArray(n) { i ->
                i < m
            }
        }

    }
}