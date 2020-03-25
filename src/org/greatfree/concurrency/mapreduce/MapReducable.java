package org.greatfree.concurrency.mapreduce;

/*
 * The interface defines the method signatures, reduce() and reset(). The one, reduce(), collects the results from multiple concurrent threads. The one, reset(), enables the Map/Reduce to get ready for further concurrent execution. 04/19/2018, Bing Li
 */

// Created: 04/19/2018, Bing Li
interface MapReducable<Result>
{
	// The method signature to reduce. 04/21/2018, Bing Li
	public void reduce(Result t);
	// The method signature to reset the Map/Reduce mechanism for reuse. 04/21/2018, Bing Li
	public void reset() throws InterruptedException;
}
