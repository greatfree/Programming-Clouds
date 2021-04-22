package org.greatfree.app.p2p;

import java.io.IOException;

import org.greatfree.app.p2p.message.GreetingRequest;
import org.greatfree.app.p2p.message.GreetingResponse;
import org.greatfree.app.p2p.message.HelloNotification;
import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.framework.p2p.message.ChatPartnerRequest;
import org.greatfree.framework.p2p.message.ChatPartnerResponse;
import org.greatfree.framework.p2p.message.ChatRegistryRequest;
import org.greatfree.framework.p2p.message.ChatRegistryResponse;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

// Created: 08/19/2018, Bing Li
class PeerSingleton
{
	private Peer<InstantDispatcher> im;
	
	private PeerSingleton()
	{
	}
	
	private static PeerSingleton instance = new PeerSingleton();
	
	public static PeerSingleton PEER()
	{
		if (instance == null)
		{
			instance = new PeerSingleton();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws IOException, InterruptedException, ClassNotFoundException, RemoteReadException
	{
		// Set the terminating signal. 11/25/2014, Bing Li
		TerminateSignal.SIGNAL().setTerminated();

		this.im.stop(timeout);
	}

	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.im = new Peer.PeerBuilder<InstantDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new InstantDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new InstantDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.syncEventerIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.syncEventerIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.syncEventerMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
//				.clientThreadPoolSize(RegistryConfig.CLIENT_THREAD_POOL_SIZE)
//				.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
//				.asyncEventerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.build();

		this.im.start();
	}

	public ChatRegistryResponse registerChat(String localUserKey, String localUserName, String description, String preference) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ChatRegistryResponse)this.im.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatRegistryRequest(localUserKey, localUserName, description, preference));
	}

	public ChatPartnerResponse searchUser(String userKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (ChatPartnerResponse)this.im.read(RegistryConfig.PEER_REGISTRY_ADDRESS, ChatConfig.CHAT_REGISTRY_PORT, new ChatPartnerRequest(userKey));
	}

	public GreetingResponse greet(String ip, int port, String greeting) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (GreetingResponse)this.im.read(ip, port, new GreetingRequest(greeting));
	}
	
	public void hello(String ip, int port, String helloWorld)
	{
		this.im.asyncNotify(ip, port, new HelloNotification(helloWorld));
	}
	
}
