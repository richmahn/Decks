package com.chinesepod.decks.utility.net;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.X509TrustManager;

import android.util.Log;

/**
 * Http implementation of NetworkUtilityInterface
 * 
 * @author delirium
 * 
 */
public class HttpNetworkUtilityImplementation implements NetworkUtilityInterface {

	private NetworkSettings mNetworkSettings;

	public HttpNetworkUtilityImplementation() {
		mNetworkSettings = new NetworkSettings();
	}

	private void fakeCertificateIfNeeded(String url) throws NoSuchAlgorithmException, KeyManagementException {

		if (url.contains(mNetworkSettings.getTestingHost())) {
			class FakeHostnameVerifier implements HostnameVerifier {
				public boolean verify(String hostname, SSLSession session) {
					return true;
				}
			}
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultHostnameVerifier(new FakeHostnameVerifier());
			HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
		}

	}

	public String sendGetRequest(String url, String query) {
		InputStream response;
Log.i("HTTP",url+"?"+query);		
		try {
			fakeCertificateIfNeeded(url);
			HttpURLConnection connection = (HttpURLConnection) new URL(url + "?" + query).openConnection();
Log.i("GET: ", url + "?" + query);
			connection.setRequestProperty("Accept-Charset", mNetworkSettings.getCharset());
			connection.setConnectTimeout(mNetworkSettings.getConnectTimeOut());
			connection.setReadTimeout(mNetworkSettings.getReadTimeOut());
Log.i("RESPONSE: ", connection.getResponseCode()+" "+connection.getResponseMessage()+" "+connection.getURL());
			if (connection.getResponseCode() == HttpsURLConnection.HTTP_OK)
				response = connection.getInputStream();
			else
				response = connection.getErrorStream();

			return readFromInputStream(connection, response);

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// e.printStackTrace();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	public String sendPostRequest(String url, String query) {
		OutputStream output = null;
Log.i("HTTP",url+"?"+query);		
		try {
			fakeCertificateIfNeeded(url);
			// url = url.replace("https", "http");
			HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(mNetworkSettings.getConnectTimeOut());
			connection.setReadTimeout(mNetworkSettings.getReadTimeOut());
			connection.setDoOutput(true); // Triggers POST.
			connection.setRequestProperty("Accept-Charset", mNetworkSettings.getCharset());
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + mNetworkSettings.getCharset());
			output = connection.getOutputStream();
			if( query != null && ! query.isEmpty() ){
				output.write(query.getBytes(mNetworkSettings.getCharset()));
			}
			InputStream response;
			if (connection.getResponseCode() == 200)
				response = connection.getInputStream();
			else
				response = connection.getErrorStream();
			return readFromInputStream(connection, response);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
				}
		}
		return null;

	}

	@Override
	public String uploadFile(String urlString, String query, String filePath) {

		String lineEnd = "\r\n";
		String twoHyphens = "--";
		String boundary = "**TRYGUHIJU@#$%^&*()NIJMOLUVIGCRF%&F(%&#$%(****";
		URL connectURL;
		try {
			fakeCertificateIfNeeded(urlString);
			urlString = urlString.replace("https://", "http://");
			FileInputStream fileInputStream = new FileInputStream(new File(filePath));
			if( query != null && ! query.isEmpty() ){
				connectURL = new URL(urlString + "?" + query);
			}
			else {
				connectURL = new URL(urlString);
			}

			// Open a HTTP connection to the URL
			HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

			// Allow Inputs
			conn.setDoInput(true);
			// Allow Outputs
			conn.setDoOutput(true);
			// Don't use a cached copy.
			conn.setUseCaches(false);

			// Use a post method.
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
			conn.setRequestProperty("Accept", "*/*");

			DataOutputStream dos = new DataOutputStream(conn.getOutputStream());

			dos.writeBytes(twoHyphens + boundary + lineEnd);
			dos.writeBytes("Content-Disposition: form-data; name=\"uploadfile\";filename=\"" + filePath + "\"" + lineEnd);
			dos.writeBytes(lineEnd);

			// create a buffer of maximum size

			int bytesAvailable = fileInputStream.available();
			int maxBufferSize = 1024;
			int bufferSize = Math.min(bytesAvailable, maxBufferSize);
			byte[] buffer = new byte[bufferSize];

			// read file and write it into form...

			int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

			while (bytesRead > 0) {
				dos.write(buffer, 0, bufferSize);
				bytesAvailable = fileInputStream.available();
				bufferSize = Math.min(bytesAvailable, maxBufferSize);
				bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			}

			// send multipart form data necessary after file data...

			dos.writeBytes(lineEnd);
			dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

			// close streams
			fileInputStream.close();
			dos.flush();
			InputStream is = null;
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
			} else {
				is = conn.getErrorStream();
			}
			// retrieve the response from server
			int ch;

			StringBuffer b = new StringBuffer();
			while ((ch = is.read()) != -1) {
				b.append((char) ch);
			}
			String s = b.toString();
			dos.close();
			return s;

		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return null;
	}

	public String readFromInputStream(URLConnection connection, InputStream response) {
		String charset = "UTF-8";
		try {
			String contentType = connection.getHeaderField("Content-Type");
			charset = mNetworkSettings.getCharset();
			for (String param : contentType.replace(" ", "").split(";")) {
				if (param.startsWith("charset=")) {
					charset = param.split("=", 2)[1];
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (charset != null) {
			BufferedReader reader = null;
			try {
				reader = new BufferedReader(new InputStreamReader(response, mNetworkSettings.getCharset()));
				String result = "";
				for (String line; (line = reader.readLine()) != null;) {
					result += line;
				}
				return result;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return null;
	}

}