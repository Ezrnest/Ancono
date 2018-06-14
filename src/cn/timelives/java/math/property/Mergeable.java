package cn.timelives.java.math.property;

/**
 * Mergeable defines an equivalent relation: canMerge and corresponding
 * method for two object that can merge.
 */
public interface Mergeable<T extends Mergeable<T>> {
    /**
     * Determines whether this object and the given can be merged,
     * @param x
     * @return
     */
    boolean canMerge(T x);

    /**
     * Merges two objects, throws an exception if canMerge(x) returns false.
     * @param x
     * @return
     */
    T merge(T x);
}
