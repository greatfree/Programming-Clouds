package org.greatfree.util;

import java.io.IOException;

// Created: 09/24/2021, Bing Li
public final class Env
{
	private String registryIP;
	private int registryPort;
	public static String GREATFREE_HOME = UtilConfig.EMPTY_STRING;
	
	private Env()
	{
		this.registryIP = UtilConfig.LOCAL_IP;
		this.registryPort = UtilConfig.DEFAULT_REGISTRY_PORT;
	}

	private static Env instance = new Env();
	
	public static Env CONFIG()
	{
		if (instance == null)
		{
			instance = new Env();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void setup(String relativeHome) throws IOException
	{
		String homePath = System.getProperty(UtilConfig.USER_HOME);
		GREATFREE_HOME = homePath + relativeHome;
		if (!FileManager.isDirExisted(GREATFREE_HOME))
		{
			FileManager.makeDir(GREATFREE_HOME);
			FileManager.createTextFile(UtilConfig.CONFIG_FILE, UtilConfig.REGISTRY_IP_CONFIG);
		}
	}
	
	public String getRegistryIP()
	{
		if (this.registryIP.equals(UtilConfig.LOCAL_IP))
		{
			XPathOnDiskReader reader = new XPathOnDiskReader(UtilConfig.CONFIG_FILE, true);
			this.registryIP = reader.read(UtilConfig.SELECT_REGISTRY_SERVER_IP);
			reader.close();
		}
		return this.registryIP;
	}
	
	public int getRegistryPort()
	{
		if (this.registryPort == UtilConfig.DEFAULT_REGISTRY_PORT)
		{
			XPathOnDiskReader reader = new XPathOnDiskReader(UtilConfig.CONFIG_FILE, true);
			String registryPortStr = reader.read(UtilConfig.SELECT_REGISTRY_SERVER_PORT);
			this.registryPort = Integer.valueOf(registryPortStr);
			reader.close();
		}
		return this.registryPort;
	}
}

