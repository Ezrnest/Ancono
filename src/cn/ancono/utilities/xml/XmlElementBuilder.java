package cn.ancono.utilities.xml;

import java.util.Objects;

public class XmlElementBuilder<T extends XmlElementBuilderString<?, T>> extends XmlElementBuilderString<T, XmlElementBuilder<T>> {

    /**
     * @param high
     * @param prefix
     */
    XmlElementBuilder(T high, int level) {
        super(high, level);
    }

    XmlElementBuilder(T high, int level, boolean doPrefix, boolean doSeparator) {
        super(high, level, doPrefix, doSeparator);
    }

    @Override
    public XmlElementBuilder<T> setName(String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    @Override
    public XmlElementBuilder<T> addAttribute(String key, String value) {
        addAttribute0(key, value);
        return this;
    }

    @Override
    public XmlElementBuilder<T> addElement(String name, String text) {
        addElement0(name, text);
        return this;
    }

    @Override
    public XmlElementBuilder<XmlElementBuilder<T>> subNode(String name) {
        XmlElementBuilder<XmlElementBuilder<T>> low = new XmlElementBuilder<XmlElementBuilder<T>>(this, level + 1, doPrefix, doSeparator);
        return low.setName(name);
    }

    @Override
    public XmlElementBuilder<XmlElementBuilder<T>> subNode() {
        return new XmlElementBuilder<XmlElementBuilder<T>>(this, level + 1, doPrefix, doSeparator);
    }

    /**
     * Append some text to this xml,this method is
     * not recommended, use other methods instead.
     */
    @Override
    protected XmlElementBuilder<T> appendImpl(String sth) {
        context.append(sth);
        return this;
    }

    @Override
    protected T buildImpl() {
        high.append0(generate());
        return high;
    }

}
