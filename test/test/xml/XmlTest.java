package test.xml;

import cn.ancono.utilities.Printer;
import cn.ancono.utilities.xml.XmlRootBuilder;
import org.junit.Test;

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
