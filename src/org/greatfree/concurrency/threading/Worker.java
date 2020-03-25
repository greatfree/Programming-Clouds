package org.greatfree.concurrency.threading;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.greatfree.concurrency.Sync;
import org.greatfree.concurrency.threading.message.InteractNotification;
import org.greatfree.concurrency.threading.message.InteractRequest;
import org.greatfree.concurrency.threading.message.TaskInvokeNotification;
import org.greatfree.concurrency.threading.message.TaskInvokeRequest;
import org.greatfree.concurrency.threading.message.TaskNotification;
import org.greatfree.concurrency.threading.message.TaskRequest;
import org.greatfree.concurrency.threading.message.TaskResponse;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.Rand;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.UtilConfig;

import com.google.common.collect.Sets;

// Created: 09/21/2019, Bing Li
class Worker
{
	private Distributer dt;
//	private ThreadTask task;
	private Map<String, ThreadTask> tasks;
	private Map<String, Sync> syncs;
	private Map<String, Set<TaskResponse>> responses;
	
	private Worker()
	{
	}
	
	private static Worker instance = new Worker();
	
	public static Worker THREADING()
	{
		if (instance == null)
		{
			instance = new Worker();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void init(Distributer dt)
	{
		this.dt = dt;
//		this.task = null;
		this.tasks = new ConcurrentHashMap<String, ThreadTask>();
		this.syncs = new ConcurrentHashMap<String, Sync>();
		this.responses = new ConcurrentHashMap<String, Set<TaskResponse>>();
	}

	/*
	public void init(Actor actor, ThreadTask task)
	{
		this.actor = actor;
		this.task = task;
		this.tasks = null;
	}
	*/
	
	public void addTask(ThreadTask t)
	{
		this.tasks.put(t.getKey(), t);
	}
	
	public void shutdown(long timeout) throws ClassNotFoundException, InterruptedException, IOException, RemoteReadException
	{
		ServerStatus.FREE().setShutdown();
		this.dt.stop(timeout);
	}
	
	public void addSync(String key, int cd)
	{
		this.syncs.put(key, new Sync(cd));
	}
	
	public void addSync(String key)
	{
		this.syncs.put(key, new Sync());
	}
	
	public void holdOn(String key, long timeout)
	{
		this.syncs.get(key).holdOn(timeout);
	}
	
	public void signal(String key)
	{
		this.syncs.get(key).signal();
	}
	
	public void processNotification(String threadKey, TaskNotification notification)
	{
		/*
		if (!notification.getTaskKey().equals(UtilConfig.EMPTY_STRING))
		{
			this.tasks.get(notification.getTaskKey()).processNotification(notification);
		}
		else
		{
			this.task.processNotification(notification);
		}
		*/
//		PrintTaskNotification n = (PrintTaskNotification)notification;
//		System.out.println("Worker-processNotification(): message = " + n.getMessage());
//		System.out.println("Worker-processNotification(): threadKey = " + notification.getThreadKey());
		
		this.tasks.get(notification.getTaskKey()).processNotification(threadKey, notification);
//		this.threader.notifyState(threadKey, notification.getTaskKey(), notification.getApplicationID(), notification.getInstructKey(), true);
		this.dt.asyncNotifyState(threadKey, notification.getTaskKey(), notification.getApplicationID(), notification.getKey(), true);
	}
	
	public void processNotification(String threadKey, TaskInvokeNotification notification)
	{
		this.tasks.get(notification.getTaskKey()).processNotification(threadKey, notification);
		this.dt.asyncNotifyState(threadKey, notification.getTaskKey(), notification.getApplicationID(), notification.getKey(), true);
	}
	
	public void processRequest(String threadKey, TaskRequest request)
	{
		this.dt.asyncNotifyMaster(this.tasks.get(request.getTaskKey()).processRequest(threadKey, request));
		this.dt.asyncNotifyState(threadKey, request.getTaskKey(), request.getApplicationID(), request.getKey(), true);
	}
	
	public void processRequest(String threadKey, TaskInvokeRequest request)
	{
		this.dt.asyncNotifyMaster(this.tasks.get(request.getTaskKey()).processRequest(threadKey, request));
		this.dt.asyncNotifyState(threadKey, request.getTaskKey(), request.getApplicationID(), request.getKey(), true);
	}
	
	public void processNotification(String threadKey, InteractNotification notification)
	{
		this.tasks.get(notification.getTaskKey()).processNotification(threadKey, notification);
		this.dt.asyncNotifyState(threadKey, notification.getTaskKey(), notification.getApplicationID(), notification.getKey(), true);
	}
	
	public void processRequest(String threadKey, InteractRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.dt.asyncNotifySlave(request.getSourceSlaveKey(), this.tasks.get(request.getTaskKey()).processRequest(threadKey, request));
		this.dt.asyncNotifyState(threadKey, request.getTaskKey(), request.getApplicationID(), request.getKey(), true);
	}
	
	public void addResponse(TaskResponse response)
	{
		if (this.syncs.get(response.getCollaboratorKey()).getCD() == UtilConfig.NO_COUNT)
		{
			Set<TaskResponse> reses = Sets.newHashSet();
			reses.add(response);
			this.responses.put(response.getCollaboratorKey(), reses);
			this.syncs.get(response.getCollaboratorKey()).signal();
		}
		else
		{
			if (!this.responses.containsKey(response.getCollaboratorKey()))
			{
				Set<TaskResponse> reses = Sets.newHashSet();
				reses.add(response);
				this.responses.put(response.getCollaboratorKey(), reses);
			}
			else
			{
				this.responses.get(response.getCollaboratorKey()).add(response);
			}
			if (this.responses.get(response.getCollaboratorKey()).size() >= this.syncs.get(response.getCollaboratorKey()).getCD())
			{
				this.syncs.get(response.getCollaboratorKey()).signal();
			}
		}
	}
	
	public boolean isResponseExisted(String collaboratorKey)
	{
		return this.responses.containsKey(collaboratorKey);
	}
	
	public TaskResponse getResponse(String collaboratorKey)
	{
		TaskResponse response = Rand.getRandomSetElement(this.responses.get(collaboratorKey));
		this.syncs.remove(response.getCollaboratorKey());
		this.responses.remove(collaboratorKey);
		return response;
	}
	
	public Set<TaskResponse> getResponses(String collaboratorKey)
	{
		Set<TaskResponse> responses = this.responses.get(collaboratorKey);
		this.syncs.remove(collaboratorKey);
		this.responses.remove(collaboratorKey);
		return responses;
	}
}
