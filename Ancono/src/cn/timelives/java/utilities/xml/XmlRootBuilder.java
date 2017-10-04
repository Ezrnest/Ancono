package cn.timelives.java.utilities.xml;

import java.util.Objects;
/**
 * A root builder for xml, which generate a String representing the xml document when final 
 * building.
 *
 */
public class XmlRootBuilder extends XmlElementBuilderString<String,XmlRootBuilder>{
	
	protected String version = "1.0";
	protected String encoding = System.getProperty("file.encoding");
	
	
	
	XmlRootBuilder(String name){
		super(null,0,true,true);
		this.name = name;
	}
	XmlRootBuilder(String name,boolean doPre,boolean doSep) {
		super(null,0,doPre,doSep);
		this.name = name;
	}
	
	/**
	 * Set the xml version of this xml document.
	 * @param version
	 * @return
	 */
	public XmlRootBuilder setXmlVersion(String version){
		this.version = version;
		return this;
	}
	
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}



	@Override
	protected XmlRootBuilder appendImpl(String sth) {
		context.append(sth);
		return this;
	}

	@Override
	public XmlRootBuilder setName(String name) {
		this.name = Objects.requireNonNull(name);
		return this;
	}

	@Override
	public XmlRootBuilder addAttribute(String key, String value) {
		addAttribute0(key, value);
		return this;
	}





	@Override
	public XmlRootBuilder addElement(String name, String text) {
		addElement0(name, text);
		return this;
	}





	@Override
	public XmlElementBuilder<XmlRootBuilder> subNode(String name) {
		XmlElementBuilder<XmlRootBuilder> low = new XmlElementBuilder<>(this,level+1);
		return low.setName(name);
	}





	@Override
	public XmlElementBuilder<XmlRootBuilder> subNode() {
		return new XmlElementBuilder<>(this,level+1);
	}


	@Override
	protected String buildImpl() {
		StringBuilder title = new StringBuilder();
		title.append("<?xml version=\"")
		.append(version).append("\" encoding=\"").append(encoding).append("\"?>");
		appendSep(title);
		title.append(generate());
		return title.toString();
	}
	
	/**
	 * This method provides a newly-created XmlRootBuilder with the given root element 
	 * name. 
	 * @param name the root element's name.
	 * @return a new XmlRootBuilder 
	 */
	public static XmlRootBuilder createRoot(String name){
		return new XmlRootBuilder(name);
	}
	/**
	 * This method provides a newly-created XmlRootBuilder with the given root element 
	 * name and sets whether to add prefix and line separator as well.
	 * @param name the root element's name.
	 * @param enablePrefix determines whether to add prefix
	 * @param enableSeparator determines whether to add separator
	 * @return
	 */
	public static XmlRootBuilder createRoot(String name,boolean enablePrefix,boolean enableSeparator){
		return new XmlRootBuilder(name,enablePrefix,enableSeparator);
	}
	
	
}
