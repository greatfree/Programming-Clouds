package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.Sequence;

// Created: 04/22/2018, Bing Li
class MyTask extends Sequence
{
	private String name;

	public MyTask(String taskKey, int seq, String name)
	{
		super(taskKey, seq);
		this.name = name;
	}

	public String getName()
	{
		return this.name;
	}
}
