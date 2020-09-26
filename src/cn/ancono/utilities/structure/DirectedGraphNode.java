package cn.ancono.utilities.structure;


public abstract class DirectedGraphNode<E> extends GraphNode<E, cn.ancono.utilities.structure.DirectedGraphNode<E>> {

    private DirectedGraph<E> graph;

    DirectedGraphNode(DirectedGraph<E> graph) {
        this.graph = graph;
    }

    @Override
    public Graph<E, cn.ancono.utilities.structure.DirectedGraphNode<E>> getGraph() {
        return graph;
    }

    public DirectedGraph<E> getDirectedGraph() {
        return graph;
    }

    /**
     * This method should be called when this node is removed from a graph.
     */
    void removeFrom() {
        graph = null;
    }

}
