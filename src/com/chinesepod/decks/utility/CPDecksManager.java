package com.chinesepod.decks.utility;

import com.chinesepod.decks.logic.CPDecksError;
import com.chinesepod.decks.logic.CPDecksResponse;

public class CPDecksManager {

	public static CPDecksResponse processRemoteError(CPDecksResponse response, String lessonsJsonString) {
		// TODO parse error here
		CPDecksError error = new CPDecksError();
//		error.setAuthenticationError(true);
		response.setError(error);
		return response;
	}

	public static CPDecksResponse processNetworkError(CPDecksResponse response) {

		CPDecksError error = new CPDecksError();
		error.setNetworkError(true);
		response.setError(error);
		return response;
	}
}
