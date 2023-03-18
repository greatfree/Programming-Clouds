package org.greatfree.testing.cache.distributed.child;

import java.io.IOException;
import java.util.Scanner;

import org.greatfree.abandoned.cache.distributed.child.ChildMapRegistry;
import org.greatfree.abandoned.cache.distributed.child.DistributedCacheChildDispatcher;
import org.greatfree.abandoned.cache.distributed.child.DistributedPersistableChildMap;
import org.greatfree.cache.CacheConfig;
import org.greatfree.cache.db.StringKeyDB;
import org.greatfree.cache.db.StringKeyDBPool;
import org.greatfree.chat.ChatConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DuplicatePeerNameException;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.exceptions.ServerPortConflictedException;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.server.Peer;
import org.greatfree.testing.cache.distributed.DistributedMapIntegerValueFactory;
import org.greatfree.testing.cache.distributed.IntegerValue;
import org.greatfree.testing.cache.distributed.StringKey;
import org.greatfree.util.TerminateSignal;
import org.greatfree.util.Tools;

// Created: 07/18/2017, Bing Li
class DistributedChildMapSaveTester
{

	public static void main(String[] args) throws IOException, ClassNotFoundException, RemoteReadException, InstantiationException, IllegalAccessException, InterruptedException, DuplicatePeerNameException, RemoteIPNotExistedException, ServerPortConflictedException
	{
		String rootPath = "/home/libing/Temp/";
		String cacheName = "IntegerChildMap";
		String rootMapName = "IntegerRootMap";
		int cacheSize = 10;
		
		System.out.println("Distributed child map starting up ...");
		
		ChildMapRegistry<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB> registry = new ChildMapRegistry<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>();

		Peer<DistributedCacheChildDispatcher<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>> childPeer = new Peer.PeerBuilder<DistributedCacheChildDispatcher<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>>()
				.peerPort(ChatConfig.CHAT_SERVER_PORT)
				.peerName(Tools.generateUniqueKey())
				.registryServerIP(RegistryConfig.PEER_REGISTRY_ADDRESS)
				.registryServerPort(RegistryConfig.PEER_REGISTRY_PORT)
				.isRegistryNeeded(true)
				.listenerCount(ServerConfig.LISTENING_THREAD_COUNT)
				.maxIOCount(ServerConfig.MAX_SERVER_IO_COUNT)
//				.serverThreadPoolSize(ServerConfig.SHARED_THREAD_POOL_SIZE)
//				.serverThreadKeepAliveTime(ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME)
				.dispatcher(new DistributedCacheChildDispatcher<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME, registry))
//				.dispatcher(new DistributedCacheChildDispatcher<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME, registry))
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
		
		childPeer.start();

		DistributedPersistableChildMap<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB> childMap = new DistributedPersistableChildMap.DistributedPersistableChildMapBuilder<StringKey, IntegerValue, DistributedMapIntegerValueFactory, StringKeyDB>()
				.factory(new DistributedMapIntegerValueFactory())
				.rootPath(rootPath)
				.cacheName(cacheName)
				.rootMapName(rootMapName)
				.cacheSize(cacheSize)
				.offheapSizeInMB(1L)
				.diskSizeInMB(20)
				.db(StringKeyDBPool.SHARED().getDB(CacheConfig.getMapCacheKeyDBPath(rootPath, cacheName)))
				.peer(childPeer)
				.subBranchCount(2)
				.build();
		
		childMap.open(registry);

		System.out.println("Distributed child map started ...");

		// Initialize a command input console for users to interact with the system. 09/21/2014, Bing Li
		Scanner in = new Scanner(System.in);
		
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
		
		childMap.close(registry, ServerConfig.SERVER_SHUTDOWN_TIMEOUT);

		childPeer.stop(0);

		in.close();

	}

}
