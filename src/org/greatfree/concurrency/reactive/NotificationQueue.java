package org.greatfree.concurrency.reactive;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

import org.greatfree.concurrency.RunnerTask;
import org.greatfree.concurrency.Sync;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.Tools;

/*
 * The server key is added as a new field. 03/30/2020, Bing Li
 * 
 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
 */

/*
 * The Notification is updated. It extends the abstract class, DisposableRunner, instead of implementing the interface of Runnable. Then, the thread is executed by Runner rather than ThreadPool. The update simplifies the code as well as lowering the resource consumption. 05/19/2018, Bing Li
 * 
 * A fundamental thread that receives and processes notifications in the form of messages concurrently. Notifications are put into a queue and prepare for further processing. It must be derived by sub classes to process specific notifications. 11/04/2014, Bing Li
 * 
 * The thread implements the comparable interface such that it can be ranked by the task loads. It is useful for a thread pool to manage the threads. 02/01/2016, Bing Li
 * 
 */

// Created: 11/04/2014, Bing Li
// public abstract class NotificationQueue<Notification extends ServerMessage> implements Runnable, Comparable<NotificationQueue<Notification>>
// public abstract class NotificationQueue<Notification extends ServerMessage> extends DisposableRunner implements Comparable<NotificationQueue<Notification>>
public abstract class NotificationQueue<Notification extends ServerMessage> extends RunnerTask
{
//	private final static Logger log = Logger.getLogger("org.greatfree.concurrency.reactive");

	// Declare the key for the notification thread. 11/04/2014, Bing Li
	private final String key;
	// Declare an instance of LinkedBlockingQueue to take received notifications. 11/04/2014, Bing Li
//	private LinkedBlockingQueue<Notification> queue;
	private Queue<Notification> queue;
	// Declare the size of the queue. 11/04/2014, Bing Li
	private final int taskSize;
	// The notify/wait mechanism to implement the producer/consumer pattern. 11/04/2014, Bing Li
	private Sync collaborator;
	// The flag that indicates the busy/idle state of the thread. 11/04/2014, Bing Li
	private boolean isIdle;
	// The lock is critical to keep synchronous to manage the idle/busy state of the thread. 02/07/2016, Bing Li
	private ReentrantLock idleLock;

	// The thread is possibly interrupted by the thread pool/the system when the thread is hung by a task permanently. If so, the exception is not required to be displayed according to the flag. 11/05/2019, Bing Li
//	private AtomicBoolean isSysInterrupted;
	private AtomicBoolean isHung;
	
	/*
	 * The server key is added as a new field. 03/30/2020, Bing Li
	 * 
	 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
	 */
	private String serverKey;

	/*
	 * Initialize the notification thread. This constructor has no limit on the size of the queue. 11/04/2014, Bing Li
	 */
	/*
	public NotificationQueue()
	{
		// Generate the key. 11/04/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue without any size constraint. 11/04/2014, Bing Li
		this.queue = new LinkedBlockingQueue<Notification>();
		// Ignore the value of taskSize. 11/04/2014, Bing Li
		this.taskSize = UtilConfig.NO_QUEUE_SIZE;
		// Initialize the collaborator. 11/04/2014, Bing Li
		this.collaborator = new Sync();
		// Set the idle state to false. 11/04/2014, Bing Li
		this.isIdle = false;
		// Initialize the lock that keeps synchronous of the threa's state, busy or idle. 02/07/2016, Bing Li
		this.idleLock = new ReentrantLock();
	}
	*/
	
	/*
	 * Initialize the notification thread. This constructor has a limit on the size of the queue. 11/04/2014, Bing Li
	 */
	public NotificationQueue(int taskSize)
	{
		// Generate the key. 11/04/2014, Bing Li
		this.key = Tools.generateUniqueKey();
		// Initialize the queue with the particular size constraint. 11/04/2014, Bing Li
//		this.queue = new LinkedBlockingQueue<Notification>(taskSize);
		this.queue = new LinkedBlockingQueue<Notification>();
		// Set the value of taskSize. 11/04/2014, Bing Li
		this.taskSize = taskSize;
		// Initialize the collaborator. 11/04/2014, Bing Li
		this.collaborator = new Sync();
		// Set the idle state to false. 11/04/2014, Bing Li
		this.isIdle = false;
		// Initialize the lock that keeps synchronous of the threa's state, busy or idle. 02/07/2016, Bing Li
		this.idleLock = new ReentrantLock();
//		this.isSysInterrupted = new AtomicBoolean(false);
		this.isHung = new AtomicBoolean(false);
	}
	
//	public abstract NotificationQueue<Notification> createInstance();

	/*
	 * Dispose the notification thread. 11/04/2014, Bing Li
	 */
	@Override
	public synchronized void dispose() throws InterruptedException
	{
		// Set the flag to be the state of being shutdown. 11/04/2014, Bing Li
		/*
		this.collaborator.setShutdown();
		// Notify the thread being waiting to go forward. Since the shutdown flag is set, the thread must die for the notification. 11/04/2014, Bing Li
		this.collaborator.signalAll();
		try
		{
			// Wait for the thread to die. 11/04/2014, Bing Li
			this.join();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		*/

		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/04/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
		/*
		if (this.isHung.get())
		{
			this.interrupt();
		}
		*/
	}

	@Override
	public synchronized void dispose(long timeout) throws InterruptedException
	{
		// The above shutdown lines are combined and executed atomically. 02/26/2016, Bing Li
		this.collaborator.shutdown(this);
		
		// Clear the queue to release resources. 11/04/2014, Bing Li
		if (this.queue != null)
		{
			this.queue.clear();
		}
		/*
		if (this.isHung.get())
		{
			this.interrupt();
		}
		*/
	}

	/*
	 * Expose the key for the convenient management. 11/04/2014, Bing Li
	 */
	public String getKey()
	{
		return this.key;
	}
	
	/*
	 * The server key is added as a new field. 03/30/2020, Bing Li
	 * 
	 * 	The key is used to identify server tasks if multiple servers instances exist within a single process. In the previous versions, only one server tasks are allowed. It is a defect if multiple instances of servers exist in a process since they are overwritten one another. 03/30/2020, Bing Li
	 */
	public void setServerKey(String key)
	{
		this.serverKey = key;
	}
	
	public String getServerKey()
	{
		return this.serverKey;
	}

	/*
	 * It is useless and incorrect to interrupt a thread this way. It should be done in the runner, which has a method, interrupt(). 11/07/2019, Bing Li
	 * 
	 * The method aims to kill one thread that is hung permanently by a task. 11/05/2019, Bing Li
	 */
	/*
	public void interrupt()
	{
		this.isSysInterrupted.set(true);
		Thread.currentThread().interrupt();
	}
	*/

	/*
	public boolean isSysInterrupted()
	{
		return this.isSysInterrupted.get();
	}
	*/

	public boolean isHung()
	{
		return this.isHung.get();
	}
	
	/*
	 * Enqueue a notification into the thread. 11/04/2014, Bing Li
	 */
//	public boolean enqueue(Notification notification) throws IllegalStateException
	public void enqueue(Notification notification) throws IllegalStateException
	{
		// Set the state of the thread to be busy. 11/04/2014, Bing Li
//		this.setBusy();
		this.idleLock.lock();
		try
		{
			// Set the state of busy for the thread. 02/07/2016, Bing Li
			this.isIdle = false;
			// Enqueue the notification. 11/04/2014, Bing Li
			this.queue.add(notification);
			/*
			// Sometimes it happens that the thread is shutdown after one notification is received. Thus, it needs to check. 02/22/2016, Bing Li
			if (!this.collaborator.isShutdown())
			{
				// Notify the waiting thread to keep on working since new notifications are received. 09/22/2014, Bing Li
				this.collaborator.signal();
				return true;
			}
			else
			{
				return false;
			}
			*/
		}
		finally
		{
			this.idleLock.unlock();
		}
		// Notify the waiting thread to keep on working since new notifications are received. 09/22/2014, Bing Li
		this.collaborator.signal();
	}

	/*
	 * Set the state to be busy. 11/04/2014, Bing Li
	 */
	/*
	private synchronized void setBusy()
	{
		this.isIdle = false;
	}
	*/
	
	/*
	 * Set the state to be idle. 11/04/2014, Bing Li
	 */
	/*
	private synchronized void setIdle()
	{
		this.isIdle = true;
	}
	*/
	
	/*
	 * Get the current size of the queue. 11/04/2014, Bing Li
	 */
	@Override
//	public int getQueueSize()
	public int getWorkload()
	{
		return this.queue.size();
	}


	/*
	 * It is not necessary since the shutdown state is judged in the loop immediately. 12/01/2021, Bing Li
	 * 
	 * I modified the code. If the thread needs to be shutdown, a false value needs to be returned. 12/01/2021, Bing Li
	 */

	/*
	 * It is not necessary since the shutdown state is judged in the loop immediately. 12/01/2021, Bing Li
	 * 
	 * I modified the code. If the thread needs to be shutdown, a false value needs to be returned. 12/01/2021, Bing Li
	 * 
	 * The method intends to stop the thread temporarily when no notifications are available. A thread is identified as being idle immediately after the temporary waiting is finished. 11/04/2014, Bing Li
	 */
//	public boolean holdOn(long waitTime) throws InterruptedException
	public void holdOn(long waitTime) throws InterruptedException
	{
//		System.out.println("NotificationQueue-holdOn(): waitTime = " + waitTime);
//		System.out.println("1) NotificationQueue-holdOn(): this.isIdle = " + this.isIdle);
		/*
		// The lock intends to avoid the problem to shutdown the thread when the thread is holding on. 02/06/2016, Bing Li
		this.idleLock.lock();
		// Set a local variable to keep the isIdle state before holding on. 02/22/2016, Bing Li
		boolean isIdleBeforeHoldOn = this.isIdle;
		// Set the state of busy for the thread. 02/07/2016, Bing Li
		this.isIdle = false;
		this.idleLock.unlock();
		*/
		// Wait for some time, which is determined by the value of waitTime. 11/04/2014, Bing Li
		if (this.collaborator.holdOn(waitTime))
		{
//			log.info("before the lock ...");
//				this.setIdle();
			this.idleLock.lock();
//			log.info("after the lock ...");
			// Only when the queue is empty, the thread is set to be busy. 02/07/2016, Bing Li
			if (this.queue.size() <= 0)
			{
//					System.out.println("one notification queue is set to be idle ...");
//				log.info("one notification queue is set to be idle ...");
				// Set the state of the thread to be idle after waiting for some time. 11/04/2014, Bing Li
				this.isIdle = true;
//				log.info("one notification queue is set idle ...");
				// If the thread is idle before holding on and the queue is empty after the waiting, it really indicates the thread is idle. So, it can dispose itself at this moment. 02/22/2016, Bing Li
				/*
				if (isIdleBeforeHoldOn)
				{
					// Dispose the thread itself. 02/22/2016, Bing Li
					this.dispose();
				}
				*/
//					this.dispose();
			}
			this.idleLock.unlock();
		}

//		log.info("hold on stopped ... " + this.hashCode());
//			return true;

		// To be continued. A severe bug exists here. If the isIdle is true at the line, it is possible to dispose the current thread by itself. If the thread needs to be disposed by the idleChecker, it is possible to the value of isIdle is reset to be false. Thus, the thread cannot be disposed for ever. 02/21/2016, Bing Li
//		System.out.println("2) NotificationQueue-holdOn(): this.isIdle = " + this.isIdle);
	}
	
	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/04/2014, Bing Li 
	 */
	public boolean isShutdown()
	{
		return this.collaborator.isShutdown();
	}
	
	/*
	 * Check whether the current size of the queue reaches the upper limit. 11/04/2014, Bing Li
	 */
	public boolean isFull()
	{
//		System.out.println("Thread-" + super.hashCode() + ": NotificationQueue-isFull(): queue.size = " + this.queue.size() + ", taskSize = " + this.taskSize);
		return this.queue.size() >= this.taskSize;
	}
	
	/*
	 * Expose the task size. 05/19/2018, Bing Li
	 */
	public int getTaskSize()
	{
		return this.taskSize;
	}
	
	/*
	 * Check whether the shutdown flag of the thread is set or not. It might take some time for the thread to be shutdown practically even though the flag is set. 11/04/2014, Bing Li 
	 */
	public boolean isEmpty()
	{
		return this.queue.size() <= 0;
	}
	
	/*
	 * Check whether the thread is idle or not. 11/04/2014, Bing Li
	 */
//	public synchronized boolean isIdle()
	public boolean isIdle()
	{
		this.idleLock.lock();
		try
		{
//			System.out.println("NotificationQueue-isIdle(): queue size = " + this.queue.size());
//			System.out.println("NotificationQueue: this.queue size = " + this.queue.size());
//			System.out.println("NotificationQueue: this.isIdle = " + this.isIdle);
			// The thread is believed to be idle only when the notification queue is empty and the idle is set to be true. The lock mechanism prevents one possibility that the queue gets new messages and the idle is set to be true. The situation occurs when the size of the queue and the idle value are checked asynchronously. Both of them being detected are a better solution. The idle guarantees the sufficient time has been waited and the queue size indicates that the thread is really not busy. 02/07/2016, Bing Li
			if (this.queue.size() <= 0)
			{
//				System.out.println("1) NotificationQueue-isIdle(): this.isIdle = " + this.isIdle);
//				this.dispose();
				return this.isIdle;
			}
			else
			{
				// If the queue size is not empty, the thread is believed to be busy even though the idle is set to be true. 02/07/2016, Bing Li
//				System.out.println("2) NotificationQueue-isIdle(): this.isIdle = " + this.isIdle);
//				System.out.println("2) NotificationQueue-isIdle(): this.queue size = " + this.queue.size());
				return false;
			}
		}
		finally
		{
			this.idleLock.unlock();
		}
	}
	
	/*
	 * Dequeue the notification from the queue. 11/04/2014, Bing Li
	 */
	public Notification dequeue() throws InterruptedException
	{
		this.isHung.set(true);
		return this.queue.poll();
	}

	/*
	 * Get the notification but leave the notification in the queue. 11/04/2014, Bing Li
	 */
	public Notification peekNotification()
	{
		this.isHung.set(true);
		return this.queue.peek();
	}
	
	/*
	 * Dispose the notification. 09/22/2014, Bing Li
	 */
	public synchronized void disposeMessage(Notification notification)
	{
		this.isHung.set(false);
		notification = null;
	}

	/*
	 * Dispose something that generates during the procedure handling the notification. Usually, it is called during a multicasting procedure. 07/23/2017, Bing Li
	 */
	public synchronized void dispose(Object obj)
	{
		this.isHung.set(false);
		obj = null;
	}

	/*
	 * Compare threads according to their task loads in the notification queue. 02/01/2016, Bing Li
	 */
	/*
	@Override
	public int compareTo(NotificationQueue<Notification> obj)
	{
		if (obj != null)
		{
			if (this.queue.size() > obj.getQueueSize())
			{
				return 1;
			}
			else if (this.queue.size() == obj.getQueueSize())
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
	*/
}
