package com.chinesepod.decks.utility;

import org.json.JSONException;
import org.json.JSONObject;

import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksError;
import com.chinesepod.decks.logic.CPDecksResponse;
import com.chinesepod.decks.utility.net.CPodAccountNetworkUtilityModel;

public class CPodAccountManager {

	/**
	 * Login
	 * 
	 * Uses CPodAccountNetworkUtilityModel object to send request to a server for
	 * logging in. If succeed, parses returned JSON object and sets account's
	 * UserId and AccessToken
	 * 
	 * @param CPDecksAccount
	 *            Account object, in which email and password fields should be
	 *            set
	 * 
	 * @return Boolean Returns true, if logged in successfully, false otherwise
	 */
	public static CPDecksResponse login(CPDecksAccount account) {
		CPDecksResponse response = new CPDecksResponse();
		String accountJsonString = new CPodAccountNetworkUtilityModel().login(account);
		if (accountJsonString != null) {
			try {
				JSONObject accountJson = new JSONObject(accountJsonString);
				
				account.cleanAllFields();

				account.setUserId(accountJson.getInt("user_id"));
				account.setAccessToken(accountJson.getString("access_token"));
				account.setUsername(accountJson.getString("username"));
				account.setName(accountJson.getString("name"));
				account.setBio(accountJson.getString("bio"));
				account.setAvatarUrl(accountJson.getString("avatar_url"));
				account.setNewLessonNotification(accountJson.getString("new_lesson_notification"));
				account.setNewShowNotification(accountJson.getString("new_show_notification"));
				account.setNewsletterNotification(accountJson.getString("newsletter_notification"));
				account.setGeneralNotification(accountJson.getString("general_notification"));
				account.setShowBookmarkedLessons(accountJson.getString("bookmarked_lessons"));
				account.setShowSubscribedLessons(accountJson.getString("subscribed_lessons"));
				account.setShowStudiedLessons(accountJson.getString("studied_lessons"));
				
				
				
				// Totals
				account.setSelfStudyLessonsTotal(accountJson.getInt("self_study_lessons_total"));
				response.setObject(account);
				return response;
			} catch (Exception x) {
				CPDecksError error = new CPDecksError();
				error.setAuthenticationError(true);
				response.setError(error);
			}
		} else {
			CPDecksError error = new CPDecksError();
			error.setNetworkError(true);
			response.setError(error);
		}
		return response;
	}

	/**
	 * Logout
	 * 
	 * Uses AccountNetwroker object to send request to a server for logging out.
	 * If succeed, parses returned JSON object and clears account's data.
	 * 
	 * @param CPDecksAccount
	 *            Account object, in which UserId and AccessToken fields should
	 *            be set
	 * 
	 * @return Boolean Returns true, if logged out successfully, false otherwise
	 */
	public static CPDecksResponse logout(CPDecksAccount account) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().logout(account);

		return getResultOrErrorFromJson(resultJsonString);

	}

	/**
	 * Signup
	 * 
	 * Uses AccountNetwroker object to send request to a server for signing up.
	 * If succeed, parses returned JSON for result code.
	 * 
	 * @param CPDecksAccount
	 *            Account object, in which UserName, Email, Password and
	 *            TargetLanguage fields should be set
	 * 
	 * @return Boolean Returns true, if logged out successfully, false otherwise
	 */
	public static CPDecksResponse signup(CPDecksAccount account) {
		CPDecksResponse response = new CPDecksResponse();
		String accountJsonString = new CPodAccountNetworkUtilityModel().signup(account);
		if (accountJsonString != null) {
			try {
				JSONObject accountJson = new JSONObject(accountJsonString);
				account.setUserId(accountJson.getInt("user_id"));
				account.setAccessToken(accountJson.getString("access_token"));
				account.setName(accountJson.getString("name"));
				account.setBio(accountJson.getString("bio"));
				account.setAvatarUrl(accountJson.getString("avatar_url"));
				account.setLocationCountry(accountJson.optString(CPDecksAccount.LOCATION_COUNTRY));
				account.setLocationCity(accountJson.optString(CPDecksAccount.LOCATION_CITY));
				// account.setNewLessonNotification(accountJson.getString("new_lesson_notification"));

				// account.setAssignedLessonsTotal(accountJson.getInt("assigned_lessons_total"));
				return response;
			} catch (Exception x) {
				try {
					JSONObject errorJson = new JSONObject(accountJsonString);
					CPDecksError error = new CPDecksError();
					error.setErrorCode(errorJson.getString("error_code"));
					error.setErrorExpalanation(errorJson.getString("error"));
					response.setError(error);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			CPDecksError error = new CPDecksError();
			error.setNetworkError(true);
			response.setError(error);
		}
		return response;
	}

	public static CPDecksResponse changeUsername(CPDecksAccount account, String newValue) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changeUsername(account, newValue);
		if (resultJsonString != null) {
			return getResultOrErrorFromJson(resultJsonString);
		}
		return null;
	}

	public static CPDecksResponse changeFullname(CPDecksAccount account, String newValue) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changeFullname(account, newValue);

		return getResultOrErrorFromJson(resultJsonString);

	}

	public static CPDecksResponse changeBio(CPDecksAccount account, String newValue) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changeBio(account, newValue);

		return getResultOrErrorFromJson(resultJsonString);

	}

	public static CPDecksResponse changePassword(CPDecksAccount account, String oldPassword, String newPassword) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changePassword(account, oldPassword, newPassword);

		return getResultOrErrorFromJson(resultJsonString);

	}

	public static CPDecksResponse uploadAvatar(CPDecksAccount account, String pathToFile) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().uploadAvatar(account, pathToFile);

		return getResultOrErrorFromJson(resultJsonString);

	}

	public static CPDecksResponse changeNotificationLevel(CPDecksAccount account, String key, String value) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changeNotificationLevel(account, key, value);

		return getResultOrErrorFromJson(resultJsonString);

	}

	public static CPDecksResponse changeShowBookmarkedLessons(CPDecksAccount account, Boolean newValue) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changeShowBookmarkedLessons(account, newValue);
		if (resultJsonString != null) {
			return getResultOrErrorFromJson(resultJsonString);
		}
		return null;
	}

	public static CPDecksResponse changeShowSubscribedLessons(CPDecksAccount account, Boolean newValue) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changeShowSubscribedLessons(account, newValue);
		if (resultJsonString != null) {
			return getResultOrErrorFromJson(resultJsonString);
		}
		return null;
	}

	public static CPDecksResponse changeShowStudiedLessons(CPDecksAccount account, Boolean newValue) {
		String resultJsonString = new CPodAccountNetworkUtilityModel().changeShowStudiedLessons(account, newValue);
		if (resultJsonString != null) {
			return getResultOrErrorFromJson(resultJsonString);
		}
		return null;
	}

	static CPDecksResponse getResultOrErrorFromJson(String jsonString) {
		CPDecksResponse response = new CPDecksResponse();
		try {
			JSONObject resultJson = new JSONObject(jsonString);
			response.setObject(resultJson);
			if (resultJson.has("error_code")) {
				CPDecksError error = new CPDecksError();
				error.setErrorCode(resultJson.getString("error_code"));
				error.setErrorExpalanation(resultJson.getString("error"));
				error.setAuthenticationError(true);
				response.setError(error);
				return response;
			} else if (resultJson.has("result")) {
				if (resultJson.has("value"))
					response.setObject(resultJson.getString("value"));
				else if (!resultJson.getString("result").equals("OK")) {
					CPDecksError error = new CPDecksError();
					response.setError(error);
				}
				return response;
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
		CPDecksError error = new CPDecksError();
		error.setNetworkError(true);

		response.setError(error);
		return response;
	}

	/**
	 * retrievePassword
	 * 
	 * @param String
	 *            email address, which will receive the password via email
	 * 
	 * @return Boolean Returns true, if password sent successfully, false otherwise
	 */
	public static CPDecksResponse retrievePassword(String email) {
		CPDecksResponse response = new CPDecksResponse();
		String responseJsonString = new CPodAccountNetworkUtilityModel().retrievePassword(email);
		if (responseJsonString != null) {
			try {
				JSONObject responseJson = new JSONObject(responseJsonString);
				if( responseJson.has("error") ){
					CPDecksError error = new CPDecksError();
					error.setErrorCode(responseJson.getString("error_code"));
					error.setErrorExpalanation(responseJson.getString("error"));
					response.setError(error);
				}
				response.setObject(responseJson);
				return response;
			} catch (Exception x) {
				CPDecksError error = new CPDecksError();
				error.setAuthenticationError(true);
				response.setError(error);
			}
		} else {
			CPDecksError error = new CPDecksError();
			error.setNetworkError(true);
			response.setError(error);
		}
		return response;
	}

	public static CPDecksResponse signupAsVisitor() {
		CPDecksResponse response = new CPDecksResponse();
		String accountJsonString = new CPodAccountNetworkUtilityModel().signupAsVisitor();
		if (accountJsonString != null) {
			CPDecksAccount account = CPDecksAccount.getInstance();
			try {
				JSONObject accountJson = new JSONObject(accountJsonString);
				
				account.cleanAllFields();
				
				account.setUserId(accountJson.getInt("user_id"));
				account.setAccessToken(accountJson.getString("access_token"));
				account.setUsername(accountJson.getString("username"));
				account.setName(accountJson.getString("name"));
				account.setAvatarUrl(accountJson.getString("avatar_url"));
				account.setNewLessonNotification(true);
				account.setNewShowNotification(true);
				account.setNewsletterNotification(true);
				account.setGeneralNotification(true);
				account.setShowBookmarkedLessons(true);
				account.setShowSubscribedLessons(true);
				account.setShowStudiedLessons(true);
				
				account.setIsVisitor(true);
				
				return response;
			} catch (Exception x) {
				CPDecksError error = new CPDecksError();
				error.setAuthenticationError(true);
				response.setError(error);
			}
		} else {
			CPDecksError error = new CPDecksError();
			error.setNetworkError(true);
			response.setError(error);
		}
		return response;
	}

	public static CPDecksResponse createAccountForVisitor(CPDecksAccount account) {
		CPDecksResponse response = new CPDecksResponse();
		String accountJsonString = new CPodAccountNetworkUtilityModel().updateVisitorAccount(account);
		if (accountJsonString != null) {
			try {
				JSONObject accountJson = new JSONObject(accountJsonString);
				account.setUsername(accountJson.getString("username"));
				account.setAvatarUrl(accountJson.getString("avatar_url"));
				account.setIsVisitor(false);
				return response;
			} catch (Exception x) {
				try {
					JSONObject errorJson = new JSONObject(accountJsonString);
					CPDecksError error = new CPDecksError();
					error.setErrorCode(errorJson.getString("error_code"));
					error.setErrorExpalanation(errorJson.getString("error"));
					response.setError(error);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			CPDecksError error = new CPDecksError();
			error.setNetworkError(true);
			response.setError(error);
		}
		return response;
	}
}
