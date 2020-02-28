/**
 * 2017-11-24
 */
package cn.ancono.math.numberModels.expression;

import cn.ancono.math.numberModels.Multinomial;
import cn.ancono.math.numberModels.expression.Node.*;
import cn.ancono.utilities.CollectionSup;

import java.util.Comparator;

/**
 * @author liyicheng 2017-11-24 19:21
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
                Multinomial p1 = a.p == null ? Multinomial.ZERO : a.p;
//			if (a.p == null) {
//				return 1;
//			}
//			if (b.p == null) {
//				return -1;
//			}
                Multinomial p2 = b.p == null ? Multinomial.ZERO : b.p;
                comp = p1.compareTo(p2);
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
                NodeFrac a = (NodeFrac) n1, b = (NodeFrac) n2;
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


//    public static void main(String[] args) {
//        Node ns = new Node[]{Expression.valueOf("f(x)g(x)h(x)")}
//    }
}
