package org.cthul.matchers.fluent.gen;

import com.thoughtworks.qdox.JavaDocBuilder;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import org.cthul.matchers.fluent.gen.iface.FluentMethod;
import org.cthul.matchers.fluent.gen.iface.FluentMethodCollector;
import org.cthul.matchers.fluent.gen.iface.FluentInterface;
import org.cthul.matchers.fluent.gen.iface.FluentWriter;
import org.cthul.matchers.fluent.gen.iface.FluentWriterFactory;
import org.cthul.matchers.fluent.gen.iface.ItemTypeFilter;
import org.cthul.matchers.fluent.gen.xml.iface.*;
import org.cthul.matchers.fluent.gen.xml.iface.PackageDefinition;

/**
 *
 * @author Arian Treffer
 */
public class FluentInterfaceGenerator {
    
    public static void run (File[] source, File target, InterfaceGenerator config) {
        new FluentInterfaceGenerator(source, config).write(target);
    }
    
    private final JavaDocBuilder doc;
    private final List<FluentMethod> methods;
    private final List<FluentInterface> files = new ArrayList<>();
    
    private FluentInterfaceGenerator(File[] source, InterfaceGenerator config) {
        doc = new JavaDocBuilder();
        for (File f: source) {
            doc.addSourceTree(f);
        }
        FluentMethodCollector collector = new FluentMethodCollector(doc);
        for (FactoryReference f: config.getFactory()) {
            collector.addMethods(f.getClazz());
        }
        methods = collector.getMethods();
        
        Map<String, FluentInterface> iMap = new HashMap<>();
        for (PackageDefinition p: config.getPackage()) {
            for (InterfaceDefinition id: p.getInterface()) {
                FluentInterface fi = new FluentInterface(iMap, 
                                id.getName(), p.getName(), id.getBase(), id.getItemType());
                files.add(fi);
                iMap.put(fi.getName(), fi);
                iMap.put(fi.getFullName(), fi);
                if (fi.getMethods() != null && !fi.getMethods().isEmpty()) {
                    addMethods(fi, id.getMethods());
                }
                String sub = p.getName() + "." + id.getName();
                for (SubInterfaceDefinition sid: id.getInterface()) {
                    FluentInterface sfi = new FluentInterface(iMap, 
                                sid.getName(), sub, sid.getBase(), sid.getItemType());
                    fi.getSubinterfaces().add(sfi);
                    iMap.put(sfi.getName(), sfi);
                    iMap.put(sfi.getFullName(), sfi);
                    addMethods(sfi, sid.getMethods());
                }
            }
        }
        
    }

    private void addMethods(FluentInterface fi, List<MethodsFilter> methodFilters) {
        List<FluentMethod> basicMethods = filterMethods(this.methods, fi.getItemType());
        if (methodFilters == null || methodFilters.isEmpty()) {
            fi.addMethods(basicMethods);
            return;
        }
        
        for (MethodsFilter m: methodFilters) {
            List<FluentMethod> myMethods = filterMethods(basicMethods, m.getItemType());
            fi.addMethods(myMethods);
        }
    }

    private List<FluentMethod> filterMethods(List<FluentMethod> basicMethods, String itemType) {
        List<FluentMethod> myMethods = basicMethods;
        if (itemType != null && !itemType.isEmpty()) {
            myMethods = new ItemTypeFilter(doc, myMethods).getMethodsForType(itemType);
        }
        return myMethods;
    }

    private void write(File target) {
        FluentWriterFactory wf = new FluentWriterFactory(target);
        for (FluentInterface fi: files) {
            FluentWriter fw = wf.create(fi.getPackage(), fi.getName());
            fw.beginInterface(fi.getPackage(), fi.getName(), fi.getBase(), fi.getItemType());
            writeMethods(fw, fi.getEffectiveMethods());
            for (FluentInterface sfi: fi.getSubinterfaces()) {
                fw.beginSubInterface(sfi.getName(), sfi.getBase(), sfi.getItemType());
                writeMethods(fw, sfi.getEffectiveMethods());
                fw.endSubInterface();
            }
            fw.endInterface();
        }
    }

    private void writeMethods(FluentWriter fw, SortedSet<FluentMethod> methods) {
        for (FluentMethod fm: methods) {
            fw.method(fm.getImplementor(), fm.getName(), fm.getArgs());
        }
    }
}
