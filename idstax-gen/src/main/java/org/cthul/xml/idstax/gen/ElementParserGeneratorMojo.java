package org.cthul.xml.idstax.gen;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import freemarker.template.TemplateException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.apache.maven.project.MavenProject;
import org.xml.sax.SAXException;

/**
 * Goal which touches a timestamp file.
 *
 * @goal generate
 * @phase generate-sources
 */
public class ElementParserGeneratorMojo extends AbstractMojo {

    public static String Version = "0.1-SNAPSHOT";

    /**
     * Output directory.
     * @parameter default-value="${project.build.directory}/generated-sources/pv-parser/"
     * @required
     */
    private File outputDirectory;

    /**
     * Location of schema file.
     * @parameter
     * @required
     */
    private File schema;

    /**
     * package of the output.
     * @parameter alias="package"
     * @required
     */
    private String pkg;

    /**
     * Name of the output.
     * @parameter
     * @required
     */
    private String name;

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    private boolean projectRequired = true;

    public ElementParserGeneratorMojo() {
    }

    /**
     * This constructor is intended for testing.
     * The generated sources will not be added to the maven project's sources.
     * @param outputDirectory
     * @param schema
     * @param pkg
     * @param name
     */
    ElementParserGeneratorMojo(File outputDirectory, File schema, String pkg, String name) {
        this.outputDirectory = outputDirectory;
        this.schema = schema;
        this.pkg = pkg;
        this.name = name;
        this.projectRequired = false;
    }

    @Override
    public void execute() throws MojoExecutionException {
        try {

            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            IDCollector idCollector = new IDCollector();

            SaxIDCollector schemaHandler = new SaxIDCollector(idCollector);
            saxParser.parse(schema, schemaHandler);
            idCollector.finish();

            ElementParserGenerator generator = new ElementParserGenerator(
                    outputDirectory, name, pkg, idCollector,
                    project, projectRequired, getLog());

            generator.run();
            
        } catch (RuntimeException ex) {
            throw ex;
        } catch (ParserConfigurationException ex) {
            throw new MojoExecutionException("Parser Configuration Exception");
        } catch (IOException ex) {
            throw new MojoExecutionException("IO Exception", ex);
        } catch (SAXException ex) {
            throw new MojoExecutionException("Invalid Schema", ex);
        } catch (TemplateException ex) {
            throw new MojoExecutionException("Template error", ex);
        }

    }
}
