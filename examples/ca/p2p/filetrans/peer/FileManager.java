package ca.p2p.filetrans.peer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

// Created: 03/07/2020, Bing Li
class FileManager
{
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
}
