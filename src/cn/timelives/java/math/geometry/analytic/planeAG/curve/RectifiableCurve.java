/**
 * 
 */
package cn.timelives.java.math.geometry.analytic.planeAG.curve;

import cn.timelives.java.math.exceptions.UnsupportedCalculationException;

/**
 * RectifiableCurve is curve with a specified length, such as 
 * circle, ellipse, square and so on. There is one thing to 
 * notice that there are curves whose length is rectifiable 
 * in math, but actually impossible for computing when it comes 
 * to different number models without proper calculating 
 * methods, then class can implement this interface but throw 
 * an exception when the method is called. Nevertheless, this actually 
 * breaks the rule of good interface using, but this is a good way of 
 * setting a good math relation.
 * @author liyicheng
 *
 */
public interface RectifiableCurve<T> {
	/**
	 * Computes the length of this curve.
	 * @return
	 * @throws UnsupportedCalculationException if the result cannot be computed.
	 */
	T computeLength();
}
