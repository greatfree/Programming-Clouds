package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/11/2017, Bing Li
public class CheckCartResponse extends ServerMessage
{
	private static final long serialVersionUID = 743385564402659349L;

	private Map<String, Merchandise> mcs;

	public CheckCartResponse(Map<String, Merchandise> mcs)
	{
		super(BusinessMessageType.CHECK_CART_RESPONSE);
		this.mcs = mcs;
	}

	public Map<String, Merchandise> getMerchandises()
	{
		return this.mcs;
	}
}
