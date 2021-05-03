package cn.ancono.utilities.structure;

import cn.ancono.utilities.Printer;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * DirectedGraph is a graph where nodes' connections have directions.For each node in this graph,there may be
 * several references to other nodes,while the referenced nodes may not have references to this node.This is what
 * the "connect" here means. <P>
 * The node in the {@linkplain cn.ancono.utilities.structure.DirectedGraph} is {@link cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode},this kind of node is more
 * specific than general
 * {@linkplain Graph DirectedGraphNode<E>ode} and more methods are available.The node here
 *
 * @param <E> the element stored in the node.
 * @param <   DirectedGraphNode<E>> the subclass of Graph DirectedGraphNode<E>ode that methods will return.
 * @author lyc
 */
public abstract class DirectedGraph<E> implements Graph<E, DirectedGraphNode<E>>, Cloneable {


    /**
     * Create a new node that connects to all the given nodes.
     */
    @SuppressWarnings("unchecked")
    public abstract DirectedGraphNode<E> createNode(DirectedGraphNode<E>... connected);


    /**
     * Create a new node that connects to {@code connectTo} and was connected by {@code connectBy}.
     *
     * @param connectTo the nodes that the new node connect to.
     * @param connectBy the nodes that connect to the new node
     * @return a newly created node.
     */
    public abstract DirectedGraphNode<E> createNode(DirectedGraphNode<E>[] connectTo, DirectedGraphNode<E>[] connectBy);

    public abstract DirectedGraphNode<E> createNode(boolean connectToAll, boolean connectByAll);

    /**
     * Create a new node which holds all the connections that the node {@code extendFrom}
     * have,including the {@code extendFrom} itself(if it has self-connection).
     *
     * @param extendFrom a node
     * @return a newly created node
     */
    public abstract DirectedGraphNode<E> createNode(DirectedGraphNode<E> extendFrom);


    /**
     * Get the collection of nodes that this node connects to.For each node n in the returned
     * collection, {@code this.isConnectedTo(node,n)} will be true.Change to the collection wouldn't
     * change the graph.
     *
     * @param node a node
     * @return a set of nodes that this node connects to
     * @see #getConnectToElements(DirectedGraphNode)
     */
    public abstract Set<DirectedGraphNode<E>> getConnectTo(DirectedGraphNode<E> node);

    /**
     * Get the collection of elements in nodes that this node connects to.
     *
     * @param node a node
     * @return a collection of elements
     * @see #getConnectTo(DirectedGraphNode)
     */
    public abstract Collection<E> getConnectToElements(DirectedGraphNode<E> node);

    /**
     * Get the collection of nodes that this node is connected by.For each node n in the returned
     * collection, {@code this.isConnectedBy(node,n)} will be true.Change to the collection wouldn't
     * change the graph.
     *
     * @param node a node
     * @return a set of nodes that this node is connected by
     * @see #getConnectByElements(DirectedGraphNode)
     */
    public abstract Set<DirectedGraphNode<E>> getConnectBy(DirectedGraphNode<E> node);

    /**
     * Get the collection of elements in nodes that this node is connected by.
     *
     * @param node a node
     * @return a collection of elements
     * @see #getConnectBy(DirectedGraphNode)
     */
    public abstract Collection<E> getConnectByElements(DirectedGraphNode<E> node);

    /**
     * Return whether the two nodes are connected,return {@code true} if n1 is connected to n2 , otherwise false.
     * This method is equal to {@link #isConnectTo(cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode, cn.ancono.utilities.structure.DirectedGraph
     * DirectedGraphNode<E>ode)}.
     *
     * @return {@code true} if n1 refers to n2 , otherwise {@code false}
     */
    @Override
    public abstract boolean isConnected(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2);

    /**
     * Return whether the two nodes are connected in ,return {@code true} if n1 is connected to n2 , otherwise false.
     * This method is equal to {@link #isConnected(cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode, cn.ancono.utilities.structure.DirectedGraph
     * DirectedGraphNode<E>ode)}.
     * The method is created just for a clear view of code.
     *
     * @param n1 a node
     * @param n2 another node
     * @return {@code true} if n1 refers to n2 , otherwise {@code false}
     * @see #connectTo(cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode, cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode)
     */
    public boolean isConnectTo(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2) {
        return isConnected(n1, n2);
    }

    /**
     * Return whether the two nodes are connected,return {@code true} if n1 is connected by n2 , otherwise false.
     * This method is the parameter position transfered type of method {@link #isConnected(cn.ancono.utilities.structure.DirectedGraph
     * DirectedGraphNode<E>ode, cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode)}.
     * The call of {@code isConnectBy(n1,n2)} is equal to {@code isConnectTo(n2,n1)}.The method is created just for a
     * clear
     * view of code.
     *
     * @param n1 a node
     * @param n2 another node
     * @return {@code true} if n1 refers to n2 , otherwise {@code false}
     * @see #connectBy(DirectedGraphNode, DirectedGraphNode)
     */
    public boolean isConnectBy(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2) {
        return isConnected(n2, n1);
    }

    /**
     * Connect node n1 to node n2.This method is equal to {@link #connect DirectedGraphNode<E>ode(DirectedGraph
     * DirectedGraphNode<E>ode, DirectedGraph DirectedGraphNode<E>ode)}.
     * The method is created just for a clear view of code.
     * <p>{@literal n1 -> n2}
     *
     * @param n1 the node that will hold reference
     * @param n2 the node that will be referred to
     * @see #isConnectTo(cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode, cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode)
     */
    public void connectTo(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2) {
        connectNode(n1, n2);
    }

    /**
     * Connect node n1 by node n2.This method is the parameter position
     * transfered type of method {@link #connectTo(cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode, cn.ancono.utilities.structure.DirectedGraph
     * DirectedGraphNode<E>ode)}.
     * The call of {@code connectBy(n1,n2)} is equal to {@code connectTo(n2,n1)}.
     * The method is created just for a clear view of code.
     * <p>{@literal n1 <- n2}
     *
     * @param n1 the node that will be referred to
     * @param n2 the node that will hold reference
     * @see #isConnectBy(DirectedGraphNode, DirectedGraphNode)
     */
    public void connectBy(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2) {
        connectNode(n2, n1);
    }

    /**
     * Connect node n1 to n2.After this operation , {@code isConnected(n1,n2)} will return true if
     * no disconnecting method to this two node after that.This method is equal to {@link #connectTo(cn.ancono.utilities.structure.DirectedGraph
     * DirectedGraphNode<E>ode, cn.ancono.utilities.structure.DirectedGraph DirectedGraphNode<E>ode)}.
     */
    @Override
    public abstract void connectNode(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2);

    /**
     * Connect nodes as described in {@link Graph#connectNodes(GraphNode, Set)}
     */
    @Override
    public void connectNodes(DirectedGraphNode<E> n, Set<? extends DirectedGraphNode<E>> others) {
        connectNodesTo(n, others);
    }

    /**
     * The identity as {@link #connectNodes(DirectedGraphNode, Set)}.This has a more clear method name.
     *
     * @param n      a node
     * @param others other nodes to connect to
     * @see #connectTo(DirectedGraphNode, DirectedGraphNode)
     */
    public abstract void connectNodesTo(DirectedGraphNode<E> n, Set<? extends DirectedGraphNode<E>> others);

    /**
     * A transposition type of method {@link #connectNodesTo(DirectedGraphNode, Set)}. After the method is done,
     * for each node {@code other} in {@code others} , {@code isConnectedBy(n,other)} will be {@code true}.
     *
     * @param n      a node
     * @param others other nodes to connect by
     * @see #connectBy(DirectedGraphNode, DirectedGraphNode)
     */
    public abstract void connectNodesBy(DirectedGraphNode<E> n, Set<? extends DirectedGraphNode<E>> others);

    /**
     * Connect all the nodes from nodes,which means for each pair of node (n1,n2), isConnected(n1,n2) will return true.
     * DirectedGraphNode<E>otice
     * that both directions of connection will be added,  but self-connection won't be added.
     */
    @Override
    public abstract void connectAll(Set<? extends DirectedGraphNode<E>> nodes);


    /**
     * Disconnect the two nodes.After this method {@code isConnectedTo(n1,n2)} will return false.
     * If {@code isConnectedTo(n1,n2) == false}, then nothing will happen.
     */
    public abstract void disconnectNode(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2);

    /**
     * Disconnect nodes as described in {@link Graph#disconnectNodes(GraphNode, Set)}
     */
    @Override
    public void disconnectNodes(DirectedGraphNode<E> n, Set<? extends DirectedGraphNode<E>> others) {
        disconnectNodesTo(n, others);
    }

    /**
     * The identity as {@link disconnectNodes(DirectedGraphNode, Set)}.This has a more clear method name.
     *
     * @param n      a node
     * @param others other nodes to disconnect to
     * @see #disconnectNode(DirectedGraphNode, DirectedGraphNode)
     */
    public abstract void disconnectNodesTo(DirectedGraphNode<E> n, Set<? extends DirectedGraphNode<E>> others);

    /**
     * A transposition type of method {@link #disconnectNodesTo(DirectedGraphNode, Set)}. After the method is done,
     * for each node {@code other} in {@code others} , {@code isConnectedBy(n,other)} will be {@code false}.
     *
     * @param n      a node
     * @param others other nodes to disconnect by
     */
    public abstract void disconnectNodesBy(DirectedGraphNode<E> n, Set<? extends DirectedGraphNode<E>> others);

    /**
     * Disconnect all the nodes from {@code nodes},which means for each pair of node (n1,n2),
     * {@code isConnected(n1,n2)} will return {@code false}.
     * DirectedGraphNode<E>otice that both directions of connection will be removed and self-connection will be removed
     * as well.
     */
    public abstract void disconnectAll(Set<? extends DirectedGraphNode<E>> nodes);

    /**
     * Return all the nodes that this node can reach through the connection.The reachable node in directed graph is
     * node that can be reached from starting node , and because the connection in directed graph has direction, node A
     * can reach node B doesn't means node B and reach node A.And in this method , only the paths from this node are
     * considered.
     * <p>The node {@code fromNode} is included in this set.
     */
    public abstract Set<DirectedGraphNode<E>> getReachableNodes(DirectedGraphNode<E> from);

    /**
     * This method is a transposition type of {@link #getReachableNodes(DirectedGraphNode)}.
     * <p>The node {@code fromNode} is included in this set.
     *
     * @param from a node
     * @return a set of nodes
     */
    public abstract Set<DirectedGraphNode<E>> getReachableNodesBy(DirectedGraphNode<E> from);

    /**
     * Returns the in degree of this node.The number is equal to {@code getConnectBy(node).size()}.
     *
     * @param node a node
     * @return the in degree of this node
     */
    public abstract int getInDegree(DirectedGraphNode<E> node);

    /**
     * Returns the out degree of this node.The number is equal to {@code getConnectTo(node).size()}.
     *
     * @param node a node
     * @return the out degree of this node
     */
    public abstract int getOutDegree(DirectedGraphNode<E> node);

    /**
     * Returns a deep copy of this graph,which means all of the nodes in the
     * graph will be cloned and their relations and the elements reference will
     * be the identity.The elements in the graph will not be cloned.
     *
     * @return a clone of this graph.
     */
    public abstract Object clone();

    /**
     * Change the stored element by using the given computing function, the type will change according to
     * the function.This operation will only affect the graph's stored elements, the structure of this map
     * will not be changed.This method will always return a new graph instead of modifying the elements
     * stored in case {@code this} may be used after calling this method and type safety canned be provided.
     *
     * @param mapper a function
     * @return a newly created graph,which is equal to {@code this} in structure while elements are changed.
     */
    public abstract <T> cn.ancono.utilities.structure.DirectedGraph<T> mapToGraph(Function<E, T> mapper);

    /**
     * Compute the transposition of this DirectedGraph. A transposition is a graph that all the nodes
     * is the identity(clone),but the connection will be turned,which means if in this graph {@code
     * isConnected(n1,n2)==true},
     * then in the transposition , {@code isConnected(n2,n1)==true}.
     *
     * @return the transposition of this graph
     */
    public abstract cn.ancono.utilities.structure.DirectedGraph<E> transpositionOf();

    /**
     * Compute a sub graph of this directed graph.The sub graph contains all the nodes in {@code nodes} and all the
     * connection
     * of these nodes are kept , but the other nodes and connections will not remain.
     *
     * @param nodes a set of nodes in this graph
     * @return a sub graph , newly created
     */
    public abstract cn.ancono.utilities.structure.DirectedGraph<E> subGraph(Set<? extends DirectedGraphNode<E>> nodes);

    /**
     * Returns a new sub graph of this directed graph.The sub graph is a graph that only contains the given node and all
     * its
     * connect-to nodes,and the connections in the new graph are only these connections , which means in the new graph ,
     * {@code isConnectTo(n1,n2)==true}
     * if and only if {@code n1 == node}.Even if there may exist nodes that have both-direction connection with {@code
     * node}, the
     * connections in the new graph will only remains one direction.
     *
     * @param node a node in this graph
     * @return a sub graph
     */
    public abstract cn.ancono.utilities.structure.DirectedGraph<E> subGraphOfTo(DirectedGraphNode<E> node);

    /**
     * Returns a new sub graph of this directed graph.The sub graph is a graph that only contains the given node and all
     * its
     * connect-by nodes,and the connections in the new graph are only these connections , which means in the new graph ,
     * {@code isConnectBy(n1,n2)==true}
     * if and only if {@code n1 == node}.Even if there may exist nodes that have both-direction connection with {@code
     * node}, the
     * connections in the new graph will only remains one direction.
     *
     * @param node a node in this graph
     * @return a sub graph
     */
    public abstract cn.ancono.utilities.structure.DirectedGraph<E> subGraphOfBy(DirectedGraphNode<E> node);

    /**
     * Almost the identity as the description in {@linkplain Graph#connectedComponent()}, and the returned
     * connected component is a strongly connected component.
     */
    public abstract List<Set<DirectedGraphNode<E>>> connectedComponent();

    /**
     * Like the method of {@link #connectedComponent()},this method returns all of the strongly connected component
     * of this directed graph, but this method will return a graph that contains the necessary relationship of the
     * strongly connected component in this graph.The returned graph is like a combined version of this graph, in each
     * node of it, the element stored is a set of nodes and the nodes are one of the connected component in this graph.
     *
     * @return a graph that represents all the strongly connected components in this.
     * @see #connectedComponent()
     */
    public abstract cn.ancono.utilities.structure.DirectedGraph<Set<DirectedGraphNode<E>>> stronglyConnectedComponent();

    /**
     * Almost the identity as the description in {@linkplain Graph#connectedComponentOf(DirectedGraphNode)},and the
     * returned
     * connected component is a strongly connected component.
     */
    public abstract Set<DirectedGraphNode<E>> connectedComponentOf(DirectedGraphNode<E> node);

    /**
     * Use printer to print this graph at the input stream.
     *
     * @see {@link Printer}
     */
    public abstract void printGraph();

}
