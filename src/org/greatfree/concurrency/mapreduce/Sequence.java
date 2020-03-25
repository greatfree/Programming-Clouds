package org.greatfree.concurrency.mapreduce;

/*
 * The class represents the ID of tasks in the type of Integer. It is convenient to manage tasks that should be executed concurrently. 04/19/2018, Bing Li
 */

// Created: 04/19/2018, Bing Li
public abstract class Sequence
{
	private String taskKey;
	private int sequence;
	
	public Sequence(String taskKey, int seq)
	{
		this.taskKey = taskKey;
		this.sequence = seq;
	}
	
	public String getTaskKey()
	{
		return this.taskKey;
	}

	public int getSequence()
	{
		return this.sequence;
	}

	public void setSequence(int seq)
	{
		this.sequence = seq;
	}
}
