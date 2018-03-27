package cn.timelives.java.utilities.swingTools;

import cn.timelives.java.utilities.Printer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.PrintWriter;
/**
 * A simple class for input-like input,to make the console input much better.
 * @author lyc
 *
 */
public class InputFrame extends JFrame {
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4996763491497179959L;
	private JPanel contentPane;
	private InputTextArea input;
	
	
	
	
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InputFrame frame = new InputFrame();
					frame.setVisible(true);
					new Thread(){
						@Override
						public void run() {
							try{
								InputTextArea input = frame.input;
//								input.disableInput();a
								PrintWriter ps = new PrintWriter(input.getOutputStream(),true);
//								Printer.reSet(ps);
								while(true){
									try {
										sleep(2000);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									Printer.print("outing");
									ps.println("out");
//									input.requireInput();
									input.enableInput();
									ps.println(input.nextLine());
									input.disableInput();
								}
							}finally{
								
							}
							
							
						}
					}.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	public InputFrame(InputTextArea input) {
		setBackground(Color.BLACK);
		setFont(new Font("Consolas", Font.PLAIN, 20));
//		setIconImage(Toolkit.getDefaultToolkit().getImage(InputFrame.class.getResource("/cn/timelives/java/utilities/swingTools/icons/rainbow_dash.png")));
		setTitle("Input");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 707, 536);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		this.input = input;
//		input.outputLine("hello!This is a input like input text area.");
		scrollPane.setViewportView(input);
		
//		input.setCaret(Toolkit.getDefaultToolkit().get);
	}
	
	public InputFrame(){
		this(new InputTextArea());
	}
	
	
	public InputTextArea getTextArea(){
		return input;
	}
	
	
}
