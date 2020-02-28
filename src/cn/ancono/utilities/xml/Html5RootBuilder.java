package cn.ancono.utilities.xml;

import static cn.ancono.utilities.xml.HtmlUtilities.*;

/**
 * The root builder for Html5,
 */
public class Html5RootBuilder extends AbstractHtmlBuilder<String, Html5RootBuilder> {


    private final HtmlBuilder<Html5RootBuilder> head, body;

    Html5RootBuilder(String title, String encoding, boolean doPrefix, boolean doSeparator) {
        super(null, 0, doPrefix, doSeparator);
        this.name = HTML;
        head = new HtmlBuilder<>(this, 0, HEAD);
        body = new HtmlBuilder<>(this, 0, BODY);
        initHead(title, encoding);
    }


    private void initHead(String title, String encoding) {
        head.subNode("meta")
                .addAttribute("charset", encoding)
                .build();
        head.addElement("title", title);
    }

    /**
     * Cannot set the name of this builder,returns instead.
     */
    @Override
    public Html5RootBuilder setName(String name) {
        return this;
    }

    /**
     * Returns the head builder of this html file.
     *
     * @return
     */
    public HtmlBuilder<Html5RootBuilder> getHead() {
        return head;
    }

    /**
     * Returns the body builder of this html file.
     *
     * @return
     */
    public HtmlBuilder<Html5RootBuilder> getBody() {
        return body;
    }

    /**
     * Add attributes of the html file,
     * which is equal to add element to the head of this html file.
     */
    @Override
    public Html5RootBuilder addAttribute(String key, String value) {
        head.addElement(key, value);
        return this;
    }

    /**
     * Add element to the body of this html document,
     * this method is equal to {@code getBody().addElement(name,text)}
     */
    @Override
    public Html5RootBuilder addElement(String name, String text) {
        body.addElement(name, text);
        return this;
    }

    /**
     * Get the sub node of the html root,if the name is head or body, then the corresponding
     * node is returned.
     */
    @Override
    public HtmlBuilder<Html5RootBuilder> subNode(String name) {
        if (name.equals(head)) {
            return head;
        }
        if (name.equals(body)) {
            return body;
        }
        return new HtmlBuilder<>(this, 0, name, doPrefix, doSeparator);
    }

    /**
     * This method will just return the node with name "head".
     */
    @Override
    public HtmlBuilder<Html5RootBuilder> subNode() {
        return head;
    }


    @Override
    protected String buildImpl() {
        StringBuilder sb = new StringBuilder();
        sb.append(documentHead);
        if (!head.isBuilt()) {
            head.build();
        }
        if (!body.isBuilt()) {
            body.build();
        }
        sb.append(context);
        return sb.toString();
    }

    @Override
    protected Html5RootBuilder appendImpl(String sth) {
        return this;
    }

    /**
     * The default encoding of the document.
     */
    private static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * Gets a builder for Html5. The document's attribute of encoding will be {@link #DEFAULT_ENCODING}.
     *
     * @param title the title of the web page.
     * @return a new builder.
     */
    public static Html5RootBuilder getHtml5Builder(String title) {
        return new Html5RootBuilder(title, DEFAULT_ENCODING, true, true);
    }

    /**
     * /**
     * Gets a builder for Html5.
     *
     * @param title    the title of the web page.
     * @param encoding the encoding of the document, which will be added as an attribute.
     * @return
     */
    public static Html5RootBuilder getHtml5Builder(String title, String encoding) {
        return new Html5RootBuilder(title, encoding, true, true);
    }

    /**
     * Gets a builder for Html5. The arguments sets the document's basic formats.
     *
     * @param title            the title of the web page.
     * @param encoding         the encoding of the document, which will be added as an attribute.
     * @param addIndentation   decides whether to add indentation.
     * @param addLineSeparator decides whether to add line separator.
     * @return a new builder.
     */
    public static Html5RootBuilder getHtml5Builder(String title, String encoding, boolean addIndentation, boolean addLineSeparator) {
        return new Html5RootBuilder(title, encoding, addIndentation, addLineSeparator);
    }

}
