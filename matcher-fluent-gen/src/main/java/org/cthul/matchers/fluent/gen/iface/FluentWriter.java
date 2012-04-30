package org.cthul.matchers.fluent.gen.iface;

import java.io.OutputStream;
import java.io.PrintWriter;
import org.cthul.matchers.fluent.custom.CustomFluent;

/**
 *
 * @author derari
 */
public class FluentWriter {
    
    private static final String GenericSig = "<ItemYY, This extends XX<Item, This>>";
    
    private static final String customFluent = CustomFluent.class.getCanonicalName();
    
    private static String genericSig(String name, String itemType) {
        if (itemType == null) {
            itemType = "";
        }
        if (!itemType.isEmpty()) {
            itemType = " extends " + itemType;
        }
        return GenericSig.replace("XX", name).replace("YY", itemType);
    }

    private final PrintWriter pw;
    private boolean inSubInterface = false;

    public FluentWriter(OutputStream out) {
        this.pw = new PrintWriter(out);
    }
    
    public void beginInterface(String pkg, String name, String base, String itemType) {
        if (base == null || base.isEmpty()) {
            base = customFluent;
        }
        
        pw.print("package ");
        pw.print(pkg);
        pw.println(";");
        pw.println();
        
        pw.print("public interface ");
        pw.print(name);
        pw.print(genericSig(name, itemType));
        pw.println();
        pw.print("        extends ");
        pw.print(base);
        pw.print("<Item, This> {");
        pw.println();
        pw.println();
        pw.flush();
    }
    
    public void endInterface() {
        pw.println("}");
        pw.flush();
        pw.close();
    }
    
    public void beginSubInterface(String name, String base, String itemType) {
        if (base == null || base.isEmpty()) {
            base = customFluent;
        }
        
        pw.print("    public static interface ");
        pw.print(name);
        pw.print(genericSig(name, itemType));
        pw.println();
        pw.print("            extends ");
        pw.print(base);
        pw.print("<Item, This> {");
        pw.println();
        pw.println();
        pw.flush();
        inSubInterface = true;
    }
    
    public void endSubInterface() {
        pw.println("    }");
        pw.println();
        pw.flush();
        inSubInterface = false;
    }
    
    public void method(String implementor, String name, String args) {
        if (inSubInterface) pw.print("    ");
        pw.print("    @org.cthul.matchers.fluent.custom.Implementation(");
        pw.print(implementor);
        pw.print(".class)");
        pw.println();
        if (inSubInterface) pw.print("    ");
        pw.print("    This ");
        pw.print(name);
        pw.print("(");
        pw.print(args);
        pw.print(");");
        pw.println();
        pw.println();
    }
    
}
