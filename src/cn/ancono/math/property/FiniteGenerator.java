package cn.ancono.math.property;
/*
 * Created by liyicheng at 2020-03-07 13:00
 */

import cn.ancono.math.algebra.linear.Vector;

import java.util.List;

/**
 * Describe a family of generators set for a set, module or something that is finitely generated.
 *
 * @author liyicheng
 */
public interface FiniteGenerator<K, V> {

    /**
     * Gets the list of generators.
     */
    List<V> getElements();

    /**
     * Reduces a given element to linear combination of generators.
     */
    Vector<K> reduce(V v);

    /**
     * Produce an object according to the coefficients.
     */
    V produce(Vector<K> coefficients);

}
