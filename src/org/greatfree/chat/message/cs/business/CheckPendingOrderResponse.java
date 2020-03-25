package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.dip.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/15/2017, Bing Li
public class CheckPendingOrderResponse extends ServerMessage
{
	private static final long serialVersionUID = 8535162108929797899L;
	
	private Map<String, String> customers;
	private Map<String, Map<String, Merchandise>> mcs;

	public CheckPendingOrderResponse(Map<String, String> customers, Map<String, Map<String, Merchandise>> mcs)
	{
		super(BusinessMessageType.CHECK_PENDING_ORDER_RESPONSE);
		this.customers = customers;
		this.mcs = mcs;
	}

	public Map<String, String> getCustomers()
	{
		return this.customers;
	}
	
	public Map<String, Map<String, Merchandise>> getMerchandises()
	{
		return this.mcs;
	}
}
