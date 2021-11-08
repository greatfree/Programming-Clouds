package org.greatfree.util;

import java.io.IOException;
import java.util.Scanner;

// Created: 09/24/2021, Bing Li
public class Env
{
	private String registryIP;
	private int registryPort;
	protected String freeHome;
	private String configFile;
//	private String relativeHome;

	Scanner in = new Scanner(System.in);

	private Env()
	{
		this.registryIP = UtilConfig.LOCAL_IP;
		this.registryPort = UtilConfig.DEFAULT_REGISTRY_PORT;
		this.freeHome = UtilConfig.EMPTY_STRING;
//		this.relativeHome = relativeHome;
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
	
	public void close()
	{
		this.in.close();
	}
	
	/*
	 * If the $HOME path is the root path of one project, the method is called to set up. 11/04/2021, Bing Li
	 */
	public void setupAtRHome(String relativeHome) throws IOException
	{
		String homePath = System.getProperty(UtilConfig.USER_HOME);
		this.freeHome = homePath + relativeHome;
		this.configFile = this.freeHome + UtilConfig.CONFIG_FILE;
		this.createRegistryXML(UtilConfig.REGISTRY_IP_CONFIG, false);
	}

	/*
	 * If another absolute path is chosen to set up the project, the method is called. 11/04/2021, Bing Li
	 */
	public void setupAtAHome(String absoluteHome) throws IOException
	{
		this.freeHome = absoluteHome;
		this.configFile = this.freeHome + UtilConfig.CONFIG_FILE;
		this.createRegistryXML(UtilConfig.REGISTRY_IP_CONFIG, false);
	}
	
	public void confirmRegistry() throws IOException
	{
		System.out.println("The Registry Server is located at " + this.getRegistryIP() + ":" + this.getRegistryPort() + "? (y or n)");
		String isTrue = this.in.nextLine();
		if (!isTrue.equals("y"))
		{
			System.out.println("Tell me the correct IP of the Registry Server: ");
			String ip = this.in.nextLine();
			this.setRegistryIP(ip);
		}
	}
	
	private void createRegistryXML(String xml, boolean isOverwritten) throws IOException
	{
		if (!FileManager.isDirExisted(this.freeHome))
		{
			FileManager.makeDir(this.freeHome);
			FileManager.createTextFile(this.configFile, xml);
		}
		if (!FileManager.isFileExisted(this.configFile))
		{
			FileManager.createTextFile(this.configFile, xml);
		}
		if (isOverwritten)
		{
			FileManager.createTextFile(this.configFile, xml);
		}
	}
	
	public String getFreeHome()
	{
		return this.freeHome;
	}
	
	public void setRegistryIP(String ip) throws IOException
	{
		String ipXML = UtilConfig.REGISTRY_IP_CONFIG;
		ipXML = ipXML.replace(UtilConfig.LOCAL_IP, ip);
		this.createRegistryXML(ipXML, true);
		this.registryIP = ip;
	}
	
	public String getRegistryIP()
	{
		if (this.registryIP.equals(UtilConfig.LOCAL_IP))
		{
//			XPathOnDiskReader reader = new XPathOnDiskReader(UtilConfig.CONFIG_FILE, true);
			XPathOnDiskReader reader = new XPathOnDiskReader(this.configFile, true);
			this.registryIP = reader.read(UtilConfig.SELECT_REGISTRY_SERVER_IP);
			reader.close();
		}
		return this.registryIP;
	}
	
	public int getRegistryPort()
	{
		if (this.registryPort == UtilConfig.DEFAULT_REGISTRY_PORT)
		{
//			XPathOnDiskReader reader = new XPathOnDiskReader(UtilConfig.CONFIG_FILE, true);
			XPathOnDiskReader reader = new XPathOnDiskReader(this.configFile, true);
			String registryPortStr = reader.read(UtilConfig.SELECT_REGISTRY_SERVER_PORT);
			this.registryPort = Integer.valueOf(registryPortStr);
			reader.close();
		}
		return this.registryPort;
	}
}

