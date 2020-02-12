package test.math.abstractAlgebra

import cn.timelives.java.math.algebra.abstractAlgebra.group.finite.PermutationGroup
import cn.timelives.java.math.numberTheory.combination.Permutation
import cn.timelives.java.math.numberTheory.combination.Permutations
import org.junit.Test
import kotlin.test.assertSame

class AbstractFiniteGroupTest{
    @Test
    fun testNormalizer(){
        val G = PermutationGroup.symmetricGroup(4)
        val H = PermutationGroup.generateFrom(
                Permutations.swap(4,0,1),Permutations.swap(4,2,3))
//        println(G.set.joinToString())
//        println(H.set.joinToString())
//        println(G.normalizer(H))
        assertSame(3,G.indexOf(G.normalizer(H)))
    }
    @Test
    fun testNormalSubgroup(){
        val G = PermutationGroup.symmetricGroup(4)
        val H = PermutationGroup.generateFrom(
                Permutations.swap(4,0,1),Permutations.swap(4,2,3))

    }
}