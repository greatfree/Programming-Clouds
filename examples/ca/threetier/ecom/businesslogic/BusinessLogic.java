package ca.threetier.ecom.businesslogic;

import java.io.IOException;

import org.greatfree.data.ServerConfig;
import org.greatfree.dip.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer;
import org.greatfree.util.TerminateSignal;

import ca.dp.tncs.message.MerchandiseRequest;
import ca.dp.tncs.message.MerchandiseResponse;
import ca.dp.tncs.message.PlaceOrderNotification;
import ca.threetier.ecom.message.ThreeTierConfig;

// Created: 03/09/2020, Bing Li
class BusinessLogic
{
	private Peer<BusinessLogicDispatcher> peer;
	
	public BusinessLogic()
	{
	}
	
	private static BusinessLogic instance = new BusinessLogic();
	
	public static BusinessLogic CPS()
	{
		if (instance == null)
		{
			instance = new BusinessLogic();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		TerminateSignal.SIGNAL().setTerminated();
		this.peer.stop(timeout);
	}
	
	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		this.peer = new Peer.PeerBuilder<BusinessLogicDispatcher>()
				.peerPort(ThreeTierConfig.MIDDLE_TIER_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(false)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.dispatcher(new BusinessLogicDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.build();
		
		this.peer.start();
	}

	public void notify(PlaceOrderNotification notification) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ThreeTierConfig.BACKEND_TIER_IP, ThreeTierConfig.BACKEND_TIER_PORT, notification);
	}
	
	public MerchandiseResponse query(MerchandiseRequest query) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (MerchandiseResponse)this.peer.read(ThreeTierConfig.BACKEND_TIER_IP, ThreeTierConfig.BACKEND_TIER_PORT, query);
	}

}
