package org.greatfree.chat.message.cs.business;

import org.greatfree.app.business.dip.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/04/2017, Bing Li
public class PostMerchandiseNotification extends ServerMessage
{
	private static final long serialVersionUID = 7840111131419379066L;

	private String vendorKey;
	private String vendorName;
	private Merchandise mc;

	public PostMerchandiseNotification(String vendorKey, String vendorName, Merchandise mc)
	{
		super(BusinessMessageType.POST_MERCHANDISE_NOTIFICATION);
		this.vendorKey = vendorKey;
		this.vendorName = vendorName;
		this.mc = mc;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public String getVendorName()
	{
		return this.vendorName;
	}
	
	public Merchandise getMerchandise()
	{
		return this.mc;
	}
}
