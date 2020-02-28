package cn.ancono.utilities.dynamic;

import cn.ancono.utilities.Printer;

import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.ToolProvider;
import java.lang.reflect.Method;
import java.util.Arrays;

import static cn.ancono.utilities.Printer.print;

public class Test {

    public static void main(String[] args) {
        method();
    }


    private static JavaCodeFileObject getObject() {
        String name = "JavaMethodObject";
        StringBuilder sb = new StringBuilder();
        sb.append("import cn.ancono.utilities.*;");
        sb.append("public class " + name + " {");
        sb.append("public static void main(String[] args){");
        sb.append("Printer.print(\"Hello world\");");
        sb.append("}");
        sb.append("}");
        return new JavaCodeFileObject(name, sb);
    }

    static void method1() {
        JavaCodeFileObject sjfo = getObject();
        JavaCompiler com = ToolProvider.getSystemJavaCompiler();
        DynamicManager manager = DynamicManager.getInstance();
        CompilationTask task = com.getTask(null, manager, null, null, null, Arrays.asList(sjfo));
        if (!task.call()) {
            print("failed");
            return;
        }
        ByteClassLoader loader = new ByteClassLoader();
        Class<?> clazz = loader.loadClass(sjfo.getClassName(), manager.extractClassData(sjfo.getClassName()));
        try {
            Method main = clazz.getMethod("main", String[].class);
            main.invoke(null, (Object) new String[]{});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void method() {
//		String name = "JavaMethodObject";
//		StringBuilder sb = new StringBuilder();
//		sb.append("import cn.ancono.utilities.*;");
//		sb.append("public class "+name+" {");
//		sb.append("public static void main(String[] args){" );
//		sb.append("Printer.print(\"Hello world\");");
//		sb.append("}");
//		sb.append("}");
        JavaCodeGenerator jcg = new JavaCodeGenerator();
        CharSequence code = jcg.setClassName("JavaMethodObject")
                .setPublicClass()
                .addImport("cn.ancono.utilities.*")
                .getFieldBuilder("str")
                .setType(String.class)
                .setInitValue("\"Hello world\"")
                .privateMod()
                .staticMod()
                .finalMod()
                .build()
                .getMethodBuilder("main")
                .publicMod()
                .staticMod()
                .returnVoid()
                .addPara(String[].class, "args")
                .line("Printer.print(str);")
                .build()
                .buildCode();
        print(code);

        DynamicCompileEngine dce = DynamicCompileEngine.getDefaultInstance();
        Class<?> clazz = dce.compileAndLoadClass(jcg.getClassName(), code);
        if (clazz == null) {
            print(dce.getFailureMessage());
            return;
        }
        try {
            Method main = clazz.getMethod("main", String[].class);
            main.invoke(null, (Object) new String[]{});
        } catch (Exception e) {
            e.printStackTrace(Printer.getOutput());
        }
    }
}
