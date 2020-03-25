package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.MRCore;
import org.greatfree.concurrency.mapreduce.MapReduceQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Rand;

// Created: 04/22/2018, Bing Li
class MyMRThread extends MapReduceQueue<MyTask, MyResult, MyMRThread, MyMRThreadCreator>
{

	public MyMRThread(int taskSize, MRCore<MyTask, MyResult, MyMRThread, MyMRThreadCreator> mp)
	{
		super(taskSize, mp);
	}

	@Override
	public void run()
	{
		MyTask task;
		MyResult result;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					task = this.getTask();
					result = new MyResult(task.getTaskKey(), task.getSequence(), task.getName());
					Thread.sleep(Rand.getRandom(5000));
//					Thread.sleep(3000);
					this.reduce(result);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
			}
			try
			{
				this.holdOn(ServerConfig.NOTIFICATION_THREAD_WAIT_TIME);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public int getWorkload()
	{
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}
}
