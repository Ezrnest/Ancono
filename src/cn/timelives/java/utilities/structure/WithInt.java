/**
 * 2017-09-30
 */
package cn.timelives.java.utilities.structure;

/**
 * WithInt is a composed object with an integer and an object. The initial 
 * integer value will be {@code zero}.
 * @author liyicheng
 * 2017-09-30 17:32
 *
 */
public final class WithInt<T> {
	private int x;
	private T obj;
	/**
	 * 
	 */
	public WithInt() {
	}
	/**
	 * 
	 */
	public WithInt(int n) {
		setInt(n);
	}
	
	/**
	 * 
	 */
	public WithInt(T e) {
		setObj(e);
	}
	
	/**
	 * 
	 */
	public WithInt(int n,T e) {
		setInt(n);
		setObj(e);
	}
	/**
	 * Gets the int value.
	 * @return
	 */
	public int getInt() {
		return x;
	}
	/**
	 * Sets the int value.
	 * @return
	 */
	public void setInt(int x) {
		this.x = x;
	}
	/**
	 * Gets the int object.
	 * @return
	 */
	public T getObj() {
		return obj;
	}
	/**
	 * Sets the object.
	 * @return
	 */
	public void setObj(T obj) {
		this.obj = obj;
	}
	
	
}
