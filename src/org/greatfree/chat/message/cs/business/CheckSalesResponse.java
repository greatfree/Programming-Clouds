package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.dip.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/22/2017, Bing Li
public class CheckSalesResponse extends ServerMessage
{
	private static final long serialVersionUID = -2277880615014045404L;

	private Map<String, String> vendors;
	private Map<String, Map<String, Merchandise>> mcs;

	public CheckSalesResponse(Map<String, String> vendors, Map<String, Map<String, Merchandise>> mcs)
	{
		super(BusinessMessageType.CHECK_SALES_RESPONSE);
		this.vendors = vendors;
		this.mcs = mcs;
	}
	
	public Map<String, String> getVendors()
	{
		return this.vendors;
	}

	public Map<String, Map<String, Merchandise>> getSales()
	{
		return this.mcs;
	}
}
