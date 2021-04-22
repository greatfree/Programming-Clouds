package org.greatfree.framework.threading.ct.slave;

import java.io.IOException;

import org.greatfree.concurrency.threading.ThreadConfig;
import org.greatfree.concurrency.threading.message.TaskStateNotification;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.peer.ChatMaintainer;
import org.greatfree.server.container.PeerContainer;
import org.greatfree.server.container.ServerTask;
import org.greatfree.util.ServerStatus;
import org.greatfree.util.TerminateSignal;

/*
 * The CT/ct represents "Controlling Threads". 09/16/2019, Bing Li
 */

// Created: 09/10/2019, Bing Li
class Slave
{
	private PeerContainer slave;
	
	private Slave()
	{
	}
	
	private static Slave instance = new Slave();
	
	public static Slave SLAVE()
	{
		if (instance == null)
		{
			instance = new Slave();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		DistributedThreadPool.POOL().dispose(timeout);
		ChatMaintainer.PEER().dispose();
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();
		this.slave.stop(timeout);
	}

	public void start(String slaveName, String masterName, ServerTask task) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.slave = new PeerContainer(slaveName, ThreadConfig.THREAD_PORT, task, true);
		this.slave.start();

		DistributedThreadPool.POOL().init();
		
		ChatMaintainer.PEER().init(slaveName, masterName);
		ServerStatus.FREE().init();
	}
	
	private void obtainMasterAddress() throws ClassNotFoundException, RemoteReadException, IOException
	{
		ChatPartnerResponse response = (ChatPartnerResponse)this.slave.read(RegistryConfig.PEER_REGISTRY_ADDRESS, RegistryConfig.PEER_REGISTRY_PORT, new ChatPartnerRequest(ChatMaintainer.PEER().getPartnerKey()));
		ChatMaintainer.PEER().setPartnerIP(response.getIP());
		ChatMaintainer.PEER().setPartnerPort(response.getPort());
		ServerStatus.FREE().addServerID(ChatMaintainer.PEER().getPartnerKey());
		ServerStatus.FREE().addServerID(ChatMaintainer.PEER().getLocalUserKey());
	}

	public void notifyState(String threadKey, String taskKey, int instructType, String instructKey, boolean isDone) throws ClassNotFoundException, RemoteReadException, IOException
	{
		if (ChatMaintainer.PEER().getPartnerIP() == null)
		{
//			System.out.println("ThreadSlave-notifyState(): ......");
			this.obtainMasterAddress();
		}
//		System.out.println("ThreadSlave-notifyState(): ...... partner IP = " + ChatMaintainer.PEER().getPartnerIP());
		// The parameter, taskKey, is empty. In the case, only one task is available for all of threads. So the taskKey does not make sense. 09/13/2019, Bing Li
		this.slave.asyncNotify(ChatMaintainer.PEER().getPartnerIP(), ChatMaintainer.PEER().getPartnerPort(), new TaskStateNotification(threadKey, taskKey, instructType, instructKey, isDone));
	}
}

