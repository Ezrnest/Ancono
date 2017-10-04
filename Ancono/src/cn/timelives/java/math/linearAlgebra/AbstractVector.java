package cn.timelives.java.math.linearAlgebra;

import cn.timelives.java.math.function.MathFunction;
import cn.timelives.java.math.numberModels.MathCalculator;
import cn.timelives.java.math.numberModels.NumberFormatter;

/**
 * The abstract class for vector.
 * @author liyicheng
 *
 * @param <T>
 */
public abstract class AbstractVector<T> extends Matrix<T> {
	/**
	 * Decide whether this vector is a row-vector which means column count is
	 * the length of vec. Otherwise,the column count will be 1 and row count will
	 * be vec.length.
	 */
	protected final boolean isRow;
	protected AbstractVector(int length,boolean isRow ,MathCalculator<T> mc) {
		super(isRow ? length : 1, isRow ? 1 : length, mc);
		this.isRow = isRow;
	}
	/**
	 * Returns the number of dimension of this vector.
	 * @return
	 */
	public abstract int getSize();
	
	public abstract T getNumber(int index);
	/**
	 * Returns an array containing all of the elements in this vector in
     * proper sequence (from first to last element),.
	 * @return
	 */
	public abstract Object[] toArray();
	/**
	 * Returns an array containing all of the elements in this vector in
     * proper sequence (from first to last element), the runtime type of
     * the returned array is that of the specified array.  If the list fits
     * in the specified array, it is returned therein.  Otherwise, a new
     * array is allocated with the runtime type of the specified array and
     * the size of this list.
	 * @param arr
	 * @return
	 */
	public abstract T[] toArray(T[] arr);
	
	/**
	 * Return the value of |this|.The value will be a non-negative value.
	 * @return |this|
	 */
	public T calLength(){
		return mc.squareRoot(calLengthSq());
	}
	/**
	 * Returns a unit vector of this vector's direction, throws an exception 
	 * if this vector is an zero vector.
	 * <pre>
	 * this/|this|
	 * </pre>
	 * @return a vector
	 */
	public abstract AbstractVector<T> unitVector();
	
	/**
	 * Calculate the square of |this|,which has full precision and use T as the 
	 * returning result.The result is equal to use {@link #scalarProduct(Vector, Vector)} as 
	 * {@code scalarProduct(this,this)} but this method will have a better performance.
	 * @return |this|^2
	 */
	public T calLengthSq(){
		T re = mc.getZero();
		int size = getSize();
		for(int i=0;i<size;i++){
			T t = getNumber(i);
			re = mc.add(mc.multiply(t, t), re);
		}
		return re;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.utilities.math.Matrix#applyFunction(cn.timelives.java.utilities.math.MathFunction)
	 */
	@Override
	public abstract AbstractVector<T> applyFunction(MathFunction<T, T> f);
	/**
	 * Gets whether it is a row vector.
	 * @return
	 */
	public boolean isRow(){
		return isRow;
	}
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#multiplyNumber(long)
	 */
	@Override
	public abstract AbstractVector<T> multiplyNumber(long n);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#multiplyNumber(java.lang.Object)
	 */
	@Override
	public abstract AbstractVector<T> multiplyNumber(T n);
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#negative()
	 */
	@Override
	public abstract AbstractVector<T> negative();
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#transportMatrix()
	 */
	@Override
	public abstract AbstractVector<T> transportMatrix();
	
	/* (non-Javadoc)
	 * @see cn.timelives.java.math.linearAlgebra.Matrix#toString(cn.timelives.java.math.number_models.NumberFormatter)
	 */
	@Override
	public String toString(NumberFormatter<T> nf) {
		StringBuilder sb = new StringBuilder();
		sb.append("(");
		int size = getSize();
		for(int i=0;i<size;i++){
			sb.append(nf.format(getNumber(i), mc)).append(",");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		return sb.toString();
	}
}
