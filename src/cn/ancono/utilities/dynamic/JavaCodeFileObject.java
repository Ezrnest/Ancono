package cn.ancono.utilities.dynamic;

import javax.tools.SimpleJavaFileObject;
import java.io.IOException;
import java.net.URI;

/**
 * A virtual file object in RAM.
 *
 * @author lyc
 */
public class JavaCodeFileObject extends SimpleJavaFileObject {

    private final CharSequence code;
    private final String className;

    public JavaCodeFileObject(String className, CharSequence code) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.className = className;
        this.code = code;
    }

    @Override
    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return code;
    }

    public String getClassName() {
        return className;
    }

}
