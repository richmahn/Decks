package com.chinesepod.decks.utility;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.chinesepod.decks.logic.CPDecksContent;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.logic.CPDecksAudio;
import com.chinesepod.decks.utility.net.CPDecksContentNetworkUtilityModel;
import com.chinesepod.decks.utility.net.NetworkUtilityModel;

public class CPDecksContentManager {
	private static Map<Integer, CPDecksContentCategory> mContentTypeMap = new HashMap<Integer, CPDecksContentCategory>();
	private static Map<Long, CPDecksContentCategory> mContentCategoryMap = new HashMap<Long, CPDecksContentCategory>();
	private static Map<Long, CPDecksContent> mContentMap = new HashMap<Long, CPDecksContent>();
	
	public static final int CONTENT_TYPE_WOTD = 1;
	public static final int CONTENT_TYPE_PHRASE = 2;
	public static final int CONTENT_TYPE_WORDLIST = 3;
	
	public static int mFetchMode = NetworkUtilityModel.FIRST_CACHE_THEN_NET;
	private static boolean mHasLoadedContent = false;

	public static void setFetchMode(int mode){
		mFetchMode = mode;
	}
	
	public static boolean generateContentListsFromServer(){
		String contentListJsonString = new CPDecksContentNetworkUtilityModel(mFetchMode).getCompleteContentList();
		
		if( contentListJsonString != null && ! contentListJsonString.isEmpty() ){
			try {
				JSONObject contentListJsonObject = new JSONObject(contentListJsonString);
				
				mContentCategoryMap.clear();
				mContentMap.clear();

				Iterator<String> keys = contentListJsonObject.keys();
				while(keys.hasNext()) {
					String key = keys.next();
				    JSONObject contentJsonObject = contentListJsonObject.getJSONObject(key);
				    CPDecksContent content = getContent(contentJsonObject.getLong("id"));
		            boolean result = processJsonToContent(contentJsonObject, content);
		            if( result ){
		            	CPDecksContentCategory typeContentCategory = mContentTypeMap.get(content.getTypeId());
		            	if( typeContentCategory == null ){
		            		typeContentCategory = new CPDecksContentCategory();
		            		typeContentCategory.setIsType();
		            		typeContentCategory.setId(content.getTypeId());
		            		typeContentCategory.setTitle(contentJsonObject.getString("type_name"));
		            		mContentTypeMap.put((int)typeContentCategory.getId(), typeContentCategory);
		            	}
		            	if( content.getCategoryId() > 0 && content.getTypeId() != 1 ){
		            		typeContentCategory.addChildCategoryId(content.getCategoryId());
		            		CPDecksContentCategory categoryContentCategory = mContentCategoryMap.get(content.getCategoryId());
			            	if( categoryContentCategory == null ){
			            		categoryContentCategory = new CPDecksContentCategory();
			            		categoryContentCategory.setIsCategory();
			            		categoryContentCategory.setId(content.getCategoryId());
			            		categoryContentCategory.setParentId(content.getTypeId());
			            		categoryContentCategory.setTitle(contentJsonObject.getString("category_name"));
			            		mContentCategoryMap.put(categoryContentCategory.getId(), categoryContentCategory);
			            	}
		            		if( content.getSubcategoryId() > 0 ){
			            		categoryContentCategory.addChildCategoryId(content.getSubcategoryId());
			            		CPDecksContentCategory subcategoryContentCategory = mContentCategoryMap.get(content.getSubcategoryId());
				            	if( subcategoryContentCategory == null ){
				            		subcategoryContentCategory = new CPDecksContentCategory();
				            		subcategoryContentCategory.setIsSubcategory();
				            		subcategoryContentCategory.setId(content.getSubcategoryId());
				            		subcategoryContentCategory.setParentId(content.getCategoryId());
				            		subcategoryContentCategory.setTitle(contentJsonObject.getString("subcategory_name"));
				            		mContentCategoryMap.put(subcategoryContentCategory.getId(), subcategoryContentCategory);
				            	}
				            	subcategoryContentCategory.addContentId(content.getAppDecksContentId());
		            		}
		            		else {
		            			categoryContentCategory.addContentId(content.getAppDecksContentId());
		            		}
		            	}
		            	else {
		            		typeContentCategory.addContentId(content.getAppDecksContentId());
		            	}
		            }
				}
				
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
		}
		
		mHasLoadedContent = true;
		return true;
	}
	
	private static boolean processJsonToContent(JSONObject contentJsonObject, CPDecksContent content) {
		if(contentJsonObject == null || ! contentJsonObject.has("id") ){
			return false;
		}
		
		try {
			content.setAppDecksContentId(contentJsonObject.getLong("id"));
			content.setTypeId(contentJsonObject.getInt("type_id"));
			content.setCategoryId(contentJsonObject.getInt("category_id"));
			content.setSubcategoryId(contentJsonObject.getInt("subcategory_id"));
			content.setSource(contentJsonObject.getString("source"));
			content.setTarget(contentJsonObject.getString("target"));
			content.setTargetPhonetics(contentJsonObject.getString("target_phonetics"));
			content.setOrderWeight(contentJsonObject.getInt("order_weight"));
			content.setCreatedAt(contentJsonObject.getString("created_at"));
			content.setUpdatedAt(contentJsonObject.getString("updated_at"));
			
			content.setId(CPDecksVocabularyManager.getVocabularyIdByAppDecksContentId(content.getAppDecksContentId()));
			
			String pubDate = contentJsonObject.getString("pub_date");
			if( pubDate == null || pubDate.equals("null") || pubDate.equals("0000-00-00 00:00:00") ){
				pubDate = "";
			}
			content.setPublishedDate(pubDate);
			
			if( contentJsonObject.has("audio") ){
				String audioUrl = contentJsonObject.getString("audio");
				if( audioUrl != null && ! audioUrl.isEmpty() && audioUrl.startsWith("http") ){
					CPDecksAudio audio = new CPDecksAudio();
					audio.setAudioUrl(audioUrl);
					audio.setAudioFile(CPDecksUtility.generateLinguisticFilePath(content));
					content.setTargetAudio(audio);
				}
			}
			
			if( contentJsonObject.has("image") ){
				String imageUrl = contentJsonObject.getString("image");
				if( imageUrl != null && ! imageUrl.isEmpty() && imageUrl.startsWith("http") ){
					content.setImageUrl(imageUrl);
				}
			}
			return true;
		} catch (JSONException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static CPDecksContentCategory getContentCategoryOfType(int typeId) {
		return mContentTypeMap.get(typeId);
	}

	public static CPDecksContentCategory getContentCategory(long categoryId) {
		return mContentCategoryMap.get(categoryId);
	}

	public class ContentSorter implements Comparator<Long> {
	    @Override
	    public int compare(Long id1, Long id2) {
	    	CPDecksContent content1 = getContent(id1);
	    	CPDecksContent content2 = getContent(id2);
	    	
	    	return content1.compareTo(content2);
	    }
	}

	public class ContentCategorySorter implements Comparator<Long> {
	    @Override
	    public int compare(Long id1, Long id2) {
	    	CPDecksContentCategory category1 = getContentCategory(id1);
	    	CPDecksContentCategory category2 = getContentCategory(id2);
	    	
	    	return category1.compareTo(category2);
	    }
	}
	
	protected static Map<Long, CPDecksContent> getContentMap(){
		if( mContentMap == null ){
			mContentMap = new HashMap<Long, CPDecksContent>();
		}
		return mContentMap;
	}
	
	protected static void addContent(CPDecksContent content){
		if( content == null){
			return;
		}
		mContentMap.put(content.getAppDecksContentId(), content);
	}
	
	public static boolean hasContent(long contentId){
		return getContentMap().containsKey(contentId);
	}

	public static CPDecksContent getContent(long id){
		CPDecksContent content = mContentMap.get(id);
		if( content == null ){
			content = new CPDecksContent();
			content.setId(id);
			mContentMap.put(id, content);
		}
		return content;
	}
	
	protected static Map<Long, CPDecksContentCategory> getContentCategoryMap(){
		if( mContentCategoryMap == null ){
			mContentCategoryMap = new HashMap<Long, CPDecksContentCategory>();
		}
		return mContentCategoryMap;
	}
	
	protected static void addContentCategory(CPDecksContentCategory contentCategory){
		if( contentCategory == null){
			return;
		}
		getContentCategoryMap();
		mContentCategoryMap.put(contentCategory.getId(), contentCategory);
	}
	
	public static boolean hasContentCategory(long contentCategoryId){
		return getContentCategoryMap().containsKey(contentCategoryId);
	}

	public static boolean hasLoadedContent() {
		return mHasLoadedContent;
	}
}
