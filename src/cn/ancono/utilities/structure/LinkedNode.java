package cn.ancono.utilities.structure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class LinkedNode<E> extends DirectedGraphNode<E> {

    private Set<cn.ancono.utilities.structure.LinkedNode<E>> connectTo;

    private Set<cn.ancono.utilities.structure.LinkedNode<E>> connectBy;


    LinkedNode(DirectedGraph<E> graph) {
        super(graph);
        connectTo = new HashSet<>();
        connectBy = new HashSet<>();
    }

    LinkedNode(DirectedGraph<E> graph, E element, int connectToSize, int connectBySize) {
        super(graph);
        ele = element;
        connectTo = new HashSet<>(connectToSize);
        connectBy = new HashSet<>(connectBySize);
    }

    Set<cn.ancono.utilities.structure.LinkedNode<E>> getConnectedNodes() {
        if (connectTo != null)
            return new HashSet<>(connectTo);
        return null;
    }

    boolean isConnected(cn.ancono.utilities.structure.LinkedNode<E> node) {
        return connectTo.contains(node);
    }

    Set<cn.ancono.utilities.structure.LinkedNode<E>> getConnectTo() {
        return connectTo;
    }

    Set<cn.ancono.utilities.structure.LinkedNode<E>> getConnectBy() {
        return connectBy;
    }


    void addConnectTo(cn.ancono.utilities.structure.LinkedNode<E> anotherNode) {
        connectTo.add(anotherNode);
    }

    void addConnectTo(Collection<cn.ancono.utilities.structure.LinkedNode<E>> nodes) {
        connectTo.addAll(nodes);
    }

    void addConnectBy(cn.ancono.utilities.structure.LinkedNode<E> anotherNode) {
        connectBy.add(anotherNode);
    }

    void addConnectBy(Collection<cn.ancono.utilities.structure.LinkedNode<E>> nodes) {
        connectBy.addAll(nodes);
    }

    boolean removeConnectTo(cn.ancono.utilities.structure.LinkedNode<E> anotherNode) {
        return connectTo.remove(anotherNode);
    }

    boolean removeConnectTo(Set<cn.ancono.utilities.structure.LinkedNode<E>> anotherNode) {
        return connectTo.removeAll(anotherNode);
    }

    boolean removeConnectBy(cn.ancono.utilities.structure.LinkedNode<E> anotherNode) {
        return connectBy.remove(anotherNode);
    }

    boolean removeConnectBy(Set<cn.ancono.utilities.structure.LinkedNode<E>> anotherNode) {
        return connectBy.removeAll(anotherNode);
    }

    void clearConnectTo() {
        connectTo.clear();
    }

    void clearConnectBy() {
        connectBy.clear();
    }


    @Override
    void removeFrom() {
        super.removeFrom();
        connectTo = null;
        connectBy = null;
    }
}