package org.greatfree.framework.streaming.unicast.child;

import java.io.IOException;

import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.multicast.child.ChildMulticastor;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.streaming.broadcast.child.RootMulticastor;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.Peer;
import org.greatfree.util.IPAddress;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 03/22/2020, Bing Li
class ChildPeer
{
	private Peer<ChildDispatcher> peer;

	private IPAddress rootAddress;

	private ChildPeer()
	{
	}
	
	private static ChildPeer instance = new ChildPeer();
	
	public static ChildPeer UNICAST()
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

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		this.peer.stop(timeout);
		ChildMulticastor.CHILD().stop();
		RootMulticastor.CHILD_STREAM().stop();

//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();
	}

	public void start() throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder<ChildDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
				.dispatcher(new ChildDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();

		this.peer.start();

		ChildMulticastor.CHILD().start(this.peer.getLocalIPKey(), this.peer.getClientPool(), MulticastConfig.SUB_BRANCH_COUNT, this.peer.getPool());

		RootMulticastor.CHILD_STREAM().start(this.peer.getClientPool(), MulticastConfig.ROOT_BRANCH_COUNT, MulticastConfig.SUB_BRANCH_COUNT, MulticastConfig.BROADCAST_REQUEST_WAIT_TIME, this.peer.getPool());
		
		this.peer.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(this.peer.getPeerID()));
	}

	public void setRootIP(IPAddress rootAddress)
	{
		this.rootAddress = rootAddress;
	}

	public void notifyRoot(ServerMessage notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(this.rootAddress.getIP(), this.rootAddress.getPort(), notification);
	}
}
