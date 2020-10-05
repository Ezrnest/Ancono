package test.logic.propLogic

import cn.ancono.logic.prop.*
import org.junit.Assert.*
import org.junit.Test

class FormulaTest{
    @Test
    fun test1(){

        assert((p implies q) valueEquals (!p or (p and q)))

        val f2 = (!(p implies q))and q
        assertEquals(f2.simplify(), F)
        val f3 = ((p implies q)and p) implies q
        assertEquals(f3.toMainDisjunctiveNorm(), T)
    }
}