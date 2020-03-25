package org.greatfree.concurrency.threading;

import java.io.IOException;
import java.util.Set;

import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ThreadAssignmentException;
import org.greatfree.util.Tools;
import org.greatfree.util.UtilConfig;

/*
 * One player is a one-to-one mapping to one slave. 10/01/2019, Bing Li
 * 
 * One important update is that a player does not map to a unique thread only. It is possible to map to multiple thread keys. In the case of Map/Reduce, the feature is useful. 10/01/2019, Bing Li
 * 
 * The player is a mapping of the remote thread. 09/28/2019, Bing Li
 */

// Created: 09/28/2019, Bing Li
public class Player
{
	// The key is identical to the thread key. 09/28/2019, Bing Li
	private String key;
	// The slave key represents the distributed node on which the thread is located. 09/28/2019, Bing Li
	private String slaveKey;
	// The reference to the distributer to manipulate the remote thread.
	private Distributer dt;
	private long readTimeout;
	private String threadKey;
	private Set<String> threadKeys;
	
	/*
	 * The constructor is used to create the players that work as masters. 09/29/2019, Bing Li
	 */
	public Player(Distributer dt, String threadKey, String slaveKey, long readTimeout)
	{
		this.key = Tools.generateUniqueKey();
		this.slaveKey = slaveKey;
		this.dt = dt;
		this.readTimeout = readTimeout;
		this.threadKey = threadKey;
		this.threadKeys = ThreadConfig.NO_THREAD_KEYS;
	}
	
	/*
	 * The constructor is used to create the players that work as masters. 09/29/2019, Bing Li
	 */
	public Player(Distributer dt, String threadKey, String slaveKey)
	{
		this.key = Tools.generateUniqueKey();
		this.slaveKey = slaveKey;
		this.dt = dt;
		this.readTimeout = ThreadConfig.READ_TIMEOUT;
		this.threadKey = threadKey;
		this.threadKeys = ThreadConfig.NO_THREAD_KEYS;
	}
	
	/*
	 * The constructor is used to create the players that work as masters. 09/29/2019, Bing Li
	 */
	public Player(Distributer dt, Set<String> threadKeys, String slaveKey, long readTimeout)
	{
		this.key = Tools.generateUniqueKey();
		this.slaveKey = slaveKey;
		this.dt = dt;
		this.readTimeout = readTimeout;
		this.threadKey = ThreadConfig.NO_THREAD_KEY;
		this.threadKeys = threadKeys;
	}
	
	/*
	 * The constructor is used to create the players that work as masters. 09/29/2019, Bing Li
	 */
	public Player(Distributer dt, Set<String> threadKeys, String slaveKey)
	{
		this.key = Tools.generateUniqueKey();
		this.slaveKey = slaveKey;
		this.dt = dt;
		this.readTimeout = ThreadConfig.READ_TIMEOUT;
		this.threadKey = ThreadConfig.NO_THREAD_KEY;
		this.threadKeys = threadKeys;
	}
	

	/*
	 * No players are needed at the slave side. 09/30/2019, Bing Li
	 * 
	 * The constructor is used to create the players that work as slaves. 09/29/2019, Bing Li
	 */
	/*
	public Player(Distributer dt)
	{
		this.dt = dt;
	}
	*/
	
	public void dispose(long timeout) throws IOException, InterruptedException
	{
		this.dt.kill(this.slaveKey, this.key, timeout);
	}
	
	public String getSlaveKey()
	{
		return this.slaveKey;
	}
	
	public String getThreadKey()
	{
		return this.threadKey;
	}
	
	public Set<String> getThreadKeys()
	{
		return this.threadKeys;
	}

	/*
	 * The methods need to detect whether the threads are alive or not remotely. So the performance is low. 10/05/2019, Bing Li
	 * 
	 * The message which describes the task the thread to be accomplished. 09/28/2019, Bing Li
	 */
	public void notifyThreads(TaskNotification notification) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException, ThreadAssignmentException
	{
		if (!notification.getThreadKey().equals(notification.getThreadKey()))
		{
			if (!this.dt.isAlive(this.slaveKey, notification.getThreadKey()))
			{
				this.dt.execute(this.slaveKey, notification.getThreadKey());
			}
		}
		else if (notification.getThreadKeys() != ThreadConfig.NO_THREAD_KEYS)
		{
			for (String entry : notification.getThreadKeys())
			{
				if (!this.dt.isAlive(this.slaveKey, entry))
				{
					this.dt.execute(this.slaveKey, entry);
				}
			}
		}
		else
		{
			throw new ThreadAssignmentException();
		}
		this.dt.assignTask(this.slaveKey, notification);
	}
	
	/*
	 * The methods need to detect whether the threads are alive or not remotely. So the performance is low. 10/05/2019, Bing Li
	 */
	public TaskResponse readThread(TaskRequest request) throws ClassNotFoundException, RemoteReadException, IOException, ThreadAssignmentException
	{
		if (!request.getThreadKey().equals(ThreadConfig.NO_THREAD_KEY))
		{
			if (!this.dt.isAlive(this.slaveKey, request.getThreadKey()))
			{
				this.dt.execute(this.slaveKey, request.getThreadKey());
			}
			return this.dt.assignTask(this.slaveKey, request, this.readTimeout);
		}
		else
		{
			throw new ThreadAssignmentException();
		}
	}

	/*
	 * The methods need to detect whether the threads are alive or not remotely. So the performance is low. 10/05/2019, Bing Li
	 */
	public Set<TaskResponse> readThreads(TaskRequest request) throws ClassNotFoundException, RemoteReadException, IOException, ThreadAssignmentException
	{
		if (request.getThreadKeys() != ThreadConfig.NO_THREAD_KEYS)
		{
			for (String entry : request.getThreadKeys())
			{
				if (!this.dt.isAlive(this.slaveKey, entry))
				{
					this.dt.execute(this.slaveKey, entry);
				}
			}
			return this.dt.assignTasks(this.slaveKey, request, this.readTimeout);
		}
		else
		{
			throw new ThreadAssignmentException();
		}
	}

	public void notifyThreads(TaskInvokeNotification notification) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		this.dt.assignTask(this.slaveKey, notification);
	}
	
	public TaskResponse readThread(TaskInvokeRequest request) throws ClassNotFoundException, RemoteReadException, IOException, ThreadAssignmentException
	{
		return this.dt.assignTask(this.slaveKey, request, this.readTimeout);
	}
	
	public Set<TaskResponse> readThreads(TaskInvokeRequest request) throws ClassNotFoundException, RemoteReadException, IOException, ThreadAssignmentException
	{
		return this.dt.assignTasks(this.slaveKey, request, this.readTimeout);
	}
	
	public void notifyThreads(InteractNotification notification)
	{
		this.dt.notify(notification);
	}
	
	public TaskResponse readThread(InteractRequest request) throws ClassNotFoundException, RemoteReadException, IOException, ThreadAssignmentException, InterruptedException
	{
		return this.dt.readThread(request, this.readTimeout);
	}
	
	public Set<TaskResponse> readThreads(InteractRequest request) throws ClassNotFoundException, RemoteReadException, IOException, ThreadAssignmentException
	{
		return this.dt.readThreads(request, this.readTimeout);
	}
	
	public String toString()
	{
		if (!this.threadKey.equals(ThreadConfig.NO_THREAD_KEY))
		{
			return ThreadConfig.SLAVE + UtilConfig.COLON + this.slaveKey + UtilConfig.SEMI_COLON + ThreadConfig.THREAD + UtilConfig.COLON + UtilConfig.ONE;
		}
		else
		{
			return ThreadConfig.SLAVE + UtilConfig.COLON + this.slaveKey + UtilConfig.SEMI_COLON + ThreadConfig.THREAD + UtilConfig.COLON + this.threadKeys.size();
		}
	}
}
