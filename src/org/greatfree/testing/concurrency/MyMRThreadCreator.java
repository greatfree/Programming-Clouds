package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.MRCore;
import org.greatfree.concurrency.mapreduce.MapReduceThreadCreatable;

// Created: 04/22/2018, Bing Li
class MyMRThreadCreator implements MapReduceThreadCreatable<MyTask, MyResult, MyMRThread, MyMRThreadCreator>
{

	@Override
	public MyMRThread createThreadInstance(int taskSize, MRCore<MyTask, MyResult, MyMRThread, MyMRThreadCreator> mp)
	{
		return new MyMRThread(taskSize, mp);
	}

}
