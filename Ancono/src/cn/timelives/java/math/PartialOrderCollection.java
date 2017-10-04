package cn.timelives.java.math;

import java.util.AbstractCollection;
/**
 * PartialOrderCollection is a collection of partial ordered elements.The collection provides more methods than add or remove,such as 
 * {@link #getParent(Object)} or {@link #getChild(Object)}.This collection will give out proper returning values for these methods.
 * <p>
 * <h1>Upper and Downer</h1>
 * Upper elements (parent elements) and downer elements (child elements) are basic expression of the relationship of the elements 
 * in the partial order collection. The upper elements of element A are the elements that {@code e ¨R A }, and the downer elements 
 * are {@code A  ¨R e }.The union of the collection of upper elements and downer elements and the collection of <pre>{e| (! e ¨R A) && (! A ¨R e)}</pre>
 * should be the whole collection.The node A itself IS included in both upper elements and downer elements.
 * <P>
 * The direct upper elements of A are the elements that <pre>{e | e ¨R A && ([not exist](e' ¡Ê collection)(e ¨R  e' ¨R  A) ) }</pre>
 * Which means there aren't any elements in the collection to be between the direct upper element and the element A.The direct downer elements 
 * is similar , <pre>{e | A ¨R e && ([not exist](e' ¡Ê collection)(A ¨R  e' ¨R  e) ) }</pre>
 * The direct upper elements and downer elements are usually needed in some mathematical calculation or graph.See {@linkplain cn.timelives.java.utilities.structure.DirectedGraph} for some 
 * implementation details.The node A itself is NOT included in both upper elements and downer elements.
 * @author lyc
 *
 * @param <E>
 */
public abstract class PartialOrderCollection<E> extends AbstractCollection<E>{
	protected final PartialOrderComparator<E> comparator;
	
	public PartialOrderCollection(PartialOrderComparator<E> comparator){
		this.comparator = comparator;
	}
	
	/**
	 * This method will search this collection for an element {@code equal} that {@code comparator.isEqual(equal,ele)==true}.
	 * If there is such an element,then the element will be returned, otherwise, {@code null} will be returned. If an element 
	 * that is added to this collection formally is used as a parameter,then the same object will be returned({@code ele == containsEqualElement(ele)}).
	 * @return an equal element in this collection , or {@code null} if there isn't one.
	 *  
	 */
	public abstract E containsEqualElement(E ele);
	
	@Override
	public abstract boolean contains(Object o);
	
	
	/**
	 * Remove the element {@code o} in this collection, this method will only remove the element if {@code contains(o)==true}, 
	 * notice that this method will not remove the element that {@code e.equals(o)==false} while {@code comparator.isEqual(e,o) == true}.
	 */
	@Override
	public abstract boolean remove(Object o) ;
	
	/**
	 * Remove the element e that {@code comparator.isEqual(e,ele)==true}.These method will only remove one such element,the order 
	 * of removing may not be specific according to the partial order collection.
	 * @param ele an element to remove
	 * @return the removed element , or {@code null} if there is not such an element.
	 */
	public abstract E removeEqualElement(E ele);
	
	
	
	
	/**
	 * Returns the upper elements of {@code e}.The definition of upper elements is in the introduction of {@linkplain PartialOrderCollection}.
	 * @param e an element in the collection
	 * @return a collection of the upper elements of {@code e}
	 */
	public abstract PartialOrderCollection<E> getUpperElements(E e);
	/**
	 * Returns the downer elements of {@code e}.The definition of downer elements is in the introduction of {@linkplain PartialOrderCollection}.
	 * @param e an element in the collection
	 * @return a collection of the downer elements of {@code e}
	 */
	public abstract PartialOrderCollection<E> getDownerElements(E e);
	/**
	 * Returns the direct upper elements of {@code e}.The definition of direct upper elements is in the introduction of {@linkplain PartialOrderCollection}.
	 * @param e an element in the collection
	 * @return a collection of the direct upper elements of {@code e}
	 */
	public abstract PartialOrderCollection<E> getDirectUpperElements(E e);
	/**
	 * Returns the direct downer elements of {@code e}.The definition of direct downer elements is in the introduction of {@linkplain PartialOrderCollection}.
	 * @param e an element in the collection
	 * @return a collection of the direct downer elements of {@code e}
	 */
	public abstract PartialOrderCollection<E> getDirectDownerElements(E e);
	
	/**
	 * Return the elements in this collection that {e| (! e ¨R A) && (! A ¨R e)}, the union of {@code getUntouchedElements(e)}, {@code getUpperElements(e)} and 
	 * {@code getDownerElements(e)} is the collection {@code this}
	 * @param e an element in the collection
	 * @return a collection of the untouched elements.
	 */
	public abstract PartialOrderCollection<E> getUntouchedElements(E e);
	/**
	 * Return the top elements in this collection that <pre>{e |[not exist](e' ¡Ê collection)(e'¨R e)}</pre>
	 * There are usually more than one elements in the top elements collection.
	 * @return a collection of top elements
	 */
	public abstract PartialOrderCollection<E> getTopElements();
	/**
	 * Return the bottom elements in this collection that <pre>{e |[not exist](e' ¡Ê collection)(e¨R e')}</pre>
	 * There are usually more than one elements in the bottom elements collection.
	 * @return a collection of bottom elements
	 */
	public abstract PartialOrderCollection<E> getBottomElements();
	
	
	
}
