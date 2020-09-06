package org.greatfree.dsf.cluster.cs.multinode.intercast.group.message;

import org.greatfree.message.multicast.MulticastResponse;

// Created: 04/07/2019, Bing Li
public class GroupSearchResponse extends MulticastResponse
{
	private static final long serialVersionUID = 2951597686446516017L;
	
	private String groupKey;
	private String groupName;
	private String description;

	public GroupSearchResponse(String groupKey, String groupName, String description, String collaboratorKey)
	{
		super(GroupChatApplicationID.GROUP_SEARCH_RESPONSE, collaboratorKey);
		this.groupKey = groupKey;
		this.groupName = groupName;
		this.description = description;
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
}
