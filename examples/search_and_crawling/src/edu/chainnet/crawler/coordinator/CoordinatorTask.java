package edu.chainnet.crawler.coordinator;

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.Logger;

import org.greatfree.cluster.RootTask;
import org.greatfree.data.ServerConfig;
import org.greatfree.exceptions.DistributedNodeFailedException;
import org.greatfree.exceptions.RemoteReadException;
import org.greatfree.message.multicast.container.ChildRootRequest;
import org.greatfree.message.multicast.container.ChildRootResponse;
import org.greatfree.message.multicast.container.Notification;
import org.greatfree.message.multicast.container.Request;
import org.greatfree.message.multicast.container.Response;

import edu.chainnet.crawler.coordinator.manage.Paths;
import edu.chainnet.crawler.message.CrawlPathsRequest;
import edu.chainnet.crawler.message.CrawlPathsResponse;
import edu.chainnet.crawler.message.CrawlingApplicationID;

// Created: 04/25/2021, Bing Li
class CoordinatorTask implements RootTask
{
	private final static Logger log = Logger.getLogger("edu.chainnet.crawler.coordinator");

	@Override
	public ChildRootResponse processChildRequest(ChildRootRequest request)
	{
		switch (request.getApplicationID())
		{
			case CrawlingApplicationID.CRAWL_PATHS_REQUEST:
				log.info("CRAWL_PATHS_REQUEST received @" + Calendar.getInstance().getTime());
				CrawlPathsRequest cpr = (CrawlPathsRequest)request;
				return new CrawlPathsResponse(Paths.CRAWL().getPath(cpr.getCachePath(), cpr.getDocPath()));
		}
		return null;
	}

	@Override
	public void processNotification(Notification notification)
	{
		switch (notification.getApplicationID())
		{
			case CrawlingApplicationID.STOP_CRAWLING_CHILDREN_NOTIFICATION:
				log.info("STOP_CRAWLING_CHILDREN_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					CrawlCoordinator.CRAWL().stopCluster();
				}
				catch (IOException | DistributedNodeFailedException e)
				{
					e.printStackTrace();
				}
				break;
	
			case CrawlingApplicationID.STOP_CRAWLING_COORDINATOR_NOTIFICATION:
				log.info("STOP_CRAWLING_COORDINATOR_NOTIFICATION received @" + Calendar.getInstance().getTime());
				try
				{
					CrawlCoordinator.CRAWL().stopServer(ServerConfig.SERVER_SHUTDOWN_TIMEOUT);
				}
				catch (ClassNotFoundException | IOException | InterruptedException | RemoteReadException e)
				{
					e.printStackTrace();
				}
				break;
		}
	}

	@Override
	public Response processRequest(Request request)
	{
		// TODO Auto-generated method stub
		return null;
	}

}
