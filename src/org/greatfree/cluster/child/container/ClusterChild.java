package org.greatfree.cluster.child.container;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.ServerMessage;
import org.greatfree.message.container.Notification;
import org.greatfree.message.container.Request;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.ClusterNotification;
import org.greatfree.server.container.Peer.PeerBuilder;
import org.greatfree.util.Builder;
import org.greatfree.util.IPAddress;

/*
 * The reason to design the class, ClusterChild, intends to connect with the registry server which is implemented in the container manner. 01/13/2019, Bing Li
 */

// Created: 01/13/2019, Bing Li
final class ClusterChild
{
//	private final int treeBranchCount;
	
	public ClusterChild(ClusterChildBuilder builder) throws IOException
	{
		PeerBuilder<ChildDispatcher> peerBuilder = new PeerBuilder<ChildDispatcher>();

		peerBuilder.peerPort(builder.getPeerPort())
			.peerPort(builder.getPeerPort())
			.peerName(builder.getPeerName())
			.registryServerIP(builder.getRegistryServerIP())
			.registryServerPort(builder.getRegistryServerPort())
			.isRegistryNeeded(builder.isRegistryNeeded())
			.listenerCount(builder.getListenerCount())
//			.serverThreadPoolSize(builder.getServerThreadPoolSize())
//			.serverThreadKeepAliveTime(builder.getServerThreadKeepAliveTime())
			.dispatcher(new ChildDispatcher(builder.getServerThreadPoolSize(), builder.getServerThreadKeepAliveTime(), builder.getSchedulerPoolSize(), builder.getSchedulerKeepAliveTime()))
			.freeClientPoolSize(builder.getClientPoolSize())
			.readerClientSize(builder.getReaderClientSize())
			.syncEventerIdleCheckDelay(builder.getClientIdleCheckDelay())
			.syncEventerIdleCheckPeriod(builder.getClientIdleCheckPeriod())
			.syncEventerMaxIdleTime(builder.getClientMaxIdleTime())
			.asyncEventQueueSize(builder.getAsyncEventQueueSize())
			.asyncEventerSize(builder.getAsyncEventerSize())
			.asyncEventingWaitTime(builder.getAsyncEventingWaitTime())
			.asyncEventerWaitTime(builder.getAsyncEventerWaitTime())
			.asyncEventerWaitRound(builder.getAsyncEventerWaitRound())
			.asyncEventIdleCheckDelay(builder.getAsyncEventIdleCheckDelay())
			.asyncEventIdleCheckPeriod(builder.getAsyncEventIdleCheckPeriod())
			.schedulerPoolSize(builder.getSchedulerPoolSize())
			.scheulerKeepAliveTime(builder.getSchedulerKeepAliveTime());

//		this.treeBranchCount = builder.getTreeBranchCount();
		Child.CONTAINER().init(peerBuilder, builder.getRootBranchCount(), builder.getTreeBranchCount(), builder.getRequestWaitTime());
	}

	public static class ClusterChildBuilder implements Builder<ClusterChild>
	{
		private String peerName;
		private int peerPort;
		private String registryServerIP;
		private int registryServerPort;
		private boolean isRegistryNeeded;
		private int listenerCount;
		// The size of the thread pool that manages the threads to listen the port. 05/11/2017, Bing Li
		private int serverThreadPoolSize;
		// The time to keep alive for threads that listen to the port. 05/11/2017, Bing Li
		private long serverThreadKeepAliveTime;
		
//		private int dispatcherThreadPoolSize;
//		private long dispatcherThreadKeepAliveTime;

		private int clientPoolSize;
		private int readerClientSize;
		private long clientIdleCheckDelay;
		private long clientIdleCheckPeriod;
		private long clientMaxIdleTime;
		
		private int asyncEventQueueSize;
		private int asyncEventerSize;
		private long asyncEventingWaitTime;
		private long asyncEventerWaitTime;
		private int asyncEventerWaitRound;
		private long asyncEventIdleCheckDelay;
		private long asyncEventIdleCheckPeriod;
		
		private int schedulerPoolSize;
		private long schedulerKeepAliveTime;

		// The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		private int rootBranchCount;
		private int treeBranchCount;
		// The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		private long requestWaitTime;
		
		public ClusterChildBuilder()
		{
		}
		
		public ClusterChildBuilder peerName(String peerName)
		{
			this.peerName = peerName;
			return this;
		}

		public ClusterChildBuilder peerPort(int peerPort)
		{
			this.peerPort = peerPort;
			return this;
		}
		
		public ClusterChildBuilder registryServerIP(String registryServerIP)
		{
			this.registryServerIP = registryServerIP;
			return this;
		}
		
		public ClusterChildBuilder registryServerPort(int registryServerPort)
		{
			this.registryServerPort = registryServerPort;
			return this;
		}
		
		public ClusterChildBuilder isRegistryNeeded(boolean isRegistryNeeded)
		{
			this.isRegistryNeeded = isRegistryNeeded;
			return this;
		}

		public ClusterChildBuilder listenerCount(int listenerCount)
		{
			this.listenerCount = listenerCount;
			return this;
		}
		
		public ClusterChildBuilder serverThreadPoolSize(int listenerThreadPoolSize)
		{
			this.serverThreadPoolSize = listenerThreadPoolSize;
			return this;
		}

		/*
		public ClusterChildBuilder dispatcherThreadPoolSize(int dispatcherThreadPoolSize)
		{
			this.dispatcherThreadPoolSize = dispatcherThreadPoolSize;
			return this;
		}
		
		public ClusterChildBuilder dispatcherThreadKeepAliveTime(long dispatcherThreadKeepAliveTime)
		{
			this.dispatcherThreadKeepAliveTime = dispatcherThreadKeepAliveTime;
			return this;
		}
		*/
		
		public ClusterChildBuilder serverThreadKeepAliveTime(long listenerThreadKeepAliveTime)
		{
			this.serverThreadKeepAliveTime = listenerThreadKeepAliveTime;
			return this;
		}

		public ClusterChildBuilder clientPoolSize(int clientPoolSize)
		{
			this.clientPoolSize = clientPoolSize;
			return this;
		}
		
		public ClusterChildBuilder readerClientSize(int readerClientSize)
		{
			this.readerClientSize = readerClientSize;
			return this;
		}
		
		public ClusterChildBuilder syncEventerIdleCheckDelay(long idleCheckDelay)
		{
			this.clientIdleCheckDelay = idleCheckDelay;
			return this;
		}
		
		public ClusterChildBuilder syncEventerIdleCheckPeriod(long idleCheckPeriod)
		{
			this.clientIdleCheckPeriod = idleCheckPeriod;
			return this;
		}

		public ClusterChildBuilder syncEventerMaxIdleTime(long maxIdleTime)
		{
			this.clientMaxIdleTime = maxIdleTime;
			return this;
		}

		public ClusterChildBuilder asyncEventQueueSize(int asyncEventQueueSize)
		{
			this.asyncEventQueueSize = asyncEventQueueSize;
			return this;
		}

		public ClusterChildBuilder asyncEventerSize(int asyncEventerSize)
		{
			this.asyncEventerSize = asyncEventerSize;
			return this;
		}

		public ClusterChildBuilder asyncEventingWaitTime(long asyncEventingWaitTime)
		{
			this.asyncEventingWaitTime = asyncEventingWaitTime;
			return this;
		}

		public ClusterChildBuilder asyncEventerWaitTime(long asyncEventerWaitTime)
		{
			this.asyncEventerWaitTime = asyncEventerWaitTime;
			return this;
		}

		public ClusterChildBuilder asyncEventerWaitRound(int asyncEventerWaitRound)
		{
			this.asyncEventerWaitRound = asyncEventerWaitRound;
			return this;
		}

		public ClusterChildBuilder asyncEventIdleCheckDelay(long asyncEventIdleCheckDelay)
		{
			this.asyncEventIdleCheckDelay = asyncEventIdleCheckDelay;
			return this;
		}

		public ClusterChildBuilder asyncEventIdleCheckPeriod(long asyncEventIdleCheckPeriod)
		{
			this.asyncEventIdleCheckPeriod = asyncEventIdleCheckPeriod;
			return this;
		}

		public ClusterChildBuilder schedulerPoolSize(int schedulerPoolSize)
		{
			this.schedulerPoolSize = schedulerPoolSize;
			return this;
		}

		public ClusterChildBuilder schedulerKeepAliveTime(long schedulerKeepAliveTime)
		{
			this.schedulerKeepAliveTime = schedulerKeepAliveTime;
			return this;
		}
		
		/*
		 * The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		 */
		public ClusterChildBuilder rootBranchCount(int rootBranchCount)
		{
			this.rootBranchCount = rootBranchCount;
			return this;
		}

		public ClusterChildBuilder treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}
		
		/*
		 * The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		 */
		public ClusterChildBuilder requestWaitTime(long requestWaitTime)
		{
			this.requestWaitTime = requestWaitTime;
			return this;
		}

		@Override
		public ClusterChild build() throws IOException
		{
			return new ClusterChild(this);
		}
		
		public String getPeerName()
		{
			return this.peerName;
		}
		
		public int getPeerPort()
		{
			return this.peerPort;
		}
		
		public String getRegistryServerIP()
		{
			return this.registryServerIP;
		}
		
		public int getRegistryServerPort()
		{
			return this.registryServerPort;
		}
		
		public boolean isRegistryNeeded()
		{
			return this.isRegistryNeeded;
		}
		
		public int getListenerCount()
		{
			return this.listenerCount;
		}

		public int getServerThreadPoolSize()
		{
			return this.serverThreadPoolSize;
		}
		
		public long getServerThreadKeepAliveTime()
		{
			return this.serverThreadKeepAliveTime;
		}

		/*
		public int getDispatcherThreadPoolSize()
		{
			return this.dispatcherThreadPoolSize;
		}
		
		public long getDispatcherThreadKeepAliveTime()
		{
			return this.dispatcherThreadKeepAliveTime;
		}
		*/
		
		public int getClientPoolSize()
		{
			return this.clientPoolSize;
		}
		
		public int getReaderClientSize()
		{
			return this.readerClientSize;
		}
		
		public long getClientIdleCheckDelay()
		{
			return this.clientIdleCheckDelay;
		}
		
		public long getClientIdleCheckPeriod()
		{
			return this.clientIdleCheckPeriod;
		}
		
		public long getClientMaxIdleTime()
		{
			return this.clientMaxIdleTime;
		}
		
		public int getAsyncEventQueueSize()
		{
			return this.asyncEventQueueSize;
		}
		
		public int getAsyncEventerSize()
		{
			return this.asyncEventerSize;
		}
		
		public long getAsyncEventingWaitTime()
		{
			return this.asyncEventingWaitTime;
		}
		
		public long getAsyncEventerWaitTime()
		{
			return this.asyncEventerWaitTime;
		}
		
		public int getAsyncEventerWaitRound()
		{
			return this.asyncEventerWaitRound;
		}
		
		public long getAsyncEventIdleCheckDelay()
		{
			return this.asyncEventIdleCheckDelay;
		}
		
		public long getAsyncEventIdleCheckPeriod()
		{
			return this.asyncEventIdleCheckPeriod;
		}

		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}

		/*
		 * The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		 */
		public int getRootBranchCount()
		{
			return this.rootBranchCount;
		}

		public int getTreeBranchCount()
		{
			return this.treeBranchCount;
		}
		
		/*
		 * The parameter is added to initialize the RootClient. 02/28/2019, Bing Li
		 */
		public long getRequestWaitTime()
		{
			return this.requestWaitTime;
		}
	}
	
	/*
	 * The method is able to get the IP address of any node. 09/22/2021, Bing Li
	 */
	public IPAddress getIPAddress(String nodeKey) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return Child.CONTAINER().getIPAddress(nodeKey);
	}
	
	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException
	{
		Child.CONTAINER().syncNotify(ip, notification);
	}

	/*
	 * The method is added to increase the flexibility for the child to interact with any distributed nodes. When designing it, the method is called to send a multicasting message to a cluster. 09/24/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, ServerMessage notification) throws IOException, InterruptedException
	{
		Child.CONTAINER().asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through notifying synchronously. 09/22/2021, Bing Li
	 */
	public void syncNotify(IPAddress ip, Notification notification) throws IOException, InterruptedException
	{
		Child.CONTAINER().syncNotify(ip, notification);
	}
	
	/*
	 * It allows the child to interact with any nodes through notifying asynchronously. 09/22/2021, Bing Li
	 */
	public void asyncNotify(IPAddress ip, Notification notification)
	{
		Child.CONTAINER().asyncNotify(ip, notification);
	}

	/*
	 * It allows the child to interact with any nodes through reading. 09/22/2021, Bing Li
	 */
	public ServerMessage read(IPAddress ip, Request request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return Child.CONTAINER().read(ip, request);
	}
	
	/*
	 * The child is enabled to interact with the root through notification synchronously. 09/14/2020, Bing Li
	 */
	public void syncNotifyRoot(ClusterNotification notification) throws IOException, InterruptedException
	{
		Child.CONTAINER().syncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through notification asynchronously. 09/14/2020, Bing Li
	 */
	public void asyncNotifyRoot(ClusterNotification notification)
	{
		Child.CONTAINER().asyncNotifyRoot(notification);
	}
	
	/*
	 * The child is enabled to interact with the root through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readRoot(ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return Child.CONTAINER().readRoot(request);
	}
	
	/*
	 * The child is enabled to interact with the collabrator through request/response. For example, it happens multiple children need to be synchronized. 09/14/2020, Bing Li
	 */
	public ChildRootResponse readCollaborator(IPAddress ip, ChildRootRequest request) throws ClassNotFoundException, RemoteReadException, IOException
	{
		return Child.CONTAINER().readCollaborator(ip, request);
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		Child.CONTAINER().dispose(timeout);
	}

	public void start(String rootKey, ChildTask task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		ChildServiceProvider.CHILD().init(task);
//		Child.CONTAINER().start(rootKey, this.treeBranchCount);
		Child.CONTAINER().start(rootKey);
	}
}
