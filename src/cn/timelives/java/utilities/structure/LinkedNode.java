package cn.timelives.java.utilities.structure;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class LinkedNode<E> extends DirectedGraphNode<E>{
	
	private Set<LinkedNode<E>> connectTo;
	
	private Set<LinkedNode<E>> connectBy;
	
	
	LinkedNode(DirectedGraph<E> graph) {
		super(graph);
		connectTo = new HashSet<>();
		connectBy = new HashSet<>();
	}
	LinkedNode(DirectedGraph<E> graph,E element,int connectToSize,int connectBySize) {
		super(graph);
		ele = element;
		connectTo = new HashSet<>(connectToSize);
		connectBy = new HashSet<>(connectBySize);
	}

	Set<LinkedNode<E>> getConnectedNodes() {
		if(connectTo!=null)
			return new HashSet<>(connectTo);
		return null;
	}
	boolean isConnected(LinkedNode<E> node) {
		return connectTo.contains(node);
	}
	
	Set<LinkedNode<E>> getConnectTo(){
		return connectTo;
	}
	
	Set<LinkedNode<E>> getConnectBy(){
		return connectBy;
	}

	
	void addConnectTo(LinkedNode<E> anotherNode){
		connectTo.add(anotherNode);
	}
	void addConnectTo(Collection<LinkedNode<E>> nodes){
		connectTo.addAll(nodes);
	}
	
	void addConnectBy(LinkedNode<E> anotherNode){
		connectBy.add(anotherNode);
	}
	void addConnectBy(Collection<LinkedNode<E>> nodes){
		connectBy.addAll(nodes);
	}
	
	boolean removeConnectTo(LinkedNode<E> anotherNode){
		return connectTo.remove(anotherNode);
	}
	
	boolean removeConnectTo(Set<LinkedNode<E>> anotherNode){
		return connectTo.removeAll(anotherNode);
	}
	
	boolean removeConnectBy(LinkedNode<E> anotherNode){
		return connectBy.remove(anotherNode);
	}
	boolean removeConnectBy(Set<LinkedNode<E>> anotherNode){
		return connectBy.removeAll(anotherNode);
	}
	void clearConnectTo(){
		connectTo.clear();
	}
	
	void clearConnectBy(){
		connectBy.clear();
	}
	
	
	@Override
	void removeFrom() {
		super.removeFrom();
		connectTo = null;
		connectBy = null;
	}
}