package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/07/2017, Bing Li
public class RemoveFromCartResponse extends ServerMessage
{
	private static final long serialVersionUID = -762336789507731468L;
	
	private Map<String, Merchandise> mcsInCart;

	public RemoveFromCartResponse(Map<String, Merchandise> mcsInCart)
	{
		super(BusinessMessageType.REMOVE_FROM_CART_RESPONSE);
		this.mcsInCart = mcsInCart;
	}

	public Map<String, Merchandise> getMerchandiseInCart()
	{
		return this.mcsInCart;
	}
}
