package org.greatfree.dsf.threading.ttc.master;

import java.io.IOException;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.ExecuteNotification;
import org.greatfree.concurrency.threading.message.IsAliveRequest;
import org.greatfree.concurrency.threading.message.IsAliveResponse;
import org.greatfree.concurrency.threading.message.KillNotification;
import org.greatfree.concurrency.threading.message.NotificationThreadRequest;
import org.greatfree.concurrency.threading.message.NotificationThreadResponse;
import org.greatfree.concurrency.threading.message.ShutdownNotification;
import org.greatfree.dsf.container.p2p.message.ChatPartnerRequest;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.dsf.p2p.message.ChatPartnerResponse;
import org.greatfree.dsf.p2p.peer.ChatMaintainer;
import org.greatfree.dsf.threading.ThreadInfo;
import org.greatfree.dsf.threading.message.PrintTaskNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

// Created: 09/12/2019, Bing Li
class Master
{
	private PeerContainer master;

	private Master()
	{
	}
	
	private static Master instance = new Master();
	
	public static Master MASTER()
	{
		if (instance == null)
		{
			instance = new Master();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		ServerStatus.FREE().setShutdown();		
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		this.master.stop(timeout);
	}
	
	public void start(String masterName, String slaveName, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.master = new PeerContainer(masterName, ThreadConfig.THREAD_PORT, task, true);
		this.master.start();
		
		ChatMaintainer.PEER().init(masterName, slaveName);
		ChatPartnerResponse response = (ChatPartnerResponse)this.master.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ChatPartnerRequest(ChatMaintainer.PEER().getPartnerKey()));
		ChatMaintainer.PEER().setPartnerIP(response.getIP());
		ChatMaintainer.PEER().setPartnerPort(response.getPort());
		
		ServerStatus.FREE().init();
		ServerStatus.FREE().addServerID(ChatMaintainer.PEER().getPartnerKey());
		ServerStatus.FREE().addServerID(ChatMaintainer.PEER().getLocalUserKey());
	}
	
	public void assignTasks() throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		System.out.println("Thread master assigning tasks ...");
		String t1 = ((NotificationThreadResponse)this.master.read(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new NotificationThreadRequest())).getThreadKey();
		String t2 = ((NotificationThreadResponse)this.master.read(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new NotificationThreadRequest())).getThreadKey();

		ThreadInfo.ASYNC().init(t1, t2);

		// The parameter, taskKey, is empty. In the case, only one task is available for all of threads. So the taskKey does not make sense. 09/13/2019, Bing Li
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ExecuteNotification(t1));
		// The parameter, taskKey, is empty. In the case, only one task is available for all of threads. So the taskKey does not make sense. 09/13/2019, Bing Li
//		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ExecuteNotification(t2));

		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new PrintTaskNotification(t1, "I am t1", ThreadConfig.TIMEOUT));
//		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new PrintTaskNotification(t2, "I am t2", ThreadConfig.TIMEOUT));
		System.out.println("Thread master tasks assigned ...");
	}
	
	public void assignTask(String threadKey) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		System.out.println("Thread master assigning task to " + ThreadInfo.ASYNC().getThreadName(threadKey));
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new PrintTaskNotification(threadKey, "I am " + ThreadInfo.ASYNC().getThreadName(threadKey), ThreadConfig.TIMEOUT));
		System.out.println("Thread master task assigned to " + ThreadInfo.ASYNC().getThreadName(threadKey));
	}
	
	public boolean isAlive(String threadKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((IsAliveResponse)this.master.read(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new IsAliveRequest(threadKey))).isAlive();
	}

	public void execute(String threadKey) throws IOException, InterruptedException
	{
		System.out.println("Thread master executing task on " + ThreadInfo.ASYNC().getThreadName(threadKey));
		// The parameter, taskKey, is empty. In the case, only one task is available for all of threads. So the taskKey does not make sense. 09/13/2019, Bing Li
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ExecuteNotification(threadKey));
		System.out.println("Thread master task executed on " + ThreadInfo.ASYNC().getThreadName(threadKey));
	}

	/*
	 * The thread starts to wait automatically when no messages exist in the queue. So the below lines are not necessary. 09/18/2019, Bing Li
	 */
	/*
	public void wait(String threadKey) throws IOException, InterruptedException
	{
		System.out.println("Thread master asking " + ThreadInfo.ASYNC().getThreadName(threadKey) + " to await ...");
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new WaitNotification(threadKey));
		System.out.println("Thread master asking " + ThreadInfo.ASYNC().getThreadName(threadKey) + " awaited ...");
	}
	*/
	
	/*
	 * The thread is signaled automatically when messages are enqueued. So the below lines are not necessary. 09/18/2019, Bing Li
	 */
	/*
	public void notify(String threadKey) throws IOException, InterruptedException
	{
		System.out.println("Thread master waking up " + ThreadInfo.ASYNC().getThreadName(threadKey));
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new SignalNotification(threadKey));
		System.out.println("Thread master waked up " + ThreadInfo.ASYNC().getThreadName(threadKey));
	}
	*/
	
	public void kill(String threadKey) throws IOException, InterruptedException
	{
		System.out.println("Thread master killing " + ThreadInfo.ASYNC().getThreadName(threadKey));
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new KillNotification(threadKey, ThreadConfig.TIMEOUT));
		System.out.println("Thread master killed " + ThreadInfo.ASYNC().getThreadName(threadKey));
	}

	public void killAll() throws IOException, InterruptedException
	{
		System.out.println("Thread master killing all ... ");
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new KillNotification(ThreadInfo.ASYNC().getThreadAKey(), ThreadConfig.TIMEOUT));
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new KillNotification(ThreadInfo.ASYNC().getThreadBKey(), ThreadConfig.TIMEOUT));
		System.out.println("Thread master killed all ");
	}
	
	public void shutdown() throws IOException, InterruptedException
	{
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ShutdownNotification(ThreadConfig.TIMEOUT));
	}
}


