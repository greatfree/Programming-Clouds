package org.greatfree.framework.cps.cache.coordinator;

import java.util.Calendar;

import org.greatfree.client.MessageStream;
import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.coordinator.front.*;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.framework.cps.cache.message.front.*;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataStream;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataStream;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.PushMuchMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.PushMyStoreDataNotification;
import org.greatfree.framework.cps.threetier.message.CPSMessageType;
import org.greatfree.framework.cps.threetier.message.FrontNotification;
import org.greatfree.framework.cps.threetier.message.FrontRequest;
import org.greatfree.framework.cps.threetier.message.FrontResponse;
import org.greatfree.framework.cps.threetier.message.FrontStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.ServerDispatcher;

// Created: 07/06/2018, Bing Li
class CoordinatorDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<FrontNotification, FrontNotificationThread, FrontNotificationThreadCreator> frontNotificationDispatcher;
	private RequestDispatcher<FrontRequest, FrontStream, FrontResponse, FrontRequestThread, FrontRequestThreadCreator> frontRequestDispatcher;

	private NotificationDispatcher<SaveMyDataNotification, SaveMyDataThread, SaveMyDataThreadCreator> saveDataNotificationDispatcher;
	private NotificationDispatcher<SaveMuchMyDataNotification, SaveMuchMyDataThread, SaveMuchMyDataThreadCreator> saveMuchDataNotificationDispatcher;
	private RequestDispatcher<LoadMyDataRequest, LoadMyDataStream, LoadMyDataResponse, LoadMyDataThread, LoadMyDataThreadCreator> loadDataRequestDispatcher;
	private RequestDispatcher<LoadMyDataByKeysRequest, LoadMyDataByKeysStream, LoadMyDataByKeysResponse, LoadMyDataByKeysThread, LoadMyDataByKeysThreadCreator> loadDataByKeysRequestDispatcher;
	private RequestDispatcher<LoadMapStoreDataKeysRequest, LoadMapStoreDataKeysStream, LoadMapStoreDataKeysResponse, LoadMapStoreDataKeysThread, LoadMapStoreDataKeysThreadCreator> loadMapStoreDataKeysRequestDispatcher;
	
	private NotificationDispatcher<SaveMyPointingListNotification, SaveMyPointingListThread, SaveMyPointingListThreadCreator> saveMyPointingListNotificationDispatcher;
	private NotificationDispatcher<SaveMyPointingsListNotification, SaveMyPointingsListThread, SaveMyPointingsListThreadCreator> saveMyPointingsListNotificationDispatcher;
	private RequestDispatcher<LoadMyPointingRequest, LoadMyPointingStream, LoadMyPointingResponse, LoadMyPointingThread, LoadMyPointingThreadCreator> loadMyPointingRequestDispatcher;
	private RequestDispatcher<LoadTopMyPointingsRequest, LoadTopMyPointingsStream, LoadTopMyPointingsResponse, LoadTopMyPointingsThread, LoadTopMyPointingsThreadCreator> loadTopMyPointingsRequestDispatcher;
	private RequestDispatcher<LoadRangeMyPointingsRequest, LoadRangeMyPointingsStream, LoadRangeMyPointingsResponse, LoadRangeMyPointingsThread, LoadRangeMyPointingsThreadCreator> loadRangeMyPointingsRequestDispatcher;

	private NotificationDispatcher<SaveMyPointingMapNotification, SaveMyPointingMapThread, SaveMyPointingMapThreadCreator> saveMyPointingMapNotificationDispatcher;
	private NotificationDispatcher<SaveMyPointingsMapNotification, SaveMyPointingsMapThread, SaveMyPointingsMapThreadCreator> saveMyPointingsMapNotificationDispatcher;
	private RequestDispatcher<LoadMaxMyPointingRequest, LoadMaxMyPointingStream, LoadMaxMyPointingResponse, LoadMaxMyPointingThread, LoadMaxMyPointingThreadCreator> loadMaxMyPointingRequestDispatcher;
	private RequestDispatcher<LoadMinMyPointingRequest, LoadMinMyPointingStream, LoadMinMyPointingResponse, LoadMinMyPointingThread, LoadMinMyPointingThreadCreator> loadMinMyPointingRequestDispatcher;
	private RequestDispatcher<LoadMyPointingMapRequest, LoadMyPointingMapStream, LoadMyPointingMapResponse, LoadMyPointingMapThread, LoadMyPointingMapThreadCreator> loadMyPointingMapRequestDispatcher;

	private NotificationDispatcher<SaveCachePointingNotification, SaveCachePointingThread, SaveCachePointingThreadCreator> saveCachePointingNotificationDispatcher;
	private NotificationDispatcher<SaveCachePointingsNotification, SaveCachePointingsThread, SaveCachePointingsThreadCreator> saveCachePointingsNotificationDispatcher;
	private RequestDispatcher<ContainsKeyOfCachePointingRequest, ContainsKeyOfCachePointingStream, ContainsKeyOfCachePointingResponse, ContainsKeyOfCachePointingThread, ContainsKeyOfCachePointingThreadCreator> containsKeyOfCachePointingRequestDispatcher;
	private RequestDispatcher<MaxCachePointingRequest, MaxCachePointingStream, MaxCachePointingResponse, MaxCachePointingThread, MaxCachePointingThreadCreator> maxCachePointingRequestDispatcher;
	private RequestDispatcher<CachePointingByKeyRequest, CachePointingByKeyStream, CachePointingByKeyResponse, CachePointingByKeyThread, CachePointingByKeyThreadCreator> cachePointingByKeyRequestDispatcher;
	private RequestDispatcher<IsCacheExistedInPointingStoreRequest, IsCacheExistedInPointingStoreStream, IsCacheExistedInPointingStoreResponse, IsCacheExistedInPointingStoreThread, IsCacheExistedInPointingStoreThreadCreator> isCacheExistedRequestDispatcher;
	private RequestDispatcher<IsCacheEmptyInPointingStoreRequest, IsCacheEmptyInPointingStoreStream, IsCacheEmptyInPointingStoreResponse, IsCacheEmptyInPointingStoreThread, IsCacheEmptyInPointingStoreThreadCreator> isCacheEmptyRequestDispatcher;
	private RequestDispatcher<CachePointingByIndexRequest, CachePointingByIndexStream, CachePointingByIndexResponse, CachePointingByIndexThread, CachePointingByIndexThreadCreator> cachePointingByIndexRequestDispatcher;
	private RequestDispatcher<TopCachePointingsRequest, TopCachePointingsStream, TopCachePointingsResponse, TopCachePointingsThread, TopCachePointingsThreadCreator> topCachePointingsRequestDispatcher;
	private RequestDispatcher<RangeCachePointingsRequest, RangeCachePointingsStream, RangeCachePointingsResponse, RangeCachePointingsThread, RangeCachePointingsThreadCreator> rangeCachePointingsRequestDispatcher;

	private RequestDispatcher<PointingByIndexPrefetchListRequest, PointingByIndexPrefetchListStream, PointingByIndexPrefetchListResponse, PointingByIndexPrefetchListThread, PointingByIndexPrefetchListThreadCreator> pointingByIndexPrefetchListRequestDispatcher;
	private RequestDispatcher<TopPointingsPrefetchListRequest, TopPointingsPrefetchListStream, TopPointingsPrefetchListResponse, TopPointingsPrefetchListThread, TopPointingsPrefetchListThreadCreator> topPointingPrefetchListRequestDispatcher;
	private RequestDispatcher<RangePointingsPrefetchListRequest, RangePointingsPrefetchListStream, RangePointingsPrefetchListResponse, RangePointingsPrefetchListThread, RangePointingsPrefetchListThreadCreator> rangePointingPrefetchListRequestDispatcher;

	private RequestDispatcher<IsCacheReadExistedInPointingStoreRequest, IsCacheReadExistedInPointingStoreStream, IsCacheReadExistedInPointingStoreResponse, IsCacheReadExistedInPointingStoreThread, IsCacheReadExistedInPointingStoreThreadCreator> isCacheReadExistedRequestDispatcher;
	private RequestDispatcher<TopReadCachePointingsRequest, TopReadCachePointingsStream, TopReadCachePointingsResponse, TopCacheReadPointingsThread, TopCacheReadPointingsThreadCreator> topCacheReadPointingsRequestDispatcher;
	private RequestDispatcher<RangeReadCachePointingsRequest, RangeReadCachePointingsStream, RangeReadCachePointingsResponse, LoadRangeReadMyPointingsThread, LoadRangeReadMyPointingsThreadCreator> rangeCacheReadPointingsRequestDispatcher;

	private NotificationDispatcher<PushMyStoreDataNotification, PushMyStoreDataThread, PushMyStoreDataThreadCreator> pushDataNotificationDispatcher;
	private NotificationDispatcher<PushMuchMyStoreDataNotification, PushMuchMyStoreDataThread, PushMuchMyStoreDataThreadCreator> pushMuchDataNotificationDispatcher;
	private RequestDispatcher<PopSingleMyStoreDataRequest, PopSingleMyStoreDataStream, PopSingleMyStoreDataResponse, PopSingleMyStoreDataThread, PopSingleMyStoreDataThreadCreator> popSingleDataRequestDispatcher;
	private RequestDispatcher<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse, PopMyStoreDataThread, PopMyStoreDataThreadCreator> popDataRequestDispatcher;
	private RequestDispatcher<PeekSingleMyStoreDataRequest, PeekSingleMyStoreDataStream, PeekSingleMyStoreDataResponse, PeekSingleMyStoreDataThread, PeekSingleMyStoreDataThreadCreator> peekSingleDataRequestDispatcher;
	private RequestDispatcher<PeekMyStoreDataRequest, PeekMyStoreDataStream, PeekMyStoreDataResponse, PeekMyStoreDataThread, PeekMyStoreDataThreadCreator> peekDataRequestDispatcher;

	private NotificationDispatcher<EnqueueMyStoreDataNotification, EnqueueMyStoreDataThread, EnqueueMyStoreDataThreadCreator> enqueueDataNotificationDispatcher;
	private NotificationDispatcher<EnqueueMuchMyStoreDataNotification, EnqueueMuchMyStoreDataThread, EnqueueMuchMyStoreDataThreadCreator> enqueueMuchDataNotificationDispatcher;
	private RequestDispatcher<DequeueSingleMyStoreDataRequest, DequeueSingleMyStoreDataStream, DequeueSingleMyStoreDataResponse, DequeueSingleMyStoreDataThread, DequeueSingleMyStoreDataThreadCreator> dequeueSingleDataRequestDispatcher;
	private RequestDispatcher<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse, DequeueMyStoreDataThread, DequeueMyStoreDataThreadCreator> dequeueDataRequestDispatcher;
	private RequestDispatcher<PeekSingleMyStoreDataQueueRequest, PeekSingleMyStoreDataQueueStream, PeekSingleMyStoreDataQueueResponse, PeekSingleMyStoreDataQueueThread, PeekSingleMyStoreDataQueueThreadCreator> peekSingleDataQueueRequestDispatcher;
	private RequestDispatcher<PeekMyStoreDataQueueRequest, PeekMyStoreDataQueueStream, PeekMyStoreDataQueueResponse, PeekMyStoreDataQueueThread, PeekMyStoreDataQueueThreadCreator> peekDataQueueRequestDispatcher;

	private NotificationDispatcher<SaveMyUKNotification, SaveMyUKThread, SaveMyUKThreadCreator> saveUKNotificationDispatcher;
	private NotificationDispatcher<SaveMuchUKsNotification, SaveMuchUKsThread, SaveMuchUKsThreadCreator> saveMuchUKsNotificationDispatcher;
	private RequestDispatcher<LoadMyUKRequest, LoadMyUKStream, LoadMyUKResponse, LoadMyUKThread, LoadMyUKThreadCreator> loadMyUKRequestDispatcher;
	private RequestDispatcher<LoadTopMyUKsRequest, LoadTopMyUKsStream, LoadTopMyUKsResponse, LoadMyTopUKsThread, LoadMyTopUKsThreadCreator> loadMyTopUKsRequestDispatcher;
	private RequestDispatcher<LoadRangeMyUKsRequest, LoadRangeMyUKsStream, LoadRangeMyUKsResponse, LoadMyRangeUKsThread, LoadMyRangeUKsThreadCreator> loadMyRangeUKsRequestDispatcher;

//	public CoordinatorDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public CoordinatorDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, FrontNotification
		this.frontNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<FrontNotification, FrontNotificationThread, FrontNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new FrontNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		// Initialize the request dispatcher for the request/response, FrontRequest/FrontResponse
		this.frontRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<FrontRequest, FrontStream, FrontResponse, FrontRequestThread, FrontRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new FrontRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMyDataNotification, SaveMyDataThread, SaveMyDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMyDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveMuchDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMuchMyDataNotification, SaveMuchMyDataThread, SaveMuchMyDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMuchMyDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMyDataRequest, LoadMyDataStream, LoadMyDataResponse, LoadMyDataThread, LoadMyDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMyDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadDataByKeysRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMyDataByKeysRequest, LoadMyDataByKeysStream, LoadMyDataByKeysResponse, LoadMyDataByKeysThread, LoadMyDataByKeysThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMyDataByKeysThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMapStoreDataKeysRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMapStoreDataKeysRequest, LoadMapStoreDataKeysStream, LoadMapStoreDataKeysResponse, LoadMapStoreDataKeysThread, LoadMapStoreDataKeysThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMapStoreDataKeysThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveMyPointingListNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMyPointingListNotification, SaveMyPointingListThread, SaveMyPointingListThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMyPointingListThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveMyPointingsListNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMyPointingsListNotification, SaveMyPointingsListThread, SaveMyPointingsListThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMyPointingsListThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMyPointingRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMyPointingRequest, LoadMyPointingStream, LoadMyPointingResponse, LoadMyPointingThread, LoadMyPointingThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMyPointingThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadTopMyPointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadTopMyPointingsRequest, LoadTopMyPointingsStream, LoadTopMyPointingsResponse, LoadTopMyPointingsThread, LoadTopMyPointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadTopMyPointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadRangeMyPointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadRangeMyPointingsRequest, LoadRangeMyPointingsStream, LoadRangeMyPointingsResponse, LoadRangeMyPointingsThread, LoadRangeMyPointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadRangeMyPointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveMyPointingMapNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMyPointingMapNotification, SaveMyPointingMapThread, SaveMyPointingMapThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMyPointingMapThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveMyPointingsMapNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMyPointingsMapNotification, SaveMyPointingsMapThread, SaveMyPointingsMapThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMyPointingsMapThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMaxMyPointingRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMaxMyPointingRequest, LoadMaxMyPointingStream, LoadMaxMyPointingResponse, LoadMaxMyPointingThread, LoadMaxMyPointingThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMaxMyPointingThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMinMyPointingRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMinMyPointingRequest, LoadMinMyPointingStream, LoadMinMyPointingResponse, LoadMinMyPointingThread, LoadMinMyPointingThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMinMyPointingThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMyPointingMapRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMyPointingMapRequest, LoadMyPointingMapStream, LoadMyPointingMapResponse, LoadMyPointingMapThread, LoadMyPointingMapThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMyPointingMapThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveCachePointingNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveCachePointingNotification, SaveCachePointingThread, SaveCachePointingThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveCachePointingThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveCachePointingsNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveCachePointingsNotification, SaveCachePointingsThread, SaveCachePointingsThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveCachePointingsThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.containsKeyOfCachePointingRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<ContainsKeyOfCachePointingRequest, ContainsKeyOfCachePointingStream, ContainsKeyOfCachePointingResponse, ContainsKeyOfCachePointingThread, ContainsKeyOfCachePointingThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new ContainsKeyOfCachePointingThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.maxCachePointingRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<MaxCachePointingRequest, MaxCachePointingStream, MaxCachePointingResponse, MaxCachePointingThread, MaxCachePointingThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new MaxCachePointingThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.cachePointingByKeyRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CachePointingByKeyRequest, CachePointingByKeyStream, CachePointingByKeyResponse, CachePointingByKeyThread, CachePointingByKeyThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new CachePointingByKeyThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.isCacheExistedRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IsCacheExistedInPointingStoreRequest, IsCacheExistedInPointingStoreStream, IsCacheExistedInPointingStoreResponse, IsCacheExistedInPointingStoreThread, IsCacheExistedInPointingStoreThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new IsCacheExistedInPointingStoreThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.isCacheEmptyRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IsCacheEmptyInPointingStoreRequest, IsCacheEmptyInPointingStoreStream, IsCacheEmptyInPointingStoreResponse, IsCacheEmptyInPointingStoreThread, IsCacheEmptyInPointingStoreThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new IsCacheEmptyInPointingStoreThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.cachePointingByIndexRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CachePointingByIndexRequest, CachePointingByIndexStream, CachePointingByIndexResponse, CachePointingByIndexThread, CachePointingByIndexThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new CachePointingByIndexThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.topCachePointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<TopCachePointingsRequest, TopCachePointingsStream, TopCachePointingsResponse, TopCachePointingsThread, TopCachePointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new TopCachePointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.rangeCachePointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<RangeCachePointingsRequest, RangeCachePointingsStream, RangeCachePointingsResponse, RangeCachePointingsThread, RangeCachePointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new RangeCachePointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pointingByIndexPrefetchListRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PointingByIndexPrefetchListRequest, PointingByIndexPrefetchListStream, PointingByIndexPrefetchListResponse, PointingByIndexPrefetchListThread, PointingByIndexPrefetchListThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PointingByIndexPrefetchListThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.topPointingPrefetchListRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<TopPointingsPrefetchListRequest, TopPointingsPrefetchListStream, TopPointingsPrefetchListResponse, TopPointingsPrefetchListThread, TopPointingsPrefetchListThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new TopPointingsPrefetchListThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.rangePointingPrefetchListRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<RangePointingsPrefetchListRequest, RangePointingsPrefetchListStream, RangePointingsPrefetchListResponse, RangePointingsPrefetchListThread, RangePointingsPrefetchListThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new RangePointingsPrefetchListThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.isCacheReadExistedRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<IsCacheReadExistedInPointingStoreRequest, IsCacheReadExistedInPointingStoreStream, IsCacheReadExistedInPointingStoreResponse, IsCacheReadExistedInPointingStoreThread, IsCacheReadExistedInPointingStoreThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new IsCacheReadExistedInPointingStoreThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.topCacheReadPointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<TopReadCachePointingsRequest, TopReadCachePointingsStream, TopReadCachePointingsResponse, TopCacheReadPointingsThread, TopCacheReadPointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new TopCacheReadPointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.rangeCacheReadPointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<RangeReadCachePointingsRequest, RangeReadCachePointingsStream, RangeReadCachePointingsResponse, LoadRangeReadMyPointingsThread, LoadRangeReadMyPointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadRangeReadMyPointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pushDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PushMyStoreDataNotification, PushMyStoreDataThread, PushMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new PushMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pushMuchDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PushMuchMyStoreDataNotification, PushMuchMyStoreDataThread, PushMuchMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new PushMuchMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.popDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse, PopMyStoreDataThread, PopMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PopMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.popSingleDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PopSingleMyStoreDataRequest, PopSingleMyStoreDataStream, PopSingleMyStoreDataResponse, PopSingleMyStoreDataThread, PopSingleMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PopSingleMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.peekSingleDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PeekSingleMyStoreDataRequest, PeekSingleMyStoreDataStream, PeekSingleMyStoreDataResponse, PeekSingleMyStoreDataThread, PeekSingleMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PeekSingleMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.peekDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PeekMyStoreDataRequest, PeekMyStoreDataStream, PeekMyStoreDataResponse, PeekMyStoreDataThread, PeekMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PeekMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.enqueueDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<EnqueueMyStoreDataNotification, EnqueueMyStoreDataThread, EnqueueMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new EnqueueMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.enqueueMuchDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<EnqueueMuchMyStoreDataNotification, EnqueueMuchMyStoreDataThread, EnqueueMuchMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new EnqueueMuchMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.dequeueSingleDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<DequeueSingleMyStoreDataRequest, DequeueSingleMyStoreDataStream, DequeueSingleMyStoreDataResponse, DequeueSingleMyStoreDataThread, DequeueSingleMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new DequeueSingleMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.dequeueDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse, DequeueMyStoreDataThread, DequeueMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new DequeueMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.peekSingleDataQueueRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PeekSingleMyStoreDataQueueRequest, PeekSingleMyStoreDataQueueStream, PeekSingleMyStoreDataQueueResponse, PeekSingleMyStoreDataQueueThread, PeekSingleMyStoreDataQueueThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PeekSingleMyStoreDataQueueThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.peekDataQueueRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PeekMyStoreDataQueueRequest, PeekMyStoreDataQueueStream, PeekMyStoreDataQueueResponse, PeekMyStoreDataQueueThread, PeekMyStoreDataQueueThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PeekMyStoreDataQueueThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveUKNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMyUKNotification, SaveMyUKThread, SaveMyUKThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMyUKThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.saveMuchUKsNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<SaveMuchUKsNotification, SaveMuchUKsThread, SaveMuchUKsThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new SaveMuchUKsThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMyUKRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadMyUKRequest, LoadMyUKStream, LoadMyUKResponse, LoadMyUKThread, LoadMyUKThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMyUKThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMyTopUKsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadTopMyUKsRequest, LoadTopMyUKsStream, LoadTopMyUKsResponse, LoadMyTopUKsThread, LoadMyTopUKsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMyTopUKsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.loadMyRangeUKsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<LoadRangeMyUKsRequest, LoadRangeMyUKsStream, LoadRangeMyUKsResponse, LoadMyRangeUKsThread, LoadMyRangeUKsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new LoadMyRangeUKsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();
	}

	@Override
	public void dispose(long timeout) throws InterruptedException
	{
		super.shutdown(timeout);
		this.frontNotificationDispatcher.dispose();
		this.frontRequestDispatcher.dispose();
		this.saveDataNotificationDispatcher.dispose();
		this.saveMuchDataNotificationDispatcher.dispose();
		this.loadDataRequestDispatcher.dispose();
		this.loadDataByKeysRequestDispatcher.dispose();
		this.loadMapStoreDataKeysRequestDispatcher.dispose();
		
		this.saveMyPointingListNotificationDispatcher.dispose();
		this.saveMyPointingsListNotificationDispatcher.dispose();
		this.loadMyPointingRequestDispatcher.dispose();
		this.loadTopMyPointingsRequestDispatcher.dispose();
		this.loadRangeMyPointingsRequestDispatcher.dispose();
		this.saveMyPointingMapNotificationDispatcher.dispose();
		this.saveMyPointingsMapNotificationDispatcher.dispose();
		this.loadMaxMyPointingRequestDispatcher.dispose();
		this.loadMinMyPointingRequestDispatcher.dispose();
		this.loadMyPointingMapRequestDispatcher.dispose();
		this.saveCachePointingNotificationDispatcher.dispose();
		this.saveCachePointingsNotificationDispatcher.dispose();
		this.containsKeyOfCachePointingRequestDispatcher.dispose();
		this.maxCachePointingRequestDispatcher.dispose();
		this.cachePointingByKeyRequestDispatcher.dispose();
		this.isCacheExistedRequestDispatcher.dispose();
		this.isCacheEmptyRequestDispatcher.dispose();
		this.cachePointingByIndexRequestDispatcher.dispose();
		this.topCachePointingsRequestDispatcher.dispose();
		this.rangeCachePointingsRequestDispatcher.dispose();
		this.pointingByIndexPrefetchListRequestDispatcher.dispose();
		this.topPointingPrefetchListRequestDispatcher.dispose();
		this.rangePointingPrefetchListRequestDispatcher.dispose();
		this.isCacheReadExistedRequestDispatcher.dispose();
		this.topCacheReadPointingsRequestDispatcher.dispose();
		this.rangeCacheReadPointingsRequestDispatcher.dispose();
		
		this.pushDataNotificationDispatcher.dispose();
		this.pushMuchDataNotificationDispatcher.dispose();
		this.popDataRequestDispatcher.dispose();
		this.popSingleDataRequestDispatcher.dispose();
		this.peekDataRequestDispatcher.dispose();
		this.peekSingleDataRequestDispatcher.dispose();
		
		this.enqueueDataNotificationDispatcher.dispose();
		this.enqueueMuchDataNotificationDispatcher.dispose();
		this.dequeueSingleDataRequestDispatcher.dispose();
		this.dequeueDataRequestDispatcher.dispose();
		this.peekSingleDataQueueRequestDispatcher.dispose();
		this.peekDataQueueRequestDispatcher.dispose();
		
		this.saveUKNotificationDispatcher.dispose();
		this.saveMuchUKsNotificationDispatcher.dispose();
		this.loadMyUKRequestDispatcher.dispose();
		this.loadMyTopUKsRequestDispatcher.dispose();
		this.loadMyRangeUKsRequestDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CPSMessageType.FRONT_NOTIFICATION:
				System.out.println("FRONT_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.frontNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.frontNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.frontNotificationDispatcher.enqueue((FrontNotification)message.getMessage());
				break;

			case CPSMessageType.FRONT_REQUEST:
				System.out.println("FRONT_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.frontRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.frontRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.frontRequestDispatcher.enqueue(new FrontStream(message.getOutStream(), message.getLock(), (FrontRequest)message.getMessage()));
				break;

			case TestCacheMessageType.SAVE_MY_DATA_NOTIFICATION:
				System.out.println("SAVE_MY_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveDataNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveDataNotificationDispatcher.enqueue((SaveMyDataNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.SAVE_MUCH_MY_DATA_NOTIFICATION:
				System.out.println("SAVE_MUCH_MY_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveMuchDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveMuchDataNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveMuchDataNotificationDispatcher.enqueue((SaveMuchMyDataNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.LOAD_MY_DATA_REQUEST:
				System.out.println("LOAD_MY_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadDataRequestDispatcher.enqueue(new LoadMyDataStream(message.getOutStream(), message.getLock(), (LoadMyDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.LOAD_MY_DATA_BY_KEYS_REQUEST:
				System.out.println("LOAD_MY_DATA_BY_KEYS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadDataByKeysRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadDataByKeysRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadDataByKeysRequestDispatcher.enqueue(new LoadMyDataByKeysStream(message.getOutStream(), message.getLock(), (LoadMyDataByKeysRequest)message.getMessage()));
				break;

			case TestCacheMessageType.LOAD_MAP_STORE_DATA_KEYS_REQUEST:
				System.out.println("LOAD_MAP_STORE_DATA_KEYS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMapStoreDataKeysRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMapStoreDataKeysRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMapStoreDataKeysRequestDispatcher.enqueue(new LoadMapStoreDataKeysStream(message.getOutStream(), message.getLock(), (LoadMapStoreDataKeysRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.SAVE_MY_POINTING_LIST_NOTIFICATION:
				System.out.println("SAVE_MY_POINTING_LIST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveMyPointingListNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveMyPointingListNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveMyPointingListNotificationDispatcher.enqueue((SaveMyPointingListNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.SAVE_MY_POINTINGS_LIST_NOTIFICATION:
				System.out.println("SAVE_MY_POINTINGS_LIST_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveMyPointingsListNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveMyPointingsListNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveMyPointingsListNotificationDispatcher.enqueue((SaveMyPointingsListNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.LOAD_MY_POINTING_REQUEST:
				System.out.println("LOAD_MY_POINTING_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMyPointingRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMyPointingRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMyPointingRequestDispatcher.enqueue(new LoadMyPointingStream(message.getOutStream(), message.getLock(), (LoadMyPointingRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.LOAD_TOP_MY_POINTINGS_REQUEST:
				System.out.println("LOAD_TOP_MY_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadTopMyPointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadTopMyPointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadTopMyPointingsRequestDispatcher.enqueue(new LoadTopMyPointingsStream(message.getOutStream(), message.getLock(), (LoadTopMyPointingsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.LOAD_RANGE_MY_POINTINGS_REQUEST:
				System.out.println("LOAD_RANGE_MY_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadRangeMyPointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadRangeMyPointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadRangeMyPointingsRequestDispatcher.enqueue(new LoadRangeMyPointingsStream(message.getOutStream(), message.getLock(), (LoadRangeMyPointingsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.SAVE_MY_POINTING_MAP_NOTIFICATION:
				System.out.println("SAVE_MY_POINTING_MAP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveMyPointingMapNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveMyPointingMapNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveMyPointingMapNotificationDispatcher.enqueue((SaveMyPointingMapNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.SAVE_MY_POINTINGS_MAP_NOTIFICATION:
				System.out.println("SAVE_MY_POINTINGS_MAP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveMyPointingsMapNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveMyPointingsMapNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveMyPointingsMapNotificationDispatcher.enqueue((SaveMyPointingsMapNotification)message.getMessage());
				break;

			case TestCacheMessageType.LOAD_MAX_MY_POINTING_REQUEST:
				System.out.println("LOAD_MAX_MY_POINTING_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMaxMyPointingRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMaxMyPointingRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMaxMyPointingRequestDispatcher.enqueue(new LoadMaxMyPointingStream(message.getOutStream(), message.getLock(), (LoadMaxMyPointingRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.LOAD_MIN_MY_POINTING_REQUEST:
				System.out.println("LOAD_MIN_MY_POINTING_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMinMyPointingRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMinMyPointingRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMinMyPointingRequestDispatcher.enqueue(new LoadMinMyPointingStream(message.getOutStream(), message.getLock(), (LoadMinMyPointingRequest)message.getMessage()));
				break;

			case TestCacheMessageType.LOAD_MY_POINTING_MAP_REQUEST:
				System.out.println("LOAD_MY_POINTING_MAP_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMyPointingMapRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMyPointingMapRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMyPointingMapRequestDispatcher.enqueue(new LoadMyPointingMapStream(message.getOutStream(), message.getLock(), (LoadMyPointingMapRequest)message.getMessage()));
				break;

			case TestCacheMessageType.SAVE_MY_CACHE_POINTING_MAP_NOTIFICATION:
				System.out.println("SAVE_MY_CACHE_POINTING_MAP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveCachePointingNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveCachePointingNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveCachePointingNotificationDispatcher.enqueue((SaveCachePointingNotification)message.getMessage());
				break;

			case TestCacheMessageType.SAVE_MY_CACHE_POINTINGS_MAP_NOTIFICATION:
				System.out.println("SAVE_MY_CACHE_POINTINGS_MAP_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveCachePointingsNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveCachePointingsNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveCachePointingsNotificationDispatcher.enqueue((SaveCachePointingsNotification)message.getMessage());
				break;

			case TestCacheMessageType.CONTAINS_KEY_OF_CACHE_POINTING_REQUEST:
				System.out.println("CONTAINS_KEY_OF_CACHE_POINTING_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.containsKeyOfCachePointingRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.containsKeyOfCachePointingRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.containsKeyOfCachePointingRequestDispatcher.enqueue(new ContainsKeyOfCachePointingStream(message.getOutStream(), message.getLock(), (ContainsKeyOfCachePointingRequest)message.getMessage()));
				break;

			case TestCacheMessageType.MAX_CACHE_POINTING_REQUEST:
				System.out.println("MAX_CACHE_POINTING_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.maxCachePointingRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.maxCachePointingRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.maxCachePointingRequestDispatcher.enqueue(new MaxCachePointingStream(message.getOutStream(), message.getLock(), (MaxCachePointingRequest)message.getMessage()));
				break;

			case TestCacheMessageType.CACHE_POINTING_BY_KEY_REQUEST:
				System.out.println("CACHE_POINTING_BY_KEY_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.cachePointingByKeyRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.cachePointingByKeyRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.cachePointingByKeyRequestDispatcher.enqueue(new CachePointingByKeyStream(message.getOutStream(), message.getLock(), (CachePointingByKeyRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.IS_CACHE_EXISTED_REQUEST:
				System.out.println("IS_CACHE_EXISTED_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.isCacheExistedRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.isCacheExistedRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.isCacheExistedRequestDispatcher.enqueue(new IsCacheExistedInPointingStoreStream(message.getOutStream(), message.getLock(), (IsCacheExistedInPointingStoreRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.IS_CACHE_EMPTY_REQUEST:
				System.out.println("IS_CACHE_EMPTY_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.isCacheEmptyRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.isCacheEmptyRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.isCacheEmptyRequestDispatcher.enqueue(new IsCacheEmptyInPointingStoreStream(message.getOutStream(), message.getLock(), (IsCacheEmptyInPointingStoreRequest)message.getMessage()));
				break;

			case TestCacheMessageType.CACHE_POINTING_BY_INDEX_REQUEST:
				System.out.println("CACHE_POINTING_BY_INDEX_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.cachePointingByIndexRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.cachePointingByIndexRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.cachePointingByIndexRequestDispatcher.enqueue(new CachePointingByIndexStream(message.getOutStream(), message.getLock(), (CachePointingByIndexRequest)message.getMessage()));
				break;

			case TestCacheMessageType.TOP_CACHE_POINTINGS_REQUEST:
				System.out.println("TOP_CACHE_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.topCachePointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.topCachePointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.topCachePointingsRequestDispatcher.enqueue(new TopCachePointingsStream(message.getOutStream(), message.getLock(), (TopCachePointingsRequest)message.getMessage()));
				break;

			case TestCacheMessageType.RANGE_CACHE_POINTINGS_REQUEST:
				System.out.println("RANGE_CACHE_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.rangeCachePointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.rangeCachePointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.rangeCachePointingsRequestDispatcher.enqueue(new RangeCachePointingsStream(message.getOutStream(), message.getLock(), (RangeCachePointingsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.POINTING_BY_INDEX_PREFETCH_LIST_REQUEST:
				System.out.println("POINTING_BY_INDEX_PREFETCH_LIST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.pointingByIndexPrefetchListRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.pointingByIndexPrefetchListRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.pointingByIndexPrefetchListRequestDispatcher.enqueue(new PointingByIndexPrefetchListStream(message.getOutStream(), message.getLock(), (PointingByIndexPrefetchListRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.TOP_POINTINGS_PREFETCH_LIST_REQUEST:
				System.out.println("TOP_POINTINGS_PREFETCH_LIST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.topPointingPrefetchListRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.topPointingPrefetchListRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.topPointingPrefetchListRequestDispatcher.enqueue(new TopPointingsPrefetchListStream(message.getOutStream(), message.getLock(), (TopPointingsPrefetchListRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.RANGE_POINTINGS_PREFETCH_LIST_REQUEST:
				System.out.println("RANGE_POINTINGS_PREFETCH_LIST_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.rangePointingPrefetchListRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.rangePointingPrefetchListRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.rangePointingPrefetchListRequestDispatcher.enqueue(new RangePointingsPrefetchListStream(message.getOutStream(), message.getLock(), (RangePointingsPrefetchListRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.IS_CACHE_READ_EXISTED_REQUEST:
				System.out.println("IS_CACHE_READ_EXISTED_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.isCacheReadExistedRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.isCacheReadExistedRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.isCacheReadExistedRequestDispatcher.enqueue(new IsCacheReadExistedInPointingStoreStream(message.getOutStream(), message.getLock(), (IsCacheReadExistedInPointingStoreRequest)message.getMessage()));
				break;

			case TestCacheMessageType.TOP_READ_CACHE_POINTINGS_REQUEST:
				System.out.println("TOP_READ_CACHE_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.topCacheReadPointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.topCacheReadPointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.topCacheReadPointingsRequestDispatcher.enqueue(new TopReadCachePointingsStream(message.getOutStream(), message.getLock(), (TopReadCachePointingsRequest)message.getMessage()));
				break;

			case TestCacheMessageType.RANGE_READ_CACHE_POINTINGS_REQUEST:
				System.out.println("RANGE_READ_CACHE_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.rangeCacheReadPointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.rangeCacheReadPointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.rangeCacheReadPointingsRequestDispatcher.enqueue(new RangeReadCachePointingsStream(message.getOutStream(), message.getLock(), (RangeReadCachePointingsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.PUSH_MY_STORE_DATA_NOTIFICATION:
				System.out.println("PUSH_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.pushDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.pushDataNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.pushDataNotificationDispatcher.enqueue((PushMyStoreDataNotification)message.getMessage());
				break;

			case TestCacheMessageType.PUSH_MUCH_MY_STORE_DATA_NOTIFICATION:
				System.out.println("PUSH_MUCH_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.pushMuchDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.pushMuchDataNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.pushMuchDataNotificationDispatcher.enqueue((PushMuchMyStoreDataNotification)message.getMessage());
				break;

			case TestCacheMessageType.POP_SINGLE_MY_STORE_DATA_REQUEST:
//				System.out.println("POP_SINGLE_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.popSingleDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.popSingleDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.popSingleDataRequestDispatcher.enqueue(new PopSingleMyStoreDataStream(message.getOutStream(), message.getLock(), (PopSingleMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POP_MY_STORE_DATA_REQUEST:
				System.out.println("POP_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.popDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.popDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.popDataRequestDispatcher.enqueue(new PopMyStoreDataStream(message.getOutStream(), message.getLock(), (PopMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.PEEK_SINGLE_MY_STORE_DATA_REQUEST:
				System.out.println("PEEK_SINGLE_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.peekSingleDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.peekSingleDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.peekSingleDataRequestDispatcher.enqueue(new PeekSingleMyStoreDataStream(message.getOutStream(), message.getLock(), (PeekSingleMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.PEEK_MY_STORE_DATA_REQUEST:
				System.out.println("PEEK_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.peekDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.peekDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.peekDataRequestDispatcher.enqueue(new PeekMyStoreDataStream(message.getOutStream(), message.getLock(), (PeekMyStoreDataRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.ENQUEUE_MY_STORE_DATA_NOTIFICATION:
				System.out.println("ENQUEUE_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.enqueueDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.enqueueDataNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.enqueueDataNotificationDispatcher.enqueue((EnqueueMyStoreDataNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.ENQUEUE_MUCH_MY_STORE_DATA_NOTIFICATION:
				System.out.println("ENQUEUE_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.enqueueMuchDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.enqueueMuchDataNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.enqueueMuchDataNotificationDispatcher.enqueue((EnqueueMuchMyStoreDataNotification)message.getMessage());
				break;

			case TestCacheMessageType.DEQUEUE_SINGLE_MY_STORE_DATA_REQUEST:
				System.out.println("DEQUEUE_SINGLE_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.dequeueSingleDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.dequeueSingleDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.dequeueSingleDataRequestDispatcher.enqueue(new DequeueSingleMyStoreDataStream(message.getOutStream(), message.getLock(), (DequeueSingleMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.DEQUEUE_MY_STORE_DATA_REQUEST:
				System.out.println("DEQUEUE_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.dequeueDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.dequeueDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.dequeueDataRequestDispatcher.enqueue(new DequeueMyStoreDataStream(message.getOutStream(), message.getLock(), (DequeueMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.PEEK_SINGLE_MY_STORE_DATA_QUEUE_REQUEST:
				System.out.println("PEEK_SINGLE_MY_STORE_DATA_QUEUE_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.peekSingleDataQueueRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.peekSingleDataQueueRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.peekSingleDataQueueRequestDispatcher.enqueue(new PeekSingleMyStoreDataQueueStream(message.getOutStream(), message.getLock(), (PeekSingleMyStoreDataQueueRequest)message.getMessage()));
				break;

			case TestCacheMessageType.PEEK_MY_STORE_DATA_QUEUE_REQUEST:
				System.out.println("PEEK_MY_STORE_DATA_QUEUE_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.peekDataQueueRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.peekDataQueueRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.peekDataQueueRequestDispatcher.enqueue(new PeekMyStoreDataQueueStream(message.getOutStream(), message.getLock(), (PeekMyStoreDataQueueRequest)message.getMessage()));
				break;

			case TestCacheMessageType.SAVE_MY_UK_NOTIFICATION:
				System.out.println("SAVE_MY_UK_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveUKNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveUKNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveUKNotificationDispatcher.enqueue((SaveMyUKNotification)message.getMessage());
				break;

			case TestCacheMessageType.SAVE_MUCH_UKS_NOTIFICATION:
				System.out.println("SAVE_MUCH_UKS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.saveMuchUKsNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.saveMuchUKsNotificationDispatcher);
				}
				// Enqueue the instance of FrontNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.saveMuchUKsNotificationDispatcher.enqueue((SaveMuchUKsNotification)message.getMessage());
				break;

			case TestCacheMessageType.LOAD_MY_UK_REQUEST:
				System.out.println("LOAD_MY_UK_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMyUKRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMyUKRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMyUKRequestDispatcher.enqueue(new LoadMyUKStream(message.getOutStream(), message.getLock(), (LoadMyUKRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.LOAD_TOP_MY_UKS_REQUEST:
				System.out.println("LOAD_TOP_MY_UKS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMyTopUKsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMyTopUKsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMyTopUKsRequestDispatcher.enqueue(new LoadTopMyUKsStream(message.getOutStream(), message.getLock(), (LoadTopMyUKsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.LOAD_RANGE_MY_UKS_REQUEST:
				System.out.println("LOAD_RANGE_MY_UKS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.loadMyRangeUKsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.loadMyRangeUKsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.loadMyRangeUKsRequestDispatcher.enqueue(new LoadRangeMyUKsStream(message.getOutStream(), message.getLock(), (LoadRangeMyUKsRequest)message.getMessage()));
				break;
		}
	}

}
