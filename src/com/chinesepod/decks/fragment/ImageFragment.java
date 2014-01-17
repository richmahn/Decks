package com.chinesepod.decks.fragment;

import java.io.File;
import java.util.ArrayList;

import android.R.integer;
import android.accounts.Account;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewAnimator;

import com.chinesepod.decks.CPDecksApplication;
import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksCanPlaySound;
import com.chinesepod.decks.CPDecksFlashcardsActivity;
import com.chinesepod.decks.fragment.ContentCardFragment.onSaveButtonClickListener;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.chinesepod.decks.logic.CPDecksDeck;
import com.chinesepod.decks.logic.CPDecksImage;
import com.chinesepod.decks.logic.CPDecksVocabulary;
import com.chinesepod.decks.utility.CPDecksVocabularyManager;
import com.chinesepod.decks.utility.CPodAccountManager;
import com.chinesepod.decks.utility.net.HttpConnectionHelper;
import com.chinesepod.decks.view.AutoResizeTextView;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.tekle.oss.android.animation.AnimationFactory;
import com.tekle.oss.android.animation.AnimationFactory.FlipDirection;

public class ImageFragment extends Fragment {
	private static final String IMAGE = "image";

	private CPDecksImage mImage;
	private ImageView mImageView;

	public void setImage(CPDecksImage image) {
		mImage = image;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (mImage == null && savedInstanceState != null ) {
			mImage = (CPDecksImage)savedInstanceState.getSerializable(IMAGE);
		}

		if (mImage == null) {
			return null;
		}

		super.onCreateView(inflater, container, savedInstanceState);
		mImageView = (ImageView)inflater.inflate(R.layout.image_view, null);
		
		UrlImageViewHelper.setUrlDrawable(mImageView, mImage.getThumbUrl(), R.drawable.loading_image, new UrlImageViewCallback() {
			@Override
			public void onLoaded(ImageView imageView, Bitmap loadedBitmap, String url,
					boolean loadedFromCache) {
				try {
					Drawable d = new BitmapDrawable(getResources(), loadedBitmap);
					UrlImageViewHelper.setUrlDrawable(mImageView, mImage.getImageUrl(), d);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		return mImageView;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putSerializable(IMAGE, mImage);
		super.onSaveInstanceState(outState);
	}

	@Override
	public String toString() {
		return mImage.getImageUrl();
	}
}
