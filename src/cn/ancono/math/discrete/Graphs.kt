package cn.ancono.math.discrete

import java.lang.Double.min
import java.util.*

interface GraphBuilder<G> {
    infix fun Int.to(y: Int): Int

    fun build(): G
}

/**
 * Builds an undirected graph.
 */
fun buildGraph(n: Int, builderAction: GraphBuilder<UndirectedGraph>.() -> Unit): UndirectedGraph {
    val builder = MatrixUndirectedGraphBuilder(n)
    builderAction(builder)
    return builder.build()
}

enum class VisitState {
    UNDISCOVERED, VISITING, VISITED
}

open class SearchResult(
    val source: Node, val target: Node,
    val visitStates: Array<VisitState>, val parents: IntArray
) {
    /**
     * Gets the path from the source to the target basing on this search result.
     * Caller should check `targetReached` first to ensure such a path do exist.
     */
    fun getPath(): List<Node> {
        return Graphs.buildPath(source, target, parents)
    }

    val targetReached: Boolean
        get() = visitStates[target] == VisitState.VISITED
}


/**
 * A (prioritized) work bag for general priority search.
 */
interface WorkBag {
    fun init(start: Node)

    /**
     * Adds the node `y` to the working bag with priority computed according to the
     * current edge `x-y`.
     *
     */
    fun add(x: Node, y: Node)

    /**
     * Returns and removes the item with lowest priority.
     */
    fun pop(): Node

    /**
     * Updates the priority of node `y` according to priority computed according to the
     * current edge `x-y`.
     *
     * @return `true` if the new priority is lower than
     */
    fun update(x: Node, y: Node): Boolean

    fun isEmpty(): Boolean
}

class StackWorkBag : WorkBag {
    private val stack: Queue<Int> = Collections.asLifoQueue(ArrayDeque())
    override fun init(start: Node) {
        stack.offer(start)
    }

    override fun add(x: Node, y: Node) {
        stack.offer(y)
    }

    override fun pop(): Node {
        return stack.poll()
    }

    override fun update(x: Node, y: Node): Boolean {
        return false
    }

    override fun isEmpty(): Boolean {
        return stack.isEmpty()
    }
}

class QueueWorkBag : WorkBag {
    private val queue: Queue<Int> = ArrayDeque()
    override fun init(start: Node) {
        queue.offer(start)
    }

    override fun add(x: Node, y: Node) {
        queue.offer(y)
    }

    override fun pop(): Node {
        return queue.poll()
    }

    override fun update(x: Node, y: Node): Boolean {
        return false
    }

    override fun isEmpty(): Boolean {
        return queue.isEmpty()
    }
}

class PriorityQueueWorkBag(
    graph: Graph,
    private val weight: (Node, Node) -> Double = { _, _ -> 1.0 },
    private val heuristic: (Node) -> Double = { 0.0 }
) : WorkBag {
    private val distances = DoubleArray(graph.n) { Double.POSITIVE_INFINITY }
    private val pq = PriorityQueue<Pair<Node, Double>>(compareBy { it.second })
    override fun init(start: Node) {
        distances[start] = 0.0
        pq.add(start to 0.0)
    }

    override fun add(x: Node, y: Node) {
        val w = distances[x] + weight(x, y) + heuristic(y)
        pq.add(y to w)
        distances[y] = w
    }

    override fun pop(): Node {
        return pq.remove().first
    }

    override fun update(x: Node, y: Node): Boolean {
        val w = distances[x] + weight(x, y) + heuristic(y)
        return if (w < distances[y]) {
            pq.remove(y to distances[y])
            pq.add(y to w)
            distances[y]
            true
        } else {
            false
        }
    }

    override fun isEmpty(): Boolean {
        return pq.isEmpty()
    }
}

class CollectiveWorkBag(val bag: WorkBag) : WorkBag {
    val visited = hashSetOf<Node>()
    override fun init(start: Node) {
        bag.init(start)
        visited.add(start)
    }

    override fun add(x: Node, y: Node) {
        bag.add(x, y)
        visited.add(y)
    }

    override fun pop(): Node {
        return bag.pop()
    }

    override fun update(x: Node, y: Node): Boolean {
        return bag.update(x, y)
    }

    override fun isEmpty(): Boolean {
        return bag.isEmpty()
    }
}

/**
 * Provides methods related to graphs.
 */
object Graphs {


    /**
     * Creates an undirected graph from an adjacent matrix.
     */
    fun fromAdjMatrix(matrix: Array<IntArray>): UndirectedGraph {
        val n = matrix.size
        require(matrix.all { it.size == n })
        for (i in 0 until n) {
            for (j in 0 until n) {
                require(matrix[i, j] == 0 || matrix[i, j] == 1)
            }
        }
        return UndirectedGraphInMatrix(matrix)
    }

    /**
     * Returns a copy of the given graph in matrix representation.
     */
    fun toMatrixRep(graph: UndirectedGraph): UndirectedGraph {
        return UndirectedGraphInMatrix(graph.adjacentMatrix())
    }


    /**
     * Gets the complete graph `K_n`.
     */
    fun completeGraph(n: Int): UndirectedGraph {
        require(n > 0)
        val mat = Array(n) {
            IntArray(n) { 1 }
        }
        for (i in 0 until n) {
            mat[i][i] = 0
        }
        return fromAdjMatrix(mat)
    }

    fun shortestPath(graph: GraphWithData<*, out Number>, src: Node, dest: Node): Pair<Double, List<Node>>? {
        return shortestPath(graph, src, dest) { i, j ->
            graph.getEdge(i, j)?.toDouble() ?: Double.POSITIVE_INFINITY
        }
    }

    internal fun buildPath(src: Node, target: Node, prev: IntArray): List<Node> {
        val path = arrayListOf<Int>()
        var x = target
        while (x != src) {
            path.add(x)
            x = prev[x]
        }
        return path.reversed()
    }

    /**
     * Finds the shortest path from source `src` to destination `dest`, using the given edge weight function `weight`.
     *
     * @return a pair of minimal cost and a list of nodes indicating the path.
     */
    fun shortestPath(graph: Graph, src: Node, dest: Node, weight: (Node, Node) -> Double): Pair<Double, List<Node>>? {
        // use Dijkstra algorithm
        val n = graph.n
        val prev = IntArray(n) { -1 }
//        val dist = DoubleArray(n) { Double.POSITIVE_INFINITY }
//        dist[src] = 0.0
        val queue = PriorityQueue<Triple<Int, Int, Double>>(compareBy { it.third })
        val state = Array(n) { VisitState.UNDISCOVERED }
        queue.add(Triple(src, -1, 0.0))
        var cost = Double.POSITIVE_INFINITY
        while (queue.isNotEmpty()) {
            val (x, from, d) = queue.remove()
            if (state[x] == VisitState.VISITED) {
                continue
            }
            state[x] = VisitState.VISITED
            prev[x] = from
            if (x == dest) {
                cost = d
                break
            }
            for (y in graph.nodesOut(x)) {
                if (state[y] == VisitState.VISITED) {
                    continue
                }
                val w = weight(x, y)
                queue.add(Triple(y, x, w + d))
            }
        }
        if (prev[dest] < 0) {
            return null
        }
        val path = buildPath(src, dest, prev)
        return cost to path
    }


    fun generalPrioritySearch(
        graph: Graph, start: Node, target: Node, bag: WorkBag,
        state: Array<VisitState>,
        prev: IntArray,
    ): SearchResult {
        bag.init(start)
        outer@
        while (!bag.isEmpty()) {
            val x = bag.pop()
            state[x] = VisitState.VISITED
            if (x == target) {
                break
            }

            for (y in graph.nodesOut(x)) {
                when (state[y]) {
                    VisitState.VISITED -> continue
                    VisitState.UNDISCOVERED -> {
                        state[y] = VisitState.VISITING
                        bag.add(x, y)
                        prev[y] = x
                    }
                    VisitState.VISITING -> {
                        if (bag.update(x, y)) {
                            prev[y] = x
                        }
                    }
                }
            }
        }
        return SearchResult(start, target, state, prev)
    }


    /**
     * A general version of priority search.
     */
    fun generalPrioritySearch(
        graph: Graph, start: Node, dest: Node,
        bag: WorkBag
    ): SearchResult {
        val n = graph.n
        val state = Array(n) { VisitState.UNDISCOVERED }
        val prev = IntArray(n) { -1 }
        return generalPrioritySearch(graph, start, dest, bag, state, prev)
    }

    /**
     * Performs deep-first search on the given graph with the given starting node.
     *
     * @param target the target to search for, it can be `-1` to let the algorithm go over the whole connected component.
     */
    fun dfs(graph: Graph, start: Node, target: Node): SearchResult {
        return generalPrioritySearch(
            graph, start, target,
            StackWorkBag()
        )
    }

    /**
     * Performs breath-first search on the given graph with the given starting node.
     *
     * @param target the target to search for, it can be `-1` to let the algorithm go over the whole connected component.
     */
    fun bfs(graph: Graph, start: Node, target: Node): SearchResult {
        return generalPrioritySearch(
            graph, start, target,
            QueueWorkBag()
        )
    }

    /**
     * Performs uniform cost search on the given graph with the given starting node and the given edge weight.
     *
     * @param target the target to search for, it can be `-1` to let the algorithm go over the whole connected component.
     */
    fun ucs(graph: Graph, start: Node, target: Node, weight: (Node, Node) -> Double): SearchResult {
        return generalPrioritySearch(
            graph, start, target,
            PriorityQueueWorkBag(graph, weight)
        )
    }

    fun aStarSearch(
        graph: Graph,
        start: Int,
        dest: Int,
        weight: (Int, Int) -> Double,
        heuristic: (Int) -> Double
    ): SearchResult {
        return generalPrioritySearch(
            graph, start, dest,
            PriorityQueueWorkBag(graph, weight, heuristic)
        )
    }

    fun pairwiseShortestPath(graph: Graph, weight: (Node, Node) -> Double): Array<DoubleArray> {
        val dists = Array(graph.n) { DoubleArray(graph.n) { Double.MAX_VALUE } }
        for (i in graph.nodes) {
            dists[i][i] = 0.0
            for (j in graph.nodesOut(i)) {
                dists[i][j] = weight(i, j)
            }
        }
        for (k in graph.nodes) {
            for (i in graph.nodes) {
                for (j in graph.nodes) {
                    dists[i][j] = min(dists[i][j], dists[i][k] + dists[k][j])
                }
            }
        }
        return dists
    }


}