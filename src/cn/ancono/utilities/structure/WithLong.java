/**
 * 2017-09-30
 */
package cn.ancono.utilities.structure;

/**
 * WithLong is a composed object with an long and an object. The initial
 * long value will be {@code zero}.
 *
 * @author liyicheng
 * 2017-09-30 17:32
 */
public final class WithLong<T> {
    private long x;
    private T obj;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof cn.ancono.utilities.structure.WithLong)) return false;

        cn.ancono.utilities.structure.WithLong<?> WithLong = (cn.ancono.utilities.structure.WithLong<?>) o;

        if (x != WithLong.x) return false;
        return obj != null ? obj.equals(WithLong.obj) : WithLong.obj == null;
    }

    @Override
    public int hashCode() {
        int result = Long.hashCode(x);
        result = 31 * result + (obj != null ? obj.hashCode() : 0);
        return result;
    }

    public WithLong(long n) {
        setLong(n);
    }

    public WithLong(T e) {
        setObj(e);
    }

    public WithLong(long n, T e) {
        setLong(n);
        setObj(e);
    }

    /**
     * Gets the long value.
     */
    public long getLong() {
        return x;
    }

    /**
     * Sets the long value.
     */
    public void setLong(long x) {
        this.x = x;
    }

    /**
     * Gets the long object.
     */
    public T getObj() {
        return obj;
    }

    /**
     * Sets the object.
     */
    public void setObj(T obj) {
        this.obj = obj;
    }


}
