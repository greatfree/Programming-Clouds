package org.greatfree.demo.cluster.mncs.message;

import org.greatfree.app.container.cs.multinode.business.message.Merchandise;
import org.greatfree.message.multicast.MulticastMessageType;
import org.greatfree.message.multicast.container.Notification;

// Created: 02/17/2019, Bing Li
public class PostMerchandiseNotification extends Notification
{
	private static final long serialVersionUID = 7615143610646373449L;
	
	private String vendorKey;
	
	private Merchandise m;

	public PostMerchandiseNotification(String vendorKey, Merchandise m)
	{
		super(vendorKey, MulticastMessageType.UNICAST_NOTIFICATION, BusinessApplicationID.POST_MERCHANDISE_NOTIFICATION);
		this.vendorKey = vendorKey;
		this.m = m;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public Merchandise getMerchandise()
	{
		return this.m;
	}
}
