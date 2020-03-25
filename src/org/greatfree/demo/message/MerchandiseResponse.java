package org.greatfree.demo.message;

import org.greatfree.message.ServerMessage;

// Created: 01/24/2019, Bing Li
public class MerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = 146337320745059823L;
	
	private Merchandise m;

	public MerchandiseResponse(Merchandise m)
	{
		super(ApplicationID.MERCHANDISE_RESPONSE);
		this.m = m;
	}

	public Merchandise getMerchandise()
	{
		return this.m;
	}
}
