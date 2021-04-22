package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 07/18/2019, Bing Li
class MerchandiseStore
{
	private Map<String, MerchandiseInfo> posts;
	
	private MerchandiseStore()
	{
	}
	
	/*
	 * A singleton definition. 04/17/2017, Bing Li
	 */
	private static MerchandiseStore instance = new MerchandiseStore();
	
	public static MerchandiseStore FILLED()
	{
		if (instance == null)
		{
			instance = new MerchandiseStore();
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
		this.posts.clear();
		this.posts = null;
	}

	/*
	 * Initialize explicitly. 04/23/2017, Bing Li
	 */
	public void init()
	{
		this.posts = new ConcurrentHashMap<String, MerchandiseInfo>();
	}

	public void addPost(MerchandisePost post)
	{
		if (!this.posts.containsKey(post.getMerchandise().getMerchandiseID()))
		{
			this.posts.put(post.getMerchandise().getMerchandiseID(), new MerchandiseInfo(post.getMerchandise().getMerchandiseID()));
		}
		this.posts.get(post.getMerchandise().getMerchandiseID()).addMerchandise(post.getVendorKey(), post.getMerchandise());
	}

	public List<MerchandisePost> getPosts(String merchandiseKey, int n, long timespan)
	{
		if (this.posts.containsKey(merchandiseKey))
		{
			return this.posts.get(merchandiseKey).getMerchandisePost(n, timespan);
		}
		return null;
	}
}
