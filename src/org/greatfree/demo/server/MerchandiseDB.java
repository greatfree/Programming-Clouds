package org.greatfree.demo.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.demo.message.Merchandise;

// Created: 01/24/2019, Bing Li
class MerchandiseDB
{
	private Map<String, Merchandise> ms;
	
	private MerchandiseDB()
	{
		this.ms = new ConcurrentHashMap<String, Merchandise>();
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

	public void save(Merchandise m)
	{
		this.ms.put(m.getName(), m);
	}
	
	public Merchandise getMerchandise(String name)
	{
		return this.ms.get(name);
	}
}
