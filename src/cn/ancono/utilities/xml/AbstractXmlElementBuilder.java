package cn.ancono.utilities.xml;

import cn.ancono.utilities.AbstractBuilder;

import java.util.Map;
import java.util.Map.Entry;

/**
 * @param <T>
 * @param <S>
 * @param <R>
 */
public abstract class AbstractXmlElementBuilder<T, S extends AbstractXmlElementBuilder<T, S, R>, R> extends AbstractBuilder<T, S, R, String> {


    /**
     * Set the name of the element
     *
     * @return
     */
    public abstract S setName(String name);

    /**
     * Add the key-value as attribute to this element.
     *
     * @param key
     * @param value
     */
    public abstract S addAttribute(String key, String value);

    /**
     * Add a sub element with name and text.Such as
     * {@code <name>text</name>}
     *
     * @param name
     * @param text
     * @return
     */
    public abstract S addElement(String name, String text);

    /**
     * Adds a comment to the xml file.<br>
     * {@literal <!-- comment -->}
     *
     * @param comment the comment to add
     * @return this
     */
    public abstract S addComment(String comment);

    @SuppressWarnings("unchecked")
    public S addAttrMap(Map<String, String> map) {
        for (Entry<String, String> en : map.entrySet()) {
            addAttribute(en.getKey(), en.getValue());
        }
        return (S) this;
    }

    /**
     * Returns a sub element node from this element,the element's name is given.
     *
     * @param name
     * @return
     */
    protected abstract AbstractXmlElementBuilder<S, ?, R> subNode(String name);

    /**
     * Returns a sub element node from this element,
     * the element's name must be given later.
     *
     * @return
     */
    protected abstract AbstractXmlElementBuilder<S, ?, R> subNode();

    @Override
    public AbstractBuilder<S, ?, R, String> nextLevel() {
        return subNode();
    }


}
