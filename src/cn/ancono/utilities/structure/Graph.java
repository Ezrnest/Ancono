package cn.ancono.utilities.structure;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * A graph is like a map , each node in the graph can connect to other nodes.
 * <p>
 * <h1>Node</h1>
 * The node of graph is like an ID card of each object in this graph, almost all operations
 * are relate to node.The only methods from node are getting the graph this node belongs to and getting or
 * setting the element at the node, which is mostly separate from the structure of the graph.
 * <p><b>Notice:Using node from another graph as a parameter is not acceptable and exception will be thrown.</b>
 * <p>The reason of using node to identify objects is very clear,because the objects
 * in the graph are not strongly associated with the structure of this graph,and the normal add or remove
 * operations as those in {@linkplain Collection} are not suitable.The nodes are implements by {@link GraphNode},
 * all the subclasses of this should use subclass {@link GraphNode} as the node in the graph.
 * <p>
 * The node type in the graph is changeable , just extends the super class of graph and implements the node operation,
 * then this kind of Graph will be also available to all places where a Graph is needed. For better implement in some
 * specific area, this kind of usage keeps both type-safety and improved time performance.
 * <p>
 *
 * @param <E> the element stored in the node.
 * @param <N> the subclass of GraphNode that methods will return.
 * @author lyc
 */
public interface Graph<E, N extends GraphNode<E, N>>
        extends Iterable<N> {
    /**
     * Create a new node that is connected with the given nodes.
     *
     * @param connected
     * @return the newly created node
     */
    @SuppressWarnings("unchecked")
    public N createNode(N... connected);

    /**
     * Create a new node in the graph,
     *
     * @param connectAll whether this node should connect to all other nodes.
     * @return the newly created node
     */
    public N createNode(boolean connectAll);

    /**
     * Create a new node in the graph , no node will be connected with this node.
     *
     * @return the newly created node
     */
    public N createNode();

    /**
     * Connect the two nodes.If the nodes have already been connected, then nothing will
     * happen.
     *
     * @param n1 a node
     * @param n2 another node
     * @see #disconncetNode(GraphNode, GraphNode)
     */
    public void connectNode(N n1, N n2);

    /**
     * Connect the node {@code n} and all the nodes in {@code others} , is method is basically
     * equal to calling {@link #connectNode(GraphNode, GraphNode)} using {@code n} as one parameter
     * and one node in {@code others} as the other parameter,but this method should have a
     * faster time performance.
     *
     * @param n      a node
     * @param others other nodes to connect
     */
    public void connectNodes(N n, Set<? extends N> others);

    /**
     * Disconnect the two nodes.If the nodes have not been connected, then nothing will
     * happen.
     *
     * @param n1 a node
     * @param n2 another node
     * @see #connectNode(GraphNode, GraphNode)
     */
    public void disconnectNode(N n1, N n2);

    /**
     * Disconnect the node {@code n} and all the nodes in {@code others} , is method is basically
     * equal to calling {@link #disconnectNode(GraphNode, GraphNode)} using {@code n} as one parameter
     * and one node in {@code others} as the other parameter,but this method should have a
     * faster time performance.
     *
     * @param n      a node
     * @param others other nodes to disconnect
     */
    public void disconnectNodes(N n, Set<? extends N> others);

    /**
     * Remove the node from the graph , all the connection from this node will be removed too.
     *
     * @param node the node to be removed.
     */
    public void removeNode(N node);

    /**
     * Transfer the connection of ({@code holder} to {@code connectTo}) to ({@code toGive} to {@code connectTo}.This
     * method only works when {@code isConnected(holder,connectTo) == true} or {@code false} will be returned and nothing will
     * happen.If {@code isConnected(holder,connectTo) == true} , then after this method, {@code isConnected(holder,connectTo)}
     * will be {@code false} and {@code isConnected(toGive,connectTo) } will be {@code true}.The method will still remove the
     * connection of ({@code holder} to {@code connectTo}) even if {@code isConnected(toGive,connectTo) == true } at first.
     *
     * @param holder    the original connecter to {@code connectTo}
     * @param connectTo the node connected
     * @param toGive    the new connector to {@code connectTo}
     * @return {@code true} if the method works,{@code false} if {@code isConnected(holder,connectTo) == false}
     */
    public boolean transferConnection(N holder, N connectTo, N toGive);

    /**
     * Transfer all the connections that {@code holder} has to the node {@code toGive}.After this method , the {@code holder}
     * will have no connection , which means for each node in the graph , {@code isConnected(holder,n)}  will return false.
     *
     * @param holder the original connecter
     * @param toGive the new connector
     */
    public void transfetConnection(N holder, N toGive);

    /**
     * Transfer all the connections that {@code holder} has to the node {@code toGive}.All the connection that node {@code holder}
     * has won't be affected.
     *
     * @param holder a node that to copy connection from
     * @param toGive a node that will have the copy of connection
     */
    public void copyConnection(N holder, N toGive);

    /**
     * Connect all the nodes from {@code nodes},which means for each pair of node (n1,n2),
     * {@code isConnected(n1,n2)} will return {@code true}.
     *
     * @param nodes a set of nodes to be connected.
     */
    public void connectAll(Set<? extends N> nodes);

    /**
     * Disconnect all the nodes from {@code nodes},which means for each pair of node (n1,n2),
     * {@code isConnected(n1,n2)} will return {@code false}.
     *
     * @param nodes a set of nodes to be connected.
     */
    public void disconnectAll(Set<? extends N> nodes);


    /**
     * Compare this graph and the given graph , if this graph and the graph have the identity structure and
     * {@code equal(Object)}method for corresponding elements all return {@code true},then the two
     * graphs are the identity,and the method will return {@code true}.
     * <p>This method may cost much time,seek some better methods if they are available.
     *
     * @param obj an object
     * @return {@code true} if the two graphs are the identity,{@code false}
     */
    public boolean equals(Object obj);


    /**
     * Return all nodes in this graph as a set.
     *
     * @return a set of nodes
     */
    public Set<N> getAllNodes();

    public Set<N> getConnectedNodes(N node);

    public Collection<E> getConncetedElements(N node);

    /**
     * Return whether the two nodes are connected,return {@code true} if n1 and n2 are connected, otherwise {@code false}.
     *
     * @param n1 a node
     * @param n2 another node
     * @return {@code true} if n1 and n2 are connected, otherwise {@code false}.
     */
    public boolean isConnected(N n1, N n2);

    /**
     * Return all the nodes that this node can reach through the connection.More specific,the set contains nodes that
     * can be reached from the starting node {@code fromNode}  through a list of nodes.We call the list of node "path" , and in
     * the path , {@code isConnected(previousNode,nextNode)==true}.
     * This method can vary a lot from different
     * implements, you may seek other more efficient methods instead.
     * <p>The node {@code fromNode} is included in this set.
     *
     * @param fromNode a node
     * @return a set of nodes
     */
    public Set<N> getReachableNodes(N fromNode);

    /**
     * Return {@code true} if {@code toNode} is reachable for {@code fromNode}.The method is basically equal to
     * {@code getReachableNodes(fromNode).contains(toNode)}, but has a better performance with less memory cost.
     *
     * @param fromNode the node to start
     * @param toNode   the node to reach
     * @return {@code true} if {@code toNode} is reachable from {@code fromNode} , else {@code false}
     */
    public boolean isReachable(N fromNode, N toNode);


    /**
     * Returns the number of nodes in this graph.
     *
     * @return the number of nodes in this graph.
     */
    public int size();

    /**
     * Returns the number of connections the node has.The number is equal to {@code getConnectedNodes(node).size()}.
     *
     * @param node a node
     * @return the number of connections the node has.
     */
    public int getConnectionCount(N node);

    /**
     * Returns all the connected components in this graph , the connected component is a set of nodes that each node
     * in this set can reach all the other nodes in the identity set.Each of the returned connected components will be the largest
     * connected component for its containing nodes , and any two sets' complement will be none , while the union of the
     * sets will be all the nodes in this graph.<p>
     * The returned sets will be contained in a list.
     *
     * @return a list of sets containing nodes in each connected component.
     */
    public List<Set<N>> connectedComponent();

    /**
     * Return the largest connected component that contains the given node.For each node pair (n1,n2) in the set , {@code isReachable(n1,n2)==true} and
     * {@code isReachable(n2,n1)==true}.
     *
     * @param node a node
     * @return a set containing nodes in the connected component of {@code node}
     */
    public Set<N> connectedComponentOf(N node);


    public static <E> E getElement(GraphNode<E, ?> node) {
        return node.getElement();
    }


}
