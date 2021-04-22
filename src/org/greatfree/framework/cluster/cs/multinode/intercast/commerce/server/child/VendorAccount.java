package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.Set;

import com.google.common.collect.Sets;

// Created: 07/21/2019, Bing Li
class VendorAccount
{
	private String vendorKey;
	
	private Set<String> followers;
	
	public VendorAccount(String vendorKey)
	{
		this.vendorKey = vendorKey;
		this.followers = Sets.newHashSet();
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
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
