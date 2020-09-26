package cn.ancono.utilities.structure;

import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.Map.Entry;

import static cn.ancono.utilities.Printer.print;
import static cn.ancono.utilities.Printer.print_;

/**
 * A partial order set is a set that contains a set of partial ordered
 * elements.This set doesn't allows elements A,B that
 * {@code comparator.compareWith(A, B) == true &&
 * comparator.compareWith(B, A) == true},which means there mustn't be two
 * elements which are equal. <p>
 * You should always be sure that for the object put in this set ,either the {@code equals()}
 * and {@code hashCode()} is literally
 * the identity to {@linkplain PartialOrderComparator#isEqual(Object, Object)} , or the {@code equals()}
 * has not been overrode. Or unexpected error would happen.
 *
 * @param <E> the element type
 * @author lyc
 */
public class PartialOrderSet<E> extends PartialOrderCollection<E> {
    // in this class , A connect to B when A partial order B .
    // comparator.compareWith(A, B)= true -> graph.isConnected(A,B)= true
    //
    // comparator.compareWith(upper(A), A) = true
    // comparator.compareWith(A, downer(A)) = true
    //
    // graph.isConnected(upper(A), A) = true
    // graph.isConnected(A, downer(A)) = true

    /**
     * The graph of this set
     */
    private DirectedGraph<E> graph;

    private Map<E, DirectedGraphNode<E>> mapper;

    private Set<DirectedGraphNode<E>> mostUpper;

    private Set<DirectedGraphNode<E>> mostDowner;
    /**
     * A random number to make the performance of partial order comparing in
     * average
     */
    private final boolean direction = System.currentTimeMillis() % 2 == 0;

    public PartialOrderSet(PartialOrderComparator<E> comparator) {
        super(comparator);
        graph = new LinkedDirectedGraph<E>();
        mapper = new HashMap<>();
        mostUpper = new HashSet<>();
        mostDowner = new HashSet<>();
    }

    private PartialOrderSet(PartialOrderComparator<E> comparator,
                            DirectedGraph<E> graph, Map<E, DirectedGraphNode<E>> mapper,
                            Set<DirectedGraphNode<E>> mostUpper,
                            Set<DirectedGraphNode<E>> mostDowner) {
        super(comparator);
        this.graph = graph;
        this.mapper = mapper;
        this.mostUpper = mostUpper;
        this.mostDowner = mostDowner;
    }

    /**
     * Be used for indicating node
     *
     * @param e
     * @return
     */
    private DirectedGraphNode<E> getNode(E e) {
        DirectedGraphNode<E> n = mapper.get(e);
        if (n == null) {
            throw new NullPointerException("Element NOT exist");
        }
        return n;
    }

    @NotNull
    @Override
    public Iterator<E> iterator() {
        return new Itr(graph.iterator());
    }

    class Itr implements Iterator<E> {

        private final Iterator<DirectedGraphNode<E>> it;

        public Itr(Iterator<DirectedGraphNode<E>> it) {
            this.it = it;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public E next() {
            return it.next().getElement();
        }
    }

    @Override
    public int size() {
        return graph.size();
    }


    /**
     * Add the element to this partial ordered set,if a equal element is not
     * already present.
     *
     * @return {@code true} if this set did not already contain a equal element
     */
    @Override
    public boolean add(E e) {
        Set<DirectedGraphNode<E>> directUpper = new HashSet<DirectedGraphNode<E>>();
        Set<DirectedGraphNode<E>> directDowner = new HashSet<DirectedGraphNode<E>>();

        // a set to remember compared nodes and all its upper / downer nodes
        Set<DirectedGraphNode<E>> upper = new HashSet<DirectedGraphNode<E>>();
        Set<DirectedGraphNode<E>> downer = new HashSet<DirectedGraphNode<E>>();

        for (Entry<E, DirectedGraphNode<E>> entry : mapper.entrySet()) {
            DirectedGraphNode<E> node = entry.getValue();
            if (upper.contains(node) || downer.contains(node)) {
                continue;
            }
            E toCompare = entry.getKey();
            if (direction) {
                if (comparator.compareWith(e, toCompare)) {
                    if (comparator.compareWith(toCompare, e)) {
                        // have an equal element
                        return false;
                    }
                    Set<? extends DirectedGraphNode<E>> toAdd = graph.getReachableNodes(node);
                    directDowner.removeAll(toAdd);
                    directDowner.add(node);
                    downer.addAll(toAdd);

                } else if (comparator.compareWith(toCompare, e)) {
                    Set<? extends DirectedGraphNode<E>> toAdd = graph.getReachableNodesBy(node);
                    directUpper.removeAll(toAdd);
                    directUpper.add(node);
                    upper.addAll(toAdd);
                }
            } else {
                if (comparator.compareWith(toCompare, e)) {
                    if (comparator.compareWith(e, toCompare)) {
                        // have an equal element
                        return false;
                    }

                    Set<? extends DirectedGraphNode<E>> toAdd = graph.getReachableNodesBy(node);
                    directUpper.removeAll(toAdd);
                    directUpper.add(node);
                    upper.addAll(toAdd);
                } else if (comparator.compareWith(e, toCompare)) {

                    Set<? extends DirectedGraphNode<E>> toAdd = graph.getReachableNodes(node);
                    directDowner.removeAll(toAdd);
                    directDowner.add(node);
                    downer.addAll(toAdd);

                }
            }
        }
//		print("For element::"+e);
//		printnb("D Upper:");
//		for(DirectedGraphNode<E> no : directUpper){
//			printnb(no.getElement()+" ");
//		}
//		print();
//		printnb("D Downer:");
//		for(DirectedGraphNode<E> no : directDowner){
//			printnb(no.getElement()+" ");
//		}
//		print();

        DirectedGraphNode<E> node = graph.createNode();
        node.setElement(e);
        mapper.put(e, node);
        graph.connectNodes(node, directDowner);
        graph.connectNodesBy(node, directUpper);
        for (DirectedGraphNode<E> du : directUpper) {
            Set<? extends DirectedGraphNode<E>> duConnect = graph.getConnectTo(du);
            duConnect.retainAll(directDowner);
            graph.disconnectNodes(du, duConnect);

        }


        if (direction) {
            if (mostUpper.removeAll(directDowner)) {
                mostUpper.add(node);
            } else if (mostDowner.removeAll(directUpper)) {
                mostDowner.add(node);
            }
        } else {
            if (mostDowner.removeAll(directUpper)) {
                mostDowner.add(node);
            } else if (mostUpper.removeAll(directDowner)) {
                mostUpper.add(node);
            }
        }
        if (directDowner.isEmpty()) {
            mostDowner.add(node);
        }
        if (directUpper.isEmpty()) {
            mostUpper.add(node);
        }

//		for(DirectedGraphNode<E> no : mostUpper){
//			printnb(no.getElement()+" ");
//		}
//		print();
//		for(DirectedGraphNode<E> no : mostDowner){
//			printnb(no.getElement()+" ");
//		}
//		print();
//		print_();
        return true;
    }

    @Override
    public boolean isEmpty() {
        return mapper.isEmpty();
    }

    @Override
    public boolean remove(Object o) {
        DirectedGraphNode<E> node = mapper.remove(o);
        if (node == null) {
            return false;
        }
        //remove the element
        remove0(node);
        return true;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean removed = false;
        for (Object o : c) {
            removed = removed || remove(o);
        }
        return removed;
    }

    @Override
    public E removeEqualElement(E ele) {
        DirectedGraphNode<E> node = mapper.get(ele);
        if (node == null) {
            node = findEqual(ele);
        }
        if (node == null) {
            return null;
        }
        remove0(node);
        return node.getElement();
    }


    private void remove0(DirectedGraphNode<E> node) {
        //a
        Set<? extends DirectedGraphNode<E>> uppers = graph.getConnectBy(node);
        graph.disconnectNodesBy(node, uppers);
        for (DirectedGraphNode<E> n : uppers) {
            graph.copyConnection(node, n);
        }
        //remove recorded node
        mostDowner.remove(node);
        mostUpper.remove(node);
    }


    /**
     * Returns whether this set contains an element that is equal to {@code o} according to
     * the comparator.
     *
     * @param ele an object
     * @return true if there is any object in {@code this} that {@code comparator.isEqual(o,obj)==true}.
     */
    @Override
    public E containsEqualElement(E ele) {
        if (mapper.containsKey(ele)) {
            return ele;
        }
        DirectedGraphNode<E> node = findEqual(ele);
        return node == null ? null : node.getElement();

    }

    private DirectedGraphNode<E> findEqual(E ele) {
        if (direction) {
            for (DirectedGraphNode<E> node : mostDowner) {
                // more downer than the most downer elements
                if (comparator.compareWith(node.getElement(), ele)) {
                    return node;
                }
            }
            for (DirectedGraphNode<E> node : mostUpper) {
                if (comparator.compareWith(ele, node.getElement())) {
                    return node;
                }
            }
        } else {
            for (DirectedGraphNode<E> node : mostUpper) {
                if (comparator.compareWith(ele, node.getElement())) {
                    return node;
                }
            }
            for (DirectedGraphNode<E> node : mostDowner) {
                if (comparator.compareWith(node.getElement(), ele)) {
                    return node;
                }
            }
        }

        for (DirectedGraphNode<E> node : graph) {
            if (comparator.isEqual(ele, node.getElement())) {
                return node;
            }
        }
        return null;
    }

    @Override
    public boolean contains(Object o) {
        return mapper.containsKey(o);
    }

    @Override
    public void clear() {
        graph = new LinkedDirectedGraph<>();
        mapper.clear();
        mostDowner.clear();
        mostUpper.clear();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        List<DirectedGraphNode<E>> toRemove = new ArrayList<DirectedGraphNode<E>>();
        for (DirectedGraphNode<E> node : graph) {
            if (!c.contains(node)) {
                toRemove.add(node);
            }
        }
        for (DirectedGraphNode<E> n : toRemove) {
            remove0(n);
        }
        return !toRemove.isEmpty();
    }

    @Override
    public PartialOrderCollection<E> getUpperElements(E e) {
        DirectedGraphNode<E> node = getNode(e);
        Set<? extends DirectedGraphNode<E>> uppers = graph.getReachableNodesBy(node);


        DirectedGraph<E> gp = graph.subGraph(uppers);
        Map<E, DirectedGraphNode<E>> newMapper = new HashMap<E, DirectedGraphNode<E>>();

        uppers = gp.getAllNodes();
        for (DirectedGraphNode<E> n : uppers) {
            newMapper.put(n.getElement(), n);
        }

        HashSet<DirectedGraphNode<E>> choosedMostUpper = new HashSet<DirectedGraphNode<E>>();
//		print(uppers.toArray());
        for (DirectedGraphNode<E> n : mostUpper) {
            DirectedGraphNode<E> nMapped = newMapper.get(n.getElement());
//			print(nMapped);
            if (nMapped != null) {
                choosedMostUpper.add(nMapped);
            }
        }

        Set<DirectedGraphNode<E>> newMostDowner = new HashSet<DirectedGraphNode<E>>();
        newMostDowner.add(newMapper.get(e));

        return new cn.ancono.utilities.structure.PartialOrderSet<E>(comparator, gp, newMapper, choosedMostUpper, newMostDowner);
    }

    @Override
    public PartialOrderCollection<E> getDownerElements(E e) {
        DirectedGraphNode<E> node = getNode(e);
        Set<? extends DirectedGraphNode<E>> uppers = graph.getReachableNodes(node);


        DirectedGraph<E> gp = graph.subGraph(uppers);
        Map<E, DirectedGraphNode<E>> newMapper = new HashMap<E, DirectedGraphNode<E>>();

        uppers = gp.getAllNodes();
        for (DirectedGraphNode<E> n : uppers) {
            newMapper.put(n.getElement(), n);
        }

        HashSet<DirectedGraphNode<E>> choosedMostDowner = new HashSet<DirectedGraphNode<E>>();
        for (DirectedGraphNode<E> n : mostDowner) {
            DirectedGraphNode<E> nMapped = newMapper.get(n.getElement());
            if (uppers.contains(nMapped)) {
                choosedMostDowner.add(nMapped);
            }
        }

        Set<DirectedGraphNode<E>> newMostUpper = new HashSet<DirectedGraphNode<E>>();
        newMostUpper.add(newMapper.get(e));

        return new cn.ancono.utilities.structure.PartialOrderSet<E>(comparator, gp, newMapper, choosedMostDowner, newMostUpper);
    }

    @Override
    public PartialOrderCollection<E> getDirectUpperElements(E e) {
        DirectedGraphNode<E> node = getNode(e);
        Set<? extends DirectedGraphNode<E>> dUppers = graph.getConnectTo(node);
        //no relationship should these nodes have .
        DirectedGraph<E> gp = new LinkedDirectedGraph<E>();
        Map<E, DirectedGraphNode<E>> newMapper = new HashMap<E, DirectedGraphNode<E>>();
        Set<DirectedGraphNode<E>> them = new HashSet<DirectedGraphNode<E>>(dUppers.size());
        for (DirectedGraphNode<E> n : dUppers) {
            DirectedGraphNode<E> nn = gp.createNode();
            //                         ^
            //                         | create node here
            E ele = n.getElement();
            nn.setElement(ele);
            newMapper.put(ele, nn);
        }


        HashSet<DirectedGraphNode<E>> downer = new HashSet<DirectedGraphNode<E>>(them);
        // them == downer == upper
        return new cn.ancono.utilities.structure.PartialOrderSet<E>(comparator, gp, newMapper, them, downer);
    }

    @Override
    public PartialOrderCollection<E> getDirectDownerElements(E e) {
        DirectedGraphNode<E> node = getNode(e);
        Set<? extends DirectedGraphNode<E>> dDowners = graph.getConnectBy(node);
        //no relationship should these nodes have .
        DirectedGraph<E> gp = new LinkedDirectedGraph<E>();
        Map<E, DirectedGraphNode<E>> newMapper = new HashMap<E, DirectedGraphNode<E>>();
        Set<DirectedGraphNode<E>> them = new HashSet<DirectedGraphNode<E>>(dDowners.size());
        for (DirectedGraphNode<E> n : dDowners) {
            DirectedGraphNode<E> nn = gp.createNode();
            //                         ^
            //                         | create node here
            E ele = n.getElement();
            nn.setElement(ele);
            newMapper.put(ele, nn);
        }


        HashSet<DirectedGraphNode<E>> upper = new HashSet<DirectedGraphNode<E>>(them);
        // them == downer == upper
        return new cn.ancono.utilities.structure.PartialOrderSet<E>(comparator, gp, newMapper, upper, them);
    }


    @Override
    public PartialOrderCollection<E> getUntouchedElements(E e) {
        DirectedGraphNode<E> node = getNode(e);
        Set<DirectedGraphNode<E>> remains = graph.getAllNodes();
        remains.removeAll(graph.getReachableNodes(node));
        remains.removeAll(graph.getReachableNodesBy(node));

        //create a new mapper
        DirectedGraph<E> gp = graph.subGraph(remains);

        Map<E, DirectedGraphNode<E>> newMapper = new HashMap<E, DirectedGraphNode<E>>();
        for (DirectedGraphNode<E> nn : gp) {
            newMapper.put(nn.getElement(), nn);
        }
        Set<DirectedGraphNode<E>> newUpper = new HashSet<DirectedGraphNode<E>>();
        Set<DirectedGraphNode<E>> newDowner = new HashSet<DirectedGraphNode<E>>();
        for (DirectedGraphNode<E> n : mostUpper) {
            DirectedGraphNode<E> nMapped = newMapper.get(n.getElement());
            if (nMapped != null) {
                newUpper.add(nMapped);
            }
        }
        for (DirectedGraphNode<E> n : mostDowner) {
            DirectedGraphNode<E> nMapped = newMapper.get(n.getElement());
            if (nMapped != null) {
                newDowner.add(nMapped);
            }
        }
        return new cn.ancono.utilities.structure.PartialOrderSet<E>(comparator, gp, newMapper, newUpper, newDowner);
    }

    @Override
    public PartialOrderCollection<E> getTopElements() {
        Set<DirectedGraphNode<E>> dUppers = mostUpper;
        //no relationship should these nodes have .
        DirectedGraph<E> gp = new LinkedDirectedGraph<E>();
        Map<E, DirectedGraphNode<E>> newMapper = new HashMap<E, DirectedGraphNode<E>>();
        Set<DirectedGraphNode<E>> them = new HashSet<DirectedGraphNode<E>>(dUppers.size());
        for (DirectedGraphNode<E> n : dUppers) {
            DirectedGraphNode<E> nn = gp.createNode();
            //                         ^
            //                         | create node here
            E ele = n.getElement();
            nn.setElement(ele);
            newMapper.put(ele, nn);
        }
        HashSet<DirectedGraphNode<E>> downer = new HashSet<DirectedGraphNode<E>>(them);
        // them == downer == upper
        return new cn.ancono.utilities.structure.PartialOrderSet<E>(comparator, gp, newMapper, them, downer);
    }

    @Override
    public PartialOrderCollection<E> getBottomElements() {
        Set<DirectedGraphNode<E>> dDowners = mostDowner;
        //no relationship should these nodes have .
        DirectedGraph<E> gp = new LinkedDirectedGraph<E>();
        Map<E, DirectedGraphNode<E>> newMapper = new HashMap<E, DirectedGraphNode<E>>();
        Set<DirectedGraphNode<E>> them = new HashSet<DirectedGraphNode<E>>(dDowners.size());
        for (DirectedGraphNode<E> n : dDowners) {
            DirectedGraphNode<E> nn = gp.createNode();
            //                         ^
            //                         | create node here
            E ele = n.getElement();
            nn.setElement(ele);
            newMapper.put(ele, nn);
        }
        HashSet<DirectedGraphNode<E>> downer = new HashSet<DirectedGraphNode<E>>(them);
        // them == downer == upper
        return new cn.ancono.utilities.structure.PartialOrderSet<E>(comparator, gp, newMapper, them, downer);
    }

    /**
     * Print this set using {@linkplain cn.ancono.utilities.Printer}.
     * <p>
     * This method is normally for debugging.
     */
    public void printSet(boolean upperFirst) {
        // use deep first iterating and
        if (upperFirst) {
            for (DirectedGraphNode<E> upperest : mostUpper) {
                printSet0(upperest, 0, true);
            }
        } else {
            for (DirectedGraphNode<E> downerest : mostUpper) {
                printSet0(downerest, 0, false);
            }
        }

    }

    private void printSet0(DirectedGraphNode<E> node, int level, boolean upperFirst) {
        print_(level * 3, ' ', true);
        print("|-->" + node.getElement().toString());
        level++;
        if (upperFirst) {
            for (DirectedGraphNode<E> nodeDowner : graph.getConnectTo(node)) {
                printSet0(nodeDowner, level, true);
            }

        } else {
            for (DirectedGraphNode<E> nodeUpper : graph.getConnectTo(node)) {
                printSet0(nodeUpper, level, false);
            }
        }
    }

    private static class PairForDebug {
        final int x, y;

        PairForDebug(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "(" + x + "," + y + ")";
        }

    }

    public static void main(String[] args) {
        cn.ancono.utilities.structure.PartialOrderSet<PairForDebug> set = new cn.ancono.utilities.structure.PartialOrderSet<>((A, B) -> A.x <= B.x && A.y >= B.y);
        Random rd = new Random();
        PairForDebug[] arr = new PairForDebug[10];
        for (int i = 0; i < 10; i++) {
            int left, right;
            left = rd.nextInt(20) - 10;
            right = rd.nextInt(20) - 10;
            if (left > right) {
                int t = right;
                right = left;
                left = t;
            }
            PairForDebug p = new PairForDebug(left, right);
            arr[i] = p;
            print(p);
            if (!set.add(p)) {
                print("Break");
                break;
            }


        }
        set.printSet(true);
        //test code 1
//		DirectedGraph<String> transGraph = set.graph.mapToGraph(new Function<PairForDebug, String>() {
//
//			@Override
//			public String apply(PairForDebug t) {
//				return new StringBuilder().append('(').append(t.x).append(',').append(t.y).append(')').toString();
//			}
//			
//		});
//		transGraph.printGraph();

        //test code 2
        print_();
        cn.ancono.utilities.structure.PartialOrderSet<PairForDebug> upper = (cn.ancono.utilities.structure.PartialOrderSet<PairForDebug>) set.getDirectUpperElements(arr[5]);
        upper.printSet(true);
        upper.graph.printGraph();
    }


}
