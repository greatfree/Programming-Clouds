package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.Sequence;

// Created: 11/06/2019, Bing Li
class MyHungResult extends Sequence
{
	private String result;

	public MyHungResult(String taskKey, int seq, String result)
	{
		super(taskKey, seq);
		this.result = result;
	}

	public String getResult()
	{
		return this.result;
	}
}
