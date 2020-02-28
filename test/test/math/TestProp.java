/**
 * 2017-11-01
 */
package test.math;

import cn.ancono.math.numberTheory.combination.Enumer;
import org.junit.Test;

import static cn.ancono.utilities.Printer.print;
import static cn.ancono.utilities.Printer.printnb;

/**
 * @author liyicheng
 * 2017-11-01 20:28
 *
 */
public final class TestProp {

	/**
	 * 
	 */
	public TestProp() {
	}

	@Test
	public void premutation1() {
		Enumer em = Enumer.permutation(10, 10);
		int n = 0;
		OUTER:
		for(int[] en : em) {
			for(int i=0;i<en.length-1;i++) {
				if(en[i]>en[i+1]+1) {
					continue OUTER;
				}
			}
			n++;
			printnb(n+":");
			for(int i=0;i<en.length;i++) {
				en[i]++;
			}
			print(en);
		}
		print(n);
	}
}
