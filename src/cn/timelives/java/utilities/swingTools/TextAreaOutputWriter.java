package cn.timelives.java.utilities.swingTools;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class TextAreaOutputWriter extends Writer{
	
	private final JTextArea area;
	
	public TextAreaOutputWriter(JTextArea area){
		this.area = area;
	}
	
	@Override
	public void write(String str) throws IOException {
		area.append(str);
	}
	
	
	@Override
	public void write(char[] cbuf, int off, int len) throws IOException {
		Document doc = area.getDocument();
		try {
			doc.insertString(doc.getLength(), String.copyValueOf(cbuf, off, len),null);
		} catch (BadLocationException e) {
		}
	}

	@Override
	public void flush() throws IOException {
		//do nothing
	}

	@Override
	public void close() throws IOException {
		//can not close
	}
	/**
	 * Get a buffered writer which buffers a new instance of this 
	 * class.
	 * @param area
	 * @return
	 */
	public static BufferedWriter getBuffered(JTextArea area){
		return new BufferedWriter(new TextAreaOutputWriter(area));
	}
}
