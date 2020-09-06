package org.greatfree.dsf.streaming.subscriber;

import java.util.ArrayList;
import java.util.List;

// Created: 03/21/2020, Bing Li
public class SubscriberDB
{
	private List<String> db;
	
	private SubscriberDB()
	{
		this.db = new ArrayList<String>();
	}
	
	private static SubscriberDB instance = new SubscriberDB();
	
	public static SubscriberDB DB()
	{
		if (instance == null)
		{
			instance = new SubscriberDB();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void save(String data)
	{
		this.db.add(data);
	}
	
	public List<String> search(String keyword)
	{
		List<String> rs = new ArrayList<String>();
		for (String entry : this.db)
		{
			if (entry.contains(keyword))
			{
				rs.add(entry);
			}
		}
		return rs;
	}
}
