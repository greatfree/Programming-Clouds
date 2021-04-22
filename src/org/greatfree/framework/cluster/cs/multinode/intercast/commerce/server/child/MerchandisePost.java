package org.greatfree.framework.cluster.cs.multinode.intercast.commerce.server.child;

import java.io.Serializable;
import java.util.Date;

import org.greatfree.framework.cluster.cs.multinode.intercast.commerce.message.Merchandise;
import org.greatfree.util.Tools;

// Created: 07/18/2019, Bing Li
public class MerchandisePost implements Serializable
{
	private static final long serialVersionUID = -3728736255407019163L;
	
	private String key;

	private String shopKey;
	private String vendorKey;
	
	private Merchandise mc;
	private Date time;

	public MerchandisePost(String shopKey, String vendorKey, Merchandise mc, Date time)
	{
		this.key = Tools.generateUniqueKey();
		this.shopKey = shopKey;
		this.vendorKey = vendorKey;
		this.mc = mc;
		this.time = time;
	}
	
	public String getKey()
	{
		return this.key;
	}
	
	public String getShopKey()
	{
		return this.shopKey;
	}
	
	public String getVendorKey()
	{
		return this.vendorKey;
	}
	
	public Merchandise getMerchandise()
	{
		return this.mc;
	}
	
	public Date getTime()
	{
		return this.time;
	}
}
