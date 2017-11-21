/**
 * 2017-11-18
 */
package cn.timelives.java.math.visual2D;

import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * @author liyicheng
 * 2017-11-18 16:15
 *
 */
public interface ImageProcessor {
	
	public BufferedImage beforeDrawing(BufferedImage image,Graphics2D g,Rectangle2D.Double rect);
	
	public BufferedImage afterDrawing(BufferedImage image,Graphics2D g,Rectangle2D.Double rect);
}
