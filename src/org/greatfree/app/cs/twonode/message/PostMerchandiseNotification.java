package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/31/2018, bing Li
public class PostMerchandiseNotification extends ServerMessage
{
	private static final long serialVersionUID = 2577329007844959030L;

	private String vendor;
	private Merchandise mc;

	public PostMerchandiseNotification(String vendor, Merchandise mc)
	{
		super(BusinessMessageType.POST_MERCHANDISE_NOTIFICATION);
		this.vendor = vendor;
		this.mc = mc;
	}
	
	public String getVendor()
	{
		return this.vendor;
	}
	
	public Merchandise getMerchandise()
	{
		return this.mc;
	}
}
