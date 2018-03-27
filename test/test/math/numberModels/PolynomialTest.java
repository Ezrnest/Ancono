/**
 * 2017-12-03
 */
package test.math.numberModels;

import cn.timelives.java.math.numberModels.PolyCalculator;
import cn.timelives.java.math.numberModels.Polynomial;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * @author liyicheng
 * 2017-12-03 11:46
 *
 */
public final class PolynomialTest {

	/**
	 * 
	 */
	public PolynomialTest() {
	}

	private final PolyCalculator pc = PolyCalculator.DEFAULT_CALCULATOR;
	Polynomial x,y,z,a,b,c;
	@Test
	public void test1() {
		Polynomial p1 = Polynomial.valueOf("a+b+c"),
				p2 = Polynomial.valueOf("a+b+c");
		x = pc.add(p1, p2);
		y = pc.multiply(p1, p2);
		z = pc.divideLong(pc.multiply(x, x), 4L);
		assertTrue(y.equals(z));
	}
	
	
}
