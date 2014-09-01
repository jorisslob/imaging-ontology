package nl.liacs;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.rdf.model.InfModel;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.reasoner.Reasoner;
import com.hp.hpl.jena.reasoner.ReasonerRegistry;
import com.hp.hpl.jena.reasoner.ValidityReport;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
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
    private final String IRI = "http://www.semanticweb.org/jslob/ontologies/2014/7/imaging-ontology";
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
    
    /**
     * Check for definitions in every class
     */
    public void testDefinitions() {
        String def = "http://www.w3.org/ns/prov#definition";
        Property p = model.getOntProperty(def);
        for(OntClass cl:model.listClasses().toList()) {
            if(cl.isAnon()) continue; // Skip Anonymous classes
            // TODO: Find a better way to check for equality to OWL:Thing
            if(cl.getLocalName().equals("Thing")) continue; 
            Statement statement = cl.getProperty(p);
            String error_msg = "Class " + cl.getLocalName() + " does not have a definition";
            assertTrue(error_msg, statement!=null);
        }
    }

    /**
     * Check for key microscopes mentioned in SMBM paper
     */
    public void testMicroscopes() {
        String[] expected = {"Fluorescent_Microscope","Confocal_Microscope",
                            "Transmission_Electron_Microscope",
                            "Scanning_Electron_Microscope",
                            "Atomic_Force_Microscope"};
        Boolean[] found = new Boolean[expected.length];
        for(int i=0;i<found.length;i++) {
            found[i] = false;
        }

        for(OntClass cl:model.listClasses().toList()) {
            if(cl.isAnon()) continue; // Skip Anonymous classes
            for(int i=0; i<expected.length;i++) {
                if(cl.getLocalName().equals(expected[i])) {
                    found[i] = true;
                }
            }
        }

        for(int i=0;i<found.length;i++) {
            if (!found[i]) {
                fail(expected[i] + " was not found in ontology");
            }
        }
    }
    
    /**
     * All microscope subclasses should have more axioms than just the 
     * assertion that it is a subclass of Microscope.
     */
    public void testAxiomsOnMicroscopes() {
        String microscopeURI = IRI+"#Microscope";
        OntClass microscope = model.getOntClass(microscopeURI);
        ExtendedIterator<OntClass> subclasses = microscope.listSubClasses();
        for(OntClass cl:subclasses.toList()) {
            if(cl.listEquivalentClasses().toSet().isEmpty()) {
                fail(cl.getLocalName() + " has no axioms defining it");
            }
        }
    }
}