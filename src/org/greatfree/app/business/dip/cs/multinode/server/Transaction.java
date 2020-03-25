package org.greatfree.app.business.dip.cs.multinode.server;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

// Created: 12/15/2017, Bing Li
public class Transaction implements Serializable
{
	private static final long serialVersionUID = 6764454426983998389L;
	
	private String key;
	private String vendorName;
	private String customerName;
	private Map<String, Merchandise> mcs;
	private float payment;
	private Date time;
	
	public Transaction(String key, String vendorName, String customerName, Map<String, Merchandise> mcs, float payment, Date time)
	{
		this.key = key;
		this.vendorName = vendorName;
		this.customerName = customerName;
		this.mcs = mcs;
		this.payment = payment;
		this.time = time;
	}
	
	public String getKey()
	{
		return this.key;
	}

	public String getVendorName()
	{
		return this.vendorName;
	}
	
	public String getCustomerName()
	{
		return this.customerName;
	}
	
	public Map<String, Merchandise> getMerchandises()
	{
		return this.mcs;
	}
	
	public float getPayment()
	{
		return this.payment;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
