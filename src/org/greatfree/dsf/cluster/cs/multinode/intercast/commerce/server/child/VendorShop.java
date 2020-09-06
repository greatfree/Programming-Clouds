package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message.Merchandise;
import org.greatfree.util.Time;

// Created: 07/21/2019, Bing Li
class VendorShop
{
	private final String shopKey;
	private final String vendorKey;

	private List<MerchandisePost> posts;
	private ReentrantLock lock;

	public VendorShop(String shopKey, String vendorKey)
	{
		this.shopKey = shopKey;
		this.vendorKey = vendorKey;
		this.posts = new ArrayList<MerchandisePost>();
		this.lock = new ReentrantLock();
	}
	
	public String getShopKey()
	{
		return this.shopKey;
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public void addMerchandise(String shopKey, String vendorKey, Merchandise mc)
	{
		this.lock.lock();
		this.posts.add(new MerchandisePost(shopKey, vendorKey, mc, Calendar.getInstance().getTime()));
		this.lock.unlock();
	}

	public List<MerchandisePost> getMerchandisePost(int n, long timespan)
	{
		List<MerchandisePost> posts = new ArrayList<MerchandisePost>();
		this.lock.lock();
		try
		{
			if (this.posts.size() <= n)
			{
				for (MerchandisePost entry : this.posts)
				{
					if (entry.getTime().after(Time.getEarlyTime(timespan)))
					{
						posts.add(entry);
					}
				}
			}
			else
			{
				List<MerchandisePost> candidates = this.posts.subList(this.posts.size() - n - 1, this.posts.size() - 1);
				for (MerchandisePost entry : candidates)
				{
					if (entry.getTime().after(Time.getEarlyTime(timespan)))
					{
						posts.add(entry);
					}
				}
			}
			return posts;
		}
		finally
		{
			this.lock.unlock();
		}
	}

}
