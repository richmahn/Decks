package com.chinesepod.decks.adapter;

import java.util.ArrayList;
import java.util.Arrays;

import android.R.color;
import android.R.integer;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksDeck;

public class CPDecksSmallDecksGridAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private int mCount;
	private ArrayList<CPDecksDeck> mDeckList;
	private int mSelectedItemPosition;
	private Context mContext;

	public CPDecksSmallDecksGridAdapter(Context context, ArrayList<CPDecksDeck> deckList) {
		mContext = context;
		mDeckList = deckList;
		mCount = deckList.size();
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
			convertView = mInflater.inflate(R.layout.deck_selector_item, null);

			holder = new ViewHolder();
			holder.txtDeckTitle = (TextView) convertView.findViewById(R.id.DeckTitle);
			holder.txtDeckCount = (TextView) convertView.findViewById(R.id.DeckCount);
			holder.imgDeckIcon = (ImageView) convertView.findViewById(R.id.DeckIcon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CPDecksDeck deck = mDeckList.get(position);

		int[] deckGridColorsOrder = mContext.getResources().getIntArray(R.array.DeckGridColorsOrder);
		int colorId = deckGridColorsOrder[position % deckGridColorsOrder.length];
		convertView.setBackgroundColor(colorId);

//		Drawable drawable = mContext.getResources().getDrawable(iconId);
//		drawable = new ScaleDrawable(drawable, 0, 100, 100).getDrawable();
//		drawable.setBounds(0, 0, 100, 100);

		if( deck.getIcon() != null && ! deck.getIcon().isEmpty() ){
			int iconId = mContext.getResources().getIdentifier("deck_icon_"+deck.getIcon(), "drawable", mContext.getPackageName());
			if( iconId > 0 ){
				holder.imgDeckIcon.setImageResource(iconId);
			}
		}

		holder.txtDeckTitle.setText(deck.getTitle());
		holder.txtDeckCount.setText(deck.getCount()+"");
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtDeckTitle;
		TextView txtDeckCount;
		ImageView imgDeckIcon;
	}

	public void refreshUI(ArrayList<CPDecksDeck> deckList) {
		// TODO Auto-generated method stub
		mDeckList = deckList;
		mCount = deckList.size();
		this.notifyDataSetChanged();
	}

	public void setSelectedItemPosition(int position) {
		mSelectedItemPosition = position;

	}

	public int getSelectedItemPosition() {
		return mSelectedItemPosition;
	}

}
