/**
 * 2018-03-02
 */
package cn.timelives.java.math;

/**
 * Invertible describes functions, matrixes and other objects that can be inversed.
 * It is required that {@code inv.inverse().inverse().equals(inv)}.
 * @author liyicheng
 * 2018-03-02 21:13
 *
 */
public interface Invertible<S extends Invertible<?>> {
	/**
	 * Returns the inverse of {@code this}.
	 * @return <code>this<sup>-1</sup></code>
	 */
	S inverse();
}
