package org.greatfree.app.container.cs.multinode.business.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.app.container.cs.multinode.business.message.Merchandise;

// Created: 01/24/2019, Bing Li
public class MerchandiseDB
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

	public void saveMerchandise(Merchandise m)
	{
		this.ms.put(m.getName(), m);
	}

	public Merchandise getMerchandise(String name)
	{
		return this.ms.get(name);
	}
}
