package cn.ancono.math.algebra.abs.module


/*
 * Created by liyicheng at 2020-03-11 17:05
 */
/**
 * Describes a lattice in a vector space. A lattice is a finitely generated Abelian group
 * in a real vector space
 */
interface Lattice<Z : Any, V : Any> : ZModule<Z, V>, FinitelyGeneratedModule<Z, V> {


}