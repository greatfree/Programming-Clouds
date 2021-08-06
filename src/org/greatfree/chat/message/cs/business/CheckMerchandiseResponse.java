package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/05/2017, Bing Li
public class CheckMerchandiseResponse extends ServerMessage
{
	private static final long serialVersionUID = 4289655078060999981L;
	
	private Map<String, Merchandise> mcs;

	public CheckMerchandiseResponse(Map<String, Merchandise> mcs)
	{
		super(BusinessMessageType.CHECK_MERCHANDISE_RESPONSE);
		this.mcs = mcs;
	}

	public Map<String, Merchandise> getMerchandises()
	{
		return this.mcs;
	}
}
