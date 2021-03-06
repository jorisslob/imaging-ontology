package nl.liacs;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntProperty;
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
import java.util.List;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class ImagingOntologyTest extends TestCase {
    private final URL onto_url;
    private final String FILENAME = "imaging-ontology.owl";
    private final OntModel model;
    private final String IRI = "http://www.semanticweb.org/jslob/ontologies/2014/7/imaging-ontology";
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ImagingOntologyTest( String testName ) {
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
        return new TestSuite( ImagingOntologyTest.class );
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
                            "Atomic_Force_Microscope", "Optical_Microscope",
                            "Bright_Field_Microscope", "Electron_Microscope"};
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

    /**
     * All relations mentioned in the SMBM paper should be in the ontology.
     * hasImagingMode, hasInteraction, NumericalAperture, Emission wavelength
     * and Pinhole size
     */
    public void testProperties() {
        String[] expected = {"hasImagingMode", "hasInteraction",
                             "numericalAperture", "emissionWavelength",
                             "pinholeSize", "hasResolution", 
                             "hasOpticalResolution"};
                Boolean[] found = new Boolean[expected.length];
        for(int i=0;i<found.length;i++) {
            found[i] = false;
        }

        for(OntProperty prop:model.listAllOntProperties().toList()) {
            for(int i=0; i<expected.length;i++) {
                if(prop.getLocalName().equals(expected[i])) {
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
     * Adjacent classes should refer to external resources using RDF:seeAlso.
     */
    public void testSeeAlso() {
        String modeURI = IRI+"#Imaging_Mode";
        String InteractionURI = IRI+"#Interaction";
        OntClass ImagingMode = model.getOntClass(modeURI);
        OntClass Interaction = model.getOntClass(InteractionURI);
        List<OntClass> classes = ImagingMode.listSubClasses().toList();
        classes.addAll(Interaction.listSubClasses().toList());
        for(OntClass cl:classes) {
            if(cl.listSeeAlso().toList().isEmpty()) {
                fail(cl.getLocalName() + " does not have an rdf:seeAlso");
            }
        }
    }
}