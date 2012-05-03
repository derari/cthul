package org.cthul.xml.idstax.gen;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.cthul.strings.JavaNames;

/**
 *
 * @author Arian Treffer
 */
public class ElementParserGenerator {

    private final File outputDirectory;
    private final String name;
    private final String pkg;
    private final IDCollector idCollector;
    private final MavenProject project;
    private final boolean projectRequired;
    private final Log log;

    public ElementParserGenerator(File outputDirectory, String name, String pkg, IDCollector idCollector, MavenProject project, boolean projectRequired, Log log) {
        this.outputDirectory = outputDirectory;
        this.name = name;
        this.pkg = pkg;
        this.idCollector = idCollector;
        this.project = project;
        this.projectRequired = projectRequired;
        this.log = log;
    }

    public ElementParserGenerator(File outputDirectory, String name, String pkg, IDCollector idCollector, MavenProject project, Log log) {
        this(outputDirectory, name, pkg, idCollector, project, true, log);
    }

    public ElementParserGenerator(File outputDirectory, String name, String pkg, IDCollector idCollector, Log log) {
        this(outputDirectory, name, pkg, idCollector, null, false, log);
    }

    public void run() throws IOException, TemplateException, MojoExecutionException {
        File target = new File(outputDirectory.getAbsolutePath() +
                    "/" + pkg.replaceAll("\\.", "/"));
        if (!target.exists()) {
            if (target.mkdirs()) {
                if (log!=null) log.info("Created dir "+target);
            } else {
                if (log!=null) log.error("Could not create dir "+target);
            }
            
        }
        
        if (project != null) {
            project.addCompileSourceRoot(outputDirectory.getPath());
            if (log!=null) log.info("Added "+outputDirectory+" to sources");
        } else if (projectRequired) {
            throw new MojoExecutionException("Missing project");
        }

        Configuration cfg = new Configuration();
        TemplateLoader l = new ClassTemplateLoader(
                ElementParserGenerator.class, "/templates");
        cfg.setTemplateLoader(l);
        BeansWrapper w = new BeansWrapper();
        cfg.setObjectWrapper(w);

        Map<String,Object> model = new HashMap<>();
        model.put("version",ElementParserGeneratorMojo.Version);
        model.put("package",pkg);
        model.put("name",name);
        model.put("u", new JavaNames());
        model.put("schema", idCollector);

        Writer out;

        Template elementIDs = cfg.getTemplate("ElementIDs.ftl");
        out = new FileWriter(target + "/" + name + "IDs.java");
        elementIDs.process(model, out);
        out.close();
        if (log!=null) log.info("Generated file "+name + "IDs.java");

        Template resolver = cfg.getTemplate("Resolver.ftl");
        out = new FileWriter(target + "/" + name + "Resolver.java");
        resolver.process(model, out);
        out.close();
        if (log!=null) log.info("Generated file "+name + "Resolver.java");
    }

    /*
    public void run() throws SAXException, ParserConfigurationException, IOException, MojoExecutionException {
        // create target folder
            File target = new File(outputDirectory.getAbsolutePath() +
                    "/" + pkg.replaceAll("\\.", "/"));
            target.mkdirs();

            // add the folder to the compile list
            if (project != null) {
                project.addCompileSourceRoot(target.getAbsolutePath());
            } else if (projectRequired) {
                throw new MojoExecutionException("MavenProject required");
            }

            File ifaceIDs = new File(
                    target + "/" + name + "IDs.java");
            new ElementIDGenerator().generate(
                    ifaceIDs, idCollector, pkg, name);

            File resolver = new File(
                    target + "/" + name + "Resolver.java");
            new ResolverGenerator().generate(
                    resolver, idCollector, pkg, name);
    }*/

}
