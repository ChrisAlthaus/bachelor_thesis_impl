package gui_control.XMLParser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class XMLBuilder {
	private static class TreeItemCreationContentHandler extends DefaultHandler {

	    private TreeItem<String> item = new TreeItem<>();

	    @Override
	    public void endElement(String uri, String localName, String qName) throws SAXException {
	        // finish this node by going back to the parent
	        this.item = this.item.getParent();
	        System.out.println("end element:"+ uri + "," + localName+ "," + qName);
	    }

	    @Override
	    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
	        // start a new node and use it as the current item
	        TreeItem<String> item = new TreeItem<>(qName);
	        this.item.getChildren().add(item);
	        this.item = item;
	        
	        System.out.println("start element:"+ uri + "," + localName+ "," + qName);
	        for (int i = 0; i < attributes.getLength(); i++) {
	        	  String uriName = attributes.getURI(i);
	        	  String lName = attributes.getLocalName(i);
	        	  String value = attributes.getValue(i);
	        	  System.out.println(uriName +"," + lName +"," + value);	  
	        	}
	        System.out.println();
	       
	        
	        for( int index = 0; index < attributes.getLength() ; index++ ){
	        	String valueName=attributes.getQName(index);
	        	TreeItem<String> leafItem = new TreeItem<>(valueName);
	        	item.getChildren().add(leafItem);
	        }
	     
	    }

	    @Override
	    public void characters(char[] ch, int start, int length) throws SAXException {
	        String s = String.valueOf(ch, start, length).trim();
	        System.out.println("characters="+ s);
	        if (!s.isEmpty()) {
	            // add text content as new child
	            this.item.getChildren().add(new TreeItem<>(s));
	        }
	    }

	}

	public static TreeItem<String> readData(File file) throws SAXException, ParserConfigurationException, IOException {
	    SAXParserFactory parserFactory = SAXParserFactory.newInstance();
	    SAXParser parser = parserFactory.newSAXParser();
	    XMLReader reader = parser.getXMLReader();
	    TreeItemCreationContentHandler contentHandler = new TreeItemCreationContentHandler();

	    // parse file using the content handler to create a TreeItem representation
	    reader.setContentHandler(contentHandler);
	    
	    try{
	    	reader.parse(file.toURI().toString());
	    }catch(SAXException e){
	    	System.out.println("warning:XML file is not displayed, because xml file has wrong syntax.");
	    	return null;
	    }
	    

	    // use first child as root (the TreeItem initially created does not contain data from the file)
	    TreeItem<String> item = contentHandler.item.getChildren().get(0);
	    contentHandler.item.getChildren().clear();
	    return item;
	}

	public TreeItem<String> getTreeView(File xmlFile) throws SAXException, ParserConfigurationException, IOException{
		// display data for file "data/tree.xml" in TreeView
		TreeItem<String> root = readData(xmlFile);
		TreeView<String> treeView = new TreeView<>(root);	
		return root;
	}
	
	public TreeItem<String> getTreeView(String xmlText) throws SAXException, ParserConfigurationException, IOException{
		// display data for file "data/tree.xml" in TreeView
		File xmlFromJson = new File("xmlFromJson");
		
		PrintWriter writer = new PrintWriter(xmlFromJson);
		writer.print("");
		writer.print(xmlText);
		writer.close();
		
		TreeItem<String> root = readData(xmlFromJson);
		TreeView<String> treeView = new TreeView<>(root);	
		return root;
	}



	
	
}
