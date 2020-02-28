/**
 * 2017-11-26
 */
package cn.ancono.math.numberModels.expression.simplification;

import cn.ancono.math.numberModels.expression.ExprCalculator;
import cn.ancono.math.numberModels.expression.Node;
import cn.ancono.math.numberModels.expression.Node.*;
import cn.ancono.math.numberModels.expression.anno.AllowModify;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * @author liyicheng
 * 2017-11-26 13:34
 */
public abstract class SimStraImpl implements SpecificStrategy, TaggedStrategy {
    protected final Set<String> tags;
    protected final Set<Type> types;
    protected final String fname;
    protected final String description;

    /**
     *
     */
    public SimStraImpl(Set<String> tags, Set<Type> types, String fname, String description) {
        this.tags = tags;
        this.types = types;
        this.fname = fname;
        this.description = description == null ? "" : description;
    }

    /**
     *
     */
    public SimStraImpl(Set<String> tags, Set<Type> types, String fname) {
        this.tags = tags;
        this.types = types;
        this.fname = fname;
        this.description = "";
    }

    /*
     * @see cn.ancono.math.numberModels.expression.simplification.TaggedStrategy#isAcceptable(java.util.Set)
     */
    @Override
    public boolean isAcceptable(Set<String> tags) {
        for (String s : tags) {
            if (this.tags.contains(s)) {
                return true;
            }
        }
        return false;
    }

    /*
     * @see cn.ancono.math.numberModels.expression.simplification.SpecificStrategy#registerFunctionName()
     */
    @Override
    public String registerFunctionName() {
        return fname;
    }

    /*
     * @see cn.ancono.math.numberModels.expression.simplification.SpecificStrategy#registerType()
     */
    @Override
    public Set<Type> registerTypes() {
        return types;
    }

    /*
     * @see cn.ancono.math.numberModels.expression.simplification.TaggedStrategy#getTags()
     */
    @Override
    public Set<String> getTags() {
        return tags;
    }

    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /*
     * @see cn.ancono.math.numberModels.expression.SimplificationStrategy#simplifyNode(cn.ancono.math.numberModels.expression.Node)
     */
    @Override
    @Nullable
    public Node simplifyNode(@AllowModify Node node, ExprCalculator mc) {
        Type t = node.getType();
        if (!types.contains(t)) {
            return node;
        }
        switch (t) {
            case ADD:
                return simplifyAdd((Add) node, mc);
            case MULTIPLY:
                return simplifyMultiply((Multiply) node, mc);
            case FRACTION:
                return simplifyFraction((NodeFrac) node, mc);
            case S_FUNCTION:
                return simplifySFunction((SFunction) node, mc);
            case D_FUNCTION:
                return simplifyDFunction((DFunction) node, mc);
            case M_FUNCTION:
                return simplifyMFunction((MFunction) node, mc);
            case POLYNOMIAL:
                return simplifyPolynomial((Poly) node, mc);
            default:
                break;
        }
        return node;
    }

    /**
     * Performs the simplification for a polynomial node. Ignore this method if this
     * simplifier doesn't register the type.
     *
     * @param node a node
     * @return the node after simplification, or {@code null} to indicate no simplification is done.
     */
    protected Node simplifyPolynomial(@AllowModify Poly node, ExprCalculator mc) {
        return node;
    }

    /**
     * Performs the simplification for a node Add. Ignore this method if this
     * simplifier doesn't register the type.
     *
     * @param node a node
     * @return the node after simplification, or {@code null} to indicate no simplification is done.
     */
    @Nullable
    protected Node simplifyAdd(@AllowModify Add node, ExprCalculator mc) {
        return node;
    }

    /**
     * Performs the simplification for a node Multiply. Ignore this method if this
     * simplifier doesn't register the type.
     *
     * @param node a node
     * @return the node after simplification, or {@code null} to indicate no simplification is done.
     */
    @Nullable
    protected Node simplifyMultiply(@AllowModify Multiply node, ExprCalculator mc) {
        return node;
    }

    /**
     * Performs the simplification for a node Fraction. Ignore this method if this
     * simplifier doesn't register the type.
     *
     * @param node a node
     * @return the node after simplification, or {@code null} to indicate no simplification is done.
     */
    @Nullable
    protected Node simplifyFraction(@AllowModify NodeFrac node, ExprCalculator mc) {
        return node;
    }

    /**
     * Performs the simplification for a node SFunction. Ignore this method if this
     * simplifier doesn't register the type.
     *
     * @param node a node
     * @return the node after simplification, or {@code null} to indicate no simplification is done.
     */
    @Nullable
    protected Node simplifySFunction(@AllowModify SFunction node, ExprCalculator mc) {
        return node;
    }

    /**
     * Performs the simplification for a node DFunction. Ignore this method if this
     * simplifier doesn't register the type.
     *
     * @param node a node
     * @return the node after simplification, or {@code null} to indicate no simplification is done.
     */
    @Nullable
    protected Node simplifyDFunction(@AllowModify DFunction node, ExprCalculator mc) {
        return node;
    }

    /**
     * Performs the simplification for a node MFunction. Ignore this method if this
     * simplifier doesn't register the type.
     *
     * @param node a node
     * @return the node after simplification, or {@code null} to indicate no simplification is done.
     */
    @Nullable
    protected Node simplifyMFunction(@AllowModify MFunction node, ExprCalculator mc) {
        return node;
    }
}
