package org.greatfree.dip.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.dip.cluster.cs.multinode.intercast.commerce.message.Merchandise;
import org.greatfree.dip.cs.twonode.server.AccountRegistry;
import org.greatfree.util.Time;

// Created: 07/18/2019, Bing Li
class MerchandiseInfo
{
	private final String merchandiseKey;
	
	private List<MerchandisePost> posts;
	private ReentrantLock lock;
	
	public MerchandiseInfo(String merchandiseKey)
	{
		this.merchandiseKey = merchandiseKey;
		this.posts = new ArrayList<MerchandisePost>();
		this.lock = new ReentrantLock();
	}

	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
	
	public void addMerchandise(String vendorKey, Merchandise mc)
	{
		this.lock.lock();
		this.posts.add(new MerchandisePost(vendorKey, AccountRegistry.CS().getAccount(vendorKey).getUserName(), mc, Calendar.getInstance().getTime()));
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
