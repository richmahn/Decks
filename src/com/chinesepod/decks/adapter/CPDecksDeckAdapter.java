package com.chinesepod.decks.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.chinesepod.decks.CPDecksActivity;
import com.chinesepod.decks.R;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;

public class CPDecksDeckAdapter extends BaseAdapter implements ExpandableListAdapter {
	private LayoutInflater mInflater;
	private int mCount;
	private ArrayList<CPDecksDeck> mDeckList;
	private int mSelectedItemPosition;
	private Context mContext;

	public CPDecksDeckAdapter(Context context, ArrayList<CPDecksDeck> deckList) {
		mDeckList = deckList;
		mCount = deckList.size();
		mInflater = LayoutInflater.from(context);
		mContext = context;
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
			convertView = mInflater.inflate(R.layout.deck_item, null);

			holder = new ViewHolder();
			holder.txtDeckName = (TextView) convertView.findViewById(R.id.DeckName);
			holder.txtDeckTotalCount = (TextView) convertView.findViewById(R.id.DeckTotalCount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CPDecksDeck deck = mDeckList.get(position);

		holder.txtDeckName.setText(deck.getTitle());
		holder.txtDeckTotalCount.setText(deck.getCount()+" card"+(deck.getCount()!=1?"s":""));
		
		return convertView;
	}

	static class ViewHolder {
		TextView txtDeckName;
		TextView txtDeckTotalCount;
	}

	public void refreshUI(ArrayList<CPDecksDeck> deckList) {
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

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return null;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return 0;
	}

	@Override
	public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		View v = mInflater.inflate(R.layout.list_deck_manager_buttons, null);
		final CPDecksDeck deck = mDeckList.get(groupPosition);

		if( groupPosition != 0 ){
			v.findViewById(R.id.moveDeckUpButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if( groupPosition > 0){
						mDeckList.remove(deck);
						mDeckList.add(groupPosition-1, deck);
						boolean result = CPDecksVocabularyManager.saveDecksInThisOrder(mDeckList);
						if( ! result ){
							Toast.makeText(mContext, "Unable to rearrange your decks. Please try again.", Toast.LENGTH_SHORT).show();
						}
						if( mContext instanceof CPDecksActivity ){
							((CPDecksActivity)mContext).refreshPage();
						}
					}
				}
			});
		}
		else {
			v.findViewById(R.id.moveDeckUpButton).setVisibility(View.GONE);
		}

		if( groupPosition < (mDeckList.size() - 1) ){
			v.findViewById(R.id.moveDeckDownButton).setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if( groupPosition < mDeckList.size() - 1){
						mDeckList.remove(deck);
						mDeckList.add(groupPosition+1, deck);
						boolean result = CPDecksVocabularyManager.saveDecksInThisOrder(mDeckList);
						if( ! result ){
							Toast.makeText(mContext, "Unable to rearrange your decks. Please try again.", Toast.LENGTH_SHORT).show();
						}
						if( mContext instanceof CPDecksActivity ){
							((CPDecksActivity)mContext).refreshPage();
						}
					}
				}
			});
		}
		else {
			v.findViewById(R.id.moveDeckDownButton).setVisibility(View.GONE);
		}
			
		v.findViewById(R.id.renameDeckButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mContext instanceof CPDecksActivity ){
					((CPDecksActivity)mContext).renameDeck(deck);
				}
			}
		});

		v.findViewById(R.id.removeDeckButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if( mContext instanceof CPDecksActivity ){
					((CPDecksActivity)mContext).removeDeck(deck);
				}
			}
		});

		return v;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return 1;
	}

	@Override
	public long getCombinedChildId(long groupId, long childId) {
		return 0;
	}

	@Override
	public long getCombinedGroupId(long groupId) {
		return 0;
	}

	@Override
	public Object getGroup(int groupPosition) {
		return mDeckList.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return mDeckList.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return 0;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		return getView(groupPosition, convertView, parent);
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return false;
	}

	@Override
	public void onGroupCollapsed(int groupPosition) {

	}

	@Override
	public void onGroupExpanded(int groupPosition) {

	}
}
