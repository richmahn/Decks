package com.chinesepod.decks.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.DialogFragment;

import android.app.ProgressDialog;
import android.os.Bundle;

public class GeneralDialogFragment extends DialogFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	protected ProgressDialog createLoadingDialog(int stringId) {
		ProgressDialog loadingDialog = new ProgressDialog(getActivity());
		if( stringId > 0){
			loadingDialog.setMessage(getActivity().getString(stringId));
		}
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setCancelable(true);
		return loadingDialog;
	}
}
