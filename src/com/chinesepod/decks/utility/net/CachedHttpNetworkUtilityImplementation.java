package com.chinesepod.decks.utility.net;

import java.net.URL;

import com.chinesepod.decks.utility.db.CPDecksApiCacheDBAdapter;

public class CachedHttpNetworkUtilityImplementation extends HttpNetworkUtilityImplementation {
	protected boolean mRetrieveFromCacheOnly = false;
	
	public CachedHttpNetworkUtilityImplementation(){
		this(false);
	}
	
	public CachedHttpNetworkUtilityImplementation(boolean retrieveFromCacheOnly){
		super();
		mRetrieveFromCacheOnly = retrieveFromCacheOnly;
	}
	
	@Override
	public String sendGetRequest(String url, String query) {
		String res = null;
		CPDecksApiCacheDBAdapter apiCacheDbHelper = new CPDecksApiCacheDBAdapter();
		try {
			URL urlObj = new URL(url + '?' + query);
			String urlHash = urlObj.getPath() + '?' + urlObj.getQuery();
			res = apiCacheDbHelper.getResponse(urlHash);

			if (res == null && ! mRetrieveFromCacheOnly ) {
				res = super.sendGetRequest(url, query);
				if (res != null) {
					apiCacheDbHelper.saveResponse(urlHash, res);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
	}
}
