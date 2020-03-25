package org.greatfree.concurrency.mapreduce;

/*
 * The interface defines the method to create the threads for the Map/Reduce. 04/19/2018, Bing Li
 */

// Created: 04/19/2018, Bing Li
public interface MapReduceThreadCreatable<Task extends Sequence, Result extends Sequence, TaskThread extends MapReduceQueue<Task, Result, TaskThread, ThreadCreator>, ThreadCreator extends MapReduceThreadCreatable<Task, Result, TaskThread, ThreadCreator>>
{
	public TaskThread createThreadInstance(int taskSize, MRCore<Task, Result, TaskThread, ThreadCreator> mp);
}
