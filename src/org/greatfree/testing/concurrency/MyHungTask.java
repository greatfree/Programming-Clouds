package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.Sequence;

// Created: 11/06/2019, Bing Li
class MyHungTask extends Sequence
{
	private String name;
	private boolean isHung;

	public MyHungTask(String taskKey, int seq, String name, boolean isHung)
	{
		super(taskKey, seq);
		this.name = name;
		this.isHung = isHung;
	}

	public String getName()
	{
		return this.name;
	}
	
	public boolean isHung()
	{
		return this.isHung;
	}
}
