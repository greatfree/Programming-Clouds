package org.greatfree.testing.cache.distributed.root;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.abandoned.cache.distributed.root.DistributedCacheRootDispatcher;
import org.greatfree.abandoned.cache.distributed.root.DistributedPersistableMap;
import org.greatfree.abandoned.cache.distributed.root.MapRegistry;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.multicast.MulticastConfig;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.Peer;
import org.greatfree.testing.cache.distributed.DistributedMapIntegerValueFactory;
import org.greatfree.testing.cache.distributed.IntegerValue;
import org.greatfree.testing.cache.distributed.StringKey;
import org.greatfree.util.TerminateSignal;

// Created: 07/17/2017, Bing Li
class DistributedRootMapSaveTester
{

	public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException, InstantiationException, IllegalAccessException
	{
		String rootPath = "/home/libing/Temp/";
		String cacheName = "IntegerRootMap";
		int cacheSize = 10;

		System.out.println("Distributed root map starting up ...");

		Peer<DistributedCacheRootDispatcher> root = new Peer.PeerBuilder<DistributedCacheRootDispatcher>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(MulticastConfig.CLUSTER_SERVER_ROOT_NAME)
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new DistributedCacheRootDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
//				.dispatcher(new DistributedCacheRootDispatcher(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
		
		root.start();

		/* It is done inside the DistributedPersistableMap. 07/18/2017, Bing Li
		// Retrieve all of the registered IP addresses of the distributed nodes in the cluster from the registry server. 05/08/2017, Bing Li
		ClusterIPResponse ipResponse = (ClusterIPResponse)root.read(ChatConfig.CHAT_REGISTRY_ADDRESS,  UtilConfig.PEER_REGISTRY_PORT, new ClusterIPRequest());
		// Add the IP addresses to the client pool. 05/08/2017, Bing Li
		for (IPAddress ip : ipResponse.getIPs().values())
		{
			System.out.println("Cluster IPs = " + ip.getIP() + ", " + ip.getPort());
			root.getClientPool().addIP(ip.getKey(), ip.getIP(), ip.getPort());
		}
		*/

//		MapRegistry<IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB> registry = new MapRegistry<IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>();
		MapRegistry<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB> registry = new MapRegistry<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>();

//		DistributedPersistableMap<IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB> map = new DistributedPersistableMap.DistributedPersistableMapBuilder<IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>()
		DistributedPersistableMap<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB> map = new DistributedPersistableMap.DistributedPersistableMapBuilder<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>()
				.factory(new DistributedMapIntegerValueFactory())
				.rootPath(rootPath)
				.cacheName(cacheName)
				.cacheSize(cacheSize)
				.offheapSizeInMB(1L)
				.diskSizeInMB(2)
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(rootPath, cacheName)))
				.peer(root)
				.rootBranchCount(2)
				.subBranchCount(2)
				.build();

		map.open(registry);

		System.out.println("Distributed root map started ...");

		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		
		System.out.println("Press any key to put data into the map ...");

		in.nextLine();

		String dataKey;
		for (int i = 0; i < 20000; i++)
		{
			dataKey = "00" + i;
			map.put(dataKey, new IntegerValue(dataKey, i));
			System.out.println(dataKey + ": " + i);
		}

		System.out.println("Data putting is done!");

		in.nextLine();
		
//		TerminateSignal.SIGNAL().setTerminated();
		TerminateSignal.SIGNAL().notifyAllTermination();

		// After the server is started, the loop check whether the flag of terminating is set. If the terminating flag is true, the process is ended. Otherwise, the process keeps running. 08/22/2014, Bing Li
		while (!TerminateSignal.SIGNAL().isTerminated())
		{
			try
			{
				// If the terminating flag is false, it is required to sleep for some time. Otherwise, it might cause the high CPU usage. 08/22/2014, Bing Li
				Thread.sleep(ServerConfig.TERMINATE_SLEEP);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}
		}

		map.close(registry, ServerConfig.SERVER_SHUTDOWN_TIMEOUT);

		root.stop(0);

		in.close();
	}

}
