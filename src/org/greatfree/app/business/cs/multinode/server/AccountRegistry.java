package org.greatfree.app.business.cs.multinode.server;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This is an account registry. All of the accounts of the chatting system are retained in it. 04/16/2017, Bing Li
 */

// Created: 04/16/2017, Bing Li
public class AccountRegistry
{
	private Map<String, CSAccount> accounts;
	
	private AccountRegistry()
	{
		// Define a concurrent map for the consideration of consistency. 11/09/2014, Bing Li
		this.accounts = new ConcurrentHashMap<String, CSAccount>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static AccountRegistry instance = new AccountRegistry();
	
	public static AccountRegistry CS()
	{
		if (instance == null)
		{
			instance = new AccountRegistry();
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
	public boolean isAccountExisted(String userKey)
	{
		return this.accounts.containsKey(userKey);
	}
	
	/*
	 * Add one account. 05/21/2017, Bing Li
	 */
	public void add(CSAccount account)
	{
		if (!this.accounts.containsKey(account.getUserKey()))
		{
			this.accounts.put(account.getUserKey(), account);
		}
	}

	/*
	 * Expose all of registered accounts. 05/21/2017, Bing Li
	 */
	public Collection<CSAccount> getAllAccounts()
	{
		return this.accounts.values();
	}

	/*
	 * Retrieve one account by its key. 05/21/2017, Bing Li
	 */
	public CSAccount getAccount(String key)
	{
		if (this.accounts.containsKey(key))
		{
			return this.accounts.get(key);
		}
		return null;
	}
}
