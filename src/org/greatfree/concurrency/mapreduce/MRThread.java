package org.greatfree.concurrency.mapreduce;

import org.greatfree.data.ServerConfig;

// Created: 01/27/2019, Bing Li
class MRThread extends MapReduceQueue<Sequence, Sequence, MRThread, MRThreadCreator>
{
	public MRThread(int taskSize, MRCore<Sequence, Sequence, MRThread, MRThreadCreator> mp)
	{
		super(taskSize, mp);
	}

	@Override
	public void run()
	{
		while (!this.isShutdown())
		{
			while (!this.isEmpty())
			{
				try
				{
					this.reduce(MRServiceProvider.MR().map(this.getTask()));
				}
				catch (InterruptedException e)
				{
					/*
					 * When the thread hangs, it needs to be interrupted. The exception is not necessary to show. 11/08/2019, Bing Li
					 */
//					e.printStackTrace();
					System.out.println(MRPrompts.MR_THREAD_IS_INTERRUPTED);
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
