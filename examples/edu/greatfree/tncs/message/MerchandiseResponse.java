package edu.greatfree.tncs.message;

import org.greatfree.message.ServerMessage;

// Created: 04/30/2019, Bing Li
public class MerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = -3292370275115516847L;
	
	private Merchandise mcd;

	public MerchandiseResponse(Merchandise mcd)
	{
		super(ECommerceMessageType.MERCHANDISE_RESPONSE);
		this.mcd = mcd;
	}
	
	public Merchandise getMerchandise()
	{
		return this.mcd;
	}
}
