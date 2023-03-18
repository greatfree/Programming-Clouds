package org.greatfree.framework.multicast.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 09/08/2018, Bing Li
class ChildPeer
{
	private Peer<ChildDispatcher> peer;
	
	// The IP address of the cluster root. 06/15/2017, Bing Li
	private IPAddress rootAddress;

	private ChildPeer()
	{
	}
	
	private static ChildPeer instance = new ChildPeer();
	
	public static ChildPeer CHILD()
	{
		if (instance == null)
		{
			instance = new ChildPeer();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, RemoteIPNotExistedException
	{
		this.peer.stop(timeout);
		ChildMulticastor.CHILD().stop();

//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		// Initialize the child peer. 05/12/2017, Bing Li
		this.peer = new Peer.PeerBuilder<ChildDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new ChildDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ChildDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
//				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();

		// Start the child peer. 05/12/2017, Bing Li
		this.peer.start();

		// Start the client. 08/26/2018, Bing Li
		ChildMulticastor.CHILD().start(this.peer.getLocalIPKey(), this.peer.getClientPool(), MulticastConfig.SUB_BRANCH_COUNT, this.peer.getPool());

		// No. The line is useful although it is a little bit tedious. 10/09/2018, Bing Li
		// The below should not be useful in the multicasting DIP. 10/09/2018, Bing Li
		// Register the peer to the chatting registry. 06/15/2017, Bing Li
		this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.peer.getPeerID()));
	}

	/*
	 * Expose the child's IP. 06/17/2017, Bing Li
	 */
	public String getChildIP()
	{
		return this.peer.getPeerIP();
	}

	/*
	 * Expose the child's port. 06/17/2017, Bing Li
	 */
	public int getChildPort()
	{
		return this.peer.getPort();
	}

	/*
	 * Keep the root IP address. 05/20/2017, Bing Li
	 */
	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}
	
	/*
	 * Respond the response to the root of the cluster. 05/20/2017, Bing Li
	 */
	public void notifyRoot(ServerMessage notification) throws IOException, InterruptedException
	{
		/*
		if (notification == null)
		{
			System.out.println("ChildPeer-notifyRoot(): response is NULL");
		}
		if (this.rootAddress == null)
		{
			System.out.println("ChildPeer-notifyRoot(): rootAddress is NULL");
		}
		*/
		this.peer.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
}
