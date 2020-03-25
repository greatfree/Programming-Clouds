package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.MRService;
import org.greatfree.concurrency.mapreduce.Sequence;
import org.greatfree.util.Rand;

// Created: 01/27/2019, Bing Li
// class MyMRService implements MRService
class MyMRService extends MRService
{
	public MyMRService(String taskKey)
	{
		super(taskKey);
	}

	@Override
	public Sequence compute(Sequence task)
	{
		MyTask myTask = (MyTask)task;
		try
		{
			System.out.println("MyMRService-compute(): " + myTask.getTaskKey() + ", " + myTask.getSequence());
			Thread.sleep(Rand.getRandom(1000));
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		return new MyResult(myTask.getTaskKey(), myTask.getSequence(), myTask.getName());
	}

}
