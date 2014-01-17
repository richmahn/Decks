package com.chinesepod.decks.utility.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.utility.ImplementationFactory;

/**
 * Model for account related network actions.
 * 
 * Can send login, logout, etc requests.
 * 
 * @author delirium
 * 
 */
public class CPodAccountNetworkUtilityModel {

	private NetworkUtilityInterface networkUtilityImplementation;
	private NetworkSettings mNetworkSettings;

	public CPodAccountNetworkUtilityModel() {
		mNetworkSettings = new NetworkSettings();
		networkUtilityImplementation = (NetworkUtilityInterface) ImplementationFactory.factory("HttpNetworkUtility");
	}

	public String login(CPDecksAccount account) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/login";

		// Magic hash generation is here
		String hash = "mh+543#%$^%&^)(^$#35" + account.getEmail().toLowerCase() + mNetworkSettings.getClientPlatform() + account.getPassword()
				+ mNetworkSettings.getClientVersion();

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(hash.getBytes());
			byte[] digest = md.digest();
			BigInteger bigInt = new BigInteger(1, digest);
			hash = bigInt.toString(16);
			// Now we need to zero pad it if you actually want the full 32 chars.
			while (hash.length() < 32) {
				hash = "0" + hash;
			}

		} catch (NoSuchAlgorithmException e) {
		}
		String query;
		try {
			query = String.format("email=%s&hash=%s&client_platform=%s&client_version=%s",
					URLEncoder.encode(account.getEmail(), mNetworkSettings.getCharset()), hash,
					URLEncoder.encode(mNetworkSettings.getClientPlatform(), mNetworkSettings.getCharset()),
					URLEncoder.encode(mNetworkSettings.getClientVersion(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String logout(CPDecksAccount account) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/logout";
		// ...
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s", URLEncoder.encode(account.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendGetRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String signup(CPDecksAccount account) {
		String url = generateLink("signup");
		String query;
		try {
			query = String.format("name=%s&email=%s&password=%s&client_platform=%s&client_version=%s",
					URLEncoder.encode(account.getName(), mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getEmail(), mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getPassword(), mNetworkSettings.getCharset()),
					URLEncoder.encode(mNetworkSettings.getClientPlatform(), mNetworkSettings.getCharset()),
					URLEncoder.encode(mNetworkSettings.getClientVersion(), mNetworkSettings.getCharset()));
			if (account.getAvatarFile() == null || account.getAvatarFile().isEmpty()) {
				return networkUtilityImplementation.sendPostRequest(url, query);
			} else {
				return networkUtilityImplementation.uploadFile(url, query, account.getAvatarFile());
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String retrievePassword(String email) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/retrieve-password";

		String query;
		try {
			query = String.format("email=%s",
					URLEncoder.encode(email, mNetworkSettings.getCharset()));
			String result = networkUtilityImplementation.sendPostRequest(url, query);
			return result;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changeUsername(CPDecksAccount cPDecksAccount, String newValue) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/change-username";
		// ...
		String query;
		try {
			query = String.format("username=%s&user_id=%s&access_token=%s", URLEncoder.encode(newValue, mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changeShowBookmarkedLessons(CPDecksAccount cPDecksAccount, Boolean newValue) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/lesson/self-study-setting";
		// ...
		String query;
		try {
			query = String.format("var=bookmarked_lessons&val=%s&user_id=%s&access_token=%s", URLEncoder.encode((newValue?"1":"0"), mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changeShowSubscribedLessons(CPDecksAccount cPDecksAccount, Boolean newValue) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/lesson/self-study-setting";
		// ...
		String query;
		try {
			query = String.format("var=subscribed_lessons&val=%s&user_id=%s&access_token=%s", URLEncoder.encode((newValue?"1":"0"), mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changeShowStudiedLessons(CPDecksAccount cPDecksAccount, Boolean newValue) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/lesson/self-study-setting";
		// ...
		String query;
		try {
			query = String.format("var=studied_lessons&val=%s&user_id=%s&access_token=%s", URLEncoder.encode((newValue?"1":"0"), mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changeFullname(CPDecksAccount cPDecksAccount, String newValue) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/change-fullname";
		// ...
		String query;
		try {
			query = String.format("fullname=%s&user_id=%s&access_token=%s", URLEncoder.encode(newValue, mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changeBio(CPDecksAccount cPDecksAccount, String newValue) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/change-bio";
		// ...
		String query;
		try {
			query = String.format("bio=%s&user_id=%s&access_token=%s", URLEncoder.encode(newValue, mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changePassword(CPDecksAccount cPDecksAccount, String oldPassword, String newPassword) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/change-password";
		// ...
		String query;
		try {
			query = String.format("oldpassword=%s&newpassword=%s&user_id=%s&access_token=%s", URLEncoder.encode(oldPassword, mNetworkSettings.getCharset()),
					URLEncoder.encode(newPassword, mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String changeNotificationLevel(CPDecksAccount cPDecksAccount, String key, String value) {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/change-notification-level";
		// ...
		String query;
		try {
			if (value.equals("true")) {
				value = "on";
			} else {
				value = "off";
			}
			query = String.format("key=%s&value=%s&user_id=%s&access_token=%s", URLEncoder.encode(key, mNetworkSettings.getCharset()),
					URLEncoder.encode(value, mNetworkSettings.getCharset()), URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.sendPostRequest(url, query);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String uploadAvatar(CPDecksAccount cPDecksAccount, String pathToFile) {

		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/upload-avatar";

		String query;
		try {
			query = String.format("user_id=%s&access_token=%s", URLEncoder.encode(cPDecksAccount.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(cPDecksAccount.getAccessToken(), mNetworkSettings.getCharset()));

			return networkUtilityImplementation.uploadFile(url, query, pathToFile);

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Bitmap downloadAvatar(CPDecksAccount account) {
		Bitmap bitmap = null;
		try {
			String url = account.getAvatarUrl();
			bitmap = BitmapFactory.decodeStream((InputStream) new URL(url).getContent());
			String fNames[] = account.getAvatarUrl().split("/");
			String fName = fNames[fNames.length - 1];
			String pathToFile = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + File.separator + fName;
			File file = new File(pathToFile);
			FileOutputStream fOut = new FileOutputStream(file);

			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
			fOut.flush();
			fOut.close();
			account.setAvatarFile(pathToFile);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return bitmap;
	}

	public String signupAsVisitor() {
		String url = mNetworkSettings.getHost() + "/api/" + mNetworkSettings.getApiVersion() + "/account/try-course";

		try {
			String query = String.format("client_platform=%s&client_version=%s",
					URLEncoder.encode(mNetworkSettings.getClientPlatform(), mNetworkSettings.getCharset()),
					URLEncoder.encode(mNetworkSettings.getClientVersion(), mNetworkSettings.getCharset()));
			return networkUtilityImplementation.sendGetRequest(url, query);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String updateVisitorAccount(CPDecksAccount account) {
		String url = generateLink("update-visitor-account");
		String query;
		try {
			query = String.format("user_id=%s&access_token=%s&name=%s&email=%s&password=%s&client_platform=%s&client_version=%s",
					URLEncoder.encode(account.getUserId() + "", mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getAccessToken(), mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getName(), mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getEmail(), mNetworkSettings.getCharset()),
					URLEncoder.encode(account.getPassword(), mNetworkSettings.getCharset()),
					URLEncoder.encode(mNetworkSettings.getClientPlatform(), mNetworkSettings.getCharset()),
					URLEncoder.encode(mNetworkSettings.getClientVersion(), mNetworkSettings.getCharset()));
			if (account.getAvatarFile() == null || account.getAvatarFile().isEmpty()) {
				return networkUtilityImplementation.sendPostRequest(url, query);
			} else {
				return networkUtilityImplementation.uploadFile(url, query, account.getAvatarFile());
			}
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
		url += "/account/" + functionName;
		return url;

	}

}
