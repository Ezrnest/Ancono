/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import java.util.Objects;

import cn.timelives.java.utilities.ArraySup;

/**
 * @author liyicheng
 * 2017-11-24 20:04
 *
 */
public final class ExprFunction {
	private final String name;
	private final boolean paramOrdered;
	private final int paramNumber;
	private final String[] paramDetails;
	/**
	 * @param name
	 * @param paramOrdered
	 * @param paramNumber
	 * @param paramDetails
	 */
	public ExprFunction(String name, int paramNumber, boolean paramOrdered, String[] paramDetails) {
		super();
		if(paramNumber<=0 ||paramDetails.length!=paramNumber) {
			throw new IllegalArgumentException();
		}
		this.paramNumber = Objects.requireNonNull(paramNumber);
		this.name = Objects.requireNonNull(name);
		this.paramOrdered = Objects.requireNonNull(paramOrdered);
		this.paramDetails = ArraySup.notEmpty(paramDetails);
	}
	/**
	 * Gets the name.
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * Gets the paramOrdered.
	 * @return the paramOrdered
	 */
	public boolean isParamOrdered() {
		return paramOrdered;
	}
	/**
	 * Gets the paramNumber.
	 * @return the paramNumber
	 */
	public int getParamNumber() {
		return paramNumber;
	}
	/**
	 * Gets the paramDetails.
	 * @return the paramDetails
	 */
	public String[] getParamDetails() {
		return paramDetails;
	}
	

}
