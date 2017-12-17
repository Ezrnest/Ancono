/**
 * 2017-11-16
 */
package cn.timelives.java.math.visual2D;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author liyicheng
 * 2017-11-16 20:47
 *
 */
public interface DrawableCurve {
	public double compute(double x,double y);
	
	public default double compute(Point2D.Double p) {
		return compute(p.x,p.y);
	}
	
	
	
	public void drawPoint(BufferedImage image,Graphics2D g,int i,int j);
	
	/**
	 * Determines whether the specific point should be drew.
	 * @param i
	 * @param j
	 * @return
	 */
	public boolean shouldDraw(int i,int j);
	
	/**
	 * Assign the working area of the curve.
	 * This method must be called first.
	 * @param rect the rectangle in the real coordinate system
	 * @param width the width of the image to draw
	 * @param height the height of the image to draw
	 * @param dx 
	 * @param dy
	 */
	public void assignWorkingArea(Rectangle2D.Double rect,int width,int height,double dx,double dy);
	
	
	/**
	 * Calls this to clear up. {@code assignWorkingArea()} must be called 
	 * before it is used again.
	 */
	public void clear();
}
