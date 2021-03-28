package cn.ancono.math.discrete

typealias Node = Int
typealias Edge = Pair<Node, Node>
//Created by lyc at 2021-03-02
/**
 * Describes a general (undirected) graph that has no multiple edges.
 * Generally speaking, in mathematics, the graph can be
 * represented by a tuple of nodes(vertices) and edges(arcs): `G = (V,E)`, where the set `E`
 * contains tuples like `(u,v), u,v in V` which means there is a (undirected) edge from `u` to `v`.
 *
 * In this interface,
 * nodes are represented using consecutive non-negative integers `0,1,...,n-1` for simplicity.
 * Edges are represented by pairs of integers.
 */
interface Graph {

    /**
     * The number of nodes in this graph.
     */
    val n: Int

    val nodeCount: Int
        get() = n

    val nodes: Sequence<Node>
        get() = (0 until n).asSequence()

    val edgeCount: Int
        get() {
            return (0 until n).sumOf { this.deg(it) } / 2
        }

    /**
     * Returns all the edges in this graph.
     */
    val edges: Sequence<Edge>

    /**
     * Returns the nodes `x` such that there is an edge from `x` to `a`. , that is, the nodes connected to `a`.
     *
     * @return nodes `{x | (x,a) in E}`
     */
    fun nodesIn(a: Node): Sequence<Node>

    /**
     * Returns the nodes `x` such that there is an edge from `a` to `x`., that is, the nodes connected from `a`.
     *
     * @return nodes `{x | (a,x) in E}`
     */
    fun nodesOut(a: Node): Sequence<Node>


    fun degIn(a: Node): Int

    fun degOut(a: Node): Int

    fun deg(a: Node): Int

    fun containsEdge(a: Node, b: Node): Boolean

//    /**
//     * Returns the node induced subgraph, that is, the subgraph containing
//     * all the given nodes, keeping the related edges.
//     */
//    fun inducedN(nodes : Set<Node>) : Graph
//
//    /**
//     * Returns the edge induced subgraph, that is, the subgraph containing
//     * all the nodes and only the given edges.
//     */
//    fun inducedE(edges : Set<Edge>) : Graph

//    fun removeN(nodes : S)

}

interface DirectedGraph : Graph {
    override fun deg(a: Node): Int {
        return degIn(a) + degOut(a)
    }


}

interface GraphWithData<V, E> : Graph {
    fun getNode(a: Node): V

    fun getEdge(a: Node, b: Node): E?
}

operator fun <N, E> GraphWithData<N, E>.get(a: Int): N = this.getNode(a)
operator fun <N, E> GraphWithData<N, E>.get(a: Int, b: Int): E? = this.getEdge(a, b)


//TODO implement mutable graphs

interface EdgeMutableGraph : Graph {
    fun addEdge()

    fun removeEdge()
}

interface EdgeMutableGraphWithData<N, E> : GraphWithData<N, E>

interface MutableGraph {

}