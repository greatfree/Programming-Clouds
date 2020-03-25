package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.MRCore;
import org.greatfree.concurrency.mapreduce.MapReduceThreadCreatable;

// Created: 11/06/2019, Bing Li
class MyHungMRThreadCreator implements MapReduceThreadCreatable<MyHungTask, MyHungResult, MyHungMRThread, MyHungMRThreadCreator>
{

	@Override
	public MyHungMRThread createThreadInstance(int taskSize, MRCore<MyHungTask, MyHungResult, MyHungMRThread, MyHungMRThreadCreator> mp)
	{
		return new MyHungMRThread(taskSize, mp);
	}

}
