package org.greatfree.app.cs.twonode.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.app.cs.twonode.message.Merchandise;

// Created: 07/31/2018, Bing Li
class MerchandiseDB
{
	private Map<String, Merchandise> db;

	private MerchandiseDB()
	{
		this.db = new ConcurrentHashMap<String, Merchandise>();
	}
	
	private static MerchandiseDB instance = new MerchandiseDB();
	
	public static MerchandiseDB CS()
	{
		if (instance == null)
		{
			instance = new MerchandiseDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void saveMerchandise(String vendor, Merchandise mc)
	{
		this.db.put(vendor, mc);
	}
	
	public boolean isAvailable(String merchandise, int quantity)
	{
		for (Map.Entry<String, Merchandise> entry : db.entrySet())
		{
			if (entry.getValue().getMerchandise().equals(merchandise))
			{
				if (entry.getValue().getInStockQuantity() > quantity)
				{
					return true;
				}
			}
		}
		return false;
	}
}
