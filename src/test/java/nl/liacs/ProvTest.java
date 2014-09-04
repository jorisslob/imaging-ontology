package nl.liacs;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

/**
 * Unit test for simple App.
 */
public class ProvTest extends TestCase {
    private final URL data_url;
    private final String PROV_URI = "http://www.w3.org/ns/prov-o#";    
    private OWLOntology prov_data_model;
    
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ProvTest( String testName ) {
        super( testName );
        Thread my_thread = Thread.currentThread();
        ClassLoader my_classloader = my_thread.getContextClassLoader();
        data_url = my_classloader.getResource("prov-example.rdf");
        
        prov_data_model = null;
        try {            
            OWLOntologyManager m = OWLManager.createOWLOntologyManager();
            prov_data_model = m.loadOntologyFromOntologyDocument(IRI.create(data_url));
        } catch (URISyntaxException | OWLOntologyCreationException ex) {
            Logger.getLogger(ProvTest.class.getName()).log(Level.SEVERE, null, ex);
        } 
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
        Set<OWLOntology> imports = prov_data_model.getImportsClosure();
        boolean foundProv = false;
        for(OWLOntology i:imports) {
            if(i.getOntologyID().getOntologyIRI().toString().equals(PROV_URI)) {
                foundProv = true;
            }
        }
        String error_msg = "Example rdf file does not import PROV";
        assertTrue(error_msg, foundProv);
    }
    
    /**
     * Check if the ontology is valid and clean (here called consistent)
     */
    public void testConsistency() {
        Reasoner hermit = new Reasoner(prov_data_model);
        assertTrue(hermit.isConsistent());
    }
}