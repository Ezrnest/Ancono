package cn.ancono.math.discrete
//Created by lyc at 2021-03-28 19:32
interface UndirectedGraph : Graph {
    override fun degIn(a: Node): Int {
        return deg(a)
    }

    override fun degOut(a: Node): Int {
        return deg(a)
    }

    /**
     * Returns the nodes that are adjacent to `a`.
     */
    fun neighbors(a: Node): Sequence<Node>

    override fun nodesIn(a: Node): Sequence<Node> {
        return neighbors(a)
    }

    override fun nodesOut(a: Node): Sequence<Node> {
        return neighbors(a)
    }

    /**
     * Gets the adjacent matrix of this undirected graph. The (i,j)-th element in the adjacent matrix is `1` if
     * `(i,j)` is an edge, otherwise `0`.
     */
    fun adjacentMatrix(): Array<IntArray> {
        val mat = Array(n) {
            IntArray(n)
        }
        for (i in nodes) {
            for (j in neighbors(i)) {
                mat[i][j] = 1
            }
        }
        return mat
    }


//    override fun inducedN(nodes: Set<Node>): Graph {
//        return UndirectedNodeSubgraph(this,nodes)
//    }
//
//    override fun inducedE(edges: Set<Edge>): Graph {
//        TODO("Not yet implemented")
//    }

    /**
     * Returns the connected components. Each set in the returned list contains the nodes
     * of a connected component.
     *
     * This method uses DFS to find out all the connected components.
     */
    fun connectedComponents(): List<Set<Node>> {
        val state = Array(n) { VisitState.UNDISCOVERED }
        val prev = IntArray(n) { -1 }
        val components = arrayListOf<Set<Node>>()
        for (i in nodes) {
            if (state[i] == VisitState.UNDISCOVERED) {
                val wb = CollectiveWorkBag(StackWorkBag())
                Graphs.generalPrioritySearch(this, i, -1, wb, state, prev)
                components += wb.visited
            }
        }
        return components
    }

    /**
     * Returns the complement graph of `this`. The complement graph of `G` is a graph `H` with
     * the same nodes, but `(u,v) in E(H)` iff `(u,v) !in E(G)`.
     */
    fun complementGraph(): UndirectedGraph {
        return UndirectedGraphInMatrix(Array(n) { i ->
            IntArray(n) { j ->
                if (containsEdge(i, j)) {
                    0
                } else {
                    1
                }
            }
        })
    }

}


internal operator fun Array<IntArray>.get(i: Int, j: Int) = this[i][j]

/**
 * Describes an immutable undirected graph stored in matrix.
 */
open class UndirectedGraphInMatrix
internal constructor(
    private val matrix: Array<IntArray>

) : UndirectedGraph {

    init {
//        require(matrix.isSquare && matrix.rowCount == vertices.size)
//        require(edges.size == vertices.size && ed)
    }


    override val n: Int = matrix.size
    override val nodes: Sequence<Int> = (matrix.indices).asSequence()

    override val edges: Sequence<Edge>
        get() = nodes.flatMap { a ->
            (0..a).asSequence().filter { b -> containsEdge(a, b) }.map { b ->
                a to b
            }
        }

    override fun neighbors(a: Node): Sequence<Node> {
        return nodes.filter { containsEdge(a, it) }
    }


    override val edgeCount: Int by lazy {
        var c = 0
        for (i in nodes) {
            for (j in nodes) {
                c += matrix[i, j]
            }
        }
        c / 2
    }


    override fun deg(a: Node): Int {
        var c = 0
        for (i in nodes) {
            c += matrix[a, i]
        }
        c += matrix[a, a]
        return c
    }

    override fun containsEdge(a: Node, b: Node): Boolean {
        return matrix[a, b] > 0
    }


    override fun adjacentMatrix(): Array<IntArray> {
        return Array(n) { i ->
            matrix[i].copyOf()
        }
    }

    override fun complementGraph(): UndirectedGraph {
        return UndirectedGraphInMatrix(Array(n) { i ->
            IntArray(n) { j ->
                1 - matrix[i, j]
            }
        })
    }
}


class MatrixUndirectedGraphBuilder(n: Int) : GraphBuilder<UndirectedGraph> {
    val mat: Array<IntArray> = Array(n) {
        IntArray(n)
    }

    override fun Int.to(y: Int): Int {
        mat[this][y] = 1
        return y
    }

    override fun build(): UndirectedGraph {
        return UndirectedGraphInMatrix(mat)
    }
}

open class UndirectedGraphInMatrixWithData<V, E>(
    matrix: Array<IntArray>, val vertices: List<V>,
    val edgeData: List<List<E?>>,
) : UndirectedGraphInMatrix(matrix), GraphWithData<V, E> {
    override fun getNode(a: Node): V {
        return vertices[a]
    }

    override fun getEdge(a: Node, b: Node): E? {
        return edgeData[a][b]
    }

}

internal class UndirectedNodeSubgraph(private val graph: UndirectedGraph, private val ns: Set<Node>) : UndirectedGraph {
    //TODO
    override val n: Int = ns.size
    override val edges: Sequence<Edge>
        get() = TODO("Not yet implemented")

    override fun deg(a: Node): Int {
        return neighbors(a).count()
    }

    override fun containsEdge(a: Node, b: Node): Boolean {
        TODO("Not yet implemented")
    }

    override fun neighbors(a: Node): Sequence<Node> {
        return graph.neighbors(a).filter { it in ns }
    }

}

