/**
 *
 */
package cn.ancono.utilities.structure;

/**
 * @author liyicheng
 *
 */
public abstract class Node<T> {
    protected T ele;

    public Node(T t) {
        this.ele = t;
    }

    public Node() {
    }

    /**
     * Get the element in this graph node.Null may be returned if there is
     * no element stored in this node.
     * @return the element in this node
     */
    public T getElement() {
        return ele;
    }

    /**
     * Set the node with the given element.This method will change the element in this
     * node,null value is acceptable.The operation of {@code selElement(null)} is equal to
     * {@link #removeElement()}.
     * @param e the element to set
     * @return the formal element stored in the node , null will be returned if there is no such element.
     */
    public T setElement(T e) {
        T t = ele;
        ele = e;
        return t;
    }

    /**
     * Set the node's element to null.The method is equal to {@code selElement(null)}.
     * @return the formal element stored in the node , null will be returned if there is no such element.
     */
    public T removeElement() {
        T t = ele;
        ele = null;
        return t;
    }
}
