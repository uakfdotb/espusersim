package com.learningu.espusersim;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

public class Worker extends Thread {
	public Worker() {
		
	}
	
	public void run() {
			try {
			    Thread.sleep(1000 + (int) (Math.random() * 50000));
			} catch(InterruptedException ie) {}
			

		while(true) {
			try {
			    Thread.sleep(100 + (int) (Math.random() * 500));
			} catch(InterruptedException ie) {}
			
			URL url;
			boolean newURL = false;
			
			synchronized(Main.urlList) {
				url = Main.urlList.get(Main.random.nextInt(Main.urlList.size()));
				
				if(!Main.seenURLs.contains(url.toExternalForm())) {
					newURL = true;
					Main.seenURLs.add(url.toExternalForm());
				}
			}
			
			System.out.println("GET " + url + " (" + Main.urlList.size() + ")");
			
			String page;
			HttpURLConnection connection = null;
			
			try {
				connection = (HttpURLConnection) url.openConnection();
				connection.connect();

				if(connection.getResponseCode() == 500 || connection.getResponseCode() == 404 || connection.getResponseCode() == 403) {

				} else if(connection.getHeaderField("Location") != null) {
					synchronized(Main.urlList) {
						Main.blacklistURLs.add(url.toExternalForm());
					}
					
					if(newURL) {
						String changedURLString = connection.getHeaderField("Location");
						URL changedURL = new URL(url, changedURLString);
					
						addURL(changedURL);
					}
				} else if(newURL) {
					Parser parser = new Parser(connection);
					NodeList nodes = parser.parse(new IUSFilter());
					
					for(int i = 0; i < nodes.size(); i++) {
						Node node = nodes.elementAt(i);

						if(node instanceof LinkTag) {
							LinkTag tag = (LinkTag) node;
							String foundLink = tag.extractLink();
							URL changedURL = new URL(url, foundLink);
							
							addURL(changedURL);
						}
					}
				} else {
					BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
					String line;
					
					while((line = in.readLine()) != null);
				}
				
				connection.disconnect();
			} catch(IOException ioe) {
				connection.disconnect();
				ioe.printStackTrace();
				continue;
			} catch(ParserException pe) {
				connection.disconnect();
				pe.printStackTrace();
				continue;
			}
			
			System.out.println("GOT " + url);
		}
	}
	
	public void addURL(URL url) {
	    if((url.getProtocol().equals("http") || url.getProtocol().equals("https")) && (url.getHost().equalsIgnoreCase("esp.mit.edu") || url.getHost().equalsIgnoreCase("esp.mit.edu"))) {
			synchronized(Main.urlList) {
				if(!Main.blacklistURLs.contains(url.toExternalForm())) {
					if(url.toExternalForm().contains("catalog")) {
						for(int i = 0; i < 100; i++) {
							Main.urlList.add(url);
						}
					}
					Main.urlList.add(url);
				}
			}
		}
	}
}
