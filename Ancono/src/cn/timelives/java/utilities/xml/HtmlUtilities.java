package cn.timelives.java.utilities.xml;

import java.awt.Color;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import cn.timelives.java.utilities.Printer;

public final class HtmlUtilities {
	//no instance.
	private HtmlUtilities(){}
	
	
	
	
	
	public static final String documentHead = "<!DOCTYPE html>"+XmlUtilities.Separator,
			HEAD = "head",
			BODY = "body",
			DIV  = "div",
			HTML = "html"
			;
	
	public static enum StandardAttributes{
		ID("id"), CLASS("class"), CONTENTEDITABLE("contenteditable"), CONTEXTMENU("contextmenu"), DRAGGABLE(
				"draggable"), IRRELEVANT("irrelevant"), LANG("lang"), REF("ref"), REGISTRATIONMARK(
						"registrationmark"), TABINDEX("tabindex"), TEMPLATE("template"), TITLE("title"),STYLE("style");
		private final String name;

		private StandardAttributes(String name) {
			this.name = name;
		}

		public String text() {
			return name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	public static enum RemainAttributes{
		STYLE("style"),
		VALUE("value"),
		ACTION("action"),
		TYPE("type"),
		ROW("row"),
		COLUMN("column"),
		BORDER("border"),
		HREF("href"),
		;
		private final String name;
		private RemainAttributes(String name) {
			this.name = name;
		}

		public String text() {
			return name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	public static enum CommonTags{
		P("p"),
		FORM("form"),
		INPUT("input"),
		TABLE("table"),
		TH("th"),
		THEAD("thead"),
		TBODY("tbody"),
		TFOOT("tfoot"),
		TR("tr"),
		TD("td"),
		UL("ul"),
		OL("ol"),
		LI("li"),
		A("a"),
		;
		private final String name;

		private CommonTags(String name) {
			this.name = name;
		}

		public String text() {
			return name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	public static enum InputTypes{
		BUTTON("button"),
		CHECKBOX("checkbox"),
		DATE("date"),
		DATETIME("datetime"),
		DATETIME_LOCAL("datetime-local"),
		EMAIL("email"),
		FILE("file"),
		HIDDEN("hidden"),
		IMAGE("image"),
		MONTH("month"),
		NUMBER("number"),
		PASSWORD("password"),
		RADIO("radio"),
		RANGE("range"),
		RESET("reset"),
		SUBMIT("submit"),
		TEXT("text"),
		TIME("time"),
		URL("url"),
		WEEK("week");
		private final String name;
		private InputTypes(String name) {
			this.name = name;
		}

		public String text() {
			return name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	public static enum SingleTags{
		BR("<br />"),
		;
		private final String name;

		private SingleTags(String name) {
			this.name = name;
		}

		public String text() {
			return name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static enum StyleAttributes{
		COLOR("color"),
		BACKGROUND_COLOR("background-color"),
		;
		private final String name;

		private StyleAttributes(String name) {
			this.name = name;
		}

		public String text() {
			return name;
		}
		@Override
		public String toString() {
			return name;
		}
	}
	
	public static String toHtmlColor(Color c){
		StringBuilder sb = new StringBuilder();
		sb.append('#');
		String s = Integer.toHexString(c.getRGB() & 0xFFFFFF);
		for(int i=s.length();i<6;i++){
			sb.append('0');
		}
		sb.append(s);
		return sb.toString();
	}
	
	public static void main(String[] args) {
		Map<String,String> map = new LinkedHashMap<>();
		Random rd = new Random();
		for(int i=0;i<10;i++){
			map.put(Integer.toString(i), rd.nextLong()+"");
		}
		Html5RootBuilder bd = Html5RootBuilder.getHtml5Builder("Test");
		bd.getBody()
			.subNode()
				.appParagraph("This is a test <String>")
				.addParagraph()
					.getStyleBuilder()
						.setBgColor(Color.YELLOW)
						.build()
					.text("This is text")
					.changeLine()
					.text("...")
					.appLink("cn.bing.com", "BING!")
					.build()
					.appMappingTable(map)
				.build()
			;
		Printer.print(bd.build());
//		String[] s = "button checkbox date datetime datetime-local email file hidden image month number password radio range reset submit text time url week".split(" ");
//		for(String t : s){
//			Printer.print(t.toUpperCase()+"(\""+t+"\"),");
//		}
				
	}
}
