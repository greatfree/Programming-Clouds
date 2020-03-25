package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.Sequence;

// Created: 04/22/2018, Bing Li
class MyResult extends Sequence
{
	private String result;

	public MyResult(String taskKey, int seq, String result)
	{
		super(taskKey, seq);
		this.result = result;
	}

	public String getResult()
	{
		return this.result;
	}
}
