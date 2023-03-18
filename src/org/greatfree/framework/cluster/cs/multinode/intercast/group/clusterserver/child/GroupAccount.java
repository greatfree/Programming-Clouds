package org.greatfree.framework.cluster.cs.multinode.intercast.group.clusterserver.child;

import java.util.HashSet;
import java.util.Set;

// Created: 04/07/2019, Bing Li
class GroupAccount
{
	private String groupKey;
	private String groupName;
	private String description;
	
	private Set<String> members;
	
	public GroupAccount(String groupKey, String groupName, String description)
	{
		this.groupKey = groupKey;
		this.groupName = groupName;
		this.description = description;
//		this.members = Sets.newHashSet();
		this.members = new HashSet<String>();
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
	
	public void addMember(String userKey)
	{
		this.members.add(userKey);
	}
	
	public void removeMember(String userKey)
	{
		this.members.remove(userKey);
	}
	
	public Set<String> getMembers()
	{
		return this.members;
	}
}
