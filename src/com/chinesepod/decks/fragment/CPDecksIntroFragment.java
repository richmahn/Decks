package com.chinesepod.decks.fragment;

import com.chinesepod.decks.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

public final class CPDecksIntroFragment extends Fragment {
    private static final String INTRO_POSITION = "intro_position";
    private int mPosition;
    
    public void setPosition(int position) {
    	mPosition = position;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if ((savedInstanceState != null) && savedInstanceState.containsKey(INTRO_POSITION)) {
            mPosition = savedInstanceState.getInt(INTRO_POSITION);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.intro_item, null);

        ImageView imgView = (ImageView)v.findViewById(R.id.introImage);
        TextView textView = (TextView)v.findViewById(R.id.introText);
        
        String[] introImages = getResources().getStringArray(R.array.IntroImages);
        String[] introText = getResources().getStringArray(R.array.IntroText);

        if( mPosition < introImages.length ){
        	imgView.setImageResource(getResources().getIdentifier(introImages[mPosition], "drawable", getActivity().getPackageName()));
        }
        if( mPosition < introText.length ){
        	textView.setText(introText[mPosition]);
        }

        return v;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(INTRO_POSITION, mPosition);
    }
}
