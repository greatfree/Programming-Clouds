package org.greatfree.dsf.cluster.cs.multinode.intercast.commerce.message;

import org.greatfree.message.multicast.container.IntercastNotification;

// Created: 07/14/2019, Bing Li
public class FollowMerchandiseNotification extends IntercastNotification
{
	private static final long serialVersionUID = -6227430433986816331L;

	private String customerKey;
	private String merchandiseKey;

	public FollowMerchandiseNotification(String customerKey, String merchandiseKey)
	{
		super(customerKey, merchandiseKey, CommerceApplicationID.FOLLOW_MERCHANDISE_NOTIFICATION);
		this.customerKey = customerKey;
		this.merchandiseKey = merchandiseKey;
	}

	public String getCustomerKey()
	{
		return this.customerKey;
	}
	
	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
}
