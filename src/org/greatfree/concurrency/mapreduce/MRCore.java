package org.greatfree.concurrency.mapreduce;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.IdleCheckable;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadIdleChecker;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.HashFreeObject;
import org.greatfree.util.Time;

/*
 * The class is the primary component to perform the high performance task, Map/Reduce. It is actually a thread pool to manage those threads that take the role of Map/Reduce. 04/19/2018, Bing Li
 */

// Created: 04/19/2018, Bing Li
public class MRCore<Task extends Sequence, Result extends Sequence, TaskThread extends MapReduceQueue<Task, Result, TaskThread, ThreadCreator>, ThreadCreator extends MapReduceThreadCreatable<Task, Result, TaskThread, ThreadCreator>> extends HashFreeObject implements IdleCheckable, MapReducable<Result>
{
	// The threads that execute the high-volume tasks concurrently. 04/21/2018, Bing Li
	private Map<String, Runner<TaskThread>> threads;
	
	/*
	 * The thread pool can be replaced with runner/threader, i.e., I need to design the pool, MRCore, from scratch. 11/05/2019, Bing Li
	 */
	// The thread pool that executes and manages the threads. In essence, MRCore is a thread pool as well. However, MRCore provides more specific management for Map/Reduce. 04/21/2018, Bing Li
//	private ThreadPool threadPool;
	private final int poolSize;
	// The module to create the instance of threads that execute the tasks concurrently. 04/21/2018, Bing Li
	private ThreadCreator threadCreator;
	// The number of tasks each thread need to work on. 04/21/2018, Bing Li
	private int taskSizePerThread;
	// A scheduler to check the status of threads. Idle ones should be collected if detected. 04/21/2018, Bing Li
	private ScheduledThreadPoolExecutor scheduler;
	// The checker to detect whether the threads are busy or idle. 04/21/2018, Bing Li
	private ThreadIdleChecker<MRCore<Task, Result, TaskThread, ThreadCreator>> idleChecker;
	// The task to check the status of the threads. 04/21/2018, Bing Li
	private ScheduledFuture<?> idleCheckingTask;
	// The module to keep the synchronization between the threads and the MRCore, the Map/Reduce threads management system. 04/21/2018, Bing Li
	private Sync workCollaborator;
	// The queue that takes the one task's result temporarily before being merged together. 04/21/2018, Bing Li
	private Queue<Result> resultQueue;
	// During the procedure to merge the results from the threads, it needs to wait for some time since each thread executes its tasks with different rates. The value defines the time to wait. 04/21/2018, Bing Li
	private final long waitTime;
	// It is possible that some threads take long time to finish their tasks and get back their results. If it is not necessary to wait for ever, the exact time to wait is defined by the value. When the waiting time exceeds the value, the merging procedure is terminated. 04/21/2018, Bing Li
	private final long shutdownTime;
	// The lock to keep relevant operations atomic. 04/21/2018, Bing Li
	private Lock lock;
	// The variable is used to keep the latest time when one thread returns its result. It is used to calculate if the shutdownTime is exceeded. 04/18/2018, Bing Li
	private Date lastUpdateTime;
	
	private InterruptTask<Task, Result, TaskThread, ThreadCreator> interrupter;
	private Runner<InterruptTask<Task, Result, TaskThread, ThreadCreator>> interRunner;

	/*
	 * The constructor of MapCore. It is implemented in the Builder pattern to clarify the initialization code when the number of parameters is large. 04/21/2018, Bing Li
	 */
	public MRCore(MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> builder)
	{
//		this.threads = new ConcurrentHashMap<String, TaskThread>();
		this.poolSize = builder.getPoolSize();
		this.threads = new ConcurrentHashMap<String, Runner<TaskThread>>();
//		this.threadPool = builder.getThreadPool();
		this.threadCreator = builder.getThreadCreator();
		this.taskSizePerThread = builder.getTaskSizePerThread();
		this.scheduler = builder.getScheduler();
		this.idleChecker = new ThreadIdleChecker<MRCore<Task, Result, TaskThread, ThreadCreator>>(this);
		this.workCollaborator = new Sync(false);
		this.resultQueue = new LinkedBlockingQueue<Result>();
		this.waitTime = builder.getWaitTime();
		this.shutdownTime = builder.getShutdownTime();
		this.lock = new ReentrantLock();
		// The current time is assigned to the variable, lastUpdateTime. 04/21/2018, Bing Li
		this.lastUpdateTime = Calendar.getInstance().getTime();
		this.interrupter = new InterruptTask<Task, Result, TaskThread, ThreadCreator>(this);
		this.interRunner = new Runner<InterruptTask<Task, Result, TaskThread, ThreadCreator>>(this.interrupter);
		this.interRunner.start();
	}

	/*
	 * The Builder pattern to clarify the constructor code. 04/21/2018, Bing Li
	 */
	public static class MRCoreBuilder<Task extends Sequence, Result extends Sequence, TaskThread extends MapReduceQueue<Task, Result, TaskThread, ThreadCreator>, ThreadCreator extends MapReduceThreadCreatable<Task, Result, TaskThread, ThreadCreator>> implements Builder<MRCore<Task, Result, TaskThread, ThreadCreator>>
	{
		private int poolSize;
//		private ThreadPool threadPool;
		private ThreadCreator threadCreator;
		private int taskSizePerThread;
		private long waitTime;
		private long shutdownTime;
		private ScheduledThreadPoolExecutor scheduler;
		
		public MRCoreBuilder()
		{
		}

		/*
		public MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}
		*/

		public MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> poolSize(int poolSize)
		{
			this.poolSize = poolSize;
			return this;
		}

		public MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> threadCreator(ThreadCreator threadCreator)
		{
			this.threadCreator = threadCreator;
			return this;
		}
		
		public MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> taskSizePerThread(int taskSizePerThread)
		{
			this.taskSizePerThread = taskSizePerThread;
			return this;
		}
		
		public MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> waitTime(long waitTime)
		{
			this.waitTime = waitTime;
			return this;
		}
		
		public MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> shutdownTime(long shutdownTime)
		{
			this.shutdownTime = shutdownTime;
			return this;
		}
		
		public MRCoreBuilder<Task, Result, TaskThread, ThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}

		@Override
		public MRCore<Task, Result, TaskThread, ThreadCreator> build()
		{
			return new MRCore<Task, Result, TaskThread, ThreadCreator>(this);
		}

		/*
		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}
		*/
		
		public int getPoolSize()
		{
			return this.poolSize;
		}
		
		public ThreadCreator getThreadCreator()
		{
			return this.threadCreator;
		}
		
		public int getTaskSizePerThread()
		{
			return this.taskSizePerThread;
		}
		
		public long getWaitTime()
		{
			return this.waitTime;
		}
		
		public long getShutdownTime()
		{
			return this.shutdownTime;
		}
		
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
	}

	/*
	 * Dispose the MRCore. All of the resources should be collected in the method. 04/21/2018, Bing Li
	 */
	public synchronized void dispose() throws InterruptedException
	{
		// Notify all of the waiting threads to terminate the waiting for termination. 04/21/2018, Bing Li
		this.workCollaborator.shutdown();

		// Check whether the idle-checking procedure is started or not. 04/21/2018, Bing Li
		if (this.idleCheckingTask != null)
		{
			// If the idle-checking procedure is started, it should be terminated. 04/21/2018, Bing Li
			this.idleCheckingTask.cancel(true);
		}

		// Each alive thread should be disposed. 04/21/2018, Bing Li
//		for (TaskThread thread : this.threads.values())
		for (Runner<TaskThread> thread : this.threads.values())
		{
			if (!thread.getFunction().isHung())
			{
//				thread.dispose();
				thread.stop();
			}
			else
			{
				System.out.println("MRCore-dispose(): interrupting hung MR threads...");
				thread.interrupt();
			}
//			System.out.println("MRCore-dispose(): isInterrupted " + thread.getFunction().isInterrupted());
		}
		// After all of the alive threads are disposed, it is necessary to clear the map that retain them. 04/21/2018, Bing Li
		this.threads.clear();
		// Nullify the thread creator. 04/21/2018, Bing Li
		this.threadCreator = null;
		this.interRunner.stop();
	}

	/*
	 * To reuse the MapCore, it is necessary to dispose alive threads and clear the map to prepare for the new Map/Reduce task. 04/21/2018, Bing Li
	 */
	@Override
	public void reset() throws InterruptedException
	{
		// Dispose each alive thread. 04/21/2018, Bing Li
//		for (TaskThread thread : this.threads.values())
		for (Runner<TaskThread> thread : this.threads.values())
		{
			if (thread.getFunction().isHung())
			{
//				thread.dispose();
//				thread.stop();
				thread.interrupt();
			}
		}
		// Clear the map that retains the alive threads. 04/21/2018, Bing Li
//		this.threads.clear();
	}

	/*
	 * Merge one result. The result is put into the resultQueue first. Then, merge all of the result into a map with an integer to represent each of them. 04/21/2018, Bing Li
	 */
	@Override
	public void reduce(Result t)
	{
		// Set the latest updated time. 04/21/2018, Bing Li
		this.setLastUpdateTime();
		// Enqueue the result into the resultQueue. 04/21/2018, Bing Li
		this.resultQueue.add(t);
		// Notify the waiting merge threads to combine the new available result. 04/21/2018, Bing Li
		this.workCollaborator.signalAll();
	}

	/*
	 * Check the idle state of all of the alive threads which work on the tasks. 04/21/2018, Bing Li 
	 */
	@Override
	public void checkIdle() throws InterruptedException
	{
		// Detect each alive thread which works on the tasks. 04/21/2018, Bing Li
//		for (TaskThread thread : this.threads.values())
		for (Runner<TaskThread> thread : this.threads.values())
		{
			// Check whether the thread is idle. 04/21/2018, Bing Li
			if (thread.getFunction().isIdle())
			{
				// If one thread is idle, it needs to be removed from the map. 04/21/2018, Bing Li
				this.threads.remove(thread.getFunction().getKey());
				// Dispose the thread. 04/21/2018, Bing Li
//				thread.dispose();
				thread.stop();
				// Nullify the thread. 04/21/2018, Bing Li
				thread = null;
			}
			else if (thread.getFunction().isHung())
			{
				this.threads.remove(thread.getFunction().getKey());
//				thread.getFunction().interrupt();
				thread.interrupt();
				thread = null;
			}
		}
	}

	/*
	 * Initialize the idle-checking task to check the idle states of those alive threads which work on the tasks. 04/21/2018, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		// Check whether the task is null or not. 04/21/2018, Bing Li
		if (this.idleCheckingTask != null)
		{
			// Initialize the idle-checking task. 04/21/2018, Bing Li
			this.idleCheckingTask = this.scheduler.scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
		}
	}

	/*
	 * Distribute all of the separated tasks to threads for concurrent execution and wait until the results of those are available. 04/21/2018, Bing Li
	 */
	public synchronized Map<Integer, Result> map(Map<Integer, Task> tasks) throws InterruptedException
	{
		// Notify the previous waiting thread to start a new Map/Reduce procedure. 04/21/2018, Bing Li
		this.workCollaborator.signalAll();
		// Create a map to take those results. 04/21/2018, Bing Li
		Map<Integer, Result> results = new HashMap<Integer, Result>();

		// The key of the thread that works on the smallest number of tasks. 04/21/2018, Bing Li
		String selectedThreadKey;
		// Assign all of the tasks to the threads in an even manner. 04/21/2018, Bing Li
		for (int i = 0; i < tasks.size(); i++)
		{
			// Check whether alive threads are available. 04/21/2018, Bing Li
			if (this.threads.size() > 0)
			{
				// Select the key of the thread that works on the smallest number of tasks. 04/21/2018, Bing Li
				selectedThreadKey = CollectionSorter.minValueKey(this.threads);
				// Check whether the selected key is valid or not. 04/21/2018, Bing Li
				if (selectedThreadKey != null)
				{
					// Check whether the key is existed. 04/21/2018, Bing Li
					if (this.threads.containsKey(selectedThreadKey))
					{
						// Check whether the selected thread's task queue is full or not. 04/21/2018, Bing Li
						if (this.threads.get(selectedThreadKey).getFunction().isFull())
						{
							// Because the selected thread's task queue is full, a new thread needs to be created if possible. It needs to check whether the number of alive threads exceeds the maximum size allowed by the thread pool. 04/21/2018, Bing Li
//							if (this.threads.size() < this.threadPool.getCorePoolSize())
							if (this.threads.size() < this.poolSize)
							{
								// If the thread pool allows more threads, a new thread is created. 04/21/2018, Bing Li
								TaskThread thread = this.threadCreator.createThreadInstance(this.taskSizePerThread, this);
								thread.enqueue(tasks.get(i));
								Runner<TaskThread> runner = new Runner<TaskThread>(thread);
								runner.start();
								// Place the newly created thread into the map. 04/21/2018, Bing Li
//								this.threads.put(thread.getKey(), thread);
								this.threads.put(thread.getKey(), runner);
								// Enqueue the current task into the newly created thread. 04/21/2018, Bing Li
//								this.threads.get(thread.getKey()).enqueue(tasks.get(i));
								// Execute the newly created thread with the thread pool. 04/21/201, Bing Li
//								this.threadPool.execute(this.threads.get(thread.getKey()));
							}
							else
							{
								// Enqueue the current task into the selected thread even though its task queue is full.  04/21/2018, Bing Li
								this.threads.get(selectedThreadKey).getFunction().enqueue(tasks.get(i));
							}
						}
						else
						{
							// Enqueue the current task into the selected thread if its task queue is not full. 04/21/2018, Bing Li
							this.threads.get(selectedThreadKey).getFunction().enqueue(tasks.get(i));
						}
					}
				}
			}
			else
			{
				// If no alive threads are available, a new thread needs to be created. 04/21/2018, Bing Li
				TaskThread thread = this.threadCreator.createThreadInstance(this.taskSizePerThread, this);
				thread.enqueue(tasks.get(i));
				Runner<TaskThread> runner = new Runner<TaskThread>(thread);
				runner.start();
				// Place the newly created thread into the map for management. 04/21/2018, Bing Li
				this.threads.put(thread.getKey(), runner);
				// Enqueue the current task into the task queue of the newly created thread. 04/21/2018, Bing Li
//				this.threads.get(thread.getKey()).enqueue(tasks.get(i));
				// Execute the newly created thread. 04/21/2018, Bing Li
//				this.threadPool.execute(this.threads.get(thread.getKey()));
			}
		}

		// Declare an instance of Result. 04/21/2018, Bing Li
		Result result;
		do
		{
			// Check whether is the queue to keep the result is empty or not. 04/21/2018, Bing Li
			while (!this.resultQueue.isEmpty())
			{
				// Dequeue one result from the result queue. 04/21/2018, Bing Li
				result = this.resultQueue.poll();
				// Put the result into the final map for merging. The key of the map is Integer that represents the task. 04/21/2018, Bing Li
				results.put(result.getSequence(), result);
			}
			// Check whether the size of the results reaches that of the tasks. 04/21/2018, Bing Li
			if (results.size() >= tasks.size())
			{
				// If the size of the results reaches that of the tasks, it indicates that all of the results are obtained. It is time to return the merged results. 04/21/2018, Bing Li
				break;
			}
			else
			{
				// If the size of the results is smaller than that of the tasks, wait for some time. 04/21/2018, Bing Li
				this.workCollaborator.holdOn(this.waitTime);
				// Check whether it has waited long enough through comparing with the predefined shutting-down time. 04/21/2018, Bing Li
				if (Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), this.getLastUpdateTime()) >= this.shutdownTime)
				{
					// If it has waited for long enough, it does not need to wait any longer. 04/21/2018, Bing Li
					break;
				}
			}
		}
		// Check whether all of the results are obtained. 04/21/2018, Bing Li
		while (results.size() < tasks.size());
		// Interrupt the hung threads. 11/08/2019, Bing Li
		this.interrupter.interrupt();
		// Clear the result queue to save space. 04/21/2018, Bing Li
		this.resultQueue.clear();
		// Notify all of the waiting for merging threads for termination. 04/21/2018, Bing Li
		this.workCollaborator.signalAll();
		// Return the results. 04/21/2018, Bing Li
		return results;
	}

	/*
	 * Set the latest time to receive the result of a task. 04/21/2018, Bing Li
	 */
	private void setLastUpdateTime()
	{
		this.lock.lock();
		this.lastUpdateTime = Calendar.getInstance().getTime();
		this.lock.unlock();
	}
	
	/*
	 * Get the latest time to receive the result of a task. 04/21/2018, Bing Li
	 */
	private Date getLastUpdateTime()
	{
		this.lock.lock();
		try
		{
			return this.lastUpdateTime;
		}
		finally
		{
			this.lock.unlock();
		}
	}
}
