package nl.liacs;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.FileManager;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ProvTest extends TestCase {
    private final URL onto_url;
    private final URL data_url;
    private final Model schema;
    private final Model data;
    private final OntModel model;
    private final InfModel inf;
    private final String PROV_URI = "http://www.w3.org/ns/prov-o-20130430";    
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ProvTest( String testName ) {
        super( testName );
        Thread my_thread = Thread.currentThread();
        ClassLoader my_classloader = my_thread.getContextClassLoader();
      	onto_url = my_classloader.getResource("imaging-ontology.owl");
        data_url = my_classloader.getResource("prov-example.rdf");
        
        schema = FileManager.get().loadModel(onto_url.toString());
        data = FileManager.get().loadModel(data_url.toString());
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        reasoner = reasoner.bindSchema(schema);
        inf = ModelFactory.createInfModel(reasoner, data);
        
        model = ModelFactory.createOntologyModel();
        model.read(onto_url.toString());
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite( ProvTest.class );
    }
   
    /**
     * Check if PROV is imported
     */
    public void testImportPROV()  {
        String error_msg = "Ontology doesn't import PROV-O 20130430";
        assertTrue(error_msg, model.hasLoadedImport(PROV_URI));
    }
    
    /**
     * Check if the ontology is valid and clean (here called consistent)
     */
    public void testConsistency() {
        ValidityReport validation = inf.validate();
        assertTrue(validation.isValid());
        assertTrue(validation.isClean());
    }
}