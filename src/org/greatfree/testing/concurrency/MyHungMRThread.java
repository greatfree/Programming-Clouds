package org.greatfree.testing.concurrency;

import org.greatfree.concurrency.mapreduce.MRCore;
import org.greatfree.concurrency.mapreduce.MapReduceQueue;
import org.greatfree.data.ServerConfig;
import org.greatfree.util.Rand;

// Created: 11/06/2019, Bing Li
class MyHungMRThread extends MapReduceQueue<MyHungTask, MyHungResult, MyHungMRThread, MyHungMRThreadCreator>
{

	public MyHungMRThread(int taskSize, MRCore<MyHungTask, MyHungResult, MyHungMRThread, MyHungMRThreadCreator> mp)
	{
		super(taskSize, mp);
	}

	@Override
	public void run()
	{
		MyHungTask task;
		MyHungResult result;
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					task = this.getTask();
					if (!task.isHung())
					{
						result = new MyHungResult(task.getTaskKey(), task.getSequence(), task.getName());
						Thread.sleep(Rand.getRandom(3000));
					}
					else
					{
						System.out.println("MyHungMRThread is starting: " + task.getName());
						result = new MyHungResult(task.getTaskKey(), task.getSequence(), task.getName());
						Thread.sleep(1000000);
						System.out.println("MyHungMRThread is done: " + task.getName());
					}
					this.reduce(result);
				}
				catch (InterruptedException e)
				{
					System.out.println("MyHungMRThread is interrupted!");
					e.printStackTrace();
				}
				finally
				{
					System.out.println("MyHungMRThread is finalized!");
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
