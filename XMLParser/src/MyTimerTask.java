import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MyTimerTask extends TimerTask {
	public void run() {
		try {
			inputData();
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//parsingData(nList);
	}
	
	@SuppressWarnings("resource")
	public void inputData() throws ParserConfigurationException, MalformedURLException, SAXException, IOException {
		String url = "http://rates.fxcm.com/RatesXML";
    	System.setProperty("http.agent", "Chrome");				//so no 403 error
    	
    	//API for creating tree object from xml
    	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    	//document represents the entire html or xml  document
    	DocumentBuilder db = dbf.newDocumentBuilder();
    	
    	Document doc = db.parse(new URL(url).openStream());
    	String targetRate = null;
    	String targetCurrency;
    	String[] array = new String[2];
    	
    	//transform unicode text into decomposed form that's easier to search
    	doc.getDocumentElement().normalize();
    	//get the node rate
    	NodeList nList = doc.getElementsByTagName("Rate");
    	//nodeParser myParser = new nodeParser();
		
		try {
    		String fileName = "target.txt";
    		FileInputStream fstream = new FileInputStream(fileName);
    	    BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
    	    array[0] = br.readLine();
    	    
    	    if (array[0] == null || array[0].equals("")) {
    	    	Scanner input = new Scanner(System.in);
    	    	System.out.println("Enter the target currency: ");
    	    	
    	    	targetCurrency = input.nextLine();
    	    	
    	    	boolean goodFormat = false;
    	    	
    	    	while (goodFormat == false) {
    	    		System.out.println("Enter the target rate: ");
    	    		targetRate = input.nextLine();
    	    		
	    	    	try {
	    	    		if (Float.parseFloat(targetRate) < 0) {
	    	    			throw new NumberFormatException("No negative number please! Only positive number allowed");
	    	    		}
	    	    		else {
	    	    			goodFormat = true;
	    	    		}
	    	    	}
	    	    	
	    	    	catch (NumberFormatException ex) {
	    	    		System.out.println("Please enter a positive number, no characters/symbol/negative number allowed");	    	    		
	    	    	}
	    	    }
    	    	
    	    	String result = String.format("%s%n%s%n", targetCurrency, targetRate);
    	    	
    	    	br.close();
    	    	BufferedWriter bw = null;
    			FileWriter fw = null;
    			fw = new FileWriter(fileName);
    	        bw = new BufferedWriter(fw);
    	        bw.write(result);
    	        bw.close();

    	        parsingData(targetCurrency, targetRate, nList);
    	    }
    	    
    	    else {
    	    	
	    	    String line;
	    	    int lineNumber = 1;
	    	    while ((line = br.readLine()) != null && lineNumber < 2) {
	    	    	array[lineNumber] = line;
	    	    	lineNumber++;
	    	    }
		    	
	    	    parsingData(array[0], array[1], nList);
	        	
    	    }
    	} catch (Exception e) {// Catch exception if any
    	    System.err.println("Error: " + e.getMessage());
    	}   
    
    }

	public void parsingData(String targetCurrency, String targetRate, NodeList nList) throws IOException {
		final String FileName = "rate.txt";
		//final DateTimeFormatter DateTimeFormatter = null;
		
			BufferedWriter bw = null;
			FileWriter fw = null;
			try {
				fw = new FileWriter(FileName);
			} catch (IOException e1) {
				System.out.println(e1 + " Can't open file");
				e1.printStackTrace();
			}
	        bw = new BufferedWriter(fw);
	        //DateTimeFormatter date = java.time.format.DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	 	   	LocalDateTime now = LocalDateTime.now();
	 	   	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
	 	   	String formattedDateTime = now.format(formatter);
	 	   	String dateUpdated = String.format("Current Date: %s %n", formattedDateTime);
	 	   	try {
				bw.write(dateUpdated);
			} catch (IOException e1) {
				System.out.println(e1 + ": Can't write to file");
				e1.printStackTrace();
			}
	         
	 	   	boolean currencyFound = false;
	 	   	boolean targetRateHit = false;
	 	   	
			for (int temp = 0; temp < nList.getLength(); temp++) {
		        org.w3c.dom.Node nNode = nList.item(temp);

		        if (nNode.getNodeType() == Node.ELEMENT_NODE) {
		           Element node = (Element) nNode;
		           String currency = (node.getAttribute("Symbol")).toString();
		           String currentRate = node.getElementsByTagName("Bid").item(0).getTextContent();
		           float theRate = Float.parseFloat(currentRate);
		           float targetDollar = Float.parseFloat(targetRate);
		          
		           if (currency.equals(targetCurrency.trim().toUpperCase())) {
		        	   currencyFound = true;
		        	   if (theRate >= targetDollar) {	
		        		   targetRateHit = true;
			        	   String sellPrice = node.getElementsByTagName("Ask").item(0).getTextContent();
			        	   String high = node.getElementsByTagName("High").item(0).getTextContent();
			        	   String low = node.getElementsByTagName("Low").item(0).getTextContent();
			        	   
			        	   System.out.println("Your target rate has been reached \n");
			        	   System.out.println("Your input: " + targetDollar);
			               System.out.println("Type of currency: " + currency);
			               System.out.println("Current rate: " + currentRate);
			               System.out.println("Suggested selling price: " + sellPrice);
			               System.out.println("Day high price: " + high);
			               System.out.println("Day low price: " + low);
			               
			               int up = Integer.parseInt(node.getElementsByTagName("Direction").item(0).getTextContent());
			               String direction = null;
			               switch (up) {
				               case -1: direction = "down";
				               			break;
				               case  0: direction = "no change";
				            		   	break;
				               case  1: direction = "up";
				               			break;
				           }
			               
			               String updateDate = node.getElementsByTagName("Last").item(0).getTextContent();
			               System.out.println("Direction of rate: " + direction);
			               System.out.println("Last updated: " + updateDate);
			               
			               String result = String.format("Your target rate has been reached %n Your input: %s %n Type of currency: %s %n Current rate: %s %n Suggested selling price: %s %n Day high: %s %n Day low: %s %n Direction: %s %n Last updated: %s %n"
       		   					,targetDollar, currency, currentRate, sellPrice, high, low, direction, updateDate);
			               
			               JOptionPane.showMessageDialog(null, result);
			               parser.timer.cancel();
			               
			               try {				               			            	   
			            	
				               bw.write(result);
				               bw.close();
			               } catch (IOException e) {
			            	
			            	System.out.println(e + " Can't write to file");
			       			e.printStackTrace();
	
			       		} finally {
	
			       			try {
	
			       				if (bw != null)
			       					bw.close();
	
			       				if (fw != null)
			       					fw.close();
	
			       			} catch (IOException ex) {
	
			       				ex.printStackTrace();
	
			       			}

		       		}
		             
		           }
		        	   
		        	   else {
		        		   break;
		        	   }
		           		        	   
		           }
		           
		        }
			}
			
		//if currency can't be found, make sure to delete that from the file so it won't be checked every time
			if (currencyFound == false) {
	    		//String fileName = "target.txt";

				System.out.println("Your currency cannot be found\nTry Again\n");
				eraseFile();
				
			}
			
			
			if (targetRateHit == false && currencyFound == true) {
				
				System.out.println("Your target rate has not been reached yet, we'll keep checking");
	        	
	           	           
			}
	}
	
	public void eraseFile() throws IOException {
		String fileName = "target.txt";
		BufferedWriter bw2 = null;
		FileWriter fw2 = null;
		fw2 = new FileWriter(fileName);
        bw2 = new BufferedWriter(fw2);
        String emptyString = "";
        bw2.write(emptyString);
        bw2.close();
	}
}
