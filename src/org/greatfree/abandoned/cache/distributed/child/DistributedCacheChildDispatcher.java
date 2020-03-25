package org.greatfree.abandoned.cache.distributed.child;

import java.util.Calendar;

import org.greatfree.abandoned.cache.distributed.CacheKey;
import org.greatfree.abandoned.cache.distributed.CacheNotificationDispatcher;
import org.greatfree.cache.KeyLoadable;
import org.greatfree.cache.PersistableMapFactorable;
import org.greatfree.cache.message.CacheMessageType;
import org.greatfree.cache.message.PutNotification;
import org.greatfree.cache.message.UniGetRequest;
import org.greatfree.client.OutMessageStream;
import org.greatfree.data.ServerConfig;
import org.greatfree.dip.multicast.message.OldRootIPAddressBroadcastNotification;
import org.greatfree.dip.old.multicast.child.ChildDispatcher;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.MulticastMessageType;

/*
 * The dispatcher is used to serve for the distributed persistable map. When it is necessary to get data from the map, the value that is retrieved from the cluster is received by the dispatcher. 07/03/2017, Bing Li
 */

// Created: 07/08/2017, Bing Li
//public class DistributedCacheChildDispatcher<Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> extends ServerDispatcher
public class DistributedCacheChildDispatcher<Key extends CacheKey<String>, Value extends CacheKey<String>, Factory extends PersistableMapFactorable<String, Value>, DB extends KeyLoadable<String>> extends ChildDispatcher<ServerMessage>
{
//	private ChildMapRegistry<Value, Factory, DB> registry;

	private CacheNotificationDispatcher<PutNotification<Value>, PutUnicastNotificationThread<Key, Value, Factory, DB>, Key, Value, Factory, DB, PutUnicastNotificationThreadCreator<Key, Value, Factory, DB>> putValueNotificationDispatcher;
	
	private CacheNotificationDispatcher<UniGetRequest<Key>, UniGetRequestThread<Key, Value, Factory, DB>, Key, Value, Factory, DB, UniGetRequestThreadCreator<Key, Value, Factory, DB>> uniGetRequestNotificationDispatcher;

//	public DistributedCacheChildDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
//	public DistributedCacheChildDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime, ChildMapRegistry<Key, Value, Factory, DB> registry)
	public DistributedCacheChildDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime, ChildMapRegistry<Key, Value, Factory, DB> registry)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);
//		this.registry = new ChildMapRegistry<Value, Factory, DB>();
		
		this.putValueNotificationDispatcher = new CacheNotificationDispatcher.CacheNotificationDispatcherBuilder<PutNotification<Value>, PutUnicastNotificationThread<Key, Value, Factory, DB>, Key, Value, Factory, DB, PutUnicastNotificationThreadCreator<Key, Value, Factory, DB>>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new PutUnicastNotificationThreadCreator<Key, Value, Factory, DB>())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
//				.registry(this.registry)
				.registry(registry)
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();

		this.uniGetRequestNotificationDispatcher = new CacheNotificationDispatcher.CacheNotificationDispatcherBuilder<UniGetRequest<Key>, UniGetRequestThread<Key, Value, Factory, DB>, Key, Value, Factory, DB, UniGetRequestThreadCreator<Key, Value, Factory, DB>>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
//				.keepAliveTime(ServerConfig.NOTIFICATION_DISPATCHER_THREAD_ALIVE_TIME)
//				.threadPool(SharedThreadPool.SHARED().getPool())
				.threadCreator(new UniGetRequestThreadCreator<Key, Value, Factory, DB>())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.registry(registry)
//				.timeout(ServerConfig.THREAD_POOL_SHUTDOWN_TIMEOUT)
				.build();
	}

	/*
	public void registeMap(DistributedPersistableChildMap<Value, Factory, DB> map)
	{
		if (!this.registry.isExisted(map.getCacheKey()))
		{
			this.registry.put(map.getCacheKey(), map);
		}
	}
	*/
	
	public void shutdown(long timeout) throws InterruptedException
	{
		this.putValueNotificationDispatcher.dispose();
		this.uniGetRequestNotificationDispatcher.dispose();
		super.shutdownChild(timeout);
//		super.shutdown(timeout);
	}
	
	@SuppressWarnings("unchecked")
	public void consume(OutMessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case MulticastMessageType.ROOT_IPADDRESS_BROADCAST_NOTIFICATION:
				System.out.println("ROOT_IPADDRESS_BROADCAST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				super.broadcastRootIP((OldRootIPAddressBroadcastNotification)message.getMessage());
				break;

			case CacheMessageType.PUT_NOTIFICATION:
				System.out.println("PUT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.putValueNotificationDispatcher.isReady())
				{
					super.execute(this.putValueNotificationDispatcher);
				}
				this.putValueNotificationDispatcher.enqueue((PutNotification<Value>)message.getMessage());
				break;
				
			case CacheMessageType.UNI_GET_REQUEST:
				System.out.println("UNI_GET_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.uniGetRequestNotificationDispatcher.isReady())
				{
					super.execute(this.uniGetRequestNotificationDispatcher);
				}
				this.uniGetRequestNotificationDispatcher.enqueue((UniGetRequest<Key>)message.getMessage());
				break;
		}
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void process(OutMessageStream<ServerMessage> message)
	{
		// TODO Auto-generated method stub
		
	}
}
