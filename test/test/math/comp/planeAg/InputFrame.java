package test.math.comp.planeAg;

import cn.timelives.java.utilities.Printer;
import cn.timelives.java.utilities.dynamic.DynamicCompileEngine;
import cn.timelives.java.utilities.dynamic.JavaCodeGenerator;
import cn.timelives.java.utilities.swingTools.TextAreaOutputWriter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InputFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6693124674557745437L;

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InputFrame frame = new InputFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public InputFrame() {
		setTitle("MathInput");
		setIconImage(Toolkit.getDefaultToolkit().getImage(InputFrame.class.getResource("/cn/timelives/java/utilities/swingTools/icons/rainbow_dash.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1080, 649);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 37, 588, 521);
		contentPane.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.isControlDown() && e.getKeyCode() == KeyEvent.VK_F11){
					launchCode();
					e.consume();
				}
			}
		});
		textArea.setForeground(Color.WHITE);
		textArea.setFont(new Font("Consolas", Font.PLAIN, 13));
		textArea.setBackground(Color.BLACK);
//		Caret c = new CaretUnderline();
//		textArea.setCaret(c);
		textArea.setCaretColor(Color.WHITE);
		textArea.setTabSize(4);
		scrollPane.setViewportView(textArea);
		
		JButton btnRun = new JButton("Run!");
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				launchCode();
			}
		});
		btnRun.setForeground(Color.BLACK);
		btnRun.setBackground(Color.GREEN);
		btnRun.setBounds(10, 577, 150, 23);
		contentPane.add(btnRun);
		
		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(608, 37, 453, 563);
		contentPane.add(scrollPane_1);
		
		output = new JTextArea();
		output.setFont(new Font("����", Font.PLAIN, 13));
		output.setForeground(Color.WHITE);
		output.setBackground(Color.BLACK);
		output.setEditable(false);
		scrollPane_1.setViewportView(output);
		
		JLabel lblInput = new JLabel("Input");
		lblInput.setBounds(10, 10, 470, 23);
		contentPane.add(lblInput);
		
		JLabel lblOutput = new JLabel("Output");
		lblOutput.setBounds(608, 14, 193, 15);
		contentPane.add(lblOutput);
		initGenerator();
		writer = new PrintWriter(TextAreaOutputWriter.getBuffered(output),true);
		
		JButton btnClearOutput = new JButton("Clear Output");
		btnClearOutput.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				output.setText("");
			}
		});
		btnClearOutput.setBounds(493, 577, 105, 23);
		contentPane.add(btnClearOutput);
	}
	
	private DynamicCompileEngine engine = DynamicCompileEngine.getDefaultInstance();
	
	private JavaCodeGenerator generator = new JavaCodeGenerator();
	private Random rd = new Random();
	private JTextArea output;
	
	private PrintWriter writer;
	private JTextArea textArea;
	private final Executor exc = Executors.newSingleThreadExecutor();
	
	private void initGenerator(){
		generator.setPublicClass()
			.setClassName("JavaMethodObject")
			.addImport("cn.timelives.java.utilities.*")
			.addImport("cn.timelives.java.utilities.math.*")
			.addImport("cn.timelives.java.utilities.math.planeAG.*")
			.addImport("cn.timelives.java.utilities.math.number_types.*")
			.addImportStatic("cn.timelives.java.utilities.Printer.*")
			.addImplement("Runnable");
	}
	
	private void launchCode(){
		launchCode(textArea.getText());
	}
	private static Pattern lineP = Pattern.compile(".+?[$|\n]");
	private static Pattern defineP = Pattern.compile(" *\\#define +(\\S+) +(\\S+)");
	private String defineAndReplace(String code){
		Printer.print(code);
		Printer.print_();
		Matcher mat = lineP.matcher(code);
		List<String> from = new LinkedList<>();
		List<String> to = new LinkedList<>();
		int end = 0;
		while(mat.find()){
			String line = mat.group();
			Matcher m2 = defineP.matcher(line);
			if(m2.find()){
				from.add(m2.group(1));
				to.add(m2.group(2));
//				Printer.print("Match::"+m2.group(1)+" -> "+ m2.group(2));
			}else{
				end = mat.start();
				break;
			}
		}
//		Printer.print("end = "+end);
		code = code.substring(end);
		Iterator<String> fit = from.iterator();
		Iterator<String> tit = to.iterator();
		while(fit.hasNext()){
//			Printer.print(".....");
			String s1 = fit.next();
			String s2 = tit.next();
//			Printer.print(s1+" -> "+s2);
			code = code.replaceAll(Pattern.quote(s1), s2);
		}
		return code;
	}
	private boolean running = false;
	
	private void launchCode(String code0){
		if(running){
			return;
		}
		running = true;
		exc.execute(new Runnable(){
			@Override
			public void run() {
				String code = defineAndReplace(code0);
				Printer.print(code);
				Printer.print_();
				PrintWriter pw = Printer.getOutput();
				Printer.reSet(writer);
				try{
					JavaCodeGenerator g2 = generator.clone();
					CharSequence javaCode = g2.setClassName(g2.getClassName()+Long.toString(Math.abs(rd.nextLong())))
						.getMethodBuilder("run")
						.publicMod()
						.returnVoid()
						.setBody(code)
						.build()
					.buildCode();
					Class<?> runnable = engine.compileAndLoadClass(g2.getClassName(), javaCode);
					if(runnable==null){
						output.append("Init failed:");
						output.append(engine.getFailureMessage());
						System.err.println(engine.getFailureMessage());
						System.err.println(javaCode);
					}else{
						try {
							Runnable task = (Runnable) runnable.newInstance();
							task.run();
						} catch (Exception e) {
							output.append("Init failed:");
							output.append(e.toString());
						}
					}
					Printer.print_();
				}finally{
					Printer.reSet(pw);
				}
				running = false;
			}
		});
		
		
		
	}
}
