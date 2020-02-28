/**
 * 2018-03-02
 */
package cn.ancono.math.property;

/**
 * Invertible describes functions, matrices and other objects that can be inverted.
 * It is required that {@code inv.inverse().inverse().equals(inv)}.
 * <p>
 * However,
 *
 * @author liyicheng
 * 2018-03-02 21:13
 */
public interface Invertible<S extends Invertible<?>> {
    /**
     * Returns the inverse of {@code this}.
     *
     * @return <code>this<sup>-1</sup></code>
     */
    S inverse();
}
