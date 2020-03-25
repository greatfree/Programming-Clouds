package org.greatfree.concurrency.mapreduce;

import java.util.concurrent.ScheduledThreadPoolExecutor;

// Created: 01/27/2019, Bing Li
public class MapReduceBuilder
{
//	private ThreadPool tp;
	private int poolSize;
	private ScheduledThreadPoolExecutor scheduler;
	private int queueSize;
	private long waitTime;
	private long shutdownTime;
	private long schedulerDelay;
	private long schedulerPeriod;
	
	public MapReduceBuilder()
	{
	}
	
//	public MapReduceBuilder(ThreadPool tp, ScheduledThreadPoolExecutor scheduler, int queueSize, long waitTime, long shutdownTime, long schedulerDelay, long schedulerPeriod)
	public MapReduceBuilder(int poolSize, ScheduledThreadPoolExecutor scheduler, int queueSize, long waitTime, long shutdownTime, long schedulerDelay, long schedulerPeriod)
	{
		this.poolSize = poolSize;
		this.scheduler = scheduler;
		this.queueSize = queueSize;
		this.waitTime = waitTime;
		this.shutdownTime = shutdownTime;
		this.schedulerDelay = schedulerDelay;
		this.schedulerPeriod = schedulerPeriod;
	}

	/*
	public MapReduceBuilder threadPool(ThreadPool tp)
	{
		this.tp = tp;
		return this;
	}
	*/

	public MapReduceBuilder poolSize(int poolSize)
	{
		this.poolSize = poolSize;
		return this;
	}
	
	public MapReduceBuilder scheduler(ScheduledThreadPoolExecutor scheduler)
	{
		this.scheduler = scheduler;
		return this;
	}

	public MapReduceBuilder queueSize(int queueSize)
	{
		this.queueSize = queueSize;
		return this;
	}

	public MapReduceBuilder waitTime(long waitTime)
	{
		this.waitTime = waitTime;
		return this;
	}

	public MapReduceBuilder shutdownTime(long shutdownTime)
	{
		this.shutdownTime = shutdownTime;
		return this;
	}

	public MapReduceBuilder schedulerDelay(long schedulerDelay)
	{
		this.schedulerDelay = schedulerDelay;
		return this;
	}

	public MapReduceBuilder schedulerPeriod(long schedulerPeriod)
	{
		this.schedulerPeriod = schedulerPeriod;
		return this;
	}
	
	public MapReduce build()
	{
		return new MapReduce(this.poolSize, this.scheduler, this.queueSize, this.waitTime, this.shutdownTime, this.schedulerDelay, this.schedulerPeriod);
	}
}
