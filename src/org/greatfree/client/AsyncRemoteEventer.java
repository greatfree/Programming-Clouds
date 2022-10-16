package org.greatfree.client;

import java.io.IOException;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.IdleCheckable;
import org.greatfree.concurrency.Runner;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Builder;
import org.greatfree.util.CollectionSorter;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Rand;
import org.greatfree.util.UtilConfig;

/*
 * This is an asynchronous eventer manager, which aims to initialize and maintain eventers that send notifications to a remote server asynchronously without waiting for responses. The sending methods are nonblocking. 11/20/2014, Bing Li
 */

// Created: 11/20/2014, Bing Li
public class AsyncRemoteEventer<Notification extends ServerMessage> extends Thread implements IdleCheckable
{
//	private final static Logger log = Logger.getLogger("org.greatfree.client");
	
	// The eventers that are available to sent the notifications concurrently. They are indexed by the keys which are usually generated upon their IP/ports to be sent to. 11/20/2014, Bing Li
//	private Map<String, Eventer<Notification>> eventers;
	private Map<String, Runner<Eventer<Notification>>> eventers;
	// The queue which contains the notification to be sent. The notification contains the IP/port to be sent and the message to be sent. 11/20/2014, Bing Li
	private Queue<IPNotification<Notification>> notificationQueue;
	// The thread pool that starts and manages the eventer concurrently. It must be shared with others. 11/20/2014, Bing Li
//	private ThreadPool threadPool;
	// The size of the event queue for each eventer. 11/20/2014, Bing Li
	private final int eventQueueSize;
	// The count of eventers to be managed. 11/20/2014, Bing Li
	private final int eventerSize;
	// The timer to manage the idle checker. 11/20/2014, Bing Li
//	private Timer checkTimer;
	// The timer is replaced because its tasks cannot be canceled. The self-disposing mechanism needs a re-submission approach to do that. Thus, the ScheduledThreadPoolExecutor is selected. 02/01/2016, Bing Li
//	private ScheduledThreadPoolExecutor scheduler;
	// The ScheduledFuture is used to cancel the scheduled task when disposing the dispatcher to save resources. 02/01/2016, Bing Li
	private ScheduledFuture<?> idleCheckingTask;
	// The idle checker to monitor whether an eventer is idle long enough. 11/20/2014, Bing Li
	private IdleChecker<AsyncRemoteEventer<Notification>> idleChecker;
	// The collaborator is used to pause the dispatcher when no notifications are available and notify to continue when new notifications are received. 11/20/2014, Bing Li
	private Sync collaborator;
	// The time to be waited when no notifications are available in the class. 11/20/2014, Bing Li
	private final long eventingWaitTime;
	// The FreeClientPool that is used to initialize eventers. It must be shared with others. 11/20/2014, Bing Li
	private FreeClientPool clientPool;
	// The time to be waited when no notifications are available in each eventer. 11/20/2014, Bing Li
	private final long eventerQueueWaitTime;
	// The delay time before a periodical idle-checking is started. 01/20/2016, Bing Li
	private final long idleCheckDelay;
	// The idle-checking period. 01/20/2016, Bing Li
	private final long idleCheckPeriod;
	/*
	 * I decide to remove the attribute since the waiting time is enough. In addition, it is not proper to dispose itself because the thread pool can handle that. 06/09/2022, Bing Li
	 */
	// When the eventer has no notifications to send, it has to wait. But if the waiting time is long enough, it needs to be disposed. The waitRound defines the count of outside-most loop in the run(). Because of it, the eventer has killed after no notifications are available for sufficient time. 01/20/2016, Bing Li 
//	private final int waitRound;
	// When self-disposing the dispatcher to save resources, it needs to keep synchronization between the message queue and the disposing. The lock is responsible for that. 02/01/2016, Bing Li
	private ReentrantLock monitorLock;
	
	private ScheduledThreadPoolExecutor scheduler;
	private final long shutdownTimeout;
	
//	private AtomicBoolean isSharedScheduler;

	/*
	 * Initialize. 11/20/2014, Bing Li
	 */
//	public AsyncRemoteEventer(FreeClientPool clientPool, ThreadPool threadPool, int eventQueueSize, int eventerSize, long eventingWaitTime, long eventerWaitTime, int waitRound, long idleCheckDelay, long idleCheckPeriod, ScheduledThreadPoolExecutor scheduler)
//	public AsyncRemoteEventer(FreeClientPool clientPool, ThreadPool threadPool, int eventQueueSize, int eventerSize, long eventingWaitTime, long eventerWaitTime, int waitRound, long idleCheckDelay, long idleCheckPeriod, int schedulerPoolSize, long schedulerKeepAliveTime)
	/*
	public AsyncRemoteEventer(FreeClientPool clientPool, int eventQueueSize, int eventerSize, long eventingWaitTime, long eventerWaitTime, int waitRound, long idleCheckDelay, long idleCheckPeriod, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		this.eventers = new ConcurrentHashMap<String, Runner<Eventer<Notification>>>();
		this.notificationQueue = new LinkedBlockingQueue<IPNotification<Notification>>();
//		this.threadPool = threadPool;
		this.eventQueueSize = eventQueueSize;
		this.eventerSize = eventerSize;
//		this.checkTimer = UtilConfig.NO_TIMER;
//		this.scheduler = scheduler;
		
		// The scheduler is shared. It is not proper to set the parameters inside one eventer. And, when the scheduler is initialized, it will not be initialized again. In addition, it is not shutdown? 07/31/2018, Bing Li
		Scheduler.GREATFREE().init(schedulerPoolSize, schedulerKeepAliveTime);
		
		// Set the pool size. 02/01/2016, Bing Li

		this.idleChecker = new IdleChecker<AsyncRemoteEventer<Notification>>(this);
		this.collaborator = new Sync(true);
		this.eventingWaitTime = eventingWaitTime;
		this.clientPool = clientPool;
		this.eventerWaitTime = eventerWaitTime;
		this.waitRound = waitRound;
		this.idleCheckDelay = idleCheckDelay;
		this.idleCheckPeriod = idleCheckPeriod;
		this.monitorLock = new ReentrantLock();
	}
	*/
	
	public AsyncRemoteEventer(AsyncRemoteEventerBuilder<Notification> builder)
	{
		this.eventers = new ConcurrentHashMap<String, Runner<Eventer<Notification>>>();
		this.notificationQueue = new LinkedBlockingQueue<IPNotification<Notification>>();
//		this.threadPool = builder.getThreadPool();
		this.eventQueueSize = builder.getEventQueueSize();
		this.eventerSize = builder.getEventerSize();
//		this.scheduler = builder.getScheduler();

//		this.isSharedScheduler = new AtomicBoolean();
		// I decide to assign the asynchronous eventer has a scheduler belonged to itself. It is not a heavy burden.er classes. 03/20/2019, Bing Li
		/*
		if (builder.getScheduler() != null)
		{
//			System.out.println("Scheduler is NOT null");
			this.scheduler = builder.getScheduler();
//			this.isSharedScheduler.set(true);
		}
		else
		{
//			System.out.println("Scheduler is null");
			Scheduler.GREATFREE().init(builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime());
			this.scheduler = Scheduler.GREATFREE().getScheduler();
//			this.isSharedScheduler.set(false);
		}
		*/

		// I decide to assign the asynchronous eventer has a scheduler belonged to itself. It is not a heavy burden.er classes. 03/20/2019, Bing Li
		this.scheduler = new ScheduledThreadPoolExecutor(builder.getSchedulerPoolSize());
		
//		log.info("builder.getSchedulerKeepAliveTime() = " + builder.getSchedulerKeepAliveTime());
		// The the lasted time to keep a thread alive. 02/01/2016, Bing Li
		this.scheduler.setKeepAliveTime(builder.getSchedulerKeepAliveTime(), TimeUnit.MILLISECONDS);
		// Set the core thread's timeout. When no tasks are available the relevant threads need to be collected and killed. 02/01/2016, Bing Li
		this.scheduler.allowCoreThreadTimeOut(true);

		/*
		// Set the pool size. 02/01/2016, Bing Li
		this.scheduler = new ScheduledThreadPoolExecutor(builder.getSchedulerPoolSize());
		// The the lasted time to keep a thread alive. 02/01/2016, Bing Li
		this.scheduler.setKeepAliveTime(builder.getSchedulerKeepAliveTime(), TimeUnit.MILLISECONDS);
		// Set the core thread's timeout. When no tasks are available the relevant threads need to be collected and killed. 02/01/2016, Bing Li
		this.scheduler.allowCoreThreadTimeOut(true);
		*/
		
		this.idleChecker = new IdleChecker<AsyncRemoteEventer<Notification>>(this);
		this.collaborator = new Sync(true);
		this.eventingWaitTime = builder.getEventingWaitTime();
		this.clientPool = builder.getClientPool();
		this.eventerQueueWaitTime = builder.getEventQueueWaitTime();
//		this.waitRound = builder.getWaitRound();
		this.idleCheckDelay = builder.getIdleCheckDelay();
		this.idleCheckPeriod = builder.getIdleCheckPeriod();
		this.monitorLock = new ReentrantLock();
		this.shutdownTimeout = builder.getSchedulerShutdownTimeout();
	}
	
	public static class AsyncRemoteEventerBuilder<Notification extends ServerMessage> implements Builder<AsyncRemoteEventer<Notification>>
	{
		// The thread pool that starts and manages the eventer concurrently. It must be shared with others. 11/20/2014, Bing Li
//		private ThreadPool threadPool;
		// The size of the event queue for each eventer. 11/20/2014, Bing Li
		private int eventQueueSize;
		// The count of eventers to be managed. 11/20/2014, Bing Li
		private int eventerSize;
		// The timer is replaced because its tasks cannot be canceled. The self-disposing mechanism needs a re-submission approach to do that. Thus, the ScheduledThreadPoolExecutor is selected. 02/01/2016, Bing Li
//		private ScheduledThreadPoolExecutor scheduler;
		// The time to be waited when no notifications are available in the class. 11/20/2014, Bing Li
		private long eventingWaitTime;
		// The FreeClientPool that is used to initialize eventers. It must be shared with others. 11/20/2014, Bing Li
		private FreeClientPool clientPool;
		// The time to be waited when no notifications are available in each eventer. 11/20/2014, Bing Li
		private long eventQueueWaitTime;
		// The delay time before a periodical idle-checking is started. 01/20/2016, Bing Li
		private long idleCheckDelay;
		// The idle-checking period. 01/20/2016, Bing Li
		private long idleCheckPeriod;
		// When the eventer has no notifications to send, it has to wait. But if the waiting time is long enough, it needs to be disposed. The waitRound defines the count of outside-most loop in the run(). Because of it, the eventer has killed after no notifications are available for sufficient time. 01/20/2016, Bing Li 
//		private int waitRound;
	
//		private ScheduledThreadPoolExecutor scheduler;

		// The scheduler's thread pool size. 05/11/2017, Bing Li
		private int schedulerPoolSize;
		// The time to keep alive for threads in the scheduler. 05/11/2017, Bing Li
		private long schedulerKeepAliveTime;
		private long schedulerShutdownTimeout;
		
		public AsyncRemoteEventerBuilder()
		{
		}
		
		/*
		public AsyncRemoteEventerBuilder<Notification> threadPool(ThreadPool threadPool)
		{
			this.threadPool = threadPool;
			return this;
		}
		*/
		
		public AsyncRemoteEventerBuilder<Notification> eventQueueSize(int eventQueueSize)
		{
			this.eventQueueSize = eventQueueSize;
			return this;
		}
		
		public AsyncRemoteEventerBuilder<Notification> eventerSize(int eventerSize)
		{
			this.eventerSize = eventerSize;
			return this;
		}

		/*
		public AsyncRemoteEventerBuilder<Notification> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		*/
		
		public AsyncRemoteEventerBuilder<Notification> eventingWaitTime(long eventingWaitTime)
		{
			this.eventingWaitTime = eventingWaitTime;
			return this;
		}
		
		public AsyncRemoteEventerBuilder<Notification> clientPool(FreeClientPool clientPool)
		{
			this.clientPool = clientPool;
			return this;
		}

		public AsyncRemoteEventerBuilder<Notification> eventQueueWaitTime(long eventQueueWaitTime)
		{
			this.eventQueueWaitTime = eventQueueWaitTime;
			return this;
		}

		public AsyncRemoteEventerBuilder<Notification> idleCheckDelay(long idleCheckDelay)
		{
			this.idleCheckDelay = idleCheckDelay;
			return this;
		}

		public AsyncRemoteEventerBuilder<Notification> idleCheckPeriod(long idleCheckPeriod)
		{
			this.idleCheckPeriod = idleCheckPeriod;
			return this;
		}

		/*
		public AsyncRemoteEventerBuilder<Notification> waitRound(int waitRound)
		{
			this.waitRound = waitRound;
			return this;
		}
		*/

		/*
		public AsyncRemoteEventerBuilder<Notification> scheduler(ScheduledThreadPoolExecutor scheduler)
		{
			this.scheduler = scheduler;
			return this;
		}
		*/

		public AsyncRemoteEventerBuilder<Notification> schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}

		public AsyncRemoteEventerBuilder<Notification> schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}

		public AsyncRemoteEventerBuilder<Notification> schedulerShutdownTimeout(long shutdownTimeout)
		{
			this.schedulerShutdownTimeout = shutdownTimeout;
			return this;
		}

		@Override
		public AsyncRemoteEventer<Notification> build()
		{
			return new AsyncRemoteEventer<Notification>(this);
		}

		/*
		public ThreadPool getThreadPool()
		{
			return this.threadPool;
		}
		*/
		
		public int getEventQueueSize()
		{
			return this.eventQueueSize;
		}
		
		public int getEventerSize()
		{
			return this.eventerSize;
		}

		public long getEventingWaitTime()
		{
			return this.eventingWaitTime;
		}
		
		public FreeClientPool getClientPool()
		{
			return this.clientPool;
		}
		
		public long getEventQueueWaitTime()
		{
			return this.eventQueueWaitTime;
		}
		
		public long getIdleCheckDelay()
		{
			return this.idleCheckDelay;
		}
		
		public long getIdleCheckPeriod()
		{
			return this.idleCheckPeriod;
		}

		/*
		public int getWaitRound()
		{
			return this.waitRound;
		}
		*/

		/*
		public ScheduledThreadPoolExecutor getScheduler()
		{
			return this.scheduler;
		}
		*/

		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}
		
		public long getSchedulerShutdownTimeout()
		{
			return this.schedulerShutdownTimeout;
		}
	}

	/*
	 * The eventer manager cannot hold on forever. Thus, it is possible that it is down when it is to be reused is down. Thus, it needs to check and restart it. 01/20/2016, Bing Li
	 */
	public synchronized void restart()
	{
		// Reset the collaborator for the new resumption. 02/01/2016, Bing Li
		this.collaborator.reset();
		// Check whether the collection to keep eventers are disposed. 01/20/2016, Bing Li
		if (this.eventers == null)
		{
			// Initialize the collection to keep eventers. 01/20/2016, Bing Li
			this.eventers = new ConcurrentHashMap<String, Runner<Eventer<Notification>>>();
		}
		// Check whether the notification queue is disposed. 01/20/2016, Bing Li
		if (this.notificationQueue == null)
		{
			// Initialize the notification queue. 01/20/2016, Bing Li
			this.notificationQueue = new LinkedBlockingQueue<IPNotification<Notification>>();
		}
		// Schedule the idle-checking task. 01/20/2016, Bing Li
		this.setIdleChecker(this.idleCheckDelay, this.idleCheckPeriod);
	}

	/*
	 * Check whether the eventer manager is ready or not. 01/20/2016, Bing Li
	 */
	public synchronized boolean isReady()
	{
		/*
		// Check whether the eventer manager is running or not. 01/20/2016, Bing Li
		if (!this.collaborator.isRunning())
		{
			// Check whether the eventer manager is shutdown or not. 01/20/2016, Bing Li
			if (!this.collaborator.isShutdown())
			{
				// Restart the eventer manager if it is already shutdown. 01/20/2016, Bing Li
				this.restart();
			}
			else
			{
				// Schedule the idle-checking task. 01/20/2016, Bing Li
				this.setIdleChecker(this.idleCheckDelay, this.idleCheckPeriod);
			}
			// When the evernter is not ready, the eventer manager as a thread needs to execute. 01/20/2016, Bing Li
			return false;
		}
		return true;
		*/
		// Check whether the dispatcher is shut down. 01/14/2016, Bing Li
		if (!this.collaborator.isShutdown())
		{
			// If not, it represents the dispatcher is ready. 02/01/2016, Bing Li
			return true;
		}
		else
		{
			// Restart the dispatcher. 01/14/2016, Bing Li
			this.restart();
			// If the dispatcher is shutdown, it is not ready. With the returned false value, the dispatcher needs to be executed as a thread by an outside thread pool. 02/01/2016, Bing Li
			return false;
		}
	}

	/*
	 * Dispose the eventer dispatcher. 11/20/2014, Bing Li
	 */
	public synchronized void dispose() throws InterruptedException
	{
		/*
		// Set the shutdown flag to be true. Thus, the loop in the dispatcher thread to schedule notification loads is terminated. 11/20/2014, Bing Li
		this.collaborator.setShutdown();
		// Notify the dispatcher thread that is waiting for the notifications to terminate the waiting. 11/20/2014, Bing Li 
		this.collaborator.signalAll();
		*/
		
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Clear the notification queue. 11/20/2014, Bing Li
		if (this.notificationQueue != null)
		{
			this.notificationQueue.clear();
		}
		/*
		// Cancel the timer that controls the idle checking. 11/20/2014, Bing Li
		if (this.checkTimer != UtilConfig.NO_TIMER)
		{
			this.checkTimer.cancel();
		}
		// Terminate the periodically running thread for idle checking. 11/20/2014, Bing Li
		if (this.idleChecker != null)
		{
			this.idleChecker.cancel();
		}
		*/
		// Detect whether the idle-checking task is initialized. 02/01/2016, Bing Li
		if (this.idleCheckingTask != null)
		{
			// Cancel the idle-checking task. 02/01/2016, Bing Li
			this.idleCheckingTask.cancel(true);
		}
		// Dispose all of eventers created during the dispatcher's running procedure. 11/20/2014, Bing Li
		for (Runner<Eventer<Notification>> eventer : this.eventers.values())
		{
			eventer.stop();
		}
		// Clear the eventer map. 11/20/2014, Bing Li
		this.eventers.clear();
		if (!this.scheduler.isShutdown())
		{
			this.scheduler.shutdownNow();
			this.scheduler.awaitTermination(this.shutdownTimeout, TimeUnit.MILLISECONDS);
		}
	}

	/*
	private void internalDispose() throws InterruptedException
	{
		// The above two lines are combined and executed atomically to shutdown the dispatcher. 02/26/2016, Bing Li
		this.collaborator.shutdown();

		// Clear the notification queue. 11/20/2014, Bing Li
		if (this.notificationQueue != null)
		{
			this.notificationQueue.clear();
		}
		// Cancel the timer that controls the idle checking. 11/20/2014, Bing Li
//		if (this.checkTimer != UtilConfig.NO_TIMER)
//		{
//			this.checkTimer.cancel();
//		}
		// Terminate the periodically running thread for idle checking. 11/20/2014, Bing Li
//		if (this.idleChecker != null)
//		{
//			this.idleChecker.cancel();
//		}
		// Detect whether the idle-checking task is initialized. 02/01/2016, Bing Li
		if (this.idleCheckingTask != null)
		{
			// Cancel the idle-checking task. 02/01/2016, Bing Li
			this.idleCheckingTask.cancel(true);
		}
		// Dispose all of eventers created during the dispatcher's running procedure. 11/20/2014, Bing Li
		for (Runner<Eventer<Notification>> eventer : this.eventers.values())
		{
			eventer.stop();
		}
		// Clear the eventer map. 11/20/2014, Bing Li
		this.eventers.clear();
	}
	*/
	
	/*
	public ScheduledThreadPoolExecutor getScheduler()
	{
		return this.scheduler;
	}
	*/

	/*
	 * The method is called back by the idle checker periodically to monitor the idle states of eventers within the dispatcher. 11/20/2014, Bing Li
	 */
	@Override
	public void checkIdle() throws InterruptedException
	{
		// Check each eventer managed by the dispatcher. 11/20/2014, Bing Li
		for (Runner<Eventer<Notification>> eventer : this.eventers.values())
		{
			// If the eventer is empty and idle, it is the one to be checked. 11/20/2014, Bing Li
			if (eventer.getFunction().isEmpty() && eventer.getFunction().isIdle())
			{
				// The algorithm to determine whether an eventer should be disposed or not is simple. When it is checked to be idle, it is time to dispose it. 11/20/2014, Bing Li
				this.eventers.remove(eventer.getFunction().getKey());
				// Dispose the eventer. 11/20/2014, Bing Li
				eventer.stop();
				// Collect the resource of the eventer. 11/20/2014, Bing Li
				eventer = null;
			}
		}
	}

	/*
	 * Set the idle checking parameters. 11/20/2014, Bing Li
	 */
	@Override
	public void setIdleChecker(long idleCheckDelay, long idleCheckPeriod)
	{
		/*
		if (this.checkTimer == UtilConfig.NO_TIMER)
		{
			// Initialize the idle-checking delay. 01/20/2016, Bing Li
			this.idleCheckDelay = idleCheckDelay;
			// Initialize the idle-checking period. 01/20/2016, Bing Li
			this.idleCheckPeriod = idleCheckPeriod;
			// Initialize the timer. 11/20/2014, Bing Li
			this.checkTimer = new Timer();
			// Initialize the idle checker. 11/20/2014, Bing Li
			this.idleChecker = new EventerIdleChecker<AsyncRemoteEventer<Notification>>(this);
		}
		// Schedule the idle checking task. 11/20/2014, Bing Li
		this.checkTimer.schedule(this.idleChecker, idleCheckDelay, idleCheckPeriod);
		*/
			// Schedule the idle-checking task and obtain the task instance, which is used to cancel the task when required. 02/01/2016, Bing Li
//		this.idleCheckingTask = Scheduler.GREATFREE().getSchedulerPool().scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
		this.idleCheckingTask = this.scheduler.scheduleAtFixedRate(this.idleChecker, idleCheckDelay, idleCheckPeriod, TimeUnit.MILLISECONDS);
	}
	
	public void clearIPs()
	{
		this.clientPool.clearAll();
	}
	
	public void addIP(String ip, int port)
	{
		this.clientPool.addIP(ip, port);
	}
	
	public int getClientSize()
	{
		return this.clientPool.getClientSourceSize();
	}
	
	public Set<String> getClientKeys()
	{
		return this.clientPool.getClientKeys();
	}

	public Set<String> getClientKeys(int n)
	{
		Set<String> childrenKeys = this.clientPool.getClientKeys();
		if (n >= childrenKeys.size())
		{
			return childrenKeys;
		}
		else
		{
			return Rand.getRandomSet(childrenKeys, n);
		}
	}

	
	public IPAddress getIPAddress(String clientKey)
	{
		return this.clientPool.getIPAddress(clientKey);
	}
	
	public void removeClient(String clientKey) throws IOException
	{
		this.clientPool.removeClient(clientKey);
	}

	/*
	 * Send the notification asynchronously to the IP/port. 11/20/2014, Bing Li
	 */
//	public synchronized void notify(String ip, int ipPort, Notification notification)
	public synchronized void notify(String ip, int ipPort, Notification notification)
	{
		// With the lock, the notification queue keeps synchronized with self-disposing mechanism. 02/01/2016, Bing Li
		this.monitorLock.lock();
		// Put the notification and the IP/port into the queue by enclosing them into an instance of IPNotification. 11/20/2014, Bing Li
		this.notificationQueue.add(new IPNotification<Notification>(new IPResource(ip, ipPort), notification));
		this.monitorLock.unlock();
		// Signal the potential waiting thread to schedule eventers to sent the notification just enqueued. 11/20/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * Send the notification asynchronously to a remote node by its key. 11/20/2014, Bing Li
	 */
	public synchronized void notify(String clientKey, Notification notification)
	{
		// With the lock, the notification queue keeps synchronized with self-disposing mechanism. 02/01/2016, Bing Li
		this.monitorLock.lock();
		// Put the notification and the IP/port into the queue by enclosing them into an instance of IPNotification. The IP/port is retrieved from the FreeClientPool by the node key. 11/20/2014, Bing Li
		this.notificationQueue.add(new IPNotification<Notification>(this.clientPool.getIPPort(clientKey), notification));
		this.monitorLock.unlock();
		// Signal the potential waiting thread to schedule eventers to sent the notification just enqueued. 11/20/2014, Bing Li
		this.collaborator.signal();
	}
	
	/*
	 * The thread of the dispatcher is always running until no notifications to be sent. If too many notifications are received, more eventers are created by the dispatcher. If notifications are limited, the count of threads created by the dispatcher is also small. It is true no any threads are alive when no notifications are received for a long time. 11/20/2014, Bing Li
	 */
	public void run()
	{
		// Declare a notification. 11/20/2014, Bing Li
//		IPNotification<Notification> notification;
		// Initialize a task map to calculate the load of each eventer. 11/20/2014, Bing Li
//		Map<String, Integer> taskMap = new HashMap<String, Integer>();
		// Declare a string to keep the selected eventer key. 11/20/2014, Bing Li
		String selectedThreadKey = UtilConfig.NO_KEY;
		// Initialize the currentRound to maintain the outside-most loop count. 01/20/2016, Bing Li
//		int currentRound = 0;
		Runner<Eventer<Notification>> runner;
		// The dispatcher usually runs all of the time unless the local node is shutdown. To shutdown the dispatcher, the shutdown flag of the collaborator is set to true. 11/20/2014, Bing Li
		while (!this.collaborator.isShutdown())
		{
			try
			{
				// Check whether notifications are available in the queue. 11/20/2014, Bing Li
				while (!this.notificationQueue.isEmpty())
				{
					// Dequeue the notification from the queue of the dispatcher. 11/20/2014, Bing Li
//					notification = this.notificationQueue.poll();

					// Since all of the eventers created by the dispatcher are saved in the map by their unique keys, it is necessary to check whether any alive eventers are available. If so, it is possible to assign tasks to them if they are not so busy. 11/20/2014, Bing Li
					while (this.eventers.size() > 0)
					{
						// Clear the map to start to calculate the loads of those eventers. 11/20/2014, Bing Li
//						taskMap.clear();

						// Each eventer's workload is saved into the task map. 11/20/2014, Bing Li
						/*
						for (Eventer<Notification> thread : this.eventers.values())
						{
							taskMap.put(thread.getKey(), thread.getQueueSize());
						}
						*/
						// Select the eventer whose load is the least and keep the key of the eventer. 11/20/2014, Bing Li
//						selectedThreadKey = CollectionSorter.minValueKey(taskMap);
						selectedThreadKey = CollectionSorter.minValueKey(this.eventers);
						// Since no concurrency is applied here, it is possible that the key is invalid. Thus, just check here. 11/20/2014, Bing Li
						if (selectedThreadKey != null)
						{
							// Since no concurrency is applied here, it is possible that the key is out of the map. Thus, just check here. 11/20/2014, Bing Li
							if (this.eventers.containsKey(selectedThreadKey))
							{
								try
								{
									// Check whether the eventer's load reaches the maximum value. 11/20/2014, Bing Li
									if (this.eventers.get(selectedThreadKey).getFunction().isFull())
									{
										// Check if the pool is full. If the least load eventer is full as checked by the above condition, it denotes that all of the current alive eventers are full. So it is required to create an eventer to respond the newly received notifications if the eventer count of the pool does not reach the maximum. 11/20/2014, Bing Li
										if (this.eventers.size() < this.eventerSize)
										{
											// Create a new eventer. 11/20/2014, Bing Li
											Eventer<Notification> thread = new Eventer<Notification>(this.eventQueueSize, this.eventerQueueWaitTime, this.clientPool);
											// Create an instance of Runner. 05/20/2018, Bing Li
											runner = new Runner<Eventer<Notification>>(thread);
											// Start the runner. 05/20/2018, Bing Li
											runner.start();
											// Save the newly created eventer into the map. 11/20/2014, Bing Li
											this.eventers.put(thread.getKey(), runner);
											// Enqueue the notification into the queue of the newly created eventer. Then, the notification will be processed by the eventer. 11/20/2014, Bing Li
											this.eventers.get(thread.getKey()).getFunction().enqueue(this.notificationQueue.poll());
											// Start the eventer by the thread pool. 11/20/2014, Bing Li
//											this.threadPool.execute(this.eventers.get(thread.getKey()));
										}
										else
										{
											// Force to put the notification into the queue when the count of eventers reaches the upper limit and each of the eventer's queue is full. 11/20/2014, Bing Li
											this.eventers.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
										}
									}
									else
									{
										// If the least load eventer's queue is not full, just put the notification into the queue. 11/20/2014, Bing Li
										this.eventers.get(selectedThreadKey).getFunction().enqueue(this.notificationQueue.poll());
									}

									// Jump out from the loop since the notification is put into a thread. 11/20/2014, Bing Li
									break;
								}
								catch (NullPointerException e)
								{
									// Since no concurrency is applied here, it is possible that a NullPointerException is raised. If so, it means that the selected eventer is not available. Just continue to select anther one. 11/20/2014, Bing Li
									continue;
								}
							}
						}
					}
					// If no eventers are available, it needs to create a new one to take the notification. 11/20/2014, Bing Li
					if (this.eventers.size() <= 0)
					{
						// Create a new eventer. 11/20/2014, Bing Li
						Eventer<Notification> thread = new Eventer<Notification>(this.eventQueueSize, this.eventerQueueWaitTime, this.clientPool);
						// Create an instance of Runner. 05/20/2018, Bing Li
						runner = new Runner<Eventer<Notification>>(thread);
						// Start the runner. 05/20/2018, Bing Li
						runner.start();
						// Put it into the map for further reuse. 11/20/2014, Bing Li
						this.eventers.put(thread.getKey(), runner);
						// Take the notification. 11/20/2014, Bing Li
//						this.threadPool.execute(this.eventers.get(thread.getKey()));
						// Start the thread. 11/20/2014, Bing Li
						this.eventers.get(thread.getKey()).getFunction().enqueue(this.notificationQueue.poll());
					}

					// If the dispatcher is shutdown, it is not necessary to keep processing the notifications. So, jump out the loop and the eventer is dead. 11/20/2014, Bing Li
					if (this.collaborator.isShutdown())
					{
						break;
					}
				}

				// Check whether the dispatcher is shutdown or not. 11/20/2014, Bing Li
				if (!this.collaborator.isShutdown())
				{
					// If the dispatcher is still alive, it denotes that no notifications are available temporarily. Just wait for a while. 11/20/2014, Bing Li
					this.collaborator.holdOn(this.eventingWaitTime);
					/*
					// The lock keeps synchronized between the notificationQueue and the self-disposing mechanism. When checking whether the dispatcher should be disposed, it needs to protect the dispatcher from receiving additional notifications. 02/01/2016, Bing Li
					this.monitorLock.lock();
					try
					{
						// Detect whether the notification is empty. 01/20/2016, Bing Li
						if (this.notificationQueue.size() <= 0)
						{
							// Check whether the waitRound is infinite. 01/20/2016, Bing Li
							if (this.waitRound != UtilConfig.INFINITE_WAIT_ROUND)
							{
								// Check whether the outside-most loop reaches the upper limit. 01/20/2016, Bing Li
								if (currentRound++ >= this.waitRound)
								{
									// Check whether all of the eventers are disposed. 01/20/2016, Bing Li
									if (this.eventers.isEmpty())
									{
										// Dispose the eventer manager. 01/20/2016, Bing Li
										this.internalDispose();
										// Quit the outside-most loop. 01/20/2016, Bing Li
										break;
									}
								}
							}
						}
					}
					finally
					{
						this.monitorLock.unlock();
					}
					*/
				}
			}
			/*
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			*/
			catch (NullPointerException e)
			{
				e.printStackTrace();
			}
		}
	}
}
