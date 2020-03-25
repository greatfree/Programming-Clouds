package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 07/18/2019, Bing Li
class Market
{
	private Map<String, VendorShop> shops;

	private Market()
	{
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static Market instance = new Market();
	
	public static Market FILLED()
	{
		if (instance == null)
		{
			instance = new Market();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	/*
	 * Dispose the free client pool. 04/17/2017, Bing Li
	 */
	public void dispose()
	{
		this.shops.clear();
		this.shops = null;
	}

	public void init()
	{
		this.shops = new ConcurrentHashMap<String, VendorShop>();
	}
	
	public void createShop(String vendorKey, String shopKey)
	{
		this.shops.put(shopKey, new VendorShop(shopKey, vendorKey));
	}
	
	public void addPost(MerchandisePost post)
	{
		if (this.shops.containsKey(post.getShopKey()))
		{
			this.shops.get(post.getShopKey()).addMerchandise(post.getShopKey(), post.getVendorKey(), post.getMerchandise());
		}
	}
	
	public List<MerchandisePost> getPosts(String shopKey, int n, long timespan)
	{
		if (this.shops.containsKey(shopKey))
		{
			return this.shops.get(shopKey).getMerchandisePost(n, timespan);
		}
		return null;
	}
}
