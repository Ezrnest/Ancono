package cn.timelives.java.utilities.swingTools;

import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.JTextComponent;
/**
 * A caret that is shown like a _ .
 * @author lyc
 *
 */
public class CaretUnderline extends DefaultCaret {
	
	static final int caret_height = 3;
	static final int caret_width = 8;
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1586407028497413875L;
	
	
	public CaretUnderline() {
		super.setBlinkRate(500);
	}
	
	@Override
	protected synchronized void damage(Rectangle r) {
		if(r == null) {
			return;
		}
		reloadPosition(r);
		repaint();
	}
	protected void reloadPosition(Rectangle r) {
		x = r.x;
		y = calculateYshifted(r.y, r.height);
		width = caret_width;
		height = caret_height;
	}
	
	private int calculateYshifted(int y,int height){
		return y + height - caret_height;
	}
	
	@Override
	public void paint(Graphics g) {
		JTextComponent component = getComponent();
		if(component == null) {
			return;
		}
		Rectangle r = null;
		try {
			r =component.modelToView(getDot());
		}catch(BadLocationException e) {
			return;
		}
		if(r == null) {
			return;
		}
		if( x != r.x || y != calculateYshifted(r.y,r.height)) {
			repaint();
			reloadPosition(r);
		}
		if(isVisible()) {
			g.setColor(component.getCaretColor());
			g.fillRect(x, y, width, height);
		}
	}
}
