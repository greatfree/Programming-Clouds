package com.greatfree.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/*
 * The class provides some fundamental file operations based on the API from File of JDK. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class FileManager
{
	/*
	 * Detect whether a path or a directory exists in a specific file system, such as Windows, Linux or Unix. 11/03/2014, Bing Li
	 */
	public static boolean isDirExisted(String directory)
	{
		File d = new File(directory);
		return d.exists();
	}

	/*
	 * Create a directory in a file system. 11/03/2014, Bing Li
	 */
	public static boolean makeDir(String directory)
	{
		try
		{
			String parentDir = getParentDir(directory);
			if (!isDirExisted(parentDir))
			{
				makeDir(parentDir);
			}
			return (new File(directory)).mkdir();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * Get the parent directory of the input one. 11/03/2014, Bing Li
	 */
	private static String getParentDir(String directory)
	{
		int index = directory.lastIndexOf(Symbols.FORWARD_SLASH);
		if (index > 0)
		{
			return directory.substring(0, index);
		}
		return UtilConfig.NO_DIR;
	}

	/*
	 * Remote a directory. 11/04/2014, Bing Li
	 */
	public static void removeFiles(String path)
	{
		File directory = new File(path);
		if (directory.exists())
		{
			File[] files = directory.listFiles();
			if (files != null)
			{
				for (File file : files)
				{
					file.delete();
				}
			}
		}
	}

	/*
	 * Create a text file that is made up with the text. 11/23/2014, Bing Li
	 */
	public static void createTextFile(String fileName, String text) throws IOException
	{
		Writer writer = null;
		try
		{
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), UtilConfig.UTF_8));
			writer.write(text);
		}
		finally
		{
			writer.close();
		}
	}

	/*
	 * Load a text file into the memory. 11/25/2014, Bing Li
	 */
	public static String loadText(String fileName) throws IOException
	{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		try
		{
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null)
			{
				sb.append(line);
				sb.append("\n");
				line = br.readLine();
			}
			return sb.toString();
		}
		finally
		{
			br.close();
		}
	}
}
