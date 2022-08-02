package org.greatfree.server.container;

// Created: 01/09/2019, Bing Li
public class ContainerXML
{
	/*
	 * The statements to retrieve the server parameters. 01/09/2019, Bing Li
	 */
	public final static String SELECT_SERVER_PORT = "/Container/Port/text()";
	public final static String SELECT_LISTENING_THREAD_COUNT = "/Container/ListeningThreadCount/text()";
	public final static String SELECT_MAX_IO_COUNT = "/Container/MaxIOCount/text()";
	public final static String SELECT_SERVER_THREAD_POOL_SIZE = "/Container/ServerThreadPoolSize/text()";
	public final static String SELECT_SERVER_THREAD_KEEP_ALIVE_TIME = "/Container/ServerThreadKeepAliveTime/text()";
	public final static String SELECT_SCHEDULER_THREAD_POOL_SIZE = "/Container/SchedulerThreadPoolSize/text()";
	public final static String SELECT_SCHEDULER_THERAD_KEEP_ALIVE_TIME = "/Container/SchedulerThreadPoolKeepAliveTime/text()";
	
	/*
	 * The statements to retrieve the notification dispatcher parameters. 01/09/2019, Bing Li
	 */
	public final static String SELECT_NOTIFICATION_DISPATCHER_POOL_SIZE = "/Container/NotificationDispatcherPoolSize/text()";
	public final static String SELECT_NOTIFICATION_QUEUE_SIZE = "/Container/NotificationQueueSize/text()";
	public final static String SELECT_NOTIFICATION_DISPATCHER_WAIT_TIME = "/Container/NotificationDispatcherWaitTime/text()";
	public final static String SELECT_NOTIFICATION_DISPATCHER_WAIT_ROUND = "/Container/NotificationDispatcherWaitRound/text()";
	public final static String SELECT_NOTIFICATION_DISPATCHER_IDLE_CHECK_DELAY = "/Container/NotificationDispatcherIdleCheckDelay/text()";
	public final static String SELECT_NOTIFICATION_DISPATCHER_IDLE_CHECK_PERIOD = "/Container/NotificationDispatcherIdleCheckPeriod/text()";
	
	/*
	 * The statements to retrieve the request dispatcher parameters. 01/09/2019, Bing Li
	 */
	public final static String SELECT_REQUEST_DISPATCHER_POOL_SIZE = "/Container/RequestDispatcherPoolSize/text()";
	public final static String SELECT_REQUEST_QUEUE_SIZE = "/Container/RequestQueueSize/text()";
	public final static String SELECT_REQUEST_DISPATCHER_WAIT_TIME = "/Container/RequestDispatcherWaitTime/text()";
	public final static String SELECT_REQUEST_DISPATCHER_WAIT_ROUND = "/Container/RequestDispatcherWaitRound/text()";
	public final static String SELECT_REQUEST_DISPATCHER_IDLE_CHECK_DELAY = "/Container/RequestDispatcherIdleCheckDelay/text()";
	public final static String SELECT_REQUEST_DISPATCHER_IDLE_CHECK_PERIOD = "/Container/RequestDispatcherIdleCheckPeriod/text()";
	
	/*
	 * The statements to retrieve the peer parameters. 01/09/2019, Bing Li
	 */
	public final static String SELECT_PEER_NAME = "/Container/PeerName/text()";
	public final static String SELECT_REGISTRY_SERVER_IP = "/Container/RegistryServerIP/text()";
	public final static String SELECT_REGISTRY_SERVER_PORT = "/Container/RegistryServerPort/text()";
	public final static String SELECT_IS_REGISTRY_NEEDED = "/Container/IsRegistryNeeded/text()";
	public final static String SELECT_FREE_CLIENT_POOL_SIZE = "/Container/FreeClientPoolSize/text()";
	public final static String SELECT_READER_CLIENT_SIZE = "/Container/ReaderClientSize/text()";
	public final static String SELECT_SYNC_EVENTER_IDLE_CHECK_DELAY = "/Container/SyncEventerIdleCheckDelay/text()";
	public final static String SELECT_SYNC_EVENTER_IDLE_CHECK_PERIOD = "/Container/SyncEventerIdleCheckPeriod/text()";
	public final static String SELECT_SYNC_EVENTER_MAX_IDLE_TIME = "/Container/SyncEventerMaxIdleTime/text()";
	public final static String SELECT_ASYNC_EVENT_QUEUE_SIZE = "/Container/AsyncEventQueueSize/text()";
	public final static String SELECT_ASYNC_EVENTER_SIZE = "/Container/AsyncEventerSize/text()";
	public final static String SELECT_ASYNC_EVENTING_WAIT_TIME = "/Container/AsyncEventingWaitTime/text()";
	public final static String SELECT_ASYNC_EVENTER_WAIT_TIME = "/Container/AsyncEventerWaitTime/text()";
//	public final static String SELECT_ASYNC_EVENTER_WAIT_ROUND = "/Container/AsyncEventerWaitRound/text()";
	public final static String SELECT_ASYNC_EVENT_IDLE_CHECK_DELAY = "/Container/AsyncEventIdleCheckDelay/text()";
	public final static String SELECT_ASYNC_EVENT_IDLE_CHECK_PERIOD = "/Container/AsyncEventIdleCheckPeriod/text()";
	public final static String SELECT_EVENT_SCHEDULER_POOL_SIZE = "/Container/EventSchedulerPoolSize/text()";
	public final static String SELECT_EVENT_SCHEDULER_KEEP_ALIVE_TIME = "/Container/EventSchedulerKeepAliveTime/text()";
	
	/*
	 * The statements to retrieve the cluster parameters. 01/09/2019, Bing Li
	 */
	public final static String SELECT_SCHEDULER_POOL_SIZE = "/Container/SchedulerPoolSize/text()";
	public final static String SELECT_SCHEDULER_KEEP_ALIVE_TIME = "/Container/SchedulerKeepAliveTime/text()";
	public final static String SELECT_SCHEDULER_SHUTDOWN_TIME_OUT = "/Container/SchedulerShutdownTimeout/text()";
	public final static String SELECT_ROOT_BRANCH_COUNT = "/Container/RootBranchCount/text()";
	public final static String SELECT_SUB_BRANCH_COUNT = "/Container/SubBranchCount/text()";
	public final static String SELECT_BROADCAST_REQUEST_WAIT_TIME = "/Container/BroadcastRequestWaitTime/text()";
	public final static String SELECT_ROOT_NAME = "/Container/RootName/text()";
}
