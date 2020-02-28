package cn.ancono.utilities.xml;

import cn.ancono.utilities.xml.HtmlUtilities.InputTypes;
import cn.ancono.utilities.xml.HtmlUtilities.RemainAttributes;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static cn.ancono.utilities.xml.HtmlUtilities.CommonTags.*;
import static cn.ancono.utilities.xml.HtmlUtilities.DIV;
import static cn.ancono.utilities.xml.HtmlUtilities.SingleTags.BR;
import static cn.ancono.utilities.xml.XmlUtilities.quote;

/**
 * The main builder for Html.
 * <h2>Method Naming</h2>
 * In this class, the number of method is very large, so we make some special
 * method naming. The method that starts with "add"(except
 * {@link #addAttribute(String, String)} and {@link #addElement(String, String)})
 * will always return a lower-level builder and the method with "app" will return
 * {@code this}.
 *
 * @param <T>
 */
public class HtmlBuilder<T extends AbstractHtmlBuilder<?, T>> extends AbstractHtmlBuilder<T, HtmlBuilder<T>> {

    HtmlBuilder(T high, int level, String name) {
        super(high, level);
        this.name = name;
    }

    HtmlBuilder(T high, int level, String name, boolean doPrefix, boolean doSeparator) {
        super(high, level);
        this.name = name;
    }

    @Override
    protected HtmlBuilder<T> appendImpl(String sth) {
        context.append(sth);
        return this;
    }


    @Override
    public HtmlBuilder<T> addAttribute(String key, String value) {
        checkState();
        addAttribute0(key, value);
        return this;
    }

    @Override
    public HtmlBuilder<T> addElement(String name, String text) {
        checkState();
        addElement0(name, text);
        return this;
    }

    @Override
    public HtmlBuilder<HtmlBuilder<T>> subNode(String name) {
        checkState();
        return new HtmlBuilder<>(this, level + 1, name, doPrefix, doSeparator);
    }

    /**
     * This method will return a sub node which name is <text>div</text>.
     */
    @Override
    protected HtmlBuilder<HtmlBuilder<T>> subNode() {
        checkState();
        return new HtmlBuilder<>(this, level + 1, DIV, doPrefix, doSeparator);
    }

    @Override
    protected T buildImpl() {
        high.append0(generate());
        return high;
    }

    /**
     * Insert &lt;br /&gt; to the document.The operation will change line as well.
     *
     * @return this
     */
    public HtmlBuilder<T> changeLine() {
        checkState();
        appendPrefix();
        context.append(BR.text());
        appendSep();
        return this;
    }

    /**
     * Insert multiply &lt;br /&gt; to the document.The operation will change line as well.
     *
     * @return this
     */
    public HtmlBuilder<T> changeLine(int n) {
        checkState();
        appendSep();
        appendPrefix();
        for (int i = 0; i < n; i++) {
            context.append(BR);
            appendSep();
        }
        return this;
    }

    /**
     * Append some text to the document,this method will add tab prefix at the very beginning of the text and
     * line separator at the end(for document format only).
     * <br />There shouldn't be line separators in the text.
     *
     * @param text
     * @return
     */
    public HtmlBuilder<T> text(String text) {
        checkState();
        appendPrefix();
        context.append(quote(text));
        appendSep();
        return this;
    }

    /**
     * Append some text to the document,this method will add tab prefix at the very beginning of the text
     * (for document format only).This method will not add line separator.This method is usually used for
     * add input text.
     * <br />There shouldn't be line separators in the text.
     *
     * @param text
     * @return this
     */
    public HtmlBuilder<T> textNb(String text) {
        checkState();
        appendPrefix();
        context.append(quote(text));
        return this;
    }

    /**
     * Add paragraph.
     *
     * @return a lower builder for the paragraph.
     */
    public HtmlBuilder<HtmlBuilder<T>> addParagraph() {
        return subNode(P.text());
    }

    /**
     * Add paragraph, this method will convert all the {@link System#lineSeparator()} to
     * &lt;br /&gt;
     *
     * @param text the paragraph's context.
     * @return this
     */
    public HtmlBuilder<T> appParagraph(String text) {
        addElement(P.text(), text);
        return this;
    }

    /**
     * Adds a link.
     *
     * @param address
     * @return
     */
    public HtmlBuilder<HtmlBuilder<T>> addLink(String address) {
        HtmlBuilder<HtmlBuilder<T>> sub =
                subNode(A.text());
        sub.addAttribute0(RemainAttributes.HREF.text(), address);
        return sub;
    }

    /**
     * Adds a link.
     *
     * @param url
     * @param text
     * @return
     */
    public HtmlBuilder<T> appLink(String url, String text) {
        appendPrefix();
        context.append("<a href=\"").append(XmlUtilities.quote(url))
                .append("\" >").append(text).append("</a>");
        appendSep();
        return this;

    }

    /**
     * Add an input block into the page.The input type will be "text":
     * &lt;input type="text" /&gt;
     *
     * @return an input builder
     */
    public InputBuilder<HtmlBuilder<T>> getInputBuilder() {
        return getInputBuilder(InputTypes.TEXT);
    }

    /**
     * Returns a builder for ordered list.
     *
     * @return a lower-level builder
     */
    public HtmlBuilder<HtmlBuilder<T>> addOl() {
        return subNode(OL.text());
    }

    /**
     * Returns a builder for unordered list.
     *
     * @return a lower-level builder
     */
    public HtmlBuilder<HtmlBuilder<T>> addUl() {
        return subNode(UL.text());
    }

    /**
     * Add an input block into the page.Such as
     * &lt;input type="text" /&gt;
     *
     * @param type the type of input
     * @return an input builder
     */
    public InputBuilder<HtmlBuilder<T>> getInputBuilder(InputTypes type) {
        return new InputBuilder<>(this, level + 1, type, doPrefix, doSeparator);
    }

    /**
     * Returns a table builder.
     *
     * @return a table builder
     */
    public TableBuilder<HtmlBuilder<T>> getTableBuilder() {
        return new TableBuilder<>(this, level + 1, doPrefix, doSeparator);
    }

    /**
     * Adds a form and returns the corresponding builder.The attribute "action"
     * must be given.
     *
     * @param action the value of attribute "action"
     * @return a lower-level builder
     */
    public HtmlBuilder<HtmlBuilder<T>> addForm(String action) {
        HtmlBuilder<HtmlBuilder<T>> form = subNode(FORM.text());
        form.addAttribute0(RemainAttributes.ACTION.text(), action);
        return form;
    }

    /**
     * Adds a form and returns the corresponding builder.The attribute "border" will
     * be set to "1"(true)
     *
     * @return a lower-level builder
     */
    public HtmlBuilder<HtmlBuilder<T>> addTable() {
        HtmlBuilder<HtmlBuilder<T>> table = subNode(TABLE.text());
        table.addAttribute0(RemainAttributes.BORDER.text(), "1");
        return table;
    }

    /**
     * Adds a ordered list at the current position.This method makes
     * sure the elements' order is kept as the order of elements in the list.
     * The object will be
     * convert to String by {@code toString()}.
     *
     * @param list the list
     * @return this
     */
    public HtmlBuilder<T> appOl(List<?> list) {
        HtmlBuilder<HtmlBuilder<T>> ol = addOl();
        String li = LI.text();
        for (Object s : list) {
            ol.addElement0(li, s.toString());
        }
        ol.build();
        return this;
    }

    /**
     * Adds a unordered list at the current position. This method also makes
     * sure the elements' order is kept as the order of elements in the list.
     * The object will be
     * convert to String by {@code toString()}.
     *
     * @param list the list
     * @return this
     */
    public HtmlBuilder<T> appUl(List<?> list) {
        HtmlBuilder<HtmlBuilder<T>> ul = addUl();
        String li = LI.text();
        for (Object s : list) {
            ul.addElement0(li, s.toString());
        }
        ul.build();
        return this;
    }

    /**
     * Adds a table that containing the map's data with two columns and table
     * head <text>Key</text>,<text>Value</text>
     * . The object will be
     * convert to String by {@code toString()}.
     *
     * @param map
     * @return
     */
    public HtmlBuilder<T> appMappingTable(Map<?, ?> map) {
        return appMappingTable(map, "Key", "Value");
    }

    /**
     * Adds a table that containing the map's data. The object will be
     * convert to String by {@code toString()}.
     *
     * @param map
     * @return
     */
    public HtmlBuilder<T> appMappingTable(Map<?, ?> map, String keyHead, String valueHead) {
        HtmlBuilder<HtmlBuilder<T>> tab = addTable();
        tab.subNode(THEAD.text())
                .subNode(TR.text())
                .addElement(TH.text(), keyHead)
                .addElement(TH.text(), valueHead)
                .build()
                .build();
        String td = TD.text();
        HtmlBuilder<HtmlBuilder<HtmlBuilder<T>>> tbody = tab.subNode(TBODY.text());
        for (Entry<?, ?> en : map.entrySet()) {
            tbody.subNode(TR.text())
                    .addElement(td, en.getKey().toString())
                    .addElement(td, en.getValue().toString())
                    .build();
        }
        tbody.build();
        tab.build();
        return this;
    }

}
