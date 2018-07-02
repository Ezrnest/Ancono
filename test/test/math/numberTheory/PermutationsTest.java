package test.math.numberTheory;

import cn.timelives.java.math.numberTheory.combination.Permutations;
import org.junit.Test;

import static cn.timelives.java.math.numberTheory.combination.Permutations.universe;
import static org.junit.Assert.*;

public class PermutationsTest {
    @Test
    public void testDecompose(){
        int size = 4;
        universe(size).forEach(x -> {
            assertEquals(x,Permutations.composeAll(x.decompose(),size));
            assertEquals(x,Permutations.composeAll(x.decomposeTransposition(),size));
        }
        );
    }

    @Test
    public void testEven(){
        int size = 4;
        universe(size).forEach(x -> assertTrue(x.isEven() == (x.decomposeTransposition().size() % 2 == 0)));
    }
}