package com.chinesepod.decks.logic;

import java.util.ArrayList;

public class CPDecksResponse {
	CPDecksError mError;
	Object mObject;
	private int mActualFetchSize;

	public CPDecksError getError() {
		return mError;
	}

	public void setError(CPDecksError mError) {
		this.mError = mError;
	}

	public Object getList() {
		return mObject;
	}

	public void setList(Object obj) {
		this.mObject = obj;
	}

	public boolean isSuccessfull() {
		return (mError == null);
	}

	public void setObject(Object obj) {
		this.mObject = obj;

	}

	public Object getObject() {
		return mObject;
	}

	public void setActualFetchSize(int fetchSize) {
		mActualFetchSize = fetchSize; 
	}

	public int getActualFetchSize() {
		if( mActualFetchSize == 0 ){
			if( getList() != null ){
				return ((ArrayList)getList()).size();
			}
		}
		return mActualFetchSize;
	}
}
