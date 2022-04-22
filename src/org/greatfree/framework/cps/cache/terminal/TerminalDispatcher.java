package org.greatfree.framework.cps.cache.terminal;

import java.util.Calendar;

import org.greatfree.concurrency.reactive.NotificationDispatcher;
import org.greatfree.concurrency.reactive.RequestDispatcher;
import org.greatfree.data.ServerConfig;
import org.greatfree.framework.cps.cache.message.TestCacheMessageType;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMinMyPointingStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMuchMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMuchMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMuchMyStoreDataStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByIndexStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingByKeyStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyCachePointingsStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataByKeysRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataByKeysResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataByKeysStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyDataStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByIndexStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingByKeyResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingsRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingsResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyPointingsStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataKeysStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyStoreDataStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValueByIndexRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValueByIndexResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValueByIndexStream;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesRequest;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesResponse;
import org.greatfree.framework.cps.cache.message.postfetch.PostfetchMyUKValuesStream;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.DequeueMyStoreDataStream;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PopMyStoreDataStream;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyCachePointingsStream;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyPointingsRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyPointingsResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyPointingsStream;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyUKValuesRequest;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyUKValuesResponse;
import org.greatfree.framework.cps.cache.message.prefetch.PrefetchMyUKValuesStream;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMuchMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.EnqueueMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.PushMuchMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.PushMyStoreDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateCachePointingNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateCachePointingsNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMuchMyDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMuchMyStoreDataMapStoreNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyDataNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyPointingNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyPointingsNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyStoreDataMapStoreNotification;
import org.greatfree.framework.cps.cache.message.replicate.ReplicateMyUKValueNotification;
import org.greatfree.framework.cps.cache.terminal.man.CoordinatorNotificationThread;
import org.greatfree.framework.cps.cache.terminal.man.CoordinatorNotificationThreadCreator;
import org.greatfree.framework.cps.cache.terminal.man.CoordinatorRequestThread;
import org.greatfree.framework.cps.cache.terminal.man.CoordinatorRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMinMyPointingRequestThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMinMyPointingRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMuchMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMuchMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyCachePointingByIndexThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyCachePointingByIndexThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyCachePointingByKeyThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyCachePointingByKeyThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyCachePointingsThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyCachePointingsThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyDataByKeysRequestThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyDataByKeysRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyDataRequestThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyDataRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyPointingByIndexRequestThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyPointingByIndexRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyPointingRequestThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyPointingRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyPointingsRequestThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyPointingsRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyStoreDataKeysThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyStoreDataKeysThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyUKValueByIndexThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyUKValueByIndexThreadCreator;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyUKValuesThread;
import org.greatfree.framework.cps.cache.terminal.postfetching.PostfetchMyUKValuesThreadCreator;
import org.greatfree.framework.cps.cache.terminal.prefetching.DequeueMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.prefetching.DequeueMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.prefetching.PopMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.prefetching.PopMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.prefetching.PrefetchMyCachePointingsThread;
import org.greatfree.framework.cps.cache.terminal.prefetching.PrefetchMyCachePointingsThreadCreator;
import org.greatfree.framework.cps.cache.terminal.prefetching.PrefetchMyPointingsRequestThread;
import org.greatfree.framework.cps.cache.terminal.prefetching.PrefetchMyPointingsRequestThreadCreator;
import org.greatfree.framework.cps.cache.terminal.prefetching.PrefetchMyUKValuesThread;
import org.greatfree.framework.cps.cache.terminal.prefetching.PrefetchMyUKValuesThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.EnqueueMuchMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.replicating.EnqueueMuchMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.EnqueueMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.replicating.EnqueueMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.PushMuchMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.replicating.PushMuchMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.PushMyStoreDataThread;
import org.greatfree.framework.cps.cache.terminal.replicating.PushMyStoreDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateCachePointingThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateCachePointingThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateCachePointingsThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateCachePointingsThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMuchMyDataThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMuchMyDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMuchMyStoreDataMapStoreThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMuchMyStoreDataMapStoreThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyDataThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyDataThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyPointingThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyPointingThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyPointingsThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyPointingsThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyStoreDataMapStoreThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyStoreDataMapStoreThreadCreator;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyUKValuesThread;
import org.greatfree.framework.cps.cache.terminal.replicating.ReplicateMyUKValuesThreadCreator;
import org.greatfree.framework.cps.threetier.message.CPSMessageType;
import org.greatfree.framework.cps.threetier.message.CoordinatorNotification;
import org.greatfree.framework.cps.threetier.message.CoordinatorRequest;
import org.greatfree.framework.cps.threetier.message.CoordinatorResponse;
import org.greatfree.framework.cps.threetier.message.CoordinatorStream;
import org.greatfree.message.ServerMessage;
import org.greatfree.server.MessageStream;
import org.greatfree.server.ServerDispatcher;

// Created: 07/06/2018, Bing Li
public class TerminalDispatcher extends ServerDispatcher<ServerMessage>
{
	private NotificationDispatcher<CoordinatorNotification, CoordinatorNotificationThread, CoordinatorNotificationThreadCreator> coordinatorNotificationDispatcher;
	private RequestDispatcher<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread, CoordinatorRequestThreadCreator> coordinatorRequestDispatcher;

	private NotificationDispatcher<ReplicateMyDataNotification, ReplicateMyDataThread, ReplicateMyDataThreadCreator> replicateMyDataNotificationDispatcher;
	private NotificationDispatcher<ReplicateMuchMyDataNotification, ReplicateMuchMyDataThread, ReplicateMuchMyDataThreadCreator> replicateMuchMyDataNotificationDispatcher;
	private RequestDispatcher<PostfetchMyDataRequest, PostfetchMyDataStream, PostfetchMyDataResponse, PostfetchMyDataRequestThread, PostfetchMyDataRequestThreadCreator> postfetchMyDataRequestDispatcher;
	private RequestDispatcher<PostfetchMyDataByKeysRequest, PostfetchMyDataByKeysStream, PostfetchMyDataByKeysResponse, PostfetchMyDataByKeysRequestThread, PostfetchMyDataByKeysRequestThreadCreator> postfetchMyDataByKeysRequestDispatcher;
	
	private NotificationDispatcher<ReplicateMyPointingNotification, ReplicateMyPointingThread, ReplicateMyPointingThreadCreator> replicateMyPointingNotificationDispatcher;
	private NotificationDispatcher<ReplicateMyPointingsNotification, ReplicateMyPointingsThread, ReplicateMyPointingsThreadCreator> replicateMyPointingsNotificationDispatcher;
	private RequestDispatcher<PrefetchMyPointingsRequest, PrefetchMyPointingsStream, PrefetchMyPointingsResponse, PrefetchMyPointingsRequestThread, PrefetchMyPointingsRequestThreadCreator> prefetchMyPointingsRequestDispatcher;
	private RequestDispatcher<PostfetchMyPointingByKeyRequest, PostfetchMyPointingStream, PostfetchMyPointingByKeyResponse, PostfetchMyPointingRequestThread, PostfetchMyPointingRequestThreadCreator> postfetchMyPointingRequestDispatcher;
	private RequestDispatcher<PostfetchMyPointingsRequest, PostfetchMyPointingsStream, PostfetchMyPointingsResponse, PostfetchMyPointingsRequestThread, PostfetchMyPointingsRequestThreadCreator> postfetchMyPointingsRequestDispatcher;
	private RequestDispatcher<PostfetchMyPointingByIndexRequest, PostfetchMyPointingByIndexStream, PostfetchMyPointingByIndexResponse, PostfetchMyPointingByIndexRequestThread, PostfetchMyPointingByIndexRequestThreadCreator> postfetchMyPointingByIndexRequestDispatcher;
	private RequestDispatcher<PostfetchMinMyPointingRequest, PostfetchMinMyPointingStream, PostfetchMinMyPointingResponse, PostfetchMinMyPointingRequestThread, PostfetchMinMyPointingRequestThreadCreator> postfetchMinMyPointingRequestDispatcher;
	
	private NotificationDispatcher<ReplicateCachePointingNotification, ReplicateCachePointingThread, ReplicateCachePointingThreadCreator> replicateMyCachePointingNotificationDispatcher;
	private NotificationDispatcher<ReplicateCachePointingsNotification, ReplicateCachePointingsThread, ReplicateCachePointingsThreadCreator> replicateMyCachePointingsNotificationDispatcher;
	private RequestDispatcher<PrefetchMyCachePointingsRequest, PrefetchMyCachePointingsStream, PrefetchMyCachePointingsResponse, PrefetchMyCachePointingsThread, PrefetchMyCachePointingsThreadCreator> prefetchMyCachePointingsRequestDispatcher;
	private RequestDispatcher<PostfetchMyCachePointingByIndexRequest, PostfetchMyCachePointingByIndexStream, PostfetchMyCachePointingByIndexResponse, PostfetchMyCachePointingByIndexThread, PostfetchMyCachePointingByIndexThreadCreator> postfetchMyCachePointingByIndexRequestDispatcher;
	private RequestDispatcher<PostfetchMyCachePointingByKeyRequest, PostfetchMyCachePointingByKeyStream, PostfetchMyCachePointingByKeyResponse, PostfetchMyCachePointingByKeyThread, PostfetchMyCachePointingByKeyThreadCreator> postfetchMyCachePointingByKeyRequestDispatcher;
	private RequestDispatcher<PostfetchMyCachePointingsRequest, PostfetchMyCachePointingsStream, PostfetchMyCachePointingsResponse, PostfetchMyCachePointingsThread, PostfetchMyCachePointingsThreadCreator> postfetchMyCachePointingsRequestDispatcher;

	private NotificationDispatcher<PushMyStoreDataNotification, PushMyStoreDataThread, PushMyStoreDataThreadCreator> pushMyStoreDataNotificationDispatcher;
	private NotificationDispatcher<PushMuchMyStoreDataNotification, PushMuchMyStoreDataThread, PushMuchMyStoreDataThreadCreator> pushMuchMyStoreDataNotificationDispatcher;
	private RequestDispatcher<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse, PopMyStoreDataThread, PopMyStoreDataThreadCreator> popMyStoreDataRequestDispatcher;

	private NotificationDispatcher<EnqueueMyStoreDataNotification, EnqueueMyStoreDataThread, EnqueueMyStoreDataThreadCreator> enqueueMyStoreDataNotificationDispatcher;
	private NotificationDispatcher<EnqueueMuchMyStoreDataNotification, EnqueueMuchMyStoreDataThread, EnqueueMuchMyStoreDataThreadCreator> enqueueMuchMyStoreDataNotificationDispatcher;
	private RequestDispatcher<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse, DequeueMyStoreDataThread, DequeueMyStoreDataThreadCreator> dequeueMyStoreDataRequestDispatcher;

	private NotificationDispatcher<ReplicateMyStoreDataMapStoreNotification, ReplicateMyStoreDataMapStoreThread, ReplicateMyStoreDataMapStoreThreadCreator> replicateMyStoreDataNotificationDispatcher;
	private NotificationDispatcher<ReplicateMuchMyStoreDataMapStoreNotification, ReplicateMuchMyStoreDataMapStoreThread, ReplicateMuchMyStoreDataMapStoreThreadCreator> replicateMuchMyStoreDataNotificationDispatcher;
	private RequestDispatcher<PostfetchMyStoreDataRequest, PostfetchMyStoreDataStream, PostfetchMyStoreDataResponse, PostfetchMyStoreDataThread, PostfetchMyStoreDataThreadCreator> postfetchMyStoreDataRequestDispatcher;
	private RequestDispatcher<PostfetchMuchMyStoreDataRequest, PostfetchMuchMyStoreDataStream, PostfetchMuchMyStoreDataResponse, PostfetchMuchMyStoreDataThread, PostfetchMuchMyStoreDataThreadCreator> postfetchMuchMyStoreDataRequestDispatcher;
	private RequestDispatcher<PostfetchMyStoreDataKeysRequest, PostfetchMyStoreDataKeysStream, PostfetchMyStoreDataKeysResponse, PostfetchMyStoreDataKeysThread, PostfetchMyStoreDataKeysThreadCreator> postfetchMyStoreDataKeysRequestDispatcher;

	private RequestDispatcher<PrefetchMyUKValuesRequest, PrefetchMyUKValuesStream, PrefetchMyUKValuesResponse, PrefetchMyUKValuesThread, PrefetchMyUKValuesThreadCreator> prefetchMyUKValuesRequestDispatcher;
	private NotificationDispatcher<ReplicateMyUKValueNotification, ReplicateMyUKValuesThread, ReplicateMyUKValuesThreadCreator> replicateMyUKValuesNotificationDispatcher;
	private RequestDispatcher<PostfetchMyUKValueByIndexRequest, PostfetchMyUKValueByIndexStream, PostfetchMyUKValueByIndexResponse, PostfetchMyUKValueByIndexThread, PostfetchMyUKValueByIndexThreadCreator> postfetchMyUKValuesByIndexRequestDispatcher;
	private RequestDispatcher<PostfetchMyUKValuesRequest, PostfetchMyUKValuesStream, PostfetchMyUKValuesResponse, PostfetchMyUKValuesThread, PostfetchMyUKValuesThreadCreator> postfetchMyUKValuesRequestDispatcher;
	
//	public TerminalDispatcher(int schedulerPoolSize, long schedulerKeepAliveTime)
	public TerminalDispatcher(int threadPoolSize, long threadKeepAliveTime, int schedulerPoolSize, long schedulerKeepAliveTime)
	{
		super(threadPoolSize, threadKeepAliveTime, schedulerPoolSize, schedulerKeepAliveTime);
//		super(schedulerPoolSize, schedulerKeepAliveTime);

		// Initialize the notification dispatcher for the notification, CoordinatorNotification. 07/07/2018, Bing Li
		this.coordinatorNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<CoordinatorNotification, CoordinatorNotificationThread, CoordinatorNotificationThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new CoordinatorNotificationThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		// Initialize the request dispatcher for the request/response, CoordinatorRequest/CoordinatorResponse
		this.coordinatorRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<CoordinatorRequest, CoordinatorStream, CoordinatorResponse, CoordinatorRequestThread, CoordinatorRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new CoordinatorRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMyDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateMyDataNotification, ReplicateMyDataThread, ReplicateMyDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateMyDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMuchMyDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateMuchMyDataNotification, ReplicateMuchMyDataThread, ReplicateMuchMyDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateMuchMyDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyDataRequest, PostfetchMyDataStream, PostfetchMyDataResponse, PostfetchMyDataRequestThread, PostfetchMyDataRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyDataRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyDataByKeysRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyDataByKeysRequest, PostfetchMyDataByKeysStream, PostfetchMyDataByKeysResponse, PostfetchMyDataByKeysRequestThread, PostfetchMyDataByKeysRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyDataByKeysRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMyPointingNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateMyPointingNotification, ReplicateMyPointingThread, ReplicateMyPointingThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateMyPointingThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMyPointingsNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateMyPointingsNotification, ReplicateMyPointingsThread, ReplicateMyPointingsThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateMyPointingsThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.prefetchMyPointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PrefetchMyPointingsRequest, PrefetchMyPointingsStream, PrefetchMyPointingsResponse, PrefetchMyPointingsRequestThread, PrefetchMyPointingsRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PrefetchMyPointingsRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyPointingRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyPointingByKeyRequest, PostfetchMyPointingStream, PostfetchMyPointingByKeyResponse, PostfetchMyPointingRequestThread, PostfetchMyPointingRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyPointingRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyPointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyPointingsRequest, PostfetchMyPointingsStream, PostfetchMyPointingsResponse, PostfetchMyPointingsRequestThread, PostfetchMyPointingsRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyPointingsRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyPointingByIndexRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyPointingByIndexRequest, PostfetchMyPointingByIndexStream, PostfetchMyPointingByIndexResponse, PostfetchMyPointingByIndexRequestThread, PostfetchMyPointingByIndexRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyPointingByIndexRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMinMyPointingRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMinMyPointingRequest, PostfetchMinMyPointingStream, PostfetchMinMyPointingResponse, PostfetchMinMyPointingRequestThread, PostfetchMinMyPointingRequestThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMinMyPointingRequestThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMyCachePointingNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateCachePointingNotification, ReplicateCachePointingThread, ReplicateCachePointingThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateCachePointingThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMyCachePointingsNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateCachePointingsNotification, ReplicateCachePointingsThread, ReplicateCachePointingsThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateCachePointingsThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.prefetchMyCachePointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PrefetchMyCachePointingsRequest, PrefetchMyCachePointingsStream, PrefetchMyCachePointingsResponse, PrefetchMyCachePointingsThread, PrefetchMyCachePointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PrefetchMyCachePointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyCachePointingByIndexRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyCachePointingByIndexRequest, PostfetchMyCachePointingByIndexStream, PostfetchMyCachePointingByIndexResponse, PostfetchMyCachePointingByIndexThread, PostfetchMyCachePointingByIndexThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyCachePointingByIndexThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyCachePointingByKeyRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyCachePointingByKeyRequest, PostfetchMyCachePointingByKeyStream, PostfetchMyCachePointingByKeyResponse, PostfetchMyCachePointingByKeyThread, PostfetchMyCachePointingByKeyThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyCachePointingByKeyThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyCachePointingsRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyCachePointingsRequest, PostfetchMyCachePointingsStream, PostfetchMyCachePointingsResponse, PostfetchMyCachePointingsThread, PostfetchMyCachePointingsThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyCachePointingsThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pushMyStoreDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PushMyStoreDataNotification, PushMyStoreDataThread, PushMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new PushMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.pushMuchMyStoreDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<PushMuchMyStoreDataNotification, PushMuchMyStoreDataThread, PushMuchMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new PushMuchMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.popMyStoreDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PopMyStoreDataRequest, PopMyStoreDataStream, PopMyStoreDataResponse, PopMyStoreDataThread, PopMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PopMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.enqueueMyStoreDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<EnqueueMyStoreDataNotification, EnqueueMyStoreDataThread, EnqueueMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new EnqueueMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.enqueueMuchMyStoreDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<EnqueueMuchMyStoreDataNotification, EnqueueMuchMyStoreDataThread, EnqueueMuchMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new EnqueueMuchMyStoreDataThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.dequeueMyStoreDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<DequeueMyStoreDataRequest, DequeueMyStoreDataStream, DequeueMyStoreDataResponse, DequeueMyStoreDataThread, DequeueMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new DequeueMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMyStoreDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateMyStoreDataMapStoreNotification, ReplicateMyStoreDataMapStoreThread, ReplicateMyStoreDataMapStoreThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateMyStoreDataMapStoreThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMuchMyStoreDataNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateMuchMyStoreDataMapStoreNotification, ReplicateMuchMyStoreDataMapStoreThread, ReplicateMuchMyStoreDataMapStoreThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateMuchMyStoreDataMapStoreThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyStoreDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyStoreDataRequest, PostfetchMyStoreDataStream, PostfetchMyStoreDataResponse, PostfetchMyStoreDataThread, PostfetchMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMuchMyStoreDataRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMuchMyStoreDataRequest, PostfetchMuchMyStoreDataStream, PostfetchMuchMyStoreDataResponse, PostfetchMuchMyStoreDataThread, PostfetchMuchMyStoreDataThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMuchMyStoreDataThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyStoreDataKeysRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyStoreDataKeysRequest, PostfetchMyStoreDataKeysStream, PostfetchMyStoreDataKeysResponse, PostfetchMyStoreDataKeysThread, PostfetchMyStoreDataKeysThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyStoreDataKeysThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.prefetchMyUKValuesRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PrefetchMyUKValuesRequest, PrefetchMyUKValuesStream, PrefetchMyUKValuesResponse, PrefetchMyUKValuesThread, PrefetchMyUKValuesThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PrefetchMyUKValuesThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.replicateMyUKValuesNotificationDispatcher = new NotificationDispatcher.NotificationDispatcherBuilder<ReplicateMyUKValueNotification, ReplicateMyUKValuesThread, ReplicateMyUKValuesThreadCreator>()
				.poolSize(ServerConfig.NOTIFICATION_DISPATCHER_POOL_SIZE)
				.threadCreator(new ReplicateMyUKValuesThreadCreator())
				.notificationQueueSize(ServerConfig.NOTIFICATION_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.NOTIFICATION_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyUKValuesByIndexRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyUKValueByIndexRequest, PostfetchMyUKValueByIndexStream, PostfetchMyUKValueByIndexResponse, PostfetchMyUKValueByIndexThread, PostfetchMyUKValueByIndexThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyUKValueByIndexThreadCreator())
				.requestQueueSize(ServerConfig.REQUEST_QUEUE_SIZE)
				.dispatcherWaitTime(ServerConfig.REQUEST_DISPATCHER_WAIT_TIME)
				.waitRound(ServerConfig.REQUEST_DISPATCHER_WAIT_ROUND)
				.idleCheckDelay(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_DELAY)
				.idleCheckPeriod(ServerConfig.REQUEST_DISPATCHER_IDLE_CHECK_PERIOD)
				.scheduler(super.getScheduler())
				.build();

		this.postfetchMyUKValuesRequestDispatcher = new RequestDispatcher.RequestDispatcherBuilder<PostfetchMyUKValuesRequest, PostfetchMyUKValuesStream, PostfetchMyUKValuesResponse, PostfetchMyUKValuesThread, PostfetchMyUKValuesThreadCreator>()
				.poolSize(ServerConfig.REQUEST_DISPATCHER_POOL_SIZE)
				.threadCreator(new PostfetchMyUKValuesThreadCreator())
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
		this.coordinatorNotificationDispatcher.dispose();
		this.coordinatorRequestDispatcher.dispose();
		this.replicateMyDataNotificationDispatcher.dispose();
		this.replicateMuchMyDataNotificationDispatcher.dispose();
		this.postfetchMyDataRequestDispatcher.dispose();
		this.postfetchMyDataByKeysRequestDispatcher.dispose();
		this.replicateMyPointingNotificationDispatcher.dispose();
		this.replicateMyPointingsNotificationDispatcher.dispose();
		this.prefetchMyPointingsRequestDispatcher.dispose();
		this.postfetchMyPointingRequestDispatcher.dispose();
		this.postfetchMyPointingsRequestDispatcher.dispose();
		this.postfetchMyPointingByIndexRequestDispatcher.dispose();
		this.postfetchMinMyPointingRequestDispatcher.dispose();
		this.replicateMyCachePointingNotificationDispatcher.dispose();
		this.replicateMyCachePointingsNotificationDispatcher.dispose();
		this.postfetchMyCachePointingByIndexRequestDispatcher.dispose();
		this.prefetchMyCachePointingsRequestDispatcher.dispose();
		this.postfetchMyCachePointingByKeyRequestDispatcher.dispose();
		this.postfetchMyCachePointingsRequestDispatcher.dispose();
		this.pushMyStoreDataNotificationDispatcher.dispose();
		this.pushMuchMyStoreDataNotificationDispatcher.dispose();
		this.popMyStoreDataRequestDispatcher.dispose();
		this.enqueueMyStoreDataNotificationDispatcher.dispose();
		this.enqueueMuchMyStoreDataNotificationDispatcher.dispose();
		this.dequeueMyStoreDataRequestDispatcher.dispose();

		this.replicateMyStoreDataNotificationDispatcher.dispose();
		this.replicateMuchMyStoreDataNotificationDispatcher.dispose();
		this.postfetchMyStoreDataRequestDispatcher.dispose();
		this.postfetchMuchMyStoreDataRequestDispatcher.dispose();
		this.postfetchMyStoreDataKeysRequestDispatcher.dispose();

		this.prefetchMyUKValuesRequestDispatcher.dispose();
		this.replicateMyUKValuesNotificationDispatcher.dispose();
		this.postfetchMyUKValuesByIndexRequestDispatcher.dispose();
		this.postfetchMyUKValuesRequestDispatcher.dispose();
	}

	@Override
	public void process(MessageStream<ServerMessage> message)
	{
		switch (message.getMessage().getType())
		{
			case CPSMessageType.COORDINATOR_NOTIFICATION:
				System.out.println("COORDINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.coordinatorNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.coordinatorNotificationDispatcher);
				}
				// Enqueue the instance of CoordinatorNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.coordinatorNotificationDispatcher.enqueue((CoordinatorNotification)message.getMessage());
				break;
				
			case CPSMessageType.COORDINATOR_REQUEST:
				System.out.println("COORDINATOR_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.coordinatorRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.coordinatorRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.coordinatorRequestDispatcher.enqueue(new CoordinatorStream(message.getOutStream(), message.getLock(), (CoordinatorRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.REPLICATE_MY_DATA_NOTIFICATION:
				System.out.println("REPLICATE_MY_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMyDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMyDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMyDataNotificationDispatcher.enqueue((ReplicateMyDataNotification)message.getMessage());
				break;

			case TestCacheMessageType.REPLICATE_MUCH_MY_DATA_NOTIFICATION:
				System.out.println("REPLICATE_MUCH_MY_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMuchMyDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMuchMyDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMuchMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMuchMyDataNotificationDispatcher.enqueue((ReplicateMuchMyDataNotification)message.getMessage());
				break;

			case TestCacheMessageType.POSTFETCH_MY_DATA_REQUEST:
				System.out.println("POSTFETCH_MY_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyDataRequestDispatcher.enqueue(new PostfetchMyDataStream(message.getOutStream(), message.getLock(), (PostfetchMyDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POSTFETCH_MY_DATA_BY_KEYS_REQUEST:
				System.out.println("POSTFETCH_MY_DATA_BY_KEYS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyDataByKeysRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyDataByKeysRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyDataByKeysRequestDispatcher.enqueue(new PostfetchMyDataByKeysStream(message.getOutStream(), message.getLock(), (PostfetchMyDataByKeysRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.REPLICATE_MY_POINTING_NOTIFICATION:
				System.out.println("REPLICATE_MY_POINTING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMyPointingNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMyPointingNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyPointingNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMyPointingNotificationDispatcher.enqueue((ReplicateMyPointingNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.REPLICATE_MY_POINTINGS_LIST_NOTIFICATION:
				System.out.println("REPLICATE_MY_POINTINGS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMyPointingsNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMyPointingsNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyPointingNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMyPointingsNotificationDispatcher.enqueue((ReplicateMyPointingsNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.PREFETCH_MY_POINTINGS_REQUEST:
				System.out.println("PREFETCH_MY_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.prefetchMyPointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.prefetchMyPointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.prefetchMyPointingsRequestDispatcher.enqueue(new PrefetchMyPointingsStream(message.getOutStream(), message.getLock(), (PrefetchMyPointingsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.POSTFETCH_MY_POINTING_BY_KEY_REQUEST:
				System.out.println("POSTFETCH_MY_POINTING_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyPointingRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyPointingRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyPointingRequestDispatcher.enqueue(new PostfetchMyPointingStream(message.getOutStream(), message.getLock(), (PostfetchMyPointingByKeyRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.POSTFETCH_MY_POINTINGS_REQUEST:
				System.out.println("POSTFETCH_MY_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyPointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyPointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyPointingsRequestDispatcher.enqueue(new PostfetchMyPointingsStream(message.getOutStream(), message.getLock(), (PostfetchMyPointingsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.POSTFETCH_MY_POINTING_BY_INDEX_REQUEST:
				System.out.println("POSTFETCH_MY_POINTING_BY_INDEX_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyPointingByIndexRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyPointingByIndexRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyPointingByIndexRequestDispatcher.enqueue(new PostfetchMyPointingByIndexStream(message.getOutStream(), message.getLock(), (PostfetchMyPointingByIndexRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.POSTFETCH_MIN_MY_POINTING_REQUEST:
				System.out.println("POSTFETCH_MIN_MY_POINTING_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMinMyPointingRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMinMyPointingRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMinMyPointingRequestDispatcher.enqueue(new PostfetchMinMyPointingStream(message.getOutStream(), message.getLock(), (PostfetchMinMyPointingRequest)message.getMessage()));
				break;

			case TestCacheMessageType.REPLICATE_CACHE_POINTING_NOTIFICATION:
				System.out.println("REPLICATE_CACHE_POINTING_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMyCachePointingNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMyCachePointingNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMyCachePointingNotificationDispatcher.enqueue((ReplicateCachePointingNotification)message.getMessage());
				break;

			case TestCacheMessageType.REPLICATE_CACHE_POINTINGS_NOTIFICATION:
				System.out.println("REPLICATE_CACHE_POINTINGS_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMyCachePointingsNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMyCachePointingsNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMyCachePointingsNotificationDispatcher.enqueue((ReplicateCachePointingsNotification)message.getMessage());
				break;

			case TestCacheMessageType.PREFETCH_MY_CACHE_POINTINGS_REQUEST:
				System.out.println("PREFETCH_MY_CACHE_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.prefetchMyCachePointingsRequestDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.prefetchMyCachePointingsRequestDispatcher);
				}
				// Enqueue the instance of PrefetchMyCachePointingsRequest into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.prefetchMyCachePointingsRequestDispatcher.enqueue(new PrefetchMyCachePointingsStream(message.getOutStream(), message.getLock(), (PrefetchMyCachePointingsRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_INDEX_REQUEST:
				System.out.println("POSTFETCH_MY_CACHE_POINTING_BY_INDEX_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyCachePointingByIndexRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyCachePointingByIndexRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyCachePointingByIndexRequestDispatcher.enqueue(new PostfetchMyCachePointingByIndexStream(message.getOutStream(), message.getLock(), (PostfetchMyCachePointingByIndexRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POSTFETCH_MY_CACHE_POINTING_BY_KEY_REQUEST:
				System.out.println("POSTFETCH_MY_CACHE_POINTING_BY_KEY_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyCachePointingByKeyRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyCachePointingByKeyRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyCachePointingByKeyRequestDispatcher.enqueue(new PostfetchMyCachePointingByKeyStream(message.getOutStream(), message.getLock(), (PostfetchMyCachePointingByKeyRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POSTFETCH_MY_CACHE_POINTINGS_REQUEST:
				System.out.println("POSTFETCH_MY_CACHE_POINTINGS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyCachePointingsRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyCachePointingsRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyCachePointingsRequestDispatcher.enqueue(new PostfetchMyCachePointingsStream(message.getOutStream(), message.getLock(), (PostfetchMyCachePointingsRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.PUSH_MY_STORE_DATA_NOTIFICATION:
				System.out.println("PUSH_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.pushMyStoreDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.pushMyStoreDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.pushMyStoreDataNotificationDispatcher.enqueue((PushMyStoreDataNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.PUSH_MUCH_MY_STORE_DATA_NOTIFICATION:
				System.out.println("PUSH_MUCH_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.pushMuchMyStoreDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.pushMuchMyStoreDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.pushMuchMyStoreDataNotificationDispatcher.enqueue((PushMuchMyStoreDataNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.POP_MY_STORE_DATA_REQUEST:
				System.out.println("POP_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.popMyStoreDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.popMyStoreDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.popMyStoreDataRequestDispatcher.enqueue(new PopMyStoreDataStream(message.getOutStream(), message.getLock(), (PopMyStoreDataRequest)message.getMessage()));
				break;
				
			case TestCacheMessageType.ENQUEUE_MY_STORE_DATA_NOTIFICATION:
				System.out.println("ENQUEUE_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.enqueueMyStoreDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.enqueueMyStoreDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.enqueueMyStoreDataNotificationDispatcher.enqueue((EnqueueMyStoreDataNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.ENQUEUE_MUCH_MY_STORE_DATA_NOTIFICATION:
				System.out.println("ENQUEUE_MUCH_MY_STORE_DATA_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.enqueueMuchMyStoreDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.enqueueMuchMyStoreDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.enqueueMuchMyStoreDataNotificationDispatcher.enqueue((EnqueueMuchMyStoreDataNotification)message.getMessage());
				break;
				
			case TestCacheMessageType.DEQUEUE_MY_STORE_DATA_REQUEST:
				System.out.println("DEQUEUE_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.dequeueMyStoreDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.dequeueMyStoreDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.dequeueMyStoreDataRequestDispatcher.enqueue(new DequeueMyStoreDataStream(message.getOutStream(), message.getLock(), (DequeueMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.REPLICATE_MY_STORE_DATA_MAP_STORE_NOTIFICATION:
				System.out.println("REPLICATE_MY_STORE_DATA_MAP_STORE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMyStoreDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMyStoreDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMyStoreDataNotificationDispatcher.enqueue((ReplicateMyStoreDataMapStoreNotification)message.getMessage());
				break;

			case TestCacheMessageType.REPLICATE_MUCH_MY_STORE_DATA_MAP_STORE_NOTIFICATION:
				System.out.println("REPLICATE_MUCH_MY_STORE_DATA_MAP_STORE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				// Check whether the adding friends notification dispatcher is ready or not. 02/15/2016, Bing Li
				if (!this.replicateMuchMyStoreDataNotificationDispatcher.isReady())
				{
					// Execute the notification dispatcher concurrently. 02/15/2016, Bing Li
					super.execute(this.replicateMuchMyStoreDataNotificationDispatcher);
				}
				// Enqueue the instance of ReplicateMyDataNotification into the dispatcher for concurrent processing. 02/15/2016, Bing Li
				this.replicateMuchMyStoreDataNotificationDispatcher.enqueue((ReplicateMuchMyStoreDataMapStoreNotification)message.getMessage());
				break;

			case TestCacheMessageType.POSTFETCH_MY_STORE_DATA_REQUEST:
				System.out.println("POSTFETCH_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyStoreDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyStoreDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyStoreDataRequestDispatcher.enqueue(new PostfetchMyStoreDataStream(message.getOutStream(), message.getLock(), (PostfetchMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POSTFETCH_MUCH_MY_STORE_DATA_REQUEST:
				System.out.println("POSTFETCH_MUCH_MY_STORE_DATA_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMuchMyStoreDataRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMuchMyStoreDataRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMuchMyStoreDataRequestDispatcher.enqueue(new PostfetchMuchMyStoreDataStream(message.getOutStream(), message.getLock(), (PostfetchMuchMyStoreDataRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POSTFETCH_MY_STORE_DATA_KEYS_REQUEST:
				System.out.println("POSTFETCH_MY_STORE_DATA_KEYS_REQUEST received @" + Calendar.getInstance().getTime());
				// Check whether the registry dispatcher is ready. 04/17/2017, Bing Li
				if (!this.postfetchMyStoreDataKeysRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyStoreDataKeysRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyStoreDataKeysRequestDispatcher.enqueue(new PostfetchMyStoreDataKeysStream(message.getOutStream(), message.getLock(), (PostfetchMyStoreDataKeysRequest)message.getMessage()));
				break;

			case TestCacheMessageType.PREFETCH_MY_UK_VALUES_REQUEST:
				System.out.println("PREFETCH_MY_UK_VALUES_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.prefetchMyUKValuesRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.prefetchMyUKValuesRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.prefetchMyUKValuesRequestDispatcher.enqueue(new PrefetchMyUKValuesStream(message.getOutStream(), message.getLock(), (PrefetchMyUKValuesRequest)message.getMessage()));
				break;

			case TestCacheMessageType.REPLICATE_MY_UK_VALUE_NOTIFICATION:
				System.out.println("REPLICATE_MY_UK_VALUE_NOTIFICATION received @" + Calendar.getInstance().getTime());
				if (!this.replicateMyUKValuesNotificationDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.replicateMyUKValuesNotificationDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.replicateMyUKValuesNotificationDispatcher.enqueue((ReplicateMyUKValueNotification)message.getMessage());
				break;

			case TestCacheMessageType.POSTFETCH_MY_UK_BY_INDEX_REQUEST:
				System.out.println("POSTFETCH_MY_UK_BY_INDEX_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.postfetchMyUKValuesByIndexRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyUKValuesByIndexRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyUKValuesByIndexRequestDispatcher.enqueue(new PostfetchMyUKValueByIndexStream(message.getOutStream(), message.getLock(), (PostfetchMyUKValueByIndexRequest)message.getMessage()));
				break;

			case TestCacheMessageType.POSTFETCH_MY_UKS_REQUEST:
				System.out.println("POSTFETCH_MY_UKS_REQUEST received @" + Calendar.getInstance().getTime());
				if (!this.postfetchMyUKValuesRequestDispatcher.isReady())
				{
					// Execute the registry dispatcher as a thread. 04/17/2017, Bing Li
					super.execute(this.postfetchMyUKValuesRequestDispatcher);
				}
				// Enqueue the request into the dispatcher for concurrent responding. 04/17/2017, Bing Li
				this.postfetchMyUKValuesRequestDispatcher.enqueue(new PostfetchMyUKValuesStream(message.getOutStream(), message.getLock(), (PostfetchMyUKValuesRequest)message.getMessage()));
				break;
		}
	}
}
