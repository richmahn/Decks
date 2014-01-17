package com.chinesepod.decks.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksDeck;

public class CPodDeckAdapter extends BaseAdapter {
	private ArrayList<CPDecksDeck> mDecks;
	private LayoutInflater mInflater;

	public CPodDeckAdapter(Context c, ArrayList<CPDecksDeck> decks) {
		mInflater = LayoutInflater.from(c);
		mDecks = decks;
	}

	public int getCount() {
		return mDecks.size();
	}

	public Object getItem(int position) {
		return mDecks.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.deck, null);
			convertView.setLayoutParams(new GridView.LayoutParams(250, 250));

			holder = new ViewHolder();
			holder.txtDeckTitle = (TextView) convertView.findViewById(R.id.deckTitle);
			holder.txtDeckSize = (TextView) convertView.findViewById(R.id.deckVolume);
			holder.deckFullyCachedIndicator = (ImageView) convertView.findViewById(R.id.deckFullyCachedIndicator);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CPDecksDeck deck = mDecks.get(position);

		holder.txtDeckTitle.setText(deck.getTitle());
		holder.txtDeckSize.setText(deck.getCount() + "");
		
		holder.txtDeckSize.setText(holder.txtDeckSize.getText());
		holder.deckFullyCachedIndicator.setVisibility(View.GONE);

		return convertView;
	}

	static class ViewHolder {
		TextView txtDeckTitle;
		TextView txtDeckSize;
		ImageView deckFullyCachedIndicator;
	}
}
