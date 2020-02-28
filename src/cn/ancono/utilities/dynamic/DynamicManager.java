package cn.ancono.utilities.dynamic;

import javax.tools.*;
import javax.tools.JavaFileObject.Kind;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DynamicManager extends ForwardingJavaFileManager<JavaFileManager> {

    private Map<String, JavaClassFileObject> mapper;

    private static final DynamicManager ins = new DynamicManager(
            ToolProvider.getSystemJavaCompiler().getStandardFileManager(null, null, null));

    public DynamicManager(JavaFileManager fileManager) {
        super(fileManager);
        mapper = new HashMap<>();
    }


    /*
     * (non-Javadoc)
     * @see javax.tools.ForwardingJavaFileManager#getJavaFileForOutput(javax.tools.JavaFileManager.Location, java.lang.String, javax.tools.JavaFileObject.Kind, javax.tools.FileObject)
     * ::
     * This method is to be called when a java complier is doing its task,override this method to save the java object
     */
    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, Kind kind, FileObject sibling)
            throws IOException {
        //only className and kind is useful
        JavaClassFileObject classObj = new JavaClassFileObject(className, kind);
        mapper.put(className, classObj);
        return classObj;
    }

    /**
     * Gets the byte array of the class file,returns null if there is no such class.
     * This class must be created through method {@link #getJavaFileForOutput(javax.tools.JavaFileManager.Location, String, Kind, FileObject)},which
     * is usually done by the compiler.
     *
     * @param className the name
     * @return a byte array of the class' data.
     */
    public byte[] extractClassData(String className) {
        JavaClassFileObject jco = mapper.get(className);
        if (jco != null) {
            return jco.getClassByteData();
        }
        return null;
    }

    /**
     * Clear the mapper of this manager,after this method is called,there
     * will be no class data in the mapper.
     */
    public void clearMapper() {
        mapper.clear();
    }

    /**
     * Gets a default dynamic manager.
     *
     * @return a DynamicManager
     */
    public static DynamicManager getInstance() {
        return ins;
    }


}
