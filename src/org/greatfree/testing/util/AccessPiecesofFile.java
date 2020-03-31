package org.greatfree.testing.util;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 03/28/2020, Bing Li
class AccessPiecesofFile
{
	public final static int PIECE_SIZE = 20000;
	public final static String FILE_PATH = "/Users/libing/Temp/r.mp4";
	public final static String FILE_DES_PATH = "/Users/libing/GreatFreeLabs/Temp/r_bak.mp4";

	public static void main(String[] args) throws IOException
	{
		long size = FileManager.getFileSize(FILE_PATH);
		int startIndex = 0;
		int endIndex = startIndex + PIECE_SIZE - 1;
		byte[] bytes;
		int index = 0;
		while (endIndex < size)
		{
			bytes = FileManager.loadFile(FILE_PATH, startIndex, endIndex);
			FileManager.saveFile(FILE_DES_PATH, bytes, true);
			System.out.println(index++ + ") " + bytes.length + " bytes are saved!");
			startIndex = endIndex + 1;
			endIndex = startIndex + PIECE_SIZE - 1;
		}
		
		bytes = FileManager.loadFile(FILE_PATH, startIndex, (int)(size - 1));
		FileManager.saveFile(FILE_DES_PATH, bytes, true);
		System.out.println(bytes.length + " bytes are saved!");

	}

}
