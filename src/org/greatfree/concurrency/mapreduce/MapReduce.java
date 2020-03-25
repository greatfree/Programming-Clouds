package org.greatfree.concurrency.mapreduce;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledThreadPoolExecutor;

// Created: 01/27/2019, Bing Li
public class MapReduce
{
	private MRCore<Sequence, Sequence, MRThread, MRThreadCreator> mr;
	// The reasons to design the map is to assign each MR with one collection to save tasks. Then, they do not share data structures to save tasks. It raises the performance. 01/29/2019, Bing Li
	private Map<String, Map<Integer, Sequence>> tasks;
	
//	public MapReduce(ThreadPool tp, ScheduledThreadPoolExecutor scheduler, MRService service, int queueSize)
//	public MapReduce(ThreadPool tp, ScheduledThreadPoolExecutor scheduler, int queueSize, long waitTime, long shutdownTime, long schedulerDelay, long schedulerPeriod)
	public MapReduce(int poolSize, ScheduledThreadPoolExecutor scheduler, int queueSize, long waitTime, long shutdownTime, long schedulerDelay, long schedulerPeriod)
	{
		this.mr = new MRCore.MRCoreBuilder<Sequence, Sequence, MRThread, MRThreadCreator>()
//				.threadPool(tp)
				.poolSize(poolSize)
				.threadCreator(new MRThreadCreator())
				.taskSizePerThread(queueSize)
				.waitTime(waitTime)
				.shutdownTime(shutdownTime)
				.scheduler(scheduler)
				.build();
		
		this.tasks = new ConcurrentHashMap<String, Map<Integer, Sequence>>();
		this.mr.setIdleChecker(schedulerDelay, schedulerPeriod);
	}
	
	public void dispose() throws InterruptedException
	{
		this.mr.dispose();
		this.tasks.clear();
		this.tasks = null;
	}

	/*
	public void create(String taskKey)
	{
		this.tasks.put(taskKey, new HashMap<Integer, Sequence>());
	}
	*/

	public void add(Sequence task)
	{
		if (!this.tasks.containsKey(task.getTaskKey()))
		{
			this.tasks.put(task.getTaskKey(), new HashMap<Integer, Sequence>());
		}
		this.tasks.get(task.getTaskKey()).put(task.getSequence(), task);
	}
	
	public int getTaskSize(String taskKey)
	{
		return this.tasks.get(taskKey).size();
	}

	/*
	public void clearTasks(String taskKey)
	{
		this.tasks.get(taskKey).clear();
		this.tasks.remove(taskKey);
	}
	*/

//	public Map<Integer, Sequence> compute(String taskKey, MRService service) throws InterruptedException
	public Map<Integer, Sequence> compute(MRService service) throws InterruptedException
	{
		MRServiceProvider.MR().init(service.getTaskKey(), service);
		Map<Integer, Sequence> results = this.mr.map(this.tasks.get(service.getTaskKey()));
		this.tasks.remove(service.getTaskKey());
		return results;
	}
}
