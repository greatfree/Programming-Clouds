package edu.chainnet.center.child;

import java.io.IOException;

import org.greatfree.cluster.ChildTask;
import org.greatfree.cluster.child.container.ClusterChildContainer;
import org.greatfree.concurrency.Scheduler;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.util.TerminateSignal;

import edu.chainnet.center.CenterConfig;
import edu.chainnet.center.child.lucene.ChildProfile;
import edu.chainnet.center.child.lucene.Indexing;
import edu.chainnet.center.child.lucene.PageCache;
import edu.chainnet.center.coordinator.manage.DataChildPath;
import edu.chainnet.center.message.DataPathsRequest;
import edu.chainnet.center.message.DataPathsResponse;

// Created: 04/22/2021, Bing Li
class DataChild
{
	private ClusterChildContainer child;

	private DataChild()
	{
	}
	
	private static DataChild instance = new DataChild();
	
	public static DataChild CENTER()
	{
		if (instance == null)
		{
			instance = new DataChild();
			return instance;
		}
		else
		{
			return instance;
		}
	}

	public void stop(long timeout) throws ClassNotFoundException, IOException, InterruptedException, RemoteReadException
	{
		Scheduler.GREATFREE().shutdown(CenterConfig.INDEXING_SHUTDOWN_TIMEOUT);
		PageCache.CENTER().dispose();
		TerminateSignal.SIGNAL().setTerminated();
		this.child.stop(timeout);
	}
	
	public void start(ChildTask task) throws ClassNotFoundException, IOException, RemoteReadException, InterruptedException
	{
		this.child = new ClusterChildContainer(task);
		this.child.start(CenterConfig.CENTER_COORDINATOR_KEY);

		DataChildPath path = ((DataPathsResponse)this.child.readRoot(new DataPathsRequest(CenterConfig.DOCS_HOME, CenterConfig.INDEX_HOME, CenterConfig.CACHE_HOME))).getPath();
		ChildProfile.CENTER().setDocPath(path.getDocPath());
		ChildProfile.CENTER().setIndexPath(path.getIndexPath());
		ChildProfile.CENTER().setCachePath(path.getCachePath());
		
		PageCache.CENTER().init(ChildProfile.CENTER().getCachePath());
		
		Scheduler.GREATFREE().init(CenterConfig.INDEXING_SCHEDULER_POOL_SIZE, CenterConfig.INDEXING_SCHEDULER_KEEP_ALIVE_TIME);
		Scheduler.GREATFREE().submit(new Indexing(), CenterConfig.INDEXING_INIT_DELAY, CenterConfig.INDEXING_PERIOD);
	}
}

