/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cthul.matchers.fluent.gen;

import java.io.File;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.SchemaFactory;
import org.cthul.matchers.fluent.gen.xml.iface.InterfaceGenerator;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author derari
 */
public class FluentInterfaceGeneratorTest {
    
    public FluentInterfaceGeneratorTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testRun() throws Exception {
        File src = new File("./src/test/java");
        File target = new File("./target/test-out1/");
        File schema = new File("./src/main/resources/schema/interfaces.xsd");
        File xml = new File("./src/test/xml/ColorInterfaces.xml");
        Source xmlSrc = new StreamSource(xml);

        JAXBContext ctx = JAXBContext.newInstance(InterfaceGenerator.class);
        Unmarshaller um = ctx.createUnmarshaller();
        SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        um.setSchema(sf.newSchema(schema));
        
        InterfaceGenerator config = um.unmarshal(xmlSrc, InterfaceGenerator.class).getValue();
        FluentInterfaceGenerator.run(new File[]{src}, target, config);
    }
}
