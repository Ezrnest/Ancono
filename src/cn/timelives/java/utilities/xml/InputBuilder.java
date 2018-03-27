package cn.timelives.java.utilities.xml;
import cn.timelives.java.utilities.xml.HtmlUtilities.CommonTags;
import cn.timelives.java.utilities.xml.HtmlUtilities.InputTypes;
import cn.timelives.java.utilities.xml.HtmlUtilities.RemainAttributes;
public class InputBuilder<T extends AbstractHtmlBuilder<?,T>> extends AbstractHtmlBuilder<T, InputBuilder<T>> {

	InputBuilder(T high, int level,InputTypes type,boolean d1,boolean d2) {
		super(high, level,d1,d2);
		this.name = CommonTags.INPUT.toString();
		addAttribute(RemainAttributes.TYPE.toString(), type.toString());
	}

	@Override
	public InputBuilder<T> appendImpl(String sth) {
		return this;
	}


	@Override
	public InputBuilder<T> addAttribute(String key, String value) {
		addAttribute0(key, value);
		return this;
	}

	@Override
	public InputBuilder<T> addElement(String name, String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AbstractXmlElementBuilder<InputBuilder<T>, ?, CharSequence> subNode(String name) {
		throw new UnsupportedOperationException();
	}

	@Override
	protected AbstractXmlElementBuilder<InputBuilder<T>, ?, CharSequence> subNode() {
		throw new UnsupportedOperationException();
	}

	@Override
	protected T buildImpl() {
		high.append0(generate());
		return high;
	}
	/**
	 * Set the value of the input.
	 * @param value
	 * @return
	 */
	public InputBuilder<T> setValue(String value){
		addAttribute(RemainAttributes.VALUE.toString(), value);
		return this;
	}
	
}
