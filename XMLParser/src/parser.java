//package search;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.Calendar;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

//import javax.lang.model.element.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
//import javax.xml.soap.Node;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.xml.sax.SAXException;


public class parser{
	static Timer timer = new Timer();
	static MyTimerTask timertask = new MyTimerTask();
    @SuppressWarnings({ "resource", "null" })
	public static void main ( String [] args ) throws IOException, ParserConfigurationException, SAXException, TransformerException{
		
		int newInput = new parser().runFrom();
				
		if (newInput == 1) {
			timertask.eraseFile();
			timertask.inputData();
		}
		else if (newInput == 2) {
			//timertask.inputData();
		}
		else {
			System.out.println("Can't read your input, closing...");
			System.exit(0);
		}
		
		timer.scheduleAtFixedRate(timertask, 0, 10000); //10 sec delay
    }
    
    public int runFrom() {
    	int runLocation = 0;
    	String className = this.getClass().getName().replace('.', '/');
		String classJar = this.getClass().getResource("/" + className + ".class").toString();
		
		if (classJar.startsWith("jar:")) {
			runLocation = 2; 
		}
		
		else {
			//int newInput = 0;
			while (runLocation > 2 || runLocation < 1) {
				System.out.println("Enter 1 for new input, 2 to continue using the existing input");
				Scanner input = new Scanner(System.in);
				runLocation = input.nextInt();
			}
		}
		return runLocation;
    }
}
