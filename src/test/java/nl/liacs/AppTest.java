package nl.liacs;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import java.io.File;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
    private final URL onto_url;
    private final String FILENAME = "imaging-ontology.owl";
    private final OntModel model;
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName ) {
        super( testName );
        Thread my_thread = Thread.currentThread();
        ClassLoader my_classloader = my_thread.getContextClassLoader();
      	onto_url = my_classloader.getResource("imaging-ontology.owl");
        model = ModelFactory.createOntologyModel();
        model.read(onto_url.toString());
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( AppTest.class );
    }

    /**
     * Check that the ontology is named correctly
     */
    public void testOntologyName() {
        File onto_file = new File(onto_url.getPath());
        String error_msg = "Ontology filename is not correct";
        assertEquals(error_msg, onto_file.getName(), FILENAME);
    }
    
    /**
     * Check if PROV is imported
     */
    public void testImportPROV()  {
        String error_msg = "Ontology doesn't import PROV-O 20130430";
        String prov_uri = "http://www.w3.org/ns/prov-o-20130430";
        assertTrue(error_msg, model.hasLoadedImport(prov_uri));
    }

    /**
     * Check if the ontology is valid and clean (here called consistent)
     */
    public void testConsistency() {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        InfModel inf = ModelFactory.createInfModel(reasoner, model);
        ValidityReport validation = inf.validate();
        assertTrue(validation.isValid());
        assertTrue(validation.isClean());
    }
}