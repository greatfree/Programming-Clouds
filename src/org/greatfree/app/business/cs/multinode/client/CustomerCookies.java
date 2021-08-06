package org.greatfree.app.business.cs.multinode.client;

import org.greatfree.util.UtilConfig;

// Created: 12/23/2017, Bing Li
public class CustomerCookies
{
	private String vendorKey;
	private String merchandiseKey;
	
	private CustomerCookies()
	{
		this.vendorKey = UtilConfig.EMPTY_STRING;
		this.merchandiseKey = UtilConfig.EMPTY_STRING;
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Li
	 */
	private static CustomerCookies instance = new CustomerCookies();
	
	public static CustomerCookies STATE()
	{
		if (instance == null)
		{
			instance = new CustomerCookies();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public void setVendorKey(String vendorKey)
	{
		this.vendorKey = vendorKey;
	}
	
	public String getMerchandiseKey()
	{
		return this.merchandiseKey;
	}
	
	public void setMerchandiseKey(String mcKey)
	{
		this.merchandiseKey = mcKey;
	}
}
