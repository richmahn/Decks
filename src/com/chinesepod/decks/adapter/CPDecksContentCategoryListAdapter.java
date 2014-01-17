package com.chinesepod.decks.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksContentCategory;
import com.chinesepod.decks.utility.CPDecksContentManager;
import com.chinesepod.decks.utility.CPDecksUtility;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class CPDecksContentCategoryListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mCount;
	private ArrayList<Long> mContentCategoryIdList;
	private int mSelectedItemPosition;

	public CPDecksContentCategoryListAdapter(Context context, ArrayList<Long> contentIdList) {
		mContentCategoryIdList = contentIdList;
		mCount = contentIdList.size();
		mInflater = LayoutInflater.from(context);
	}

	public int getCount() {
		return mCount;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.content_category_item, null);

			holder = new ViewHolder();
			holder.txtCategoryTitle = (TextView) convertView.findViewById(R.id.categoryTitle);
			holder.txtCategoryDescription = (TextView) convertView.findViewById(R.id.categoryDescription);
			holder.txtCategoryContentCount = (TextView) convertView.findViewById(R.id.categoryContentCount);
			holder.imgCategoryIcon = (ImageView) convertView.findViewById(R.id.categoryIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CPDecksContentCategory contentCategory = CPDecksContentManager.getContentCategory(mContentCategoryIdList.get(position));

		holder.txtCategoryTitle.setText(contentCategory.getTitle());
		holder.txtCategoryDescription.setText(CPDecksUtility.ellipsize(contentCategory.getCategoryDescription(), 60));
		holder.txtCategoryContentCount.setText("Cards: "+contentCategory.getCategoryContentCount());
		
		int resId = 0;
		switch(contentCategory.getType()){
		case CPDecksContentManager.CONTENT_TYPE_PHRASE:
			resId = R.drawable.phrases;
			break;
		case CPDecksContentManager.CONTENT_TYPE_WORDLIST:
			resId = R.drawable.lists;
			break;
		case CPDecksContentManager.CONTENT_TYPE_WOTD:
		default:
			resId = R.drawable.word_of_the_day;
		}

		if( contentCategory.getImageUrl() != null && ! contentCategory.getImageUrl().isEmpty() ){
			UrlImageViewHelper.setUrlDrawable(holder.imgCategoryIcon, contentCategory.getImageUrl(), resId);
		}
		else {
			holder.imgCategoryIcon.setImageResource(resId);
		}
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtCategoryTitle;
		TextView txtCategoryDescription;
		TextView txtCategoryContentCount;
		ImageView imgCategoryIcon;
	}

	public void refreshUI(ArrayList<Long> contentCategoryIdList) {
		// TODO Auto-generated method stub
		mContentCategoryIdList = contentCategoryIdList;
		mCount = contentCategoryIdList.size();
		this.notifyDataSetChanged();
	}

	public void setSelectedItemPosition(int position) {
		mSelectedItemPosition = position;

	}

	public int getSelectedItemPosition() {
		return mSelectedItemPosition;
	}
}
