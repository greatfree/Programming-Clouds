package edu.chainnet.s3;

import java.io.IOException;

import org.greatfree.util.FileManager;

import edu.chainnet.s3.storage.child.table.SliceState;

/*
 * Some common methods to process files are retained in the class. 07/18/2020, Bing Li
 */

// Created: 07/18/2020, Bing Li
public class S3File
{
	public static String getFilePath(String filesPath, String sessionKey)
	{
		return filesPath + sessionKey;
	}
	
	public static String getSlicePath(String filePath, String sliceKey, String suffix)
	{
		return filePath + sliceKey + suffix;
	}

	public static void persistSlice(String path, byte[] data) throws IOException
	{
		FileManager.saveFile(path, data);
	}
	
	public static byte[] loadSlice(String path) throws IOException
	{
		if (FileManager.isDirExisted(path))
		{
			return FileManager.loadFile(path);
		}
		return null;
	}
	
	public static void merge(SliceState state, byte[] slice)
	{
		
	}
}
