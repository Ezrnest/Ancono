package cn.ancono.utilities.structure

import cn.ancono.math.property.Invertible

/**
 * A bijection map describes the bijection of two sets, namely keys and values.
 */
interface BiMap<K, V> : Map<K, V>, Invertible<BiMap<V, K>> {

    override fun get(key: K): V?

    fun reget(value: V): K?

    /**
     * Returns the inverse of this map.
     */
    override fun inverse(): BiMap<V, K>

    override val values: Set<V>
}

interface MutableBiMap<K, V> : BiMap<K, V>, MutableMap<K, V> {


    override val values: MutableSet<V>

    /**
     * Returns the inverse of this map. Any modification done to the
     * inverse map will also affect the original map.
     */
    override fun inverse(): MutableBiMap<V, K>

    fun removeValue(value: V): K?
}

abstract class AbstractMutableBiMap<K, V> protected constructor() : AbstractMutableMap<K, V>(),
        MutableBiMap<K, V> {
    abstract override val values: MutableSet<V>

}

class WrappedBiMap<K, V> internal constructor(private val forwardMap: MutableMap<K, V>, private val backwardMap: MutableMap<V, K>) : AbstractMutableBiMap<K, V>() {
    override val values: MutableSet<V>
        get() = backwardMap.keys
    override val entries: MutableSet<MutableMap.MutableEntry<K, V>>
        get() = forwardMap.entries

    constructor(initialCapacity: Int) : this(initialCapacity, 0.75f)
    constructor(initialCapacity: Int, loadFactor: Float) :
            this(HashMap(initialCapacity, loadFactor), HashMap(initialCapacity, loadFactor))


    // From Map

    override val size: Int
        get() = forwardMap.size

    override fun isEmpty(): Boolean {
        return size == 0
    }

    override fun containsKey(key: K): Boolean {
        return forwardMap.containsKey(key)
    }

    override fun containsValue(value: @UnsafeVariance V): Boolean {
        return backwardMap.containsKey(value)
    }

    override operator fun get(key: K): V? {
        return forwardMap[key]
    }

    override fun reget(value: V): K? {
        return backwardMap[value]
    }

    // From MutableMap

    override fun put(key: K, value: V): V? {
        //remove first
        val v1 = forwardMap.put(key, value)
        if (v1 != null) {
            backwardMap.remove(v1)
        }
        val v2 = backwardMap.put(value, key)
        if (v2 != null && v2 != key) {
            forwardMap.remove(v2)
        }
        return v1
    }

    override fun remove(key: K): V? {
        val v1 = forwardMap.remove(key)
        if (v1 != null) {
            backwardMap.remove(v1)
        }
        return v1
    }

    override fun removeValue(value: V): K? {
        val key = backwardMap.remove(value)
        if (key != null) {
            forwardMap.remove(key)
        }
        return key
    }


    override fun putAll(from: Map<out K, V>) {
        for (en in from) {
            put(en.key, en.value)
        }
    }

    override fun clear() {
        forwardMap.clear()
        backwardMap.clear()
    }

    override fun inverse(): MutableBiMap<V, K> {
        return WrappedBiMap(backwardMap, forwardMap)
    }


}
