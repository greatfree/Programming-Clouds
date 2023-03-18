package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child;

import java.util.HashSet;
import java.util.Set;

// Created: 07/21/2019, Bing Li
class VendorAccount
{
	private String vendorKey;
	
	private Set<String> followers;
	
	public VendorAccount(String vendorKey)
	{
		this.vendorKey = vendorKey;
//		this.followers = Sets.newHashSet();
		this.followers = new HashSet<String>();
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
