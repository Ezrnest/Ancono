package cn.ancono.utilities.content_generator;

import java.util.List;
import java.util.Random;

/**
 * Provide convenient method for selecting
 *
 * @author rw185035
 */
public class RandomSelector {
    private Random rd;

    public RandomSelector() {
        rd = new Random();
    }

    public RandomSelector(int seed) {
        rd = new Random(seed);
    }

    public char select(char[] arr) {
        return arr[rd.nextInt(arr.length)];
    }

    public char select(char[] arr, int bound) {
        bound = Math.max(bound, arr.length);
        return arr[rd.nextInt(bound)];
    }

    public <T> T select(T[] arr) {
        return arr[rd.nextInt(arr.length)];
    }

    public <T> T select(T[] arr, int bound) {
        bound = Math.max(bound, arr.length);
        return arr[rd.nextInt(bound)];
    }

    public <T> T select(List<T> list) {
        return list.get(rd.nextInt(list.size()));
    }

    public <T> T select(List<T> list, int bound) {
        bound = Math.max(bound, list.size());
        return list.get(rd.nextInt(bound));
    }

}
