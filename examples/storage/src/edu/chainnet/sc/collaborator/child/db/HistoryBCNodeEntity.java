package edu.chainnet.sc.collaborator.child.db;

import static com.sleepycat.persist.model.Relationship.MANY_TO_ONE;

import java.util.List;

import com.sleepycat.persist.model.Entity;
import com.sleepycat.persist.model.PrimaryKey;
import com.sleepycat.persist.model.SecondaryKey;

/*
 * Deprecated BCNode is saved in the Berkeley Object-Oriented DB for Java. 10/18/2020, Bing Li 
 */

// Created: 10/18/2020, Bing Li
@Entity
class HistoryBCNodeEntity
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

	private String name;
	private String ip;
	private int port;
	private List<String> partners;
	private String description;

	public HistoryBCNodeEntity()
	{
	}
	
	public HistoryBCNodeEntity(String key, String nodeID, int version, String name, String ip, int port, String serviceName, List<String> partners, String description)
	{
		this.key = key;
		this.nodeID = nodeID;
		this.version = version;
		this.name = name;
		this.ip = ip;
		this.port = port;
		this.serviceName = serviceName;
		this.partners = partners;
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
	
	public int getVersion()
	{
		return this.version;
	}
	
	public void setVersion(int version)
	{
		this.version = version;
	}
	
	public String getNodeID()
	{
		return this.nodeID;
	}
	
	public void setNodeID(String nodeID)
	{
		this.nodeID = nodeID;
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
	
	public List<String> getPartners()
	{
		return this.partners;
	}
	
	public void setPartners(List<String> partners)
	{
		this.partners = partners;
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
