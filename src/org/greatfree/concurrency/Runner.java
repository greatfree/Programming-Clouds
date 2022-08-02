package org.greatfree.concurrency;

/*
 * This is a class that is used to simplify the procedure to invoke a single thread that implements the interface of Runnable. 07/17/2014, Bing Li
 */

// Created: 07/17/2014, Bing Li
//public class Runner<Function extends Runnable, FunctionDisposer extends RunnerDisposable<Function>>
public class Runner<Function extends RunnerTask> implements Comparable<Runner<Function>>
{
	// The thread that supports the concurrency. 08/04/2014, Bing Li
	private Thread thread;
	
	// The task that needs to be taken concurrently. 08/04/2014, Bing Li
	private Function function;
	
	// The class aims to dispose the task being executed concurrently. 08/04/2014, Bing Li
//	private FunctionDisposer disposer;
	
//	public Runner(Function func, FunctionDisposer disposer)
	public Runner(Function func)
	{
		// The constructor encapsulates the thread initialization task. It looks cleaner than the situation in which the procedure is exposed to developers. 08/04/2014, Bing Li  
		this.function = func;
		this.thread = new Thread(this.function);
		this.thread.setDaemon(true);
//		this.disposer = disposer;
	}
	
//	public Runner(Function func, FunctionDisposer disposer, boolean isDaemon)
	public Runner(Function func, boolean isDaemon)
	{
		// The constructor encapsulates the thread initialization task. It looks cleaner than the situation in which the procedure is exposed to developers. 08/04/2014, Bing Li  
		this.function = func;
		this.thread = new Thread(this.function);
		this.thread.setDaemon(isDaemon);
//		this.disposer = disposer;
	}

	/*
	 * Start the initialized thread. 08/04/2014, Bing Li
	 */
	public void start()
	{
		if (!this.thread.isAlive())
		{
			this.thread.start();
		}
	}

	/*
	 * Stop the thread and wait for its death for some time. 08/04/2014, Bing Li
	 */
	public void stop(long waitTime) throws InterruptedException
	{
//		this.disposer.dispose(this.function, waitTime);
//		If the line is added, the thread shutting-down is abnormal. The entire process cannot be shutdown properly. I need to check the method, join(), carefully AGAIN. 06/13/2019, Bing Li
//		this.thread.join(waitTime);
		this.function.dispose(waitTime);
	}

	/*
	 * Stop the thread by waiting for its death. 08/04/2014, Bing Li
	 */
	public void stop() throws InterruptedException
	{
//		this.disposer.dispose(this.function);
		/*
		 * If the line is added, the thread shutting-down is abnormal. The entire process cannot be shutdown properly. I need to check the method, join(), carefully AGAIN. 06/13/2019, Bing Li
		 */
//		this.thread.join();
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
		return this.thread.isAlive();
	}

	/*
	 * Interrupt the enclosed thread. 08/04/2014, Bing Li
	 */
	public void interrupt()
	{
		this.thread.interrupt();
	}

	@Override
	public int compareTo(Runner<Function> obj)
	{
		if (obj != null)
		{
			if (this.function.getWorkload() > obj.getFunction().getWorkload())
			{
				return 1;
			}
			else if (this.function.getWorkload() == obj.getFunction().getWorkload())
			{
				return 0;
			}
			else
			{
				return -1;
			}
		}
		else
		{
			return 1;
		}
	}
}
