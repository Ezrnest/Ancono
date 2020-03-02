package samples;

import cn.ancono.math.algebra.abstractAlgebra.group.finite.PermutationGroup;
import cn.ancono.math.numberTheory.combination.Permutation;
import cn.ancono.math.numberTheory.combination.Permutations;
import cn.ancono.utilities.ArraySup;
import cn.ancono.utilities.Printer;
/*
 * Created by lyc at 2020/3/1
 */
public class FiniteGroupSample {
    public static void permutationGroup1() {
        var G = PermutationGroup.symmetricGroup(4);
        var H = PermutationGroup.generateFrom(
                Permutations.swap(4, 0, 1),
                Permutations.swap(4, 2, 3)
        );
        var H1 = G.normalizer(H);
        System.out.println(H1.getSet());
        System.out.println(G.indexOf(H1));
    }


    public static void main(String[] args) {
        permutationGroup1();
    }
}
