package cn.ancono.utilities.dynamic;

import cn.ancono.utilities.Printer;

import javax.tools.*;
import javax.tools.JavaCompiler.CompilationTask;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A class aimed to dynamically compile the source code using the current
 * ClassLoader and the Compiler available.
 *
 * @author lyc
 */
public class DynamicCompileEngine {

    private final ByteClassLoader loader;

    private final JavaCompiler compiler;

    private final DynamicManager manager;

    private DiagnosticCollector<JavaFileObject> failureDiagnostic;
    private String failureMessage;


    private static final DynamicCompileEngine default_instance;

    static {
        ByteClassLoader loader = new ByteClassLoader(DynamicCompileEngine.class.getClassLoader());
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DynamicManager manager = DynamicManager.getInstance();
        default_instance = new DynamicCompileEngine(loader, compiler, manager);
    }

    private DynamicCompileEngine(ByteClassLoader loader, JavaCompiler compiler, DynamicManager manager) {
        this.loader = loader;
        this.compiler = compiler;
        this.manager = manager;
    }

    /**
     * Compile and returns the corresponding class object,returns {@code null} if any exception occurs,and the error or exception
     * info will be saved and can be extracted through {@link #getFailureMessage()}
     *
     * @param className
     * @param code
     * @return
     */
    public Class<?> compileAndLoadClass(String className, CharSequence code) {
        JavaCodeFileObject codeObj = new JavaCodeFileObject(className, code);
        //add options.
        List<String> options = new ArrayList<>();
        options.add("-encoding");
        options.add("UTF-8");

        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<JavaFileObject>();
        CompilationTask task = compiler.getTask(Printer.getOutput(), manager, diagnostics, options, null, Arrays.asList(codeObj));
        if (!task.call()) {
            recordFailure(diagnostics);
            return null;
        }
        //cover the last time's failure.
        recordFailure(null);
        byte[] buf = manager.extractClassData(className);
        manager.clearMapper();
        return loader.loadClass(className, buf);


    }

    private void recordFailure(DiagnosticCollector<JavaFileObject> diagnostics) {
        //cover the message
        failureDiagnostic = diagnostics;
        failureMessage = null;
    }

    private StringBuilder toDetailMessage(StringBuilder toAppend, Diagnostic<?> diagnostic) {
//		System.out.println("Code:" + diagnostic.getCode());
//		System.out.println("Kind:" + diagnostic.getKind());
//		System.out.println("Position:" + diagnostic.getPosition());
//		System.out.println("Start Position:" + diagnostic.getStartPosition());
//		System.out.println("End Position:" + diagnostic.getEndPosition());
//		System.out.println("Source:" + diagnostic.getSource());
//		System.out.println("Message:" + diagnostic.getMessage(null));
//		System.out.println("LineNumber:" + diagnostic.getLineNumber());
//		System.out.println("ColumnNumber:" + diagnostic.getColumnNumber());
        toAppend.append("Code:[" + diagnostic.getCode() + "]\n");
        toAppend.append("Kind:[" + diagnostic.getKind() + "]\n");
        toAppend.append("Position:[" + diagnostic.getPosition() + "]\n");
        toAppend.append("Start Position:[" + diagnostic.getStartPosition() + "]\n");
        toAppend.append("End Position:[" + diagnostic.getEndPosition() + "]\n");
        toAppend.append("Source:[" + diagnostic.getSource() + "]\n");
        toAppend.append("Message:[" + diagnostic.getMessage(null) + "]\n");
        toAppend.append("LineNumber:[" + diagnostic.getLineNumber() + "]\n");
        toAppend.append("ColumnNumber:[" + diagnostic.getColumnNumber() + "]\n");
        return toAppend;
    }

    /**
     * Gets the failure message of the last time's compilation,if there is no failure message,
     * a String "No failure message available." will be returned.
     *
     * @return a String recording the failure message.
     */
    public String getFailureMessage() {
        if (failureDiagnostic == null) {
            return "No failure message available.";
        }
        if (failureMessage == null) {
            failureMessage = failureDiagnostic.getDiagnostics()
                    .stream()
                    .collect(StringBuilder::new, this::toDetailMessage, (sb1, sb2) -> sb1.append(sb2))
                    .toString();
        }
        return failureMessage;
    }

    /**
     * Gets the failure diagnostics of the last time.
     *
     * @return
     */
    public List<Diagnostic<? extends JavaFileObject>> getFailureDiagnostics() {
        if (failureDiagnostic == null) {
            return null;
        }
        return failureDiagnostic.getDiagnostics();
    }


    /**
     * Gets a default instance of the engine.
     *
     * @return
     */
    public static DynamicCompileEngine getDefaultInstance() {
        return default_instance;
    }


}
