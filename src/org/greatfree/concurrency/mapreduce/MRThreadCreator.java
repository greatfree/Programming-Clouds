package org.greatfree.concurrency.mapreduce;

// Created: 01/27/2019, Bing Li
class MRThreadCreator implements MapReduceThreadCreatable<Sequence, Sequence, MRThread, MRThreadCreator>
{

	@Override
	public MRThread createThreadInstance(int taskSize, MRCore<Sequence, Sequence, MRThread, MRThreadCreator> mp)
	{
		return new MRThread(taskSize, mp);
	}

}
