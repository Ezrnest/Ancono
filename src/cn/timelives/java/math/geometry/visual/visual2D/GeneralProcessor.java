/**
 * 2017-11-18
 */
package cn.timelives.java.math.geometry.visual.visual2D;

import java.awt.*;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author liyicheng
 * 2017-11-18 16:16
 *
 */
public final class GeneralProcessor implements ImageProcessor {
	private List<ImageDecorator> before,
			after;
	/**
	 * 
	 */
	GeneralProcessor() {
	}
	
	GeneralProcessor(List<ImageDecorator> before,List<ImageDecorator> after) {
		this.before = before;
		this.after = after;
	}
	
	/**
	 * Adds an ImageDecorator2D to this processor's 'before' group, the added ImageDecorator2D will 
	 * process the image before the drawing and it will be the last to
	 * process in the 'before' group(if no else is added).
	 * @param id
	 */
	public void addBefore(ImageDecorator id) {
		if(before == null) {
			before = new ArrayList<>();
		}
		before.add(id);
	}
	/**
	 * Adds an ImageDecorator2D to this processor's 'after' group, the added ImageDecorator2D will
	 * process the image after the drawing and it will be the last to
	 * process in the 'after' group(if no else is added).
	 * @param id
	 */
	public void addAfter(ImageDecorator id) {
		if(after == null) {
			after = new ArrayList<>();
		}
		after.add(id);
	}
	
	public GeneralProcessor clone() {
		GeneralProcessor g = new GeneralProcessor();
		if(before !=null) {
			g.before = new ArrayList<>(before);
		}
		if(after !=null) {
			g.after = new ArrayList<>(after);
		}
		return g;
	}
	
	/*
	 * @see cn.timelives.java.math.visual.ImageProcessor2D#beforeDrawing(java.awt.image.BufferedImage, java.awt.geom.Rectangle2D.Double)
	 */
	@Override
	public BufferedImage beforeDrawing(BufferedImage image,Graphics2D g, Double rect) {
		if(before != null) {
			for(ImageDecorator d : before) {
				image = d.process(image,g, rect);
			}
		}
		return image;
	}

	/*
	 * @see cn.timelives.java.math.visual.ImageProcessor2D#afterDrawing(java.awt.image.BufferedImage, java.awt.geom.Rectangle2D.Double)
	 */
	@Override
	public BufferedImage afterDrawing(BufferedImage image,Graphics2D g, Double rect) {
		if(after != null) {
			for(ImageDecorator d : after) {
				image = d.process(image,g, rect);
			}
		}
		return image;
	}
	
	
	
	public static GeneralProcessor getInstance() {
		return new GeneralProcessor();
	}
	public static GeneralProcessor getInstance(ImageDecorator before) {
		GeneralProcessor g = new GeneralProcessor();
		g.addBefore(before);
		return g;
	}
	
	public static GeneralProcessor getInstance(ImageDecorator before,ImageDecorator after) {
		GeneralProcessor g = new GeneralProcessor();
		g.addBefore(before);
		g.addAfter(after);
		return g;
	}
	

}
