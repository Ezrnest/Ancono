package cn.timelives.java.utilities.swingTools;

import java.awt.Color;
import java.awt.Font;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;



//import myUtil.Printer;

public class InputTextArea extends JTextArea {
	
	private static final String message = "Hello!           Input Console By Lyc"
			+ "\n-----------------------------------\n";
	
	public static final String DEFAULT_PREFIX_OUT = " ";
	public static final String DEFAULT_PREFIX_IN = "-->";
	/**
	 * The size of the text area's buffer,the actually character count may not exceed it.
	 */
	public static final int DEFAULT_BUFFER_SIZE = 65536;
	/**
	 * The reduced size of text when the text area is overflow. 
	 */
	public static final int DEFAULT_BUFFER_REDUCE = 65536 >> 1; 
	
	
	private String prefixOutput = DEFAULT_PREFIX_OUT
					,prefixInput = DEFAULT_PREFIX_IN;
	
	private boolean inputEnabled = true;
	
	private final int headLength;
	
	private final Lock lock = new ReentrantLock();
	private final Condition con_getter = lock.newCondition();
	/**
	 * 
	 */
	private static final long serialVersionUID = 9215104415343615750L;
	
	
	private final int bufSize,bufReduce;
	
	private final Document doc ;
	
	/**
	 * Create a input text area as default out view.
	 */
	public InputTextArea(String text,int bufferSize,int bufferReduce){
		this.bufSize = bufferSize <= 0 ? DEFAULT_BUFFER_SIZE : bufferSize;
		this.bufReduce =  bufferReduce <= 0 ? DEFAULT_BUFFER_REDUCE : bufferReduce;
		
		setBackground(Color.BLACK);
		setFont(new Font("Consolas", Font.PLAIN, 14));
		setTabSize(4);
		setLineWrap(false);
		setForeground(Color.WHITE);
		setCaret(new CaretUnderline());
		setCaretColor(Color.WHITE);
		setCursor(null);
		
		
		text = text == null ? message : text;
		doc = getDocument();
		try {
			doc.insertString(0, text, null);
		} catch (BadLocationException e) {
		}
		if(!text.endsWith("\n")){
			append("\n");
		}
		headLength = doc.getLength();
		append(prefixInput);
		lockAll();
		
		initPopupMenu();
		initListener();
	}
	
	public InputTextArea(){
		this(message,DEFAULT_BUFFER_SIZE,DEFAULT_BUFFER_REDUCE);
	}
	
	
	private final JPopupMenu pm = new JPopupMenu("Menu");;
	private void initPopupMenu(){
		JMenuItem copy = new JMenuItem("copy");
		JMenuItem paste = new JMenuItem("paste");
		JMenuItem cut = new JMenuItem("cut");
		
		copy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				InputTextArea.this.copy();
			}
		});
		paste.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pasteEvent();
			}
		});
		cut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(userInputRequired()){
					InputTextArea.this.cut();
				}
			}
		});
		
		pm.add(copy);
		pm.add(paste);
		pm.add(cut);
		
		pm.addPopupMenuListener(new PopupMenuListener() {
			@Override
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				copy.setEnabled(isSelecting);
				cut.setEnabled(isSelecting);
			}
			
			@Override
			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void popupMenuCanceled(PopupMenuEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		this.add(pm);
		pm.setEnabled(true);
	}
	
	private int lockedDotPos = 0;
	
	private boolean isSelecting = false;
	
	
	private void initListener(){
		
		
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			public void mouseReleased(MouseEvent e) {
				if (e.isPopupTrigger()) {
					showMenu(e);
				}
			}

			private void showMenu(MouseEvent e) {
				pm.show(e.getComponent(), e.getX(), e.getY());
			}

			
		});
		
		addCaretListener(new CaretListener() {
			
			@Override
			public void caretUpdate(CaretEvent e) {
				if(e.getDot() != e.getMark()){
					isSelecting = true;
				}else{
					isSelecting = false;
					if(e.getDot() < lockedDotPos){
						InputTextArea.this.setCaretPosition(lockedDotPos);
					}
				}
				
			}
		});
		
		addKeyListener(new KeyListener() {
			
			@Override
			public void keyTyped(KeyEvent e) {
				if(userInputRequired()){
					if(getCaretPosition() <= 
							lockedDotPos && e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
						e.consume();
						return;
					}
				}else{
					e.consume();
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(!inputEnabled){
					e.consume();
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				//allow selecting and copying from this text area
				
				int code = e.getKeyCode();
				if(e.isControlDown()){
					if(code == KeyEvent.VK_X ){
						if(!userInputRequired()){
							e.consume();
						}
					}else if(code == KeyEvent.VK_V){
						pasteEvent();
						e.consume();
					}else if(code != KeyEvent.VK_C 
							&& code != KeyEvent.VK_A){
						e.consume();
					}
					return;
				}
				if(!userInputRequired()){
					e.consume();
					return;
				}
				int pos = InputTextArea.this.getCaretPosition();
				if( pos <= lockedDotPos ){
					if(code == KeyEvent.VK_BACK_SPACE || (code == KeyEvent.VK_DELETE && pos != lockedDotPos)){
						getToolkit().beep();
						e.consume();
						return;
					}
				}
				if(code == KeyEvent.VK_ENTER){
					inputLineChange();
					e.consume();
					return;
				}
			}
		});
		
		
		
//		
//		Printer.print(getMouseListeners());
	}
	/**
	 * Return true to allow this input , otherwise false may remove the caret to the 
	 * lockDotPos.
	 * @return
	 */
	private boolean userInputRequired(){
		if(!inputEnabled){
			return false;
		}
		int selectStart = getSelectionStart();
		int selectEnd = getSelectionEnd();
		if(selectStart == selectEnd ){
			//no selection
			if(selectStart >= lockedDotPos){
				return true;
			}else{
				setCaretPosition(lockedDotPos);
				return false;
			}
		}
		int minPos = Math.min(selectStart, selectEnd);
		if(minPos < lockedDotPos){
			setCaretPosition(lockedDotPos);
			return false;
		}
		return true;
	}
	private static Pattern lineP = Pattern.compile("\\R");
	
	private void pasteEvent(){
		if(!userInputRequired()){
			return;
		}
		
		//get text from clip board
		Clipboard cb = getToolkit().getSystemClipboard();
		Transferable content = cb.getContents(this);
		String text = null;
		try {
			text = (String)content.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		if(text!=null){
			String[] insertLines = lineP.split(text);
			int start = getSelectionStart();
			int end = getSelectionEnd();
			int pos = Math.min(start, end);
			try {
				doc.remove(pos, start-end);
				lock.lock();
				doc.insertString(pos, insertLines[0], null);
				inputLineChange(true);
				lock.unlock();
				for(int i=1;i<insertLines.length;i++){
					lock.lock();
					System.out.println(lockedDotPos);
					doc.insertString(lockedDotPos, insertLines[i], null);
					if(i!=insertLines.length-1){
						inputLineChange(true);
					}
					lock.unlock();
					
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			
		}
		
	}
	
	public void disableInput(){
		inputEnabled = false;
		getCaret().setVisible(false);
	}
	
	public void enableInput(){
		inputEnabled = true;
		getCaret().setVisible(true);
	}
	
	/**
	 * When user change a line,this method should be called to save the line's text 
	 * into buffer and insert new line prefix.
	 */
	private void inputLineChange(){
		inputLineChange(true);
	}
	
	private void inputLineChange(boolean ensureBuffer){
		lock.lock();
		try {
//			Printer.print(lockedDotPos+"---"+(doc.getLength()-lockedDotPos));
			String line = doc.getText(lockedDotPos, doc.getLength()-lockedDotPos);
//			Printer.print(line);
			linesBuffer.add(line);
			
		} catch (BadLocationException ignore) {
		}
		if(ensureBuffer)
			ensureBufferSize(prefixInput.length()+1);
		String input = "\n"+prefixInput;
		append(input);
		lockAll();
		
		con_getter.signal();
		lock.unlock();
	}
	
	/**
	 * Ignore this operation.
	 */
	@Override
	public void setText(String t) {
		//do not support this kind of operation
	}
	
//	@Override
//	public void setDocument(Document doc) {
//		//disable this method 
//	}
	
	
	private void modifyLockPos(int pos){
		if(pos < lockedDotPos){
			return;
		}
		int dot = getCaretPosition();
//		if(dot<pos){
//			setCaretPosition(pos);
//		}
		int lt = lockedDotPos;
		lockedDotPos = pos;
		if(dot >= lt){
			//need shifting 
			int shift = pos - lt;
			setCaretPosition(Math.min(doc.getLength(), dot + shift));
		}
		
	}
	
	public void lockAll(){
		int end = doc.getLength();
//		setCaretPosition(end);
		modifyLockPos(end);
	}
	
	private void ensureBufferSize(int toAdd){
		if(toAdd>bufSize){
			throw new OutOfMemoryError("buffer size too small");
		}
		int end = doc.getLength();
		int sum = end + toAdd;
		if(sum > bufSize){
			//need to reduce first.
			//the caret and lock position should be moved
			int shift = Math.max(toAdd, end - bufReduce);
			int caret = Math.max(0, getCaretPosition()- shift);
			lockedDotPos = Math.max(0, lockedDotPos- shift);
			try {
				doc.remove(headLength, shift);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
			setCaretPosition(caret);
		}
	}
	
	
//	/**
//	 * 
//	 */
//	public void requireInput(){
//		append("\n");
//		lockAll();
//	}
	
	
	/**
	 * Insert a full output with '\n' at end.
	 * @param output
	 */
	private void insertOutput(String output){
		lock.lock();
		int inPos = lockedDotPos - prefixInput.length();
//		Printer.print(inPos);
		try {
			doc.insertString(inPos, output, null);
			modifyLockPos(lockedDotPos+output.length());
		} catch (BadLocationException e) {
		}
		ensureBufferSize(output.length());
		lock.unlock();
		
	}
	
	/**
	 * Add the String str as out put of a line.This String should not 
	 * contain any line separator.
	 * @param str
	 */
	public void outputLine(String str){
		
		if(str.contains("\n")){
			throw new IllegalArgumentException("For String:"+str);
		}
		insertOutput(prefixOutput+str+"\n");
	}
	
	
	private LinkedList<String> linesBuffer = new LinkedList<>();
	
	/**
	 * Get the next line from the input,this method will wait until the next line of input is available.
	 * @return
	 */
	public String nextLine(){
		lock.lock();
		if(linesBuffer.isEmpty()){
			try {
				con_getter.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		lock.lock();
		String line = linesBuffer.removeFirst();
		
		lock.unlock();
		return line;
	}
	
	/**
	 * Returns whether there are next line stored in the buffer,this method will only check 
	 * the current buffer and won't block the user. 
	 * @return
	 */
	public boolean hasNextLine(){
		return !linesBuffer.isEmpty();
	}
	
	public String getPrefixOutput() {
		return prefixOutput;
	}

	public void setPrefixOutPut(String prefixOutput) {
		this.prefixOutput = prefixOutput;
	}

	public String getPrefixInput() {
		return prefixInput;
	}

	public void setPrefixInput(String prefixInput) {
		this.prefixInput = prefixInput;
	}

	/**
	 * Set a print stream to this input text area.
	 */
	public Writer getOutputStream(){
		return out;
	}
	
	private final ShellOut out = new ShellOut();
	
	private class ShellOut extends Writer{
		private StringBuilder buf = new StringBuilder();
		boolean lineChanged = false;
		
		ShellOut(){
			appendPrefix();
		}
		
		private void appendPrefix(){
			buf.append(prefixOutput);
		}
		
		@Override
		public void write(char[] cbuf) throws IOException {
			buf.ensureCapacity(buf.length() + cbuf.length);
			for(char c : cbuf){
				buf.append(c);
				if(c == '\n'){
					buf.append(prefixOutput);
					lineChanged = true;
				}
			}
//			Printer.print("Writed");
		}
		
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			buf.ensureCapacity(buf.length() + len);
			for(int i=off;i<off+len;i++){
				char c = cbuf[i];
				buf.append(c);
				if(c == '\n'){
					buf.append(prefixOutput);
					lineChanged = true;
				}
			}
//			Printer.print("Writed");
		}
		@Override
		public void flush() throws IOException {
			if(lineChanged){
				//need to remove the last prefix 
				
				buf.delete(buf.length() - prefixOutput.length(), buf.length());
			}
			insertOutput(buf.toString());
			buf.delete(0,buf.length());
			appendPrefix();
//			Printer.print("flush");
		}
		@Override
		public void close() throws IOException {
			//close not supported,because no file is used.
		}
	}
	
	
	
}
