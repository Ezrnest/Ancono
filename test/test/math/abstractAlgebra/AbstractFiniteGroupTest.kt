package test.math.abstractAlgebra

import cn.ancono.math.algebra.abs.FiniteGroups
import cn.ancono.math.algebra.abs.group.finite.PermutationGroup
import cn.ancono.math.algebra.linear.Matrix
import cn.ancono.math.discrete.combination.Permutations
import cn.ancono.math.numberModels.Calculators
import org.junit.Test
import kotlin.test.assertFalse
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
        assertSame(3, G.indexOf(G.normalizer(H)))
    }

    @Test
    fun testNormalSubgroup() {
        val G = PermutationGroup.symmetricGroup(4)
        val H = PermutationGroup.generateFrom(
            Permutations.swap(4, 0, 1), Permutations.swap(4, 2, 3)
        )
        assertFalse(G.isNormalSubgroup(H))
    }

    @Test
    fun testConjugateClass() {
        val z3 = Calculators.intModP(3)
        val mc = Matrix.calculatorGL(2, z3)
        val G = FiniteGroups.createGroup(mc, Matrix.of(2, 2, z3, 1, 1, 0, 1), Matrix.of(2, 2, z3, 0, -1, 1, 0))
        val classes = G.conjugationClasses()
        assertSame(7, classes.size)
        assertSame(24, classes.sumOf { it.size })
    }
}