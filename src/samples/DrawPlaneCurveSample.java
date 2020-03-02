package samples;

import cn.ancono.math.geometry.analytic.planeAG.PlaneAgUtils;
import cn.ancono.math.geometry.analytic.planeAG.Point;
import cn.ancono.math.geometry.analytic.planeAG.Segment;
import cn.ancono.math.geometry.visual.visual2D.PlaneDrawer;
import cn.ancono.math.numberModels.Calculators;
import cn.ancono.utilities.EasyCanvas;
import cn.ancono.utilities.ZoomingPlugin;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/*
 * Created by lyc at 2020/3/1
 */
public class DrawPlaneCurveSample {
    public static void sample1() {
        var mcd = Calculators.getCalDouble();
		/*
		high school entrance exam real exercise:
		2011.23
		 */
        PlaneDrawer drawer = new PlaneDrawer();
        var A = Point.valueOf(1d, 3d, mcd);
        var B = Point.valueOf(1d, 0d, mcd);
        var C = Point.valueOf(-1d, 3d, mcd);
        var D = Point.valueOf(-1d, 0d, mcd);
        var AB = Segment.twoPoints(A, B);
        var CD = Segment.twoPoints(C, D);
        drawer.addCurve(Color.BLUE, PlaneAgUtils.mapToDouble(AB));
        drawer.addCurve(Color.BLUE, PlaneAgUtils.mapToDouble(CD));
        drawer.addCurve(Color.RED, (x, y) -> {
            var p = Point.valueOf(x, y, mcd);
            return AB.distanceSq(p) - CD.distanceSq(p);
        });
        Rectangle2D.Double rect = new Rectangle2D.Double(-5, -5, 10, 10);

        EasyCanvas canvas = new EasyCanvas(500.0, 500.0, "Image");
        var image = drawer.draw(rect, 500, 500);
        canvas.drawImage(image, 0, 0);
        canvas.show();
    }

    public static void main(String[] args) {
        sample1();
    }
}
