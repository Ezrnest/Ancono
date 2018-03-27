package cn.timelives.java.utilities.swingTools;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
/**
 * An MouseListener which drag the given JScrollPane by the Mouse.
 * To complete the dragging,some values are necessary.
 * When the mouse is dragged in the component,the mouse's movement in the component will be reflected to 
 * the scroll in the JScrollPane.<p>
 * For example,assume max as the JScrollBar's maximum value, dx as the horizontal moving length of the mouse , 
 * xSize as the component's width, k as the double factor has been set. So the new value of the JScrollBar 
 * will be {@code oldValue + k*dx*max/xSize}<p>
 * You may use this class like this:
 * <pre>
 * new ScorllPaneDragger(scrollPane,scrollPane);
 * </pre>
 * All the listener adding things will be done by this object and you 
 * don't need to add again
 * @author lyc
 *
 */
public class ScrollPaneDragger extends MouseAdapter{
	/**
	 * the JScrollPane to drag
	 */
	private JScrollPane scrollPane;
	/**
	 * the horizontal ScrollBar
	 */
	private JScrollBar xBar ;
	/**
	 * the vertical ScrollBar
	 */
	private JScrollBar yBar ;
	private boolean dragging = false;
	private Point startP;
	
	/**
	 * the factor, the default value is 1.
	 */
	private double k;
	
	/**
	 * the mouse will drag in it
	 */
	private JComponent container;
	/**
	 * decide whether x movement will be considered
	 */
	private boolean moveX = true;
	/**
	 * decide whether y movement will be considered
	 */
	private boolean moveY = true;
	/**
	 * Create the ScrollPaneDragger ,
	 * @param sp the JScrollPane to drag
	 * @param container the container in which the mouse moves,
	 * notice that it should be the component listened
	 */
	public ScrollPaneDragger(JScrollPane sp,JComponent container){
		this(sp,container,1);
	}
	/**
	 * Create the ScrollPaneDragger , k will be the factor multiplied to the movement of the Mouse
	 * @param sp the JScrollPane to drag
	 * @param container the container in which the mouse moves,
	 * notice that it should be the component listened
	 * @param k the factor multiplied
	 */
	public ScrollPaneDragger(JScrollPane sp,JComponent container,double k){
		this.scrollPane = sp;
		sp.addMouseListener(this);
		sp.addMouseMotionListener(this);
		xBar = sp.getHorizontalScrollBar();
		yBar = sp.getVerticalScrollBar();
		this.container = container;
		this.k = k;
		
	}
	/**
	 * set whether x and y movement will be considered,If false is set,the corresponding movement will be ignored
	 * @param isXMoved whether x movement will be considered
	 * @param isYMoved whether y movement will be considered
	 */
	public void setMovement(boolean isXMoved,boolean isYMoved){
		moveX = isXMoved;
		moveY = isYMoved;
	}
	
	
	@Override
	public void mousePressed(MouseEvent e) {
		startP = e.getPoint();
		scrollPane.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		dragging = true;
	}
	
	@Override
	public void mouseReleased(MouseEvent e){
		dragging = false;
		scrollPane.setCursor(Cursor.getDefaultCursor());
	}
	
	@Override
	public void mouseDragged(MouseEvent e){
		if(dragging){
			Point endP = e.getPoint();
			int dx = endP.x - startP.x;
			int dy = endP.y - startP.y;
			int xBarMax = xBar.getMaximum();
			int yBarMax = yBar.getMaximum();
			Dimension size = container.getSize();
			if(moveX)
				xBar.setValue(xBar.getValue()-(int)(k*dx*xBarMax/size.width));
			if(moveY)
				yBar.setValue(yBar.getValue()-(int)(k*dy*yBarMax/size.width));
			startP = endP;
		}
	}
	
	
}
