package cn.timelives.java.utilities.xml;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import static cn.timelives.java.utilities.xml.XmlUtilities.*;

public abstract class 
XmlElementBuilderString<T,S extends XmlElementBuilderString<T,S>> 
extends AbstractXmlElementBuilder<T,S,CharSequence>{
	/**
	 * The attributes map.
	 */
	protected final Map<String,String> attr;
	/**
	 * The name of this element.
	 */
	protected String name = null;
	/**
	 * The context in this element.If the context is empty,then the element will be built as <br>
	 * &lt;name key1="..." ... /&gt;
	 */
	protected StringBuilder context;
	
	protected final T high;
	protected final int level;
	protected final String prefix;
	protected final boolean doPrefix,doSeparator;
	/**
	 *  
	 * @param high
	 * @param prefix 
	 */
	protected XmlElementBuilderString(T high,int level){
		this(high, level, true,true);
	}
	protected XmlElementBuilderString(T high,int level,boolean doPrefix,boolean doSeparator){
		this.high = high;
		this.level = level;
		if(doPrefix){
			prefix = XmlUtilities.getPrefix(level);
		}else{
			prefix = "";
		}
		this.doSeparator = doSeparator;
		this.doPrefix = doPrefix;
		attr = new LinkedHashMap<>();
		context = new StringBuilder();
	}
	/**
	 * The default implement for the method, no returning this.
	 * @param name
	 * @param text
	 */
	protected void addElement0(String name, String text) {
		name = quote(name);
		if(doPrefix){
			context.append(prefix).append('	');
		}
		appendBlockStart(name,context);
		context.append(quote(text));
		appendBlockEnd(name,context);
		appendSep();
	}
	/**
	 * The default implement for the method.
	 * @param key
	 * @param value
	 */
	protected void addAttribute0(String key,String value){
		attr.put(key, value);
	}
	
	/**
	 * @see cn.timelives.java.utilities.xml.AbstractXmlElementBuilder#addComment(java.lang.String)
	 */
	@Override
	public S addComment(String comment) {
		comment = quote(comment);
		if(doPrefix){
			context.append(prefix).append('	');
		}
		context.append("<!-- ");
		context.append(comment);
		context.append(" -->");
		appendSep();
		return null;
	}
	
	
	/**
	 * Append method for sub classes.
	 * @param sth
	 */
	protected void append0(CharSequence sth) {
		context.append(sth);
	}
	/**
	 * Appends some text to this xml, this method is 
	 * not recommended, use other methods instead.
	 */
	@Override
	protected abstract S appendImpl(String sth);
	
	/**
	 * Append prefix or not.
	 * @param sb
	 */
	protected void appendPrefix(StringBuilder sb){
		if(doPrefix){
			sb.append(prefix);
		}
	}
	/**
	 * Append prefix or not.
	 * @param sb
	 */
	protected void appendPrefix(){
		if(doPrefix){
			context.append(prefix);
		}
	}
	
	/**
	 * Append line separator or not.
	 * @param sb
	 */
	protected void appendSep(StringBuilder sb){
		if(doSeparator)
			sb.append(Separator);
	}

	/**
	 * Append line separator or not.
	 * @param sb
	 */
	protected void appendSep(){
		if(doSeparator)
			context.append(Separator);
	}
	
	
	@Override
	protected CharSequence generate() {
		if(name==null){
			throw new IllegalStateException("No Name");
		}
		//first we append the attributes
		StringBuilder full = new StringBuilder();
		String name0 = quote(name);
		appendPrefix(full);
		full.append('<').append(name0);
		for(Entry<String,String> en : attr.entrySet()){
			full.append(' ').append(quote(en.getKey())).append('=')
				.append('"').append(quote(en.getValue()))
				.append("\"");
		}
		if(context.length()==0){
			full.append(' ').append(Ending);
		}else{
			full.append('>');
			appendSep(full);
			full.append(context);
			appendPrefix(full);
			appendBlockEnd(name0,full);
		}
		
		appendSep(full);
		return full;
	}
	/**
	 * Returns the prefix of this builder.
	 * @return prefix
	 */
	public String getPrefix(){
		return prefix;
	}
	
	
}
