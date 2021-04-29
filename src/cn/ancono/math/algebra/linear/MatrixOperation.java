package cn.ancono.math.algebra.linear;

/**
 * A simple class defines the elementary operations to a Matrix.
 *
 * @author lyc
 */
public class MatrixOperation<T> {
    public enum Operation {
        EXCHANGE_ROW,
        EXCHANGE_COLUMN,
        MULTIPLY_ROW,
        MULTIPLY_COLUMN,
        MULTIPLY_ADD_ROW,
        MULTIPLY_ADD_COLUMN,
    }

    public final Operation ope;


    /**
     * Arguments : arg0 should be the first row or column.For multiply and add , the row(column) to multiply
     * should to arg0.If there is only one argument is needed , then arg1 will be useless.
     */
    public final int arg0, arg1;
    /**
     *
     */
    public final T num;

    MatrixOperation(Operation op, int arg0, int arg1, T num) {
        this.ope = op;
        this.arg0 = arg0;
        this.arg1 = arg1;
        this.num = num;
    }

    public T getNum() {
        return num;
    }

    /**
     * Show this operation with a String which can be understood easily.This method is designed to
     * be used for showing the operation to users.
     *
     * @return a String
     */
    public String toDetail() {
        return switch (ope) {
            case EXCHANGE_COLUMN -> "Exchange Column:" + arg0 + "<->" + arg1;
            case EXCHANGE_ROW -> "Exchange Row:" + arg0 + "<->" + arg1;
            case MULTIPLY_ADD_COLUMN -> "Mul Column " + arg0 + " by " + num.toString() + " add to Column " + arg1;
            case MULTIPLY_ADD_ROW -> "Mul Row " + arg0 + " by " + num.toString() + " add to Row " + arg1;
            case MULTIPLY_COLUMN -> "Multiply Column: " + arg0 + " by " + num.toString();
            case MULTIPLY_ROW -> "Multiply Row: " + arg0 + " by " + num.toString();
        };
    }

    @Override
    public String toString() {
        return toDetail();
    }

    public static <T> MatrixOperation<T> exchangeRow(int r1, int r2) {
        return new MatrixOperation<>(Operation.EXCHANGE_ROW, r1, r2, null);
    }

    public static <T> MatrixOperation<T> exchangeColumn(int c1, int c2) {
        return new MatrixOperation<>(Operation.EXCHANGE_COLUMN, c1, c2, null);
    }

    public static <T> MatrixOperation<T> multiplyRow(int r, T f) {
        return new MatrixOperation<>(Operation.MULTIPLY_ROW, r, -1, f);
    }

    public static <T> MatrixOperation<T> multiplyColumn(int c, T f) {
        return new MatrixOperation<>(Operation.MULTIPLY_COLUMN, c, -1, f);
    }

    public static <T> MatrixOperation<T> multiplyAddRow(int r1, int r2, T f) {
        return new MatrixOperation<>(Operation.MULTIPLY_ADD_ROW, r1, r2, f);
    }

    public static <T> MatrixOperation<T> multiplyAddColumn(int c1, int c2, T f) {
        return new MatrixOperation<>(Operation.MULTIPLY_ADD_COLUMN, c1, c2, f);
    }
}
