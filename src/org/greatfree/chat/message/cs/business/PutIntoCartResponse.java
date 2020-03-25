package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.dip.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/06/2017, Bing Li
public class PutIntoCartResponse extends ServerMessage
{
	private static final long serialVersionUID = -5434330146399064823L;
	
	private Map<String, Merchandise> mcsInCart;

	public PutIntoCartResponse(Map<String, Merchandise> mcsInCart)
	{
		super(BusinessMessageType.PUT_INTO_CART_RESPONSE);
		this.mcsInCart = mcsInCart;
	}

	public Map<String, Merchandise> getMerchandiseInCart()
	{
		return this.mcsInCart;
	}
}
