package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 07/22/2019, Bing Li
public class FollowShopNotification extends IntercastNotification
{
	private static final long serialVersionUID = 5369916806394886495L;
	
	private String customerKey;
	private String shopKey;

	public FollowShopNotification(String customerKey, String shopKey)
	{
		super(customerKey, shopKey, CommerceApplicationID.FOLLOW_SHOP_NOTIFICATION);
		this.customerKey = customerKey;
		this.shopKey = shopKey;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
	
	public String getShopKey()
	{
		return this.shopKey;
	}
}
