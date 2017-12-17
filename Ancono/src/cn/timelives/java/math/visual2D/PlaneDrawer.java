/**
 * 2017-11-15
 */
package cn.timelives.java.math.visual2D;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author liyicheng
 * 2017-11-15 18:54
 *
 */
public class PlaneDrawer {
	private List<DrawableCurve> fs = new ArrayList<>();
	
	private ImageProcessor processor = whiteBackground;
	
	private static final GeneralProcessor whiteBackground = 
			GeneralProcessor.getInstance(BackGroundDecorator.ofColor(Color.WHITE));
	static {
		whiteBackground.addBefore(new CoordinateDrawer());
	}
	
	/**
	 * 
	 */
	public PlaneDrawer() {
		
	}
	
	public boolean addCurve(Color c,SubstitutableCurve f) {
		return fs.add(
				new SimpleCurve(f,
						SimpleDrawPointPredicate.getDefault(),
						SimpleCurveColorer.ofColor(c)));
	}
	
	public boolean addCurve(Color c,SubstitutableCurve f,DrawPointPredicate pre) {
		return fs.add(
				new SimpleCurve(f,pre,SimpleCurveColorer.ofColor(c))
				);
	}
	
	public boolean addCurve(SubstitutableCurve f,DrawPointPredicate pre,CurveColorer colorer) {
		return fs.add(new SimpleCurve(f,pre,colorer));
	}
	
	public boolean addLeveled(Color c,SubstitutableCurve f,double[] levels) {
		return fs.add(new SimpleCurve(f,LeveledPredicate.getLeveledPrecidate(levels),SimpleCurveColorer.ofColor(c)));
	}
	
	public boolean addLeveled(SubstitutableCurve f,double[] levels,CurveColorer colorer) {
		return fs.add(new SimpleCurve(f,LeveledPredicate.getLeveledPrecidate(levels),colorer));
	}
	
	public boolean addLeveled(Color c,SubstitutableCurve f,double[] levels,Color[] colors) {
		return fs.add(new SimpleCurve(f,
				LeveledPredicate.getLeveledPrecidate(levels),
				MapColorer.getLeveled(levels, colors)));
	}
	
	public boolean addCurve(DrawableCurve curve) {
		return fs.add(Objects.requireNonNull(curve));
	}
	
	/**
	 * 
	 * @param rect
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage draw(Rectangle2D.Double rect,int width,int height) {
		if(rect.isEmpty()) {
			throw new IllegalArgumentException();
		}
		double dx = rect.width / width;
		double dy = rect.height / height;
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		if(processor != null) {
			image = processor.beforeDrawing(image,g, rect);
		}
		for(DrawableCurve d : fs) {
			d.assignWorkingArea(rect, width, height, dx, dy);
		}
		
		for(int j=height-1;j>-1;j--) {
			for(int i=0;i<width;i++) {
				for(DrawableCurve df : fs) {
					if(df.shouldDraw(i, j)) {
						df.drawPoint(image, g, i, height-j-1);
						break;
					}
				}
			}
		}
		for(DrawableCurve d : fs) {
			d.clear();
		}
		
		if(processor != null) {
			image = processor.afterDrawing(image,g, rect);
		}
		g.dispose();
		return image;
	}
	
//	public static void main(String[] args) throws IOException {
//		MathCalculator<Double> mc = MathCalculatorAdapter.getCalculatorDouble();
//		ConicSection<Double> ell = EllipseV.createEllipse(55d, 45d, mc).transform(PAffineTrans.ofTranslation(
//				PVector.valueOf(0d, 5d, mc)));
//		//
////		print(ell);
//		PlaneDrawer pd = new PlaneDrawer();
//		DrawableCurve curve = SimpleCurve.of(Utilities.mapToDouble(x -> x, x -> x, ell), 
//				SimpleDrawPointPredicate.getDefault(), SimpleCurveColorer.ofColor(Color.ORANGE),1);
//		pd.addCurve(curve);
//		curve = SimpleCurve.of((x,y)-> x*x + y*y - 5, 
//				SimpleDrawPointPredicate.getDefault(), SimpleCurveColorer.ofColor(Color.BLUE),2);
//		pd.addCurve(curve);
////		pd.addCurve(Color.RED,  (x,y)->{
////			return Math.abs(MathUtils.tschebyscheffDistance(x,y,-2,0) - MathUtils.tschebyscheffDistance(x,y,2,0)) - 3; 
////		});
//		
//		Timer t = new Timer();
//		t.start();
//		BufferedImage image = pd.draw(new Rectangle2D.Double(-100,-100,200,200), 1000, 1000);
//		ImageIO.write(image, "png", new File("C:\\Users\\liyicheng\\Desktop\\新建文件夹\\test.png"));
//		print(t.end());
//		print("Finished !");
//	}
}
