package cn.ancono.utilities.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

/**
 * A circular linked list.
 *
 * @author lyc
 */
public class CircularLinkedList<E> implements Iterable<E> {
    private Node root;

    int size = 0;


    public class Node extends cn.ancono.utilities.structure.Node<E> {
        private Node(E e) {
            super(e);
        }

        private boolean removed = false;
        private Node prv;
        private Node nex;

        /**
         * Gets the previous Node
         *
         * @return
         */
        public Node getPrev() {
            return prv;
        }

        /**
         * Gets the next Node
         *
         * @return
         */
        public Node getNext() {
            return nex;
        }

        /**
         * Adds the element to the next of this node, which means after this
         * operation, the result of calling getNext() will be a node containing e.
         *
         * @param e
         */
        public void addNext(E e) {
            Node n = new Node(e);
            insertNode(this, nex, n);
        }

        /**
         * Adds the element to the previous of this node, which means after this
         * operation, the result of calling getPrev() will be a node containing e.
         *
         * @param e
         */
        public void addPrev(E e) {
            Node n = new Node(e);
            insertNode(prv, this, n);
        }

        public void remove() {
            removed = true;
            removeNode(this);
        }

    }

    public void setRoot(Node r) {
        Objects.requireNonNull(r);
        if (r.removed) {
            throw new IllegalArgumentException("The node was removed");
        }
        root = r;
    }

    /**
     * Gets the root node of this list.
     *
     * @return a node
     */
    public Node getRoot() {
        return root;
    }
//	private void breakLink(Node prv,Node nex){
//		prv.nex = null;
//		nex.prv = null;
//	}

    private void insertNode(Node prv, Node nex, Node mid) {
        prv.nex = mid;
        nex.prv = mid;
        mid.prv = prv;
        mid.nex = nex;
    }

    /**
     * Gets the size of this CircularLinkedList
     *
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * Adds(Inserts) a node before the last node.
     *
     * @param e
     */
    public void add(E e) {
        e = Objects.requireNonNull(e, "Cannot add null into CircularLinkedList");
        Node n = new Node(e);
        if (root == null) {
            root = n;
            n.nex = n;
            n.prv = n;
        } else {
            Node last = root.prv;
            last.nex = n;
            n.prv = last;
            n.nex = root;
            root.prv = n;
        }
        size++;
    }

    /**
     * Gets the node
     *
     * @param e
     * @return
     */
    private Node getNode(E e) {
        if (root == null)
            return null;
        Node n = root;
        do {
            if (n.ele.equals(e)) {
                return n;
            }
            n = n.nex;
        } while (n != root);
        return null;
    }

    /**
     * @param e
     * @return
     */
    public boolean contains(E e) {
        return getNode(e) != null;
    }

    /**
     * Remove the given element from this list.
     *
     * @param e
     * @return true if the list contains this element and it has been removed, otherwise false.
     */
    public boolean remove(E e) {
        Node n = getNode(e);
        return removeNode(n);
    }


    private boolean removeNode(Node n) {
        if (n == null)
            return false;
        n.removed = true;
        if (size == 1) {
            root = null;
        } else {
            if (n == root) {
                root = n.nex;
            }
            n.nex.prv = n.prv;
            n.prv.nex = n.nex;
        }
        size--;
        return true;
    }

    /**
     * Return the previous element of the given element.If e if the first element,then the last element will
     * be returned.Otherwise,this method will return the element which was the last one added before the given element was added
     * and hasn't been removed.If the list doesn't contain the given element, null will be returned.
     *
     * @param e an element
     * @return the predecessor element of e.
     */
    public E predecessor(E e) {
        Node n = getNode(e);
        if (n == null) {
            return null;
        } else {
            return n.prv.ele;
        }
    }

    /**
     * Return an element whose predecessor is e.
     *
     * @param e
     * @return the successor element of e.
     * @see #predecessor(Object)
     */
    public E successor(E e) {
        Node n = getNode(e);
        if (n == null) {
            return null;
        } else {
            return n.nex.ele;
        }
    }

    /**
     * Return an iterator of this list.The element returned will be in the order of the order that they were added.
     * The first element will be the first element added without removing.
     *
     * @return an iterator.
     */
    public Iterator<E> iterator() {
        return new CLLIterator(root);
    }

    private class CLLIterator implements Iterator<E> {
        private Node start;
        private Node cur;

        private CLLIterator(Node n) {
            start = n;
            cur = n;
        }


        @Override
        public boolean hasNext() {
            return cur != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E e = cur.ele;
            cur = cur.nex == start ? null : cur.nex;
            return e;
        }

    }

    public void clear() {
        root = null;
        size = 0;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append('[');
        for (E e : this) {
            sb.append(e.toString()).append(',');
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(']');
        return sb.toString();
    }

    public static void main(String[] args) {
        cn.ancono.utilities.structure.CircularLinkedList<Integer> list = new cn.ancono.utilities.structure.CircularLinkedList<Integer>();
        for (int i = 0; i < 100; i++) {
            list.add(i);
        }
        for (Iterator<Integer> it = list.iterator(); it.hasNext(); ) {
            System.out.println(it.next());
        }
    }
}
