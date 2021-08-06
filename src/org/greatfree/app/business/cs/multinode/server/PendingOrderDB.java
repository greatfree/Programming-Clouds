package org.greatfree.app.business.cs.multinode.server;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 12/11/2017, Bing Li
class PendingOrderDB
{
	// The keys represent the vendors, the customers and the merchandises, respectively. 12/11/2017, Bing Li
	private Map<String, Map<String, Map<String, Integer>>> orders;

	private PendingOrderDB()
	{
		// Define a concurrent map for the consideration of consistency to save merchandises. 12/04/2017, Bing Li
		this.orders = new ConcurrentHashMap<String, Map<String, Map<String, Integer>>>();
	}

	/*
	 * A singleton implementation. 11/09/2014, Bing Li
	 */
	private static PendingOrderDB instance = new PendingOrderDB();
	
	public static PendingOrderDB CS()
	{
		if (instance == null)
		{
			instance = new PendingOrderDB();
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
		this.orders.clear();
		this.orders = null;
	}

	/*
	 * Get the orders. 12/11/2017, Bing Li
	 */
	public Map<String, Map<String, Integer>> getOrders(String vendorKey)
	{
		if (this.orders.containsKey(vendorKey))
		{
			return this.orders.get(vendorKey);
		}
		return null;
	}

	/*
	 * Save new orders. 12/12/2017, Bing Li
	 */
	public void saveOrder(String vendorKey, String customerKey, String merchandiseKey, int count)
	{
		if (!this.orders.containsKey(vendorKey))
		{
			this.orders.put(vendorKey, new HashMap<String, Map<String, Integer>>());
			this.orders.get(vendorKey).put(customerKey, new HashMap<String, Integer>());
			this.orders.get(vendorKey).get(customerKey).put(merchandiseKey, count);
		}
		else if (!this.orders.get(vendorKey).containsKey(customerKey))
		{
			this.orders.get(vendorKey).put(customerKey, new HashMap<String, Integer>());
			this.orders.get(vendorKey).get(customerKey).put(merchandiseKey, count);
		}
		else if (!this.orders.get(vendorKey).get(customerKey).containsKey(merchandiseKey))
		{
			this.orders.get(vendorKey).get(customerKey).put(merchandiseKey, count);
		}
		else
		{
			int existingCount = this.orders.get(vendorKey).get(customerKey).get(merchandiseKey);
			this.orders.get(vendorKey).get(customerKey).put(merchandiseKey, existingCount + count);
		}
	}

	/*
	 * Remove placed order. 12/12/2017, Bing Li
	 */
	public void removeOrder(String vendorKey, String customerKey, String merchandiseKey, int count)
	{
		if (this.orders.containsKey(vendorKey))
		{
			if (this.orders.get(vendorKey).containsKey(customerKey))
			{
				if (this.orders.get(vendorKey).get(customerKey).containsKey(merchandiseKey))
				{
					int existingCount = this.orders.get(vendorKey).get(customerKey).get(merchandiseKey);
					if (existingCount > count)
					{
						this.orders.get(vendorKey).get(customerKey).put(merchandiseKey, existingCount - count);
					}
					else
					{
						this.orders.get(vendorKey).get(customerKey).remove(merchandiseKey);
						if (this.orders.get(vendorKey).get(customerKey).size() <= 0)
						{
							this.orders.get(vendorKey).remove(customerKey);
							if (this.orders.get(vendorKey).size() <= 0)
							{
								this.orders.remove(vendorKey);
							}
						}
					}
				}
			}
		}
	}
}
