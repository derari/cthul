package org.cthul.xml.idstax.gen;

import java.io.File;
import org.junit.*;

/**
 *
 * @author Arian Treffer
 */
public class ElementParserGeneratorMojoTest {

    public ElementParserGeneratorMojoTest() {
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

    /**
     * Test of execute method, of class ElementParserGeneratorMojo.
     */
    @Test
    public void testExecute() throws Exception {
        ElementParserGeneratorMojo instance = new ElementParserGeneratorMojo(
                    new File("target/generated-sources/test"),
                    new File("src/test/resources/schema/menu.xsd"),
                    "test.menu", "MenuTest"
                );
        instance.execute();
    }
//
//    @Test
//    public void parseXMLSchema() throws Exception {
//        ElementParserGeneratorMojo instance = new ElementParserGeneratorMojo(
//                    new File("target/test-output"),
//                    new File("src/test/resources/XMLSchema.xsd"),
//                    "xml", "XMLSchema"
//                );
//        instance.execute();
//
//    }
//
//    @Test
//    public void parsePVMetaSchema() throws Exception {
//        ElementParserGeneratorMojo instance = new ElementParserGeneratorMojo(
//                    new File("target/test-output"),
//                    new File("src/test/resources/meta.xsd"),
//                    "meta", "Meta"
//                );
//        instance.execute();
//
//    }

}