package com.chinesepod.decks.utility.net;

/**
 * Interface to be used for retrieving data from Network
 * 
 * @author delirium
 */
public interface NetworkUtilityInterface {

	String sendGetRequest(String url, String query);

	String sendPostRequest(String url, String query);

	String uploadFile(String url, String query, String pathToFile);

}
