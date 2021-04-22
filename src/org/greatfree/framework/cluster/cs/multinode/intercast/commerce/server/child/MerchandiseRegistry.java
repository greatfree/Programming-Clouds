package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.app.business.dip.cs.multinode.server.CSAccount;
import org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message.Merchandise;

// Created: 07/18/2019, Bing Li
class MerchandiseRegistry
{
	private Map<String, MerchandiseAccount> accounts;
	private Map<String, Merchandise> merchandises;

	private MerchandiseRegistry()
	{
		// Define a concurrent map for the consideration of consistency. 11/09/2014, Bing Li
		this.accounts = new ConcurrentHashMap<String, MerchandiseAccount>();
		this.merchandises = new ConcurrentHashMap<String, Merchandise>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static MerchandiseRegistry instance = new MerchandiseRegistry();
	
	public static MerchandiseRegistry CS()
	{
		if (instance == null)
		{
			instance = new MerchandiseRegistry();
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
		this.merchandises.clear();
		this.merchandises = null;
	}

	public boolean isAccountExisted(String merchandiseKey)
	{
		return this.accounts.containsKey(merchandiseKey);
	}
	
	public boolean isFollowerExisted(String merchandiseKey, String customerKey)
	{
		if (this.accounts.containsKey(merchandiseKey))
		{
			return this.accounts.get(merchandiseKey).getFollowers().contains(customerKey);
		}
		return false;
	}
	
	public void add(MerchandiseAccount account)
	{
		if (!this.accounts.containsKey(account.getMerchandiseKey()))
		{
			this.accounts.put(account.getMerchandiseKey(), account);
		}
	}
	
	public void followMerchandise(String merchandiseKey, String customerKey)
	{
		if (this.accounts.containsKey(merchandiseKey))
		{
			this.accounts.get(merchandiseKey).follow(customerKey);
		}
	}
	
	public void followMerchandise(String merchandiseKey, Set<CSAccount> accounts)
	{
		if (this.accounts.containsKey(merchandiseKey))
		{
			for (CSAccount entry : accounts)
			{
				this.accounts.get(merchandiseKey).follow(entry.getUserKey());
			}
		}
	}
	
	public void unfollowMerchandise(String merchandiseKey, String customerKey)
	{
		if (this.accounts.containsKey(merchandiseKey))
		{
			this.accounts.get(merchandiseKey).unfollow(customerKey);
		}
	}
	
	public Collection<MerchandiseAccount> getAllAccounts()
	{
		return this.accounts.values();
	}
	
	public MerchandiseAccount getAccount(String merchandiseKey)
	{
		if (this.accounts.containsKey(merchandiseKey))
		{
			return this.accounts.get(merchandiseKey);
		}
		return null;
	}
	
	public Set<String> getMerchandiseFollowers(String merchandiseKey)
	{
		if (this.accounts.containsKey(merchandiseKey))
		{
			return this.accounts.get(merchandiseKey).getFollowers();
		}
		return null;
	}
	
	public Merchandise getMerchandise(String merchandiseKey)
	{
		if (this.merchandises.containsKey(merchandiseKey))
		{
			return this.merchandises.get(merchandiseKey);
		}
		return null;
	}
}
