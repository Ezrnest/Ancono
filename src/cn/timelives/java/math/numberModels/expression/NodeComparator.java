/**
 * 2017-11-24
 */
package cn.timelives.java.math.numberModels.expression;

import cn.timelives.java.math.numberModels.expression.Node.*;
import cn.timelives.java.utilities.CollectionSup;

import java.util.Comparator;

/**
 * @author liyicheng 2017-11-24 19:21
 *
 */
public final class NodeComparator implements Comparator<Node> {

	/**
	 * 
	 */
	NodeComparator() {
		functionName = Comparator.naturalOrder();
	}

	private final Comparator<String> functionName;

	/*
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Node n1, Node n2) {
		Type ty1 = n1.getType();
		Type ty2 = n2.getType();
		int comp = ty1.compareTo(ty2);
		if (comp != 0) {
			return comp;
		}
		switch (ty1) {
		case ADD:
		case MULTIPLY: {
			CombinedNode a = (CombinedNode) n1, b = (CombinedNode) n2;
			comp = a.getNumberOfChildren() - b.getNumberOfChildren();
			if (comp != 0) {
				return comp;
			}
			if (a.p == null) {
				return 1;
			}
			if (b.p == null) {
				return -1;
			}
			comp = a.p.compareTo(b.p);
			if (comp != 0) {
				return comp;
			}
			return CollectionSup.compareCollection(a.children, b.children, this);
		}
		case D_FUNCTION: {
			DFunction a = (DFunction) n1, b = (DFunction) n2;
			comp = functionName.compare(a.functionName, b.functionName);
			if (comp != 0) {
				return comp;
			}
			comp = compare(a.c1, b.c1);
			if (comp != 0) {
				return comp;
			}
			return compare(a.c2, b.c2);
		}
		case FRACTION: {
			Fraction a = (Fraction) n1, b = (Fraction) n2;
			comp = compare(a.c1, b.c1);
			if (comp != 0) {
				return comp;
			}
			return compare(a.c2, b.c2);
		}
		case M_FUNCTION: {
			MFunction a = (MFunction) n1, b = (MFunction) n2;
			comp = functionName.compare(a.functionName, b.functionName);
			if (comp != 0) {
				return comp;
			}
			comp = a.getNumberOfChildren() - b.getNumberOfChildren();
			if (comp != 0) {
				return comp;
			}
			return CollectionSup.compareCollection(a.children, b.children, this);
		}
		case POLYNOMIAL: {
			Poly a = (Poly) n1, b = (Poly) n2;
			return a.p.compareTo(b.p);
		}
		case S_FUNCTION: {
			SFunction a = (SFunction) n1, b = (SFunction) n2;
			comp = functionName.compare(a.functionName, b.functionName);
			if (comp != 0) {
				return comp;
			}
			return compare(a.child, b.child);
		}
		default: {
			throw new AssertionError();
		}
		}
	}
	
	public static final NodeComparator DEFAULT = new NodeComparator();
	
}
