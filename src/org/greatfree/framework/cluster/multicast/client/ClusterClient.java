package org.greatfree.framework.cluster.multicast.client;

import java.io.IOException;
import java.util.List;

import org.greatfree.client.CSClient;
import org.greatfree.concurrency.SharedThreadPool;
import org.greatfree.data.ClientConfig;
import org.greatfree.exceptions.RemoteIPNotExistedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.framework.container.p2p.message.PeerAddressRequest;
import org.greatfree.framework.p2p.RegistryConfig;
import org.greatfree.message.PeerAddressResponse;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.multicast.container.CollectedClusterResponse;
import org.greatfree.util.IPAddress;
import org.greatfree.util.Tools;

/**
 * 
 * Revised for getting involved with the container-registry on 10/08/2022, Bing Li
 * 
 * @author libing
 * 
 * 10/02/2022
 *
 */
public final class ClusterClient
{
//	private final static Logger log = Logger.getLogger("edu.greatfree.cluster.root");

	private CSClient client;

	private ClusterClient()
	{
	}

	private static ClusterClient instance = new ClusterClient();
	
	public static ClusterClient MULTI()
	{
		if (instance == null)
		{
			instance = new ClusterClient();
			return instance;
		}
		else
		{
			return instance;
		}
	}
	
	public void dispose() throws InterruptedException
	{
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
				.asyncEventQueueWaitTime(RegistryConfig.ASYNC_EVENT_QUEUE_WAIT_TIME)
				.asyncEventIdleCheckDelay(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_DELAY)
				.asyncEventIdleCheckPeriod(RegistryConfig.ASYNC_EVENT_IDLE_CHECK_PERIOD)
				.schedulerPoolSize(RegistryConfig.SCHEDULER_THREAD_POOL_SIZE)
				.schedulerKeepAliveTime(RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME)
				.asyncSchedulerShutdownTimeout(ClientConfig.ASYNC_SCHEDULER_SHUTDOWN_TIMEOUT)
				.readerClientSize(RegistryConfig.READER_CLIENT_SIZE)
				.pool(SharedThreadPool.SHARED().getPool())
				.build();
	}

	public IPAddress getAddress(String registryIP, int registryPort, String peerName) throws ClassNotFoundException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		return ((PeerAddressResponse)this.client.read(registryIP, registryPort, new PeerAddressRequest(Tools.getNodeKey(peerName)))).getPeerAddress();
	}

	public void syncNotify(String ip, int port, ServerMessage notification) throws IOException, InterruptedException
	{
		this.client.syncNotify(ip, port, notification);
	}

	public <T> List<T> read(String ip, int port, ServerMessage request, Class<T> c) throws ClassNotFoundException, RemoteReadException, IOException, RemoteIPNotExistedException
	{
		CollectedClusterResponse response = (CollectedClusterResponse)this.client.read(ip, port, request);
		return Tools.filter(response.getResponses(), c);
	}
}

