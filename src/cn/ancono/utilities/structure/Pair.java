/**
 *
 */
package cn.ancono.utilities.structure;

import java.util.Objects;

/**
 * A utility class for storing a pair of objects. The pair contains 
 * two elements, the first one and the second one.  
 * @author liyicheng
 *
 */
public final class Pair<T, S> {
    private T first;
    private S second;

    /**
     *
     */
    public Pair() {
    }

    public Pair(T first, S second) {
        this.first = first;
        this.second = second;
    }

    public T getFirst() {
        return first;
    }

    public void setFirst(T first) {
        this.first = first;
    }

    public S getSecond() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    public Pair<S, T> swapped() {
        return new Pair<>(second, first);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hashCode(first) * 31 + Objects.hashCode(second);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair) {
            Pair<?, ?> p = (Pair<?, ?>) obj;
            return Objects.equals(first, p.first) && Objects.equals(second, p.second);
        }
        return false;
    }

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "[" + Objects.toString(first) + "," + Objects.toString(second) + "]";
    }

}
