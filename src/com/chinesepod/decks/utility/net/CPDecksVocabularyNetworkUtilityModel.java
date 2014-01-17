package com.chinesepod.decks.utility.net;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;

/**
 * Model for store related network actions.
 * 
 * Can send receive list of courses from store
 * 
 * @author delirium
 * 
 * @created 2011 Nov 21
 * 
 */
public class CPDecksVocabularyNetworkUtilityModel extends NetworkUtilityModel {
	private NetworkSettings mNetworkSettings;
	private String urlPrefix;

	private void init() {
		mNetworkSettings = new NetworkSettings();
		urlPrefix = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/vocabulary";
	}

	public CPDecksVocabularyNetworkUtilityModel() {
		super();
		init();
	}

	public CPDecksVocabularyNetworkUtilityModel(int mode) {
		super(mode);
		init();
	}

	public String getDecks(CPDecksAccount cPDecksAccount) {
		String url = urlPrefix + "/get-decks";
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s", URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return sendGetRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getWords(CPDecksAccount cPDecksAccount, CPDecksDeck deck) {
		String url = urlPrefix + "/get-words";
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s&deck_title=%s&offset=%s", URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()),
					URLEncoder.encode(deck.getTitle(), mNetworkSettings.getCharset()), deck.getCount());

			return sendGetRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String addWord(CPDecksAccount account, CPDecksVocabulary word) {
		CPDecksDeck deck = new CPDecksDeck();
		return addWord(account, word, deck);
	}

	public String addWord(CPDecksAccount account, CPDecksVocabulary word, CPDecksDeck deck) {
		String url = urlPrefix + "/add-word";
		String query;
		try {
			query = String
					.format("user_id=%s&access_token=%s&source=%s&translate=%s&pinyin=%s&deck_id=%s",
							URLEncoder.encode(account.getUserId() + "", mNetworkSettings.getCharset()),
							URLEncoder.encode(account.getAccessToken(), mNetworkSettings.getCharset()),
							URLEncoder.encode(word.getTarget(), mNetworkSettings.getCharset()),
							URLEncoder.encode(word.getSource(), mNetworkSettings.getCharset()),
							URLEncoder.encode(word.getTargetPhonetics(), mNetworkSettings.getCharset()),
							deck.getId());

			return sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String saveWords(CPDecksAccount account,  List<CPDecksVocabulary> terms, long deckId) {
		String url = urlPrefix + "/add-words";
		String query;
		String termIds = "";
		if (!terms.isEmpty()) {
			for (CPDecksVocabulary term : terms) {
				termIds += term.getId() + ",";
			}
			termIds = termIds.substring(0, termIds.length() - 1);
		}
		try {
			query = String
					.format("user_id=%s&access_token=%s&term_id=%s&deck_id=%s",
							URLEncoder.encode(account.getUserId() + "", mNetworkSettings.getCharset()),
							URLEncoder.encode(account.getAccessToken(), mNetworkSettings.getCharset()),
							termIds, deckId);

			return sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getTermGlossary(CPDecksAccount account, long id, String lessonId) {
		String url = generateLink("get-glossary");
		// ...
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s&term_id=%s&lesson_id=%s",
					URLEncoder.encode(account.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getAccessToken(), mNetworkSettings.getCharset()), id, lessonId);
			return sendGetRequest(url, query);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String searchGlossary(String requestQuery) {
		String url = generateLink("search-word");
		// ...
		String query;
		try {
			query = String.format("keyword=%s", URLEncoder.encode(requestQuery, mNetworkSettings.getCharset()));
			String result = sendGetRequest(url, query);
			if( result != null && ! result.isEmpty() ){
				result = URLDecoder.decode(result, "UTF-8");
			}
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String retrieveGlossarySampleSentences(String chineseWord, int page, int count) {
		String url = generateLink("word-drill-down");
		String query;
		try {
			query = String.format("word=%s&page=%s&count=%s",
					URLEncoder.encode(chineseWord, mNetworkSettings.getCharset()),
					page, count);
			return sendGetRequest(url, query);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String createDeck(CPDecksAccount account, String deckName) {
		String url = generateLink("create-deck");
		// ...
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s&title=%s", URLEncoder.encode(account.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getAccessToken(), mNetworkSettings.getCharset()),
					URLEncoder.encode(deckName, mNetworkSettings.getCharset()));

			return sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String removeDeck(CPDecksAccount account, CPDecksDeck deck) {
		String url = generateLink("delete-deck");
		// ...
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s&deck_id=%s",
					URLEncoder.encode(account.getUserId() + "",
							mNetworkSettings.getCharset()), URLEncoder.encode(
							account.getAccessToken(),
							mNetworkSettings.getCharset()), deck.getId());
			return sendPostRequest(url, query);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String renameDeck(CPDecksAccount account, CPDecksDeck deck, String newName) {
		String url = generateLink("rename-deck");
		// ...
		String query;
		try {
			query = String.format(
					"user_id=%s&access_token=%s&deck_id=%s&new_title=%s", URLEncoder
							.encode(account.getUserId() + "",
									mNetworkSettings.getCharset()), URLEncoder
							.encode(account.getAccessToken(),
									mNetworkSettings.getCharset()), deck
							.getId(), URLEncoder.encode(newName,
							mNetworkSettings.getCharset()));

			return sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String createDeck(CPDecksAccount account, String currentLanguageCode,
			String deckName) {
		String url = generateLink("create-deck");
		// ...
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s&lang=%s&title=%s",
					URLEncoder.encode(account.getUserId() + "",
							mNetworkSettings.getCharset()), URLEncoder.encode(
							account.getAccessToken(),
							mNetworkSettings.getCharset()),
					currentLanguageCode, URLEncoder.encode(deckName,
							mNetworkSettings.getCharset()));

			return sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String removeDeckTerm(CPDecksAccount account, CPDecksDeck deck, CPDecksVocabulary term) {
		String url = generateLink("remove-from-deck");
		// ...
		String query;
		try {
			query = String.format(
					"user_id=%s&access_token=%s&deck_id=%s&term_id=%s",
					URLEncoder.encode(account.getUserId() + "",
							mNetworkSettings.getCharset()), URLEncoder.encode(
							account.getAccessToken(),
							mNetworkSettings.getCharset()), deck.getId(), term
							.getUserVocabId());

			return sendPostRequest(url, query);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String moveDeckTerm(CPDecksAccount account, CPDecksDeck oldDeck,
			CPDecksDeck newDeck, CPDecksVocabulary term) {
		String url = generateLink("move-to-deck");
		// ...
		String query;
		try {
			query = String
					.format("user_id=%s&access_token=%s&old_deck_id=%s&deck_id=%s&term_id=%s",
							URLEncoder.encode(account.getUserId() + "",
									mNetworkSettings.getCharset()), URLEncoder
									.encode(account.getAccessToken(),
											mNetworkSettings.getCharset()),
							oldDeck.getId(), newDeck.getId(), term.getUserVocabId());

			return sendPostRequest(url, query);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String generateLink(String functionName) {
		String url = mNetworkSettings.getHost() + "/api";
		if (mNetworkSettings.getApiVersion() != null) {
			url += "/" + mNetworkSettings.getApiVersion();
		}
		url += "/vocabulary/" + functionName;
		return url;
	}
}
