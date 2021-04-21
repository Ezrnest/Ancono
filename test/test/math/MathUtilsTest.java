package test.math;

import cn.ancono.math.MathUtils;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertEquals;

/*
 * Created by liyicheng at 2020-03-03 16:43
 */
public class MathUtilsTest {
    Random rd = new Random();

    @Test
    public void gcdUV() {
        int bound = 100000;
        for (int i = 0; i < 100; i++) {
            int a = rd.nextInt(bound) - bound / 2;
            int b = rd.nextInt(bound) - bound / 2;
            var result = MathUtils.gcdUV(a, b);
            assertEquals("a*u+b*v = gcd(a,b)", result[0], a * result[1] + b * result[2]);
        }

    }

    @Test
    public void sqrIntL() {
        for (long i = 100; i < 1000000; i++) {
            assertEquals("", i, MathUtils.sqrtInt(i * i));
            assertEquals("", i, MathUtils.sqrtInt(i * i + 1));
        }
    }

    @Test
    public void chineseRemainder() {
        long[] mods = {3, 5, 7};
        long[] remainders = {2, 3, 2};
        long res = MathUtils.chineseRemainder(mods, remainders);
        assertEquals(23, res, MathUtils.product(mods));
    }
}