package org.greatfree.concurrency.reactive;

/*
 * Here, the calling-back is a mechanism to build a mechanism between the caller and the callee to interact. The caller notifies the callee by calling the methods provided by the callee such that the callee can respond to the caller. The caller does that when its running circumstance is changed in a certain situation. 11/20/2014, Bing Li
 * 
 *  In the tutorial, the callee is a thread pool, i.e., InteractiveDispatcher. With the support of the interfaces below, the caller can help the pool to determine how to manage the threads within it. 11/20/2014, Bing Li 
 */

// Created: 11/20/2014, Bing Li
public interface Interactable<Task>
{
	// Pause the execution of a thread. 11/20/2014, Bing Li
	public void pause();
	// Continue to work on a task. 11/20/2014, Bing Li
	public void keepOn();
	// Restore to a fast state for a thread by specifying its key. 11/20/2014, Bing Li
	public void restoreFast(String key);
	// Notify the task is finished. 11/20/2014, Bing Li
	public void done(Task task);
}
