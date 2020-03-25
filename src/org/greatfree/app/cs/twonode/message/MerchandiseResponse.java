package org.greatfree.app.cs.twonode.message;

import org.greatfree.message.ServerMessage;

// Created: 07/27/2018, Bing Li
public class MerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = 7798671042802501855L;
	
	private String merchandiseDesc;

	public MerchandiseResponse(String merchandiseDesc)
	{
		super(BusinessMessageType.MERCHANDISE_RESPONSE);
		this.merchandiseDesc = merchandiseDesc;
	}

	public String getMerchandiseDesc()
	{
		return this.merchandiseDesc;
	}
}
