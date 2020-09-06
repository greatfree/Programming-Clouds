package org.greatfree.dsf.cps.cache.front;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.greatfree.client.FreeClientPool;
import org.greatfree.client.SyncRemoteEventer;
import org.greatfree.data.ServerConfig;
import org.greatfree.dsf.cps.cache.data.MyCachePointing;
import org.greatfree.dsf.cps.cache.data.MyCacheTiming;
import org.greatfree.dsf.cps.cache.data.MyData;
import org.greatfree.dsf.cps.cache.data.MyPointing;
import org.greatfree.dsf.cps.cache.data.MyStoreData;
import org.greatfree.dsf.cps.cache.message.front.SaveCachePointingNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveCachePointingsNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMuchMyDataNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMuchUKsNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMyDataNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMyPointingListNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMyPointingMapNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMyPointingsListNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMyPointingsMapNotification;
import org.greatfree.dsf.cps.cache.message.front.SaveMyUKNotification;
import org.greatfree.dsf.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.EnqueueMyStoreDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.PushMuchMyStoreDataNotification;
import org.greatfree.dsf.cps.cache.message.replicate.PushMyStoreDataNotification;
import org.greatfree.dsf.cps.threetier.message.FrontNotification;
import org.greatfree.dsf.p2p.RegistryConfig;
import org.greatfree.testing.cache.local.MyUKValue;

// Created: 07/06/2018, Bing Li
class FrontEventer
{
	// The notification of ChatNotification is sent to the chatting server in an asynchronous manner. 04/27/2017, Bing Li
	private SyncRemoteEventer<FrontNotification> fronter;
	
	/*
	 * Save data into distributed caches. 07/24/2018, Bing Li
	 */
	private SyncRemoteEventer<SaveMyDataNotification> saveMyDataEventer;
	private SyncRemoteEventer<SaveMuchMyDataNotification> saveMuchMyDataEventer;
	
	private SyncRemoteEventer<SaveMyPointingListNotification> saveMyPointingListEventer;
	private SyncRemoteEventer<SaveMyPointingsListNotification> saveMyPointingsListEventer;
	
	private SyncRemoteEventer<SaveMyPointingMapNotification> saveMyPointingMapEventer;
	private SyncRemoteEventer<SaveMyPointingsMapNotification> saveMuchMyPointingMapEventer;
	
	/*
	 * Save data into distributed stores. 07/24/2018, Bing Li
	 */
	private SyncRemoteEventer<SaveCachePointingNotification> saveMyCachePointingEventer;
	private SyncRemoteEventer<SaveCachePointingsNotification> saveMyCachePointingsEventer;
	
	private SyncRemoteEventer<PushMyStoreDataNotification> pushMyStoreDataEventer;
	private SyncRemoteEventer<PushMuchMyStoreDataNotification> pushMuchMyStoreDataEventer;
	
	private SyncRemoteEventer<EnqueueMyStoreDataNotification> enqueueMyStoreDataEventer;
	private SyncRemoteEventer<EnqueueMuchMyStoreDataNotification> enqueueMuchMyStoreDataEventer;
	
	private SyncRemoteEventer<SaveMyUKNotification> saveMyUKEventer;
	private SyncRemoteEventer<SaveMuchUKsNotification> saveUKsEventer;
	
	// The TCP client pool that sends notifications to the chatting server. 05/27/2017, Bing Li
	private FreeClientPool clientPool;

	private FrontEventer()
	{
	}

	/*
	 * A singleton implementation. 11/07/2014, Bing Liaa
	 */
	private static FrontEventer instance = new FrontEventer();
	
	public static FrontEventer RE()
	{
		if (instance == null)
		{
			instance = new FrontEventer();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose(long timeout) throws IOException
	{
		this.fronter.dispose();
		this.saveMyDataEventer.dispose();
		this.saveMuchMyDataEventer.dispose();
		
		this.saveMyPointingListEventer.dispose();
		this.saveMyPointingsListEventer.dispose();
		
		this.saveMyPointingMapEventer.dispose();
		this.saveMuchMyPointingMapEventer.dispose();
		
		this.saveMyCachePointingEventer.dispose();
		this.saveMyCachePointingsEventer.dispose();
		
		this.pushMyStoreDataEventer.dispose();
		this.pushMuchMyStoreDataEventer.dispose();
		
		this.enqueueMyStoreDataEventer.dispose();
		this.enqueueMuchMyStoreDataEventer.dispose();
		
		this.saveMyUKEventer.dispose();
		this.saveUKsEventer.dispose();
		
		this.clientPool.dispose();
	}
	
	public void init()
	{
		 // Initialize the TCP client pool to send messages efficiently. 02/02/2016, Bing Li
		this.clientPool = new FreeClientPool(RegistryConfig.CLIENT_POOL_SIZE);
		// Set idle checking for the client pool. 04/17/2017, Bing Li
		this.clientPool.setIdleChecker(RegistryConfig.CLIENT_IDLE_CHECK_DELAY, RegistryConfig.CLIENT_IDLE_CHECK_PERIOD, RegistryConfig.CLIENT_MAX_IDLE_TIME);

		this.fronter = new SyncRemoteEventer<FrontNotification>(this.clientPool);
		this.saveMyDataEventer = new SyncRemoteEventer<SaveMyDataNotification>(this.clientPool);
		this.saveMuchMyDataEventer = new SyncRemoteEventer<SaveMuchMyDataNotification>(this.clientPool);
		
		this.saveMyPointingListEventer = new SyncRemoteEventer<SaveMyPointingListNotification>(this.clientPool);
		this.saveMyPointingsListEventer = new SyncRemoteEventer<SaveMyPointingsListNotification>(this.clientPool);
		
		this.saveMyPointingMapEventer = new SyncRemoteEventer<SaveMyPointingMapNotification>(this.clientPool);
		this.saveMuchMyPointingMapEventer = new SyncRemoteEventer<SaveMyPointingsMapNotification>(this.clientPool);
		
		this.saveMyCachePointingEventer = new SyncRemoteEventer<SaveCachePointingNotification>(this.clientPool);
		this.saveMyCachePointingsEventer = new SyncRemoteEventer<SaveCachePointingsNotification>(this.clientPool);
		
		this.pushMyStoreDataEventer = new SyncRemoteEventer<PushMyStoreDataNotification>(this.clientPool);
		this.pushMuchMyStoreDataEventer = new SyncRemoteEventer<PushMuchMyStoreDataNotification>(this.clientPool);
		
		this.enqueueMyStoreDataEventer = new SyncRemoteEventer<EnqueueMyStoreDataNotification>(this.clientPool);
		this.enqueueMuchMyStoreDataEventer = new SyncRemoteEventer<EnqueueMuchMyStoreDataNotification>(this.clientPool);
		
		this.saveMyUKEventer = new SyncRemoteEventer<SaveMyUKNotification>(this.clientPool);
		this.saveUKsEventer = new SyncRemoteEventer<SaveMuchUKsNotification>(this.clientPool);
	}
	
	public void notify(String notification) throws IOException, InterruptedException
	{
		this.fronter.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new FrontNotification(notification));
	}
	
	public void saveDataIntoDistributedMap(MyData data) throws IOException, InterruptedException
	{
		this.saveMyDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMyDataNotification(data));
	}
	
	public void saveDataIntoDistributedMapStore(String mapKey, MyStoreData data) throws IOException, InterruptedException
	{
		this.saveMyDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMyDataNotification(mapKey, data));
	}
	
	public void saveDataIntoDistributedMap(Map<String, MyData> data) throws IOException, InterruptedException
	{
		this.saveMuchMyDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMuchMyDataNotification(data));
	}
	
	public void saveDataIntoDistributedMapStore(String mapKey, Map<String, MyStoreData> data) throws IOException, InterruptedException
	{
		this.saveMuchMyDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMuchMyDataNotification(mapKey, data));
	}
	
	public void saveMyPointingIntoPointingDistributedList(MyPointing pointing) throws IOException, InterruptedException
	{
		this.saveMyPointingListEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMyPointingListNotification(pointing));
	}
	
	public void saveMyPointingsIntoPointingDistributedList(List<MyPointing> pointings) throws IOException, InterruptedException
	{
		this.saveMyPointingsListEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMyPointingsListNotification(pointings));
	}
	
	public void saveMyPointingIntoPointingDistributedMap(MyPointing pointing) throws IOException, InterruptedException
	{
		this.saveMyPointingMapEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMyPointingMapNotification(pointing));
	}
	
	public void saveMyPointingsIntoPointingDistributedMap(List<MyPointing> pointings) throws IOException, InterruptedException
	{
		this.saveMuchMyPointingMapEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMyPointingsMapNotification(pointings));
	}
	
	public void saveCachePointingIntoPointingDistributedCacheStore(String mapKey, MyCachePointing pointing) throws IOException, InterruptedException
	{
		this.saveMyCachePointingEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveCachePointingNotification(mapKey, pointing));
	}
	
	public void saveCacheTimingIntoTimingDistributedCacheStore(String mapKey, MyCacheTiming timing) throws IOException, InterruptedException
	{
		this.saveMyCachePointingEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveCachePointingNotification(mapKey, timing));
	}
	
	public void saveCachePointingsIntoPointingDistributedCacheStore(String mapKey, List<MyCachePointing> pointings) throws IOException, InterruptedException
	{
		this.saveMyCachePointingsEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveCachePointingsNotification(mapKey, pointings));
	}
	
	public void saveCacheTimingsIntoTimingingDistributedCacheStore(String mapKey, List<MyCacheTiming> timings) throws IOException, InterruptedException
	{
		this.saveMyCachePointingsEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveCachePointingsNotification(timings, mapKey));
	}
	
	public void push(MyStoreData data) throws IOException, InterruptedException
	{
		this.pushMyStoreDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PushMyStoreDataNotification(data));
	}
	
	public void push(String stackKey, List<MyStoreData> data) throws IOException, InterruptedException
	{
		this.pushMuchMyStoreDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new PushMuchMyStoreDataNotification(stackKey, data));
	}
	
	public void enqueue(MyStoreData data) throws IOException, InterruptedException
	{
		this.enqueueMyStoreDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new EnqueueMyStoreDataNotification(data));
	}
	
	public void enqueue(String queueKey, List<MyStoreData> data) throws IOException, InterruptedException
	{
		this.enqueueMuchMyStoreDataEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new EnqueueMuchMyStoreDataNotification(queueKey, data));
	}
	
	public void saveUKIntoDistributedList(MyUKValue uk) throws IOException, InterruptedException
	{
		this.saveMyUKEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMyUKNotification(uk));
	}
	
	public void saveUKsIntoDistributedList(List<MyUKValue> uks) throws IOException, InterruptedException
	{
		this.saveUKsEventer.notify(ServerConfig.COORDINATOR_ADDRESS, ServerConfig.COORDINATOR_PORT, new SaveMuchUKsNotification(uks));
	}
}
