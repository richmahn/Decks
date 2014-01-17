package com.chinesepod.decks.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.Preference;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.chinesepod.decks.R;
import com.chinesepod.decks.CPDecksSettingsActivity;
import com.chinesepod.decks.logic.CPDecksAccount;
import com.koushikdutta.urlimageviewhelper.UrlImageViewCallback;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;

public class AvatarPreference extends Preference implements OnClickListener {
	private ImageView mAvatarImageView;
	private LinearLayout mUploadChooseCancelLayout;
	private Button mChangeAvatarButton;
	private Button mUploadButton;
	private Button mCancelButton;
	private Button mChooseButton;
	private AlertDialog.Builder mDialogBuilder;
	private Context mContext;

	public AvatarPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		setPersistent(false);
		setLayoutResource(R.layout.preference_avatar);
		mDialogBuilder = new AlertDialog.Builder(getContext());

		final String[] items = new String[] { "From Camera", "From SD Card" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.select_dialog_item, items);

		mContext = context;
		
		mDialogBuilder.setTitle("Select Image");
		mDialogBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) {
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					try {
						intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, ((CPDecksSettingsActivity) getContext()).getImageCaptureUri());
						intent.putExtra("return-data", true);

						((Activity) getContext()).startActivityForResult(intent, CPDecksSettingsActivity.PICK_FROM_CAMERA);
					} catch (Exception e) {
						e.printStackTrace();
					}

					dialog.cancel();
				} else {
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					((Activity) getContext()).startActivityForResult(Intent.createChooser(intent, "Complete action using"), CPDecksSettingsActivity.PICK_FROM_FILE);
				}
				setAvatarChangeButtonVisible(false);
			}
		});

	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);

		mChangeAvatarButton = (Button) view.findViewById(R.id.changeAvatarButton);
		mAvatarImageView = (ImageView) view.findViewById(R.id.avatarImageView);
		mUploadChooseCancelLayout = (LinearLayout) view.findViewById(R.id.uploadChooseCancelLayout);
		mUploadButton = (Button) view.findViewById(R.id.uploadAvatarButton);
		mCancelButton = (Button) view.findViewById(R.id.cancelAvatarButton);
		mChooseButton = (Button) view.findViewById(R.id.chooseAvatarButton);

		mChangeAvatarButton.setOnClickListener(this);
		mChooseButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mDialogBuilder.create().show();
				setAvatarChangeButtonVisible(false);
			}
		});
		mCancelButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setAvatar();
				setAvatarChangeButtonVisible(true);

			}
		});
		mUploadButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((CPDecksSettingsActivity) getContext()).uploadAvatar();

			}
		});

		setAvatar();
	}

	private void setAvatar() {
		UrlImageViewHelper.setUseBitmapScaling(true);
		UrlImageViewHelper.setUrlDrawable(mAvatarImageView, CPDecksAccount.getInstance().getAvatarUrl(), R.drawable.default_avatar);
	}

	@Override
	protected View onCreateView(ViewGroup parent) {
		return super.onCreateView(parent);
	}

	public void setAvatarChangeButtonVisible(boolean bool) {
		if (bool == false) {
			mChangeAvatarButton.setVisibility(View.GONE);
			mUploadChooseCancelLayout.setVisibility(View.VISIBLE);
		} else {
			mChangeAvatarButton.setVisibility(View.VISIBLE);
			mUploadChooseCancelLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		mDialogBuilder.create().show();
	}

	public ImageView getAvatarImageView() {
		return mAvatarImageView;
	}
}
