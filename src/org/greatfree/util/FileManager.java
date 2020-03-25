package org.greatfree.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/*
 * The class provides some fundamental file operations based on the API from File of JDK. 11/03/2014, Bing Li
 */

// Created: 11/03/2014, Bing Li
public class FileManager
{
	/*
	 * Detect whether a path or a directory exists in a specific file system, such
	 * as Windows, Linux or Unix. 11/03/2014, Bing Li
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
		String parentDir = getParentDir(directory);
		if (!isDirExisted(parentDir))
		{
			makeDir(parentDir);
		}
		return (new File(directory)).mkdir();
	}

	/*
	 * Get the parent directory of the input one. 11/03/2014, Bing Li
	 */
	private static String getParentDir(String directory)
	{
		int index;
		if (!OSValidator.isWindows())
		{
			index = directory.lastIndexOf(Symbols.FORWARD_SLASH);
		}
		else
		{
			index = directory.lastIndexOf(Symbols.BACK_SLASH);
		}
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

	/*
	 * The method is used to append slashes at the end of a directory. It is
	 * necessary to do so when processing paths for pne particular file systems.
	 * 07/01/2017, Bing Li
	 */
	public static String appendSlash(String directory)
	{
		if (!OSValidator.isWindows())
		{
			if (directory.length() <= 1)
			{
				return directory + Symbols.FORWARD_SLASH;
			}
			else
			{
				String lastAlphabet = directory.substring(directory.length() - 1);
				if (lastAlphabet.equals(Symbols.FORWARD_SLASH))
				{
					return directory;
				}
				else
				{
					return directory + Symbols.FORWARD_SLASH;
				}
			}
		}
		else
		{
			if (directory.length() <= 1)
			{
				return directory + Symbols.BACK_SLASH;
			}
			else
			{
				String lastAlphabet = directory.substring(directory.length() - 1);
				if (lastAlphabet.equals(Symbols.BACK_SLASH))
				{
					return directory;
				}
				else
				{
					return directory + Symbols.BACK_SLASH;
				}
			}
		}
	}

	public static Object readObject(String objectPath) throws IOException, ClassNotFoundException
	{
		FileInputStream fis = null;
		ObjectInputStream in = null;
		try
		{
			fis = new FileInputStream(objectPath);
			in = new ObjectInputStream(fis);
			Object object = in.readObject();
			// in.close();
			return object;
		}
		finally
		{
			// fis = null;
			// in = null;
			fis.close();
			in.close();
		}
	}

	public static boolean writeObject(String objectPath, Object object) throws IOException
	{
		FileOutputStream fos = null;
		ObjectOutputStream out = null;
		try
		{
			fos = new FileOutputStream(objectPath);
			out = new ObjectOutputStream(fos);
			out.writeObject(object);
			// out.close();
			return true;
		}
		finally
		{
			// fos = null;
			// out = null;
			fos.close();
			out.close();
		}
	}

	public static byte[] loadFile(String filePath) throws IOException
	{
		File file = new File(filePath);
		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			return IOUtils.toByteArray(is);
		}
		finally
		{
			is.close();
		}
	}

	public static void saveFile(String filePath, byte[] bytes) throws IOException
	{
		FileUtils.writeByteArrayToFile(new File(filePath), bytes);
	}
	
	public static List<String> readText(String filePath) throws IOException
	{
		return FileUtils.readLines(new File(filePath), UtilConfig.UTF_8);
	}
}
