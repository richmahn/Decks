package com.chinesepod.decks.fragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;

public class CPodModalDialogFragment extends DialogFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	public static boolean isValidEmailAddress(String emailAddress) {
		String expression = "^[\\w\\-+]+(\\.[\\w\\-+]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$";
		CharSequence inputStr = emailAddress;
		Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);
		return matcher.matches();
	}

	protected ProgressDialog createLoadingDialog(int stringId) {
		ProgressDialog loadingDialog = new ProgressDialog(getActivity());
		loadingDialog.setMessage(getActivity().getString(stringId));
		loadingDialog.setIndeterminate(true);
		loadingDialog.setCanceledOnTouchOutside(false);
		loadingDialog.setCancelable(true);
		loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {

			@Override
			public void onCancel(DialogInterface dialog) {
				dismiss();

			}
		});
		return loadingDialog;
	}
}
