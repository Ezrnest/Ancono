package cn.timelives.java.utilities.xml;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import cn.timelives.java.utilities.AbstractBuilder;
import cn.timelives.java.utilities.xml.HtmlUtilities.StandardAttributes;
import cn.timelives.java.utilities.xml.HtmlUtilities.StyleAttributes;
/**
 * The abstract super class of HtmlBuilder 
 *
 * @param <T>
 * @param <S>
 */
abstract class 
AbstractHtmlBuilder<T,S extends AbstractHtmlBuilder<T,S>> 
extends XmlElementBuilderString<T,S>{
	//<T extends XmlElementBuilderString<?,T>> extends XmlElementBuilderString<T, HtmlBuilder<T>>
	/**
	 *  
	 * @param high
	 * @param prefix 
	 */
	AbstractHtmlBuilder(T high,int level){
		super(high,level,true,true);
	}
	/**
	 *  
	 * @param high
	 * @param prefix 
	 */
	AbstractHtmlBuilder(T high,int level,boolean doPrefix,boolean doSeparator){
		super(high,level,doPrefix,doSeparator);
	}
	
	/**
	 * This method will set the name attribute of the element instead of setting the 
	 * tag's name
	 */
	@SuppressWarnings("unchecked")
	@Override
	public S setName(String name) {
		addAttribute0("name", name);
		return (S) this;
	}
	/**
	 * Set the style of this element.
	 * @param style
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public S setStyle(String style){
		addAttribute0(StandardAttributes.STYLE.text(), style);
		return (S) this;
	}
	/**
	 * Returns a style builder.
	 * @return
	 */
	public StyleBuilder getStyleBuilder(){
		return new StyleBuilder();
	}
	
	public class StyleBuilder extends AbstractBuilder<S,StyleBuilder,String,String>{
		StyleBuilder() {
		}
		private Map<String,String> styleMap = new HashMap<>();
		public AbstractHtmlBuilder<T, S>.StyleBuilder addAttribute(String key,String value){
			styleMap.put(key, value);
			return this;
		}
		/**
		 * Set the color of the attribute "color".
		 * @param c
		 * @return
		 */
		public AbstractHtmlBuilder<T, S>.StyleBuilder setColor(Color c){
			styleMap.put(StyleAttributes.COLOR.text(),HtmlUtilities.toHtmlColor(c));
			return this;
		}
		
		/**
		 * Set the color of the attribute "color".
		 * @param name
		 * @return
		 */
		public AbstractHtmlBuilder<T, S>.StyleBuilder setColor(String name){
			styleMap.put(StyleAttributes.COLOR.text(),name);
			return this;
		}
		/**
		 * Set the background.
		 * @param name
		 * @return
		 */
		public AbstractHtmlBuilder<T, S>.StyleBuilder setBgColor(Color c){
			styleMap.put(StyleAttributes.BACKGROUND_COLOR.text(),HtmlUtilities.toHtmlColor(c));
			return this;
		}
		/**
		 * Set the background.
		 * @param name
		 * @return
		 */
		public AbstractHtmlBuilder<T, S>.StyleBuilder setBgColor(String name){
			styleMap.put(StyleAttributes.BACKGROUND_COLOR.text(),name);
			return this;
		}
		
		@Override
		protected String generate() {
			StringBuilder sb = new StringBuilder();
			//"xxx: yyy;"
			for(Entry<String,String> en : styleMap.entrySet()){
				sb.append(en.getKey()).append(": ")
					.append(en.getValue()).append(';');
			}
			return sb.toString();
		}

		@Override
		protected S buildImpl() {
			return setStyle(generate());
		}
		/**
		 * Disabled.
		 */
		@Override
		protected AbstractHtmlBuilder<T, S>.StyleBuilder appendImpl(String sth) {
			return this;
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public S setClass(String clazz){
		addAttribute0(StandardAttributes.CLASS.text(), clazz);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setContenteditable(String contenteditable){
		addAttribute0(StandardAttributes.CONTENTEDITABLE.text(), contenteditable);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setContextmenu(String contextmenu){
		addAttribute0(StandardAttributes.CONTEXTMENU.text(), contextmenu);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setDraggable(String draggable){
		addAttribute0(StandardAttributes.DRAGGABLE.text(), draggable);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setId(String id){
		addAttribute0(StandardAttributes.ID.text(), id);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setIrrelevant(String irrelevant){
		addAttribute0(StandardAttributes.IRRELEVANT.text(), irrelevant);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setLang(String lang){
		addAttribute0(StandardAttributes.LANG.text(), lang);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setRef(String ref){
		addAttribute0(StandardAttributes.REF.text(), ref);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setRegistrationmark(String registrationmark){
		addAttribute0(StandardAttributes.REGISTRATIONMARK.text(), registrationmark);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setTabindex(String tabindex){
		addAttribute0(StandardAttributes.TABINDEX.text(), tabindex);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setTemplate(String template){
		addAttribute0(StandardAttributes.TEMPLATE.text(), template);
		return (S) this;
	}
	@SuppressWarnings("unchecked")
	public S setTitle(String title){
		addAttribute0(StandardAttributes.TITLE.text(), title);
		return (S) this;
	}

	
	
//	public static void main(String[] args) {
//		JavaCodeGenerator gen = new JavaCodeGenerator();
//		gen.setClassName("Name");
//		String[] strs = "class, contenteditable, contextmenu, dir, draggable, id, irrelevant, lang, ref, registrationmark, tabindex, template, title".split(", ");
//		for(String s : strs){
//			String name = "set"+Character.toUpperCase(s.charAt(0))+s.substring(1, s.length());
//			MethodBuilder mb = gen.getMethodBuilder(name);
//			mb.publicMod()
//				.addAnnotation("@SuppressWarnings(\"unchecked\")")
//				.setReturnType("S")
//				.addPara("String", s)
//				.block()
//				.line("addAttribute0(StandardAttributes."+s.toUpperCase()+".text(), "+s+");")
//				.line("return (S) this;")
//				.build()
//			.build();
//		}
//		Printer.print(gen.buildCode());
//	}
	//class, contenteditable, contextmenu, dir, draggable, id, irrelevant, 
	//lang, ref, registrationmark, tabindex, template, title
}
