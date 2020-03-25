package org.greatfree.app.cps.message;

import org.greatfree.message.ServerMessage;

// Created: 08/14/2018, Bing Li
public class MerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = -6016239862679067865L;
	
	private String merchandise;
	private float price;

	public MerchandiseResponse(String merchandise, float price)
	{
		super(BusinessMessageType.MERCHANDISE_RESPONSE);
		this.merchandise = merchandise;
		this.price = price;
	}

	public String getMerchandise()
	{
		return this.merchandise;
	}
	
	public float getPrice()
	{
		return this.price;
	}
}
