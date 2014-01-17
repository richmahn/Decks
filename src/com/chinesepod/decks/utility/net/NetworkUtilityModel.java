package com.chinesepod.decks.utility.net;

abstract public class NetworkUtilityModel {
	private NetworkUtilityInterface networkUtilityImplementation;

	public static final int FIRST_CACHE_THEN_NET = 0; 
	public static final int FIRST_NET_THEN_CACHE = 1; 
	public static final int DO_NOT_USE_CACHE = 2;
	public static final int ONLY_USE_CACHE = 3;
	
	public NetworkUtilityModel() {
		this(DO_NOT_USE_CACHE);
	}

	public NetworkUtilityModel(int mode) {
		switch(mode) {
		case DO_NOT_USE_CACHE:
			networkUtilityImplementation = new HttpNetworkUtilityImplementation();
			break;
		case FIRST_NET_THEN_CACHE:
			networkUtilityImplementation = new RefreshCacheHttpNetworkUtilityImplementation();
			break;
		case FIRST_CACHE_THEN_NET:
			networkUtilityImplementation = new CachedHttpNetworkUtilityImplementation();
			break;
		case ONLY_USE_CACHE:
			networkUtilityImplementation = new CachedHttpNetworkUtilityImplementation(true);
			break;
		}
	}
	
	protected String getRequest(String url, String query){
		return networkUtilityImplementation.sendGetRequest(url, query);
	}

	protected String sendPostRequest(String url, String query){
		return networkUtilityImplementation.sendPostRequest(url, query);
	}

	protected String sendGetRequest(String url, String query){
		return networkUtilityImplementation.sendGetRequest(url, query);
	}
	
	protected String uploadFile(String url, String query, String pathToFile) {
		return networkUtilityImplementation.uploadFile(url, query, pathToFile);
	}
}
