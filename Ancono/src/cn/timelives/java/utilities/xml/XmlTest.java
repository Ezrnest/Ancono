package cn.timelives.java.utilities.xml;

import org.junit.Test;

import cn.timelives.java.utilities.Printer;

public class XmlTest {
	@Test
	public void test(){
		XmlRootBuilder bd = XmlRootBuilder.createRoot("XML");
		bd.subNode("pony")
			.addAttribute("name", "Twilight")
			.addElement("FullName", "Twilight Sparkle")
			.subNode("color")
				.addElement("hair", "purple")
				.addElement("eye", "purple")
				.build()
			.build()
		.subNode("pony")
			.addAttribute("name", "Rainbow")
			.addElement("FullName", "Rainbow Dash")
			.subNode("color")
				.addElement("hair", "rainbow")
				.addElement("eye", "rose")
				.build()
			.build();
		Printer.print(bd.build());
	}
}
