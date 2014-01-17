package com.chinesepod.decks.logic;

abstract public class CPodExpandableObject implements CPDecksObject {

	private boolean mIsExpanded;

	public Boolean isExpanded() {
		return mIsExpanded;
	}

	public void setExpanded(boolean b) {
		this.mIsExpanded = b;
	}

}
