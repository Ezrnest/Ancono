package cn.timelives.java.utilities;


import cn.timelives.java.utilities.swingTools.InputFrame;
import cn.timelives.java.utilities.swingTools.InputTextArea;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * A class for easy usage of a console,this console only accept lines 
 * as input and lines as output.
 * <p>After creating an instance of such console,
 * the method {@code open()} should be called to open the console to user.
 *  
 * @author lyc
 *
 */
public abstract class EasyConsole {
	
	private static final Pattern intPattern = Pattern.compile("[+-]?\\d+{1,10}"),
			emptyLinePattern = Pattern.compile(" *\\z");
	
	public static final Pattern COMMENT_PATTERN = Pattern.compile("//");
	
	
	protected boolean isLineFilterEnabled = false;
	
	protected Pattern lineFilter = emptyLinePattern;
	
	public EasyConsole(){
	}
	/**
	 * Open this console,this method should be called only once and multiply 
	 * calls are meaningless.
	 */
	public abstract void open();
	
	/**
	 * Close this console, this method should be called only once and multiply 
	 * calls are meaningless.
	 */
	public abstract void close();
	
	
	/**
	 * Get the output stream of this console,any output should be given to the PrintWriter or directly call method {@link #print(String)}.
	 * @return
	 */
	public abstract PrintWriter getOutput();
	/**
	 * Print the given output line text to the console,the given String should not contain any line separator such 
	 * as {@code \n},this method will automatically change line in the console and no flush operation is needed.
	 * @param line
	 */
	public abstract void print(String line);
	/**
	 * Append the given text to the console, the given String should not contain any line separator such 
	 * as {@code \n}. No flush operation is needed.
	 * @param line
	 */
	public abstract void append(String text);
	
	/**
	 * Set whether the console should enable line filter.If line filter is enabled,
	 * lines that match the line filter pattern will be ignored.
	 * @param b
	 */
	public void setLineFilterActivated(boolean b){
		isLineFilterEnabled = b;
	}
	/**
	 * Set the line filter.The code may like this: <pre>
	 * String line = readLine();
	 * if(!lineFilter.matcher(line).lookingAt()){
	 * return line;
	 * }
	 * </pre>
	 * <b>This method will automatically activate line filter</b>
	 * The default line filter is a multiply-white-space pattern (" +")
	 * @param p
	 */
	public void setLineFilter(Pattern p){
		lineFilter = p;
		isLineFilterEnabled = true;
	}
	
	/**
	 * Get the next line from the input,this method will block the user.Before 
	 * returning the new line , the filter will work first. 
	 * @return the next line from input.
	 */
	public String nextLine(){
		if(isLineFilterEnabled){
			String line;
			do{
				line = readLine();
			}while(lineFilter.matcher(line).lookingAt());
			return line;
		}else{
			return readLine();
		}
	}
	/**
	 * Implement this method to create the console instance.
	 * @return the next line from input/
	 */
	protected abstract String readLine();
	
	
	
	/**
	 * Get the next integer from the next line,only the starting numbers will be 
	 * considered,the remaining part will be ignored.
	 * <p>
	 * This method will wait until a number is actually inputed instead of throwing 
	 * an exception.
	 * @return
	 */
	public int nextInt(){
		String line ;
		while(true){
			line = nextLine();
			Matcher ma = intPattern.matcher(line);
			if(ma.lookingAt()){
				String intstr = ma.group();
				try{
					int t = Integer.parseInt(intstr);
					return t;
				}catch(NumberFormatException nfe){
					nfe.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Get the next integers from the next line.The integers should be split with spaces,
	 * illegal input will be ignored
	 * ,which means if the 
	 * input is totally illegal,an empty array will be returned.
	 * <p>
	 * For example,if user input <pre>12 1a 32</pre>an array of <code>{12,32}</code>  should be returned.
	 * @return a array of integers.
	 */
	public int[] nextInts(){
		String line = nextLine().trim();
		String[] ints = RegexSup.SPACE.split(line);
		int[] re = new int[ints.length];
		int p = 0;
		for(int i=0;i<ints.length;i++){
			try{
				re[p++] = Integer.parseInt(ints[i]);
			}catch(NumberFormatException n){
				//ignore this exception firstly
				p--;
			}
		}
		if(p==re.length)
			return re;
		else{
			return Arrays.copyOf(re, p);
		}
	}
	
	/**
	 * Return the next parsed object from the input,this method will continue to search for the input object 
	 * if there is no such object at next line.The pattern of the object should be given.<p>
	 * <b>Only the first object matched will be returned,the remaining part of the line will be ignored.</b>
	 * @param p
	 * @param parser a parser from the matched matcher.
	 * @return
	 */
	public <T> T nextObject(Pattern p,Function<Matcher,T> parser){
		String line;
		while(true){
			line = nextLine();
			Matcher mat = p.matcher(line);
			if(mat.find()){
				return parser.apply(mat);
			}
		}
	}
	/**
	 * Return the next parsed object from the input,this method will continue to search for the input object 
	 * if there is no such object at next line.The pattern of the object should be given.<p>
	 * This method will try to get enough objects from the input,the method will read the input line by line and 
	 * if one line contains multiply objects,they will be read all.However,after enough objects are got,the 
	 * current reading line will be ignored and the remaining part of this line will be ignored. 
	 * @param p
	 * @param parser
	 * @param limit the limit of the number of objects,must be >=0
	 * @return
	 */
	public <T> List<T> nextObjects(Pattern p,Function<Matcher,T> parser,int limit){
		ArrayList<T> list = new ArrayList<>(limit);
		String line;
		int count = 0;
		while(count < limit){
			line = nextLine();
			Matcher mat = p.matcher(line);
			while(mat.find()){
				count ++;
				list.add(parser.apply(mat));
			}
		}
		return list;
	}
	
	
	/**
	 * Returns a list of objects from the input.The {@code parser} must match the pattern {@code p},
	 * and space will be used as spliterator;
	 * @param p
	 * @param func
	 * @return
	 */
	public <T> List<T> nextObjects(Pattern p,Function<String,T> parser){
		return nextObjects(p,RegexSup.SPACE,parser);
	}
	 
	
	/**
	 * Returns a list of objects from the input.The {@code parser} must match the pattern {@code p},and the 
	 * {@code spliterator} will be used for recognizing the space or other things in the inputed line.
	 * @param p
	 * @param spliterator
	 * @param func
	 * @return
	 */
	public <T> List<T> nextObjects(Pattern p,Pattern spliterator,Function<String,T> parser){
		ArrayList<T> list = new ArrayList<>();
		String line = nextLine();
		Matcher mP = p.matcher(line);
		Matcher mS = spliterator.matcher(line);
		int pos = 0;
		while(pos < line.length()){
			if(!mP.find(pos)){
				break;
			}else{
				String str = mP.group();
				list.add(parser.apply(str));
				pos = mP.end();
				if(mS.find(pos)){
					pos = mS.end();
				}else{
					//cannot find spliterator
				}
			}
		}
		return list;
	}
	
	
	/**
	 * Enables the input of this console,which means the user cannot enter anything 
	 * into the console,this method is optional,which means in some implementation this 
	 * method may not work.
	 */
	public abstract void enableInput();
	/**
	 * Disables the input of this console,which means the user cannot enter anything 
	 * into the console,this method is optional,which means in some implementation this 
	 * method may not work.
	 */
	public abstract void disableInput();
	/**
	 * An optional method.The console will try to make up a beep sound to notice/alert the user.
	 */
	public abstract void beep();
	
	/**
	 * Set the prefix of input to this console,this prefix will be shown in the front of 
	 * any line that user inputs.
	 * @param prefix a String input
	 */
	public abstract void setInputPrefix(String prefix);
	/**
	 * Set the prefix of output to this console,this prefix will be shown in the front of 
	 * any line that user inputs.
	 * @param prefix a String input
	 */
	public abstract void setOutputPrefix(String prefix);
		
	
	public abstract void setFont(Font f);
	
	/**
	 * Get a default easy console.The console will not open automatically.
	 * @return
	 */
	public static EasyConsole getSwingImpl(){
		EasyConsole ecx = new InputConsole();
		return ecx;
	}
	
	/**
	 * Get a default easy console.The console will <b>NOT</b> open automatically.
	 * @return
	 */
	public static EasyConsole getSwingImpl(String title){
		EasyConsole ecx = new InputConsole(title);
		return ecx;
	}
	
	static class InputConsole extends EasyConsole{
		
		private final InputFrame frame;
		private final InputTextArea input;
		private final PrintWriter out;
		
		
		public InputConsole() {
			input = new InputTextArea();
			frame = new InputFrame(input);
			out = new PrintWriter(input.getOutputStream(),true);
		}
		
		public InputConsole(String text) {
			input = new InputTextArea(text,-1,-1,-1);
			frame = new InputFrame(input);
			frame.setTitle(text);
			out = new PrintWriter(input.getOutputStream(),true);
		}

		@Override
		public void open() {
			SwingUtilities.invokeLater(()->{
				frame.setVisible(true);
			});
		}
		
		/**
		 * @see cn.timelives.java.utilities.EasyConsole#close()
		 */
		@Override
		public void close() {
			SwingUtilities.invokeLater(()->{
				frame.setVisible(false);
				frame.dispose();
			});
		}

		@Override
		public PrintWriter getOutput() {
			return out;
		}


		@Override
		protected String readLine() {
			return input.nextLine();
		}

		@Override
		public void enableInput() {
			input.enableInput();
		}

		@Override
		public void disableInput() {
			input.disableInput();
		}

		@Override
		public void beep() {
			input.getToolkit().beep();
		}

		@Override
		public void print(String line) {
			input.outputLine(line);
		}
		/*
		 * @see cn.timelives.java.utilities.EasyConsole#append(java.lang.String)
		 */
		@Override
		public void append(String text) {
			out.append(text);
			out.flush();
		}

		@Override
		public void setInputPrefix(String prefix) {
			input.setPrefixInput(prefix);
		}

		@Override
		public void setOutputPrefix(String prefix) {
			input.setPrefixOutPut(prefix);
		}

		/**
		 * @see cn.timelives.java.utilities.EasyConsole#setFont(java.awt.Font)
		 */
		@Override
		public void setFont(Font f) {
			input.setFont(f);
		}
		
	}
	
	
	public static void main(String[] args) {
		EasyConsole con = getSwingImpl("ALLLLLL");
		con.setOutputPrefix("   ");
		con.setLineFilter(COMMENT_PATTERN);
		con.open();
		con.setLineFilterActivated(true);
		int i=0;
		while(true){
			String line = con.nextLine();
//			con.print(line);
			con.setInputPrefix((i%2) == 0 ? "==":"----");
			i++;
//			con.beep();
			System.out.println(line);
		}
	}
	
}
