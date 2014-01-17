package com.chinesepod.decks.utility;

import com.chinesepod.decks.utility.net.CachedHttpNetworkUtilityImplementation;
import com.chinesepod.decks.utility.net.HttpNetworkUtilityImplementation;
import com.chinesepod.decks.utility.net.RefreshCacheHttpNetworkUtilityImplementation;

public class ImplementationFactory {

	public static Object factory(String implName) {
		if (implName.equals("HttpNetworkUtility")) {
			return new HttpNetworkUtilityImplementation();
		} else if (implName.equals("CachedHttpNetworkUtility")) {
			return new CachedHttpNetworkUtilityImplementation();
		} else if (implName.equals("RefreshCacheHttpNetworkUtility")) {
			return new RefreshCacheHttpNetworkUtilityImplementation();
		}
		return null;
	}

}
