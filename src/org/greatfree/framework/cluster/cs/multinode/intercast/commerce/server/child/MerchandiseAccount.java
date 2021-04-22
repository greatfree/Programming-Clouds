package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.Set;

import com.google.common.collect.Sets;

// Created: 07/18/2019, Bing Li
class MerchandiseAccount
{
	private String merchandiseKey;
	
	private Set<String> followers;
	
	public MerchandiseAccount(String merchandiseKey)
	{
		this.merchandiseKey = merchandiseKey;
		this.followers = Sets.newHashSet();
	}

	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
	
	public void follow(String customerKey)
	{
		this.followers.add(customerKey);
	}
	
	public void unfollow(String customerKey)
	{
		this.followers.remove(customerKey);
	}
	
	public Set<String> getFollowers()
	{
		return this.followers;
	}
}
