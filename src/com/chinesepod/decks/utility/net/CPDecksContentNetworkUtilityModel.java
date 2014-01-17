package com.chinesepod.decks.utility.net;

public class CPDecksContentNetworkUtilityModel extends NetworkUtilityModel {
	NetworkSettings mNetworkSettings;
	
	private void init() {
		mNetworkSettings = new NetworkSettings();
	}

	public CPDecksContentNetworkUtilityModel() {
		super();
		init();
	}
			
	public CPDecksContentNetworkUtilityModel(int mode) {
		super(mode);
		init();
	}

	public String getCompleteContentList() {
		return getContentIdList(0, 0, 0, 0, 0);
	}
	public String getContentIdList(int typeId, int categoryId, int subcategoryId, int page, int count) {
		String url = generateLink("get-content-list");
		String query = "";

		try {
			query = String.format("type_id=%s&category_id=%s&subcategory_id=%s&page=%s&count=%s", typeId, categoryId, subcategoryId, page, count);
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
