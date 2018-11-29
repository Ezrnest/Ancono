package cn.timelives.java.math.geometry.visual.visual2D;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Objects;
import java.util.function.DoubleFunction;
import java.util.function.Function;

import static cn.timelives.java.utilities.Printer.print;

/*
 * Created at 2018/11/13 11:40
 * @author liyicheng
 */
public class ParaCurveDrawer {
    private double pixelMinDist, pixelMaxDist;
    private double downer,upper;
    private ParametricCurve curve;



    private float stroke = 2;
    private Function<double[],Color> colorer;
    private Color curveColor = Color.RED;
    private static Function<double[],Color> DEFAULT_COLORER = x -> Color.RED;

    ParaCurveDrawer(ParametricCurve curve){
        pixelMinDist = 2;
        pixelMaxDist = 5;
        this.curve = curve;
        downer = curve.downerBound();
        upper = curve.upperBound();
    }

    public void setPixelMinDist(double pixelMinDist) {
        this.pixelMinDist = pixelMinDist;
    }

    public void setPixelMaxDist(double pixelMaxDist) {
        this.pixelMaxDist = pixelMaxDist;
    }

    public void setDowner(double downer) {
        this.downer = downer;
    }

    public void setUpper(double upper) {
        this.upper = upper;
    }

    public void setStroke(float stroke) {
        this.stroke = stroke;
    }

    /**
     * Sets the colorer of this parametric curve. The parameter of the colorer is
     * an array of parameter value, x coordinate and y coordinate in order.
     * @param colorer
     */
    public void setColorer(Function<double[],Color> colorer) {
        this.colorer = Objects.requireNonNull(colorer);
        curveColor = null;
    }

    public void setCurveColor(Color c){
        curveColor = Objects.requireNonNull(c);
    }

    private int toCanvasX(Point2D.Double p, Rectangle2D.Double rect, int width){
        return (int) ((p.x-rect.x)/rect.width * width);
    }

    private int toCanvasY(Point2D.Double p, Rectangle2D.Double rect, int height){
        return (int) ((p.y-rect.y)/rect.height * height);
    }

    public void draw(Graphics2D g, Rectangle2D.Double rect, int width, int height,double dx,double dy){
        double dt = Math.min(dx,dy);
        double t = downer;
        Point2D.Double p = curve.substitute(downer);
        int cx= toCanvasX(p,rect,width),cy= toCanvasY(p,rect,height);
        g.setStroke(new BasicStroke(stroke));
        if(curveColor!=null){
            g.setColor(curveColor);
        }
        while(t <= upper){
            int count =0 ;
            while(!rect.contains(p)){
                t+=dt;
                p = curve.substitute(t);
                cx = toCanvasX(p,rect,width);
                cy = toCanvasY(p,rect,height);
                count++;
                if(count > 100){
                    print("!");
                }
            }
            while(true){
                count++;
                if(count > 100){
                    print("!");
                }
                double nt = t + dt;
                var np = curve.substitute(nt);
                int ncx = toCanvasX(np,rect,width);
                int ncy = toCanvasY(np,rect,height);
                double dist = Math.hypot(cx - ncx,cy-ncy);
                if(dist < pixelMinDist){
                    dt *= 2;
                }else if(dist > pixelMaxDist){
                    dt = dt * pixelMinDist/ 2 / dist;
                    continue;
                }
                if(curveColor==null){
                    g.setColor(colorer.apply(new double[]{t,p.x,p.y}));
                }
                g.drawLine(cx,cy,ncx,ncy);
                p = np;
                cx = ncx;
                cy = ncy;
                t = nt;
                break;
            }
        }
    }
}
