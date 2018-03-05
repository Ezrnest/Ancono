/**
 * 2018-03-03
 */
package test.math;

import static cn.timelives.java.utilities.Printer.print;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import cn.timelives.java.math.combination.Permutation;
import cn.timelives.java.math.combination.Permutations;
import cn.timelives.java.utilities.ArraySup;

/**
 * @author liyicheng
 * 2018-03-03 10:05
 *
 */
public class TestCombination {

	/**
	 * 
	 */
	public TestCombination() {
	}
//	@Test
	public void testPermutation1() {
		Permutation p = Permutations.flipAll(5);
		int[] arr = new int[] {1,2,3,4,5};
		assertTrue("",Arrays.equals(p.apply(arr), new int[] {5,4,3,2,1}));
	}
//	@Test
	public void testPermutation2() {
		int size = 6;
		Permutation p = Permutations.valueOf(ArraySup.ranArrNoSame(size, size));
		long index = p.index();
		Permutation p2 = Permutations.fromIndex(index, size);
		assertEquals(p, p2);
	}
	
	@Test
	public void testPermutation3() {
		int[] arr = new int[] {2,0,4,3,1,7,6,5};
		Permutation p = Permutations.valueOf(arr);
		print(p.decompose());
	}
}
