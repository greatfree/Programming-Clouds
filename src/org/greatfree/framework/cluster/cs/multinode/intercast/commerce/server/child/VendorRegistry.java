package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.app.business.cs.multinode.server.CSAccount;

// Created: 07/21/2019, Bing Li
class VendorRegistry
{
	private Map<String, VendorAccount> accounts;

	private VendorRegistry()
	{
		// Define a concurrent map for the consideration of consistency. 11/09/2014, Bing Li
		this.accounts = new ConcurrentHashMap<String, VendorAccount>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static VendorRegistry instance = new VendorRegistry();
	
	public static VendorRegistry CS()
	{
		if (instance == null)
		{
			instance = new VendorRegistry();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose()
	{
		this.accounts.clear();
		this.accounts = null;
	}

	public boolean isAccountExisted(String vendorKey)
	{
		return this.accounts.containsKey(vendorKey);
	}
	
	public boolean isFollowerExisted(String vendorKey, String customerKey)
	{
		if (this.accounts.containsKey(vendorKey))
		{
			return this.accounts.get(vendorKey).getFollowers().contains(customerKey);
		}
		return false;
	}
	
	public void add(VendorAccount account)
	{
		if (!this.accounts.containsKey(account.getVendorKey()))
		{
			this.accounts.put(account.getVendorKey(), account);
		}
	}
	
	public void followVendor(String vendorKey, String customerKey)
	{
		if (this.accounts.containsKey(vendorKey))
		{
			this.accounts.get(vendorKey).follow(customerKey);
		}
	}
	
	public void followVendor(String vendorKey, Set<CSAccount> accounts)
	{
		if (this.accounts.containsKey(vendorKey))
		{
			for (CSAccount entry : accounts)
			{
				this.accounts.get(vendorKey).follow(entry.getUserKey());
			}
		}
	}
	
	public void unfollowMerchandise(String vendorKey, String customerKey)
	{
		if (this.accounts.containsKey(vendorKey))
		{
			this.accounts.get(vendorKey).unfollow(customerKey);
		}
	}
	
	public Collection<VendorAccount> getAllAccounts()
	{
		return this.accounts.values();
	}
	
	public VendorAccount getAccount(String vendorKey)
	{
		if (this.accounts.containsKey(vendorKey))
		{
			return this.accounts.get(vendorKey);
		}
		return null;
	}
	
	public Set<String> getVendorFollowers(String vendorKey)
	{
		if (this.accounts.containsKey(vendorKey))
		{
			return this.accounts.get(vendorKey).getFollowers();
		}
		return null;
	}
}
