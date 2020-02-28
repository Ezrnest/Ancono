package cn.ancono.utilities.structure;

/**
 * @param <E> the element stored in the node.
 * @param <N> the subclass of GraphNode that methods will return.
 * @author lyc
 */
public abstract class GraphNode<E, N extends GraphNode<E, N>> extends Node<E> {


    protected GraphNode(E ele) {
        super(ele);
    }

    public GraphNode() {
    }

    /**
     * Return the graph the node is referring to,if the node was removed from a graph,then
     * null will be returned.
     *
     * @return the graph the node is referring to , or null if the node was removed.
     */
    public abstract Graph<E, N> getGraph();

}
