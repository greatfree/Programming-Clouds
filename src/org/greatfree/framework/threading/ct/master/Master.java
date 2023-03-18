package org.greatfree.framework.threading.ct.master;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.ExecuteNotification;
import org.greatfree.concurrency.threading.message.IsAliveRequest;
import org.greatfree.concurrency.threading.message.IsAliveResponse;
import org.greatfree.concurrency.threading.message.KillNotification;
import org.greatfree.concurrency.threading.message.ATMThreadRequest;
import org.greatfree.concurrency.threading.message.ATMThreadResponse;
import org.greatfree.concurrency.threading.message.ShutdownSlaveNotification;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.container.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.peer.ChatMaintainer;
import org.greatfree.framework.threading.ThreadInfo;
import org.greatfree.framework.threading.message.PrintTaskNotification;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

// Created: 09/16/2019, Bing Li
class Master
{
	private PeerContainer master;
	
	private String threadKey;
	
	private Date startTime;
	private Date endTime;

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
	
	public void setStartTime()
	{
		this.startTime = Calendar.getInstance().getTime();
	}
	
	public Date getStartTime()
	{
		return this.startTime;
	}
	
	public void setEndTime()
	{
		this.endTime = Calendar.getInstance().getTime();
	}
	
	public Date getEndTime()
	{
		return this.endTime;
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
		ServerStatus.FREE().setShutdown();		
		// Set the terminating signal. 11/25/2014, Bing Li
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
		this.master.stop(timeout);
	}
	
	public void start(String masterName, String slaveName, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
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
	
	public void assignTasks() throws ClassNotFoundException, RemoteReadException, InterruptedException, RemoteIPNotExistedException, IOException
	{
		System.out.println("Thread master assigning tasks ...");
		String t = ((ATMThreadResponse)this.master.read(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ATMThreadRequest())).getThreadKey();
		
		this.threadKey = t;

		ThreadInfo.ASYNC().init(t);

		// The parameter, taskKey, is empty. In the case, only one task is available for all of threads. So the taskKey does not make sense. 09/13/2019, Bing Li
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ExecuteNotification(t));

		this.setStartTime();
		
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new PrintTaskNotification(t, "I am one Thread", ThreadConfig.TIMEOUT));
		System.out.println("Thread master tasks assigned ...");
	}
	
	public void assignTask(String threadKey) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		System.out.println("Thread master assigning task to " + ThreadInfo.ASYNC().getThreadName(threadKey));
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new PrintTaskNotification(threadKey, "I am " + ThreadInfo.ASYNC().getThreadName(threadKey), ThreadConfig.TIMEOUT));
		System.out.println("Thread master task assigned to " + ThreadInfo.ASYNC().getThreadName(threadKey));
	}
	
	public boolean isAlive(String threadKey) throws ClassNotFoundException, RemoteReadException, RemoteIPNotExistedException
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
	
	public void kill() throws IOException, InterruptedException
	{
		System.out.println("Thread master killing " + ThreadInfo.ASYNC().getThreadName(threadKey));
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new KillNotification(this.threadKey, ThreadConfig.TIMEOUT));
		System.out.println("Thread master killed " + ThreadInfo.ASYNC().getThreadName(threadKey));
	}
	
	public void shutdown() throws IOException, InterruptedException
	{
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new ShutdownSlaveNotification(ThreadConfig.TIMEOUT));
	}

	/*
	public void wait(String threadKey) throws IOException, InterruptedException
	{
		System.out.println("Thread master asking " + ThreadInfo.ASYNC().getThreadName(threadKey) + " to await ...");
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new WaitNotification(threadKey));
		System.out.println("Thread master asking " + ThreadInfo.ASYNC().getThreadName(threadKey) + " awaited ...");
	}
	*/

	/*
	 * The method is not necessary since the thread is signaled when new messages are received. 09/18/2019, Bing Li
	 */
	/*
	public void signal(String threadKey) throws IOException, InterruptedException
	{
		System.out.println("Thread master waking up " + ThreadInfo.ASYNC().getThreadName(threadKey));
		this.master.syncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new SignalNotification(threadKey));
		System.out.println("Thread master waked up " + ThreadInfo.ASYNC().getThreadName(threadKey));
	}
	*/

}
