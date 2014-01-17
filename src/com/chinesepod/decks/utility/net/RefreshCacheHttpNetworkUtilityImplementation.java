package com.chinesepod.decks.utility.net;

import java.net.URL;

import com.chinesepod.decks.utility.db.CPDecksApiCacheDBAdapter;

public class RefreshCacheHttpNetworkUtilityImplementation extends HttpNetworkUtilityImplementation {

	@Override
	public String sendGetRequest(String url, String query) {
		String res = null;
		CPDecksApiCacheDBAdapter apiCacheDbHelper = new CPDecksApiCacheDBAdapter();
		try {
			URL urlObj = new URL(url + '?' + query);
			String urlHash = urlObj.getPath() + '?' + urlObj.getQuery();
			res = super.sendGetRequest(url, query);
			String cachedRes = apiCacheDbHelper.getResponse(urlHash);
			if (res != null) {
				if (cachedRes == null || !cachedRes.equals(res)) {
					apiCacheDbHelper.deleteResponse(urlHash); // This will also delete all previous requests for pagination
					apiCacheDbHelper.saveResponse(urlHash, res);
				}
			} else {
				res = cachedRes;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return res;
	}
}
