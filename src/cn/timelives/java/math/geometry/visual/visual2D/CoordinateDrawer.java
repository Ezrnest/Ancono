/**
 * 2017-11-18
 */
package cn.timelives.java.math.geometry.visual.visual2D;

import cn.timelives.java.math.MathUtils;

import java.awt.*;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * Draws the coordinate axis.
 * @author liyicheng
 * 2017-11-18 13:16
 *
 */
public class CoordinateDrawer implements ImageDecorator {
	private final Color axisColor,textColor;
	private boolean enableNumber;
	private Font font;
	private int markLengthDivider = 150;
	private int markGapDivider = 50;
	/**
	 * 
	 */
	public CoordinateDrawer() {
		axisColor = Color.BLACK;
		textColor = Color.BLACK;
		enableNumber = true;
		font = Font.decode("Dialog-15");
		
	}

	/*
	 * @see cn.timelives.java.math.visual.ImageDecorator2D#process(java.awt.image.BufferedImage, java.awt.geom.Rectangle2D.Double)
	 */
	@Override
	public BufferedImage process(BufferedImage image,Graphics2D g, Double rect) {
			//draw x axis
		drawX(image,g,rect);
		drawY(image,g,rect);
		drawO(image,g,rect);
		return image;
	}
	
	private void drawX(BufferedImage image,Graphics2D g,Double rect) {
		final int height = image.getHeight(),
				width = image.getWidth();
		final int y0 = height-toPixelCoorY(0,rect,height);
		if(y0 < 0 || y0>= height) {
			return;
		}
		g.setColor(axisColor);
		final int markLength = Math.min(width, height)/ markLengthDivider;
		g.drawLine(0, y0, width, y0);
		
		double gap = calGap(rect.width*markGapDivider/ width);
		double xt = MathUtils.maxBelow(rect.x,gap);
		double max = rect.getMaxX();
		DecimalFormat df = decideFormat(rect.x, max);
		g.setFont(font);
		g.setColor(textColor);
		while(xt < max) {
			int x = toPixelCoorX(xt, rect, width);
			if(x>-1 && x<width) {
				g.drawLine(x, y0, x, y0-markLength);
//				g.setFont(null);
				if(enableNumber && Math.abs(xt)*10>gap) {
					String text = df.format(xt);
					g.drawString(text, x-(text.length()*font.getSize())/4, y0+font.getSize());
				}
			}
			xt += gap;
		}
		g.setColor(axisColor);
		int t = width-markLength*2;
		g.fillPolygon(new int[] {width,t,t}, new int[] {y0,y0+markLength,y0-markLength}, 3);
		g.setColor(textColor);
		g.drawString("x", width-font.getSize(), y0+font.getSize());
	}
	
	private void drawY(BufferedImage image,Graphics2D g,Double rect) {
		final int height = image.getHeight(),
				width = image.getWidth();
		final int x0 = toPixelCoorX(0,rect,width);
		if(x0 < 0 || x0>= width) {
			return;
		}
		g.setColor(axisColor);
		final int markLength = Math.min(width, height)/ markLengthDivider;
		g.drawLine(x0, 0, x0, height);
		
		double gap = calGap(rect.height*markGapDivider/ height);
		double yt = MathUtils.maxBelow(rect.y,gap);
		double max = rect.getMaxY();
		DecimalFormat df = decideFormat(rect.y, max);
		g.setFont(font);
		g.setColor(textColor);
		while(yt < max) {
			int y = height - toPixelCoorY(yt, rect, height);
			if(y>-1 && y<height) {
				g.drawLine(x0,y , x0+markLength, y);
//				g.setFont(null);
				if(enableNumber && Math.abs(yt)*10>gap) {
					String text = df.format(yt);
					g.drawString(text, x0-(text.length()*font.getSize())/2-1, y+font.getSize()/2);
				}
			}
			yt += gap;
		}
		g.setColor(axisColor);
		g.fillPolygon(new int[] {x0,x0-markLength,x0+markLength}, 
				new int[] {0,2*markLength,2*markLength}, 3);
		g.setColor(textColor);
		g.drawString("y", x0-(font.getSize())/2-1,font.getSize()*2);
	}
	
	private void drawO(BufferedImage image,Graphics2D g,Double rect) {
		final int height = image.getHeight(),
				width = image.getWidth();
		final int x0 = toPixelCoorX(0,rect,width);
		final int y0 = toPixelCoorY(0,rect,height);
		g.setColor(textColor);
		g.setFont(font);
		g.drawString("O", x0-font.getSize()-1, y0+font.getSize());
	}
	
	private int toPixelCoorX(double x,Double rect,int width) {
		return (int) ((x-rect.x)*width/rect.width);
	}
	private int toPixelCoorY(double y,Double rect,int height) {
		return (int) ((y-rect.y)*height/rect.height);
	}
	
	private static DecimalFormat decideFormat(double x1,double x2){
		x1 = Math.abs(x1);
		x2 = Math.abs(x2);
		if(Math.max(x1, x2)>=MAX_PLAIN){
			return df2;
		}
		if(Math.min(x1, x2)<=MIN_PLAIN){
			return df2;
		}
		return df1;
	}
	private static final DecimalFormat df1 = new DecimalFormat("0");
	private static final DecimalFormat df2 = new DecimalFormat("0.#E0");
	private static final double MAX_PLAIN = 950,MIN_PLAIN = 1/950;
	
	private static final double MIN_GAP = 1E-10;
	
	private static double calGap(double t){
		double gap = MIN_GAP;
		while(gap < t){
			gap *= 5;
			if(gap >t)
				break;
			gap *= 2;
		}
//		Printer.print("GAP="+gap);
		return gap;
	}
}
