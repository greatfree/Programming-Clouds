package edu.chainnet.s3.storage;

import org.greatfree.util.Builder;

/*
 * I decide to implement the synchronization through the interaction between the children and the root rather than using the file locking. The approach is more convenient and reasonable. 09/14/2020, Bing Li
 * 
 * The program keeps the environment of each child of the storage cluster. 09/10/2020, Bing Li
 */

// Created: 09/10/2020, Bing Li
public class Env
{
	private String childID;

	private String storePath;
	private String registryIP;
	private int registryPort;
	
	private String sssPath;
	private String filePath;
//	private String ccoPath;
//	private ChildConfigObject cco;
	
	public Env(EnvBuilder builder)
	{
		this.childID = builder.getChildID();
		this.storePath = builder.getStorePath();
		this.registryIP = builder.getRegistryIP();
		this.registryPort = builder.getRegistryPort();
		this.sssPath = builder.getSSSPath();
		this.filePath = builder.getFilePath();
//		this.ccoPath = builder.getCCOPath();
//		this.cco = builder.getCCO();
	}
	
	public static class EnvBuilder implements Builder<Env>
	{
		private String childID;

		private String storePath;
		private String registryIP;
		private int registryPort;
		
		private String sssPath;
		private String filePath;
//		private String ccoPath;
//		private ChildConfigObject cco;
		
		public EnvBuilder()
		{
		}
		
		public EnvBuilder childID(String childID)
		{
			this.childID = childID;
			return this;
		}
		
		public EnvBuilder storePath(String storePath)
		{
			this.storePath = storePath;
			return this;
		}
		
		public EnvBuilder registryIP(String registryIP)
		{
			this.registryIP = registryIP;
			return this;
		}
		
		public EnvBuilder registryPort(int registryPort)
		{
			this.registryPort = registryPort;
			return this;
		}
		
		public EnvBuilder sssPath(String sssPath)
		{
			this.sssPath = sssPath;
			return this;
		}
		
		public EnvBuilder filePath(String filePath)
		{
			this.filePath = filePath;
			return this;
		}

		/*
		public EnvBuilder ccoPath(String ccoPath)
		{
			this.ccoPath = ccoPath;
			return this;
		}
		
		public EnvBuilder cco(ChildConfigObject cco)
		{
			this.cco = cco;
			return this;
		}
		*/

		@Override
		public Env build()
		{
			return new Env(this);
		}
		
		public String getChildID()
		{
			return this.childID;
		}
		
		public String getStorePath()
		{
			return this.storePath;
		}
		
		public String getRegistryIP()
		{
			return this.registryIP;
		}
		
		public int getRegistryPort()
		{
			return this.registryPort;
		}
		
		public String getSSSPath()
		{
			return this.sssPath;
		}
		
		public String getFilePath()
		{
			return this.filePath;
		}

		/*
		public String getCCOPath()
		{
			return this.ccoPath;
		}
		
		public ChildConfigObject getCCO()
		{
			return this.cco;
		}
		*/
	}
	
	public String getChildID()
	{
		return this.childID;
	}
	
	public void setChildID(String childID)
	{
		this.childID = childID;
	}
	
	public String getStorePath()
	{
		return this.storePath;
	}
	
	public String getRegistryIP()
	{
		return this.registryIP;
	}
	
	public int getRegistryPort()
	{
		return this.registryPort;
	}
	
	public String getSSSPath()
	{
		return this.sssPath;
	}
	
	public void setSSSPath(String sssPath)
	{
		this.sssPath = sssPath;
	}
	
	public String getFilePath()
	{
		return this.filePath;
	}
	
	public void setFilePath(String filePath)
	{
		this.filePath = filePath;
	}

	/*
	public String getCCOPath()
	{
		return this.ccoPath;
	}
	
	public void setCCOPath(String ccoPath)
	{
		this.ccoPath = ccoPath;
	}
	
	public ChildConfigObject getCCO()
	{
		return this.cco;
	}
	
	public void setCCO(ChildConfigObject cco)
	{
		this.cco = cco;
	}
	*/
}
