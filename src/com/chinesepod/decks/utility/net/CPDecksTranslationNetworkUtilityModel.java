package com.chinesepod.decks.utility.net;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URLEncoder;

import com.chinesepod.decks.Languages;
import com.chinesepod.decks.Languages.Language;


public class CPDecksTranslationNetworkUtilityModel extends NetworkUtilityModel {
	static NetworkSettings mNetworkSettings;
	
	private void init() {
		mNetworkSettings = new NetworkSettings();
	}

	public CPDecksTranslationNetworkUtilityModel() {
		super();
		init();
	}
			
	public CPDecksTranslationNetworkUtilityModel(int mode) {
		super(mode);
		init();
	}
	
	public String getTranslationData(Language sourceLanguage, Language targetLanguage, String text){
		String url = generateLink("get-translation-data");
		String query = "";

		try {
			query = String.format("sl=%s&tl=%s&q=%s", 
					Languages.toLocale(sourceLanguage), 
					Languages.toLocale(targetLanguage),
					URLEncoder.encode(text, mNetworkSettings.getCharset())); 
			String request = getRequest(url, query);
			
			return request;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	/**
     * Returns the url of the first image found using Google image search for the given text
     *
     * @param text The text to search for
     * @throws MalformedURLException
     * @throws IOException
     */
	public String findImage(String text) throws Exception {
		String url = "http://ajax.googleapis.com/ajax/services/search/images";
		String query = "";

		try {
			query = String.format("v=1.0&q=%s", 
					URLEncoder.encode(text, mNetworkSettings.getCharset()));
			String request = getRequest(url, query);
			
			return request;
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	private String generateLink(String functionName) {
		String url = mNetworkSettings.getHost() + "/api";
		if (mNetworkSettings.getApiVersion() != null) {
			url += "/" + mNetworkSettings.getApiVersion();
		}
		url += "/app-decks/" + functionName;
		return url;
	}
}
