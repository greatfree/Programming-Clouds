package org.greatfree.chat.message.cs.business;

import java.util.Map;

import org.greatfree.app.business.cs.multinode.server.Merchandise;
import org.greatfree.message.ServerMessage;

// Created: 12/07/2017, Bing Li
public class PlaceOrderResponse extends ServerMessage
{
	private static final long serialVersionUID = -980778598862784108L;
	
	private String customerName;
	private String vendorName;
	private Map<String, Merchandise> mcs;
	private float payment;

	public PlaceOrderResponse(String customerName, String vendorName, Map<String, Merchandise> mcs, float payment)
	{
		super(BusinessMessageType.PLACE_ORDER_RESPONSE);
		this.customerName = customerName;
		this.vendorName = vendorName;
		this.mcs = mcs;
		this.payment = payment;
	}

	public String getCustomerName()
	{
		return this.customerName;
	}
	
	public String getVendorName()
	{
		return this.vendorName;
	}
	
	public Map<String, Merchandise> getMerchandise()
	{
		return this.mcs;
	}
	
	public float getPayment()
	{
		return this.payment;
	}
}
