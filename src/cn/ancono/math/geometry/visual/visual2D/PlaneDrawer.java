/**
 * 2017-11-15
 */
package cn.ancono.math.geometry.visual.visual2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static cn.ancono.utilities.Printer.print;

/**
 * @author liyicheng
 * 2017-11-15 18:54
 */
public class PlaneDrawer {
    private List<DrawableCurve> fs = new ArrayList<>();
    private List<ParaCurveDrawer> ps = new ArrayList<>();
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

    public boolean addCurve(Color c, SubstitutableCurve f) {
        return fs.add(
                new SimpleCurve(f,
                        SimpleDrawPointPredicate.getDefault(),
                        SimpleCurveColorer.ofColor(c)));
    }

    public boolean addCurve(Color c, SubstitutableCurve f, DrawPointPredicate pre) {
        return fs.add(
                new SimpleCurve(f, pre, SimpleCurveColorer.ofColor(c))
        );
    }

    public boolean addCurve(SubstitutableCurve f, DrawPointPredicate pre, CurveColorer colorer) {
        return fs.add(new SimpleCurve(f, pre, colorer));
    }

    public boolean addLeveled(Color c, SubstitutableCurve f, double[] levels) {
        return fs.add(new SimpleCurve(f, LeveledPredicate.getLeveledPrecidate(levels), SimpleCurveColorer.ofColor(c)));
    }

    public boolean addLeveled(SubstitutableCurve f, double[] levels, CurveColorer colorer) {
        return fs.add(new SimpleCurve(f, LeveledPredicate.getLeveledPrecidate(levels), colorer));
    }

    public boolean addLeveled(Color c, SubstitutableCurve f, double[] levels, Color[] colors) {
        return fs.add(new SimpleCurve(f,
                LeveledPredicate.getLeveledPrecidate(levels),
                MapColorer.getLeveled(levels, colors)));
    }

    public boolean addCurve(DrawableCurve curve) {
        return fs.add(Objects.requireNonNull(curve));
    }

    public void addPCurve(ParametricCurve pc) {
        ps.add(new ParaCurveDrawer(pc));
    }

    /**
     * @param rect
     * @param width  the width of the image, in pixel
     * @param height the height of the image, in pixel
     * @return
     */
    public BufferedImage draw(Rectangle2D.Double rect, int width, int height) {
        if (rect.isEmpty()) {
            throw new IllegalArgumentException();
        }
        double dx = rect.width / width;
        double dy = rect.height / height;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();
        if (processor != null) {
            image = processor.beforeDrawing(image, g, rect);
        }
        for (DrawableCurve d : fs) {
            d.assignWorkingArea(rect, width, height, dx, dy);
        }

        for (int j = height - 1; j > -1; j--) {
            for (int i = 0; i < width; i++) {
                for (DrawableCurve df : fs) {
                    if (df.shouldDraw(i, j)) {
                        df.drawPoint(image, g, i, height - j - 1);
                        break;
                    }
                }
            }
        }
        for (DrawableCurve d : fs) {
            d.clear();
        }

        for (var p : ps) {
            p.draw(g, rect, width, height, dx, dy);
        }

        if (processor != null) {
            image = processor.afterDrawing(image, g, rect);
        }
        g.dispose();
        return image;
    }

    public boolean drawToFile(Rectangle2D.Double rect, int width, int height, File file) {
        BufferedImage image = draw(rect, width, height);
        String format = "jpg";
        {
            String name = file.getName();
            int index = name.lastIndexOf('.');
            if (index > -1 && index < name.length() - 1) {
                String f = name.substring(index + 1);
                if (!f.isEmpty()) {
                    format = f;
                }
            }
        }
        try {
            return ImageIO.write(image, format, file);
        } catch (IOException e) {
            return false;
        }
    }

    public boolean drawToFile(Rectangle2D.Double rect, int width, int height, String path) {

        return drawToFile(rect, width, height, new File(path));
    }

    public static void main(String[] args) throws IOException {
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

//        MathCalculator<Double> mc = Calculators.getCalculatorDoubleDev();
        Random rd = new Random();
        for (int i = 0; i < 100; i++) {
            PlaneDrawer pd = new PlaneDrawer();
            double A1 = rd.nextDouble() + 1;
            double A2 = 2 * rd.nextDouble() + 1;
            double w1 = rd.nextDouble() * 4;
            double w2 = w1 * (rd.nextInt());
            //double basis = (1 + rd.nextDouble()) / 50;
            //            double w1 = basis * (1 + rd.nextInt(100));
            //            double w2 = basis * (1 + rd.nextInt(100));
            double p1 = rd.nextDouble() * 4;
            double p2 = rd.nextDouble();
            pd.addPCurve(ParametricCurve.from(t -> new Point2D.Double(A1 * Math.sin(w1 * t + p1), A2 * Math.cos(w2 * t + p2)), 0, 300));
            String name = String.format("C:\\Users\\14037\\Desktop\\temp\\curve%d.jpg", i);
            BufferedImage buf = pd.draw(new Rectangle2D.Double(-3, -3, 6, 6), 1000, 1000);
            var g = buf.getGraphics();
            g.setColor(Color.BLACK);
            g.setFont(Font.decode("consolas-18"));
            g.drawString(String.format("x=%3fsin(%3f*t+%3f)", A1, w1, p1), 20, 20);
            g.drawString(String.format("y=%3fcos(%3f*t+%3f)", A2, w2, p2), 20, 40);
            g.drawString("t from 0 to 300", 20, 60);
            ImageIO.write(buf, "jpg", new File(name));
            print("Success! " + i);
        }

    }
}
