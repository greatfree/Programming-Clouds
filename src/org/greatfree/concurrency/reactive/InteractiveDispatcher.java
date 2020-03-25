package org.greatfree.concurrency.reactive;

import java.util.Calendar;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.concurrency.CheckIdleable;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadIdleChecker;
import org.greatfree.reuse.RunnerDisposable;
import org.greatfree.util.Time;
import org.greatfree.util.UtilConfig;

/*
 * This is a more complicated thread. Besides the feature of NotificationObjectQueue, it is able to interact with its thread pool, i.e., InteractiveDispatcher, to notify its current status and even make a decision based on its status. In contrast, the thread pool can determine how to schedule tasks to those threads and how to maintain the life cycle of those threads in accordance with the status notifications from the thread. It is made use of the case when the work load is high and each of tasks can hardly be evaluated heavy or light prior to being executed. Thus, running status has to be notified by the thread itself after working on the load for some time. 05/24/2017, Bing Li
 */

/*
 * This is a task dispatcher which schedules tasks to the special type of threads derived from InteractiveQueue. For the distinct designs, instances of managed threads can interact with the dispatcher for high quality management. 11/21/2014, Bing Li
 * 
 * Different from classic dispatchers, the interactive pool splits the threads into two types, the fast one and the slow one. However, the splitting is performed upon interactions between the dispatcher and those threads. 11/21/2014, Bing Li
 * 
 */

// Created: 11/20/2014, Bing Li
public class InteractiveDispatcher<Task, Notifier extends Interactable<Task>, Function extends InteractiveQueue<Task, Notifier>, ThreadCreator extends InteractiveThreadCreatable<Task, Notifier, Function>, ThreadDisposer extends RunnerDisposable<Function>> implements CheckIdleable
{
	// The size of the fast thread pool. 11/21/2014, Bing Li
	private int fastPoolSize;
	// The size of the slow thread pool. 11/21/2014, Bing Li
	private int slowPoolSize;
	// When creating a new thread, it is required to specify the queue size for the thread. This is the argument. 11/21/2014, Bing Li
	private int threadQueueSize;
	// The maximum idle time for a thread. 11/21/2014, Bing Li
	private long idleTime;
	// The notifier which defines the methods, which are called back to notify the pool. 11/21/2014, Bing Li
	private Notifier notifier;
	// The map to contain fast threads. 11/21/2014, Bing Li
//	private Map<String, Runner<Function, ThreadDisposer>> fastThreaderMap;
	private Map<String, Runner<Function>> fastThreaderMap;
	// The map to contain slow threads. 11/21/2014, Bing Li
//	private Map<String, Runner<Function, ThreadDisposer>> slowThreaderMap;
	private Map<String, Runner<Function>> slowThreaderMap;
	// The creator to create instances of threads. 11/21/2014, Bing Li
	private ThreadCreator creator;
	// The disposer to dispose instances of threads. 11/21/2014, Bing Li
//	private ThreadDisposer disposer;
	// The collaborator is in essence controlled by managed threads through callback. The pool's working steps are administered by those threads of the pool. 11/21/2014, Bing Li
	private Sync collaborator;
	// Declare a timer that controls the task of idle checking. 11/21/2014, Bing Li
	private Timer checkTimer;
	// Declare the checker to check whether created threads are idle long enough. 11/21/2014, Bing Li
	private ThreadIdleChecker<InteractiveDispatcher<Task, Notifier, Function, ThreadCreator, ThreadDisposer>> idleChecker;

	/*
	 * Initialize the pool. 11/21/2014, Bing Li
	 */
	public InteractiveDispatcher(int fastPoolSize, int slowPoolSize, int threadQueueSize, long idleTime, Notifier notifier, ThreadCreator creator, ThreadDisposer disposer)
	{
		this.fastPoolSize = fastPoolSize;
		this.slowPoolSize = slowPoolSize;
		this.threadQueueSize = threadQueueSize;
		this.idleTime = idleTime;
		this.notifier = notifier;
//		this.fastThreaderMap = new ConcurrentHashMap<String, Runner<Function, ThreadDisposer>>();
		this.fastThreaderMap = new ConcurrentHashMap<String, Runner<Function>>();
//		this.slowThreaderMap = new ConcurrentHashMap<String, Runner<Function, ThreadDisposer>>();
		this.slowThreaderMap = new ConcurrentHashMap<String, Runner<Function>>();
		this.creator = creator;
//		this.disposer = disposer;
		this.collaborator = new Sync();
		this.checkTimer = UtilConfig.NO_TIMER;
	}

	/*
	 * Dispose the pool. 11/21/2014, Bing Li
	 */
	public void dispose() throws InterruptedException
	{
		/*
		// Set the flag of the collaborator. 11/21/2014, Bing Li
		this.collaborator.setShutdown();
		// Signal blocked threads to advance. 11/21/2014, Bing li
		this.collaborator.signalAll();
		*/
		
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Stop the fast threads. 11/21/2014, Bing Li
//		for (Runner<Function, ThreadDisposer> threader : this.fastThreaderMap.values())
		for (Runner<Function> threader : this.fastThreaderMap.values())
		{
			threader.stop();
		}
		// Stop the slow threads. 11/21/2014, Bing Li
//		for (Runner<Function, ThreadDisposer> threader : this.slowThreaderMap.values())
		for (Runner<Function> threader : this.slowThreaderMap.values())
		{
			threader.stop();
		}
		// Cancel the timer that controls the idle checking. 11/21/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
		// Terminate the periodically running thread for idle checking. 11/21/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
	}

	/*
	 * Check whether the pool is shutdown or not. 11/21/2014, Bing Li
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}

	/*
	 * Pause the pool. 11/21/2014, Bing Li
	 */
	public void pause() throws InterruptedException
	{
		this.collaborator.holdOn();
	}

	/*
	 * Check whether the pool is paused or not. 11/21/2014, Bing Li
	 */
	public boolean isPaused()
	{
		return this.collaborator.isPaused();
	}

	/*
	 * Demand the pool to wait. 11/21/2014, Bing Li
	 */
	public void holdOn() throws InterruptedException
	{
		this.collaborator.holdOn();
	}

	/*
	 * Demand the pool to keep on working. 11/21/2014, Bing Li
	 */
	public void keepOn()
	{
		this.collaborator.keepOn();
	}

	/*
	 * Get the fast thread by its key. 11/21/2014, Bing Li
	 */
	public Function getFastFunction(String key)
	{
		// Check whether the fast thread is contained in the fast thread map. 11/21/2014, Bing Li
		if (this.fastThreaderMap.containsKey(key))
		{
			// Return the fast thread. 11/21/2014, Bing Li
			return this.fastThreaderMap.get(key).getFunction();
		}
		// Return null if the key is not contained in the fast thread map. 11/21/2014, Bing Li
		return null;
	}

	/*
	 * Get the slow thread by its key. 11/21/2014, Bing Li
	 */
	public Function getSlowFunction(String key)
	{
		// Check whether the slow thread is contained in the fast thread map. 11/21/2014, Bing Li
		if (this.slowThreaderMap.containsKey(key))
		{
			// Return the slow thread. 11/21/2014, Bing Li
			return this.slowThreaderMap.get(key).getFunction();
		}
		// Return null if the key is not contained in the slow thread map. 11/21/2014, Bing Li
		return null;
	}

	/*
	 * Enqueue a new task into the pool for scheduling to a proper thread. 11/21/2014, Bing Li
	 */
	public void enqueue(Task task)
	{
		// The scheduling procedure must be finished until the task is scheduled to a thread. 11/21/2014, Bing Li
		while (true)
		{
			// Check whether the count of fast threads is less than the maximum size. 11/21/2014, Bing Li
			if (this.fastThreaderMap.size() < this.fastPoolSize)
			{
				// If the count of fast threads does not exceed the upper limit, check whether the count is greater than zero. 11/21/2014, Bing Li
				if (this.fastThreaderMap.size() > 0)
				{
					// Scan each fast thread and schedule the task to the one whose tasks are not full. 11/21/2014, Bing Li
//					for (Runner<Function, ThreadDisposer> threader : this.fastThreaderMap.values())
					for (Runner<Function> threader : this.fastThreaderMap.values())
					{
						// Check whether the load of a fast thread is full. 11/21/2014, Bing Li
						if (!threader.getFunction().isFull())
						{
							// Enqueue the task to the fast thread. 11/21/2014, Bing Li
							threader.getFunction().enqueue(task);
							// End the scheduling. 11/21/2014, Bing Li
							return;
						}
					}
				}

				// If no fast threads are available or each existing fast threads are full, it is required to create a new thread. 11/21/2014, Bing Li
				Function thread = this.creator.createInteractiveThreadInstance(this.threadQueueSize, this.notifier);
				// Wrap the new created thread with threader. 11/21/2014, Bing Li
//				Runner<Function, ThreadDisposer> threader = new Runner<Function, ThreadDisposer>(thread, this.disposer);
				Runner<Function> threader = new Runner<Function>(thread);
				// Enqueue the task into the thread. 11/21/2014, Bing Li
				threader.getFunction().enqueue(task);
				// Put the threader into the fast thread pool. 11/21/2014, Bing Li
				this.fastThreaderMap.put(thread.getKey(), threader);
				// Start the threader. 11/21/2014, Bing Li
				threader.start();
				return;
			}
			else
			{
				// If the count of fast threads exceeds the upper limit, it is required to check whether one of them is not full. If so, schedule the task to it. 11/21/2014, Bing Li
//				for (Runner<Function, ThreadDisposer> threader : this.fastThreaderMap.values())
				for (Runner<Function> threader : this.fastThreaderMap.values())
				{
					// Check whether one thread is full or not. 11/21/2014, Bing Li
					if (!threader.getFunction().isFull())
					{
						// Schedule the task to the thread. 11/21/2014, Bing Li
						threader.getFunction().enqueue(task);
						// End the scheduling. 11/21/2014, Bing Li
						return;
					}
				}
				
				// If the scheduling is not completed in the above steps, it denotes that threads are too busy. Thus, it is necessary to notify the relevant module to pause putting new tasks into the dispatcher temporarily. In addition, the pausing keeps until the existing tasks are done. The scheduling procedure must be waken up by relevant threads which finish the tasks. 11/21/2014, Bing Li 
				this.notifier.pause();
			}
		}
	}

	/*
	 * Get the fast thread keys. 11/21/2014, Bing Li
	 */
	public Set<String> getFastThreadKeys()
	{
		return this.fastThreaderMap.keySet();
	}

	/*
	 * Get the slow thread keys. 11/21/2014, Bing Li
	 */
	public Set<String> getSlowThreadKeys()
	{
		return this.slowThreaderMap.keySet();
	}

	/*
	 * Once if a fast thread becomes slow, it is required to put the thread into the slow pool. A fast becomes slow when the task it is working on is heavy. For instance, crawling a URL might take much time for network problems. 11/21/2014, Bing Li
	 */
	public void alleviateSlow(String key) throws InterruptedException
	{
		// Check whether the fast pool contains the slow thread key. 11/21/2014, Bing Li
		if (this.fastThreaderMap.containsKey(key))
		{
			// Check whether the count of slow threads is less than the upper limit. 11/21/2014, Bing Li
			if (this.slowThreaderMap.size() < this.slowPoolSize)
			{
				// If the slow thread pool is still not full, it is time to get the new slow threader out from the fast thread pool. 11/21/2014, Bing Li
//				Runner<Function, ThreadDisposer> threader = this.fastThreaderMap.get(key);
				Runner<Function> threader = this.fastThreaderMap.get(key);
				// Remove the new slow threader from the fast thread pool. 11/21/2014, Bing Li
				this.fastThreaderMap.remove(key);
				// Clear the waiting tasks in the new slow threader. It is necessary to indicate that the cleared tasks are not taken care by the dispatcher. Additional mechanisms should be implemented outside. For better solution, it must be revised. 11/21/2014, Bing Li
				threader.getFunction().clear();
				// Put the new slow threader into the slow thread pool. 11/21/2014, Bing Li
				this.slowThreaderMap.put(key, threader);
				// Since one fast thread is removed from the fast thread pool, it is necessary to notify the scheduling procedure that might be blocked because the current size of the fast thread pool exceeds the upper limit. 11/21/2014, Bing Li
				this.notifier.keepOn();
			}
			
			// If the size of the slow thread pool exceeds its upper limit, the new slow thread has to stay in the fast thread pool in the version. It is suggested to revise here to accommodate the problem. 11/21/2014, Bing Li
		}
	}

	/*
	 * Once if a slow thread is identified as a fast one, it is time to put it into the fast thread pool. It usually happens the slow thread has no tasks to do further. 11/21/2014, Bing Li
	 */
	public void restoreFast(String key) throws InterruptedException
	{
		// Check whether the slow thread pool contains the thread key. 11/21/2014, Bing Li
		if (this.slowThreaderMap.containsKey(key))
		{
			// Get the threader from the slow thread pool. 11/21/2014, Bing Li
//			Runner<Function, ThreadDisposer> threader = this.slowThreaderMap.get(key);
			Runner<Function> threader = this.slowThreaderMap.get(key);
			// Remove the threader from the slow thread pool. 11/21/2014, Bing Li
			this.slowThreaderMap.remove(key);
			// Put the new fast thread into the fast thread pool. 11/21/2014, Bing Li
			this.fastThreaderMap.put(key, threader);
			// Since one fast thread is added to the fast thread pool, it is necessary to notify the scheduling procedure that might be blocked because no empty or low load fast threads are available. 11/21/2014, Bing Li
			this.notifier.keepOn();
		}
	}

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of threads within the dispatcher. 11/21/2014, Bing Li
	 */
	@Override
	public void checkIdle()
	{
		// Check each thread in the fast thread pool. 11/21/2014, Bing Li
//		for (Map.Entry<String, Runner<Function, ThreadDisposer>> threadEntry : this.fastThreaderMap.entrySet())
		for (Map.Entry<String, Runner<Function>> threadEntry : this.fastThreaderMap.entrySet())
		{
			// Check whether the fast thread is idle. 11/21/2014, Bing Li
			if (threadEntry.getValue().getFunction().getIdleTime() != Time.INIT_TIME)
			{
				// Check whether the thread is idle long enough. 11/21/2014, Bing Li
				if (Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), threadEntry.getValue().getFunction().getEndTime()) > this.idleTime)
				{
					try
					{
						// Stop the thread that is idle long enough. 11/21/2014, Bing Li
						threadEntry.getValue().stop();
						// Remove the dead thread from the fast thread pool. 11/21/2014, Bing Li
						this.fastThreaderMap.remove(threadEntry.getKey());
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		// Since the threads in the slow thread pool are busy working, it is unreasonable to check them. 11/21/2014, Bing Li
	}

	/*
	 * Set the idle checking parameters. 11/21/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		// Initialize the timer. 11/21/2014, Bing Li
		this.checkTimer = new Timer();
		// Initialize the idle checker. 11/21/2014, Bing Li
		this.idleChecker = new ThreadIdleChecker<InteractiveDispatcher<Task, Notifier, Function, ThreadCreator, ThreadDisposer>>(this);
		// Schedule the idle checking task. 11/21/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
	}
}
