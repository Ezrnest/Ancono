package cn.timelives.java.utilities.structure;

import static cn.timelives.java.utilities.Printer.print;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;

import cn.timelives.java.utilities.Printer;

/**
 * A implement of {@link DirectedGraph} which uses double-linked nodes in the graph.This implement 
 * is suitable for a graph that massive connect-to and connect-by operations are called while the 
 * number of nodes is much bigger than the real connection of the nodes.In this graph,self-connection is 
 * acceptable and no exception will be thrown when you call {@code connectNode(n,n)}.
 * <p>
 * An example is the family tree,
 * if there are a lot of objects in the tree , use a matrix to record the relations in the tree is 
 * very unnecessary and a waste of memory. 
 * 
 * 
 * <p>
 * 
 * 
 * 
 * 
 * @author lyc
 *
 * @param <E> the type of elements stored in the graph
 */
/**
 * @author lyc
 *
 * @param <E>
 */
public class LinkedDirectedGraph<E> extends DirectedGraph<E> {
	
	
//	private int modCount;
	
	private List<LinkedNode<E>> nodes;
	
	
	
	public LinkedDirectedGraph(){
		nodes = new ArrayList<LinkedNode<E>>();
	}
	
	private LinkedDirectedGraph(List<LinkedNode<E>> nodes){
		this.nodes = nodes;
	}
	
	
	
	
	
	
	@Override
	public Iterator<DirectedGraphNode<E>> iterator() {
		return new Itr();
	}

	class Itr implements Iterator< DirectedGraphNode<E>>{
		
		private Iterator<LinkedNode<E>> it = nodes.iterator();
		
		@Override
		public boolean hasNext() {
			return it.hasNext();
		}

		@Override
		public DirectedGraphNode<E> next() {
			return it.next();
		}
		
	}
	
	/**
	 * 
	 * @param nodes the nodes to check
	 * @throws IllegalArgumentException if any of the nodes doesn't refer to this
	 * @throws NullPointerException if null value is given
	 */
	@SuppressWarnings("unchecked")
	private LinkedNode<E>[] checkNodes(DirectedGraphNode<E>...nodes){
		for( DirectedGraphNode<E> n : nodes){
			if(n.getGraph()!=this)
				throw new IllegalArgumentException("Different graph");
		}
		return (LinkedNode<E>[]) nodes;
	}
	/**
	 * 
	 * @param n the node to check
	 * @throws IllegalArgumentException if any of the nodes doesn't refer to this
	 * @throws NullPointerException if null value is given
	 */
	private LinkedNode<E> checkNode(DirectedGraphNode<E> n){
		if(n.getGraph()!=this)
			throw new IllegalArgumentException("Different graph");
		return (LinkedNode<E>) n;
	}

	@Override
	public LinkedNode<E> createNode() {
		LinkedNode<E> node = new LinkedNode<E>(this);
		nodes.add(node);
		return node;
	}
	/**
	 * @throws NullPointerException if null value is given
	 * @throws IllegalArgumentException if any of the nodes doesn't refer to this
	 */
	@SuppressWarnings("unchecked")
	@Override
	public LinkedNode<E> createNode(DirectedGraphNode<E>... connected) {
		checkNodes(connected);
		LinkedNode<E>[] linked = (LinkedNode<E>[]) connected;
		LinkedNode<E> node = createNode();
		for(LinkedNode<E> n : linked){
			connectNodeTo0(node,n);
		}
		
		return node;
	}

	@Override
	public LinkedNode<E> createNode(DirectedGraphNode<E>[] connectTo, DirectedGraphNode<E>[] connectBy) {
		LinkedNode<E>[] ctl=checkNodes(connectTo);
		LinkedNode<E>[] cbl=checkNodes(connectBy);
		LinkedNode<E> node = createNode();
		for(LinkedNode<E> n : ctl){
			connectNodeTo0(node,n);
		}
		for(LinkedNode<E> n : cbl){
			connectNodeTo0(n,node);
		}
		return node;
	}




	@Override
	public LinkedNode<E> createNode(boolean connectToAll) {
		return createNode(connectToAll,false);
	}

	@Override
	public LinkedNode<E> createNode(boolean connectToAll, boolean connectByAll) {
		LinkedNode<E> node = new LinkedNode<E>(this);
		if(connectToAll ){
			if(connectByAll){
				for(LinkedNode<E> n : nodes){
					connectNodeTo0(n,node);
					connectNodeTo0(node,n);
				}
			}else{
				for(LinkedNode<E> n : nodes){
					connectNodeTo0(node,n);
				}
			}
		}else if(connectByAll){
			for(LinkedNode<E> n : nodes){
				connectNodeTo0(n,node);
			}
		}
		return node;
	}
	
	@Override
	public LinkedNode<E> createNode(DirectedGraphNode<E> extendFrom) {
		LinkedNode<E> node = createNode();
		copyConnection(node, extendFrom);
		return node;
	}
	

	/**
	 * A version of connecting nodes without checking first.
	 * <p> n1 -> n2
	 * @param n1
	 * @param n2
	 */
	private void connectNodeTo0(LinkedNode<E> n1, LinkedNode<E> n2){
		n1.addConnectTo(n2);
		n2.addConnectBy(n1);
	}
	
	private void disconnectNode0(LinkedNode<E> n1, LinkedNode<E> n2){
		n1.removeConnectTo(n2);
		n2.removeConnectBy(n1);
	}
	






	@Override
	public void connectNode(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2) {
		
		connectNodeTo0(checkNode(n1),checkNode(n2));
	}
	
	
	@Override
	public void connectNodesTo(DirectedGraphNode<E> nD, Set<? extends DirectedGraphNode<E>> others) {
		LinkedNode<E> n = checkNode(nD);
		for(DirectedGraphNode<E> node: others){
			checkNode(node);
		}
		for(DirectedGraphNode<E> node: others){
			connectNodeTo0(n, (LinkedNode<E>)node);
		}
	}
	
	@Override
	public void connectNodesBy(DirectedGraphNode<E> nD, Set<? extends DirectedGraphNode<E>> others) {
		LinkedNode<E> n = checkNode(nD);
		for(DirectedGraphNode<E> node: others){
			checkNode(node);
		}
		for(DirectedGraphNode<E> node: others){
			connectNodeTo0((LinkedNode<E>)node,n);
		}
	}



	@Override
	public void disconnectNode(DirectedGraphNode<E> n1, DirectedGraphNode<E> n2) {
		
		disconnectNode0(checkNode(n1),checkNode(n2));
	}
	@Override
	public void disconnectNodesTo(DirectedGraphNode<E> nD, Set<? extends DirectedGraphNode<E>> others) {
		LinkedNode<E> n = checkNode(nD);
		for(DirectedGraphNode<E> node: others){
			checkNode(node);
		}
		for(DirectedGraphNode<E> node: others){
			disconnectNode0(n, (LinkedNode<E>)node);
		}
		
	}
	
	@Override
	public void disconnectNodesBy(DirectedGraphNode<E> nD, Set<? extends DirectedGraphNode<E>> others) {
		LinkedNode<E> n = checkNode(nD);
		for(DirectedGraphNode<E> node: others){
			checkNode(node);
		}
		for(DirectedGraphNode<E> node: others){
			disconnectNode0((LinkedNode<E>)node, n);
		}
		
	}
	

	@Override
	public Set<DirectedGraphNode<E>> getAllNodes() {
		return new HashSet<DirectedGraphNode<E>>(nodes);
	}

	






	@Override
	public Set<DirectedGraphNode<E>> getConnectTo(DirectedGraphNode<E> node) {
		return new HashSet<DirectedGraphNode<E>>(checkNode(node).getConncetTo());
	}





	
	@Override
	public Set<DirectedGraphNode<E>> getConnectBy(DirectedGraphNode<E> node) {
		return new HashSet<DirectedGraphNode<E>>(checkNode(node).getConncetBy());
	}



	
	

	
	@Override
	public Object clone() {
		//a heavy weight , deep clone method
		
		List<LinkedNode<E>> newList = new ArrayList<LinkedNode<E>>(nodes.size());
		HashMap<LinkedNode<E>,LinkedNode<E>> map = new HashMap<LinkedNode<E>,LinkedNode<E>>(nodes.size());
		LinkedDirectedGraph<E> newGraph = new LinkedDirectedGraph<E>(newList);
		for(LinkedNode<E> n : nodes){
			LinkedNode<E> nn = new LinkedNode<E>(newGraph,n.ele,
					n.getConncetTo().size(),
					n.getConncetBy().size());
			newList.add(nn);
			map.put(n, nn);
		}
		for(LinkedNode<E> n : nodes){
			LinkedNode<E> mapped = map.get(n);
			for(LinkedNode<E> nt : n.getConncetTo()){
				LinkedNode<E> nt_mapped = map.get(nt);
				mapped.addConnectTo(nt_mapped);
				nt_mapped.addConnectBy(mapped);
			}
		}
		
		
		
		
		
		return newGraph;
	}
	
	@Override
	public <T> DirectedGraph<T> mapToGraph(Function<E, T> mapper) {
		List<LinkedNode<T>> newList = new ArrayList<LinkedNode<T>>(nodes.size());
		HashMap<LinkedNode<E>,LinkedNode<T>> map = new HashMap<LinkedNode<E>,LinkedNode<T>>(nodes.size());
		LinkedDirectedGraph<T> newGraph = new LinkedDirectedGraph<T>(newList);
		for(LinkedNode<E> n : nodes){
			LinkedNode<T> nn = new LinkedNode<T>(newGraph,mapper.apply(n.ele),
					n.getConncetTo().size(),
					n.getConncetBy().size());
			newList.add(nn);
			map.put(n, nn);
		}
		for(LinkedNode<E> n : nodes){
			LinkedNode<T> mapped = map.get(n);
			for(LinkedNode<E> nt : n.getConncetTo()){
				LinkedNode<T> nt_mapped = map.get(nt);
				mapped.addConnectTo(nt_mapped);
				nt_mapped.addConnectBy(mapped);
			}
		}
		
		
		
		
		
		return newGraph;
	}
	
	@Override
	public DirectedGraph<E> transpositionOf() {
		List<LinkedNode<E>> newList = new ArrayList<LinkedNode<E>>(nodes.size());
		HashMap<LinkedNode<E>,LinkedNode<E>> map = new HashMap<LinkedNode<E>,LinkedNode<E>>(nodes.size());
		LinkedDirectedGraph<E> newGraph = new LinkedDirectedGraph<E>(newList);
		for(LinkedNode<E> n : nodes){
			LinkedNode<E> nn = new LinkedNode<E>(newGraph,n.ele,
					n.getConncetTo().size(),
					n.getConncetBy().size());
			newList.add(nn);
			map.put(n, nn);
		}
		for(LinkedNode<E> n : nodes){
			LinkedNode<E> mapped = map.get(n);
			for(LinkedNode<E> nt : n.getConncetTo()){
				LinkedNode<E> nt_mapped = map.get(nt);
				mapped.addConnectBy(nt_mapped);
				nt_mapped.addConnectTo(mapped);
			}
		}
		return newGraph;
	}





	@Override
	public void removeNode(DirectedGraphNode<E> noded) {
		LinkedNode<E> node=checkNode(noded);
		for(LinkedNode<E> ct : node.getConncetTo()){
			ct.removeConnectBy(node);
		}
		for(LinkedNode<E> cb : node.getConncetBy()){
			cb.removeConnectTo(node);
		}
		nodes.remove(node);
	}






	@Override
	public Set<DirectedGraphNode<E>> getConnectedNodes(DirectedGraphNode<E> node) {
		return new HashSet<DirectedGraphNode<E>>(checkNode(node).getConncetTo());
	}






	@Override
	public Collection<E> getConncetedElements(DirectedGraphNode<E> noded) {
		LinkedNode<E> node = checkNode(noded);
		Set<LinkedNode<E>> set = node.getConncetTo();
		ArrayList<E> list = new ArrayList<E>(set.size());
		for(LinkedNode<E> n : set){
			list.add(n.ele);
		}
		return list;
	}


	@Override
	public Collection<E> getConnectToElements(DirectedGraphNode<E> node) {
		return getConncetedElements(node);
	}

	@Override
	public Collection<E> getConnectByElements(DirectedGraphNode<E> noded) {
		LinkedNode<E> node = checkNode(noded);
		Set<LinkedNode<E>> set = node.getConncetBy();
		ArrayList<E> list = new ArrayList<E>(set.size());
		for(LinkedNode<E> n : set){
			list.add(n.ele);
		}
		return list;
	}



	






	@Override
	public boolean isConnected(DirectedGraphNode<E> n1d, DirectedGraphNode<E> n2d) {
		LinkedNode<E> n1 = checkNode(n1d);
		LinkedNode<E> n2 = checkNode(n2d);
		return n1.isConnected(n2);
	}






	@Override
	public boolean transferConnection(DirectedGraphNode<E> holderD, DirectedGraphNode<E> connectToD, DirectedGraphNode<E> toGiveD) {
		LinkedNode<E>  holder = checkNode(holderD);
		LinkedNode<E> connectTo =checkNode(connectToD);
		LinkedNode<E> toGive = checkNode(toGiveD);
		if(!holder.isConnected(connectTo)){
			return false;
		}
		disconnectNode0(holder, connectTo);
		connectNodeTo0(toGive, connectTo);
		return true;
	}






	@Override
	public void transfetConnection(DirectedGraphNode<E> holderD, DirectedGraphNode<E> toGiveD) {
		LinkedNode<E>  holder = checkNode(holderD);
		LinkedNode<E> toGive = checkNode(toGiveD);
		for(LinkedNode<E> node : holder.getConncetTo()){
			node.removeConnectBy(holder);
			connectNodeTo0(toGive, node);
		}
		holder.clearConnectTo();
		
	}






	@Override
	public void copyConnection(DirectedGraphNode<E> holderD, DirectedGraphNode<E> toGiveD) {
		LinkedNode<E> holder = checkNode(holderD);
		LinkedNode<E> toGive = checkNode(toGiveD);
		for(LinkedNode<E> node : holder.getConncetTo()){
			connectNodeTo0(toGive, node);
		}
	}




	@Override
	public void connectAll(Set<? extends DirectedGraphNode<E>> nodes) {
		@SuppressWarnings("unchecked")
		LinkedNode<E>[] arr = new LinkedNode[nodes.size()];
		nodes.toArray(arr);
		checkNodes(arr);
		for(int i=0;i<arr.length-1;i++){
			for(int j=i+1;j<arr.length;j++){
				connectNodeTo0(arr[i], arr[j]);
				connectNodeTo0(arr[j], arr[i]);
			}
		}
	}
	

	@Override
	public void disconnectAll(Set<? extends DirectedGraphNode<E>> nodesD) {
		for(DirectedGraphNode<E> n : nodesD){
			checkNode(n);
		}
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<LinkedNode<E>> nodes = (Set) nodesD;
		for(LinkedNode<E> n : nodes){
			n.removeConnectBy(nodes);
			n.removeConnectTo(nodes);
		}
	}






	@Override
	public Set<DirectedGraphNode<E>> getReachableNodes(DirectedGraphNode<E> fromNodeD) {
		LinkedNode<E> fromNode = checkNode(fromNodeD);
		Set<DirectedGraphNode<E>> nset = new HashSet<DirectedGraphNode<E>>();
		deepFirstSearch(nset,fromNode);
		return nset;
		
	}
	
	@Override
	public Set<DirectedGraphNode<E>> getReachableNodesBy(DirectedGraphNode<E> fromNodeD) {
		LinkedNode<E> fromNode = checkNode(fromNodeD);
		Set<DirectedGraphNode<E>> nset = new HashSet<DirectedGraphNode<E>>();
		deepFirstSearchBy(nset,fromNode);
		return nset;
		
	}
	

	private void deepFirstSearch(Set<DirectedGraphNode<E>> set,LinkedNode<E> node){
		set.add(node);
		//use deep search to search the reachable nodes
		for(LinkedNode<E> n : node.getConncetTo()){
			if(set.contains(n)==false){
				deepFirstSearch(set,n);
			}
		}
	}
	
	private void deepFirstSearchBy(Set<DirectedGraphNode<E>> set,LinkedNode<E> node){
		set.add(node);
		//use deep search to search the reachable nodes
		for(LinkedNode<E> n : node.getConncetBy()){
			if(set.contains(n)==false){
				deepFirstSearchBy(set,n);
			}
		}
	}
	

	@Override
	public int size() {
		return nodes.size();
	}

	@Override
	public int getConnectionCount(DirectedGraphNode<E> node) {
		return checkNode(node).getConncetTo().size();
	}

	@Override
	public int getInDegree(DirectedGraphNode<E> node) {
		return checkNode(node).getConncetBy().size();
	}

	@Override
	public int getOutDegree(DirectedGraphNode<E> node) {
		return checkNode(node).getConncetTo().size();
	}

	@Override
	public DirectedGraph<E> subGraph(Set<? extends DirectedGraphNode<E>> nodesD) {
		//create a new Graph first.
		for(DirectedGraphNode<E> n : nodesD){
			checkNode(n);
		}
		@SuppressWarnings({ "unchecked", "rawtypes" })
		Set<LinkedNode<E>> nodes = (Set) nodesD;
		
		//mapping
		int size = nodes.size();
		ArrayList<LinkedNode<E>> list = new ArrayList<LinkedNode<E>>(size);
		LinkedDirectedGraph<E> re = new LinkedDirectedGraph<E>(list);
		HashMap<LinkedNode<E>,LinkedNode<E>> mapper = new HashMap<LinkedNode<E>,LinkedNode<E>>();
		for(LinkedNode<E> n : nodes){
			LinkedNode<E> nn = new LinkedNode<E>(re,n.ele,
					n.getConncetTo().size(),
					n.getConncetBy().size());
			list.add(nn);
			mapper.put(n, nn);
		}
		
		for(LinkedNode<E> n : nodes){
			LinkedNode<E> mapped = mapper.get(n);
			for(LinkedNode<E> nTo : n.getConncetTo()){
				if(nodes.contains(nTo)){
					mapped.addConnectTo(mapper.get(nTo));
					
				}
			}
			for(LinkedNode<E> nBy : n.getConncetBy()){
				if(nodes.contains(nBy)){
					mapped.addConnectBy(mapper.get(nBy));
				}
			}
			
		}
		
		return re;
		
	}

	@Override
	public DirectedGraph<E> subGraphOfTo(DirectedGraphNode<E> nodeD) {
		LinkedNode<E> node = checkNode(nodeD);
		Set<LinkedNode<E>> cTo = node.getConncetTo();
		ArrayList<LinkedNode<E>> list = new ArrayList<LinkedNode<E>>(cTo.size()+1);
		LinkedDirectedGraph<E> gra = new LinkedDirectedGraph<E>(list);
		LinkedNode<E> connect = new LinkedNode<E>(gra,node.ele,cTo.size(),0);
		for(LinkedNode<E> n : cTo){
			LinkedNode<E> nn = new LinkedNode<E>(gra,n.ele,0,1);
			list.add(nn);
			nn.addConnectTo(connect);
		}
		
		return gra;
	}

	@Override
	public DirectedGraph<E> subGraphOfBy(DirectedGraphNode<E> nodeD) {
		LinkedNode<E> node = checkNode(nodeD);
		Set<LinkedNode<E>> cBy = node.getConncetBy();
		ArrayList<LinkedNode<E>> list = new ArrayList<LinkedNode<E>>(cBy.size()+1);
		LinkedDirectedGraph<E> gra = new LinkedDirectedGraph<E>(list);
		LinkedNode<E> connect = new LinkedNode<E>(gra,node.ele,0,cBy.size());
		for(LinkedNode<E> n : cBy){
			LinkedNode<E> nn = new LinkedNode<E>(gra,n.ele,1,0);
			list.add(nn);
			nn.addConnectBy(connect);
		}
		
		return gra;	
	}

	@Override
	public boolean isReachable(DirectedGraphNode<E> fromNode, DirectedGraphNode<E> toNode) {
		LinkedNode<E> node = checkNode(fromNode);
		LinkedNode<E> target = checkNode(toNode);
		
		Set<LinkedNode<E>> reached = new HashSet<LinkedNode<E>>();
		if(node==target)
			return true;
		return deepFirstTestReachable(node, reached, target);
	}
	
	private boolean deepFirstTestReachable(LinkedNode<E> node,Set<LinkedNode<E>> reached,LinkedNode<E> target){
		reached.add(node);
		for(LinkedNode<E> n : node.getConncetTo()){
			if(n==target)
				return true;
			if(deepFirstTestReachable(n, reached, target)){
				return true;
			}
		}
		return false;
	}

	
	
	
	@Override
	public List<Set<DirectedGraphNode<E>>> connectedComponent() {
		//a deep first search is needed in this method.
		DeepFirstSearcher dfs = new DeepFirstSearcher(false);
		dfs.search();
		
		return dfs.comps;
		
		
	}
	
	
	
	@Override
	public DirectedGraph<Set<DirectedGraphNode<E>>> stronglyConnectedComponent() {
		DeepFirstSearcher dfs = new DeepFirstSearcher(true);
		dfs.search();
		
		return dfs.graph;
	}
	@Override
	public Set<DirectedGraphNode<E>> connectedComponentOf(DirectedGraphNode<E> nodeD) {
		LinkedNode<E> node = checkNode(nodeD);
		DeepFirstSearcher dfs = new DeepFirstSearcher(false);
		return dfs.searchFor(node);
	}
	
	
	//a simple implement for deep first searching
	private class DeepFirstSearcher {
		HashMap<DirectedGraphNode<E>,DeepFirstPair> map ;
		
		ArrayList<Set<DirectedGraphNode<E>>> comps ;
		
		private int time = 1;
		
		private LinkedDirectedGraph<Set<DirectedGraphNode<E>>> graph;
		private final boolean graphed;
		
		public DeepFirstSearcher(boolean graphed) {
			this.graphed = graphed;
		}
		private void init(){
			map = new HashMap<DirectedGraphNode<E>,DeepFirstPair>();
			comps = new ArrayList<Set<DirectedGraphNode<E>>>();
			if(graphed){
				graph = new LinkedDirectedGraph<Set<DirectedGraphNode<E>>>();
			}
		}
		
		void search(){
			//part one
			init();
			
			
			for(LinkedNode<E> node : nodes){
				if(!map.containsKey(node)){
					//haven't been reached
					DeepFirstPair dfp = new DeepFirstPair();
					map.put(node, dfp);
					deepFirst_1(node,dfp);
				}
			}
			
			//part two
			
			@SuppressWarnings("unchecked")
			Entry<LinkedNode<E>,DeepFirstPair>[] ens =new Entry[map.size()];
			ens = map.entrySet().toArray(ens);
			Arrays.sort(ens,new Comparator<Entry<LinkedNode<E>,DeepFirstPair>>(){
				@Override
				public int compare(Entry<LinkedNode<E>, LinkedDirectedGraph<E>.DeepFirstSearcher.DeepFirstPair> o1,
						Entry<LinkedNode<E>, LinkedDirectedGraph<E>.DeepFirstSearcher.DeepFirstPair> o2) {
					return o2.getValue().finished-o1.getValue().finished;
				}
			});
			
			map.clear();
//			time = 1;
			for(Entry<LinkedNode<E>,DeepFirstPair> en: ens){
				LinkedNode<E> n = en.getKey();
				if(!map.containsKey(n)){
					//a node from a new strongly connected component
					Set<DirectedGraphNode<E>> comp = new HashSet<DirectedGraphNode<E>>();
					comps.add(comp);
					if(graphed){
						LinkedNode<Set<DirectedGraphNode<E>>> graphNode = graph.createNode();
						graphNode.setElement(comp);
						deepFirst_2(n,comp,graphNode);
					}else{
						deepFirst_2(n,comp,null);
						
					}
				}
				
			}
			
		}
		
		@SuppressWarnings({ "rawtypes", "unchecked" })
		Set<DirectedGraphNode<E>> searchFor(LinkedNode<E> node){
			init();
			
			DeepFirstPair dfp = new DeepFirstPair();
			map.put(node, dfp);
			deepFirst_1(node,dfp);
			Entry<LinkedNode<E>,DeepFirstPair> enMax = null;
			int max = -1;
			Set<DirectedGraphNode<E>> range = new HashSet<DirectedGraphNode<E>>(map.size()); 
			for(Entry<DirectedGraphNode<E>,DeepFirstPair> en : map.entrySet()){
				int cur =  en.getValue().finished;
				if(max< cur){
					max = cur;
					enMax = (Entry)en;
				}
				range.add(en.getKey());
			}
			Set<DirectedGraphNode<E>> comp = new HashSet<DirectedGraphNode<E>>();
			if(enMax==null){
				return comp;
			}
			//a node from a new strongly connected component
			map.clear();
			
			if (graphed) {
				LinkedNode<Set<DirectedGraphNode<E>>> graphNode = graph.createNode();
				graphNode.setElement(comp);
				deepFirst2_InRange(enMax.getKey(), range, comp, graphNode);
			} else {
				deepFirst2_InRange(enMax.getKey(), range, comp, null);

			}
			return comp;
		}
		
		
		
		private void deepFirst_1(LinkedNode<E> n,DeepFirstPair dfp){
//			dfp.detected = time++;
			for(LinkedNode<E> node : n.getConncetTo()){
				if(map.containsKey(node)==false){
					DeepFirstPair dfpN = new DeepFirstPair();
					map.put(node, dfpN);
					deepFirst_1(node,dfpN);
				}
			}
			
			dfp.finished = time++;
		}
		
		private void deepFirst_2(LinkedNode<E> n,
				Set<DirectedGraphNode<E>> comp,LinkedNode<Set<DirectedGraphNode<E>>> graphNode){
//			dfp.detected = time ++;
			comp.add(n);
			for(LinkedNode<E> node : n.getConncetBy()){
				//transposition
				if(map.containsKey(node)==false){
//					DeepFirstPair dfpN = new DeepFirstPair();
					map.put(node, null);
					deepFirst_2(node,comp,graphNode);
				}else if(graphed){
					//back through search
					for(LinkedNode<Set<DirectedGraphNode<E>>> nInGraph : graph.nodes){
						if(nInGraph!= graphNode && nInGraph.ele.contains(node)){
//							print("Found");
							graph.connectNode(nInGraph,graphNode);
						}
					}
				}
			}
//			dfp.finished = time++;
		}
		
		private void deepFirst2_InRange(LinkedNode<E> n ,Set<DirectedGraphNode<E>> range,
				Set<DirectedGraphNode<E>> comp,LinkedNode<Set<DirectedGraphNode<E>>> graphNode){
			comp.add(n);
			for(LinkedNode<E> node : n.getConncetBy()){
				//transposition
				if(range.contains(n)&&map.containsKey(node)==false){
//					DeepFirstPair dfpN = new DeepFirstPair();
					map.put(node, null);
					deepFirst_2(node,comp,graphNode);
				}else if(graphed){
					//back through search
					for(LinkedNode<Set<DirectedGraphNode<E>>> nInGraph : graph.nodes){
						if(nInGraph!= graphNode && nInGraph.ele.contains(node)){
//							print("Found");
							graph.connectNode(nInGraph,graphNode);
						}
					}
				}
			}
			
		}
		
		
		
		private class DeepFirstPair{
			int finished;
			//int detected;
		}
		
	}
	

	
	
	public void printGraph(){
		//create a matrix
		int size = size();
		@SuppressWarnings("unchecked")
		LinkedNode<E>[] nods = new LinkedNode[size];
		nods = nodes.toArray(nods);
		size ++;
		String[][] mat = new String[size][size];
		mat[0][0] = "";
		for(int i=1;i<size;i++){
			mat[i][0] = nods[i-1].ele.toString();
			mat[0][i] = mat[i][0];
		}
		
		
		for(int i=1;i<size;i++){
			for(int j=1;j<size;j++){
				if(i==j){
					mat[i][j] = "";
					continue;
				}
				if(nods[i-1].isConnected(nods[j-1])){
					mat[i][j] = "T";
				}else{
					mat[i][j] = "";
				}
			}
		}
		
		Printer.printMatrix(mat);
		
	}





	






	public static void main(String[] args) {
		LinkedDirectedGraph<String> gra = new  LinkedDirectedGraph<String>();
		@SuppressWarnings("unchecked")
		DirectedGraphNode<String>[] ns = new DirectedGraphNode[8];
		for(int i=0;i<8;i++){
			DirectedGraphNode<String> n = gra.createNode();
			ns[i] = n;
			n.setElement(""+(char)('a'+i));
		}
		gra.connectNode(ns[0], ns[1]);
		gra.connectNode(ns[1], ns[2]);
		gra.connectNode(ns[1], ns[4]);
		gra.connectNode(ns[1], ns[5]);
		gra.connectNode(ns[2], ns[3]);
		gra.connectNode(ns[2], ns[6]);
		gra.connectNode(ns[3], ns[2]);
		gra.connectNode(ns[3], ns[7]);
		gra.connectNode(ns[4], ns[0]);
		gra.connectNode(ns[4], ns[5]);
		gra.connectNode(ns[5], ns[6]);
		gra.connectNode(ns[6], ns[5]);
		gra.connectNode(ns[6], ns[7]);
		gra.connectNode(ns[7], ns[7]);
		
		gra.printGraph();
		
//		List<Set<? extends DirectedGraphNode<String>>> list = gra.connectedComponent();
//		for(Set<? extends DirectedGraphNode<String>> set : list){
//			StringBuilder sb = new StringBuilder();
//			for(DirectedGraphNode<String> node : set){
//				sb.append(node.ele);
//			}
//			Printer.print(sb.toString());
//		}
//		DirectedGraph<Set<? extends DirectedGraphNode<String>>> gr2 = gra.stronglyConnectedComponent();
//		
//		DirectedGraph<String> compGraph = gr2.mapToGraph(
//				new Function<Set<? extends DirectedGraphNode<String>>,String>(){
//
//					@Override
//					public String apply(Set<? extends DirectedGraphNode<String>> t) {
//						StringBuilder sb = new StringBuilder();
//						for(DirectedGraphNode<String> n : t){
//							sb.append(n.ele);
//						}
//						return sb.toString();
//					}
//					
//				});
//		compGraph.printGraph();
//		Set<? extends DirectedGraphNode<String>> set = gra.connectedComponentOf(ns[0]);
//		for(DirectedGraphNode<String> node : set){
//			print(node.ele);
//		}
//		compGraph = gra.subGraph(set);
//		compGraph.printGraph();
		for(DirectedGraphNode<String> node :gra.getReachableNodes(ns[0])){
			print(node.ele);
		}
	}

	

	

	

	

	
	
	
	
	
}
