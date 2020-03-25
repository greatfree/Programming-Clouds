package org.greatfree.concurrency.mapreduce;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Created: 01/27/2019, Bing Li
class MRServiceProvider
{
	private Map<String, MRService> services;
	
	private MRServiceProvider()
	{
		this.services = new ConcurrentHashMap<String, MRService>();
	}
	
	private static MRServiceProvider instance = new MRServiceProvider();
	
	public static MRServiceProvider MR()
	{
		if (instance == null)
		{
			instance = new MRServiceProvider();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init(String taskKey, MRService service)
	{
		this.services.put(taskKey, service);
	}
	
	public Sequence map(Sequence task)
	{
		return this.services.get(task.getTaskKey()).compute(task);
	}
}
