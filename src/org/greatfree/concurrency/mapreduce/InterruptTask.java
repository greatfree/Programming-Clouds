package org.greatfree.concurrency.mapreduce;

import java.util.concurrent.atomic.AtomicBoolean;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;

// Created: 11/08/2019, Bing Li
class InterruptTask<Task extends Sequence, Result extends Sequence, TaskThread extends MapReduceQueue<Task, Result, TaskThread, ThreadCreator>, ThreadCreator extends MapReduceThreadCreatable<Task, Result, TaskThread, ThreadCreator>> extends RunnerTask
{
	private MRCore<Task, Result, TaskThread, ThreadCreator> core;
	private AtomicBoolean isDown;
	private Sync coordinator;
	
	public InterruptTask(MRCore<Task, Result, TaskThread, ThreadCreator> core)
	{
		this.core = core;
		this.isDown = new AtomicBoolean(false);
		this.coordinator = new Sync(false);
	}
	
	public void interrupt()
	{
		this.coordinator.signalAll();
	}

	@Override
	public void run()
	{
		while (!this.isDown.get())
		{
			try
			{
				this.core.reset();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			this.coordinator.holdOn();
		}
	}

	@Override
	public int getWorkload()
	{
		return 0;
	}

	@Override
	public void dispose() throws InterruptedException
	{
		this.coordinator.signalAll();
		this.isDown.set(true);
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		this.coordinator.signalAll();
		this.isDown.set(true);
	}

}
