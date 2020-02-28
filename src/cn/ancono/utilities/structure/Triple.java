/**
 * 2017-09-09
 */
package cn.ancono.utilities.structure;

/**
 * @author liyicheng
 * 2017-09-09 21:07
 */
public final class Triple<T1, T2, T3> {
    private T1 first;
    private T2 second;
    private T3 third;

    /**
     * Creates a Triple.
     */
    public Triple() {
    }

    /**
     * Creates a Triple with its initial values.
     *
     * @param first
     * @param second
     * @param third
     */
    public Triple(T1 first, T2 second, T3 third) {
        super();
        this.first = first;
        this.second = second;
        this.third = third;
    }

    /**
     * Gets the first.
     *
     * @return the first
     */
    public T1 getFirst() {
        return first;
    }

    /**
     * Sets the first.
     *
     * @param first the first to set
     */
    public void setFirst(T1 first) {
        this.first = first;
    }

    /**
     * Gets the second.
     *
     * @return the second
     */
    public T2 getSecond() {
        return second;
    }

    /**
     * Sets the second.
     *
     * @param second the second to set
     */
    public void setSecond(T2 second) {
        this.second = second;
    }

    /**
     * Gets the third.
     *
     * @return the third
     */
    public T3 getThird() {
        return third;
    }

    /**
     * Sets the third.
     *
     * @param third the third to set
     */
    public void setThird(T3 third) {
        this.third = third;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((first == null) ? 0 : first.hashCode());
        result = prime * result + ((second == null) ? 0 : second.hashCode());
        result = prime * result + ((third == null) ? 0 : third.hashCode());
        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Triple))
            return false;
        @SuppressWarnings("rawtypes")
        Triple other = (Triple) obj;
        if (first == null) {
            if (other.first != null)
                return false;
        } else if (!first.equals(other.first))
            return false;
        if (second == null) {
            if (other.second != null)
                return false;
        } else if (!second.equals(other.second))
            return false;
        if (third == null) {
            return other.third == null;
        } else return third.equals(other.third);
    }


}
