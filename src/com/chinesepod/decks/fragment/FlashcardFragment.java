package com.chinesepod.decks.fragment;

import java.io.File;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ViewAnimator;

import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksCanPlaySound;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;
import com.chinesepod.decks.view.AutoResizeTextView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

public class FlashcardFragment extends Fragment {

	private static final String WORD = "word";

	CPDecksVocabulary mWord = null;
	private boolean isShowingBack = false;

	private AutoResizeTextView[] mRowsFront = new AutoResizeTextView[3];
	private AutoResizeTextView[] mRowsBack = new AutoResizeTextView[3];
	
	private ViewAnimator mViewAnimator;
	private ImageView mImageFront;
	private Button mPlayButtonFront;
	private Button mPlayButtonBack;

	public void setWord(CPDecksVocabulary mWord) {
		this.mWord = mWord;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mWord == null && savedInstanceState != null ) {
			mWord = (CPDecksVocabulary) savedInstanceState.getSerializable(WORD);
		}

		if (mWord == null) {
			getActivity().finish();
			return null;
		}

		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.flashcard_flipper, null);
		
        mViewAnimator = (ViewAnimator)v.findViewById(R.id.viewFlipper);

        mViewAnimator.setOnClickListener(new OnClickListener() { 
			@Override
			public void onClick(View v) { 
				flipIt();
			}
        });
        
        v.findViewById(R.id.FlaschardScrollView).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				flipIt();
			}
		});
		
        v.findViewById(R.id.FlaschardScrollView).setOnTouchListener(new OnTouchListener() {
            private float startX;
            private float startY;
 
            private boolean isAClick(float startX, float endX, float startY, float endY) {
                float differenceX = Math.abs(startX - endX);
                float differenceY = Math.abs(startY - endY);
                if (differenceX > 5 || differenceY > 5) {
                  return false;
                }
               return true;
            } 
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
            	switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    break;
                case MotionEvent.ACTION_UP: {
                    float endX = event.getX();
                    float endY = event.getY();
                    if (isAClick(startX, endX, startY, endY)) { 
                        flipIt();// WE HAVE A CLICK!!
                        return true;
                    }
                    break;
                }
                }
                return false;
            }
		});

        mRowsFront[0] = (AutoResizeTextView) v.findViewById(R.id.firstRowFront);
		mRowsFront[1] = (AutoResizeTextView) v.findViewById(R.id.secondRowFront);
		mRowsFront[2] = (AutoResizeTextView) v.findViewById(R.id.thirdRowFront);
		
		mRowsBack[0] = (AutoResizeTextView) v.findViewById(R.id.firstRowBack);
		mRowsBack[1] = (AutoResizeTextView) v.findViewById(R.id.secondRowBack);
		mRowsBack[2] = (AutoResizeTextView) v.findViewById(R.id.thirdRowBack);

		mPlayButtonFront = (Button) v.findViewById(R.id.wordPlayButtonFront);
		mPlayButtonBack = (Button) v.findViewById(R.id.wordPlayButtonBack);

		mImageFront = (ImageView)v.findViewById(R.id.wordImageFront);

		return v;
	}
	
	public void updateFragment(){
		String source = mWord.getSource();
		String target = mWord.getTarget();
		String targetPhonetics = mWord.getTargetPhonetics();

		CPDecksAccount account = CPDecksAccount.getInstance();

		int currentFrontRow = 0;
		if( account.getFlashCardsShowTarget() ){
			mRowsFront[currentFrontRow].setVisibility(View.VISIBLE);
			mRowsFront[currentFrontRow].setText(target);
			++currentFrontRow;
		}
		if( account.getFlashCardsShowTargetPhonetics() ){
			mRowsFront[currentFrontRow].setVisibility(View.VISIBLE);
			mRowsFront[currentFrontRow].setText(targetPhonetics);
			++currentFrontRow;
		}
		if( account.getFlashCardsShowSource() ){
			mRowsFront[currentFrontRow].setVisibility(View.VISIBLE);
			mRowsFront[currentFrontRow].setText(source);
			++currentFrontRow;
		}

		mImageFront.setImageDrawable(null);
		if( account.getFlashCardsShowImage() ){
			File imgFile = new  File(mWord.getImageFile());
			if(imgFile.exists()){
			    Bitmap imageBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
			    mImageFront.setImageBitmap(imageBitmap);
			}
			else if (HttpConnectionHelper.isProperUrl(mWord.getImageUrl())) {
				UrlImageViewHelper.setUrlDrawable(mImageFront, mWord.getImageUrl(), R.drawable.default_image);
			}
			else if( currentFrontRow == 0 ){
				mRowsFront[currentFrontRow].setVisibility(View.VISIBLE);
				mRowsFront[currentFrontRow].setText(target);
				++currentFrontRow;
				mRowsFront[currentFrontRow].setVisibility(View.VISIBLE);
				mRowsFront[currentFrontRow].setText(targetPhonetics);
				++currentFrontRow;
			}
		}
		
		if (HttpConnectionHelper.isProperUrl(mWord.getTargetAudio().getAudioUrl())) {
			if( account.getFlashCardsShowAudio() ){
				mPlayButtonFront.setVisibility(View.VISIBLE);
			}
			mPlayButtonBack.setVisibility(View.VISIBLE);
			
			if( new File(mWord.getTargetAudio().getAudioFile()).exists() ){
				mPlayButtonFront.setBackgroundResource(R.drawable.sound_downloaded);
				mPlayButtonBack.setBackgroundResource(R.drawable.sound_downloaded);
			}
			
			mPlayButtonFront.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((CPDecksCanPlaySound) getActivity()).playSound(mWord.getTargetAudio(), v, R.drawable.sound_red, R.drawable.sound_downloaded);
					mPlayButtonBack.setBackgroundResource(R.drawable.sound_downloaded);
				}
			});
			
			mPlayButtonBack.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					((CPDecksCanPlaySound) getActivity()).playSound(mWord.getTargetAudio(), v, R.drawable.sound_red, R.drawable.sound_downloaded);
				}
			});
		} else {
			mPlayButtonFront.setVisibility(View.INVISIBLE);
			mPlayButtonBack.setVisibility(View.INVISIBLE);
		}

		mRowsBack[0].setText(target);
		mRowsBack[1].setText(targetPhonetics);
		mRowsBack[2].setText(source);
	}

	@Override
	public void onStart() {
		super.onStart();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		updateFragment();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(WORD, mWord);
		super.onSaveInstanceState(outState);
	}

	@Override
	public String toString() {
		return mWord.toString() + "| flipped: " + isShowingBack;
	}

	public void showFront() {
		if( isShowingBack ){
			AnimationFactory.flipTransition(mViewAnimator, FlipDirection.LEFT_RIGHT);
		}
		isShowingBack = false;
	}

	public void flipIt() {
		if (!isShowingBack ) {
			showBack();
		} else {
			showFront();
		}
	}

	public boolean isShowingBack() {
		return isShowingBack;
	}

	public void showBack() {
		AnimationFactory.flipTransition(mViewAnimator, FlipDirection.LEFT_RIGHT);
		isShowingBack = true;
	}
}
