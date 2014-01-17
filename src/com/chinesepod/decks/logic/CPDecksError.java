package com.chinesepod.decks.logic;

public class CPDecksError {
	String mErrorCode;
	String mErrorExpalanation;
	boolean isAuthenticationError = false;
	boolean isNetworkError = false;

	public CPDecksError(){
		super();
	}
	
	public CPDecksError(String errorCode){
		super();
		setErrorCode(errorCode);
	}
	
	/**
	 * @return the mErrorCode
	 */
	public String getErrorCode() {
		return mErrorCode;
	}

	/**
	 * @param mErrorCode
	 *            the mErrorCode to set
	 */
	public void setErrorCode(String mErrorCode) {
		this.mErrorCode = mErrorCode;
	}

	/**
	 * @return the mErrorExpalanation
	 */
	public String getErrorExpalanation() {
		if (mErrorExpalanation == null) {
			mErrorExpalanation = "Unknown Error";
		}
		return mErrorExpalanation;
	}

	/**
	 * @param mErrorExpalanation
	 *            the mErrorExpalanation to set
	 */
	public void setErrorExpalanation(String mErrorExpalanation) {
		this.mErrorExpalanation = mErrorExpalanation;
	}

	/**
	 * @return the isAuthenticationError
	 */
	public Boolean isAuthenticationError() {
		return isAuthenticationError;
	}

	/**
	 * @param isAuthenticationError
	 *            the isAuthenticationError to set
	 */
	public void setAuthenticationError(Boolean isAuthenticationError) {
		this.isAuthenticationError = isAuthenticationError;
	}

	public void setNetworkError(Boolean isNetworkError) {
		this.isNetworkError = isNetworkError;
	}

	public boolean isNetworkError() {
		return isNetworkError;
	}

}
