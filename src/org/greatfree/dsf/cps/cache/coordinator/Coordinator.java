package org.greatfree.dsf.cps.cache.coordinator;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.greatfree.concurrency.Scheduler;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCacheTiming;
import org.greatfree.dsf.cps.cache.data.MyData;
import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMinMyPointingRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMinMyPointingResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMuchMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMuchMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingsRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataByKeysResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyDataResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingByIndexRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingByIndexResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingByKeyRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingByKeyResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingsRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyPointingsResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyStoreDataKeysRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyStoreDataKeysResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyUKValueByIndexRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyUKValueByIndexResponse;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyUKValuesRequest;
import org.greatfree.dsf.cps.cache.message.postfetch.PostfetchMyUKValuesResponse;
import org.greatfree.dsf.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.dsf.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsRequest;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyPointingsRequest;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyPointingsResponse;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyUKValuesRequest;
import org.greatfree.dsf.cps.cache.message.prefetch.PrefetchMyUKValuesResponse;
import org.greatfree.dsf.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.EnqueueMyStoreDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.PushMuchMyStoreDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.PushMyStoreDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateCachePointingNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateCachePointingsNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMuchMyDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMuchMyStoreDataMapStoreNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMyDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMyPointingNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMyPointingsNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMyStoreDataMapStoreNotification;
import org.greatfree.dsf.cps.cache.message.replicate.ReplicateMyUKValueNotification;
import org.greatfree.dsf.cps.threetier.message.CoordinatorNotification;
import org.greatfree.dsf.cps.threetier.message.CoordinatorRequest;
import org.greatfree.dsf.cps.threetier.message.CoordinatorResponse;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.CSServer;
import org.greatfree.server.Peer;
import org.greatfree.testing.cache.local.MyUKValue;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.UtilConfig;

// Created: 07/06/2018, Bing Li
public class Coordinator
{
	private Peer<CoordinatorDispatcher> peer;
	private CSServer<ManCoordinatorDispatcher> manServer;
	
	public Coordinator()
	{
	}
	
	private static Coordinator instance = new Coordinator();
	
	public static Coordinator CPS()
	{
		if (instance == null)
		{
			instance = new Coordinator();
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
		
		Scheduler.GREATFREE().shutdown(ServerConfig.SCHEDULER_SHUTDOWN_TIMEOUT);
		SharedThreadPool.SHARED().dispose(ServerConfig.MR_SHUTDOWN_TIME);
		
		MyDistributedMap.MIDDLE().dispose();
		MyDistributedList.MIDDLE().dispose();
		MySortedDistributedList.MIDDLE().dispose();
		MySortedDistributedMap.MIDDLE().dispose();
		MyReadDistributedMap.MIDDLE().dispose();
		MySortedDistributedCacheStore.MIDDLESTORE().dispose();
		MySortedPrefetchListStore.MIDDLE().dispose();
		MyDistributedStackStore.MIDDLESTORE().dispose();
		MyDistributedQueueStore.MIDDLESTORE().dispose();
		MyTimingDistributedCacheStore.MIDDLESTORE().dispose();
		MyDistributedReadStackStore.MIDDLESTORE().dispose();
		MySortedDistributedReadCacheStore.MIDDLESTORE().dispose();
		MyDistributedMapStore.MIDDLE().dispose();
		
		this.peer.stop(timeout);
		this.manServer.stop(timeout);
	}
	
	public void start(String username) throws IOException, ClassNotFoundException, RemoteReadException
	{
		// I am not sure whether the below line solves the exception or not, i.e.,  Comparison method violates its general contract!. 08/12/2018, Bing Li
		System.setProperty(UtilConfig.MERGE_SORT, UtilConfig.TRUE);

		// Initialize the peer. 06/05/2017, Bing Li
		this.peer = new Peer.PeerBuilder<CoordinatorDispatcher>()
				.peerPort(ServerConfig.COORDINATOR_PORT)
				.peerName(username)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(false)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
//				.dispatcher(new CoordinatorDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.dispatcher(new CoordinatorDispatcher(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
				.build();
		
		// Start the peer. 04/30/2017, Bing Li
		this.peer.start();

		// For the three-node CPS infrastructure, it is unnecessary to set up a registry server. So the below line can be removed. 07/06/2018, Bing Li
		// Get the port for the management server. It is required when multiple peers run on the same node. 05/12/2017, Bing Li
//		PortResponse response = (PortResponse)this.peer.read(ChatConfig.CHAT_REGISTRY_ADDRESS, UtilConfig.PEER_REGISTRY_PORT, new PortRequest(ChatMaintainer.PEER().getLocalUserKey(), ChatConfig.PEER_ADMIN_PORT_KEY, this.peer.getPeerIP(), ChatConfig.CHAT_ADMIN_PORT));

		// Initialize the chat management server. 04/30/2017, Bing Li
		this.manServer = new CSServer.CSServerBuilder<ManCoordinatorDispatcher>()
				.port(ServerConfig.COORDINATOR_ADMIN_PORT)
				.listenerCount(ServerConfig.SINGLE_THREAD_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new ManCoordinatorDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new ManCoordinatorDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
				.build();

		// Start the management server. 04/30/2017, Bing Li
		this.manServer.start();

		Scheduler.GREATFREE().init(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME);
		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);

		// Initialize the cache of DistributedMap. 07/09/2018, Bing Li
		MyDistributedMap.MIDDLE().init();
		
		// Initialize the cache of DistributedList. 03/01/2019, Bing Li
		MyDistributedList.MIDDLE().init();
		
		// Initialize the cache of PointingDistributedList. 07/09/2018, Bing Li
		MySortedDistributedList.MIDDLE().init();
		
		// Initialize the cache of PointingDistributedMap. 07/20/2018, Bing Li
		MySortedDistributedMap.MIDDLE().init();
		
		// Initialize the cache of PostDistributedMap. 07/09/2018, Bing Li
		MyReadDistributedMap.MIDDLE().init();
		
		// Initialize the cache of PointingDistributedCacheStore. 07/26/2018, Bing Li
		MySortedDistributedCacheStore.MIDDLESTORE().init();
		
		// Initialize the cache of PointingPrefetchListStore. 08/04/2018, Bing Li
		MySortedPrefetchListStore.MIDDLE().init();
		
		// Initialize the cache of DistributedStackStore. 08/09/2018, Bing Li
		MyDistributedStackStore.MIDDLESTORE().init();
		
		// Initialize the cache of DistributedQueueStore. 08/14/2018, Bing Li
		MyDistributedQueueStore.MIDDLESTORE().init();
		
		// Initialize the cache of TimingDistributedCacheStore. 08/21/2018, Bing Li
		MyTimingDistributedCacheStore.MIDDLESTORE().init();
		
		// Initialize the cache of DistributedReadStackStore. 08/21/2018, Bing Li
		MyDistributedReadStackStore.MIDDLESTORE().init();
		
		// Initialize the cache of PointingDistributedReadCacheStore. 08/21/2018, Bing Li
		MySortedDistributedReadCacheStore.MIDDLESTORE().init();
		
		// Initialize the cache of DistributedMapStore. 08/21/2018, Bing Li
		MyDistributedMapStore.MIDDLE().init();
	}

	public void notify(String notification) throws IOException, InterruptedException
	{
//		System.out.println("Coordinator notify(): " + notification + " is being sent to the terminal ...");
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new CoordinatorNotification(notification));
//		System.out.println("Coordinator notify(): " + notification + " is being sent to the terminal DONE ...");
	}
	
	public CoordinatorResponse query(String query) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (CoordinatorResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new CoordinatorRequest(query));
	}
	
	public void replicate(MyData data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyDataNotification(data));
	}
	
	public void replicate(Map<String, MyData> data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMuchMyDataNotification(data));
	}
	
	public PostfetchMyDataResponse postfetchMyData(String myDataKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		System.out.println("Coordinator-postfetch(): myDataKey = " + myDataKey);
		return (PostfetchMyDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyDataRequest(myDataKey));
	}
	
	public PostfetchMyStoreDataResponse postfetchMyStoreData(String mapKey, String key) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyStoreDataRequest(mapKey, key));
	}

	public PostfetchMyStoreDataKeysResponse postfetchMyStoreDataKeys(String mapKey, long size) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyStoreDataKeysResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyStoreDataKeysRequest(mapKey, (int)size));
	}

	public PostfetchMuchMyStoreDataResponse postfetchMyStoreData(String mapKey, Set<String> keys) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMuchMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMuchMyStoreDataRequest(mapKey, keys));
	}
	
	public PostfetchMyDataByKeysResponse postfetchMyData(Set<String> keys) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyDataByKeysResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyDataByKeysRequest(keys));
	}
	
	public void replicate(MyPointing pointing) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyPointingNotification(pointing));
	}
	
	public void replicate(List<MyPointing> pointings) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyPointingsNotification(pointings));
	}
	
	public void replicate(MyUKValue v) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyUKValueNotification(v));
	}
	
	public void replicateUKs(List<MyUKValue> vs) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyUKValueNotification(vs));
	}

	/*
	public void replicateMap(MyPointing pointing) throws IOException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyPointingMapNotification(pointing));
	}
	
	public void replicateMap(List<MyPointing> pointings) throws IOException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyPointingsMapNotification(pointings));
	}
	*/

	public PrefetchMyPointingsResponse prefetch(int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PrefetchMyPointingsResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PrefetchMyPointingsRequest(startIndex, endIndex));
	}
	
	public PrefetchMyUKValuesResponse prefetchUK(int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PrefetchMyUKValuesResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PrefetchMyUKValuesRequest(startIndex, endIndex));
	}

	public PostfetchMyPointingByKeyResponse postfetchMyPointing(String resourceKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyPointingByKeyResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyPointingByKeyRequest(resourceKey));
	}
	
	public PostfetchMinMyPointingResponse postfetchMinMyPointing() throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMinMyPointingResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMinMyPointingRequest());
	}
	
//	public PostfetchMyPointingsResponse postfetchMyPointing(int index, int postfetchCount) throws ClassNotFoundException, RemoteReadException, IOException
	public PostfetchMyPointingsResponse postfetchTopMyPointing(int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return (PostfetchMyPointingsResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyPointingsRequest(index, postfetchCount));
		return (PostfetchMyPointingsResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyPointingsRequest(0, endIndex));
	}
	
	public PostfetchMyUKValuesResponse postfetch(int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyUKValuesResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyUKValuesRequest(startIndex, endIndex));
	}
	
	public PostfetchMyPointingByIndexResponse postfetchMyPointing(int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyPointingByIndexResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyPointingByIndexRequest(index));
	}
	
	public PostfetchMyUKValueByIndexResponse postfetchMyUK(int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyUKValueByIndexResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyUKValueByIndexRequest(index));
	}

	public void replicate(MyCachePointing pointing) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateCachePointingNotification(pointing, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}

	public void replicate(MyCacheTiming timing) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateCachePointingNotification(timing));
	}

	public void replicateCachePointings(String mapKey, List<MyCachePointing> pointings) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateCachePointingsNotification(mapKey, pointings, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}

	public void replicateCacheTimings(String mapKey, List<MyCacheTiming> timings) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateCachePointingsNotification(timings, mapKey));
	}
	
	public void replicateMyStoreData(MyStoreData data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMyStoreDataMapStoreNotification(data));
	}
	
	public void replicateMyStoreData(String mapKey, Map<String, MyStoreData> data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new ReplicateMuchMyStoreDataMapStoreNotification(mapKey, data));
	}
	
	public PrefetchMyCachePointingsResponse prefetchPointings(String mapKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PrefetchMyCachePointingsResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PrefetchMyCachePointingsRequest(mapKey, startIndex, endIndex, false, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}
	
	public PrefetchMyCachePointingsResponse prefetchTimings(String mapKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PrefetchMyCachePointingsResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PrefetchMyCachePointingsRequest(mapKey, startIndex, endIndex, true, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}

	public PostfetchMyCachePointingByIndexResponse postfetchMyCachePointing(String mapKey, int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyCachePointingByIndexResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyCachePointingByIndexRequest(mapKey, index, false, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}

	public PostfetchMyCachePointingByIndexResponse postfetchMyCacheTiming(String mapKey, int index) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyCachePointingByIndexResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyCachePointingByIndexRequest(mapKey, index, true, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}
	
	public PostfetchMyCachePointingByKeyResponse postfetchMyCachePointing(String mapKey, String resourceKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyCachePointingByKeyResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyCachePointingByKeyRequest(mapKey, resourceKey, false, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}
	
	public PostfetchMyCachePointingByKeyResponse postfetchMyCacheTiming(String mapKey, String resourceKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyCachePointingByKeyResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyCachePointingByKeyRequest(mapKey, resourceKey, true, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}
	
	public PostfetchMyCachePointingsResponse postfetchMyCachePointings(String mapKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyCachePointingsResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyCachePointingsRequest(mapKey, startIndex, endIndex, false, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}
	
	public PostfetchMyCachePointingsResponse postfetchMyCacheTimings(String mapKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PostfetchMyCachePointingsResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PostfetchMyCachePointingsRequest(mapKey, startIndex, endIndex, true, MySortedDistributedCacheStore.MIDDLESTORE().isTerminalMap()));
	}
	
	public void push(MyStoreData data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PushMyStoreDataNotification(data));
	}

	public void push(String stackKey, List<MyStoreData> data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PushMuchMyStoreDataNotification(stackKey, data));
	}
	
//	public PopMyStoreDataResponse pop(String stackKey, int count, boolean isPeeking) throws ClassNotFoundException, RemoteReadException, IOException
	public PopMyStoreDataResponse pop(String stackKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
//		return (PopMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PopMyStoreDataRequest(stackKey, count, false, isPeeking));
		return (PopMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PopMyStoreDataRequest(stackKey, count, false, false));
	}
	
	public PopMyStoreDataResponse peekStack(String stackKey, int kickOutCount) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PopMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PopMyStoreDataRequest(stackKey, kickOutCount, false, true));
	}
	
	public PopMyStoreDataResponse peekStack(String stackKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (PopMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new PopMyStoreDataRequest(stackKey, startIndex, endIndex));
	}
	
	public void enqueue(MyStoreData data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new EnqueueMyStoreDataNotification(data));
	}

	public void enqueue(String queueKey, List<MyStoreData> data) throws IOException, InterruptedException
	{
		this.peer.syncNotify(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new EnqueueMuchMyStoreDataNotification(queueKey, data));
	}
	
	public DequeueMyStoreDataResponse dequeue(String queueKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (DequeueMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new DequeueMyStoreDataRequest(queueKey, count, false));
	}
	
	public DequeueMyStoreDataResponse peekQueue(String queueKey, int count) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (DequeueMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new DequeueMyStoreDataRequest(queueKey, count, true));
	}
	
	public DequeueMyStoreDataResponse peekQueue(String queueKey, int startIndex, int endIndex) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return (DequeueMyStoreDataResponse)this.peer.read(ServerConfig.TERMINAL_ADDRESS, ServerConfig.TERMINAL_PORT, new DequeueMyStoreDataRequest(queueKey, startIndex, endIndex));
	}
}
