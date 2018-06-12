/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package amber;

import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 *
 * @author MRUDULA
 */
public class readXML {
  
        
    File candFile = new File("D:\\RA\\candiate.xml");
    File refFile = new File("D:\\RA\\reference.xml");
    
    NodeList candList = null;
    NodeList refList = null;
    
    public void readXMLfile(){
        
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doccand = dBuilder.parse(candFile);
            Document docref = dBuilder.parse(refFile);

            doccand.getDocumentElement().normalize();
            docref.getDocumentElement().normalize();

            candList = doccand.getElementsByTagName("CandSent");
            refList = docref.getElementsByTagName("RefSent");
        } catch(Exception e){}
    }
    
    public NodeList getCandList(){
        return candList;
    }
    
     public NodeList getRefList(){
        return refList;
    }
    
}
