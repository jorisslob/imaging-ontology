package nl.liacs;

import java.io.File;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Check that the ontology is named correctly
     */
    public void testOntologyName()
    {
	URL url = Thread.currentThread().getContextClassLoader().getResource("imaging-ontology.owl");
	File file = new File(url.getPath());
        assertEquals(file.getName(),"imaging-ontology.owl");
    }
}
