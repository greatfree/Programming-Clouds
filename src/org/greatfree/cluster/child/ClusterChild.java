package org.greatfree.cluster.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ChildServiceProvider;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.server.Peer.PeerBuilder;
import org.greatfree.util.Builder;

// Created: 09/23/2018, Bing Li
// public class ClusterChild extends Peer<ChildDispatcher>
public class ClusterChild
{
	private final int treeBranchCount;
	
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
//			.dispatcher(new ChildDispatcher(RegistryConfig.DISPATCHER_THREAD_POOL_SIZE, RegistryConfig.DISPATCHER_THREAD_POOL_KEEP_ALIVE_TIME, RegistryConfig.SCHEDULER_THREAD_POOL_SIZE, RegistryConfig.SCHEDULER_THREAD_POOL_KEEP_ALIVE_TIME))
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
//			.clientThreadPoolSize(builder.getClientThreadPoolSize())
//			.clientThreadKeepAliveTime(RegistryConfig.CLIENT_THREAD_KEEP_ALIVE_TIME);
			.schedulerPoolSize(builder.getSchedulerPoolSize())
			.schedulerKeepAliveTime(builder.getSchedulerKeepAliveTime());

		this.treeBranchCount = builder.getTreeBranchCount();
//		ClusterChild.CHILD().init(peerBuilder, builder.getTreeBranchCount());
		Child.CLUSTER().init(peerBuilder);
	}

	public static class ClusterChildBuilder implements Builder<ClusterChild>
	{
//		private PeerBuilder<ChildDispatcher> builder;
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
		
		private int dispatcherThreadPoolSize;
		private long dispatcherThreadKeepAliveTime;

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
		
//		private int clientThreadPoolSize;
//		private long clientThreadKeepAliveTime;
		
		private int schedulerPoolSize;
		private long schedulerKeepAliveTime;
		
		private int treeBranchCount;
		
		public ClusterChildBuilder()
		{
		}
		
		/*
		public ClusterChildBuilder builder(PeerBuilder<ChildDispatcher> peer)
		{
			this.builder = peer;
			return this;
		}
		*/
		
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
		public ClusterChildBuilder clientThreadPoolSize(int threadPoolSize)
		{
			this.clientThreadPoolSize = threadPoolSize;
			return this;
		}
		
		public ClusterChildBuilder clientThreadKeepAliveTime(long threadKeepAliveTime)
		{
			this.clientThreadKeepAliveTime = threadKeepAliveTime;
			return this;
		}
		*/
		
		public ClusterChildBuilder treeBranchCount(int treeBranchCount)
		{
			this.treeBranchCount = treeBranchCount;
			return this;
		}

		@Override
		public ClusterChild build() throws IOException
		{
			return new ClusterChild(this);
		}

		/*
		public PeerBuilder<ChildDispatcher> getBuilder()
		{
			return this.builder;
		}
		*/
		
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
		
		public int getDispatcherThreadPoolSize()
		{
			return this.dispatcherThreadPoolSize;
		}
		
		public long getDispatcherThreadKeepAliveTime()
		{
			return this.dispatcherThreadKeepAliveTime;
		}
		
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

		/*
		public int getClientThreadPoolSize()
		{
			return this.clientThreadPoolSize;
		}
		
		public long getClientThreadKeepAliveTime()
		{
			return this.clientThreadKeepAliveTime;
		}
		*/

		public int getSchedulerPoolSize()
		{
			return this.schedulerPoolSize;
		}
		
		public long getSchedulerKeepAliveTime()
		{
			return this.schedulerKeepAliveTime;
		}

		public int getTreeBranchCount()
		{
			return this.treeBranchCount;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
//		super.stop(timeout);
//		TerminateSignal.SIGNAL().notifyAllTermination();
		Child.CLUSTER().dispose(timeout);
	}

	public void start(String rootKey, ChildTask task) throws ClassNotFoundException, RemoteReadException, IOException, InterruptedException
	{
		ChildServiceProvider.CHILD().init(task);
		Child.CLUSTER().start(rootKey, this.treeBranchCount);
	}
}
