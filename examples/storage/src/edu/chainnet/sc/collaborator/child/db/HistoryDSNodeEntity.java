package edu.chainnet.sc.collaborator.child.db;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

/*
 * Deprecated DSNode is saved in the Berkeley Object-Oriented DB for Java. 10/19/2020, Bing Li 
 */

// Created: 10/19/2020, Bing Li
@Entity
class HistoryDSNodeEntity
{
	// The key is generated through DBConfig.getDPKey(nodeKey, version). 10/18/2020, Bing Li
	@PrimaryKey
	private String key;

	@SecondaryKey(relate=MANY_TO_ONE)
	private String nodeID;

	@SecondaryKey(relate=MANY_TO_ONE)
	private Integer version;

	@SecondaryKey(relate=MANY_TO_ONE)
	private String serviceName;

	// There are three options, including SC, ON and DL. 10/18/2020, Bing Li
	@SecondaryKey(relate=MANY_TO_ONE)
	private Integer nodeType;

	private String name;
	private String ip;
	private int port;
	private String description;

	public HistoryDSNodeEntity()
	{
	}
	
	
	public HistoryDSNodeEntity(String key, String nodeID, int version, int nodeType, String name, String ip, int port, String serviceName, String description)
	{
		this.key = key;
		this.nodeID = nodeID;
		this.version = version;
		this.nodeType = nodeType;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.serviceName = serviceName;
		this.description = description;
	}

	public String getKey()
	{
		return this.key;
	}
	
	public void setKey(String key)
	{
		this.key = key;
	}
	
	public String getNodeID()
	{
		return this.nodeID;
	}
	
	public void setNodeID(String nodeID)
	{
		this.nodeID = nodeID;
	}
	
	public int getVersion()
	{
		return this.version;
	}
	
	public void setVersion(int version)
	{
		this.version = version;
	}

	public int getNodeType()
	{
		return this.nodeType;
	}
	
	public void setNodeType(int nodeType)
	{
		this.nodeType = nodeType;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getIP()
	{
		return this.ip;
	}
	
	public void setIP(String ip)
	{
		this.ip = ip;
	}
	
	public int getPort()
	{
		return this.port;
	}
	
	public void setPort(int port)
	{
		this.port = port;
	}
	
	public String getServiceName()
	{
		return this.serviceName;
	}
	
	public void setServiceName(String serviceName)
	{
		this.serviceName = serviceName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}

}
