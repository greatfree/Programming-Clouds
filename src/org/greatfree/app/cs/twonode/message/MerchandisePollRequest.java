package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/31/2018, Bing Li
public class MerchandisePollRequest extends ServerMessage
{
	private static final long serialVersionUID = 6257746984909771031L;
	
	private String merchandise;
	private int quantity;

	public MerchandisePollRequest(String merchandise, int quantity)
	{
		super(BusinessMessageType.MERCHANDISE_POLL_REQUEST);
		this.merchandise = merchandise;
		this.quantity = quantity;
	}
	
	public String getMerchandise()
	{
		return this.merchandise;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
}
