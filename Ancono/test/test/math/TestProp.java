/**
 * 2017-11-01
 */
package test.math;

import static cn.timelives.java.utilities.Printer.print;
import static cn.timelives.java.utilities.Printer.printnb;

import org.junit.Test;

import cn.timelives.java.math.prob.Enumer;

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
		// TODO Auto-generated constructor stub
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
