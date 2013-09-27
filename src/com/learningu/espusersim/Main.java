package com.learningu.espusersim;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class Main {
	public static List<URL> urlList;
	public static Set<String> seenURLs;
	public static Set<String> blacklistURLs;
	public static Random random;

	public static void main(String args[]) throws IOException {
		urlList = new ArrayList<URL>();
		seenURLs = new HashSet<String>();
		blacklistURLs = new HashSet<String>();
		random = new Random();

		urlList.add(new URL("http://esp.mit.edu"));

		for(int i = 0; i < 256; i++) {
			Worker worker = new Worker();
			worker.start();
		}
	}
}
