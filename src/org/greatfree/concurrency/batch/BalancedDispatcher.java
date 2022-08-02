package org.greatfree.concurrency.batch;

import java.util.Calendar;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.greatfree.concurrency.IdleCheckable;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.ThreadIdleChecker;
import org.greatfree.util.Builder;
import org.greatfree.util.Rand;
import org.greatfree.util.Time;

// Created: 04/23/2018, Bing Li
// public class BalancedDispatcher<Task, Notifier extends ThreadNotifiable<Task>, Function extends BalancedQueue<Task, Notifier>, ThreadCreator extends BalancedThreadCreatable<Task, Notifier, Function>, ThreadDisposer extends RunnerDisposable<Function>> implements CheckIdleable
public class BalancedDispatcher<Task, Notifier extends ThreadNotifiable<Task>, Function extends BalancedQueue<Task, Notifier>, ThreadCreator extends BalancedThreadCreatable<Task, Notifier, Function>> implements IdleCheckable
{
	private final int fastPoolSize;
	private final int slowPoolSize;
	private final int threadQueueSize;
	private final long idleTime;
	private Notifier notifier;
//	private Map<String, Runner<Function, ThreadDisposer>> fastThreaders;
	private Map<String, Runner<Function>> fastThreaders;
//	private Map<String, Runner<Function, ThreadDisposer>> slowThreaders;
	private Map<String, Runner<Function>> slowThreaders;
	private ThreadCreator creator;
//	private ThreadDisposer disposer;
	private Sync collaborator;
	private ScheduledThreadPoolExecutor scheduler;
//	private ThreadIdleChecker<BalancedDispatcher<Task, Notifier, Function, ThreadCreator, ThreadDisposer>> idleChecker;
	private ThreadIdleChecker<BalancedDispatcher<Task, Notifier, Function, ThreadCreator>> idleChecker;
	private ScheduledFuture<?> idleCheckingTask;
	// Those Fast threads that encounter long crawling problems have to put into the queue if the Slow pool has no position to take them. They will be placed into the Slow pool when a new position is available. 10/26/2017, Bing Li
//	private Queue<Runner<Function, ThreadDisposer>> alleviatedThreaderQueue;
	private Queue<Runner<Function>> alleviatedThreaderQueue;

//	public BalancedDispatcher(BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator, ThreadDisposer> builder)
	public BalancedDispatcher(BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> builder)
	{
		this.fastPoolSize = builder.getFastPoolSize();
		this.slowPoolSize = builder.getSlowPoolSize();
		this.threadQueueSize = builder.getThreadQueueSize();
		this.idleTime = builder.getIdleTime();
		this.notifier = builder.getNotifier();
//		this.fastThreaders = new ConcurrentHashMap<String, Runner<Function, ThreadDisposer>>();
		this.fastThreaders = new ConcurrentHashMap<String, Runner<Function>>();
//		this.slowThreaders = new ConcurrentHashMap<String, Runner<Function, ThreadDisposer>>();
		this.slowThreaders = new ConcurrentHashMap<String, Runner<Function>>();
		this.creator = builder.getCreator();
//		this.disposer = builder.getDisposer();
		this.collaborator = new Sync(false);
		this.scheduler = builder.getScheduler();
//		this.idleChecker = new ThreadIdleChecker<BalancedDispatcher<Task, Notifier, Function, ThreadCreator, ThreadDisposer>>(this);
		this.idleChecker = new ThreadIdleChecker<BalancedDispatcher<Task, Notifier, Function, ThreadCreator>>(this);
//		this.alleviatedThreaderQueue = new LinkedBlockingQueue<Runner<Function, ThreadDisposer>>();
		this.alleviatedThreaderQueue = new LinkedBlockingQueue<Runner<Function>>();
	}

//	public static class BalanceDispatcherBuilder<Task, Notifier extends ThreadNotifiable<Task>, Function extends BalancedQueue<Task, Notifier>, ThreadCreator extends BalancedThreadCreatable<Task, Notifier, Function>, ThreadDisposer extends RunnerDisposable<Function>> implements Builder<BalancedDispatcher<Task, Notifier, Function, ThreadCreator, ThreadDisposer>>
	public static class BalanceDispatcherBuilder<Task, Notifier extends ThreadNotifiable<Task>, Function extends BalancedQueue<Task, Notifier>, ThreadCreator extends BalancedThreadCreatable<Task, Notifier, Function>> implements Builder<BalancedDispatcher<Task, Notifier, Function, ThreadCreator>>
	{
		private int fastPoolSize;
		private int slowPoolSize;
		private int threadQueueSize;
		private long idleTime;
		private Notifier notifier;
		private ThreadCreator creator;
//		private ThreadDisposer disposer;
		private ScheduledThreadPoolExecutor scheduler;
		
		public BalanceDispatcherBuilder()
		{
		}
		
		public BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> fastPoolSize(int fastPoolSize)
		{
			this.fastPoolSize = fastPoolSize;
			return this;
		}

		public BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> slowPoolSize(int slowPoolSize)
		{
			this.slowPoolSize = slowPoolSize;
			return this;
		}

		public BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> threadQueueSize(int threadQueueSize)
		{
			this.threadQueueSize = threadQueueSize;
			return this;
		}
		
		public BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> idleTime(long idleTime)
		{
			this.idleTime = idleTime;
			return this;
		}
		
		public BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> notifier(Notifier notifier)
		{
			this.notifier = notifier;
			return this;
		}
		
		public BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> creator(ThreadCreator creator)
		{
			this.creator = creator;
			return this;
		}

		public BalanceDispatcherBuilder<Task, Notifier, Function, ThreadCreator> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		
		@Override
		public BalancedDispatcher<Task, Notifier, Function, ThreadCreator> build()
		{
			return new BalancedDispatcher<Task, Notifier, Function, ThreadCreator>(this);
		}
		
		public int getFastPoolSize()
		{
			return this.fastPoolSize;
		}
		
		public int getSlowPoolSize()
		{
			return this.slowPoolSize;
		}
		
		public int getThreadQueueSize()
		{
			return this.threadQueueSize;
		}
		
		public long getIdleTime()
		{
			return this.idleTime;
		}
		
		public Notifier getNotifier()
		{
			return this.notifier;
		}
		
		public ThreadCreator getCreator()
		{
			return this.creator;
		}

		/*
		public ThreadDisposer getDisposer()
		{
			return this.disposer;
		}
		*/
		
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
	}

	public void dispose() throws InterruptedException
	{
		this.collaborator.shutdown();
		if (this.idleCheckingTask != null)
		{
			this.idleCheckingTask.cancel(true);
		}
//		for (Runner<Function, ThreadDisposer> threader : this.fastThreaders.values())
		for (Runner<Function> threader : this.fastThreaders.values())
		{
			threader.stop();
		}
//		for (Runner<Function, ThreadDisposer> threader : this.slowThreaders.values())
		for (Runner<Function> threader : this.slowThreaders.values())
		{
			threader.stop();
			// The threads take long time to die. So the deprecated method is used to force it to die. 03/13/2014, Bing Li
		}
		
//		Runner<Function, ThreadDisposer> threader;
		Runner<Function> threader;
		while (!this.alleviatedThreaderQueue.isEmpty())
		{
			threader = this.alleviatedThreaderQueue.poll();
			threader.stop();
		}
	}

	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}

	public void setPause() throws InterruptedException
	{
		this.collaborator.setPause();
	}
	
	public boolean isPaused()
	{
		return this.collaborator.isPaused();
	}

	public void holdOn(long time) throws InterruptedException
	{
		this.collaborator.holdOn(time);
	}

	public void keepOn()
	{
		this.collaborator.keepOn();
	}
	
	public Function getFastFunction(String key)
	{
		if (this.fastThreaders.containsKey(key))
		{
			return this.fastThreaders.get(key).getFunction();
		}
		return null;
	}

	public Function getSlowFunction(String key)
	{
		if (this.slowThreaders.containsKey(key))
		{
			return this.slowThreaders.get(key).getFunction();
		}
		return null;
	}
	
	public void kill(String key) throws InterruptedException
	{
		if (this.slowThreaders.containsKey(key))
		{
//			Runner<Function, ThreadDisposer> t = this.slowThreaders.get(key);
			Runner<Function> t = this.slowThreaders.get(key);
			t.stop();
			this.slowThreaders.remove(key);
		}
	}
	
	public void killAll() throws InterruptedException
	{
//		for (Map.Entry<String, Runner<Function, ThreadDisposer>> entry : this.fastThreaders.entrySet())
		for (Map.Entry<String, Runner<Function>> entry : this.fastThreaders.entrySet())
		{
			entry.getValue().stop();
		}
		this.fastThreaders.clear();
		
//		for (Map.Entry<String, Runner<Function, ThreadDisposer>> entry : this.slowThreaders.entrySet())
		for (Map.Entry<String, Runner<Function>> entry : this.slowThreaders.entrySet())
		{
			entry.getValue().stop();
		}
		this.slowThreaders.clear();
	}
	
	public boolean enqueue(Task task) throws InterruptedException
	{
		if (this.fastThreaders.size() < this.fastPoolSize)
		{
			if (this.fastThreaders.size() > 0)
			{
//				for (Runner<Function, ThreadDisposer> threader : this.fastThreaders.values())
				for (Runner<Function> threader : this.fastThreaders.values())
				{
					if (!threader.getFunction().isFull())
					{
						threader.getFunction().enqueue(task);
						return true;
					}
				}
			}
			
			Function thread = this.creator.createBalanceThreadInstance(this.threadQueueSize, this.notifier);
//			Runner<Function, ThreadDisposer> threader = new Runner<Function, ThreadDisposer>(thread, this.disposer);
//			Runner<Function> threader = new Runner<Function>(thread, this.disposer);
			Runner<Function> threader = new Runner<Function>(thread);
			threader.getFunction().enqueue(task);
			this.fastThreaders.put(thread.getKey(), threader);
			threader.start();
			return true;
		}
		else
		{
//			for (Runner<Function, ThreadDisposer> threader : this.fastThreaders.values())
			for (Runner<Function> threader : this.fastThreaders.values())
			{
				if (!threader.getFunction().isFull())
				{
					threader.getFunction().enqueue(task);
					return true;
				}
			}
			this.notifier.pause();

			// The task assignment of InteractiveDispatcher is not processed well. When a task is not assigned to one thread, it is ignored. I will consider improving that in the near future. 10/22/2017, Bing Li
			// One task, e.g., the hub to be crawled, is lost in the queue if it is NOT assigned to a thread such that it will never be crawled any longer. It is NOT correct. 10/22/2017, Bing Li
			return false;
		}
	}

	public Set<String> getFastThreadKeys()
	{
		return this.fastThreaders.keySet();
	}

	public Set<String> getSlowThreadKeys()
	{
		return this.slowThreaders.keySet();
	}

	public void alleviateSlow(String key) throws InterruptedException
	{
		if (this.fastThreaders.containsKey(key))
		{
//			Runner<Function, ThreadDisposer> threader = this.fastThreaders.get(key);
			Runner<Function> threader = this.fastThreaders.get(key);
			this.fastThreaders.remove(key);
			threader.getFunction().clear();
			if (this.slowThreaders.size() < this.slowPoolSize)
			{
				this.slowThreaders.put(key, threader);
			}
			else
			{
				// Should I do something here? 10/26/2017, Bing Li
				// Maybe put those long-crawling threads into a queue to wait for the slow pool? Then, more threads can keep fast crawling. 10/26/2017, Bing Li
				this.alleviatedThreaderQueue.add(threader);
			}
			this.notifier.keepOn();
		}
	}
	
	public void appendSlow() throws InterruptedException
	{
//		Runner<Function, ThreadDisposer> threader;
		Runner<Function> threader;
		while (!this.alleviatedThreaderQueue.isEmpty())
		{
			if (this.slowThreaders.size() < this.slowPoolSize)
			{
				threader = this.alleviatedThreaderQueue.poll();
				this.slowThreaders.put(threader.getFunction().getKey(), threader);
			}
			else
			{
				// The lines should be added. Otherwise, the dead loop is formed. 12/05/2017, Bing Li
				this.kill(Rand.getRandomStringInSet(this.slowThreaders.keySet()));
			}
		}
	}

	public void restoreFast(String key) throws InterruptedException
	{
		if (this.slowThreaders.containsKey(key))
		{
//			Runner<Function, ThreadDisposer> threader = this.slowThreaders.get(key);
			Runner<Function> threader = this.slowThreaders.get(key);
			this.slowThreaders.remove(key);
			this.fastThreaders.put(key, threader);
			this.notifier.keepOn();
		}
	}

	@Override
	public void checkIdle() throws InterruptedException
	{
//		for (Map.Entry<String, Runner<Function, ThreadDisposer>> threadEntry : this.fastThreaders.entrySet())
		for (Map.Entry<String, Runner<Function>> threadEntry : this.fastThreaders.entrySet())
		{
			if (threadEntry.getValue().getFunction().getIdleTime() != Time.INIT_TIME)
			{
				if (Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), threadEntry.getValue().getFunction().getIdleTime()) > this.idleTime)
				{
					try
					{
						threadEntry.getValue().stop();
						this.fastThreaders.remove(threadEntry.getKey());
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
//		for (Map.Entry<String, Runner<Function, ThreadDisposer>> threadEntry : this.slowThreaders.entrySet())
		for (Map.Entry<String, Runner<Function>> threadEntry : this.slowThreaders.entrySet())
		{
			if (threadEntry.getValue().getFunction().getIdleTime() != Time.INIT_TIME)
			{
				if (Time.getTimespanInMilliSecond(Calendar.getInstance().getTime(), threadEntry.getValue().getFunction().getIdleTime()) > this.idleTime)
				{
					try
					{
						threadEntry.getValue().stop();
						this.slowThreaders.remove(threadEntry.getKey());
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		if (this.idleCheckingTask == null)
		{
			this.idleCheckingTask = this.scheduler.scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
		}
	}

}
