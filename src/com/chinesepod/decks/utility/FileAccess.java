package com.chinesepod.decks.utility;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;



public class FileAccess implements Serializable {
	public static final String TAG="FileAccess";
	private static final long serialVersionUID = 1L;
	private RandomAccessFile oSavedFile;

	public FileAccess() throws IOException {
		this("", 0);
	}

	public FileAccess(String sName, long nPos) throws IOException {
		oSavedFile = new RandomAccessFile(sName, "rw");
		oSavedFile.seek(nPos);
	}
	
	public synchronized void seekTo(long nPos) throws IOException {
		oSavedFile.seek(nPos);
	}
	
	public synchronized void close() {
		try {
			oSavedFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		oSavedFile = null;
	}
	
	public synchronized int write(byte[] b, int nStart, int nLen) {
		int n = -1;
		try {
			oSavedFile.write(b, nStart, nLen);
			n = nLen;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
}