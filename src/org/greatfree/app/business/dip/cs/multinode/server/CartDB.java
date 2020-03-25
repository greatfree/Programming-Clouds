package org.greatfree.app.business.dip.cs.multinode.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 12/06/2017, Bing Li
class CartDB
{
	// The keys represent the customer, the vendors and the merchandises, respectively. 12/11/2017, Bing Li
	private Map<String, Map<String, Map<String, Integer>>> mcs;

	private CartDB()
	{
		// Define a concurrent map for the consideration of consistency to save merchandises. 12/04/2017, Bing Li
		this.mcs = new ConcurrentHashMap<String, Map<String, Map<String, Integer>>>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static CartDB instance = new CartDB();
	
	public static CartDB CS()
	{
		if (instance == null)
		{
			instance = new CartDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	/*
	 * Dispose the DB. 12/05/2017, Bing Li
	 */
	public void dispose()
	{
		this.mcs.clear();
		this.mcs = null;
	}

	/*
	 * Put the merchandise in the DB of the cart. 12/05/2017, Bing Li
	 */
	public void putIntoCart(String customerKey, String vendorKey, String mcKey, int count)
	{
		if (!this.mcs.containsKey(customerKey))
		{
			this.mcs.put(customerKey, new HashMap<String, Map<String, Integer>>());
			this.mcs.get(customerKey).put(vendorKey, new HashMap<String, Integer>());
			this.mcs.get(customerKey).get(vendorKey).put(mcKey, count);
		}
		else if (!this.mcs.get(customerKey).containsKey(vendorKey))
		{
			this.mcs.get(customerKey).put(vendorKey, new HashMap<String, Integer>());
			this.mcs.get(customerKey).get(vendorKey).put(mcKey, count);
		}
		else
		{
			int existingCount = this.mcs.get(customerKey).get(vendorKey).get(mcKey);
			this.mcs.get(customerKey).get(vendorKey).put(mcKey, existingCount + count);
		}
	}
	
	/*
	 * Remove the merchandise in the DB of the cart. 12/05/2017, Bing Li
	 */
	public void removeFromCart(String customerKey, String vendorKey, String mcKey, int count)
	{
		int currentCount;
		if (this.mcs.containsKey(customerKey))
		{
			if (this.mcs.get(customerKey).containsKey(vendorKey))
			{
				if (this.mcs.get(customerKey).get(vendorKey).containsKey(mcKey))
				{
					currentCount = this.mcs.get(customerKey).get(vendorKey).get(mcKey);
					if (currentCount > count)
					{
						currentCount -= count;
						this.mcs.get(customerKey).get(vendorKey).put(mcKey, currentCount);
					}
					else
					{
						currentCount = 0;
						this.mcs.get(customerKey).get(vendorKey).remove(mcKey);
						if (this.mcs.get(customerKey).get(vendorKey).size() <= 0)
						{
							this.mcs.get(customerKey).remove(vendorKey);
						}
					}
				}
			}
		}
	}

	/*
	 * Get the merchandises from the cart. 12/06/2017, Bing Li
	 */
	public Map<String, Integer> getFromCart(String customerKey, String vendorKey)
	{
		if (this.mcs.containsKey(customerKey))
		{
			if (this.mcs.get(customerKey).containsKey(vendorKey))
			{
				return this.mcs.get(customerKey).get(vendorKey);
			}
		}
		return null;
	}
}
