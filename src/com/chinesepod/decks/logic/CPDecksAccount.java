package com.chinesepod.decks.logic;

import java.util.Locale;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.utility.net.NetworkSettings;

import android.R.integer;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class CPDecksAccount extends Object {
	private static final String DEFAULT_SCREEN_ORIENTATION = "sensor";
	private static final String DELETE_LESSONS_WO_CONF_MSG = "delete_lessons_wo_conf_msg";
	private static final String DOWNLOAD_LESSONS_WO_CONF_MSG = "download_lessons_wo_conf_msg";
	private static final String DELETE_VOCABULARY_WO_CONF_MSG = "delete_vocabulary_wo_conf_msg";
	private static final String DOWNLOAD_VOCABULARY_WO_CONF_MSG = "download_vocabulary_wo_conf_msg";
	private static final String SHOULD_REFRESH_DECK_LIST = "should_refresh_deck_list";
	private static final String IS_VISITOR = "is_visitor";

	private static String CURRENTLY_PLAYING_LESSON_ID = "currently_playing_lesson_id";
	private static String CURRENTLY_PLAYING_LESSON_TYPE = "currently_playing_lesson_type";
	private static String CURRENTLY_PLAYING_AUDIO_ID = "currently_playing_audio";

	public static final String PHONE = "phone";
	public static final String LOCATION_COUNTRY = "location_country";
	public static final String LOCATION_CITY = "location_city";
	public static final String NAME = "fullname";
	public static final String BIO = "bio";

	private String mCurrentlyPlayingLessonId;
	private int mCurrentlyPlayingLessonType;
	private Integer mUserId;
	private String mAccessToken;
	private String mEmail;
	private String mName;
	private String mBio;
	private String mAvatarUrl;
	private String mAvatarFile;
	private String mPassword;
	private String mUsername;
	private String mTargetLanguage;
	private Context mContext;
	private SharedPreferences mAccountPref;
	private Editor mAccountPrefEditor;
	private Boolean mNewLessonNotification;
	private Boolean mNewShowNotification;
	private Boolean mNewsletterNotification;
	private Boolean mGeneralNotification;
	private int mSelfStudyLessonsTotal; 
	private int mCoursesTotal;
	private boolean mIsUsingTraditional;
	private boolean mShowBookmarkedLessons;
	private boolean mShowSubscribedLessons;
	private boolean mShowStudiedLessons;
	private boolean mShowOnlyDownloadedLessons;
	private String mScreenOrientation;
	private int mFlashCardsTimeInterval;
	private String mDeckEndedBehavior;
	private int mFlashCardsWhatToShowFirst;
	private boolean mDownloadMp3OnLessonOpen;
	private String mCurrentlyPlayingAudioId;
	private boolean mDeleteLessonsWithoutConfirmationMessage;
	private boolean mDeleteVocabularyWithoutConfirmationMessage;
	private boolean mDownloadVocabularyWithoutConfirmationMessage;
	private boolean mShouldRefreshDeckList;
	private boolean mShowImagesInLists;
	private String mLocationCountry;
	private String mLocationCity;
	private int mNewMessageCount;
	private boolean mIsVisitor;
	private Boolean mFlashCardsShowTarget;
	private boolean mFlashCardsShowSource;
	private boolean mFlashCardsShowTargetPhonetics;
	private boolean mFlashCardsShowImage;
	private boolean mFlashCardsShowAudio;

	private static CPDecksAccount _instance = null;

	public static CPDecksAccount getInstance() {
		if (_instance == null) {
			_instance = new CPDecksAccount();
		}

		return _instance;
	}

	private CPDecksAccount() {
		mAccountPref = CPDecksApplication.getInstance().getSharedPreferences("account", Context.MODE_PRIVATE);
		mAccountPrefEditor = mAccountPref.edit();
		mUserId = mAccountPref.getInt("user_id", -1);
		mAccessToken = mAccountPref.getString("access_token", null);
		mEmail = mAccountPref.getString("email", null);
		mUsername = mAccountPref.getString("name", null);
		mTargetLanguage = mAccountPref.getString("target_language", null);
		mAvatarUrl = mAccountPref.getString("avatar_url", null);
		mAvatarFile = mAccountPref.getString("avatar_file", null);

		// Load notifications

		mNewLessonNotification = mAccountPref.getBoolean("new_lesson_notification", false);
		mNewShowNotification = mAccountPref.getBoolean("new_show_notification", false);
		mNewsletterNotification = mAccountPref.getBoolean("newsletter_notification", false);
		mGeneralNotification = mAccountPref.getBoolean("general_notification", false);
		mIsUsingTraditional = mAccountPref.getBoolean("use_traditional", false);
		mShowImagesInLists = mAccountPref.getBoolean("show_images_in_lists", true);

		mShowBookmarkedLessons = mAccountPref.getBoolean("show_bookmarked_lessons", true);
		mShowSubscribedLessons = mAccountPref.getBoolean("show_subscribed_lessons", true);
		mShowStudiedLessons = mAccountPref.getBoolean("show_studied_lessons", true);
		
		mScreenOrientation = mAccountPref.getString("orientation", DEFAULT_SCREEN_ORIENTATION);

		// Flashcards
		mFlashCardsTimeInterval = mAccountPref.getInt("flashcards_time_interval", 4000);
		mDeckEndedBehavior = mAccountPref.getString("end_of_deck_behavior", "stop");
		mFlashCardsShowTarget = mAccountPref.getBoolean("flashcards_show_target", true);
		mFlashCardsShowTargetPhonetics = mAccountPref.getBoolean("flashcards_show_target_phonetics", false);
		mFlashCardsShowSource = mAccountPref.getBoolean("flashcards_show_source", false);
		mFlashCardsShowImage = mAccountPref.getBoolean("flashcards_show_image", true);
		mFlashCardsShowAudio = mAccountPref.getBoolean("flashcards_show_audio", true);

		mSelfStudyLessonsTotal = mAccountPref.getInt("self_study_lessons_total", 0); 

		mDownloadMp3OnLessonOpen = mAccountPref.getBoolean("download_on_open", false);
		mCurrentlyPlayingLessonId = mAccountPref.getString(CURRENTLY_PLAYING_LESSON_ID, null);
		mCurrentlyPlayingLessonType = mAccountPref.getInt(CURRENTLY_PLAYING_LESSON_TYPE, -1);

		mCurrentlyPlayingAudioId = mAccountPref.getString(CURRENTLY_PLAYING_AUDIO_ID, "");
		
		mShowOnlyDownloadedLessons = mAccountPref.getBoolean("show_only_downloaded_lessons", false);

		mDeleteLessonsWithoutConfirmationMessage = mAccountPref.getBoolean(DELETE_LESSONS_WO_CONF_MSG, false);

		mDeleteVocabularyWithoutConfirmationMessage = mAccountPref.getBoolean(DELETE_VOCABULARY_WO_CONF_MSG, false);
		mDownloadVocabularyWithoutConfirmationMessage = mAccountPref.getBoolean(DOWNLOAD_VOCABULARY_WO_CONF_MSG, false);

		mShouldRefreshDeckList = mAccountPref.getBoolean(SHOULD_REFRESH_DECK_LIST, false);

		mIsVisitor = mAccountPref.getBoolean(IS_VISITOR, false);
	}

	public void setUserId(int id) {
		mUserId = id;
		mAccountPrefEditor.putInt("user_id", id);
		mAccountPrefEditor.commit();
	}

	public int getUserId() {
		return mUserId;
	}

	public void setEmail(String email) {
		mEmail = email;
		mAccountPrefEditor.putString("email", email);
		mAccountPrefEditor.commit();
	}

	public String getEmail() {
		return mEmail;
	}

	public void setBio(String bio) {
		mBio = bio;
		mAccountPrefEditor.putString("bio", bio);
		mAccountPrefEditor.commit();
	}

	public String getBio() {
		return mBio;
	}

	public void setAvatarUrl(String avatar) {
		mAvatarUrl = avatar;
		mAccountPrefEditor.putString("avatar_url", avatar);
		mAccountPrefEditor.commit();
	}

	public String getAvatarUrl() {
		if( mAvatarUrl != null && ! mAvatarUrl.startsWith("http") ){
			String host = (new NetworkSettings()).getHost();
			if( mAvatarUrl.startsWith("/") ){
				mAvatarUrl = host + mAvatarUrl;
			}
			else {
				mAvatarUrl = host + "/" + mAvatarUrl;
			}
		}
		return mAvatarUrl;
	}

	public void setAvatarFile(String file) {
		mAvatarFile = file;
		mAccountPrefEditor.putString("avatar_file", file);
		mAccountPrefEditor.commit();
	}

	public String getAvatarFile() {
		return mAvatarFile;
	}

	public void setUsername(String name) {
		mUsername = name;
		mAccountPrefEditor.putString("username", name);
		mAccountPrefEditor.commit();
	}

	public String getUsername() {
		return mUsername;
	}

	public void setName(String name) {
		mName = name;
		mAccountPrefEditor.putString("name", name);
		mAccountPrefEditor.commit();
	}

	public String getName() {
		if( mName == null || mName.isEmpty() ){
			return getUsername();
		}
		return mName;
	}

	public void setPassword(String password) {
		mPassword = password;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setTargetLanguage(String language) {
		mTargetLanguage = language;
		mAccountPrefEditor.putString("target_language", language);
		mAccountPrefEditor.commit();
	}

	public String getTargetLanguage() {
		return mTargetLanguage;
	}

	public void setAccessToken(String token) {
		mAccessToken = token;
		mAccountPrefEditor.putString("access_token", token);
		mAccountPrefEditor.commit();
	}

	public String getAccessToken() {
		return mAccessToken;
	}

	public boolean isLoggedIn() {
		if (mAccessToken != null && !mAccessToken.isEmpty()) {
			return true;
		}
		return false;
	}

	public void cleanAllFields() {
		setUserId(-1);
		setAccessToken("");
		setEmail("");
		setName("");
		setBio("");
		setAvatarUrl("");
		setAvatarFile("");
		setPassword("");
		setUsername("");
		setTargetLanguage("");
		setSelfStudyLessonsTotal(0); 
		useTraditional(false);
		showImagesInLists(true);
		downloadMp3OnLessonOpen(false);
		setOrientation(DEFAULT_SCREEN_ORIENTATION);
		setIsVisitor(false);
	}

	public void useTraditional(boolean b) {
		mIsUsingTraditional = b;
		mAccountPrefEditor.putBoolean("use_traditional", b);
		mAccountPrefEditor.commit();
	}

	public void showImagesInLists(boolean b) {
		mShowImagesInLists = b;
		mAccountPrefEditor.putBoolean("show_images_in_lists", b);
		mAccountPrefEditor.commit();
	}

	public boolean isUsingTraditional() {
		return mIsUsingTraditional;
	}
	
	public boolean showImagesInLists(){
		return mShowImagesInLists;
	}

	public void setShowBookmarkedLessons(String s) {
		if( s.equals("0") || s.toLowerCase(Locale.US).equals("false") ){
			setShowBookmarkedLessons(false);
		}
		else {
			setShowBookmarkedLessons(true);
		}
	}

	public void setShowBookmarkedLessons(boolean b) {
		mShowBookmarkedLessons = b;
		mAccountPrefEditor.putBoolean("show_bookmarked_lessons", b);
		mAccountPrefEditor.commit();
	}

	public boolean getShowBookmarkedLessons() {
		return mShowBookmarkedLessons;
	}

	public void setShowSubscribedLessons(String s) {
		if( s.equals("0") || s.toLowerCase(Locale.US).equals("false") ){
			setShowSubscribedLessons(false);
		}
		else {
			setShowSubscribedLessons(true);
		}
	}

	public void setShowSubscribedLessons(boolean b) {
		mShowSubscribedLessons = b;
		mAccountPrefEditor.putBoolean("show_subscribed_lessons", b);
		mAccountPrefEditor.commit();
	}

	public boolean getShowSubscribedLessons() {
		return mShowSubscribedLessons;
	}

	public void setShowStudiedLessons(String s) {
		if( s.equals("0") || s.toLowerCase(Locale.US).equals("false") ){
			setShowStudiedLessons(false);
		}
		else {
			setShowStudiedLessons(true);
		}
	}

	public void setShowStudiedLessons(boolean b) {
		mShowStudiedLessons = b;
		mAccountPrefEditor.putBoolean("show_studied_lessons", b);
		mAccountPrefEditor.commit();
	}

	public boolean getShowStudiedLessons() {
		return mShowStudiedLessons;
	}

	public void setNewLessonNotification(Boolean value) {
		mNewLessonNotification = value;
		mAccountPrefEditor.putBoolean("new_lesson_notification", value);
		mAccountPrefEditor.commit();
	}

	public void setNewLessonNotification(String string) {
		if (string.equals("on") || string.equals("true") || string.equals("1"))
			mNewLessonNotification = true;
		else
			mNewLessonNotification = false;

		mAccountPrefEditor.putBoolean("new_lesson_notification", mNewLessonNotification);
		mAccountPrefEditor.commit();
	}

	public Boolean getNewLessonNotification() {
		return mNewLessonNotification;
	}

	public void setNewShowNotification(Boolean value) {
		mNewShowNotification = value;
		mAccountPrefEditor.putBoolean("new_show_notification", value);
		mAccountPrefEditor.commit();
	}

	public void setNewShowNotification(String string) {
		if (string.equals("on") || string.equals("true") || string.equals("1"))
			mNewShowNotification = true;
		else
			mNewShowNotification = false;

		mAccountPrefEditor.putBoolean("new_lesson_notification", mNewShowNotification);
		mAccountPrefEditor.commit();
	}

	public Boolean getNewShowNotification() {
		return mNewLessonNotification;
	}

	public void setNewsletterNotification(Boolean value) {
		mNewsletterNotification = value;
		mAccountPrefEditor.putBoolean("newsletter_notification", value);
		mAccountPrefEditor.commit();
	}

	public void setNewsletterNotification(String string) {
		if (string.equals("on") || string.equals("true") || string.equals("1"))
			mNewsletterNotification = true;
		else
			mNewsletterNotification = false;

		mAccountPrefEditor.putBoolean("newsletter_notification", mNewsletterNotification);
		mAccountPrefEditor.commit();
	}

	public Boolean getNewsletterNotification() {
		return mNewsletterNotification;
	}

	public void setGeneralNotification(Boolean value) {
		mGeneralNotification = value;
		mAccountPrefEditor.putBoolean("general_notification", value);
		mAccountPrefEditor.commit();
	}

	public void setGeneralNotification(String string) {
		if (string.equals("on") || string.equals("true") || string.equals("1"))
			mGeneralNotification = true;
		else
			mGeneralNotification = false;

		mAccountPrefEditor.putBoolean("general_notification", mGeneralNotification);
		mAccountPrefEditor.commit();
	}

	public Boolean getGeneralNotification() {
		return mGeneralNotification;
	}

	public int getSelfStudyLessonsTotal() {
		return mSelfStudyLessonsTotal;
	}

	public void setSelfStudyLessonsTotal(int total) {
		this.mSelfStudyLessonsTotal = total;
		mAccountPrefEditor.putInt("self_study_lessons_total", total);
		mAccountPrefEditor.commit();
	}
  
	public void setCoursesTotal(int coursesTotal) {
		this.mCoursesTotal = coursesTotal;
		mAccountPrefEditor.putInt("courses_total", coursesTotal);
		mAccountPrefEditor.commit();
	}

	public int getCoursesTotal() {
		return mCoursesTotal;
	}

	public void setOrientation(String orientation) {
		this.mScreenOrientation = orientation;
		mAccountPrefEditor.putString("orientation", mScreenOrientation);
		mAccountPrefEditor.commit();
	}

	public String getOrientation() {
		return mScreenOrientation;
	}

	public int getFlashCardsTimeInterval() {
		return mFlashCardsTimeInterval;
	}

	public void setFlashCardsTimeInterval(Integer interval) {
		this.mFlashCardsTimeInterval = interval;
		mAccountPrefEditor.putInt("flashcards_time_interval", interval);
		mAccountPrefEditor.commit();
	}

	public String getDeckEndedBehavior() {
		return mDeckEndedBehavior;
	}

	public void setDeckEndedBehavior(String val) {
		this.mDeckEndedBehavior = val;
		mAccountPrefEditor.putString("end_of_deck_behavior", val);
		mAccountPrefEditor.commit();
	}

	public int getFlashCardsWhatToShowFirst() {
		return mFlashCardsWhatToShowFirst;
	}

	public void setFlashCardsWhatToShowFirst(Integer id) {
		this.mFlashCardsWhatToShowFirst = id;
		mAccountPrefEditor.putInt("what_to_show_first", id);
		mAccountPrefEditor.commit();
	}

	public boolean downloadMp3OnLessonOpen() {
		return mDownloadMp3OnLessonOpen;
	}

	private void downloadMp3OnLessonOpen(boolean b) {
		mDownloadMp3OnLessonOpen = b;
		mAccountPrefEditor.putBoolean("download_on_open", mDownloadMp3OnLessonOpen);
		mAccountPrefEditor.commit();

	}

	public String getCurrentlyPlayingLessonId() {
		return mCurrentlyPlayingLessonId;
	}

	public void setCurrentlyPlayingLessonId(String currentlyPlayingLessonId) {
		this.mCurrentlyPlayingLessonId = currentlyPlayingLessonId;
		mAccountPrefEditor.putString(CURRENTLY_PLAYING_LESSON_ID, currentlyPlayingLessonId);
		mAccountPrefEditor.commit();
	}
	
	public String getCurrentlyPlayingAudioId() {
		return mCurrentlyPlayingAudioId;
	}

	public void setCurrentlyPlayingAudioId(String currentlyPlayingAudioId) {
		this.mCurrentlyPlayingAudioId = currentlyPlayingAudioId;
		mAccountPrefEditor.putString(CURRENTLY_PLAYING_AUDIO_ID, currentlyPlayingAudioId);
		mAccountPrefEditor.commit();
	}

	public int getCurrentlyPlayingLessonType() {
		return mCurrentlyPlayingLessonType;
	}

	public void setCurrentlyPlayingLessonType(int currentlyPlayingLessonType) {
		this.mCurrentlyPlayingLessonType = currentlyPlayingLessonType;
		mAccountPrefEditor.putInt(CURRENTLY_PLAYING_LESSON_TYPE, currentlyPlayingLessonType);
		mAccountPrefEditor.commit();
	}

	public void setShowOnlyDownloadedLessons(Boolean b) {
		mShowOnlyDownloadedLessons = b;
		mAccountPrefEditor.putBoolean("show_only_downloaded_lessons", b);
		mAccountPrefEditor.commit();
	}

	public boolean getShowOnlyDownloadedLessons() {
		return mShowOnlyDownloadedLessons;
	}

	public boolean getDeleteLessonsWithoutConfirmationMessage() {
		return mDeleteLessonsWithoutConfirmationMessage;
	}

	public void setDeleteLessonsWithoutConfirmationMessage(boolean b) {
		this.mDeleteLessonsWithoutConfirmationMessage = b;
		mAccountPrefEditor.putBoolean(DELETE_LESSONS_WO_CONF_MSG, mDeleteLessonsWithoutConfirmationMessage);
		mAccountPrefEditor.commit();
	}

	public boolean getDeleteVocabularyWithoutConfirmationMessage() {
		return mDeleteVocabularyWithoutConfirmationMessage;
	}

	public void setDeleteVocabularyWithoutConfirmationMessage(boolean b) {
		this.mDeleteVocabularyWithoutConfirmationMessage = b;
		mAccountPrefEditor.putBoolean(DELETE_VOCABULARY_WO_CONF_MSG, mDeleteVocabularyWithoutConfirmationMessage);
		mAccountPrefEditor.commit();
	}

	public boolean getDownloadVocabularyWithoutConfirmationMessage() {
		return mDownloadVocabularyWithoutConfirmationMessage;
	}

	public void setDownloadVocabularyWithoutConfirmationMessage(boolean b) {
		this.mDownloadVocabularyWithoutConfirmationMessage = b;
		mAccountPrefEditor.putBoolean(DOWNLOAD_VOCABULARY_WO_CONF_MSG, mDownloadVocabularyWithoutConfirmationMessage);
		mAccountPrefEditor.commit();
	}

	public void setShouldRefreshDeckList(boolean b) {
		mShouldRefreshDeckList = b;
		mAccountPrefEditor.putBoolean(SHOULD_REFRESH_DECK_LIST, mShouldRefreshDeckList);
		mAccountPrefEditor.commit();
	}
	public boolean getShouldRefreshDeckList() {
		return mShouldRefreshDeckList;
	}

	public void setLocationCountry(String str) {
		mLocationCountry = str;
	}

	public void setLocationCity(String str) {
		mLocationCity = str;
	}

	public void setNewMessageCount(int count) {
		mNewMessageCount = count;
	}
	
	public int getNewMessageCount(){
		return mNewMessageCount;
	}

	public void setIsVisitor(boolean b) {
		mIsVisitor = b;
		mAccountPrefEditor.putBoolean(IS_VISITOR, b);
		mAccountPrefEditor.commit();
	}
	
	public boolean isVisitor(){
		return mIsVisitor;
	}

	public Boolean getFlashCardsShowTarget() {
		return mFlashCardsShowTarget;
	}
	public void setFlashCardsShowTarget(Boolean value) {
		mFlashCardsShowTarget = value;
		mAccountPrefEditor.putBoolean("flashcards_show_target", value);
		mAccountPrefEditor.commit();
	}

	public Boolean getFlashCardsShowTargetPhonetics() {
		return mFlashCardsShowTargetPhonetics;
	}
	public void setFlashCardsShowTargetPhonetics(Boolean value) {
		mFlashCardsShowTargetPhonetics = value;
		mAccountPrefEditor.putBoolean("flashcards_show_target_phonetics", value);
		mAccountPrefEditor.commit();
	}

	public Boolean getFlashCardsShowSource() {
		return mFlashCardsShowSource;
	}
	public void setFlashCardsShowSource(Boolean value) {
		mFlashCardsShowSource = value;
		mAccountPrefEditor.putBoolean("flashcards_show_source", value);
		mAccountPrefEditor.commit();
	}

	public Boolean getFlashCardsShowImage() {
		return mFlashCardsShowImage;
	}
	public void setFlashCardsShowImage(Boolean value) {
		mFlashCardsShowImage = value;
		mAccountPrefEditor.putBoolean("flashcards_show_image", value);
		mAccountPrefEditor.commit();
	}

	public Boolean getFlashCardsShowAudio() {
		return mFlashCardsShowAudio;
	}
	public void setFlashCardsShowAudio(Boolean value) {
		mFlashCardsShowAudio = value;
		mAccountPrefEditor.putBoolean("flashcards_show_audio", value);
		mAccountPrefEditor.commit();
	}
}
