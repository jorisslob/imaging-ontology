/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nl.liacs;

import java.io.File;
import java.util.Iterator;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 * @author jslob
 */
public class AnnotationGUI extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open image file");
        File file = fileChooser.showOpenDialog(primaryStage);
        
        ImageInputStream iis = ImageIO.createImageInputStream(file);
        Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
        
        int count=0;
        
        if(readers.hasNext()) {
            count++;
            ImageReader reader = readers.next();
            reader.setInput(iis, true);
            IIOMetadata metadata = reader.getImageMetadata(0);
            for(String name:metadata.getMetadataFormatNames()) {
                System.out.println("Format name: " + name);
                displayMetadata(metadata.getAsTree(name));
            }
        }
        if(count<1) {
            System.out.println("No Image Readers found for this file");
        }
        primaryStage.close();
    }
    
    void displayMetadata(Node root) {
        displayMetadata(root, 0);
    }

    void indent(int level) {
        for (int i = 0; i < level; i++)
            System.out.print("    ");
    }

    void displayMetadata(Node node, int level) {
        // print open tag of element
        indent(level);
        System.out.print("<" + node.getNodeName());
        NamedNodeMap map = node.getAttributes();
        if (map != null) {

            // print attribute values
            int length = map.getLength();
            for (int i = 0; i < length; i++) {
                Node attr = map.item(i);
                System.out.print(" " + attr.getNodeName() +
                                 "=\"" + attr.getNodeValue() + "\"");
            }
        }

        Node child = node.getFirstChild();
        if (child == null) {
            // no children, so close element and return
            System.out.println("/>");
            return;
        }

        // children, so close current tag
        System.out.println(">");
        while (child != null) {
            // print children recursively
            displayMetadata(child, level + 1);
            child = child.getNextSibling();
        }

        // print close tag of element
        indent(level);
        System.out.println("</" + node.getNodeName() + ">");
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
