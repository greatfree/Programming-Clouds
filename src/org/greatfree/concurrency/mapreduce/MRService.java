package org.greatfree.concurrency.mapreduce;

// Created: 01/27/2019, Bing Li
// public interface MRService
public abstract class MRService
{
	private String taskKey;
	
	public MRService(String taskKey)
	{
		this.taskKey = taskKey;
	}
	
	public String getTaskKey()
	{
		return this.taskKey;
	}
	
	public abstract Sequence compute(Sequence task);
}
