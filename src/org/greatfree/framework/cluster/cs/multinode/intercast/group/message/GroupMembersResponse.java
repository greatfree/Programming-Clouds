package org.greatfree.framework.cluster.cs.multinode.intercast.group.message;

import java.util.Set;

import org.greatfree.framework.cs.multinode.server.CSAccount;
import org.greatfree.message.multicast.MulticastResponse;

// Created: 04/07/2019, Bing Li
public class GroupMembersResponse extends MulticastResponse
{
	private static final long serialVersionUID = 3858290265417262216L;
	
	private String groupKey;
	private Set<CSAccount> accounts;

	public GroupMembersResponse(String groupKey, Set<CSAccount> accounts, String collaboratorKey)
	{
		super(GroupChatApplicationID.GROUP_MEMBERS_RESPONSE, collaboratorKey);
		this.groupKey = groupKey;
		this.accounts = accounts;
	}

	public String getGroupKey()
	{
		return this.groupKey;
	}
	
	public Set<CSAccount> getAccounts()
	{
		return this.accounts;
	}
}
