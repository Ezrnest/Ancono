package test.math;

import cn.ancono.math.MathUtils;
import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.*;

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
}