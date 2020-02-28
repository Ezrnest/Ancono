package cn.ancono.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/*
 * Created at 2018/10/19 12:39
 * @author liyicheng
 */
public class FunctionSup {
    private FunctionSup() {
    }

    public static <T, R> CachedFunction<T, R> cachedFunction(Function<? super T, ? extends R> f) {
        return new CachedFunction<>(f);
    }

    public static <T, R> CachedRecurFunction<T, R> cachedRecurFunction(BiFunction<T, Function<T, R>, R> f) {
        return new CachedRecurFunction<>(f);
    }

    public static class CachedFunction<T, R> implements Function<T, R> {
        private final Map<T, R> cache;

        private final Function<? super T, ? extends R> f;

        public CachedFunction(Function<? super T, ? extends R> f, Supplier<Map<T, R>> mapSupplier) {
            this.f = f;
            this.cache = mapSupplier.get();
        }

        public CachedFunction(Function<? super T, ? extends R> f) {
            this.f = f;
            this.cache = new HashMap<>();
        }

        public Function<? super T, ? extends R> getF() {
            return f;
        }

        @Override
        public R apply(T t) {
            R re = cache.get(t);
            if (re == null) {
                re = f.apply(t);
                cache.put(t, re);
            }
            return re;
        }
    }

    public static class CachedRecurFunction<T, R> implements Function<T, R> {
        private final BiFunction<T, Function<T, R>, R> f;

        private final Map<T, R> cache;

        public CachedRecurFunction(BiFunction<T, Function<T, R>, R> f) {
            this.f = f;
            cache = new HashMap<>();
        }

        public CachedRecurFunction(BiFunction<T, Function<T, R>, R> f, Supplier<Map<T, R>> mapSupplier) {
            this.f = f;
            this.cache = mapSupplier.get();
        }

        @Override
        public R apply(T t) {
            R re = cache.get(t);
            if (re == null) {
                re = f.apply(t, this);
                cache.put(t, re);
            }
            return re;
        }
    }
}
