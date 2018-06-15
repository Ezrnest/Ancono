/**
 * 2017-04-14
 */
package test.math.comp.planeAg;

import cn.timelives.java.math.MathCalculator;
import cn.timelives.java.math.geometry.analytic.planeAG.Line;
import cn.timelives.java.math.geometry.analytic.planeAG.PVector;
import cn.timelives.java.math.geometry.analytic.planeAG.Point;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.timelives.java.utilities.Printer.print;

/**
 * 
 * @author liyicheng
 * 2017-04-14 20:18
 *
 */
public class PlaneObjectHandler<T> {
	private Map<String,Point<T>> pmap;
	private Map<String,Line<T>> lmap;
	private Map<String,PVector<T>> vmap;
	private Map<String,T> nmap;
	private Function<String,T> parser;
	private MathCalculator<T> mc;
	private List<Map<?,?>> maps;
	/**
	 * 
	 */
	public PlaneObjectHandler(Function<String,T> parser,MathCalculator<T> mc) {
		pmap = new HashMap<>();
		lmap = new HashMap<>();
		vmap = new HashMap<>();
		nmap = new HashMap<>();
		maps = new ArrayList<>();
		maps.add(pmap);
		maps.add(lmap);
		maps.add(vmap);
		maps.add(nmap);
		this.parser = parser;
		this.mc = mc;
	}
	
	boolean showInput = true;
	
	/**
	 * Gets the showInput.
	 * @return the showInput
	 */
	public boolean isShowInput() {
		return showInput;
	}
	/**
	 * Sets the showInput.
	 * @param showInput the showInput to set
	 */
	public void setShowInput(boolean showInput) {
		this.showInput = showInput;
	}

	private static String VERTEX_REGEX = "\\(([^\\,]+)\\,([^\\,]+)\\)";
	static final Pattern INPUTPATTERN = Pattern.compile(" ?([^\\|]+) ?(\\|)?"),
						POINT_PATTERN = Pattern.compile("[pP]\\:([^\\|]+)"),
						POINT_PATTERN2 = Pattern.compile("(\\w+)"+VERTEX_REGEX),
						LINE_PATTERN = Pattern.compile("[lL]:(\\w+)"+VERTEX_REGEX+"[vV]"+VERTEX_REGEX),
						VECTOR_PATTERN = Pattern.compile("[vV]:(\\w+)"+VERTEX_REGEX),
						NUMBER_PATTERN = Pattern.compile("(\\w)=(.+)");
	public Point<T> point(String name){
		return (Point<T>) pmap.get(name);
	}
	public Line<T> line(String name){
		return (Line<T>) lmap.get(name);
	}
	public PVector<T> vector(String name){
		return (PVector<T>) vmap.get(name);
	}
	private CodePlace cp = null;
	public void giveCodePlane(CodePlace cp){
		this.cp = cp;
	}
	
	public void clear(){
		pmap.clear();
		lmap.clear();
		vmap.clear();
	}
	
	/**
	 * Point
	 * <pre>p:A(x,y)</pre>
	 * Line
	 * <pre>l:a(x,y)v(vx,vy)</pre>
	 * Vector
	 * <pre>v:v(x,y)</pre>
	 * Number
	 * <pre>x=a</pre>
	 * @param expr
	 */
	public void input(String expr){
		expr.replaceAll(" +", "");
		Matcher sep = INPUTPATTERN.matcher(expr);
		while(sep.find()){
			String s = sep.group(1);
			if(s == null){
				break;
			}
			if(showInput) {
				print(s);
			}
			Matcher mat = POINT_PATTERN.matcher(s);
			if(mat.matches()){
				mat = POINT_PATTERN2.matcher(mat.group(1));
				while(mat.find()){
					String name = mat.group(1);
					String x = mat.group(2);
					String y = mat.group(3);
					pmap.put(name, Point.valueOf(parser.apply(x),parser.apply(y), mc));
				}
				continue;
			}
			mat = LINE_PATTERN.matcher(s);
			if(mat.matches()){
				String name = mat.group(1);
				String x = mat.group(2);
				String y = mat.group(3);
				
				String vx = mat.group(5);
				String vy = mat.group(6);
				lmap.put(name, Line.pointDirection(Point.valueOf(parser.apply(x),parser.apply(y), mc),
						PVector.valueOf(parser.apply(vx),parser.apply(vy), mc)));
				continue;
			}
			
			mat = VECTOR_PATTERN.matcher(s);
			if(mat.matches()){
				String name = mat.group(1);
				String x = mat.group(2);
				String y = mat.group(3);
				vmap.put(name, PVector.valueOf(parser.apply(x),parser.apply(y), mc));
				continue;
			}
			
			mat = NUMBER_PATTERN.matcher(s);
			if(mat.matches()) {
				String name = mat.group(1);
				String val = mat.group(2);
				nmap.put(name, parser.apply(val));
				continue;
			}
		}
		if(cp!=null){
			Class<CodePlace> clz = CodePlace.class;
			for(Field f : clz.getDeclaredFields()){
				for(Map<?,?> map : maps) {
					Object p = map.get(f.getName());
					if(checkAndSet(p,f))
						break;
				}
			}
		}
	}
	private boolean checkAndSet(Object obj,Field f){
		if(obj != null && f.getType().equals(obj.getClass())){
			try {
				f.set(cp, obj);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
			return true;
		}
		return false;
	}
}
