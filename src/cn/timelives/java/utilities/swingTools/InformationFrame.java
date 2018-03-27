/**
 * 2017-11-11
 */
package cn.timelives.java.utilities.swingTools;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import java.awt.*;

/**
 * @author liyicheng
 * 2017-11-11 20:24
 *
 */
public class InformationFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2103379469624804012L;
	private JPanel contentPane;
	private JTextPane textPane;


	/**
	 * Create the frame.
	 */
	public InformationFrame() {
		setTitle("Information");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 400, 500);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		textPane = new JTextPane();
		scrollPane.setViewportView(textPane);
	}
	
	public InformationFrame(String text) {
		this();
		textPane.setText(text);
		
	}
	
	/**
	 * Create a new InformationFrame which contains the given information.
	 * @param information
	 */
	public static void showInformation(String information) {
		new InformationFrame(information).setVisible(true);
	}
	
	
	public static void showInformation(String... information) {
		InformationFrame in = new InformationFrame();
		Document doc = in.textPane.getDocument();
		for(String s : information) {
			try {
				doc.insertString(doc.getLength(),s+System.lineSeparator(), null);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		}
		in.setVisible(true);
	}
	
	/**
	 * Create a new InformationFrame which contains the given information.
	 * @param information
	 */
	public static void showInformationWithTitle(String information,String title) {
		InformationFrame in = new InformationFrame(information);
		in.setTitle(title);
		in.setVisible(true);
	}
	
	
}
