/**
 * 2017-12-03
 */
package test.math.numberModels;

import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.PolynomialOld;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author liyicheng
 * 2017-12-03 11:46
 *
 */
public final class PolynomialOldTest {

	/**
	 * 
	 */
	public PolynomialOldTest() {
	}

	private final PolyCalculator pc = PolyCalculator.DEFAULT_CALCULATOR;
	PolynomialOld x,y,z,a,b,c;
	@Test
	public void test1() {
		PolynomialOld p1 = PolynomialOld.valueOf("a+b+c"),
				p2 = PolynomialOld.valueOf("a+b+c");
		x = pc.add(p1, p2);
		y = pc.multiply(p1, p2);
		z = pc.divideLong(pc.multiply(x, x), 4L);
		assertTrue(y.equals(z));
	}
	
	
}
