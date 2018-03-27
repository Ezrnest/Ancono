package cn.timelives.java.utilities.structure;

import java.util.Set;

/**
 * A directed acyclic graph is a graph doesn't have loops.In this graph, self-connection is unacceptable
 * @author lyc
 *
 * @param <E> the element stored in the node.
 * @param <N> the subclass of GraphNode that methods will return.
 */
public abstract class DirectedAcyclicGraph<E> extends DirectedGraph<E>{
	/**
	 * Throws unsupported exception because there must be a loop if the nodes are connected.
	 * @throws UnsupportedOperationException
	 */
	@Override
	public void connectAll(Set<? extends DirectedGraphNode<E>> nodes) {
		throw new UnsupportedOperationException("DirectedAcyclicGraph");
	}
	
	
	
	
}



