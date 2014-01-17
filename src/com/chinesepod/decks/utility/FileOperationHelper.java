/*
 * Copyright (C) 2008 The ChinesePod Dictionary Project
 *
 * Praxis Language Ltd.	http://praxislanguage.com
 * Website: ChinesePod	http://chinesepod.com
 * Author: liwenqin
 * Email: yiizzhoou@gmail.com
 * 
 */

package com.chinesepod.decks.utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;

import com.chinesepod.decks.CPDecksApplication;

import android.os.Environment;
import android.util.Log;

public class FileOperationHelper {
	private static final String TAG = "FileOperationHelper";
	public static final String AUDIO_FILES_DIR = CPDecksApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath();
	public static final String IMAGE_FILES_DIR = CPDecksApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
	public static final String USER_AVATAR_DIR = CPDecksApplication.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

	private String line;
	private File file;

	public static boolean checkFile(String filePath) {
		boolean flag = false;
		try {
			File file = new File(filePath);
			if (file.exists() && file.isFile() && file.canRead()) {
				flag = true;
			}
		} catch (Exception e) {
			Log.e(TAG, "ERROR :" + e.getStackTrace());
		}

		return flag;
	}

	// Checks if directories exist, otherwise tries to create them
	// TIP: Don't forget about adding
	// <uses-permission
	// android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
	// to manifest file.
	static public boolean checkSaveDir(String dirPath) {
		boolean flag = false;
		try {
			File dir = new File(dirPath);
			if (!dir.exists() || !dir.isDirectory()) {
				if (dir.mkdirs()) {
					flag = true;
				}
			}

			if (dir.canRead() && dir.canWrite()) {
				flag = true;
			}
		} catch (Exception e) {
			Log.e(TAG, "ERROR :" + e.getStackTrace());
		}

		return flag;
	}

	static public String getFileExtension(String filename) {
		String fileExtension = null;
		if (filename != null && !"".equals(filename)) {
			fileExtension = filename.substring(filename.lastIndexOf("."));
		}
		return fileExtension;
	}

	public boolean isInfoFileExist(String filename) {
		File infoFile = new File(filename + ".info");
		return infoFile.exists();
	}

	public boolean createInfoFile(String filename) {
		boolean flag = true;
		File infoFile = new File(filename + ".info");
		if (!infoFile.exists()) {
			try {
				flag = infoFile.createNewFile();
			} catch (IOException e) {
				flag = false;
				e.printStackTrace();
			}
		}
		return flag;
	}

	public boolean deleteInfoFile(String filename) {
		File infoFile = new File(filename + ".info");
		return infoFile.delete();
	}

	public void createText(String path) {
		try {
			file = new File(path);
			if (!file.exists()) {
				file.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void deleteText(String path) {
		try {
			RandomAccessFile file = new RandomAccessFile(path, "rw");
			file.setLength(0);
			file.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public String readText(String path) {
		FileReader fileread;
		file = new File(path);
		try {
			fileread = new FileReader(file);
			BufferedReader bfr = new BufferedReader(fileread);
			try {
				line = bfr.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return line;
	}

	public void writeText(String body, String path) {
		RandomAccessFile mm = null;
		file = new File(path);
		try {
			mm = new RandomAccessFile(file, "rw");
			mm.writeBytes(body);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			if (mm != null) {
				try {
					mm.close();
				} catch (IOException e2) {
					e2.printStackTrace();
				}
			}
		}
	}

}
