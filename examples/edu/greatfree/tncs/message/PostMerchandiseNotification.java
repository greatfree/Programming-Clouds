package edu.greatfree.tncs.message;

import org.greatfree.message.ServerMessage;

// Created: 05/18/2019, Bing Li
public class PostMerchandiseNotification extends ServerMessage
{
	private static final long serialVersionUID = -301201089346625158L;
	
	private Merchandise mcd;

	public PostMerchandiseNotification(Merchandise mcd)
	{
		super(ECommerceMessageType.POST_MERCHANDISE_NOTIFICATION);
		this.mcd = mcd;
	}

	public Merchandise getMerchandise()
	{
		return this.mcd;
	}
}
