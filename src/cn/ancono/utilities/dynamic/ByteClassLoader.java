package cn.ancono.utilities.dynamic;

import java.security.SecureClassLoader;

/**
 * A class loader which loads classes from byte arrays.
 *
 * @author lyc
 */
public class ByteClassLoader extends SecureClassLoader {

    public ByteClassLoader() {
        super();
    }

    public ByteClassLoader(ClassLoader loader) {
        super(loader);
    }


    public Class<?> loadClass(String compliedClassName, byte[] buf) {

        Class<?> re = defineClass(compliedClassName, buf, 0, buf.length);
        resolveClass(re);
        return re;
    }


}
