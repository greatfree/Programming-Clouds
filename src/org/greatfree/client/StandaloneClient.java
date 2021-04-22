package org.greatfree.client;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.Future;

import org.greatfree.cluster.message.AdditionalChildrenRequest;
import org.greatfree.cluster.message.AdditionalChildrenResponse;
import org.greatfree.cluster.message.ClusterSizeRequest;
import org.greatfree.cluster.message.ClusterSizeResponse;
import org.greatfree.cluster.message.PartitionSizeRequest;
import org.greatfree.cluster.message.PartitionSizeResponse;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.FutureExceptionHandler;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.multicast.message.PeerAddressResponse;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.ServerMessage;
import org.greatfree.util.IPAddress;

// Created: 01/10/2019, Bing Li
public class StandaloneClient
{
	private CSClient client;

	private StandaloneClient()
	{
	}

	/*
	 * Initialize a singleton. 04/23/2017, Bing Li
	 */
	private static StandaloneClient instance = new StandaloneClient();
	
	public static StandaloneClient CS()
	{
		if (instance == null)
		{
			instance = new StandaloneClient();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void dispose() throws IOException, InterruptedException
	{
		SharedThreadPool.SHARED().dispose(ServerConfig.SHARED_THREAD_POOL_SHUTDOWN_TIMEOUT);
		this.client.dispose();
	}

	public void init() throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.clientIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.clientIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.clientMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.build();
		
		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		this.client.init(SharedThreadPool.SHARED().getPool());
	}

	public void init(FutureExceptionHandler handler) throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.clientIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.clientIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.clientMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.readTimeoutExceptionHandler(handler)
				.build();
		
		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		this.client.init(SharedThreadPool.SHARED().getPool());
	}

	/*
	public void init(ScheduledThreadPoolExecutor scheduler) throws ClassNotFoundException, RemoteReadException, IOException
	{
		this.client = new CSClient.CSClientBuilder()
				.freeClientPoolSize(RegistryConfig.CLIENT_POOL_SIZE)
				.clientIdleCheckDelay(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_DELAY)
				.clientIdleCheckPeriod(RegistryConfig.SYNC_EVENTER_IDLE_CHECK_PERIOD)
				.clientMaxIdleTime(RegistryConfig.SYNC_EVENTER_MAX_IDLE_TIME)
				.asyncEventQueueSize(RegistryConfig.ASYNC_EVENT_QUEUE_SIZE)
				.asyncEventerSize(RegistryConfig.ASYNC_EVENTER_SIZE)
				.asyncEventingWaitTime(RegistryConfig.ASYNC_EVENTING_WAIT_TIME)
				.asyncEventerWaitTime(RegistryConfig.ASYNC_EVENTER_WAIT_TIME)
				.asyncEventerWaitRound(RegistryConfig.ASYNC_EVENTER_WAIT_ROUND)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.scheduler(scheduler)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.build();
		
		SharedThreadPool.SHARED().init(ServerConfig.SHARED_THREAD_POOL_SIZE, ServerConfig.SHARED_THREAD_POOL_KEEP_ALIVE_TIME);
		this.client.init(SharedThreadPool.SHARED().getPool());
	}
	*/

	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.client.syncNotify(ip, port, notification);
	}
	
	public void asyncNotify(String ip, int port, ServerMessage notification)
	{
		this.client.asyncNotify(ip, port, notification);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.client.read(ip, port, request);
	}
	
	public ServerMessage read(String ip, int port, ServerMessage request, int timeout) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return this.client.read(ip, port, request, timeout);
	}
	
	public Future<ServerMessage> futureRead(String ip, int port, ServerMessage request)
	{
		return this.client.futureRead(ip, port, request);
	}
	
	public Future<ServerMessage> futureRead(String ip, int port, ServerMessage request, int timeout)
	{
		return this.client.futureRead(ip, port, request, timeout);
	}

	/*
	 * The method is common in many cases to access the IP of one node. 09/09/2020, Bing Li 
	 */
	public IPAddress getIPAddress(String registryIP, int registryPort, String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((PeerAddressResponse)this.client.read(registryIP,  registryPort, new PeerAddressRequest(nodeKey))).getPeerAddress();
	}

	/*
	 * The method is useful for most storage systems, which need the partition information to design the upper level distribution strategy. 09/09/2020, Bing Li
	 */
	public int getPartitionSize(String clusterIP, int clusterPort) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((PartitionSizeResponse)this.client.read(clusterIP,  clusterPort, new PartitionSizeRequest())).getPartitionSize();
	}
	
	/*
	 * The message is designed for the scalability such that all of the current children are replaced by new coming ones. In the storage system, the current ones is full in the disk space. In the case, they have to be replaced. But in other cases, it depends on the application level how to raise the scale and deal with the existing children. The system level cannot help. 09/12/2020, Bing Li
	 * 
	 * The message is an internal one, like the PartitionSizeRequest/PartitionSizeResponse, which is processed by the cluster root only. Programmers do not need to do anything but send it. So it inherits ServerMessage. 09/12/2020, Bing Li
	 */
	public int getClusterSize(String clusterIP, int clusterPort) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((ClusterSizeResponse)this.client.read(clusterIP,  clusterPort, new ClusterSizeRequest())).getSize();
	}

	/*
	 * When additional children are needed by the task cluster, sometimes those children should be initialized or configured before joining. The method serves this goal. 09/13/2020, Bing Li
	 */
	public Set<String> getChildrenKeys(String clusterIP, int clusterPort, int size) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return ((AdditionalChildrenResponse)this.client.read(clusterIP, clusterPort, new AdditionalChildrenRequest(size))).getChildrenKeys();
	}
}
