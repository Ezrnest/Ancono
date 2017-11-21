/**
 * 2017-11-18
 */
package cn.timelives.java.math.visual2D;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author liyicheng
 * 2017-11-18 13:15
 *
 */
public interface ImageDecorator {
	
	/**
	 * Decorates this image. 
	 * @param image an image to draw into
	 * @param rect the rectangle in the actual coordinate system. 
	 * @return
	 */
	public BufferedImage process(BufferedImage image,Graphics2D g,Rectangle2D.Double rect);
	
}
