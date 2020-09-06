package org.greatfree.dsf.cluster.cs.multinode.intercast.group.clusterserver.child;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.dsf.cs.multinode.server.CSAccount;

// Created: 04/07/2019, Bing Li
class GroupRegistry
{
	private Map<String, GroupAccount> accounts;

	private GroupRegistry()
	{
		// Define a concurrent map for the consideration of consistency. 11/09/2014, Bing Li
		this.accounts = new ConcurrentHashMap<String, GroupAccount>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static GroupRegistry instance = new GroupRegistry();
	
	public static GroupRegistry CS()
	{
		if (instance == null)
		{
			instance = new GroupRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the accounts. 05/21/2017, Bing Li
	 */
	public void dispose()
	{
		this.accounts.clear();
		this.accounts = null;
	}

	/*
	 * Check whether one particular account is existed. 05/21/2017, Bing Li
	 */
	public boolean isAccountExisted(String groupKey)
	{
		return this.accounts.containsKey(groupKey);
	}
	
	public boolean isMemberExisted(String groupKey, String userKey)
	{
		if (this.accounts.containsKey(groupKey))
		{
			return this.accounts.get(groupKey).getMembers().contains(userKey);
		}
		return false;
	}
	
	/*
	 * Add one account. 05/21/2017, Bing Li
	 */
	public void add(GroupAccount account)
	{
		if (!this.accounts.containsKey(account.getGroupKey()))
		{
			this.accounts.put(account.getGroupKey(), account);
		}
	}
	
	/*
	 * Add one member to the group. Programmed in the aircraft from Zhuhai to Xi'An, immediately after coming back from Canada. 04/19/2019, Bing Li
	 */
	public void addMember(String groupKey, String userKey)
	{
		if (this.accounts.containsKey(groupKey))
		{
			this.accounts.get(groupKey).addMember(userKey);
		}
	}
	
	public void addMembers(String groupKey, Set<CSAccount> accounts)
	{
		if (this.accounts.containsKey(groupKey))
		{
			for (CSAccount entry : accounts)
			{
				this.accounts.get(groupKey).addMember(entry.getUserKey());
			}
		}
	}
	
	public void removeMember(String groupKey, String userKey)
	{
		if (this.accounts.containsKey(groupKey))
		{
			this.accounts.get(groupKey).removeMember(userKey);
		}
	}

	/*
	 * Expose all of registered accounts. 05/21/2017, Bing Li
	 */
	public Collection<GroupAccount> getAllAccounts()
	{
		return this.accounts.values();
	}

	/*
	 * Retrieve one account by its key. 05/21/2017, Bing Li
	 */
	public GroupAccount getAccount(String groupKey)
	{
		if (this.accounts.containsKey(groupKey))
		{
			return this.accounts.get(groupKey);
		}
		return null;
	}
	
	public Set<String> getGroupMembers(String groupKey)
	{
		if (this.accounts.containsKey(groupKey))
		{
			return this.accounts.get(groupKey).getMembers();
		}
		return null;
	}
}
