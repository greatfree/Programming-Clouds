package edu.chainnet.s3;

import java.io.IOException;

import org.greatfree.util.FileManager;

// Created: 07/20/2020, Bing Li
public class Setup
{
	public static String S3_CONFIG_XML = S3Config.NO_CONFIG;
	public static String CLIENT_CONFIG_XML = S3Config.NO_CONFIG;
	public static String META_CONFIG_XML = S3Config.NO_CONFIG;
	public static String STORAGE_CONFIG_XML = S3Config.NO_CONFIG;

	private static String setupHome()
	{
		String homePath = System.getProperty(S3Config.USER_HOME);
		String s3Home = homePath + S3Config.S3_HOME;
		if (!FileManager.isDirExisted(s3Home))
		{
			FileManager.makeDir(s3Home);
		}
		return s3Home;
	}

	public static boolean setupS3()
	{
		String path = setupHome() + S3Config.S3_CONFIG_PATH;
		S3_CONFIG_XML = path + S3Config.S3_CONFIG_FILE;
		if (!FileManager.isDirExisted(path))
		{
			FileManager.makeDir(path);
			if (!FileManager.isDirExisted(S3_CONFIG_XML))
			{
				try
				{
					FileManager.createTextFile(S3_CONFIG_XML, InitXML.S3_CONFIG_XML);
				}
				catch (IOException e)
				{
					return false;
				}
			}
		}
		return true;
	}
	
	public static boolean setupClient()
	{
		if (setupS3())
		{
			String path = setupHome() + S3Config.S3_CLIENT_CONFIG_PATH;
			CLIENT_CONFIG_XML = path + S3Config.CLIENT_CONFIG_FILE;
			if (!FileManager.isDirExisted(path))
			{
				FileManager.makeDir(path);
				if (!FileManager.isDirExisted(CLIENT_CONFIG_XML))
				{
					try
					{
						FileManager.createTextFile(CLIENT_CONFIG_XML, InitXML.CLIENT_CONFIG_XML);
					}
					catch (IOException e)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static boolean setupMeta()
	{
		if (setupS3())
		{
			String path = setupHome() + S3Config.S3_META_CONFIG_PATH;
			META_CONFIG_XML = path + S3Config.META_CONFIG_FILE;
			if (!FileManager.isDirExisted(path))
			{
				FileManager.makeDir(path);
				if (!FileManager.isDirExisted(META_CONFIG_XML))
				{
					try
					{
						FileManager.createTextFile(META_CONFIG_XML, InitXML.META_CONFIG_XML);
					}
					catch (IOException e)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
	
	public static boolean setupStorage()
	{
		if (setupS3())
		{
			String path = setupHome() + S3Config.S3_STORAGE_CONFIG_PATH;
			STORAGE_CONFIG_XML = path + S3Config.STORAGE_CONFIG_FILE;
			if (!FileManager.isDirExisted(path))
			{
				FileManager.makeDir(path);
				if (!FileManager.isDirExisted(STORAGE_CONFIG_XML))
				{
					try
					{
						FileManager.createTextFile(STORAGE_CONFIG_XML, InitXML.STORAGE_CONFIG_XML);
					}
					catch (IOException e)
					{
						return false;
					}
				}
			}
		}
		return true;
	}
}
