package org.greatfree.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

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
	 * Create a text file that is made up with the text synchronously among
	 * processes. 11/23/2014, Bing Li
	 */
	public static void createTextFileSync(String fileName, String text, boolean isAppended) throws IOException
	{
		ByteBuffer buffer = ByteBuffer.wrap(text.getBytes());
		Path path = Paths.get(fileName);
		FileChannel fileChannel;
		if (isAppended)
		{
			fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
			fileChannel.position(fileChannel.size() - 1);
		}
		else
		{
			fileChannel = FileChannel.open(path, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW);
			fileChannel.position(0);
		}
		FileLock lock = fileChannel.lock();
		try
		{
			if (lock.isValid())
			{
				fileChannel.write(buffer);
			}
		}
		finally
		{
			lock.release();
			fileChannel.close();
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
	 * Load a text file into the memory synchronously among processes. 11/25/2014,
	 * Bing Li
	 */
	/*
	public static String loadTextSync(String fileName, int bufferSize) throws IOException
	{
		Path path = Paths.get(fileName);
		FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
		FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true);
		if (lock.isValid() && lock.isShared())
		{
			StringBuffer sb = new StringBuffer();
			ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
			int noOfBytesRead = fileChannel.read(buffer);
			while (noOfBytesRead != -1)
			{
				buffer.flip();
				while (buffer.hasRemaining())
				{
					sb.append((char) buffer.get());
				}
				buffer.clear();
				noOfBytesRead = fileChannel.read(buffer);
			}
			return sb.toString();
		}
		fileChannel.close();
		return UtilConfig.EMPTY_STRING;
	}
	*/

	/*
	 * Load a text file into the memory synchronously among processes. 11/25/2014,
	 * Bing Li
	 */
	public static String loadTextSync(String fileName) throws IOException
	{
		Path path = Paths.get(fileName);
		FileChannel fileChannel = FileChannel.open(path, StandardOpenOption.READ);
		FileLock lock = fileChannel.lock(0, Long.MAX_VALUE, true);
		try
		{
			if (lock.isValid() && lock.isShared())
			{
				StringBuffer sb = new StringBuffer();
				ByteBuffer buffer = ByteBuffer.allocate((int)fileChannel.size());
				int noOfBytesRead = fileChannel.read(buffer);
				if (noOfBytesRead != -1)
				{
					buffer.flip();
					while (buffer.hasRemaining())
					{
						sb.append((char) buffer.get());
					}
					buffer.clear();
				}
				return sb.toString();
			}
		}
		finally
		{
			lock.release();
			fileChannel.close();
		}
		return UtilConfig.EMPTY_STRING;
	}

	/*
	 * The method is used to append slashes at the end of a directory. It is
	 * necessary to do so when processing paths for one particular file systems.
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

	public static Object readObject(String objectPath) throws IOException
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
		catch (ClassNotFoundException e)
		{
			return null;
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

	public static boolean writeObjectSync(String objectPath, Object object) throws IOException
	{
		FileOutputStream fos = null;
		ByteArrayOutputStream baos = null;
		ObjectOutputStream oos = null;
		FileLock lock = null;
		try
		{
			baos = new ByteArrayOutputStream();
			oos = new ObjectOutputStream(baos);
			oos.writeObject(object);
			oos.flush();
			byte[] bytes = baos.toByteArray();
			ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
			buffer.clear();
			buffer.put(bytes);
			buffer.flip();

			fos = new FileOutputStream(objectPath);
			FileChannel fc = fos.getChannel();
			fc.position(0);
			lock = fc.lock();
			if (!lock.isShared() && lock.isValid())
			{
				fc.write(buffer);
			}
			return true;
		}
		finally
		{
			if (lock != null)
			{
				lock.release();
			}
			fos.close();
			oos.close();
			baos.close();
		}
	}
	
	public static Object readObjectSync(String objectPath) throws IOException
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		ByteArrayInputStream bais = null;
		FileLock lock = null;
		try
		{
			fis = new FileInputStream(objectPath);
			FileChannel fc = fis.getChannel();
//			System.out.println("FileManager-readObjectSync(): lock is acquiring");
			lock = fc.lock(0, Long.MAX_VALUE, true);
//			System.out.println("FileManager-readObjectSync(): lock is acquired!");
			if (lock.isShared() && lock.isValid())
			{
				ByteBuffer buffer = ByteBuffer.allocate((int)fc.size());
				int noOfBytesRead = fc.read(buffer);
				if (noOfBytesRead != -1)
				{
					bais = new ByteArrayInputStream(buffer.array());
					ois = new ObjectInputStream(bais);
					buffer.clear();
					return ois.readObject();
				}
			}
			else
			{
//				System.out.println("FileManager-readObjectSync(): lock is NOT valid!");
			}
		}
		catch (IOException | ClassNotFoundException e)
		{
			return null;
		}
		finally
		{
			if (lock != null)
			{
				lock.release();
			}
			if (fis != null)
			{
				try
				{
					fis.close();
					ois.close();
					bais.close();
				}
				catch (IOException e)
				{
					return null;
				}
			}
		}
		return null;
	}

	public static long getFileSize(String filePath)
	{
		return (new File(filePath)).length();
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

	public static byte[] loadFile(String filePath, int startIndex, int endIndex) throws IOException
	{
		RandomAccessFile f = new RandomAccessFile(filePath, "r");
		f.seek(startIndex);
		byte[] bytes = new byte[endIndex - startIndex + 1];
		f.read(bytes);
		f.close();
		return bytes;
	}

	public static byte[] loadFile(String filePath, int size) throws IOException
	{
		File file = new File(filePath);
		InputStream is = null;
		try
		{
			is = new FileInputStream(file);
			return IOUtils.toByteArray(is, size);
		}
		finally
		{
			is.close();
		}
	}

	public static Collection<File> loadAllFiles(String path)
	{
		return FileUtils.listFiles(FileUtils.getFile(path), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
	}

	public static void moveFile(File sourceFile, String destinationFile) throws IOException
	{
		FileUtils.moveFile(sourceFile, FileUtils.getFile(destinationFile));
	}

	public static void moveFile(String sourceFile, String destinationFile) throws IOException
	{
		FileUtils.moveFile(FileUtils.getFile(sourceFile), FileUtils.getFile(destinationFile));
	}

	public static void saveFile(String filePath, byte[] bytes) throws IOException
	{
		FileUtils.writeByteArrayToFile(new File(filePath), bytes);
	}
	
	public static void removeDir(String directory) throws IOException
	{
		FileUtils.deleteDirectory(new File(directory));
	}

	public static boolean removeFile(String file)
	{
		return (new File(file)).delete();
	}

	public static void saveFile(String filePath, byte[] bytes, boolean isAppend) throws IOException
	{
		FileUtils.writeByteArrayToFile(new File(filePath), bytes, isAppend);
	}

	public static List<String> readText(String filePath) throws IOException
	{
		return FileUtils.readLines(new File(filePath), UtilConfig.UTF_8);
	}
}
