package org.greatfree.concurrency;

/*
 * This is a class that is used to simplify the procedure to invoke a single thread that is derived from the class of Thread. 07/17/2014, Bing Li
 */

// Created: 08/04/2014, Bing Li
// public class Threader<Function extends Thread, FunctionDisposer extends ThreadDisposable<Function>>
public class Threader<Function extends ThreaderTask>
{
	// The task that needs to be taken concurrently. 08/04/2014, Bing Li
	private Function function;

	// The class aims to dispose the task being executed concurrently. 08/04/2014, Bing Li
//	private FunctionDisposer disposer;
	
//	public Threader(Function func, FunctionDisposer disposer)
	public Threader(Function func)
	{
		// The constructor encapsulates the thread initialization task. It looks cleaner than the situation in which the procedure is exposed to developers. 08/04/2014, Bing Li  
		this.function = func;
		this.function.setDaemon(true);
//		this.disposer = disposer;
	}
	
//	public Threader(Function func, FunctionDisposer disposer, boolean isDaemon)
	public Threader(Function func, boolean isDaemon)
	{
		// The constructor encapsulates the thread initialization task. It looks cleaner than the situation in which the procedure is exposed to developers. 08/04/2014, Bing Li  
		this.function = func;
		this.function.setDaemon(isDaemon);
//		this.disposer = disposer;
	}
	
	/*
	 * Start the initialized thread. 08/04/2014, Bing Li
	 */
	public void start()
	{
		this.function.start();
	}
	
	/*
	 * Stop the thread and wait for its death for some time. 08/04/2014, Bing Li
	 */
	public void stop(long waitTime) throws InterruptedException
	{
//		this.disposer.dispose(this.function, waitTime);
		this.function.dispose(waitTime);
	}
	
	/*
	 * Stop the thread by waiting for its death. 08/04/2014, Bing Li
	 */
	public void stop() throws InterruptedException
	{
//		this.disposer.dispose(this.function);
		this.function.dispose();
	}
	
	/*
	 * Expose the task. 08/04/2014, Bing Li
	 */
	public Function getFunction()
	{
		return this.function;
	}
	
	/*
	 * Detect whether the thread is alive. 08/04/2014, Bing Li
	 */
	public boolean isAlive()
	{
		return this.function.isAlive();
	}
}
