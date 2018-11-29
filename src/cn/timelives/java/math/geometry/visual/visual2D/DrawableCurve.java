/**
 * 2017-11-16
 */
package cn.timelives.java.math.geometry.visual.visual2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author liyicheng
 * 2017-11-16 20:47
 *
 */
public interface DrawableCurve {
	double compute(double x, double y);
	
	default double compute(Point2D.Double p) {
		return compute(p.x,p.y);
	}
	
	
	
	void drawPoint(BufferedImage image, Graphics2D g, int i, int j);
	
	/**
	 * Determines whether the specific point should be drew.
	 * @param i
	 * @param j
	 * @return
	 */
    boolean shouldDraw(int i, int j);
	
	/**
	 * Assign the working area of the curve.
	 * This method must be called first.
	 * @param rect the rectangle in the real coordinate system
	 * @param width the width of the image to draw
	 * @param height the height of the image to draw
	 * @param dx the corresponding length of a horizontal pixel in the plane
	 * @param dy the corresponding length of a vertical pixel in the plane
	 */
    void assignWorkingArea(Rectangle2D.Double rect, int width, int height, double dx, double dy);
	
	
	/**
	 * Calls this to clear up. {@code assignWorkingArea()} must be called 
	 * before it is used again.
	 */
    void clear();
}
