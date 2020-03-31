package edu.greatfree.tncs.message;

import org.greatfree.message.ServerMessage;

// Created: 04/30/2019, Bing Li
public class MerchandiseRequest extends ServerMessage
{
	private static final long serialVersionUID = 3656755068015986110L;

	private String model;

	public MerchandiseRequest(String model)
	{
		super(ECommerceMessageType.MERCHANDISE_REQUEST);
		this.model = model;
	}

	public String getModel()
	{
		return this.model;
	}
}
