package org.greatfree.dip.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.container.IntercastRequest;

// Created: 04/02/2019, Bing Li
// public class GroupRegistryRequest extends Request
public class GroupRegistryRequest extends IntercastRequest
{
	private static final long serialVersionUID = 6867823068230454137L;

	private String creatorKey;
	private String creatorName;
	private String creatorDescription;
	
	private String groupKey;
	private String groupName;
	private String description;
	
	private String senderName;

	public GroupRegistryRequest(String creatorKey, String groupKey, String groupName, String description, String creatorName, String creatorDescription)
	{
		super(creatorKey, groupKey, GroupChatApplicationID.GROUP_REGISTRY_REQUEST);
		this.creatorKey = creatorKey;
		this.creatorName = creatorName;
		this.creatorDescription = creatorDescription;
		this.groupKey = groupKey;
		this.groupName = groupName;
		this.description = description;
		
		this.senderName = creatorName;
	}
	
	public String getCreatorKey()
	{
		return this.creatorKey;
	}
	
	public String getCreatorName()
	{
		return this.creatorName;
	}
	
	public String getCreatorDescription()
	{
		return this.creatorDescription;
	}

	public String getGroupKey()
	{
		return this.groupKey;
	}
	
	public String getGroupName()
	{
		return this.groupName;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public String getSenderName()
	{
		return this.senderName;
	}
}
