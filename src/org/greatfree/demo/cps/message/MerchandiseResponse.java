package org.greatfree.demo.cps.message;

import org.greatfree.message.ServerMessage;

// Created: 01/28/2019, Bing Li
public class MerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = 7244536859366762400L;
	
	private String merchandise;
	private float price;

	public MerchandiseResponse(String merchandise, float price)
	{
		super(ApplicationID.MERCHANDISE_RESPONSE);
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
