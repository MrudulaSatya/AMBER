/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package amber;

import java.sql.ResultSet;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.*;
import java.util.*;

/**
 *
 * @author Dopt
 */
public class AMBER {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        //Database for reading the synonyms
            DBConn conn = new DBConn();
            conn.getConnection();
            conn.stmtConnection();
            String synonym_query;
            
            //xml file reading for reference and candidate xml files
            readXML xml = new readXML();
            xml.readXMLfile();
            NodeList candList, refList;
            candList = xml.getCandList();
            refList = xml.getRefList();
            
            String[] splitC;
            String[] splitR1;
            
        
        
            String tempC ;
            String tempC1 ;
            String tempC2 ;
            String tempC3 ;
        
        
            int p1=0, p2=0, p3=0, p4=0;
            
            double SBP;
            double SRP;
            double CSBP;
            double CSRP;
            double CKP;
            double CTP;
            double SWDP;
            double LWDP;
            double shortwordT = 0;
            double shortwordR = 0;
            double longwordT = 0;
            double longwordR = 0;
            double NSCP;
            double NKCP;
            double AMBER;
            double avgLengthOfReferences,noOfwordsIncandi;
            double charRefLen = 0, charCandLen=0;
            int ifBreak = 0;
         
	
        // loop that reads the xml one by one  
	for (int tempcand = 0; tempcand < candList.getLength(); tempcand ++) { 

		Node candNode = candList.item(tempcand);
                Node refNode = refList.item(tempcand);

		if (candNode.getNodeType() == Node.ELEMENT_NODE && refNode.getNodeType() == Node.ELEMENT_NODE) {

			Element candElement = (Element) candNode;
                        Element refElement = (Element) refNode;

			String cand;
                        cand = candElement.getElementsByTagName("DATA").item(0).getTextContent();
                        
                        String R1; 
                        R1 = refElement.getElementsByTagName("Ref1").item(0).getTextContent();
                        
                       
                         //Display values
                        System.out.println("cand: " + cand);
                        System.out.println("ref1: " + R1);
                        
                        
         
                        // splitting the candidate and reference sentences into individual words
                        String temp = cand.replaceAll("[\\n]"," ");
                        splitC = temp.replaceAll("[.,!?:;'।]","").split(" ");
         
                        temp = R1.replaceAll("[\\n]"," ");
                        splitR1 = temp.replaceAll("[.,!?:;'।]","").split(" ");
         
                       
                       
                        //calculating c for the current candidate sentence
                        noOfwordsIncandi = splitC.length;
                        avgLengthOfReferences = splitR1.length ;
                        ArrayList<Integer> rankingT = new ArrayList<Integer>(Math.max(splitC.length,splitR1.length));
                        ArrayList<Integer> rankingR = new ArrayList<Integer>(Math.max(splitC.length,splitR1.length));

                        //calculating p1
                        
                        //loop that traverses through each word of the candidate 
                        //one by one that is 1 gram precision
                        for(int i=0;i<splitC.length;i++){
                            
                            
                             ifBreak = 0;
                             
                             //ith word stored in tempC 
                             tempC = splitC[i];
                             charCandLen = charCandLen + tempC.length();
                             if(tempC.length()<4)shortwordT=shortwordT+1;
                             if(tempC.length()>3)longwordT=longwordT+1;
                             //loop traverses R1 word by word
                             for(int j=0;j<splitR1.length;j++){
                                 charRefLen = charRefLen + splitR1[j].length();
                                 if(splitR1[j].length()<4)shortwordR=shortwordR+1;
                                 if(splitR1[j].length()>3)longwordR=longwordR+1;
                                 if(tempC.equalsIgnoreCase(splitR1[j])){
                                      p1++; //word found in R1
                                      splitR1[j]="";//empty the word in R1 to avoid matching it again
                                      rankingT.add(i);
                                      rankingR.add(j);
                                      break;//comes out of the j loop
                                 } 
                                 //checking whther a synonym of the word exists or not
                                 else{
                                      ifBreak = 0; 
                                      synonym_query = "SELECT syn from hindi_syn where word = \"" + splitR1[j]+"\"";
                                      try(ResultSet rs1 = conn.runQuery(synonym_query)){
                                         while(rs1.next()){
                                             
                                             if(tempC.equalsIgnoreCase(rs1.getString("syn"))){
                                                 p1++; //synonym found!
                                                 splitR1[j]="";
                                                 ifBreak = 1;//indicates synonym found
                                                 
                                                 break; // no need to check next synonym in database
                                             }
                                         }//while ends
                                      } catch(Exception e){}
                                      
                                     if(ifBreak == 1){ // checking to see whether a synonym was found
                                         break;//as synonym is found break from the R1 loop
                                     }
                                 }//else ends
                                 
                             } //for for R1 ends 
                             
                            
                        } // ENDS - for loop traversing the candidate array for 1 gram precision
                        
                        //print the final calculated p1
                        double unigram = p1/noOfwordsIncandi;
                        double r1 = p1/(avgLengthOfReferences);
                        System.out.print("\n p1 = " + unigram  + "\n");
       
                        //splitting for p2
                        temp = R1.replaceAll("[\\n]"," ");
                        splitR1 = temp.replaceAll("[.,!?:;']","").split(" ");
         
                      
                         //Calculating p2
                         
                         //loop checking two consecutive words at a time i.e 2-gram precison
                         for(int h=1,l=0;h<splitC.length;l++,h++){
                                if(l<splitC.length){   
           
                                    //storing the 2 consecutive words
                                    tempC = splitC[l]; //the first word in sequence
                                    tempC1 = splitC[h]; //the second word in sequence
                                    
                                    //loop checking for the pair in R1
                                    for(int k=1,n=0;k<splitR1.length;n++,k++){
                                            if(n<splitR1.length){
                                                
                                                    if(tempC.equalsIgnoreCase(splitR1[n]) && tempC1.equalsIgnoreCase(splitR1[k])){
                                                            p2++; //pair found
                                                            splitR1[n]="";
                                                            break;
                                                     }
                                            }
                                    } //R1 loop ends
                                    
                                  
                                } // ENDS if condition checking whether the current wprd selection lies inside the candidate array
           
                         } // ENDS - for loopp travesing for 2 gram precison
                         
                        //print p2 out
                         double bigram =  p2/(noOfwordsIncandi - 1);
                         double r2 = p2/(avgLengthOfReferences-1);
                        System.out.print("\n p2 = " + bigram + "\n");
       
                         //splitting for p3
                         temp = R1.replaceAll("[\\n]"," ");
                         splitR1 = temp.replaceAll("[.,!?:;']","").split(" ");
         
                      
       
                        //calculating p3
                        
                         //checking three words at a time i.e 3 gram precison
                        for(int a=2,b=1,c=0;a<splitC.length;b++,a++,c++){
                                if(b<splitC.length && c<splitC.length){   
           
                                        tempC = splitC[c];//first word in sequence
                                        tempC1 = splitC[b];//second word in sequence
                                        tempC2 = splitC[a];//third word in sequence
                                        
                                        //loop checking in R1
                                        for(int k=2,l=1,m=0;k<splitR1.length;l++,k++,m++){
                                                if(l<splitR1.length && m<splitR1.length){
                                                            
                                                            if(tempC.equalsIgnoreCase(splitR1[m]) && tempC1.equalsIgnoreCase(splitR1[l]) && tempC2.equalsIgnoreCase(splitR1[k])){
                                                                         p3++;//found the triplet
                                                                         splitR1[m]="";
                                                                         break;
                           
                                                            }
                                                }
                                        }//R1 checking for p3 ends
                                        
                                       
                                       
                                }
                         }//ENDS - 3 gram precison loop   
                        
                        //print p3 value out 
                        double trigram = p3/(noOfwordsIncandi-2);
                        double r3 = p3/(avgLengthOfReferences-2);
                        System.out.print("\n p3 = " + trigram + "\n");
       
                         //splitting for p4
                         temp = R1.replaceAll("[\\n]"," ");
                         splitR1 = temp.replaceAll("[.,!?:;']","").split(" ");
         
                     
                        //calculating p4
                         
                        //loop to check the 4 gram precison 
                        for(int d=3,e=2,f=1,g=0;d<splitC.length;d++,e++,f++,g++){
                                if(f<splitC.length && e<splitC.length && g<splitC.length){   
           
                                                tempC = splitC[g];//1st word in sequence
                                                tempC1 = splitC[f];//2nd word in sequence
                                                tempC2 = splitC[e];//3rd word in sequence
                                                tempC3 = splitC[d];//4th word in sequence
                                                
                                                //loop checking for 4 gram precision in R1
                                                for(int k=3,l=2,m=1,n=0;k<splitR1.length;l++,k++,m++,n++){
                                                        if(l<splitR1.length && m<splitR1.length && n<splitR1.length){
                                                                    
                                                                    if(tempC.equalsIgnoreCase(splitR1[n]) && tempC1.equalsIgnoreCase(splitR1[m]) && tempC2.equalsIgnoreCase(splitR1[l]) && tempC3.equalsIgnoreCase(splitR1[k])){
                                                                                p4++;//quadruple found
                                                                                splitR1[n]="";//removing first word in sequence as it wont be matched again
                                                                                break;
                           
                                                                    }
                                                        }
                                                }//loop ends for checking in R1
                                                
                                                
                                }
                        } // ENDS - loop for 4 gram precision
                        
                        //print p4 value out
                        double quadgram = p4/(noOfwordsIncandi-3);
                        double r4 = p4/(avgLengthOfReferences-3);
                        System.out.print("\n p4 = " + quadgram + "\n");
                        System.out.println("r = "+avgLengthOfReferences);
                        System.out.println("c = "+noOfwordsIncandi);
                        
                        double AvgP = Math.pow(unigram*bigram*trigram*quadgram, 0.25);
                        System.out.println("AVgP"+AvgP);
                        double PN = (unigram+bigram+trigram+quadgram)/4;
                        double RM = r1; //where M=1
                        double alpha = 0.9;
                        double Fmean = (PN*RM)/(alpha*PN + (1-alpha)*RM);
                        System.out.println("Fmean "+Fmean);
                        double AvgF;
                        if(bigram==0){
                             AvgF = (((unigram*r1)/(alpha*unigram + (1-alpha)*r1)))/4;
                        }
                        
                        else if(trigram==0){
                             AvgF = (((unigram*r1)/(alpha*unigram + (1-alpha)*r1))+((bigram*r2)/(alpha*bigram + (1-alpha)*r2)))/4;
                        }
                        
                        else if(quadgram==0){
                             AvgF = (((unigram*r1)/(alpha*unigram + (1-alpha)*r1))+((bigram*r2)/(alpha*bigram + (1-alpha)*r2))+((trigram*r3)/(alpha*trigram + (1-alpha)*r3)))/4;
                        }
                        
                        else {
                             AvgF = (((unigram*r1)/(alpha*unigram + (1-alpha)*r1))+((bigram*r2)/(alpha*bigram + (1-alpha)*r2))+((trigram*r3)/(alpha*trigram + (1-alpha)*r3))+((quadgram*r4)/(alpha*quadgram + (1-alpha)*quadgram)))/4;
                        }
                        
                        System.out.println("AvgF "+AvgF);
                        double theta1 = 0.3;
                        double theta2 = 0.5;
                        double score = theta1*AvgP + theta2*Fmean + (1-theta1-theta2)*AvgF;
                        SBP = Math.exp(1 - ((avgLengthOfReferences)/(Math.min(noOfwordsIncandi,avgLengthOfReferences))));
                        SRP = Math.exp(1 - ((Math.max(noOfwordsIncandi,avgLengthOfReferences))/(avgLengthOfReferences)));
                        System.out.println("SBP "+SBP);
                        System.out.println("SRP "+SRP);
                        double gamma = 0.1;
                        double beta = 3;
                        CSBP = Math.exp(1 - ((charRefLen)/(Math.min(charCandLen,charRefLen))));
                        CSRP = Math.exp(1 - ((Math.max(charCandLen,charRefLen))/(charRefLen)));
                        System.out.println("CSBP "+CSBP);
                        System.out.println("CSRP "+CSRP);
                        CKP = 1 - gamma*(Math.pow((p2-p1)/(p1),beta));
                        System.out.println("CKP "+CKP);
                        CTP =  Math.exp(((bigram/(unigram -2))+(trigram/(bigram-3))+(quadgram/(trigram-4)))*(-1/3));
                        System.out.println("CTP "+CTP);
                        SWDP = Math.exp(-(Math.abs(shortwordT - shortwordR)/avgLengthOfReferences));
                        System.out.println("SWDP "+SWDP);
                        LWDP = Math.exp(-(Math.abs(longwordT - longwordR)/avgLengthOfReferences));
                        System.out.println("LWDP "+LWDP);
                        double SpearmansCorrelation;
                        double sum = 0;
                        if(rankingR.isEmpty()){
                            System.out.println("LOL");
                        }
                        for (int i =0;i<rankingR.size();i++){
                            sum = sum + Math.pow((rankingT.get(i)- rankingR.get(i)),2);
                        }
                        System.out.println("SUm "+sum);
                        SpearmansCorrelation = 1 - ((sum)/((rankingR.size()+1)*(rankingR.size())*(rankingR.size()-1)));
                        System.out.println("SC "+SpearmansCorrelation);
                        NSCP = (1+SpearmansCorrelation)/2;
                        System.out.println("NSCP "+NSCP);
                        double NumOfIncreasingPairs = 0;
                        for(int i = 0;i<rankingT.size()-1;i++){
                            for(int j = i+1;j<rankingT.size();j++){
                                if(rankingT.get(j)>rankingT.get(i))NumOfIncreasingPairs=NumOfIncreasingPairs+1;
                            }
                        }
                        double KendallsCorrelation = 2*((NumOfIncreasingPairs)/((rankingR.size()*(rankingR.size()-1))/2))-1;
                        NKCP = (1+KendallsCorrelation)/2;
                        
                        double penalty = Math.pow(SBP,0.3)*Math.pow(SRP,0.1)*Math.pow(CSBP,0.15)*Math.pow(CSRP,0.05)*Math.pow(SWDP,0.1)*Math.pow(LWDP,0.2)*Math.pow(CKP,1.0)*Math.pow(CTP,0.8)*Math.pow(NSCP,0.5)*Math.pow(NKCP,2.0);
                        AMBER = score * penalty;
                        System.out.println(score);
                        System.out.println(penalty);
                        System.out.println("AMBER score ="+AMBER);
                } //if loop checking node type ends
                
               //resetting all values for next set of reference and candidate 
               p1 = 0; p2 =0; p3 = 0; p4=0; AMBER =0 ; SBP = 0; SRP=0;CSBP=0;CSRP=0;SWDP=0;LWDP=0;CKP=0;CTP=0;NSCP=0;NKCP=0;shortwordR=0;shortwordT=0;longwordR=0;longwordT=0;
            }// for loop reading the xml file ends
        
            
    }//main loop ends
    
} //program end
