package test.util;

import cn.ancono.utilities.CollectionSup;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;

public class CollectionSupTest {

    @Test
    public void compareCollectionLexi() {
        var l1 = Arrays.asList(-5, 1);
        var l2 = Arrays.asList(1, 2, 3);
        assertEquals("[-5,1] < [-1,2,3]", CollectionSup.compareCollectionLexi(l1, l2), -1);
        assertEquals("[-1,2,3] > [-5,1]", CollectionSup.compareCollectionLexi(l2, l1), 1);
        assertEquals("[-1,2,3] = [-1,2,3]", CollectionSup.compareCollectionLexi(l1, l1), 0);
        l1 = Arrays.asList(1, 2, 3);
        l2 = Arrays.asList(1, 2, 3, 4);
        assertEquals("[1,2,3] < [1,2,3,4]", CollectionSup.compareCollectionLexi(l1, l2), -1);
    }

    @Test
    public void cartesianProduct() {
        var c1 = Arrays.asList(1, 2);
        var c2 = Arrays.asList(3, 4);
        var cp = CollectionSup.cartesianProductM(c1, c2);
        assertEquals("[1,2]*[3,4] = [[1,3],[1,4],[2,3],[2,4]]", "[[1,3],[1,4],[2,3],[2,4]]", cp.toString().replace(" ", ""));
        assertEquals("Cartesian Product of nothing:", Collections.singletonList(Collections.emptyList()), CollectionSup.cartesianProductM());
        assertEquals("Cartesian Product of [1,2] * [] = []", Collections.emptyList(), CollectionSup.cartesianProductM(c1, Collections.emptyList()));
    }
}