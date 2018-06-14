/**
 * 2017-11-17
 */
package cn.timelives.java.math.geometry.visual.visual2D;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;


/**
 * @author liyicheng
 * 2017-11-17 18:33
 *
 */
public class SimpleCurve extends BufferedCurve {
	protected final DrawPointPredicate pre;
	protected final CurveColorer colorer;
	/**
	 * Radius - 1
	 */
	protected final int strokeRadius;
	/**
	 * @param f
	 */
	public SimpleCurve(SubstitutableCurve f,DrawPointPredicate pre,CurveColorer colorer,int strokeRadius) {
		super(f);
		this.pre = Objects.requireNonNull(pre);
		this.colorer = Objects.requireNonNull(colorer);
		if(strokeRadius<=0) {
			throw new IllegalArgumentException("r<1");
		}
		this.strokeRadius = strokeRadius-1;
	}
	/**
	 * @param f
	 */
	public SimpleCurve(SubstitutableCurve f,DrawPointPredicate pre,CurveColorer colorer) {
		this(f,pre,colorer,1);
	}
	
	/*
	 * @see cn.timelives.java.math.geometry.visual.visual2D.DrawableCurve#drawPoint(java.awt.image.BufferedImage, java.awt.Graphics, int, int)
	 */
	@Override
	public void drawPoint(BufferedImage image, Graphics2D g, int i, int j) {
		Color c = colorer.getColor(i+1, j+1, data);
		if(strokeRadius == 0){
			image.setRGB(i, j, c.getRGB());
		}else {
			int t = strokeRadius*2;
			g.setColor(c);
			g.fillArc(i-strokeRadius, j-strokeRadius, t, t, 0, 360);
		}
	}

	/*
	 * @see cn.timelives.java.math.visual.DrawableCurve#shouldDraw(int, int)
	 */
	@Override
	public boolean shouldDraw(int i, int j) {
		return pre.shouldDraw(data, i+1, j+1);
	}
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((f == null) ? 0 : f.hashCode());
		result = prime * result + ((pre == null) ? 0 : pre.hashCode());
		return result;
	}
	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof SimpleCurve))
			return false;
		SimpleCurve other = (SimpleCurve) obj;
		if (f == null) {
			if (other.f != null)
				return false;
		} else if (!f.equals(other.f))
			return false;
		if (pre == null) {
			if (other.pre != null)
				return false;
		} else if (!pre.equals(other.pre))
			return false;
		return true;
	}
	
	public static SimpleCurve of(SubstitutableCurve f,Color c) {
		return of(f,SimpleDrawPointPredicate.getDefault(),c);
		
	}
	public static SimpleCurve of(SubstitutableCurve f,DrawPointPredicate pre,Color c) {
		return of(f,pre,SimpleCurveColorer.ofColor(c));
	}
	public static SimpleCurve of(SubstitutableCurve f,DrawPointPredicate pre,CurveColorer c) {
		return new SimpleCurve(f, pre, c);
	}
	public static SimpleCurve of(SubstitutableCurve f,DrawPointPredicate pre,CurveColorer c,int strokeSize) {
		return new SimpleCurve(f, pre, c,strokeSize);
	}
	public static SimpleCurve leveled(SubstitutableCurve f,double[] levels,CurveColorer colorer) {
		return new SimpleCurve(f,LeveledPredicate.getLeveledPrecidate(levels),colorer);
	}
	
	public static SimpleCurve leveled(SubstitutableCurve f,double[] levels,Color[] colors) {
		LeveledPredicate pre = LeveledPredicate.getLeveledPrecidate(levels);
		CurveColorer c = LevelColorer.getLeveled(pre, colors);
		return new SimpleCurve(f,pre,c);
	}
	
}
