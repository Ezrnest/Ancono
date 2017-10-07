package cn.timelives.java.utilities.xml;
import static cn.timelives.java.utilities.xml.HtmlUtilities.CommonTags.TABLE;
import static cn.timelives.java.utilities.xml.HtmlUtilities.CommonTags.TD;
import static cn.timelives.java.utilities.xml.HtmlUtilities.CommonTags.TH;
import static cn.timelives.java.utilities.xml.HtmlUtilities.CommonTags.TR;
import static cn.timelives.java.utilities.xml.HtmlUtilities.RemainAttributes.BORDER;

import java.util.List;


/**
 * The builder specialized for table building.
 *
 * @param <T>
 */
public class TableBuilder<T extends AbstractHtmlBuilder<?,T>> extends HtmlBuilder<T> {
	
	TableBuilder(T high, int level, boolean doPrefix, boolean doSeparator) {
		super(high, level,TABLE.toString(), doPrefix, doSeparator);
		setBorder(true);
	}
	/**
	 * Set the border,the default setting is "1"(true).
	 * @param enable
	 * @return this
	 */
	public TableBuilder<T> setBorder(boolean enable){
		addAttribute0(BORDER.toString(), enable ? "1" : "0");
		return this;
	}
	private HtmlBuilder<HtmlBuilder<T>> row(){
		return subNode(TR.toString());
	}
	/**
	 * Add a table <b>head</b> row of the given {@code rowData},the number of 
	 * elements will be equal to {@code rowData.length}. The object will be 
	 * convert to String by {@code toString()}
	 * @param rowData
	 * @return this
	 */
	public TableBuilder<T> addHeadRow(Object... rowData){
		HtmlBuilder<HtmlBuilder<T>> sub = row();
		for(Object s : rowData){
			sub.addElement0(TH.toString(), s.toString());
		}
		return this;
	}
	
	/**
	 * Add a table <b>head</b> row of the given {@code rowData},the number of 
	 * elements will be equal to {@code rowData.length}
	 * @param rowData
	 * @return this
	 */
	public TableBuilder<T> addHeadRow(List<? extends Object> rowData){
		HtmlBuilder<HtmlBuilder<T>> sub = row();
		for(Object s : rowData){
			sub.addElement0(TH.toString(), s.toString());
		}
		return this;
	}
	
	/**
	 * Add a table row of the given {@code rowData},the number of 
	 * elements will be equal to {@code rowData.length}. The object will be 
	 * convert to String by {@code toString()}.
	 * @param rowData
	 * @return this
	 */
	public TableBuilder<T> addRow(Object... rowData){
		HtmlBuilder<HtmlBuilder<T>> sub = row();
		for(Object s : rowData){
			sub.addElement0(TD.toString(), s.toString());
		}
		return this;
	}
	/**
	 * Add a table row of the given {@code rowData},the number of 
	 * elements will be equal to {@code rowData.length}. The object will be 
	 * convert to String by {@code toString()}
	 * @param rowData
	 * @return this
	 */
	public TableBuilder<T> addRow(List<? extends Object> rowData){
		HtmlBuilder<HtmlBuilder<T>> sub = row();
		for(Object s : rowData){
			sub.addElement0(TD.toString(), s.toString());
		}
		return this;
	}
	
}
