/*
 * Copyright (C) 2008 The ChinesePod Dictionary Project
 *
 * Praxis Language Ltd.	http://praxislanguage.com
 * Website: ChinesePod	http://chinesepod.com
 * Author: liwenqin
 * Email: yiizzhoou@gmail.com
 * 
 */
package com.chinesepod.decks.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ComposeShader;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract.Directory;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.speech.RecognizerIntent;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.R.id;
import com.chinesepod.decks.logic.CPDecksAudio;
import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksTranslation;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;

public class CPDecksUtility {

	public static final long FILE_SIZE_UNKNOWN = -1;
	private static HashMap<String, String> pinyinMap2;
	public static String CHINESE_PRONOUNS = "他她它";

	static {
		pinyinMap2 = new HashMap<String, String>();
		pinyinMap2.put("v1", "ǖ");
		pinyinMap2.put("v2", "ǘ");
		pinyinMap2.put("v3", "ǚ");
		pinyinMap2.put("v4", "ǜ");
		pinyinMap2.put("v5", "ü");
		pinyinMap2.put("ve1", "ǖe");
		pinyinMap2.put("ve2", "ǘe");
		pinyinMap2.put("ve3", "ǚe");
		pinyinMap2.put("ve4", "ǜe");
		pinyinMap2.put("ve5", "üe");
		pinyinMap2.put("vn1", "ǖn");
		pinyinMap2.put("vn2", "ǘn");
		pinyinMap2.put("vn3", "ǚn");
		pinyinMap2.put("vn4", "ǜn");
		pinyinMap2.put("vn5", "ün");

		pinyinMap2.put("a1", "�?");
		pinyinMap2.put("a2", "á");
		pinyinMap2.put("a3", "ǎ");
		pinyinMap2.put("a4", "à");
		pinyinMap2.put("a5", "a");
		pinyinMap2.put("ai1", "�?i");
		pinyinMap2.put("ai2", "ái");
		pinyinMap2.put("ai3", "ǎi");
		pinyinMap2.put("ai4", "ài");
		pinyinMap2.put("ai5", "ai");
		pinyinMap2.put("an1", "�?n");
		pinyinMap2.put("an2", "án");
		pinyinMap2.put("an3", "ǎn");
		pinyinMap2.put("an4", "àn");
		pinyinMap2.put("an5", "an");
		pinyinMap2.put("ao1", "�?o");
		pinyinMap2.put("ao2", "áo");
		pinyinMap2.put("ao3", "ǎo");
		pinyinMap2.put("ao4", "ào");
		pinyinMap2.put("ao5", "ao");
		pinyinMap2.put("ang1", "�?ng");
		pinyinMap2.put("ang2", "áng");
		pinyinMap2.put("ang3", "ǎng");
		pinyinMap2.put("ang4", "àng");
		pinyinMap2.put("ang5", "ang");

		pinyinMap2.put("e1", "ē");
		pinyinMap2.put("e2", "é");
		pinyinMap2.put("e3", "ě");
		pinyinMap2.put("e4", "è");
		pinyinMap2.put("e5", "e");
		pinyinMap2.put("en1", "ēn");
		pinyinMap2.put("en2", "én");
		pinyinMap2.put("en3", "ěn");
		pinyinMap2.put("en4", "èn");
		pinyinMap2.put("en5", "en");
		pinyinMap2.put("er1", "ēr");
		pinyinMap2.put("er2", "ér");
		pinyinMap2.put("er3", "ěr");
		pinyinMap2.put("er4", "èr");
		pinyinMap2.put("er5", "er");
		pinyinMap2.put("eng1", "ēng");
		pinyinMap2.put("eng2", "éng");
		pinyinMap2.put("eng3", "ěng");
		pinyinMap2.put("eng4", "èng");
		pinyinMap2.put("eng5", "eng");
		pinyinMap2.put("ei1", "ēi");
		pinyinMap2.put("ei2", "éi");
		pinyinMap2.put("ei3", "ěi");
		pinyinMap2.put("ei4", "èi");
		pinyinMap2.put("ei5", "ei");

		pinyinMap2.put("i1", "ī");
		pinyinMap2.put("i2", "í");
		pinyinMap2.put("i3", "�?");
		pinyinMap2.put("i4", "ì");
		pinyinMap2.put("i5", "i");
		pinyinMap2.put("in1", "īn");
		pinyinMap2.put("in2", "ín");
		pinyinMap2.put("in3", "�?n");
		pinyinMap2.put("in4", "ìn");
		pinyinMap2.put("in5", "in");
		pinyinMap2.put("ing1", "īng");
		pinyinMap2.put("ing2", "íng");
		pinyinMap2.put("ing3", "�?ng");
		pinyinMap2.put("ing4", "ìng");
		pinyinMap2.put("ing5", "ing");

		pinyinMap2.put("o1", "�?");
		pinyinMap2.put("o2", "ó");
		pinyinMap2.put("o3", "ǒ");
		pinyinMap2.put("o4", "ò");
		pinyinMap2.put("o5", "o");
		pinyinMap2.put("ou1", "�?u");
		pinyinMap2.put("ou2", "óu");
		pinyinMap2.put("ou3", "ǒu");
		pinyinMap2.put("ou4", "òu");
		pinyinMap2.put("ou5", "ou");
		pinyinMap2.put("ong1", "�?ng");
		pinyinMap2.put("ong2", "óng");
		pinyinMap2.put("ong3", "ǒng");
		pinyinMap2.put("ong4", "òng");
		pinyinMap2.put("ong5", "ong");

		pinyinMap2.put("u1", "ū");
		pinyinMap2.put("u2", "ú");
		pinyinMap2.put("u3", "ǔ");
		pinyinMap2.put("u4", "ù");
		pinyinMap2.put("u5", "u");
		pinyinMap2.put("ue1", "ūe");
		pinyinMap2.put("ue2", "úe");
		pinyinMap2.put("ue3", "ǔe");
		pinyinMap2.put("ue4", "ùe");
		pinyinMap2.put("ue5", "ue");
		pinyinMap2.put("ui1", "ūi");
		pinyinMap2.put("ui2", "úi");
		pinyinMap2.put("ui3", "ǔi");
		pinyinMap2.put("ui4", "ùi");
		pinyinMap2.put("ui5", "ui");
		pinyinMap2.put("un1", "ūn");
		pinyinMap2.put("un2", "ún");
		pinyinMap2.put("un3", "ǔn");
		pinyinMap2.put("un4", "ùn");
		pinyinMap2.put("un5", "un");
	}

	public static boolean isConnectionNet(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				int size = info.length;
				for (int i = 0; i < size; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static CharSequence getFormatPlayTime(int milliseconds) {
		String formatTime = "00:00";
		int seconds = (int) milliseconds / 1000;
		if (seconds >= 60) {
			int minutes = (int) seconds / 60;
			int leftSeconds = seconds % 60;
			if (minutes < 10) {
				formatTime = "0" + minutes + ":" + (leftSeconds < 10 ? "0" + leftSeconds : leftSeconds);
			} else {
				formatTime = minutes + ":" + (leftSeconds < 10 ? "0" + leftSeconds : leftSeconds);
			}
		} else {
			formatTime = "00:" + (seconds < 10 ? "0" + seconds : seconds);
		}

		return formatTime;
	}

	public static String getHumanReadLength(long length) {
		if (length < 1024) {
			return length + "B";
		} else {
			return (length / 1024) + "K";
		}
	}

	public static int getFileDownloadStatus(String filepath) {
		int status = 0;
		File file = new File(filepath);
		File tmpFile = new File(filepath + ".info");
		if (tmpFile.exists()) {
			// check if the info file is empty
			return 1;
		} else {
			if (file.exists()) {
				return 2;
			}
		}
		return status;
	}

	// get file length - THIS NEEDS TO BE DONE IN A THREAD!!!
	static public int getUrlFileSize(Context context, String fileUrl) {
		if( fileUrl == null || fileUrl.isEmpty() || ! HttpConnectionHelper.isProperFileUrl(fileUrl) ){
			return -1;
		}
		try {
			return getUrlFileSize(context, Uri.parse(fileUrl));
		}
		catch(Exception e){
			e.printStackTrace();
			return -1;
		}
	}

	// get file length - THIS NEEDS TO BE DONE IN A THREAD!!!
	static public int getUrlFileSize(Context context, Uri fileUrl) {
		int length = -1;
		if( fileUrl == null ){
			return length;
		}
		try {
			URL url = new URL(fileUrl.toString());
			
			HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
			httpConnection.setReadTimeout(10000);
			httpConnection.setConnectTimeout(10000);
			int responseCode = httpConnection.getResponseCode();
			// Log.v(TAG, "responseCode:"+responseCode);
			if (responseCode >= 400) {
				// Log.v(TAG, "Error Code : " + responseCode);
				return -2; // -2 represent access is error
			}
			String sHeader;
			for (int i = 1;; i++) {
				sHeader = httpConnection.getHeaderFieldKey(i);
				if (sHeader != null) {
					// Log.v(TAG, "sHeader:"+sHeader);
					if (sHeader.equalsIgnoreCase("content-length")) {
						try {
							length = Integer.parseInt(httpConnection.getHeaderField(sHeader));
						}
						catch(Exception e){
						}
						break;
					}
				} else {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// Log.v(TAG, "file length:"+ length);
		return length;
	}

	static public boolean isTv() {
		return CPDecksApplication.getContext().getPackageManager().hasSystemFeature("com.google.android.tv");
	}

	/**
	 * Return a string with pinyin special characters
	 * 
	 * @param text
	 * @return
	 */
	public static String getPinyinTextWithTone2__(String text) {
		String pinyinWithTone = text;
		if (pinyinWithTone != null && !"".equals(pinyinWithTone)) {
			Iterator<?> it = pinyinMap2.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Map.Entry) it.next();
				pinyinWithTone = pinyinWithTone.replaceAll((String) entry.getKey(), (String) entry.getValue());
			}
		}
		return pinyinWithTone;
	}
	
	public static boolean isTablet(Context context) {
		Display display = null;
		try {
			display = ((Activity)context).getWindowManager().getDefaultDisplay();
		}
		catch(Exception e){
		}
		
		if( display == null ){
			return false;
		}
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		display.getMetrics(displayMetrics);

		int width = displayMetrics.widthPixels / displayMetrics.densityDpi;
		int height = displayMetrics.heightPixels / displayMetrics.densityDpi;

		double screenDiagonal = Math.sqrt(width * width + height * height);
		return (screenDiagonal >= 7);
	}

	static public String generateTranslationAudioFilePath(CPDecksAudio audio){
		if( audio == null || audio.getAudioUrl() == null || audio.getAudioUrl().isEmpty() ){
			return null;
		}
		String audioDir = FileOperationHelper.AUDIO_FILES_DIR + File.separator + "translations" + File.separator;
		String audioName = new File(audio.getAudioUrl()).getName();
		String audioFile = audioDir+audioName;
		return audioFile;
	}
	
	static public String generateLinguisticFilePath(CPDecksVocabulary obj){
		if( obj == null ){
			return null;
		}
		
		String directory = "custom";
		long id = obj.getId();
		String type = "";
		
		if( obj.getAppDecksContentId() > 0 ){
			directory = "content";
			id = obj.getAppDecksContentId();
		}
		else if( obj.getCpodVocabId() > 0 ){
			directory = "sample_sentences";
			id = obj.getCpodVocabId();
		}
		
		if( obj.getType() == CPDecksVocabulary.TYPE_SENTENCE ){
			type = "sentence";
		}
		
		if( id < 1 ){
			return null;
		}
		
		
		if( id < 1 ){
			id = 9999999;
		}
		
		String audioDir = FileOperationHelper.AUDIO_FILES_DIR + File.separator + directory + File.separator + id + File.separator;
		String audioName = type + id + ".mp3";
		String audioFile = audioDir+audioName;
		return audioFile;
	}

	static public String generateVocabularyImageFilePath(CPDecksVocabulary obj){
		long id = obj.getId();
		String type = "decks";
		
		if( obj instanceof CPDecksContent ){
			 type = "content";
		}
		
		if( id < 1 ){
			id = 9999999;
		}
		
		String imageDir = FileOperationHelper.IMAGE_FILES_DIR  + File.separator + type + File.separator + id + File.separator;
		String imageName = obj.getType() + obj.getId() + ".jpg";
		String imageFile = imageDir+imageName;
		return imageFile;
	}

	public static String generateLinguisticRecordFilePath(CPDecksVocabulary obj){
		if( obj == null ){
			return null;
		}
		
		String directory = "custom";
		long id = obj.getId();
		String type = "";
		
		if( obj.getAppDecksContentId() > 0 ){
			directory = "content";
			id = obj.getAppDecksContentId();
		}
		else if( obj.getCpodVocabId() > 0 ){
			directory = "sample_sentences";
			id = obj.getCpodVocabId();
		}
		
		if( obj.getType() == CPDecksVocabulary.TYPE_SENTENCE ){
			type = "sentence";
		}
		
		if( id < 1 ){
			return null;
		}
		
		String audioDir = FileOperationHelper.AUDIO_FILES_DIR + File.separator + directory + File.separator;
		String audioName = type + id + "_record.mp3";
		String audioFile = audioDir+audioName;
		return audioFile;
	}

	static public Bitmap getReflection(Bitmap bitmap) {
		// We're cropping the height of the reflection to 80
		if( bitmap == null ){
			return null;
		}
		int reflectionH = 80;
		Bitmap reflection = Bitmap.createBitmap(bitmap.getWidth(), reflectionH, Bitmap.Config.ARGB_8888);
		try {
			Bitmap blurryBitmap = Bitmap.createBitmap(bitmap, 0, bitmap.getHeight() - reflectionH, bitmap.getWidth(), reflectionH);
			// cheap and easy scaling algorithm; down-scale it, then
			// upscale it. The filtering during the scale operations
			// will blur the resulting image
			blurryBitmap = Bitmap.createScaledBitmap(Bitmap.createScaledBitmap(blurryBitmap, blurryBitmap.getWidth() / 2, blurryBitmap.getHeight() / 2, true),
					blurryBitmap.getWidth(), blurryBitmap.getHeight(), true);
			// This shader will hold a cropped, inverted,
			// blurry version of the original image
			BitmapShader bitmapShader = new BitmapShader(blurryBitmap, TileMode.CLAMP, TileMode.CLAMP);
			Matrix invertMatrix = new Matrix();
			invertMatrix.setScale(1f, -1f);
			invertMatrix.preTranslate(0, -reflectionH);
			bitmapShader.setLocalMatrix(invertMatrix);

			// This shader holds an alpha gradient
			Shader alphaGradient = new LinearGradient(0, 0, 0, reflectionH, 0x80ffffff, 0x00000000, TileMode.CLAMP);

			// This shader combines the previous two, resulting in a
			// blurred, fading reflection
			ComposeShader compositor = new ComposeShader(bitmapShader, alphaGradient, PorterDuff.Mode.DST_IN);

			Paint reflectionPaint = new Paint();
			reflectionPaint.setShader(compositor);

			// Draw the reflection into the bitmap that we will return
			Canvas canvas = new Canvas(reflection);
			canvas.drawRect(0, 0, reflection.getWidth(), reflection.getHeight(), reflectionPaint);
		} catch (Exception x) {
			return null;
		}
		return reflection;
	}

	public static String getV3idFromAudioUrl(String audioUrl) {
		String v3id = "";

		// Getting the LessonID from such a URL, where in the below example, 0211 is the Lesson ID
		// http://s3contents.chinesepod.com/0211/fe245fd3e266ff2726113b9327bd0aee7654ee67/mp3/64/rec-1188871765296-2.mp3

		Pattern pattern = Pattern.compile("http.*://s3contents.chinesepod.com/([^/]+)/", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(audioUrl);
		if( matcher.find() ){
			v3id = matcher.group(1);
		}
		
		return v3id;
	}

	// Need this function because some words don't have an unique id
	public static int makeAudioIdFromAudioUrl(String audioUrl) {
		int id = 0;

		// Getting the LessonID from such a URL, where in the below example, 0211 is the Lesson ID
		// http://s3contents.chinesepod.com/0211/fe245fd3e266ff2726113b9327bd0aee7654ee67/mp3/64/rec-1188871765296-2.mp3

		Pattern pattern = Pattern.compile(".*/rec-\\d+(\\d{7})-(\\d+)\\.", Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(audioUrl);
		if( matcher.find() ){
			id = Integer.parseInt(matcher.group(2)+matcher.group(1)); // 1188871765296-2 becomes 21765296. Put the small number first to be safe.
		}
		
		return id;
	}
	
	public static String getProperChinesePodUrl(String url) {
		if( url == null ){
			return null;
		}
		
		url = URLDecoder.decode(url); // this decodes already encode characters like %20(space) before we encode it again, otherwise there will be problems. 
		try {
			// This needs to be done so that Chinese characters in the file name are ASCII
			URI uri = new URI(url);
			url = uri.toURL().toString();			
		} catch (Exception e) {
		}
		
		url = url.replaceAll(" ", "%20");
		
		if( url != null && ! url.matches("(?i:https{0,1}://.*)") ){ //?i: makes it case insensitive
			if( ! url.matches("/.*") ){
				return "http://chinesepod.com/"+url;
			}
			else {
				return "http://chinesepod.com"+url;
			}
		}
		return url;
	}
	
	public static String timeDiff(long from){
		return timeDiff(from, (System.currentTimeMillis() / 1000));
	}

	public static String timeDiff(long from, long to){
        long diff = Math.abs(to - from);
        if (diff <= 3600) {
            long minutes = Math.round(diff / 60);
            if (minutes <= 1){
                return "1 minute ago";
            }else{
                return minutes+" minutes ago";
            }
        } else if ((diff < 86400) && (diff > 3600)) {
            long hours = Math.round(diff / 3600);
            if (hours <= 1){
                return "1 hour ago";
            } else {
                return hours+" hours ago";
            }
        } else if (diff >= 86400) {
            long days = Math.round(diff / 86400);
            if (days <= 1){
                return "1 day ago";
            } else if (days > 1 && days <= 7){
                return days+" days ago";
            } else {
                SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
            	return formatter.format(new Date(from*1000));
            }
        }
 		return null;
	}

	private final static String NON_THIN = "[^iIl1\\.,']";
	public static final String GOOGLE_API_KEY = "AIzaSyB3Vl5BMMJDxpJQH5HiFemSpgTrmEMVAlQ";

	private static int textWidth(String str) {
	    return (int) (str.length() - str.replaceAll(NON_THIN, "").length() / 2);
	}

	public static String ellipsize(String text, int max) {

	    if (textWidth(text) <= max)
	        return text;

	    // Start by chopping off at the word before max
	    // This is an over-approximation due to thin-characters...
	    int end = text.lastIndexOf(' ', max - 3);

	    // Just one long word. Chop it off.
	    if (end == -1)
	        return text.substring(0, max-3) + "...";

	    // Step forward as long as textWidth allows.
	    int newEnd = end;
	    do {
	        end = newEnd;
	        newEnd = text.indexOf(' ', end + 1);

	        // No more spaces.
	        if (newEnd == -1)
	            newEnd = text.length();

	    } while (textWidth(text.substring(0, newEnd) + " …") < max);

	    return text.substring(0, end) + " …";
	}

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static String putSpacesAfterCommas(String str) {
		if( str != null ){
			str = str.replaceAll(",", ", ");
			str = str.replaceAll(",  ",", ");
		}
		return str;
	}

	public static void delete(File file) throws IOException{
	    if(file.isDirectory()){
	    	//directory is empty, then delete it
	    	if(file.list().length==0){
	    	   file.delete();
	    	}else{
	    	   //list all the directory contents
	       	   String files[] = file.list();
	       	   for (String temp : files) {
	       	      //construct the file structure
	       	      File fileDelete = new File(file, temp);
	       	      //recursive delete
	       	      delete(fileDelete);
	       	   }
	       	   //check the directory again, if empty then delete it
	       	   if(file.list().length==0){
	       	     file.delete();
	       	   }
	    	}
	    }else{
	    	//if file, then delete it
	    	file.delete();
	    }
	}

	private static int minimum(int a, int b, int c) {
		return Math.min(Math.min(a, b), c);
	}

	public static int computeLevenshteinDistance(CharSequence str1, CharSequence str2) {
		int[][] distance = new int[str1.length() + 1][str2.length() + 1];

		for (int i = 0; i <= str1.length(); i++)
			distance[i][0] = i;
		for (int j = 1; j <= str2.length(); j++)
			distance[0][j] = j;

		for (int i = 1; i <= str1.length(); i++)
			for (int j = 1; j <= str2.length(); j++)
				distance[i][j] = minimum(distance[i - 1][j] + 1, distance[i][j - 1] + 1, distance[i - 1][j - 1]
						+ ((str1.charAt(i - 1) == str2.charAt(j - 1)) ? 0 : 1));

		return distance[str1.length()][str2.length()];
	}

	public static int calculateStringsAccuracy(CharSequence str1, CharSequence str2) {
		int distance = computeLevenshteinDistance(str1, str2);
		Log.i("comparing strings", "\"" + str1 + "\", \"" + str2 + "\"");
		float score = 1;
		// float deduction = (float) distance / ((str2.length() + str1.length()) / 2);
		float deduction = (float) distance / Math.min(str2.length(), str1.length());
		score = score - deduction;
		if (score <= 0) {
			score = 0;
		}

		score = score * 100;

		return (int) score;
	}

	public static boolean canRecognizeAudio(Context context) {
		Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		List<ResolveInfo> speechActivities = context.getPackageManager().queryIntentActivities(speechIntent, 0);
		return (speechActivities.size() != 0);
	}

	public static String makeStringsSimilarForComparison(String punctuatedString, String unpunctuatedString) {
		boolean stringsAreSameLength = true;
		
		// First we see if we are working with strings that are identical in length or not, with no punctuation and spaces
		unpunctuatedString = unpunctuatedString.replaceAll("[\\p{Punct}\\p{Space}]","");
		String punctuatedStringUnpunctuated = punctuatedString.replaceAll("[\\p{Punct}\\p{Space}]","");
		if( punctuatedStringUnpunctuated.length() == unpunctuatedString.length() ){
			stringsAreSameLength = true;
		}
		
		// Second we make sure we have a totally unpunctuated string, and replace the pronouns 他， 她，and 它 with the right ones
	    String patternStr = "["+CHINESE_PRONOUNS+"]";
	    Pattern pattern = Pattern.compile(patternStr);
	    Matcher matcher = pattern.matcher(punctuatedStringUnpunctuated);
    	while(matcher.find()){
    		int index = matcher.start();
    		String correctPronoun = matcher.group();
    		if( index < unpunctuatedString.length() && unpunctuatedString.substring(index, index).matches("["+CHINESE_PRONOUNS+"]") ){
				unpunctuatedString = unpunctuatedString.substring(0, index) + correctPronoun + unpunctuatedString.substring(index);
    		}
    	}

		// Third we put on the new string any punctuation at the beginning or end, and then remove punctuation from the end of the punctuated String so that it won't mess up regular expression matching to come
		unpunctuatedString = punctuatedString.replaceAll("^([\\p{Punct}\\p{Space}]{0,1}).*?([\\p{Punct}\\p{Space}]{0,1})$", "$1"+unpunctuatedString+"$2");
		punctuatedString = punctuatedString.replaceAll("^(.*?)[\\p{Punct}\\p{Space}]$", "$1");
		
	    patternStr = ".[\\p{Punct}\\p{Space}]";
	    pattern = Pattern.compile(patternStr);
	    matcher = pattern.matcher(punctuatedString);
    	while(matcher.find()){
    		int index = matcher.start();
    		char charToMatch = punctuatedString.charAt(index);
    		char punctuation = punctuatedString.charAt(index+1);
    		if( index < unpunctuatedString.length() ){
	    		char recordedChar = unpunctuatedString.charAt(index);
    			if( index < unpunctuatedString.length() ){
    				if( recordedChar != charToMatch && ! stringsAreSameLength ){
    					punctuation = '#'; // just a place holder.... removed once out of this loop
    				}
					unpunctuatedString = unpunctuatedString.substring(0, index+1) + punctuation + unpunctuatedString.substring(index+1);
    			}
    			else {
    				break;
    			}
    		}
    	}
    	unpunctuatedString = unpunctuatedString.replace("#", "");
    	
    	return unpunctuatedString;
	}

	public static boolean isAnotationAudioFileDownloaded(CPDecksVocabulary wordOrSentence) {
		return isAnotationAudioFileDownloaded(wordOrSentence, false);
	}

	public static boolean isAnotationAudioFileDownloaded(CPDecksVocabulary wordOrSentence, boolean isDictation) {
		if( wordOrSentence == null || wordOrSentence.getId() < 1 ){
			return false;
		}

		if (isDictation) {
			wordOrSentence.setType(CPDecksVocabulary.TYPE_DICTATION);
		}
		
		File audio = new File(wordOrSentence.getTargetAudio().getAudioFile());
		if (audio.exists() && audio.canRead()) {
			return true;
		}
		return false;
	}

	public static boolean isAudioRecorded(CPDecksVocabulary wordOrSentence) {
		String fileName = wordOrSentence.getRecordAudioFile();

		File audio = new File(fileName);
		if (audio.exists() && audio.canRead()) {
			return true;

		}
		return false;

	}
	
	public static boolean copy(File src, File dest) {
		boolean result = false; 
		
        if(src.isDirectory()){
            //if directory not exists, create it
            if(!dest.exists()){
               result = dest.mkdirs();
               if( ! result ){
            	   return false;
               }
            }
            //list all the directory contents
            String files[] = src.list();

            for (String file : files) {
               //construct the src and dest file structure
               File srcFile = new File(src, file);
               File destFile = new File(dest, file);
               //recursive copy
               result = copy(srcFile,destFile);
               if( ! result ){
            	   return false;
               }
            }
            return true;
        }else{
            //if file, then copy it
            //Use bytes stream to support all file types
        	try {
        		File parent = dest.getParentFile();
                if(!parent.exists()){
                    result = parent.mkdirs();
                    if( ! result ){
                 	   return false;
                    }
                 }
        		
                InputStream in = new FileInputStream(src);
                OutputStream out = new FileOutputStream(dest); 

                byte[] buffer = new byte[1024];

                int length;
                //copy the file content in bytes 
                while ((length = in.read(buffer)) > 0){
                   out.write(buffer, 0, length);
                }

                in.close();
                out.close();
                Log.i("Status : ","File copied from " + src + " to " + dest);
        	}
        	catch(Exception e){
        		e.printStackTrace();
        		return false;
        	}
        }
        return true;
    }
	
	public static boolean isValidEmailAddress(String emailAddress) {
		String expression = "^[\\w\\-+]+(\\.[\\w\\-+]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}
}